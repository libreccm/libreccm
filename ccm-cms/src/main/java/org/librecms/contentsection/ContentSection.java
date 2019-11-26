/*
 * Copyright (C) 2015 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.librecms.contentsection;

import org.libreccm.imexport.Exportable;
import org.libreccm.security.RecursivePermissions;
import org.libreccm.security.Role;
import org.libreccm.web.CcmApplication;
import org.libreccm.workflow.Workflow;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import java.util.ArrayList;

import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;

import org.librecms.contentsection.privileges.AssetPrivileges;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.contentsection.privileges.TypePrivileges;
import org.librecms.lifecycle.LifecycleDefinition;

import javax.persistence.OrderBy;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONTENT_SECTIONS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "ContentSection.findById",
        query = "SELECT S FROM ContentSection s WHERE s.objectId = :objectId")
    ,
    @NamedQuery(
        name = "ContentSection.findByLabel",
        query = "SELECT s FROM ContentSection s WHERE s.label = :label")
    ,
    @NamedQuery(
        name = "ContentSection.findUsableContentTypes",
        query = "SELECT t FROM ContentType t "
                    + "WHERE t.contentSection = :section "
                    + "AND "
                    + "(t IN (SELECT p.object FROM Permission p "
                    + "WHERE p.grantedPrivilege = '"
                    + TypePrivileges.USE_TYPE + "' "
                    + "AND p.grantee in :roles) "
                    + "OR true = :isSysAdmin)")
    ,
    @NamedQuery(
        name = "ContentSection.countUsableContentTypes",
        query = "SELECT COUNT(t) FROM ContentType t "
                    + "WHERE t.contentSection = :section "
                    + "AND "
                    + "(t IN (SELECT p.object FROM Permission p "
                    + "WHERE p.grantedPrivilege = '"
                    + TypePrivileges.USE_TYPE + "' "
                    + "AND p.grantee IN :roles) "
                    + "OR true = :isSysAdmin)")
    ,
    @NamedQuery(
        name = "ContentSection.hasUsableContentTypes",
        query = "SELECT (CASE WHEN COUNT(t) > 0 THEN true ELSE false END)"
                    + "FROM ContentType t "
                    + "WHERE t.contentSection = :section "
                    + "AND "
                    + "(t IN (SELECT p.object FROM Permission p "
                    + "WHERE p.grantedPrivilege = '"
                    + TypePrivileges.USE_TYPE + "' "
                    + "AND p.grantee IN :roles) "
                    + "OR true = :isSysAdmin)")
    ,
    @NamedQuery(
        name = "ContentSection.findPermissions",
        query = "SELECT p FROM Permission p "
                    + "WHERE (p.object = :section "
                    + "       OR p.object = :rootDocumentsFolder"
                    + "                   OR p.object = :rootAssetsFolder) "
                    + "AND p.grantee = :role")
})
//@ApplicationType(
//    name = CONTENT_SECTION_APP_TYPE,
//    descBundle = "org.librecms.contentsection.ContentSectionResources",
//    singleton = false,
//    creator = ContentSectionCreator.class,
//    servlet = ContentSectionServlet.class,
//    instanceForm = ApplicationInstanceForm.class)
public class ContentSection 
    extends CcmApplication 
    implements Serializable, Exportable {

    private static final long serialVersionUID = -671718122153931727L;

    protected static final String ROOT = "root";
    protected static final String ASSETS = "assets";
    protected static final String ALERT_RECIPIENT = "alert_recipient";
    protected static final String AUTHOR = "author";
    protected static final String EDITOR = "editor";
    protected static final String MANAGER = "manager";
    protected static final String PUBLISHER = "publisher";
    protected static final String CONTENT_READER = "content_reader";

    @Column(name = "LABEL", length = 512)
    private String label;

    @RecursivePermissions(privileges = {ItemPrivileges.ADMINISTER,
                                        ItemPrivileges.APPLY_ALTERNATE_WORKFLOW,
                                        ItemPrivileges.APPROVE,
                                        ItemPrivileges.CATEGORIZE,
                                        ItemPrivileges.CREATE_NEW,
                                        ItemPrivileges.DELETE,
                                        ItemPrivileges.EDIT,
                                        ItemPrivileges.PREVIEW,
                                        ItemPrivileges.PUBLISH,
                                        ItemPrivileges.VIEW_PUBLISHED})
    @OneToOne
    @JoinColumn(name = "ROOT_DOCUMENTS_FOLDER_ID")
    private Folder rootDocumentsFolder;

    @RecursivePermissions(privileges = {AssetPrivileges.CREATE_NEW,
                                        AssetPrivileges.DELETE,
                                        AssetPrivileges.EDIT,
                                        AssetPrivileges.USE,
                                        AssetPrivileges.VIEW})
    @OneToOne
    @JoinColumn(name = "ROOT_ASSETS_FOLDER_ID")
    private Folder rootAssetsFolder;

    @Column(name = "PAGE_RESOLVER_CLASS", length = 1024)
    private String pageResolverClass;

    @Column(name = "ITEM_RESOLVER_CLASS", length = 1024)
    private String itemResolverClass;

    @Column(name = "TEMPLATE_RESOLVER_CLASS", length = 1024)
    private String templateResolverClass;

    @Column(name = "XML_GENERATOR_CLASS", length = 1024)
    private String xmlGeneratorClass;

    @ManyToMany
    @JoinTable(name = "CONTENT_SECTION_ROLES",
               schema = DB_SCHEMA,
               joinColumns = {
                   @JoinColumn(name = "SECTION_ID")
               },
               inverseJoinColumns = {
                   @JoinColumn(name = "ROLE_ID")
               })
    private List<Role> roles = new ArrayList<>();

    @Column(name = "DEFAULT_LOCALE")
    private Locale defaultLocale;

    @OneToMany(mappedBy = "contentSection")
    @OrderBy("contentItemClass ASC")
    private List<ContentType> contentTypes = new ArrayList<>();

    @OneToMany
    @JoinTable(
        name = "CONTENT_SECTION_LIFECYCLE_DEFINITIONS",
        schema = DB_SCHEMA,
        joinColumns = {
            @JoinColumn(name = "CONTENT_SECTION_ID")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "LIFECYCLE_DEFINITION_ID")
        }
    )
    private List<LifecycleDefinition> lifecycleDefinitions = new ArrayList<>();

    @OneToMany
    @JoinTable(
        name = "CONTENT_SECTION_WORKFLOW_TEMPLATES",
        schema = DB_SCHEMA,
        joinColumns = {
            @JoinColumn(name = "CONTENT_SECTION_ID")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "WORKFLOW_TEMPLATE_ID")
        }
    )
    private List<Workflow> workflowTemplates = new ArrayList<>();

    public ContentSection() {
        roles = new ArrayList<>();
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Folder getRootDocumentsFolder() {
        return rootDocumentsFolder;
    }

    protected void setRootDocumentFolder(final Folder rootDocumentsFolder) {
        this.rootDocumentsFolder = rootDocumentsFolder;
    }

    public Folder getRootAssetsFolder() {
        return rootAssetsFolder;
    }

    protected void setRootAssetsFolder(final Folder rootAssetsFolder) {
        this.rootAssetsFolder = rootAssetsFolder;
    }

    public String getPageResolverClass() {
        return pageResolverClass;
    }

    public void setPageResolverClass(final String pageResolverClass) {
        this.pageResolverClass = pageResolverClass;
    }

    public String getItemResolverClass() {
        return itemResolverClass;
    }

    public void setItemResolverClass(final String itemResolverClass) {
        this.itemResolverClass = itemResolverClass;
    }

    public String getTemplateResolverClass() {
        return templateResolverClass;
    }

    public void setTemplateResolverClass(final String templateResolverClass) {
        this.templateResolverClass = templateResolverClass;
    }

    public String getXmlGeneratorClass() {
        return xmlGeneratorClass;
    }

    public void setXmlGeneratorClass(final String xmlGeneratorClass) {
        this.xmlGeneratorClass = xmlGeneratorClass;
    }

    public List<Role> getRoles() {
        return Collections.unmodifiableList(roles);
    }

    protected void setRoles(final List<Role> roles) {
        this.roles = roles;
    }

    protected void addRole(final Role role) {
        roles.add(role);
    }

    protected void removeRole(final Role role) {

        int index = -1;
        for (int i = 0; i < roles.size(); i++) {
            if (roles.get(i).getName().equals(role.getName())) {
                index = i;
            }
        }

        if (index == -1) {
            //Role not part of ContentSection
            return;
        }

        roles.remove(index);
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(final Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    public List<ContentType> getContentTypes() {
        return Collections.unmodifiableList(contentTypes);
    }

    protected void setContentTypes(final List<ContentType> contentTypes) {
        this.contentTypes = contentTypes;
    }

    protected void addContentType(final ContentType contentType) {
        contentTypes.add(contentType);
    }

    protected void removeContentType(final ContentType contentType) {
        contentTypes.remove(contentType);
    }

    public List<LifecycleDefinition> getLifecycleDefinitions() {
        return Collections.unmodifiableList(lifecycleDefinitions);
    }

    protected void setLifecycleDefinitions(
        final List<LifecycleDefinition> lifecycleDefinitions) {
        this.lifecycleDefinitions = lifecycleDefinitions;
    }

    protected void addLifecycleDefinition(final LifecycleDefinition definition) {
        lifecycleDefinitions.add(definition);
    }

    protected void removeLifecycleDefinition(
        final LifecycleDefinition definition) {
        lifecycleDefinitions.remove(definition);
    }

    public List<Workflow> getWorkflowTemplates() {
        return Collections.unmodifiableList(workflowTemplates);
    }

    protected void setWorkflowTemplates(
        final List<Workflow> workflowTemplates) {
        this.workflowTemplates = workflowTemplates;
    }

    protected void addWorkflowTemplate(final Workflow template) {
        if (!template.isAbstractWorkflow()) {
            throw new IllegalArgumentException(
                "The provided workflow is not an abstract workflow.");
        }
        workflowTemplates.add(template);
    }

    protected void removeWorkflowTemplate(final Workflow template) {
        workflowTemplates.remove(template);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash + Objects.hashCode(label);
        hash = 47 * hash + Objects.hashCode(rootDocumentsFolder);
        hash = 47 * hash + Objects.hashCode(rootAssetsFolder);
        hash = 47 * hash + Objects.hashCode(pageResolverClass);
        hash = 47 * hash + Objects.hashCode(itemResolverClass);
        hash = 47 * hash + Objects.hashCode(templateResolverClass);
        hash = 47 * hash + Objects.hashCode(xmlGeneratorClass);
        hash = 47 * hash + Objects.hashCode(defaultLocale);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ContentSection)) {
            return false;
        }

        final ContentSection other = (ContentSection) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(label, other.getLabel())) {
            return false;
        }
        if (!Objects.equals(rootDocumentsFolder, other.getRootDocumentsFolder())) {
            return false;
        }
        if (!Objects.equals(rootAssetsFolder, other.getRootAssetsFolder())) {
            return false;
        }
        if (!Objects.equals(pageResolverClass, other.getPageResolverClass())) {
            return false;
        }
        if (!Objects.equals(itemResolverClass, other.getItemResolverClass())) {
            return false;
        }
        if (!Objects.equals(templateResolverClass,
                            other.getTemplateResolverClass())) {
            return false;
        }
        if (!Objects.equals(xmlGeneratorClass, other.getXmlGeneratorClass())) {
            return false;
        }
        return Objects.equals(defaultLocale, other.getDefaultLocale());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ContentSection;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(
            ", label = \"%s\", "
                + "rootDocumentsFolder = \"%s\", "
                + "rootAssetsFolder = \"%s\", "
                + "pageResolverClass = \"%s\", "
                + "itemResolverClass = \"%s\", "
                + "templateResolverClass = \"%s\", "
                + "xmlGeneratorClass = \"%s\", "
                + "defaultLocale = \"%s\"%s",
            label,
            Objects.toString(rootDocumentsFolder),
            Objects.toString(rootAssetsFolder),
            pageResolverClass,
            itemResolverClass,
            templateResolverClass,
            xmlGeneratorClass,
            Objects.toString(defaultLocale),
            data));
    }

}
