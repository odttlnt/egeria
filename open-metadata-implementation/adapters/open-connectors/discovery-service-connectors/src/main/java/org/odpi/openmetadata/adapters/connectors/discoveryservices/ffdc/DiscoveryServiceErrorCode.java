/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adapters.connectors.discoveryservices.ffdc;

import org.odpi.openmetadata.frameworks.auditlog.messagesets.ExceptionMessageDefinition;
import org.odpi.openmetadata.frameworks.auditlog.messagesets.ExceptionMessageSet;


/**
 * The DiscoveryServiceErrorCode is used to define first failure data capture (FFDC) for errors that occur
 * when running Discovery Services.  It is
 * used in conjunction with both Checked and Runtime (unchecked) exceptions.
 *
 * The 5 fields in the enum are:
 * <ul>
 *     <li>HTTP Error Code - for translating between REST and JAVA - Typically the numbers used are:</li>
 *     <li><ul>
 *         <li>500 - internal error</li>
 *         <li>400 - invalid parameters</li>
 *         <li>404 - not found</li>
 *         <li>409 - data conflict errors - eg item already defined</li>
 *     </ul></li>
 *     <li>Error Message Id - to uniquely identify the message</li>
 *     <li>Error Message Text - includes placeholder to allow additional values to be captured</li>
 *     <li>SystemAction - describes the result of the error</li>
 *     <li>UserAction - describes how a consumer should correct the error</li>
 * </ul>
 */
public enum DiscoveryServiceErrorCode implements ExceptionMessageSet
{
    INVALID_ASSET_TYPE(400, "OMAG-DISCOVERY-SERVICE-400-001 ",
             "Asset {0} is of type {1} but discovery service {2} only supports the following asset type(s): {3}",
             "The discovery service terminates without running any automated metadata discovery function.",
             "The caller has requested a discovery request type that is incompatible with the type of the " +
                               "asset that has been supplied.  Ths problem could be resolved by issuing the discovery request with " +
                               "a discovery request type that is compatible with the asset, or changing the discovery service " +
                               "associated with the discovery request type to one that supports this type of asset."),

    NO_ASSET(500, "OMAG-DISCOVERY-SERVICE-500-001 ",
            "No information about the asset {0} has been returned from the asset store for discovery service {1}.",
            "The discovery service terminates without running any automated metadata discovery function.",
            "This is an unexpected condition because if the metadata server was unavailable, an exception would have been caught."),

    NO_ASSET_TYPE(500, "OMAG-DISCOVERY-SERVICE-500-002 ",
             "No type name is available for the asset passed to discovery service {0}.  The full asset contents are: {1}.",
             "The discovery service terminates without running any automated metadata discovery function.",
             "This is an unexpected condition because if the metadata server was unavailable, an exception would have been caught."),
        ;


    private ExceptionMessageDefinition messageDefinition;


    /**
     * The constructor for DiscoveryEngineServicesErrorCode expects to be passed one of the enumeration rows defined in
     * DiscoveryEngineServicesErrorCode above.   For example:
     *
     *     DiscoveryEngineServicesErrorCode   errorCode = DiscoveryEngineServicesErrorCode.UNKNOWN_ENDPOINT;
     *
     * This will expand out to the 5 parameters shown below.
     *
     * @param httpErrorCode   error code to use over REST calls
     * @param errorMessageId   unique Id for the message
     * @param errorMessage   text for the message
     * @param systemAction   description of the action taken by the system when the error condition happened
     * @param userAction   instructions for resolving the error
     */
    DiscoveryServiceErrorCode(int  httpErrorCode, String errorMessageId, String errorMessage, String systemAction, String userAction)
    {
        this.messageDefinition = new ExceptionMessageDefinition(httpErrorCode,
                                                                errorMessageId,
                                                                errorMessage,
                                                                systemAction,
                                                                userAction);
    }


    /**
     * Retrieve a message definition object for an exception.  This method is used when there are no message inserts.
     *
     * @return message definition object.
     */
    @Override
    public ExceptionMessageDefinition getMessageDefinition()
    {
        return messageDefinition;
    }


    /**
     * Retrieve a message definition object for an exception.  This method is used when there are values to be inserted into the message.
     *
     * @param params array of parameters (all strings).  They are inserted into the message according to the numbering in the message text.
     * @return message definition object.
     */
    @Override
    public ExceptionMessageDefinition getMessageDefinition(String... params)
    {
        messageDefinition.setMessageParameters(params);

        return messageDefinition;
    }


    /**
     * JSON-style toString
     *
     * @return string of property names and values for this enum
     */
    @Override
    public String toString()
    {
        return "DiscoveryServiceErrorCode{" +
                       "messageDefinition=" + messageDefinition +
                       '}';
    }
}
