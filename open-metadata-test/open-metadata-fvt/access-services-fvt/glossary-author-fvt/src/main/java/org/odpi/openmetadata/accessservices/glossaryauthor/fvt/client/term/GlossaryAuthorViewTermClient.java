/* Copyright Contributors to the ODPi Egeria project. */

package org.odpi.openmetadata.accessservices.glossaryauthor.fvt.client.term;

import org.odpi.openmetadata.accessservices.glossaryauthor.fvt.client.GlossaryAuthorViewRestClient;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.category.Category;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.common.FindRequest;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.graph.Relationship;
import org.odpi.openmetadata.accessservices.subjectarea.properties.objects.term.Term;
import org.odpi.openmetadata.accessservices.subjectarea.responses.SubjectAreaOMASAPIResponse;
import org.odpi.openmetadata.accessservices.subjectarea.utils.QueryBuilder;
import org.odpi.openmetadata.adminservices.configuration.properties.OMAGServerConfig;
import org.odpi.openmetadata.adminservices.configuration.properties.ViewServiceConfig;
import org.odpi.openmetadata.adminservices.rest.OMAGServerConfigResponse;
import org.odpi.openmetadata.adminservices.rest.ViewServiceConfigResponse;
import org.odpi.openmetadata.adminservices.rest.ViewServicesResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.GenericResponse;
import org.odpi.openmetadata.commonservices.ffdc.rest.ResponseParameterization;
import org.odpi.openmetadata.frameworks.connectors.ffdc.InvalidParameterException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.PropertyServerException;
import org.odpi.openmetadata.frameworks.connectors.ffdc.UserNotAuthorizedException;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.ResolvableType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.odpi.openmetadata.accessservices.glossaryauthor.fvt.FVTConstants.*;


public class GlossaryAuthorViewTermClient implements GlossaryAuthorViewTerm, ResponseParameterization<Term> {

    protected final GlossaryAuthorViewRestClient client;
    private static final String BASE_URL = GLOSSARY_AUTHOR_BASE_URL + "terms";
    ///servers/{viewServerName}/open-metadata/view-services/dino/users/{userId}/server/{serverName}/instance/configuration
//    "/servers/{viewServerName}/open-metadata/view-services/dino/users/{userId}/server/{serverName}/configuration

    //private static final String GLOSSARY_AUTHOR_CONFIG_BASE_URL = ADMIN_BASE_URL + "instance/configuration";
    private static final String GLOSSARY_AUTHOR_CONFIG_BASE_URL = ADMIN_BASE_URL + "configuration";
    private static final String GLOSSARY_AUTHOR_C_BASE_URL = ADMIN_BASE_URL + "view-services/glossary-author";
    //https://localhost:10454/open-metadata/admin-services/users/garygeeke/servers/serverview/view-services/glossary-author


    private static final String GLOSSARY_AUTHOR_VIEWCONFIG_BASE_URL = ADMIN_BASE_URL + "view-services/configuration";

    protected String getMethodInfo(String methodName) {
        return methodName + " for " + resultType().getSimpleName();
    }

    public GlossaryAuthorViewTermClient(GlossaryAuthorViewRestClient client) {
        this.client = client;
    }


    @Override
    public Term create(String userId, Term term) throws PropertyServerException, InvalidParameterException, UserNotAuthorizedException {
        GenericResponse<Term> response = client.postRESTCall(userId, getMethodInfo("create"), BASE_URL, getParameterizedType(), term);

        return response.head().get();
    }

    @Override
    public Term update(String userId, String guid, Term term, boolean isReplace) throws PropertyServerException, InvalidParameterException, UserNotAuthorizedException {
        Map<String, String> params = new HashMap<>();
      //  boolean isReplace
        if (isReplace)
            params.put("isReplace", "true");
        else
            params.put("isReplace", "false");

///servers/{serverName}/open-metadata/view-services/glossary-author/users/{userId}/categories")
        GenericResponse<Term> response = client.putRESTCall(userId,
                guid,
                getMethodInfo("create"),
                BASE_URL,
                getParameterizedType(),
                term,
                params);


        return response.head().get();
    }

    @Override
    public void delete(String userId, String guid) throws PropertyServerException {
        String methodName = getMethodInfo("Restore");
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SubjectAreaOMASAPIResponse.class, Category.class);
        ParameterizedTypeReference<GenericResponse<Category>> type = ParameterizedTypeReference.forType(resolvableType.getType());

        String urlTemplate = BASE_URL;// + "/%s";

        GenericResponse<Category> response = client.delRESTCall( userId,
                type,methodName,
                urlTemplate,
                guid);

        return;
    }

    @Override
    public Term restore(String userId, String guid) throws PropertyServerException, InvalidParameterException, UserNotAuthorizedException {
        String methodName = getMethodInfo("Restore");
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SubjectAreaOMASAPIResponse.class, Term.class);
        ParameterizedTypeReference<GenericResponse<Term>> type = ParameterizedTypeReference.forType(resolvableType.getType());

        String urlTemplate = BASE_URL + "/%s";

        GenericResponse<Term> response = client.postRESTCall( userId,
                methodName,
                urlTemplate,
                type,
                guid);

        return response.head().get();
    }

/*    @Override
    public Config getConfig(String userId) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        return null;
    }*/

    @Override
    public List<Category> getCategoryChildren(String userId, String parentGuid, FindRequest findRequest) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SubjectAreaOMASAPIResponse.class, Category.class);
        ParameterizedTypeReference<GenericResponse<Category>> type = ParameterizedTypeReference.forType(resolvableType.getType());
///servers/{serverName}/open-metadata/view-services/glossary-author/users/{userId}/categories/{guid}/categories
     //   String urlTemplate = BASE_URL + "/%s/categories";

        GenericResponse<Category> completeResponse =
                client.findRESTCall(userId,getMethodInfo("getCategoryChildren"),BASE_URL,
                        type, findRequest, false, true, null);

        return completeResponse.results();
    }

    @Override
    public List<Category> getCategoryChildren(String userId, String parentGuid, FindRequest findRequest, boolean exactValue, boolean ignoreCase) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SubjectAreaOMASAPIResponse.class, Category.class);
        ParameterizedTypeReference<GenericResponse<Category>> type = ParameterizedTypeReference.forType(resolvableType.getType());
///servers/{serverName}/open-metadata/view-services/glossary-author/users/{userId}/categories/{guid}/categories
        String urlTemplate = BASE_URL + "/%s/categories";

        GenericResponse<Category> completeResponse =
                client.findRESTCallById(userId,getMethodInfo("getCategoryChildren"),urlTemplate,
                        type, findRequest, false, true, null,parentGuid);

        return completeResponse.results();
    }

    @Override
    public List<Term> findAll(String userId) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        return find(userId, new FindRequest(), false, true);
    }

    @Override
    public Term getByGUID(String userId, String guid) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SubjectAreaOMASAPIResponse.class, Term.class);
        ParameterizedTypeReference<GenericResponse<Term>> type = ParameterizedTypeReference.forType(resolvableType.getType());
        GenericResponse<Term> response =
                client.getByGUIdRESTCall(userId, guid, getMethodInfo("getByGUID"), type, BASE_URL);
        return response.head().get();
    }


    @Override
    public List<Term> find(String userId, FindRequest findRequest, boolean exactValue, boolean ignoreCase) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {

        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SubjectAreaOMASAPIResponse.class, Term.class);
        ParameterizedTypeReference<GenericResponse<Term>> type = ParameterizedTypeReference.forType(resolvableType.getType());

        GenericResponse<Term> completeResponse =
        client.findRESTCall(userId,getMethodInfo("find"),BASE_URL,
                type, findRequest, exactValue, ignoreCase, null);

        return completeResponse.results();
    }

    @Override
    public List<Category> getCategories(String userId, String termGuid, FindRequest findRequest) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        final String urnTemplate = BASE_URL + "/%s/categories";
        final String methodInfo = getMethodInfo(" getCategories");
        QueryBuilder query = client.createFindQuery(methodInfo, findRequest);
        String urlTemplate = urnTemplate + query.toString();
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SubjectAreaOMASAPIResponse.class, Category.class);
        ParameterizedTypeReference<GenericResponse<Category>> type = ParameterizedTypeReference.forType(resolvableType.getType());
        GenericResponse<Category> response = client.getByIdRESTCall(userId ,termGuid, methodInfo, type, urlTemplate);
        return response.results();
    }

    @Override
    public OMAGServerConfig getConfig(String userId) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
/*
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(OMAGServerConfigResponse.class, OMAGServerConfig.class);
        ParameterizedTypeReference<GenericResponse<OMAGServerConfig>> type = ParameterizedTypeReference.forType(resolvableType.getType());
*/

        OMAGServerConfigResponse completeResponse =
                client.getConfigRESTCall(userId,"current",getMethodInfo("getConfig"),OMAGServerConfigResponse.class,GLOSSARY_AUTHOR_CONFIG_BASE_URL);
        //findRequest, exactValue, ignoreCase, null);
        System.out.println("completeresponse ******");
        System.out.println(completeResponse.toString());
        System.out.println("****** completeresponse");
        return completeResponse.getOMAGServerConfig();
    }

    @Override
    public ViewServiceConfig getGlossaryAuthViewServiceConfig(String userId) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
/*
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(OMAGServerConfigResponse.class, OMAGServerConfig.class);
        ParameterizedTypeReference<GenericResponse<OMAGServerConfig>> type = ParameterizedTypeReference.forType(resolvableType.getType());
*/

        ViewServiceConfigResponse completeResponse =
                client.getViewServiceConfigRESTCall(userId,"current",getMethodInfo("getOmagServerName"),ViewServiceConfigResponse.class,GLOSSARY_AUTHOR_C_BASE_URL);
        //findRequest, exactValue, ignoreCase, null);
        System.out.println("GlossaryResponse ******");
        System.out.println(completeResponse.toString());
        System.out.println("****** GlossaryResponse");



        return completeResponse.getConfig();
    }

    @Override
    public List<Relationship> getRelationships(String userId, String guid, FindRequest findRequest) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(SubjectAreaOMASAPIResponse.class, Relationship.class);
        ParameterizedTypeReference<GenericResponse<Relationship>> type = ParameterizedTypeReference.forType(resolvableType.getType());

        String urlTemplate = BASE_URL + "/%s/relationships";

        GenericResponse<Relationship> completeResponse =
                client.getByIdRESTCall(userId,guid, getMethodInfo("find"),
                        type, urlTemplate);

        return completeResponse.results();
    }


    @Override
    public List<ViewServiceConfig> getViewServiceConfigs(String userId) throws InvalidParameterException, PropertyServerException, UserNotAuthorizedException {
/*
        ResolvableType resolvableType = ResolvableType.forClassWithGenerics(OMAGServerConfigResponse.class, OMAGServerConfig.class);
        ParameterizedTypeReference<GenericResponse<OMAGServerConfig>> type = ParameterizedTypeReference.forType(resolvableType.getType());
*/

        ViewServicesResponse completeResponse =
                client.getViewConfigRESTCall(userId,getMethodInfo("getViewServiceConfig"),ViewServicesResponse.class,GLOSSARY_AUTHOR_VIEWCONFIG_BASE_URL);
        //findRequest, exactValue, ignoreCase, null);

        return completeResponse.getServices();
    }

    @Override
    public List<Relationship> getAllRelationships(String userId, String guid) {
        return null;
    }

    @Override
    public List<Term> getTerms(String userId, String categoryGuid, FindRequest findRequest) {
        return null;
    }

    @Override
    public Class<? extends GenericResponse> responseType() {
        return SubjectAreaOMASAPIResponse.class;
    }
}
