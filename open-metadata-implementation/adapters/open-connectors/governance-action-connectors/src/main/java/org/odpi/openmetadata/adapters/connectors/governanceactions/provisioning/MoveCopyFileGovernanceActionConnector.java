/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.governanceactions.provisioning;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.odpi.openmetadata.adapters.connectors.governanceactions.ffdc.GovernanceActionConnectorsAuditCode;
import org.odpi.openmetadata.adapters.connectors.governanceactions.ffdc.GovernanceActionConnectorsErrorCode;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.connectors.ffdc.*;
import org.odpi.openmetadata.frameworks.connectors.properties.beans.ElementStatus;
import org.odpi.openmetadata.frameworks.governanceaction.OpenMetadataStore;
import org.odpi.openmetadata.frameworks.governanceaction.ProvisioningGovernanceActionService;
import org.odpi.openmetadata.frameworks.governanceaction.properties.*;
import org.odpi.openmetadata.frameworks.governanceaction.search.*;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.*;

/**
 * MoveCopyFileGovernanceActionConnector moves or copies files from one location to another and optionally creates lineage between them.
 */
public class MoveCopyFileGovernanceActionConnector extends ProvisioningGovernanceActionService
{
    /*
     * This map remembers the index of the last file that was created in a destination folder.
     */
    private static final Map<String, Integer> fileIndexMap = new HashMap<>();

    private final PropertyHelper propertyHelper = new PropertyHelper();

    private String  topLevelProcessName                  = "MoveCopyFileGovernanceActionConnector";
    private String  destinationFileTemplateQualifiedName = null;
    private String  topLevelProcessTemplateQualifiedName = null;
    private String  destinationFileNamePattern           = "{0}";
    private String  sourceFileName                       = null;
    private String  sourceFileGUID                       = null;
    private String  destinationFolderName                = null;
    private boolean copyFile                             = true;
    private boolean deleteFile                           = false;

    /*
     * This describes the default lineage pattern
     */
    private boolean createLineage = true;
    private boolean sourceLineageFromFile = true;
    private boolean destinationLineageToFile = true;
    private boolean childProcessLineage = true;

    /**
     * Generate a destination file name based on the input.
     *
     * @param previousDestinationFileName the file name tried on a previous iteration of the loop
     * @param destinationFolderName folder name where the file is to be copied to
     * @param sourceFile File object pointing to the source file
     * @param fileNamePattern pattern to generate the destination filename (or null to use the source file name)
     * @return next file name to try - or null if no more
     */
    private static synchronized String getDestinationFileName(String previousDestinationFileName,
                                                              String destinationFolderName,
                                                              File   sourceFile,
                                                              String fileNamePattern)
    {
         int fileIndex;

         if (fileIndexMap.get(destinationFolderName) != null)
         {
             fileIndex = fileIndexMap.get(destinationFolderName) + 1;
         }
         else
         {
             fileIndex = 0;
         }

         String nextDestinationFileName;

         if (fileNamePattern != null)
         {
             nextDestinationFileName = FilenameUtils.concat(destinationFolderName,
                                                            MessageFormat.format(fileNamePattern, sourceFile.getName(), fileIndex));
         }
         else
         {
             nextDestinationFileName = FilenameUtils.concat(destinationFolderName, sourceFile.getName());
         }

         if ((previousDestinationFileName != null) && (previousDestinationFileName.equals(nextDestinationFileName)))
         {
             /*
              * The file name is no longer changing as the index increases so return null to show that there
              * are no more options.
              */
             return null;
         }
         else
         {
             /*
              * A new file name has been created so return it to try.
              */
             fileIndexMap.put(destinationFolderName, fileIndex);
             return nextDestinationFileName;
         }
    }


    /**
     * Perform the file provisioning.
     *
     * @param governanceServiceName name of requesting service
     * @param destinationFolderName name of the folder where the file is to be provisioned into
     * @param sourceFilePathName full path name of the source file
     * @param fileNamePattern patten for generating the name of the destination file
     * @param copyFile is this a copy of a move?
     * @param auditLog logging destination
     * @return name of new file
     */
    private static synchronized String provisionFile(String   governanceServiceName,
                                                     String   destinationFolderName,
                                                     String   sourceFilePathName,
                                                     String   fileNamePattern,
                                                     boolean  copyFile,
                                                     AuditLog auditLog) throws IOException
    {
        final String methodName = "provisionFile";

        File   sourceFile          = new File(sourceFilePathName);
        File   destinationFolder   = new File(destinationFolderName);

        if (! destinationFolder.exists())
        {
            FileUtils.forceMkdir(destinationFolder);
        }

        String destinationFileName = getDestinationFileName(null, destinationFolderName, sourceFile, fileNamePattern);

        while (destinationFileName != null)
        {
            File destinationFile = new File(destinationFileName);
            if (! FileUtils.directoryContains(destinationFolder, destinationFile))
            {
                if (copyFile)
                {
                    if (auditLog != null)
                    {
                        auditLog.logMessage(methodName,
                                            GovernanceActionConnectorsAuditCode.COPY_FILE.getMessageDefinition(governanceServiceName,
                                                                                                               sourceFilePathName,
                                                                                                               destinationFileName));
                    }

                    FileUtils.copyFile(sourceFile, destinationFile, true);
                }
                else
                {
                    if (auditLog != null)
                    {
                        auditLog.logMessage(methodName,
                                            GovernanceActionConnectorsAuditCode.MOVE_FILE.getMessageDefinition(governanceServiceName,
                                                                                                               sourceFilePathName,
                                                                                                               destinationFileName));
                    }

                    FileUtils.moveFile(sourceFile, destinationFile);
                }

                return destinationFileName;
            }

            destinationFileName = getDestinationFileName(destinationFileName, destinationFolderName, sourceFile, fileNamePattern);
        }

        /*
         * No suitable file name can be found
         */
        if (auditLog != null)
        {
            auditLog.logMessage(methodName,
                                GovernanceActionConnectorsAuditCode.PROVISIONING_FAILURE.getMessageDefinition(governanceServiceName,
                                                                                                              sourceFilePathName,
                                                                                                              destinationFolderName,
                                                                                                              fileNamePattern));
        }

        return null;
    }


    /**
     * Indicates that the governance action service is completely configured and can begin processing.
     *
     * This is a standard method from the Open Connector Framework (OCF) so
     * be sure to call super.start() at the start of your overriding version.
     *
     * @throws ConnectorCheckedException there is a problem within the governance action service.
     */
    @Override
    public void start() throws ConnectorCheckedException
    {
        final String methodName = "start";

        super.start();

        List<String>     outputGuards = new ArrayList<>();
        CompletionStatus completionStatus;
        String           completionMessage = null;

        if (MoveCopyFileGovernanceActionProvider.MOVE_REQUEST_TYPE.equals(governanceContext.getRequestType()))
        {
            copyFile = false;
        }
        else if (MoveCopyFileGovernanceActionProvider.DELETE_REQUEST_TYPE.equals(governanceContext.getRequestType()))
        {
            deleteFile = true;
        }

        Map<String, Object> configurationProperties = connectionProperties.getConfigurationProperties();

        /*
         * Retrieve the configuration properties from the Connection object.  These properties affect all requests to this connector.
         */
        if (configurationProperties != null)
        {
            Object noLineageOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.NO_LINEAGE_PROPERTY);

            if (noLineageOption != null)
            {
                createLineage = false;
            }

            Object processNameOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.TOP_LEVEL_PROCESS_NAME_PROPERTY);

            if (processNameOption != null)
            {
                topLevelProcessName = processNameOption.toString();
            }

            Object templateNameOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.DESTINATION_TEMPLATE_NAME_PROPERTY);

            if (templateNameOption != null)
            {
                destinationFileTemplateQualifiedName = templateNameOption.toString();
            }

            templateNameOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.TOP_LEVEL_PROCESS_TEMPLATE_NAME_PROPERTY);

            if (templateNameOption != null)
            {
                topLevelProcessTemplateQualifiedName = templateNameOption.toString();
            }

            Object fileNamePatternOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.TARGET_FILE_NAME_PATTERN_PROPERTY);

            if (fileNamePatternOption != null)
            {
                destinationFileNamePattern = fileNamePatternOption.toString();
            }

            Object destinationFolderOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.DESTINATION_FOLDER_PROPERTY);

            if (destinationFolderOption != null)
            {
                destinationFolderName = destinationFolderOption.toString();
            }

            Object sourceLineageOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.LINEAGE_FROM_SOURCE_FOLDER_ONLY_PROPERTY);

            if (sourceLineageOption != null)
            {
                sourceLineageFromFile = false;
            }

            Object destinationLineageOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.LINEAGE_TO_DESTINATION_FOLDER_ONLY_PROPERTY);

            if (destinationLineageOption != null)
            {
                destinationLineageToFile = false;
            }

            Object processLineageOption = configurationProperties.get(MoveCopyFileGovernanceActionProvider.TOP_LEVEL_PROCESS_ONLY_LINEAGE_PROPERTY);

            if (processLineageOption != null)
            {
                childProcessLineage = false;
            }
        }

        /*
         * Retrieve the source file and destination folder from either the request parameters or the action targets.  If both
         * are specified then the action target elements take priority.
         */
        if (governanceContext.getRequestParameters() != null)
        {
            Map<String, String> requestParameters = governanceContext.getRequestParameters();

            for (String requestParameterName : requestParameters.keySet())
            {
                if (requestParameterName != null)
                {
                    switch (requestParameterName)
                    {
                        case MoveCopyFileGovernanceActionProvider.SOURCE_FILE_PROPERTY:
                            sourceFileName = requestParameters.get(requestParameterName);
                            break;
                        case MoveCopyFileGovernanceActionProvider.DESTINATION_FOLDER_PROPERTY:
                            destinationFolderName = requestParameters.get(requestParameterName);
                            break;
                        case MoveCopyFileGovernanceActionProvider.DESTINATION_TEMPLATE_NAME_PROPERTY:
                            destinationFileTemplateQualifiedName = requestParameters.get(requestParameterName);
                            break;
                        case MoveCopyFileGovernanceActionProvider.TARGET_FILE_NAME_PATTERN_PROPERTY:
                            destinationFileNamePattern = requestParameters.get(requestParameterName);
                            break;
                        case MoveCopyFileGovernanceActionProvider.NO_LINEAGE_PROPERTY:
                            createLineage = false;
                            break;
                        case MoveCopyFileGovernanceActionProvider.TOP_LEVEL_PROCESS_NAME_PROPERTY:
                            topLevelProcessName = requestParameters.get(requestParameterName);
                            break;
                        case MoveCopyFileGovernanceActionProvider.TOP_LEVEL_PROCESS_TEMPLATE_NAME_PROPERTY:
                            topLevelProcessTemplateQualifiedName = requestParameters.get(requestParameterName);
                            break;
                        case MoveCopyFileGovernanceActionProvider.TOP_LEVEL_PROCESS_ONLY_LINEAGE_PROPERTY:
                            childProcessLineage = false;
                            break;
                        case MoveCopyFileGovernanceActionProvider.LINEAGE_FROM_SOURCE_FOLDER_ONLY_PROPERTY:
                            sourceLineageFromFile = false;
                            break;
                        case MoveCopyFileGovernanceActionProvider.LINEAGE_TO_DESTINATION_FOLDER_ONLY_PROPERTY:
                            destinationLineageToFile = false;
                            break;
                    }
                }
            }
        }

        if (governanceContext.getActionTargetElements() != null)
        {
            for (ActionTargetElement actionTargetElement : governanceContext.getActionTargetElements())
            {
                if (actionTargetElement != null)
                {
                    if (MoveCopyFileGovernanceActionProvider.SOURCE_FILE_PROPERTY.equals(actionTargetElement.getActionTargetName()))
                    {
                        OpenMetadataElement sourceMetadataElement = actionTargetElement.getTargetElement();

                        if (sourceMetadataElement != null)
                        {
                            sourceFileName = this.getPathName(sourceMetadataElement);
                            sourceFileGUID = sourceMetadataElement.getElementGUID();
                        }
                    }
                    else if (MoveCopyFileGovernanceActionProvider.DESTINATION_FOLDER_PROPERTY.equals(actionTargetElement.getActionTargetName()))
                    {
                        OpenMetadataElement destinationMetadataElement = actionTargetElement.getTargetElement();

                        if (destinationMetadataElement != null)
                        {
                            destinationFolderName = this.getPathName(destinationMetadataElement);
                        }
                    }
                }
            }
        }

        if (sourceFileName == null)
        {
            if (auditLog != null)
            {
                auditLog.logMessage(methodName, GovernanceActionConnectorsAuditCode.NO_SOURCE_FILE_NAME.getMessageDefinition(governanceServiceName));

            }

            throw new ConnectorCheckedException(GovernanceActionConnectorsErrorCode.NO_SOURCE_FILE_NAME.getMessageDefinition(governanceServiceName),
                                                this.getClass().getName(),
                                                methodName);
        }


        List<NewActionTarget> newActionTargets = null;

        try
        {

            /*
             * The delete-file option does not perform any updates on metadata.
             * This can be managed by the integration connectors that monitor the file system.
             */
            if (deleteFile)
            {
                File fileToDelete = new File(sourceFileName);
                FileUtils.forceDelete(fileToDelete);

                outputGuards.add(MoveCopyFileGovernanceActionProvider.PROVISIONING_COMPLETE_GUARD);
                completionStatus = CompletionStatus.ACTIONED;
            }
            else
            {
                String destinationFileName = provisionFile(governanceServiceName,
                                                           destinationFolderName,
                                                           sourceFileName,
                                                           destinationFileNamePattern,
                                                           copyFile,
                                                           auditLog);

                if (destinationFileName != null)
                {
                    String newActionTargetGUID = null;

                    if (createLineage)
                    {
                        newActionTargetGUID = createLineage(destinationFileName);
                    }

                    if (newActionTargetGUID != null)
                    {
                        newActionTargets = new ArrayList<>();

                        NewActionTarget actionTarget = new NewActionTarget();

                        actionTarget.setActionTargetGUID(newActionTargetGUID);
                        actionTarget.setActionTargetName(MoveCopyFileGovernanceActionProvider.NEW_ASSET_GUID_PROPERTY);
                        newActionTargets.add(actionTarget);
                    }

                    outputGuards.add(MoveCopyFileGovernanceActionProvider.PROVISIONING_COMPLETE_GUARD);
                    completionStatus = CompletionStatus.ACTIONED;
                }
                else
                {
                    outputGuards.add(MoveCopyFileGovernanceActionProvider.PROVISIONING_FAILED_NO_FILE_NAMES_GUARD);
                    completionStatus = CompletionStatus.FAILED;
                }
            }
        }
        catch (Exception  error)
        {
            if (auditLog != null)
            {
                auditLog.logException(methodName,
                                      GovernanceActionConnectorsAuditCode.PROVISIONING_EXCEPTION.getMessageDefinition(governanceServiceName,
                                                                                                                      error.getClass().getName(),
                                                                                                                      sourceFileName,
                                                                                                                      destinationFolderName,
                                                                                                                      destinationFileNamePattern,
                                                                                                                      error.getMessage()),
                                      error);
            }

            outputGuards.add(MoveCopyFileGovernanceActionProvider.PROVISIONING_FAILED_EXCEPTION_GUARD);
            completionStatus = CompletionStatus.FAILED;
            completionMessage = error.getMessage();
        }

        try
        {
            governanceContext.recordCompletionStatus(completionStatus, outputGuards, null, newActionTargets, completionMessage);
        }
        catch (OCFCheckedExceptionBase error)
        {
            throw new ConnectorCheckedException(error.getReportedErrorMessage(), error);
        }
    }


    /**
     * Extract the path name located in the properties of the supplied asset metadata element (either a FileFolder or DataFile).
     * It looks first in the linked connection endpoint.  If this is not available then the qualified name of the asset is used.
     *
     * @param asset metadata element
     * @return pathname
     */
    private String getPathName(OpenMetadataElement asset)
    {
        final String methodName = "getPathName";
        final String connectionRelationshipName = "ConnectionToAsset";

        final String qualifiedNameParameterName = "qualifiedName";
        final String pathNameParameterName = "pathName";

        ElementProperties properties = asset.getElementProperties();

        /*
         * Begin by extracting the path name from the pathName property in the asset.
         */
        String pathName = propertyHelper.getStringProperty(governanceServiceName,
                                                           pathNameParameterName,
                                                           properties,
                                                           methodName);

        if (pathName != null)
        {
            return pathName;
        }

        /*
         * The next best location of the pathname is in the endpoint address property of the connection object linked to the
         * metadata element.
         */
        OpenMetadataStore store = governanceContext.getOpenMetadataStore();

        try
        {
            List<RelatedMetadataElement> connectionLinks = store.getRelatedMetadataElements(asset.getElementGUID(),
                                                                                            2,
                                                                                            connectionRelationshipName,
                                                                                            false,
                                                                                            false,
                                                                                            null,
                                                                                            0,
                                                                                            0);

            if ((connectionLinks == null) || (connectionLinks.isEmpty()))
            {
                if (auditLog != null)
                {
                    auditLog.logMessage(methodName,
                                          GovernanceActionConnectorsAuditCode.NO_LINKED_CONNECTION.getMessageDefinition(governanceServiceName,
                                                                                                                        asset.getElementGUID()));
                }
            }
            else if (connectionLinks.size() > 1)
            {
                for (RelatedMetadataElement connectionLink : connectionLinks)
                {
                    if (connectionLink != null)
                    {
                        String networkAddress = this.getPathNameFromConnection(asset.getElementGUID(), connectionLink);

                        if (networkAddress != null)
                        {
                            if (pathName == null)
                            {
                                pathName = networkAddress;
                            }
                            else
                            {
                                if (! networkAddress.equals(pathName))
                                {
                                    if (auditLog != null)
                                    {
                                        auditLog.logMessage(methodName,
                                                            GovernanceActionConnectorsAuditCode.TOO_MANY_CONNECTIONS.getMessageDefinition(governanceServiceName,
                                                                                                                                          Integer.toString(connectionLinks.size()),
                                                                                                                                          asset.getElementGUID(),
                                                                                                                                          connectionLinks.toString()));
                                    }

                                    pathName = null;
                                    break;
                                }
                            }
                        }
                    }
                }

                if (pathName != null)
                {
                    return pathName;
                }
            }
            else
            {
                pathName = this.getPathNameFromConnection(asset.getElementGUID(), connectionLinks.get(0));

                if (pathName != null)
                {
                    return pathName;
                }
            }
        }
        catch (Exception error)
        {
            if (auditLog != null)
            {
                auditLog.logException(methodName,
                                      GovernanceActionConnectorsAuditCode.ENDPOINT_EXCEPTION.getMessageDefinition(governanceServiceName,
                                                                                                                  error.getClass().getName(),
                                                                                                                  error.getMessage()),
                                      error);
            }
        }

        /*
         * Unable to get the path name from the pathName or endpoint so default to the qualified name.
         */

        pathName = propertyHelper.getStringProperty(governanceServiceName,
                                                    qualifiedNameParameterName,
                                                    properties,
                                                    methodName);

        if (auditLog != null)
        {
            auditLog.logMessage(methodName,
                                GovernanceActionConnectorsAuditCode.QUALIFIED_NAME_PATH_NAME.getMessageDefinition(governanceServiceName,
                                                                                                                  pathName));
        }

        return pathName;
    }


    /**
     * Extract the network address from the endpoint lined to the connection.
     *
     * @param assetGUID unique identifier of the asset
     * @param connectionLink link between the asset and the connection
     * @return network address from the endpoint, null or exception
     * @throws InvalidParameterException the unique identifier is null or not known; the relationship type is invalid
     * @throws UserNotAuthorizedException the governance action service is not able to access the elements
     * @throws PropertyServerException there is a problem accessing the metadata store
     */
    private String getPathNameFromConnection(String                 assetGUID,
                                             RelatedMetadataElement connectionLink) throws InvalidParameterException,
                                                                                           UserNotAuthorizedException,
                                                                                           PropertyServerException
    {
        final String endpointRelationshipName = "ConnectionEndpoint";
        final String endpointNetworkAddressName = "networkAddress";
        final String methodName = "getPathNameFromConnection";

        OpenMetadataStore   store = governanceContext.getOpenMetadataStore();
        OpenMetadataElement connection = connectionLink.getElement();

        if (connection == null)
        {
            /*
             * The connection comes from the RelatedMetadataElement.  If it is null then this is a bug in the governance action framework (GAF).
             */
            if (auditLog != null)
            {
                auditLog.logMessage(methodName,
                                    GovernanceActionConnectorsAuditCode.NO_RELATED_ASSET.getMessageDefinition(governanceServiceName,
                                                                                                              connectionLink.toString()));
            }
        }
        else
        {
            List<RelatedMetadataElement> endpointLinks = store.getRelatedMetadataElements(connection.getElementGUID(),
                                                                                          2,
                                                                                          endpointRelationshipName,
                                                                                          false,
                                                                                          false,
                                                                                          null,
                                                                                          0,
                                                                                          0);

            if ((endpointLinks == null) || (endpointLinks.isEmpty()))
            {
                if (auditLog != null)
                {
                    auditLog.logMessage(methodName,
                                        GovernanceActionConnectorsAuditCode.NO_LINKED_ENDPOINT.getMessageDefinition(governanceServiceName,
                                                                                                                    assetGUID,
                                                                                                                    connection.getElementGUID()));
                }
            }
            else if (endpointLinks.size() > 1)
            {
                if (auditLog != null)
                {
                    auditLog.logMessage(methodName,
                                        GovernanceActionConnectorsAuditCode.TOO_MANY_ENDPOINTS.getMessageDefinition(governanceServiceName,
                                                                                                                    assetGUID,
                                                                                                                    connection.getElementGUID(),
                                                                                                                    Integer.toString(
                                                                                                                            endpointLinks.size()),
                                                                                                                    endpointLinks.toString()));
                }
            }
            else
            {
                OpenMetadataElement endpoint = endpointLinks.get(0).getElement();

                if (endpoint == null)
                {
                    if (auditLog != null)
                    {
                        auditLog.logMessage(methodName,
                                            GovernanceActionConnectorsAuditCode.NO_RELATED_ASSET.getMessageDefinition(governanceServiceName,
                                                                                                                      endpointLinks.get(0).toString()));
                    }
                }
                else
                {
                    ElementProperties properties = endpoint.getElementProperties();

                    String address = propertyHelper.getStringProperty(governanceServiceName,
                                                                      endpointNetworkAddressName,
                                                                      properties,
                                                                      methodName);
                    if (address == null)
                    {
                        if (auditLog != null)
                        {
                            auditLog.logMessage(methodName,
                                                GovernanceActionConnectorsAuditCode.NO_NETWORK_ADDRESS.getMessageDefinition(governanceServiceName,
                                                                                                                            endpoint.getElementGUID(),
                                                                                                                            connection.getElementGUID(),
                                                                                                                            assetGUID));
                        }
                    }

                    return address;
                }
            }
        }

        return null;
    }


    /**
     * Create the lineage mapping for the provisioning process.  This governance action service supports a number of lineage patterns.
     * It assumes the source file / folder is catalogued.  It attaches it to the metadata element that represents this process
     * (if needed) and the destination file / folder.
     *
     * @param destinationFilePathName name of the file that was created
     * @return unique identifier if the new file asset
     *
     * @throws InvalidParameterException one of the parameters passed to open metadata is invalid (probably a bug in this code)
     * @throws UserNotAuthorizedException the userId for the connector does not have the authority it needs
     * @throws PropertyServerException there is a problem with the metadata server(s)
     */
    private String createLineage(String destinationFilePathName) throws InvalidParameterException,
                                                                        UserNotAuthorizedException,
                                                                        PropertyServerException
    {
        final String methodName              = "createLineage";
        final String childProcessTypeName    = "TransientEmbeddedProcess";
        final String topLevelProcessTypeName = "DeployedConnector";

        OpenMetadataStore metadataStore = governanceContext.getOpenMetadataStore();

        String fileName = FilenameUtils.getName(destinationFilePathName);
        String fileExtension = FilenameUtils.getExtension(destinationFilePathName);
        String newFileGUID;

        String topLevelProcessGUID = governanceContext.getOpenMetadataStore().getMetadataElementGUIDByUniqueName(topLevelProcessName, null, true, false, null);
        String processGUID;

        if (topLevelProcessGUID == null)
        {
            /*
             * Ensure that the top level process is set up
             */
            if (topLevelProcessTemplateQualifiedName == null)
            {
                topLevelProcessGUID = governanceContext.createProcess(topLevelProcessTypeName,
                                                                      ElementStatus.ACTIVE,
                                                                      topLevelProcessName,
                                                                      topLevelProcessName,
                                                                      null);
            }
            else
            {
                topLevelProcessGUID = governanceContext.createProcessFromTemplate(topLevelProcessTemplateQualifiedName,
                                                                                  ElementStatus.ACTIVE,
                                                                                  topLevelProcessName,
                                                                                  topLevelProcessName,
                                                                                  null);
            }
        }

        if (childProcessLineage)
        {
            processGUID = governanceContext.createChildProcess(childProcessTypeName,
                                                               ElementStatus.ACTIVE,
                                                               topLevelProcessName + connectorInstanceId,
                                                               topLevelProcessName,
                                                               null,
                                                               topLevelProcessGUID);
        }
        else
        {
            processGUID = topLevelProcessGUID;
        }

        if (sourceFileGUID == null)
        {
            sourceFileGUID = metadataStore.getMetadataElementGUIDByUniqueName(sourceFileName, "pathName", true, false, null);

            if (sourceFileGUID == null)
            {
                sourceFileGUID = metadataStore.getMetadataElementGUIDByUniqueName(sourceFileName, null, true, false, null);
            }

            if (! sourceLineageFromFile)
            {
                sourceFileGUID = getFolderGUID(sourceFileGUID);
            }
        }

        ElementProperties extendedProperties = propertyHelper.addStringProperty(null, "pathName", destinationFilePathName);
        extendedProperties = propertyHelper.addStringProperty(extendedProperties, "fileName", fileName);

        String            assetTypeName = this.getAssetTypeName(fileExtension);

        if (destinationFileTemplateQualifiedName == null)
        {
            newFileGUID = governanceContext.createAsset(assetTypeName, assetTypeName + ":" + destinationFilePathName, fileName, null, null, extendedProperties);
        }
        else
        {
            String assetTemplateGUID = governanceContext.getOpenMetadataStore().getMetadataElementGUIDByUniqueName(destinationFileTemplateQualifiedName,
                                                                                                                   null,
                                                                                                                   false,
                                                                                                                   false,
                                                                                                                   null);

            newFileGUID = governanceContext.createAssetFromTemplate(assetTypeName, assetTemplateGUID, "CSVFile:" + destinationFilePathName, fileName, null, null, extendedProperties);

            if ((assetTemplateGUID == null) && (auditLog != null))
            {
                auditLog.logMessage(methodName, GovernanceActionConnectorsAuditCode.MISSING_TEMPLATE.getMessageDefinition(governanceServiceName,
                                                                                                                          destinationFileTemplateQualifiedName,
                                                                                                                          MoveCopyFileGovernanceActionProvider.DESTINATION_TEMPLATE_NAME_PROPERTY,
                                                                                                                          newFileGUID));
            }
        }

        if (! destinationLineageToFile)
        {
            newFileGUID = getFolderGUID(newFileGUID);
        }

        sourceFileGUID = governanceContext.getOpenMetadataStore().getMetadataElementGUIDByUniqueName(sourceFileName, "pathName", true, false, null);
        if (sourceFileGUID != null)
        {
            governanceContext.createLineageRelationship("DataFlow", sourceFileGUID, null, null, null, processGUID);
        }

        governanceContext.createLineageRelationship("DataFlow", processGUID, null, null, null, newFileGUID);

        if (auditLog != null)
        {
            auditLog.logMessage(methodName,
                                GovernanceActionConnectorsAuditCode.CREATED_LINEAGE.getMessageDefinition(governanceServiceName,
                                                                                                         sourceFileGUID,
                                                                                                         processGUID,
                                                                                                         newFileGUID));
        }

        return newFileGUID;
    }


    /**
     * Return the unique identifier of a folder by navigating from the file.
     *
     * @param fileGUID file unique identifier
     *
     * @return unique identifier of the folder
     * @throws InvalidParameterException one of the parameters passed to open metadata is invalid (probably a bug in this code)
     * @throws UserNotAuthorizedException the userId for the connector does not have the authority it needs
     * @throws PropertyServerException there is a problem with the metadata server(s)
     */
    private String getFolderGUID(String  fileGUID) throws InvalidParameterException,
                                                          UserNotAuthorizedException,
                                                          PropertyServerException
    {
        String folderGUID = null;

        List<RelatedMetadataElement> relatedMetadataElementList = governanceContext.getOpenMetadataStore().getRelatedMetadataElements(fileGUID,
                                                                                                           2,
                                                                                                           "NestedFile",
                                                                                                           false,
                                                                                                           false,
                                                                                                           null,
                                                                                                           0,
                                                                                                           0);

        if (relatedMetadataElementList != null)
        {
            for (RelatedMetadataElement relatedMetadataElement : relatedMetadataElementList)
            {
                if (relatedMetadataElement != null)
                {
                    folderGUID = relatedMetadataElement.getElement().getElementGUID();
                }
            }
        }

        return folderGUID;
    }


    /**
     * Determine the open metadata type to use based on the file extension from the file name.  If no file extension, or it is unrecognized
     * then the default is "DataFile".
     *
     * @param fileExtension file extension extracted from the file name
     * @return asset type name to use
     */
    private String getAssetTypeName(String fileExtension)
    {
        String assetTypeName = "DataFile";

        if (fileExtension != null)
        {
            switch (fileExtension)
            {
                case "csv":
                    assetTypeName = "CSVFile";
                    break;

                case "json":
                    assetTypeName = "JSONFile";
                    break;

                case "avro":
                    assetTypeName = "AvroFileName";
                    break;

                case "pdf":
                case "doc":
                case "docx":
                case "ppt":
                case "pptx":
                case "xls":
                case "xlsx":
                case "md":
                    assetTypeName = "Document";
                    break;

                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                case "mp3":
                case "mp4":
                    assetTypeName = "MediaFile";
                    break;
            }
        }

        return assetTypeName;
    }
}
