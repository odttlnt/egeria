/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.accessservices.dataengine.server.converters;

import org.odpi.openmetadata.commonservices.generichandlers.OpenMetadataAPIGenericConverter;
import org.odpi.openmetadata.repositoryservices.connectors.stores.metadatacollectionstore.repositoryconnector.OMRSRepositoryHelper;
/**
 * DatabaseTableConverter transfers the relevant properties from an Open Metadata Repository Services (OMRS)
 * EntityDetail object into a RelationalTable bean.
 */
public class DatabaseTableConverter <B> extends OpenMetadataAPIGenericConverter<B> {
    public DatabaseTableConverter(OMRSRepositoryHelper repositoryHelper, String serviceName, String serverName) {
        super(repositoryHelper, serviceName, serverName);
    }
}
