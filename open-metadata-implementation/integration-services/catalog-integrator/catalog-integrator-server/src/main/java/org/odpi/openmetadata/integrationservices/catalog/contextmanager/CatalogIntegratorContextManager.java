/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.integrationservices.catalog.contextmanager;

import org.odpi.openmetadata.accessservices.assetmanager.client.*;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.CollaborationExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.ConnectionExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.DataAssetExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.ExternalAssetManagerClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.ExternalReferenceExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.GlossaryExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.GovernanceExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.InfrastructureExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.LineageExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.StewardshipExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.exchange.ValidValuesExchangeClient;
import org.odpi.openmetadata.accessservices.assetmanager.client.rest.AssetManagerRESTClient;
import org.odpi.openmetadata.accessservices.assetmanager.properties.AssetManagerProperties;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.frameworks.integration.connectors.IntegrationConnector;
import org.odpi.openmetadata.frameworks.integration.context.IntegrationContext;
import org.odpi.openmetadata.frameworks.integration.contextmanager.IntegrationContextManager;
import org.odpi.openmetadata.frameworks.integration.contextmanager.PermittedSynchronization;
import org.odpi.openmetadata.governanceservers.integrationdaemonservices.registration.IntegrationServiceDescription;
import org.odpi.openmetadata.integrationservices.catalog.connector.CatalogIntegratorConnector;
import org.odpi.openmetadata.integrationservices.catalog.connector.CatalogIntegratorContext;
import org.odpi.openmetadata.integrationservices.catalog.ffdc.CatalogIntegratorAuditCode;
import org.odpi.openmetadata.integrationservices.catalog.ffdc.CatalogIntegratorErrorCode;

import java.util.List;
import java.util.Map;


/**
 * CatalogIntegratorContextManager provides the bridge between the integration daemon services and
 * the specific implementation of an integration service
 */
public class CatalogIntegratorContextManager extends IntegrationContextManager
{
    private final static String disabledExchangeServicesOption = "disabledExchangeServices";

    private ConnectedAssetClient            connectedAssetClient;
    private OpenMetadataStoreClient         openMetadataStoreClient;
    private ExternalAssetManagerClient      assetManagerClient;
    private CollaborationExchangeClient     collaborationExchangeClient;
    private ConnectionExchangeClient        connectionExchangeClient;
    private DataAssetExchangeClient         dataAssetExchangeClient;
    private ExternalReferenceExchangeClient externalReferenceExchangeClient;
    private GlossaryExchangeClient          glossaryExchangeClient;
    private GovernanceExchangeClient        governanceExchangeClient;
    private InfrastructureExchangeClient    infrastructureExchangeClient;
    private LineageExchangeClient           lineageExchangeClient;
    private StewardshipExchangeClient       stewardshipExchangeClient;
    private ValidValuesExchangeClient       validValuesExchangeClient;


    /**
     * Default constructor
     */
    public CatalogIntegratorContextManager()
    {
        super();
    }


    /**
     * Initialize server properties for the context manager.
     *
     * @param partnerOMASServerName name of the server to connect to
     * @param partnerOMASPlatformRootURL the network address of the server running the OMAS REST services
     * @param userId caller's userId embedded in all HTTP requests
     * @param password caller's userId embedded in all HTTP requests
     * @param serviceOptions options from the integration service's configuration
     * @param maxPageSize maximum number of results that can be returned on a single REST call
     * @param auditLog logging destination
     */
    public void initializeContextManager(String              partnerOMASServerName,
                                         String              partnerOMASPlatformRootURL,
                                         String              userId,
                                         String              password,
                                         Map<String, Object> serviceOptions,
                                         int                 maxPageSize,
                                         AuditLog            auditLog)
    {
        super.initializeContextManager(partnerOMASServerName, partnerOMASPlatformRootURL, userId, password, serviceOptions, maxPageSize, auditLog);

        final String methodName = "initializeContextManager";

        auditLog.logMessage(methodName,
                            CatalogIntegratorAuditCode.CONTEXT_INITIALIZING.getMessageDefinition(partnerOMASServerName, partnerOMASPlatformRootURL));
    }


    /**
     * Suggestion for subclass to create client(s) to partner OMAS.
     *
     * @throws InvalidParameterException the subclass is not able to create one of its clients
     */
    @Override
    public void createClients() throws InvalidParameterException
    {
        super.openIntegrationClient = new OpenIntegrationServiceClient(partnerOMASServerName, partnerOMASPlatformRootURL);

        super.openMetadataStoreClient = new OpenMetadataStoreClient(partnerOMASServerName, partnerOMASPlatformRootURL);

        AssetManagerRESTClient restClient;

        if (localServerPassword == null)
        {
            restClient = new AssetManagerRESTClient(partnerOMASServerName,
                                                    partnerOMASPlatformRootURL,
                                                    auditLog);

            connectedAssetClient = new ConnectedAssetClient(partnerOMASServerName,
                                                            partnerOMASPlatformRootURL,
                                                            auditLog);

            openMetadataStoreClient = new OpenMetadataStoreClient(partnerOMASServerName,
                                                                  partnerOMASPlatformRootURL);
        }
        else
        {
            restClient = new AssetManagerRESTClient(partnerOMASServerName,
                                                    partnerOMASPlatformRootURL,
                                                    localServerUserId,
                                                    localServerPassword,
                                                    auditLog);

            connectedAssetClient = new ConnectedAssetClient(partnerOMASServerName,
                                                            partnerOMASPlatformRootURL,
                                                            localServerUserId,
                                                            localServerPassword,
                                                            auditLog);

            openMetadataStoreClient = new OpenMetadataStoreClient(partnerOMASServerName,
                                                                  partnerOMASPlatformRootURL,
                                                                  localServerUserId,
                                                                  localServerPassword);
        }


        assetManagerClient = new ExternalAssetManagerClient(partnerOMASServerName,
                                                            partnerOMASPlatformRootURL,
                                                            restClient,
                                                            maxPageSize,
                                                            auditLog);

        collaborationExchangeClient = new CollaborationExchangeClient(partnerOMASServerName,
                                                                      partnerOMASPlatformRootURL,
                                                                      restClient,
                                                                      maxPageSize,
                                                                      auditLog);

        connectionExchangeClient = new ConnectionExchangeClient(partnerOMASServerName,
                                                                partnerOMASPlatformRootURL,
                                                                restClient,
                                                                maxPageSize,
                                                                auditLog);

        dataAssetExchangeClient = new DataAssetExchangeClient(partnerOMASServerName,
                                                              partnerOMASPlatformRootURL,
                                                              restClient,
                                                              maxPageSize,
                                                              auditLog);

        externalReferenceExchangeClient = new ExternalReferenceExchangeClient(partnerOMASServerName,
                                                                              partnerOMASPlatformRootURL,
                                                                              restClient,
                                                                              maxPageSize,
                                                                              auditLog);

        glossaryExchangeClient = new GlossaryExchangeClient(partnerOMASServerName,
                                                            partnerOMASPlatformRootURL,
                                                            restClient,
                                                            maxPageSize,
                                                            auditLog);

        governanceExchangeClient = new GovernanceExchangeClient(partnerOMASServerName,
                                                                partnerOMASPlatformRootURL,
                                                                restClient,
                                                                maxPageSize,
                                                                auditLog);

        infrastructureExchangeClient = new InfrastructureExchangeClient(partnerOMASServerName,
                                                                        partnerOMASPlatformRootURL,
                                                                        restClient,
                                                                        maxPageSize,
                                                                        auditLog);

        lineageExchangeClient = new LineageExchangeClient(partnerOMASServerName,
                                                          partnerOMASPlatformRootURL,
                                                          restClient,
                                                          maxPageSize,
                                                          auditLog);

        stewardshipExchangeClient = new StewardshipExchangeClient(partnerOMASServerName,
                                                                  partnerOMASPlatformRootURL,
                                                                  restClient,
                                                                  maxPageSize,
                                                                  auditLog);

        validValuesExchangeClient = new ValidValuesExchangeClient(partnerOMASServerName,
                                                                  partnerOMASPlatformRootURL,
                                                                  restClient,
                                                                  maxPageSize,
                                                                  auditLog);
    }


    /**
     * Retrieve the metadata source's unique identifier (GUID) or if it is not defined, create the software server capability
     * for this integrator.
     *
     * @param metadataSourceQualifiedName unique name of the software server capability that represents this integration service
     *
     * @return unique identifier of the metadata source
     *
     * @throws InvalidParameterException one of the parameters passed (probably on initialize) is invalid
     * @throws UserNotAuthorizedException the integration daemon's userId does not have access to the partner OMAS
     * @throws PropertyServerException there is a problem in the remote server running the partner OMAS
     */
    private String setUpMetadataSource(String   metadataSourceQualifiedName) throws InvalidParameterException,
                                                                                    UserNotAuthorizedException,
                                                                                    PropertyServerException
    {
        if (metadataSourceQualifiedName != null)
        {
            String metadataSourceGUID = assetManagerClient.getExternalAssetManagerGUID(localServerUserId, metadataSourceQualifiedName);

            if (metadataSourceGUID == null)
            {
                AssetManagerProperties properties = new AssetManagerProperties();

                properties.setQualifiedName(metadataSourceQualifiedName);

                metadataSourceGUID = assetManagerClient.createExternalAssetManager(localServerUserId, properties);
            }

            return metadataSourceGUID;
        }

        return null;
    }


    /**
     * Set up the context in the supplied connector. This is called between initialize() and start() on the connector.
     *
     * @param connectorId unique identifier of the connector (used to configure the event listener)
     * @param connectorName name of connector from config
     * @param connectorUserId userId for the connector
     * @param integrationConnector connector created from connection integration service configuration
     * @param integrationConnectorGUID unique identifier of the integration connector entity (only set if working with integration groups)
     * @param permittedSynchronization controls the direction(s) that metadata is allowed to flow
     * @param generateIntegrationReport should the connector generate an integration reports?
     * @param metadataSourceQualifiedName unique name of the software server capability that represents the metadata source.
     *
     * @return the new integration context
     * @throws InvalidParameterException the connector is not of the correct type
     * @throws UserNotAuthorizedException user not authorized to issue this request
     * @throws PropertyServerException problem accessing the property server
     */
    @Override
    public IntegrationContext setContext(String                   connectorId,
                                         String                   connectorName,
                                         String                   connectorUserId,
                                         IntegrationConnector     integrationConnector,
                                         String                   integrationConnectorGUID,
                                         PermittedSynchronization permittedSynchronization,
                                         boolean                  generateIntegrationReport,
                                         String                   metadataSourceQualifiedName) throws InvalidParameterException,
                                                                                                      UserNotAuthorizedException,
                                                                                                      PropertyServerException
    {
        final String  methodName = "setContext";

        String permittedSynchronizationName = PermittedSynchronization.BOTH_DIRECTIONS.getName();
        String serviceOptionsString = "null";

        if (permittedSynchronization != null)
        {
            permittedSynchronizationName = permittedSynchronization.getName();
        }

        if (serviceOptions != null)
        {
            serviceOptionsString = serviceOptions.toString();
        }

        if (integrationConnector instanceof CatalogIntegratorConnector serviceSpecificConnector)
        {
            auditLog.logMessage(methodName,
                                CatalogIntegratorAuditCode.CONNECTOR_CONTEXT_INITIALIZING.getMessageDefinition(connectorName,
                                                                                                               connectorId,
                                                                                                               metadataSourceQualifiedName,
                                                                                                               permittedSynchronizationName,
                                                                                                               serviceOptionsString));

            AssetManagerEventClient eventClient = new AssetManagerEventClient(partnerOMASServerName,
                                                                              partnerOMASPlatformRootURL,
                                                                              localServerUserId,
                                                                              localServerPassword,
                                                                              maxPageSize,
                                                                              auditLog,
                                                                              connectorId);

            String externalSourceGUID = this.setUpMetadataSource(metadataSourceQualifiedName);
            String externalSourceName = metadataSourceQualifiedName;
            if (externalSourceGUID == null)
            {
                externalSourceName = null;
            }

            CatalogIntegratorContext integratorContext = new CatalogIntegratorContext(connectorId,
                                                                                      connectorName,
                                                                                      connectorUserId,
                                                                                      partnerOMASServerName,
                                                                                      openIntegrationClient,
                                                                                      openMetadataStoreClient,
                                                                                      assetManagerClient,
                                                                                      eventClient,
                                                                                      connectedAssetClient,
                                                                                      collaborationExchangeClient,
                                                                                      connectionExchangeClient,
                                                                                      dataAssetExchangeClient,
                                                                                      externalReferenceExchangeClient,
                                                                                      glossaryExchangeClient,
                                                                                      governanceExchangeClient,
                                                                                      infrastructureExchangeClient,
                                                                                      lineageExchangeClient,
                                                                                      stewardshipExchangeClient,
                                                                                      validValuesExchangeClient,
                                                                                      generateIntegrationReport,
                                                                                      permittedSynchronization,
                                                                                      integrationConnectorGUID,
                                                                                      externalSourceGUID,
                                                                                      externalSourceName,
                                                                                      IntegrationServiceDescription.CATALOG_INTEGRATOR_OMIS.getIntegrationServiceFullName(),
                                                                                      this.extractDisabledExchangeServices(serviceOptions, connectorName),
                                                                                      maxPageSize,
                                                                                      auditLog);
            serviceSpecificConnector.setContext(integratorContext);
            integrationConnector.setConnectorName(connectorName);

            return integratorContext;
        }
        else
        {
            final String  parameterName = "integrationConnector";

            throw new InvalidParameterException(
                    CatalogIntegratorErrorCode.INVALID_CONNECTOR.getMessageDefinition(connectorName,
                                                                                      IntegrationServiceDescription.CATALOG_INTEGRATOR_OMIS.getIntegrationServiceFullName(),
                                                                                      CatalogIntegratorConnector.class.getCanonicalName()),
                                                this.getClass().getName(),
                                                methodName,
                                                parameterName);
        }
    }



    /**
     * Extract the list of services that have been disabled.
     *
     * @param serviceOptions options passed to the integration service.
     * @return null or list of disabled exchange service names
     * @throws InvalidParameterException the "supported zones" property is not a list of zone names.
     */
    private List<String> extractDisabledExchangeServices(Map<String, Object> serviceOptions,
                                                         String              connectorName) throws InvalidParameterException
    {
        final String  methodName = "extractDisabledExchangeServices";

        if (serviceOptions == null)
        {
            return null;
        }
        else
        {
            Object serviceListObject = serviceOptions.get(disabledExchangeServicesOption);

            if (serviceListObject == null)
            {
                return null;
            }
            else
            {
                try
                {
                    @SuppressWarnings("unchecked")
                    List<String> serviceList = (List<String>) serviceListObject;

                    auditLog.logMessage(methodName,
                                        CatalogIntegratorAuditCode.DISABLED_EXCHANGE_SERVICES.getMessageDefinition(connectorName, serviceList.toString()));
                    return serviceList;
                }
                catch (Exception error)
                {
                    final String  parameterName = "serviceOptions";

                    throw new InvalidParameterException(
                            CatalogIntegratorErrorCode.BAD_CONFIG_PROPERTIES.getMessageDefinition(IntegrationServiceDescription.CATALOG_INTEGRATOR_OMIS.getIntegrationServiceFullName(),
                                                                                                  serviceListObject.toString(),
                                                                                                  disabledExchangeServicesOption,
                                                                                                  error.getClass().getName(),
                                                                                                  error.getMessage()),
                            this.getClass().getName(),
                            methodName,
                            parameterName);

                }
            }
        }
    }
}
