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

import org.libreccm.categorization.Category;
import org.libreccm.security.Role;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.Locale;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "CONTENT_SECTIONS", schema = DB_SCHEMA)
public class ContentSection extends CcmApplication implements Serializable {

    private static final long serialVersionUID = -671718122153931727L;

    @Column(name = "LABEL", length = 512)
    private String label;

    @OneToOne
    @JoinColumn(name = "ROOT_DOCUMENTS_FOLDER_ID")
    private Category rootDocumentsFolder;

    @OneToOne
    @JoinColumn(name = "ROOT_ASSETS_FOLDER_ID")
    private Category rootAssetsFolder;

    @Column(name = "PAGE_RESOLVER_CLASS", length = 1024)
    private String pageResolverClass;

    @Column(name = "ITEM_RESOLVER_CLASS", length = 1024)
    private String itemResolverClass;

    @Column(name = "TEMPLATE_RESOLVER_CLASS", length = 1024)
    private String templateResolverClass;

    @Column(name = "XML_GENERATOR_CLASS", length = 1024)
    private String xmlGeneratorClass;

    @OneToOne
    @JoinColumn(name = "STAFF_ROLE_ID")
    private Role staffRole;

    @OneToOne
    @JoinColumn(name = "VIEWERS_ROLE_ID")
    private Role viewersRole;

    @Column(name = "DEFAULT_LOCALE")
    private Locale defaultLocale;

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
    }

    public Category getRootDocumentsFolder() {
        return rootDocumentsFolder;
    }

    protected void setRootDocumentFolder(final Category rootDocumentsFolder) {
        this.rootDocumentsFolder = rootDocumentsFolder;
    }

    public Category getRootAssetsFolder() {
        return rootAssetsFolder;
    }

    protected void setRootAssetsFolder(final Category rootAssetsFolder) {
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

    public Role getStaffRole() {
        return staffRole;
    }

    public void setStaffRole(final Role staffRole) {
        this.staffRole = staffRole;
    }

    public Role getViewersRole() {
        return viewersRole;
    }

    public void setViewersRole(final Role viewersRole) {
        this.viewersRole = viewersRole;
    }

    public Locale getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(final Locale defaultLocale) {
        this.defaultLocale = defaultLocale;
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
