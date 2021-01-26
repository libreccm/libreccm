/*
 * Copyright (C) 2016 LibreCCM Foundation.
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

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.libreccm.categorization.Category;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.persistence.Column;
import javax.persistence.ColumnResult;
import javax.persistence.ConstructorResult;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinTable;
import javax.persistence.NamedNativeQueries;
import javax.persistence.NamedNativeQuery;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SqlResultSetMapping;
import javax.persistence.SqlResultSetMappings;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FOLDERS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "Folder.rootFolders",
        query = "SELECT f FROM Folder f "
                    + "WHERE f.parentCategory IS NULL "
                    + "  AND f.type = :type"),
    @NamedQuery(
        name = "Folder.findByName",
        query = "SELECT f FROM Folder f WHERE f.name = :name"),
    @NamedQuery(
        name = "Folder.findSubFolders",
        query = "SELECT f "
                    + "FROM Folder f"
                    + " WHERE f.parentCategory = :parent "
                    + "ORDER BY f.name"
    ),
    @NamedQuery(
        name = "Folder.countSubFolders",
        query = "SELECT COUNT(f) "
                    + "FROM Folder f "
                    + "WHERE f.parentCategory = :parent"
    ),
//    @NamedQuery(
//        name = "Folder.findItems",
//        query = "SELECT c.categorizedObject "
//                    + "FROM Categorization c "
//                    + "WHERE c.category = :folder "
//                    + "AND TYPE(c.categorizedObject) IN ContentItem "
//                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
//                    + "AND c.version = "
//                    + "org.librecms.contentsection.ContentItemVersion.DRAFT"
//                    + "AND (LOWER(c.categorizedObject.displayName) LIKE :term "
//                    + "OR LOWER(c.categorizedObject.name.value) LIKE :term) "
//                    + "ORDER BY c.categorizedObject.name")
//    ,
//    @NamedQuery(
//        name = "Folder.countItems",
//        query = "SELECT COUNT(c).categorizedObject "
//                    + "FROM Categorization c "
//                    + "WHERE c.category = :folder "
//                    + "AND Type(c.categorizedObject) IN ContentItem "
//                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
//                    + "AND c.version = "
//                    + "org.librecms.contentsection.ContentItemVersion.DRAFT"
//                    + "AND (LOWER(c.categorizedObject.displayName) LIKE :term "
//                    + "OR LOWER(c.categorizedObject.name.value) LIKE :term)")
//    ,
    @NamedQuery(
        name = "Folder.hasLiveItems",
        query = "SELECT (CASE WHEN COUNT(i) > 0 THEN true ELSE false END) "
                    + "FROM ContentItem i JOIN i.categories c "
                    + "WHERE c.category = :folder "
                    + "AND i.version = org.librecms.contentsection.ContentItemVersion.LIVE"
    ),
    @NamedQuery(
        name = "Folder.findObjects",
        query
        = "SELECT o FROM CcmObject o WHERE TYPE(O) IN (ContentItem, Folder) AND o IN (SELECT f FROM Category f WHERE TYPE(f) IN (Folder) AND f.parentCategory = :folder AND LOWER(f.name) LIKE LOWER(CONCAT('%', :term))) OR o IN (SELECT i FROM ContentItem i JOIN i.categories c WHERE TYPE(i) IN (ContentItem) AND c.category = :folder AND c.type = '"
          + CATEGORIZATION_TYPE_FOLDER
              + "' AND i.version = org.librecms.contentsection.ContentItemVersion.DRAFT AND (LOWER(i.displayName) LIKE LOWER(CONCAT('%', :term)))) ORDER BY o.displayName"
    ),
    @NamedQuery(
        name = "Folder.countObjects",
        query
        = "SELECT COUNT(o) FROM CcmObject o WHERE TYPE(O) IN (ContentItem, Folder) AND o IN (SELECT f FROM Category f WHERE TYPE(f) IN (Folder) AND f.parentCategory = :folder AND LOWER(f.name) LIKE LOWER(CONCAT('%', :term))) OR o IN (SELECT i FROM ContentItem i JOIN i.categories c WHERE TYPE(i) IN (ContentItem) AND c.category = :folder AND c.type = '"
          + CATEGORIZATION_TYPE_FOLDER
              + "' AND i.version = org.librecms.contentsection.ContentItemVersion.DRAFT AND (LOWER(i.displayName) LIKE LOWER(CONCAT('%', :term))))"
    )
})
@NamedNativeQueries({
    @NamedNativeQuery(
        name = "Folder.countDocumentFolderEntries",
        query = "SELECT ("
                    + "("
                    + "SELECT COUNT(*) "
                    + "FROM ccm_core.ccm_objects "
                    + "JOIN ccm_cms.content_items "
                    + "ON ccm_objects.object_id = content_items.object_id "
                    + "JOIN ccm_core.categorizations "
                    + "    ON ccm_objects.object_id "
                    + "        = categorizations.object_id "
                    + "WHERE categorizations.category_id = :folderId "
                    + "AND content_items.version = 'DRAFT'"
                    + ") "
                    + "+ "
                    + "("
                    + "SELECT COUNT(*) "
                    + "FROM ccm_core.categories "
                    + "JOIN ccm_core.ccm_objects "
                    + "    ON categories.object_id = ccm_objects.object_id "
                    + "JOIN ccm_cms.folders "
                    + "    ON categories.object_id = folders.object_id "
                    + "WHERE categories.parent_category_id = :folderId "
                    + "AND folders.type = 'DOCUMENTS_FOLDER'"
                    + ") "
                    + ") AS entries_count",
        resultSetMapping = "Folder.countDocumentFolderEntries"
    ),
    @NamedNativeQuery(
        name = "Folder.getDocumentFolderEntries",
        query
        = "SELECT ccm_objects.object_id AS entry_id, "
              + "    ccm_objects.uuid AS entry_uuid, "
              + "    ccm_objects.display_name AS display_name, "
              + "    content_types.content_item_class AS item_class, "
              + "    content_items.creation_date AS creation_date, "
              + "    content_items.last_modified AS last_modified, "
              + "    content_items.\"version\" AS version, "
              + "    false AS is_folder "
              + "FROM ccm_cms.content_items "
              + "JOIN ccm_core.ccm_objects "
              + "    ON ccm_cms.content_items.object_id "
              + "        = ccm_core.ccm_objects.object_id "
              + "JOIN ccm_core.categorizations "
              + "    ON ccm_objects.object_id "
              + "        = ccm_core.categorizations.object_id "
              + "JOIN ccm_cms.content_types "
              + "    ON content_items.content_type_id "
              + "        = content_types.object_id "
              + "WHERE categorizations.category_id = :folderId "
              + "AND content_items.\"version\" ='DRAFT' "
              + "UNION "
              + "SELECT categories.object_id AS entry_id, "
              + "    ccm_objects.uuid AS entry_uuid, "
              + "    categories.\"name\" AS display_name, "
              + "    null AS item_class, "
              + "    null AS creation_date, "
              + "    null AS last_modified, "
              + "    null AS version, "
              + "    true as is_folder "
              + "FROM ccm_core.categories "
              + "JOIN ccm_core.ccm_objects "
              + "    ON categories.object_id = ccm_objects.object_id "
              + "JOIN ccm_cms.folders "
              + "    ON categories.object_id = folders.object_id "
              + "WHERE categories.parent_category_id = :folderId "
              + "AND folders.\"type\" = 'DOCUMENTS_FOLDER'",
        resultSetMapping = "Folder.DocumentFolderEntry"
    )
})
@SqlResultSetMappings({
    @SqlResultSetMapping(
        name = "Folder.countDocumentFolderEntries",
        columns = {
            @ColumnResult(name = "entries_count", type = long.class)
        }
    ),
    @SqlResultSetMapping(
        name = "Folder.DocumentFolderEntry",
        classes = {
            @ConstructorResult(
                columns = {
                    @ColumnResult(name = "entry_id", type = long.class),
                    @ColumnResult(name = "entry_uuid"),
                    @ColumnResult(name = "display_name"),
                    @ColumnResult(name = "item_class"),
                    @ColumnResult(name = "creation_date"),
                    @ColumnResult(name = "last_modified"),
                    @ColumnResult(name = "version"),
                    @ColumnResult(name = "is_folder", type = boolean.class)
                },
                targetClass = DocumentFolderEntry.class
            ),}
    //        entities = {
    //            @EntityResult(
    //                entityClass = DocumentFolderEntry.class,
    //                fields = {
    //                    @FieldResult(column = "entry_id", name = "entryId"),
    //                    @FieldResult(column = "entry_uuid", name = "entryUuid"),
    //                    @FieldResult(column = "display_name", name = "displayName"),
    //                    @FieldResult(column = "item_class", name = "itemClass"),
    //                    @FieldResult(
    //                        column = "creation_date",
    //                        name = "creation_date"
    //                    ),
    //                    @FieldResult(
    //                        column = "last_modified",
    //                        name = "lastModified"
    //                    ),
    //                    @FieldResult(column = "version", name = "version"),
    //                    @FieldResult(column = "is_folder", name = "folder")
    //                }
    //            )
    //        }
    )
})
public class Folder extends Category implements Serializable {

    private static final long serialVersionUID = 1L;

    @OneToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "CONTENT_SECTION_ID")
    @JoinTable(name = "FOLDER_CONTENT_SECTION_MAP", schema = DB_SCHEMA,
               inverseJoinColumns = {
                   @JoinColumn(name = "CONTENT_SECTION_ID")},
               joinColumns = {
                   @JoinColumn(name = "FOLDER_ID")})
    private ContentSection section;

    @Column(name = "TYPE", nullable = false)
    @Enumerated(EnumType.STRING)
    private FolderType type;

    public ContentSection getSection() {
        return section;
    }

    protected void setSection(final ContentSection section) {
        this.section = section;
    }

    public FolderType getType() {
        return type;
    }

    protected void setType(final FolderType type) {
        this.type = type;
    }

    public Folder getParentFolder() {
        return (Folder) getParentCategory();
    }

    /**
     * A convenient method for getting all sub folders of folder.
     *
     * @return The sub folders of this folder.
     */
    public List<Folder> getSubFolders() {
        return Collections.unmodifiableList(
            getSubCategories()
                .stream()
                .filter(subCategory -> subCategory instanceof Folder)
                .map(subCategory -> (Folder) subCategory)
                .collect(Collectors.toList()));
    }

//    public Folder getParentFolder() {
//        final Category parent = getParentCategory();
//        if (parent == null) {
//            return null;
//        } else {
//            return (Folder) getParentCategory();
//        }
//    }
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 29 * hash + Objects.hashCode(type);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Folder)) {
            return false;
        }
        final Folder other = (Folder) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return type == other.getType();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Folder;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", type = %s%s",
                                            type,
                                            data));
    }

}
