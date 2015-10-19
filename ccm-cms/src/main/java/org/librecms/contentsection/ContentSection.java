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

import static org.librecms.CmsConstants.*;

import org.libreccm.core.Group;
import org.libreccm.web.CcmApplication;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "content_sections", schema = DB_SCHEMA)
public class ContentSection extends CcmApplication implements Serializable {

    private static final long serialVersionUID = -671718122153931727L;

    @Column(name = "label", length = 512)
    private String label;

    @Column(name = "page_resolver_class", length = 1024)
    private String pageResolverClass;

    @Column(name = "item_resolver_class", length = 1024)
    private String itemResolverClass;

    @Column(name = "template_resolver_class", length = 1024)
    private String templateResolverClass;

    @Column(name = "xml_generator_class", length = 1024)
    private String xmlGeneratorClass;

    @OneToOne
    private Group staffGroup;

    @OneToOne
    private Group viewersGroup;

    @Column(name = "default_locale", length = 10)
    private String defaultLocale;
    
    

    public String getLabel() {
        return label;
    }

    public void setLabel(final String label) {
        this.label = label;
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

    public Group getStaffGroup() {
        return staffGroup;
    }

    public void setStaffGroup(final Group staffGroup) {
        this.staffGroup = staffGroup;
    }

    public Group getViewersGroup() {
        return viewersGroup;
    }

    public void setViewersGroup(final Group viewersGroup) {
        this.viewersGroup = viewersGroup;
    }

    public String getDefaultLocale() {
        return defaultLocale;
    }

    public void setDefaultLocale(final String defaultLocale) {
        this.defaultLocale = defaultLocale;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash + Objects.hashCode(label);
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
        return super.toString(String.format(", label = \"%s\", "
                                                + "pageResolverClass = \"%s\", "
                                                + "itemResolverClass = \"%s\", "
                                                + "templateResolverClass = \"%s\", "
                                            + "xmlGeneratorClass = \"%s\", "
                                                + "defaultLocale = \"%s\"%s",
                                            label,
                                            pageResolverClass,
                                            itemResolverClass,
                                            templateResolverClass,
                                            xmlGeneratorClass,
                                            defaultLocale,
                                            data));
    }

}
