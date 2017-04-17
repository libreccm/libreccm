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
import org.libreccm.categorization.Categorization;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.LocalizedString;
import org.librecms.CmsConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * Base class for all assets providing common fields. This class is
 * <strong>not</strong> indented for direct use. Only to sub classes should be
 * used.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(schema = DB_SCHEMA, name = "ASSETS")
@Inheritance(strategy = InheritanceType.JOINED)
@Audited
@NamedQueries({
    @NamedQuery(name = "Asset.findByUuid",
                query = "SELECT a FROM Asset a WHERE a.uuid = :uuid")
    ,
    @NamedQuery(name = "Asset.findByType",
                query = "SELECT a FROM Asset a "
                            + "WHERE TYPE(a) = :type "
                            + "AND a.categories IS NOT EMPTY")
    ,
    @NamedQuery(name = "Asset.findByTypeAndContentSection",
                query = "SELECT a FROM Asset a "
                            + "JOIN a.categories c "
                            + "WHERE TYPE(a) = :type "
                            + "AND c.category.section = :section")
    ,
    @NamedQuery(name = "Asset.findByUuidAndType",
                query = "SELECT a FROM Asset a "
                            + "WHERE a.uuid = :uuid "
                            + "AND TYPE(a) = :type")
    ,
    @NamedQuery(name = "Asset.findByContentSection",
                query = "SELECT a FROM Asset a "
                            + "JOIN a.categories c "
                            + "WHERE c.category.section = :section")
    ,
    @NamedQuery(name = "Asset.findByTitle'",
                query = "SELECT a FROM Asset a "
                            + "JOIN a.title.values t "
                            + "WHERE LOWER(t) LIKE CONCAT('%', :title, '%') "
                            + "AND a.categories IS NOT EMPTY")
    ,
    @NamedQuery(name = "Asset.findByTitleAndContentSection",
                query = "SELECT a FROM Asset a "
                            + "JOIN a.title.values t "
                            + "JOIN a.categories c "
                            + "WHERE LOWER(t) LIKE CONCAT('%s', :title, '%s') "
                            + "AND c.category.section = :section")
    ,
    @NamedQuery(name = "Asset.findByTitleAndType",
                query = "SELECT a FROM Asset a "
                            + "JOIN a.title.values t "
                            + "WHERE LOWER(t) LIKE CONCAT('%', :title, '%') "
                            + "AND TYPE(a) = :type")
    ,
    @NamedQuery(name = "Asset.findByTitleAndTypeAndContentSection",
                query = "SELECT a FROM Asset a "
                            + "JOIN a.title.values t "
                            + "JOIN a.categories c "
                            + "WHERE LOWER(t) LIKE CONCAT('%', :title, '%') "
                            + "AND TYPE(a) = :type "
                            + "AND c.category.section = :section")
    ,
    @NamedQuery(
        name = "Asset.findByFolder",
        query = "SELECT a FROM Asset a "
                    + "JOIN a.categories c "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "'")
    ,
    @NamedQuery(
        name = "Asset.countInFolder",
        query = "SELECT COUNT(a) FROM Asset a "
                    + "JOIN a.categories c "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "'")
    ,
    @NamedQuery(
        name = "Asset.filterByFolderAndTitle",
        query = "SELECT a FROM Asset a "
                    + "JOIN a.categories c "
                    + "JOIN a.title.values t "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND LOWER(t) LIKE CONCAT('%', LOWER(:title), '%')")
    ,
    @NamedQuery(
        name = "Asset.countFilterByFolderAndTitle",
        query = "SELECT COUNT(a) FROM Asset a "
                    + "JOIN a.categories c "
                    + "JOIN a.title.values t "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND LOWER(t) LIKE CONCAT('%', LOWER(:title), '%')")
    ,
    @NamedQuery(
        name = "Asset.filterByFolderAndType",
        query = "SELECT a FROM Asset a "
                    + "JOIN a.categories c "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND TYPE(a) = :type")
    ,
    @NamedQuery(
        name = "Asset.countFilterByFolderAndType",
        query = "SELECT COUNT(a) FROM Asset a "
                    + "JOIN a.categories c "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND TYPE(a) = :type")
    ,
    @NamedQuery(
        name = "Asset.filterByFolderAndTitleAndType",
        query = "SELECT a FROM Asset a "
                    + "JOIN a.title.values t "
                    + "JOIN a.categories c "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND LOWER(t) LIKE CONCAT('%', LOWER(:title), '%') "
                    + "AND TYPE(a) = :type")
    ,
    @NamedQuery(
        name = "Asset.countFilterByFolderAndTitleAndType",
        query = "SELECT COUNT(a) FROM Asset a "
                    + "JOIN a.categories c "
                    + "JOIN a.title.values t "
                    + "WHERE c.category = :folder "
                    + "AND c.type = '" + CATEGORIZATION_TYPE_FOLDER + "' "
                    + "AND LOWER(t) LIKE CONCAT('%', LOWER(:title), '%') "
                    + "AND TYPE(a) = :type")
})
public class Asset extends CcmObject {

    private static final long serialVersionUID = -3499741368562653529L;

    @OneToMany(mappedBy = "asset")
    private List<ItemAttachment<?>> itemAttachments;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "ASSET_TITLES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "ASSET_ID")
                               }
        )
    )
    private LocalizedString title;

    public Asset() {
        title = new LocalizedString();
        itemAttachments = new ArrayList<>();
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        this.title = title;
    }

    public List<ItemAttachment<?>> getItemAttachments() {
        if (itemAttachments == null) {
            return new ArrayList<>();
        } else {
            return Collections.unmodifiableList(itemAttachments);
        }
    }

    protected void setItemAttachments(
        final List<ItemAttachment<?>> itemAttachments) {

        if (itemAttachments == null) {
            this.itemAttachments = new ArrayList<>();
        } else {
            this.itemAttachments = itemAttachments;
        }
    }

    protected void addItemAttachment(final ItemAttachment<?> itemAttachment) {
        itemAttachments.add(itemAttachment);
    }

    protected void removeItemAttachment(final ItemAttachment<?> itemAttachment) {
        itemAttachments.remove(itemAttachment);
    }

    private Optional<CcmObject> getFolder() {
        final Optional<Categorization> result = getCategories()
            .stream()
            .filter(categorization -> CmsConstants.CATEGORIZATION_TYPE_FOLDER.
            equals(categorization.getType()))
            .findFirst();

        if (result.isPresent()) {
            return Optional.of(result.get().getCategory());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(title);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Asset)) {
            return false;
        }
        final Asset other = (Asset) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        return Objects.equals(title, other.getTitle());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Asset;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", title = \"%s\"%s",
                                            title,
                                            data));
    }

}
