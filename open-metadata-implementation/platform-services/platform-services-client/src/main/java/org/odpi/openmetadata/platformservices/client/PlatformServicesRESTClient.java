/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.platformservices.client;


import org.odpi.openmetadata.adminservices.rest.OMAGServerConfigResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.ConnectionResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.ConnectorTypeListResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.ConnectorTypeResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.FFDCRESTClient;
import org.odpi.openmetadata.commonservices.ffdc.rest.RegisteredOMAGServicesResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.VoidResponse;
import org.odpi.openmetadata.frameworks.auditlog.AuditLog;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.odpi.openmetadata.platformservices.rest.ServerListResponse;
import org.odpi.openmetadata.serveroperations.rest.OMAGServerStatusResponse;
import org.odpi.openmetadata.serveroperations.rest.ServerServicesListResponse;
import org.odpi.openmetadata.serveroperations.rest.ServerStatusResponse;
import org.odpi.openmetadata.serveroperations.rest.SuccessMessageResponse;


/**
 * AssetOwnerRESTClient is responsible for issuing calls to the OMAS REST APIs.
 */
class PlatformServicesRESTClient extends FFDCRESTClient
{
    /**
     * Constructor for no authentication with audit log.
     *
     * @param platformName name of the OMAG Server to call
     * @param platformURLRoot URL root of the server manager where the OMAG Server is running.
     * @param auditLog destination for log messages.
     *
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     * REST API calls.
     */
    PlatformServicesRESTClient(String   platformName,
                               String   platformURLRoot,
                               AuditLog auditLog) throws InvalidParameterException
    {
        super(platformName, platformURLRoot, auditLog);
    }


    /**
     * Constructor for no authentication.
     *
     * @param platformName name of the OMAG Server to call
     * @param platformURLRoot URL root of the server manager where the OMAG Server is running.
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     * REST API calls.
     */
    PlatformServicesRESTClient(String platformName,
                               String platformURLRoot) throws InvalidParameterException
    {
        super(platformName, platformURLRoot);
    }


    /**
     * Constructor for simple userId and password authentication with audit log.
     *
     * @param platformName name of the OMAG Server to call
     * @param platformURLRoot URL root of the server manager where the OMAG Server is running.
     * @param userId user id for the HTTP request
     * @param password password for the HTTP request
     * @param auditLog destination for log messages.
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     * REST API calls.
     */
    PlatformServicesRESTClient(String   platformName,
                               String   platformURLRoot,
                               String   userId,
                               String   password,
                               AuditLog auditLog) throws InvalidParameterException
    {
        super(platformName, platformURLRoot, userId, password, auditLog);
    }


    /**
     * Constructor for simple userId and password authentication.
     *
     * @param platformName name of the OMAG Server to call
     * @param platformURLRoot URL root of the server manager where the OMAG Server is running.
     * @param userId user id for the HTTP request
     * @param password password for the HTTP request
     * @throws InvalidParameterException there is a problem creating the client-side components to issue any
     * REST API calls.
     */
    PlatformServicesRESTClient(String platformName,
                               String platformURLRoot,
                               String userId,
                               String password) throws InvalidParameterException
    {
        super(platformName, platformURLRoot, userId, password);
    }


    // TODO - this method is to support the CURRENT implementation of platform origin - which may need to change to a wrapped response result.
    /**
     * Issue a GET REST call that returns a String object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return response object
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    String callStringGetRESTCall(String    methodName,
                                 String    urlTemplate,
                                 Object... params) throws InvalidParameterException,
                                                          UserNotAuthorizedException,
                                                          PropertyServerException
    {
        String restResult = this.callGetRESTCall(methodName, String.class, urlTemplate, params);

        //exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }


    /**
     * Issue a GET REST call that returns a ServerStatusResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return response object
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    ServerStatusResponse callServerStatusGetRESTCall(String    methodName,
                                                     String    urlTemplate,
                                                     Object... params) throws InvalidParameterException,
                                                                              UserNotAuthorizedException,
                                                                              PropertyServerException
    {
        ServerStatusResponse restResult = this.callGetRESTCall(methodName, ServerStatusResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }


    /**
     * Issue a GET REST call that returns a ConnectorTypeResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return response object
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    ConnectorTypeResponse callConnectorTypeGetRESTCall(String    methodName,
                                                       String    urlTemplate,
                                                       Object... params) throws InvalidParameterException,
                                                                                UserNotAuthorizedException,
                                                                                PropertyServerException
    {
        ConnectorTypeResponse restResult = this.callGetRESTCall(methodName, ConnectorTypeResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }



    /**
     * Issue a GET REST call that returns a ConnectorTypeListResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return response object
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    ConnectorTypeListResponse callConnectorTypeListGetRESTCall(String    methodName,
                                                               String    urlTemplate,
                                                               Object... params) throws InvalidParameterException,
                                                                                        UserNotAuthorizedException,
                                                                                        PropertyServerException
    {
        ConnectorTypeListResponse restResult = this.callGetRESTCall(methodName, ConnectorTypeListResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }



    /**
     * Issue a GET REST call that returns a 'ServerServicesListResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return response object
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    ServerServicesListResponse callServiceListGetRESTCall(String    methodName,
                                                          String    urlTemplate,
                                                          Object... params) throws InvalidParameterException,
                                                                                   UserNotAuthorizedException,
                                                                                   PropertyServerException
    {
        ServerServicesListResponse restResult = this.callGetRESTCall(methodName, ServerServicesListResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }


    /**
     * Issue a GET REST call that returns a ServerListResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return response object
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    ServerListResponse callServerListGetRESTCall(String    methodName,
                                                 String    urlTemplate,
                                                 Object... params) throws InvalidParameterException,
                                                                          UserNotAuthorizedException,
                                                                          PropertyServerException
    {
        ServerListResponse restResult = this.callGetRESTCall(methodName, ServerListResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }


    /**
     * Issue a GET REST call that returns a RegisteredOMAGServicesResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return response object
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    RegisteredOMAGServicesResponse callRegisteredOMAGServicesGetRESTCall(String    methodName,
                                                                         String    urlTemplate,
                                                                         Object... params) throws InvalidParameterException,
                                                                                                  UserNotAuthorizedException,
                                                                                                  PropertyServerException
    {
        RegisteredOMAGServicesResponse restResult = this.callGetRESTCall(methodName, RegisteredOMAGServicesResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }


    /**
     * Issue a POST REST call that returns a SuccessMessageResponse object.  This is typically a server start.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate  template of the URL for the REST API call with place-holders for the parameters.
     * @param requestBody request body for the request.
     * @param params  a list of parameters that are slotted into the url template.
     *
     * @return SuccessMessageResponse
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    SuccessMessageResponse callSuccessMessagePostRESTCall(String    methodName,
                                                          String    urlTemplate,
                                                          Object    requestBody,
                                                          Object... params) throws InvalidParameterException,
                                                                                   UserNotAuthorizedException,
                                                                                   PropertyServerException
    {
        SuccessMessageResponse restResult =  this.callPostRESTCall(methodName,
                                                                   SuccessMessageResponse.class,
                                                                   urlTemplate,
                                                                   requestBody,
                                                                   params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }


    /**
     * Issue a GET REST call that returns a OMAGServerStatusResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API call with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return OMAGServerStatusResponse
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException the repository is not available or not working properly.
     */
    OMAGServerStatusResponse callOMAGServerStatusGetRESTCall(String    methodName,
                                                             String    urlTemplate,
                                                             Object... params) throws InvalidParameterException,
                                                                                      UserNotAuthorizedException,
                                                                                      PropertyServerException
    {
        OMAGServerStatusResponse restResult = this.callGetRESTCall(methodName, OMAGServerStatusResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }


    /**
     * Issue a DELETE REST call that returns a VoidResponse object.  This is typically a delete
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate  template of the URL for the REST API call with place-holders for the parameters.
     * @param params  a list of parameters that are slotted into the url template.
     *
     * @return VoidResponse
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException something went wrong with the REST call stack.
     */
    VoidResponse callVoidDeleteRESTCall(String    methodName,
                                        String    urlTemplate,
                                        Object... params) throws InvalidParameterException,
                                                                 UserNotAuthorizedException,
                                                                 PropertyServerException
    {
        VoidResponse restResult =  this.callDeleteRESTCall(methodName,
                                                           VoidResponse.class,
                                                           urlTemplate,
                                                           null,
                                                           params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }


    /**
     * Issue a GET REST call that returns a OMAGServerConfigResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API call with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return OMAGServerConfigResponse
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException something went wrong with the REST call stack.
     */
    OMAGServerConfigResponse callOMAGServerConfigGetRESTCall(String    methodName,
                                                             String    urlTemplate,
                                                             Object... params) throws InvalidParameterException,
                                                                                      UserNotAuthorizedException,
                                                                                      PropertyServerException
    {
        OMAGServerConfigResponse restResult = this.callGetRESTCall(methodName, OMAGServerConfigResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }



    /**
     * Issue a GET REST call that returns a ConnectionResponse object.
     *
     * @param methodName  name of the method being called.
     * @param urlTemplate template of the URL for the REST API call with place-holders for the parameters.
     * @param params      a list of parameters that are slotted into the url template.
     *
     * @return ConnectionResponse
     * @throws InvalidParameterException one of the parameters is invalid.
     * @throws UserNotAuthorizedException the user is not authorized to make this request.
     * @throws PropertyServerException something went wrong with the REST call stack.
     */
    ConnectionResponse callConnectionGetRESTCall(String    methodName,
                                                 String    urlTemplate,
                                                 Object... params) throws InvalidParameterException,
                                                                          UserNotAuthorizedException,
                                                                          PropertyServerException
    {
        ConnectionResponse restResult = this.callGetRESTCall(methodName, ConnectionResponse.class, urlTemplate, params);

        exceptionHandler.detectAndThrowStandardExceptions(methodName, restResult);

        return restResult;
    }
}
