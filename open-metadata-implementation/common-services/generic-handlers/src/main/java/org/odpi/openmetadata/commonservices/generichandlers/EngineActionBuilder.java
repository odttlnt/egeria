/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.commonservices.generichandlers;

import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.properties.instances.InstanceProperties;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;
import org.odpi.openmetadata.repositoryservices.ffdc.exception.TypeErrorException;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * EngineActionBuilder creates the parts for an entity that represents an engine action.
 */
public class EngineActionBuilder extends ReferenceableBuilder
{
    private int                 domainIdentifier                = 0;
    private String              displayName                     = null;
    private String              description                     = null;
    private String              governanceEngineGUID            = null;
    private String              governanceEngineName            = null;
    private String              processName                     = null;
    private String              governanceActionProcessStepGUID = null;
    private String              governanceActionProcessStepName = null;
    private String              requestType                     = null;
    private Map<String, String> requestParameters               = null;
    private List<String>        mandatoryGuards                 = null;
    private List<String>        receivedGuards                  = null;
    private int                 actionStatus                    = 0;
    private Date                startDate                       = null;
    private String              processingEngineUserId          = null;
    private Date                completionDate                  = null;
    private List<String>        completionGuards                = null;

    /**
     * Create constructor
     *
     * @param qualifiedName unique name for the governance action
     * @param domainIdentifier governance domain for this governance action
     * @param displayName short display name for the governance action
     * @param description description of the governance action
     * @param governanceEngineGUID GUID of the governance engine that should execute the request
     * @param governanceEngineName name of the governance engine that should execute the request
     * @param processName name of the process that requested the governance action
     * @param governanceActionProcessStepGUID unique identifier of the governance action process step that initiated this engine action
     * @param governanceActionProcessStepName unique name of the governance action process step that initiated this engine action
     * @param requestType request type from the caller
     * @param requestParameters properties to pass to the governance action service
     * @param mandatoryGuards list of guards that must be supplied before this governance action can proceed
     * @param receivedGuards list of guards that triggered this governance action
     * @param actionStatus status for the governance action
     * @param startDate the date/time to start this governance action
     * @param processingEngineUserId userId of the engine host processing this governance action
     * @param completionDate the date/time that the governance action completed
     * @param completionGuards the guards produced by the governance service that ran on behalf of this governance action
     * @param additionalProperties additional properties for a governance action
     * @param repositoryHelper helper methods
     * @param serviceName name of this OMAS
     * @param serverName name of local server
     */
    EngineActionBuilder(String               qualifiedName,
                        int                  domainIdentifier,
                        String               displayName,
                        String               description,
                        String               governanceEngineGUID,
                        String               governanceEngineName,
                        String               processName,
                        String               governanceActionProcessStepGUID,
                        String               governanceActionProcessStepName,
                        String               requestType,
                        Map<String, String>  requestParameters,
                        List<String>         mandatoryGuards,
                        List<String>         receivedGuards,
                        int                  actionStatus,
                        Date                 startDate,
                        String               processingEngineUserId,
                        Date                 completionDate,
                        List<String>         completionGuards,
                        Map<String, String>  additionalProperties,
                        OMRSRepositoryHelper repositoryHelper,
                        String               serviceName,
                        String               serverName)
    {
        super(qualifiedName,
              additionalProperties,
              OpenMetadataAPIMapper.ENGINE_ACTION_TYPE_GUID,
              OpenMetadataAPIMapper.ENGINE_ACTION_TYPE_NAME,
              null,
              repositoryHelper,
              serviceName,
              serverName);

        this.domainIdentifier = domainIdentifier;
        this.displayName = displayName;
        this.description = description;
        this.governanceEngineGUID = governanceEngineGUID;
        this.governanceEngineName = governanceEngineName;
        this.processName = processName;
        this.governanceActionProcessStepGUID = governanceActionProcessStepGUID;
        this.governanceActionProcessStepName = governanceActionProcessStepName;
        this.requestType = requestType;
        this.requestParameters = requestParameters;
        this.mandatoryGuards = mandatoryGuards;
        this.receivedGuards = receivedGuards;
        this.actionStatus = actionStatus;
        this.startDate = startDate;
        this.processingEngineUserId = processingEngineUserId;
        this.completionDate = completionDate;
        this.completionGuards = completionGuards;
    }


    /**
     * Create constructor for claiming a governance action
     *
     * @param actionStatus status for the governance action
     * @param processingEngineUserId userId of the engine host processing this governance action
     * @param repositoryHelper helper methods
     * @param serviceName name of this OMAS
     * @param serverName name of local server
     */
    EngineActionBuilder(int                  actionStatus,
                        String               processingEngineUserId,
                        OMRSRepositoryHelper repositoryHelper,
                        String               serviceName,
                        String               serverName)
    {
        super(null,
              null,
              OpenMetadataAPIMapper.ENGINE_ACTION_TYPE_GUID,
              OpenMetadataAPIMapper.ENGINE_ACTION_TYPE_NAME,
              null,
              repositoryHelper,
              serviceName,
              serverName);

        this.actionStatus = actionStatus;
        this.processingEngineUserId = processingEngineUserId;
    }


    /**
     * Classification constructor
     *
     * @param repositoryHelper helper methods
     * @param serviceName name of this OMAS
     * @param serverName name of local server
     */
    EngineActionBuilder(OMRSRepositoryHelper repositoryHelper,
                        String               serviceName,
                        String               serverName)
    {
        super(OpenMetadataAPIMapper.ENGINE_ACTION_TYPE_GUID,
              OpenMetadataAPIMapper.ENGINE_ACTION_TYPE_NAME,
              repositoryHelper,
              serviceName,
              serverName);
    }


    /**
     * Return the supplied bean properties in an InstanceProperties object.
     *
     * @param methodName name of the calling method
     * @return InstanceProperties object
     * @throws InvalidParameterException there is a problem with the properties
     */
    @Override
    public InstanceProperties getInstanceProperties(String  methodName) throws InvalidParameterException
    {
        InstanceProperties properties = super.getInstanceProperties(methodName);

        properties = repositoryHelper.addIntPropertyToInstance(serviceName,
                                                               properties,
                                                               OpenMetadataAPIMapper.DOMAIN_IDENTIFIER_PROPERTY_NAME,
                                                               domainIdentifier,
                                                               methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                      properties,
                                                                      OpenMetadataAPIMapper.DISPLAY_NAME_PROPERTY_NAME,
                                                                      displayName,
                                                                      methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                      properties,
                                                                      OpenMetadataAPIMapper.DESCRIPTION_PROPERTY_NAME,
                                                                      description,
                                                                      methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                  properties,
                                                                  OpenMetadataAPIMapper.EXECUTOR_ENGINE_GUID_PROPERTY_NAME,
                                                                  governanceEngineGUID,
                                                                  methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                  properties,
                                                                  OpenMetadataAPIMapper.EXECUTOR_ENGINE_NAME_PROPERTY_NAME,
                                                                  governanceEngineName,
                                                                  methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                  properties,
                                                                  OpenMetadataAPIMapper.PROCESS_NAME_PROPERTY_NAME,
                                                                  processName,
                                                                  methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                  properties,
                                                                  OpenMetadataAPIMapper.PROCESS_STEP_GUID_PROPERTY_NAME,
                                                                  governanceActionProcessStepGUID,
                                                                  methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                  properties,
                                                                  OpenMetadataAPIMapper.PROCESS_STEP_NAME_PROPERTY_NAME,
                                                                  governanceActionProcessStepName,
                                                                  methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                  properties,
                                                                  OpenMetadataAPIMapper.REQUEST_TYPE_PROPERTY_NAME,
                                                                  requestType,
                                                                  methodName);

        properties = repositoryHelper.addStringMapPropertyToInstance(serviceName,
                                                                     properties,
                                                                     OpenMetadataAPIMapper.REQUEST_PARAMETERS_PROPERTY_NAME,
                                                                     requestParameters,
                                                                     methodName);

        properties = repositoryHelper.addStringArrayPropertyToInstance(serviceName,
                                                                           properties,
                                                                           OpenMetadataAPIMapper.MANDATORY_GUARDS_PROPERTY_NAME,
                                                                           mandatoryGuards,
                                                                           methodName);

        properties = repositoryHelper.addStringArrayPropertyToInstance(serviceName,
                                                                       properties,
                                                                       OpenMetadataAPIMapper.RECEIVED_GUARDS_PROPERTY_NAME,
                                                                       receivedGuards,
                                                                       methodName);

        try
        {
            properties = repositoryHelper.addEnumPropertyToInstance(serviceName,
                                                                    properties,
                                                                    OpenMetadataAPIMapper.ACTION_STATUS_PROPERTY_NAME,
                                                                    OpenMetadataAPIMapper.ENGINE_ACTION_STATUS_ENUM_TYPE_GUID,
                                                                    OpenMetadataAPIMapper.ENGINE_ACTION_STATUS_ENUM_TYPE_NAME,
                                                                    actionStatus,
                                                                    methodName);
        }
        catch (TypeErrorException error)
        {
            throw new InvalidParameterException(error, OpenMetadataAPIMapper.ACTION_STATUS_PROPERTY_NAME);
        }

        if (startDate == null)
        {
            this.startDate = new Date();
        }

        properties = repositoryHelper.addDatePropertyToInstance(serviceName,
                                                                properties,
                                                                OpenMetadataAPIMapper.START_DATE_PROPERTY_NAME,
                                                                startDate,
                                                                methodName);


        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                      properties,
                                                                      OpenMetadataAPIMapper.PROCESSING_ENGINE_USER_ID_PROPERTY_NAME,
                                                                      processingEngineUserId,
                                                                      methodName);

        properties = repositoryHelper.addDatePropertyToInstance(serviceName,
                                                                    properties,
                                                                    OpenMetadataAPIMapper.COMPLETION_DATE_PROPERTY_NAME,
                                                                    completionDate,
                                                                    methodName);

        properties = repositoryHelper.addStringArrayPropertyToInstance(serviceName,
                                                                           properties,
                                                                           OpenMetadataAPIMapper.COMPLETION_GUARDS_PROPERTY_NAME,
                                                                           completionGuards,
                                                                           methodName);

        return properties;
    }


    /**
     * Return the supplied bean properties in an InstanceProperties object.
     *
     * @param methodName name of the calling method
     * @return InstanceProperties object
     * @throws InvalidParameterException there is a problem with the properties
     */
    InstanceProperties getClaimInstanceProperties(String  methodName) throws InvalidParameterException
    {
        InstanceProperties properties = super.getInstanceProperties(methodName);

        try
        {
            properties = repositoryHelper.addEnumPropertyToInstance(serviceName,
                                                                    properties,
                                                                    OpenMetadataAPIMapper.ACTION_STATUS_PROPERTY_NAME,
                                                                    OpenMetadataAPIMapper.ENGINE_ACTION_STATUS_ENUM_TYPE_GUID,
                                                                    OpenMetadataAPIMapper.ENGINE_ACTION_STATUS_ENUM_TYPE_NAME,
                                                                    actionStatus,
                                                                    methodName);
        }
        catch (TypeErrorException error)
        {
            throw new InvalidParameterException(error, OpenMetadataAPIMapper.ACTIVITY_TYPE_PROPERTY_NAME);
        }

        properties = repositoryHelper.addDatePropertyToInstance(serviceName,
                                                                    properties,
                                                                    OpenMetadataAPIMapper.START_DATE_PROPERTY_NAME,
                                                                    startDate,
                                                                    methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                      properties,
                                                                      OpenMetadataAPIMapper.PROCESSING_ENGINE_USER_ID_PROPERTY_NAME,
                                                                      processingEngineUserId,
                                                                      methodName);

        return properties;
    }


    /**
     * Append the supplied bean properties in the supplied InstanceProperties object.
     *
     * @param properties existing properties
     * @param actionStatus completion status enum value
     * @param completionGuards optional guard strings for triggering subsequent action(s)
     * @param completionDate when did it finish
     * @param completionMessage message to describe completion results or reasons for failure
     * @param methodName name of the calling method
     * @return InstanceProperties object
     * @throws InvalidParameterException there is a problem with the properties
     */
    InstanceProperties getCompletionInstanceProperties(InstanceProperties properties,
                                                       int                actionStatus,
                                                       Date               completionDate,
                                                       List<String>       completionGuards,
                                                       String             completionMessage,
                                                       String             methodName) throws InvalidParameterException
    {
        try
        {
            properties = repositoryHelper.addEnumPropertyToInstance(serviceName,
                                                                    properties,
                                                                    OpenMetadataAPIMapper.ACTION_STATUS_PROPERTY_NAME,
                                                                    OpenMetadataAPIMapper.ENGINE_ACTION_STATUS_ENUM_TYPE_GUID,
                                                                    OpenMetadataAPIMapper.ENGINE_ACTION_STATUS_ENUM_TYPE_NAME,
                                                                    actionStatus,
                                                                    methodName);
        }
        catch (TypeErrorException error)
        {
            throw new InvalidParameterException(error, OpenMetadataAPIMapper.ACTION_STATUS_PROPERTY_NAME);
        }

        properties = repositoryHelper.addDatePropertyToInstance(serviceName,
                                                                properties,
                                                                OpenMetadataAPIMapper.COMPLETION_DATE_PROPERTY_NAME,
                                                                completionDate,
                                                                methodName);

        properties = repositoryHelper.addStringArrayPropertyToInstance(serviceName,
                                                                       properties,
                                                                       OpenMetadataAPIMapper.COMPLETION_GUARDS_PROPERTY_NAME,
                                                                       completionGuards,
                                                                       methodName);

        properties = repositoryHelper.addStringPropertyToInstance(serviceName,
                                                                  properties,
                                                                  OpenMetadataAPIMapper.COMPLETION_MESSAGE_PROPERTY_NAME,
                                                                  completionMessage,
                                                                  methodName);

        return properties;
    }
}
