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

import org.hibernate.envers.Audited;
import org.hibernate.envers.RelationTargetAuditMode;
import org.hibernate.search.annotations.Field;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.workflow.Workflow;
import org.librecms.lifecycle.Lifecycle;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.search.annotations.IndexedEmbedded;

import javax.persistence.FetchType;

import org.hibernate.envers.NotAudited;
import org.librecms.contentsection.privileges.ItemPrivileges;

import static org.librecms.CmsConstants.*;

/**
 * Base type for all content item types. Specifies some common properties.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "CONTENT_ITEMS", schema = DB_SCHEMA)
//@Indexed
@NamedQueries({
    @NamedQuery(
        name = "ContentItem.findById",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE i.objectId = :objectId "
                  + "AND ("
                  + "      ("
                  + "        p.grantee IN :roles "
                  + "        AND p.grantedPrivilege = "
                  + "          (CASE WHEN i.version = 'DRAFT' "
                  + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                  + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                  + "           END"
                  + "          )"
                  + "      ) "
                  + "      OR true = :isSystemUser OR true = :isAdmin"
                  + "    )")
    ,
    @NamedQuery(
        name = "ContentItem.findByUuid",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE i.uuid = :uuid "
                  + "AND ("
                  + "      ("
                  + "        p.grantee IN :roles "
                  + "        AND p.grantedPrivilege = "
                  + "          (CASE WHEN i.version = 'DRAFT' "
                  + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                  + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                  + "           END"
                  + "          )"
                  + "      ) "
                  + "      OR true = :isSystemUser OR true = :isAdmin"
                  + "    )")
    ,
    @NamedQuery(
        name = "ContentItem.findByType",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE TYPE(i) = :type "
                  + "AND ("
                  + "      ("
                  + "        p.grantee IN :roles "
                  + "        AND p.grantedPrivilege = "
                  + "          (CASE WHEN i.version = 'DRAFT' "
                  + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                  + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                  + "           END"
                  + "          )"
                  + "      ) "
                  + "      OR true = :isSystemUser OR true = :isAdmin"
                  + "    )")
    ,
    @NamedQuery(
        name = "ContentItem.findByIdAndType",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE i.objectId = :objectId "
                  + "AND TYPE(i) = :type "
                  + "AND ("
                  + "      ("
                  + "        p.grantee IN :roles "
                  + "        AND p.grantedPrivilege = "
                  + "          (CASE WHEN i.version = 'DRAFT' "
                  + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                  + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                  + "           END"
                  + "          )"
                  + "      ) "
                  + "      OR true = :isSystemUser OR true = :isAdmin"
                  + "    )")
    ,
    @NamedQuery(
        name = "ContentItem.findByUuidAndType",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE i.uuid = :uuid "
                  + "AND TYPE(i) = :type "
                  + "AND ("
                  + "      ("
                  + "        p.grantee IN :roles "
                  + "        AND p.grantedPrivilege = "
                  + "          (CASE WHEN i.version = 'DRAFT' "
                  + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                  + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                  + "           END"
                  + "          )"
                  + "      ) "
                  + "      OR true = :isSystemUser OR true = :isAdmin"
                  + "    )")
    ,
    @NamedQuery(
        name = "ContentItem.findByContentSection",
        query = "SELECT DISTINCT i "
                    + "FROM ContentItem i "
                    + "JOIN i.contentType t "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE t.contentSection = :section "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivileges = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "            THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "            ELSE '" + ItemPrivileges.VIEW_PUBLISHED
                    + "' "
                    + "            END"
                    + "              )"
                    + "      )"
                    + "          OR true = :isSystemUser OR true = :isAdmin"
                    + ")")
    ,
    @NamedQuery(
        name = "ContentItem.findByNameAndContentSection",
        query = "SELECT DISTINCT i "
                    + "FROM ContentItem i "
                    + "JOIN i.contentType t "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE t.contentSection = :section "
                    + "AND lower(i.displayName) LIKE CONCAT('%', :name, '%s') "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivileges = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "            THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "            ELSE '" + ItemPrivileges.VIEW_PUBLISHED
                    + "' "
                    + "            END"
                    + "              )"
                    + "      )"
                    + "          OR true = :isSystemUser OR true = :isAdmin"
                    + ")")
    ,
    @NamedQuery(
        name = "ContentItem.findByTypeAndContentSection",
        query = "SELECT DISTINCT i "
                    + "FROM ContentItem i "
                    + "JOIN i.contentType t "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE t.contentSection = :section "
                    + "AND TYPE(i) = :type "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivileges = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "            THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "            ELSE '" + ItemPrivileges.VIEW_PUBLISHED
                    + "' "
                    + "            END"
                    + "              )"
                    + "      )"
                    + "          OR true = :isSystemUser OR true = :isAdmin"
                    + ")")
    ,
        @NamedQuery(
        name = "ContentItem.findByNameAndTypeAndContentSection",
        query = "SELECT DISTINCT i "
                    + "FROM ContentItem i "
                    + "JOIN i.contentType t "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE t.contentSection = :section "
                    + "AND TYPE(i) = :type "
                    + "AND lower(i.displayName) LIKE CONCAT('%', :name, '%s') "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivileges = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "            THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "            ELSE '" + ItemPrivileges.VIEW_PUBLISHED
                    + "' "
                    + "            END"
                    + "              )"
                    + "      )"
                    + "          OR true = :isSystemUser OR true = :isAdmin"
                    + ")")
    ,
    @NamedQuery(
        name = "ContentItem.findByFolder",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "JOIN i.categories c "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE c.category = :folder "
                  + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                  + "AND ("
                  + "      ("
                  + "        p.grantee IN :roles "
                  + "        AND p.grantedPrivilege = "
                  + "          (CASE WHEN i.version = 'DRAFT' "
                  + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                  + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                  + "           END"
                  + "          )"
                  + "       )"
                  + "       OR true = :isSystemUser OR true = :isAdmin"
                  + "     )")
    ,
    @NamedQuery(
        name = "ContentItem.countItemsInFolder",
        query
            = "SELECT COUNT(DISTINCT i) "
                  + "FROM ContentItem i "
                  + "JOIN i.categories c "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE c.category = :folder "
                  + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                  + "AND ("
                  + "      ("
                  + "        p.grantee IN :roles "
                  + "        AND p.grantedPrivilege = "
                  + "          (CASE WHEN i.version = 'DRAFT' "
                  + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                  + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                  + "           END"
                  + "          )"
                  + "       )"
                  + "       OR true = :isSystemUser OR true = :isAdmin"
                  + "     )")
    ,
    @NamedQuery(
        name = "ContentItem.findByNameInFolder",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "JOIN i.categories c "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE c.category = :folder "
                  + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                  + "AND i.displayName = :name "
                  + "AND ("
                  + "      ("
                  + "        p.grantee IN :roles "
                  + "        AND p.grantedPrivilege = "
                  + "          (CASE WHEN i.version = 'DRAFT' "
                  + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                  + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                  + "           END"
                  + "          )"
                  + "      )"
                  + "      OR true = :isSystemUser OR true = :isAdmin"
                  + "    )")
    ,
    @NamedQuery(
        name = "ContentItem.countByNameInFolder",
        query = "SELECT COUNT(DISTINCT i)"
                    + " FROM ContentItem i "
                    + "JOIN i.categories c "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND i.displayName = :name "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivilege = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                    + "           END"
                    + "          )"
                    + "       )"
                    + "       OR true = :isSystemUser OR true = :isAdmin"
                    + "     )")
    ,
    @NamedQuery(
        name = "ContentItem.filterByFolderAndName",
        query = "SELECT DISTINCT i "
                    + "FROM ContentItem i "
                    + "JOIN i.categories c "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND LOWER(i.displayName) LIKE CONCAT(LOWER(:name), '%') "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivilege = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                    + "           END"
                    + "          )"
                    + "       )"
                    + "       OR true = :isSystemUser OR true = :isAdmin"
                    + "     ) "
                    + "ORDER BY i.displayName")
    ,
    @NamedQuery(
        name = "ContentItem.countFilterByFolderAndName",
        query = "SELECT COUNT(DISTINCT i) FROM ContentItem i "
                    + "JOIN i.categories c "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND LOWER(i.displayName) LIKE CONCAT(LOWER(:name), '%') "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivilege = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                    + "           END"
                    + "          )"
                    + "       )"
                    + "       OR true = :isSystemUser OR true = :isAdmin"
                    + "     )"
    )
    ,
    @NamedQuery(
        name = "ContentItem.filterByFolderAndType",
        query = "SELECT DISTINCT i "
                    + "FROM ContentItem i "
                    + "JOIN i.categories c "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND TYPE(i) = :type "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivilege = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                    + "           END"
                    + "          )"
                    + "       )"
                    + "       OR true = :isSystemUser OR true = :isAdmin"
                    + "     ) "
                    + "ORDER BY i.displayName")
    ,
    @NamedQuery(
        name = "ContentItem.filterByFolderAndTypeAndName",
        query = "SELECT DISTINCT i "
                    + "FROM ContentItem i "
                    + "JOIN i.categories c "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND LOWER(i.displayName) LIKE CONCAT(LOWER(:name), '%') "
                    + "AND TYPE(i) = :type"
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivilege = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                    + "           END"
                    + "          )"
                    + "       )"
                    + "       OR true = :isSystemUser OR true = :isAdmin"
                    + "     ) "
                    + "ORDER BY i.displayName")
    ,
    @NamedQuery(
        name = "ContentItem.hasLiveVersion",
        query = "SELECT (CASE WHEN COUNT(i) > 0 THEN true ELSE false END) "
                    + "FROM ContentItem i "
                    + "WHERE i.itemUuid = :uuid "
                    + "AND i.version = org.librecms.contentsection.ContentItemVersion.LIVE")
    ,
    @NamedQuery(
        name = "ContentItem.findDraftVersion",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE i.itemUuid = :uuid "
                  + "AND i.version = 'DRAFT' "
                  + "AND "
                  + "((p.grantee IN :roles "
                  + "AND p.grantedPrivilege = '" + ItemPrivileges.PREVIEW + "' "
                  + ") OR true = :isSystemUser OR true = :isAdmin)")
    ,
    @NamedQuery(
        name = "ContentItem.findLiveVersion",
        query
            = "SELECT DISTINCT i "
                  + "FROM ContentItem i "
                  + "LEFT JOIN i.permissions p "
                  + "WHERE i.itemUuid = :uuid "
                  + "AND i.version = 'LIVE' "
                  + "AND "
                  + "((p.grantee IN :roles "
                  + "AND p.grantedPrivilege = "
                  + "'"
                  + ItemPrivileges.VIEW_PUBLISHED
                  + "' "
                  + ") OR true = :isSystemUser OR true = :isAdmin)")
    ,
    @NamedQuery(
        name = "ContentItem.findItemWithWorkflow",
        query = "SELECT DISTINCT i "
                    + "FROM ContentItem i "
                    + "LEFT JOIN i.permissions p "
                    + "WHERE i.workflow = :workflow "
                    + "AND ("
                    + "      ("
                    + "        p.grantee IN :roles "
                    + "        AND p.grantedPrivilege = "
                    + "          (CASE WHEN i.version = 'DRAFT' "
                    + "           THEN '" + ItemPrivileges.PREVIEW + "' "
                    + "           ELSE '" + ItemPrivileges.VIEW_PUBLISHED + "' "
                    + "           END"
                    + "          )"
                    + "       )"
                    + "       OR true = :isSystemUser OR true = :isAdmin"
                    + "     )"
    )
})
public class ContentItem extends CcmObject implements Serializable {

    private static final long serialVersionUID = 5897287630227129653L;

    @Column(name = "ITEM_UUID", nullable = false)
    @Field
    private String itemUuid;

    /**
     * The name of the content item which is used to generate the URL of the
     * content item. We are using a {@link LocalizedString} here to make it
     * possible to generate localised URLs. Therefore only characters allowed in
     * URLs should be used here.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CONTENT_ITEM_NAMES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")
                               }
        )
    )
//    @Field
    private LocalizedString name;

    /**
     * The content type associated with the content item.
     */
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CONTENT_TYPE_ID")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
//    @Field
    private ContentType contentType;

    /**
     * The human readable title of the content item.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CONTENT_ITEM_TITLES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")
                               }
        )
    )
    @IndexedEmbedded
    private LocalizedString title;

    /**
     * A short text which describes the content of the content item.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "CONTENT_ITEM_DESCRIPTIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    @IndexedEmbedded
    private LocalizedString description;

    /**
     * The version/publishing state of the content item.
     */
    @Column(name = "VERSION")
    @Enumerated(EnumType.STRING)
    @Field
    private ContentItemVersion version;

    /**
     * The launch date of the content item (date when the item is made public)
     */
    @Column(name = "LAUNCH_DATE")
    @Temporal(TemporalType.DATE)
    private Date launchDate;

    /**
     * String with the IDs (separated by slashes) of the ancestors of the
     * content item (aka the path of the content item).
     */
    @Column(name = "ANCESTORS", length = 1024)
    private String ancestors;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private List<AttachmentList> attachments;

    @OneToOne()
    @JoinColumn(name = "LIFECYCLE_ID")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Lifecycle lifecycle;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "WORKFLOW_ID")
    @Audited(targetAuditMode = RelationTargetAuditMode.NOT_AUDITED)
    private Workflow workflow;

    /**
     * Date when the item was created. This information is also available from
     * the revision managed by Envers, but getting access to them involves some
     * complex queries. Also it is not possible to get the creation date (date
     * of the first entity) together with the last modified date (date of the
     * last revision/current revision) of the item.
     */
    @Column(name = "CREATION_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    @NotAudited
    private Date creationDate;

    /**
     * Date the item was last modified. This information is also available from
     * the revision managed by Envers, but getting access to them involves some
     * complex queries. Also it is not possible to get the creation date (date
     * of the first entity) together with the last modified date (date of the
     * last revision/current revision) of the item.
     */
    @Column(name = "LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    @NotAudited
    private Date lastModified;

    /**
     * The name of the user which created the item. This information is also
     * available from the revision managed by Envers, but getting access to them
     * involves some complex queries. Also it is not possible to get the
     * creation user (the user which created the first entity) together with the
     * last modifying user (user which created the last revision/current
     * revision) of the item.
     *
     * Please note that there is no grantee that the user still exists.
     */
    @Column(name = "CREATION_USER_NAME")
    @NotAudited
    private String creationUserName;

    /**
     * The name of the user which was the last one which modified the item. This
     * information is also available from the revision managed by Envers, but
     * getting access to them involves some complex queries. Also it is not
     * possible to get the creation user (the user which created the first
     * entity) together with the last modifying user (user which created the
     * last revision/current revision) of the item.
     *
     * Please note that there is no grantee that the user still exists.
     */
    @Column(name = "LAST_MODIFYING_USER_NAME")
    @NotAudited
    private String lastModifyingUserName;

    public ContentItem() {
        name = new LocalizedString();
        title = new LocalizedString();
        description = new LocalizedString();
        attachments = new ArrayList<>();
    }

    public String getItemUuid() {
        return itemUuid;
    }

    protected void setItemUuid(final String itemUuid) {
        this.itemUuid = itemUuid;
    }

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        this.name = name;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public ContentItemVersion getVersion() {
        return version;
    }

    public void setVersion(final ContentItemVersion version) {
        this.version = version;
    }

    public Date getLaunchDate() {
        if (launchDate == null) {
            return null;
        } else {
            return new Date(launchDate.getTime());
        }
    }

    public void setLaunchDate(final Date launchDate) {
        if (launchDate == null) {
            this.launchDate = null;
        } else {
            this.launchDate = new Date(launchDate.getTime());
        }
    }

    public String getAncestors() {
        return ancestors;
    }

    public void setAncestors(final String ancestors) {
        this.ancestors = ancestors;
    }

    public List<AttachmentList> getAttachments() {
        Collections.sort(attachments);
        return Collections.unmodifiableList(attachments);
    }

    protected void setAttachments(final List<AttachmentList> attachments) {
        if (attachments == null) {
            this.attachments = new ArrayList<>();
        } else {
            this.attachments = attachments;
        }
    }

    protected void addAttachmentList(final AttachmentList attachmentList) {
        attachments.add(attachmentList);
    }

    protected void removeAttachmentList(final AttachmentList attachmentList) {
        attachments.remove(attachmentList);
    }

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(final Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public Workflow getWorkflow() {
        return workflow;
    }

    public void setWorkflow(final Workflow workflow) {
        this.workflow = workflow;
    }

    public Date getCreationDate() {
        if (creationDate == null) {
            return null;
        } else {
            return new Date(creationDate.getTime());
        }
    }

    public void setCreationDate(final Date creationDate) {
        if (creationDate == null) {
            this.creationDate = null;
        } else {
            this.creationDate = new Date(creationDate.getTime());
        }
    }

    public Date getLastModified() {
        if (lastModified == null) {
            return null;
        } else {
            return new Date(lastModified.getTime());
        }
    }

    public void setLastModified(final Date lastModified) {
        if (lastModified == null) {
            this.lastModified = null;
        } else {
            this.lastModified = new Date(lastModified.getTime());
        }
    }

    public String getCreationUserName() {
        return creationUserName;
    }

    public void setCreationUserName(final String creationUserName) {
        this.creationUserName = creationUserName;
    }

    public String getLastModifyingUserName() {
        return lastModifyingUserName;
    }

    public void setLastModifyingUserName(final String lastModifyingUserName) {
        this.lastModifyingUserName = lastModifyingUserName;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + Objects.hashCode(itemUuid);
        hash = 59 * hash + Objects.hashCode(name);
//        hash = 59 * hash + Objects.hashCode(contentType);
        hash = 59 * hash + Objects.hashCode(title);
        hash = 59 * hash + Objects.hashCode(description);
        hash = 59 * hash + Objects.hashCode(version);
        hash = 59 * hash + Objects.hashCode(launchDate);
        hash = 59 * hash + Objects.hashCode(lifecycle);
        hash = 59 * hash + Objects.hashCode(workflow);
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

        if (!(obj instanceof ContentItem)) {
            return false;
        }

        final ContentItem other = (ContentItem) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(itemUuid, other.getItemUuid())) {
            return false;
        }

        if (!Objects.equals(name, other.getName())) {
            return false;
        }
//        if (!Objects.equals(contentType, other.getContentType())) {
//            return false;
//        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (version != other.getVersion()) {
            return false;
        }
        if (!Objects.equals(launchDate, other.getLaunchDate())) {
            return false;
        }
        if (!Objects.equals(lifecycle, other.getLifecycle())) {
            return false;
        }
        return Objects.equals(workflow, other.getWorkflow());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ContentItem;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", itemUuid = %s, "
                                                + "name = %s, "
                                                //                                                + "contentType = { %s }, "
                                                + "title = %s, "
                                                + "description = %s, "
                                                + "version = %s, "
                                                + "launchDate = %s, "
                                                + "lifecycle = { %s }, "
                                                + "workflow = { %s }"
                                                + "%s",
                                            itemUuid,
                                            Objects.toString(name),
                                            //                                            Objects.toString(contentType),
                                            Objects.toString(title),
                                            Objects.toString(description),
                                            Objects.toString(version),
                                            Objects.toString(launchDate),
                                            Objects.toString(lifecycle),
                                            Objects.toString(workflow),
                                            data));
    }

}
