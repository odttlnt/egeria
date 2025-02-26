/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.adminservices.configuration.registration;

import org.odpi.openmetadata.frameworks.auditlog.ComponentDevelopmentStatus;

import java.io.Serializable;

/**
 * ViewServiceDescription provides a list of registered view services.
 */
public enum ViewServiceDescription implements Serializable
{
    /**
     * View Service for glossary authoring.
     */
    GLOSSARY_AUTHOR(800,
                    ComponentDevelopmentStatus.IN_DEVELOPMENT,
                    "Glossary Author",
                    "Glossary Author OMVS",
                    "glossary-author",
                    "View Service for glossary authoring.",
                    "https://egeria-project.org/services/omvs/glossary-author/overview"),

    /**
     * Explore open metadata instances.
     */
    REPOSITORY_EXPLORER(801,
                  ComponentDevelopmentStatus.TECHNICAL_PREVIEW,
                  "Repository Explorer",
                  "Repository Explorer OMVS",
                  "rex",
                  "Explore open metadata instances.",
                  "https://egeria-project.org/services/omvs/rex/overview"),

    /**
     * Explore the open metadata types in a repository or cohort.
     */
    TYPE_EXPLORER(802,
                  ComponentDevelopmentStatus.TECHNICAL_PREVIEW,
                  "Type Explorer",
                  "Type Explorer OMVS",
                  "tex",
                  "Explore the open metadata types in a repository or cohort.",
                  "https://egeria-project.org/services/omvs/tex/overview"),

    /**
     * Explore and operate an open metadata ecosystem.
     */
    DINO(803,
                  ComponentDevelopmentStatus.TECHNICAL_PREVIEW,
                  "Dynamic Infrastructure and Operations",
                  "Dynamic Infrastructure and Operations OMVS",
                  "dino",
                  "Explore and operate an open metadata ecosystem.",
                  "https://egeria-project.org/services/omvs/dino/overview"),

    /**
     * Author server configuration.
     */
    SERVER_AUTHOR(804,
                 ComponentDevelopmentStatus.IN_DEVELOPMENT,
                 "Server Author",
                 "Server Author OMVS",
                 "server-author",
                 "Author server configuration.",
                 "https://egeria-project.org/services/omvs/server-author/overview/"),

    /**
     * View glossary terms and categories within a glossary.
     */
    GLOSSARY_BROWSER(805,
                      ComponentDevelopmentStatus.IN_DEVELOPMENT,
                      "Glossary Browser",
                      "Glossary Browser OMVS",
                      "glossary-browser",
                      "View glossary terms and categories within a glossary.",
                      "https://egeria-project.org/services/omvs/glossary-browser/overview/"),

    /**
     * Create glossary terms and organize them into categories as part of a controlled workflow process. It supports the editing glossary and multiple states.
     */
    GLOSSARY_WORKFLOW(806,
                  ComponentDevelopmentStatus.IN_DEVELOPMENT,
                 "Glossary Workflow",
                         "Glossary Workflow OMVS",
                         "glossary-workflow",
                         "Create glossary terms and organize them into categories as part of a controlled workflow process. It supports the editing glossary and multiple states.",
                         "https://egeria-project.org/services/omvs/glossary-workflow/overview/"),

    /**
     * Manage information about the logged on user as well as their preferences.
     */
    MY_PROFILE(807,
                      ComponentDevelopmentStatus.IN_DEVELOPMENT,
                      "My Profile",
                      "My Profile OMVS",
                      "my-profile",
                      "Manage information about the logged on user as well as their preferences.",
                      "https://egeria-project.org/services/omvs/my-profile/overview/"),
    ;

    private static final long serialVersionUID = 1L;

    private final int                        viewServiceCode;
    private final ComponentDevelopmentStatus viewServiceDevelopmentStatus;
    private final String                     viewServiceName;
    private final String                     viewServiceFullName;
    private final String                     viewServiceURLMarker;
    private final String                     viewServiceDescription;
    private final String                     viewServiceWiki;


    /**
     * Default Constructor
     *
     * @param viewServiceCode        ordinal for this UI view
     * @param viewServiceDevelopmentStatus development status
     * @param viewServiceURLMarker   string used in URLs
     * @param viewServiceName        symbolic name for this UI view
     * @param viewServiceDescription short description for this UI view
     * @param viewServiceWiki        wiki page for the UI view for this UI view
     */
    ViewServiceDescription(int                        viewServiceCode,
                           ComponentDevelopmentStatus viewServiceDevelopmentStatus,
                           String                     viewServiceName,
                           String                     viewServiceFullName,
                           String                     viewServiceURLMarker,
                           String                     viewServiceDescription,
                           String                     viewServiceWiki)
    {
        /*
         * Save the values supplied
         */
        this.viewServiceCode = viewServiceCode;
        this.viewServiceDevelopmentStatus = viewServiceDevelopmentStatus;
        this.viewServiceName = viewServiceName;
        this.viewServiceFullName = viewServiceFullName;
        this.viewServiceURLMarker = viewServiceURLMarker;
        this.viewServiceDescription = viewServiceDescription;
        this.viewServiceWiki = viewServiceWiki;
    }


    /**
     * Return the enum that corresponds with the supplied code.
     *
     * @param viewServiceCode requested code
     * @return enum
     */
    public static ViewServiceDescription getViewServiceDefinition(int viewServiceCode)
    {
        for (ViewServiceDescription description : ViewServiceDescription.values())
        {
            if (viewServiceCode == description.getViewServiceCode())
            {
                return description;
            }
        }
        return null;
    }


    /**
     * Return the code for this enum instance
     *
     * @return int type code
     */
    public int getViewServiceCode() {
        return viewServiceCode;
    }


    /**
     * Return the development status of the component.
     *
     * @return enum describing the status
     */
    public ComponentDevelopmentStatus getViewServiceDevelopmentStatus()
    {
        return viewServiceDevelopmentStatus;
    }


    /**
     * Return the default name for this enum instance.
     *
     * @return String default name
     */
    public String getViewServiceName() {
        return viewServiceName;
    }


    /**
     * Return the formal name for this enum instance.
     *
     * @return String default name
     */
    public String getViewServiceFullName() {
        return viewServiceFullName;
    }

    /**
     * Return the string that appears in the REST API URL that identifies the owning service.
     * Null means no REST APIs supported by this service.
     *
     * @return String default name
     */
    public String getViewServiceURLMarker() {
        return viewServiceURLMarker;
    }

    /**
     * Return the default description for the type for this enum instance.
     *
     * @return String default description
     */
    public String getViewServiceDescription() {
        return viewServiceDescription;
    }


    /**
     * Return the URL for the wiki page describing this UI view.
     *
     * @return String URL for the wiki page
     */
    public String getViewServiceWiki() {
        return viewServiceWiki;
    }
}
