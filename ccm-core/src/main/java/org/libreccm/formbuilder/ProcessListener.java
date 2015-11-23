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
package org.libreccm.formbuilder;

import org.libreccm.core.CcmObject;

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FORMBUILDER_PROCESS_LISTENERS", schema = DB_SCHEMA)
public class ProcessListener extends CcmObject implements Serializable {

    private static final long serialVersionUID = -3029184333026605708L;

    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "FORMBUILDER_PROCESS_LISTENER_NAMES",
            schema = DB_SCHEMA,
            joinColumns = {
                @JoinColumn(name = "PROCESS_LISTENER_ID")}))
    private LocalizedString name;

    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(
            name = "FORMBUILDER_PROCESS_LISTENER_DESCRIPTIONS",
            schema = DB_SCHEMA,
            joinColumns = {
                @JoinColumn(name = "PROCESS_LISTENER_ID")}))

    private LocalizedString description;

    @Column(name = "LISTENER_CLASS")
    private String listenerClass;

    @ManyToOne
    private FormSection formSection;

    @Column(name = "PROCESS_LISTENER_ORDER")
    private long order;

    public LocalizedString getName() {
        return name;
    }

    public void setName(final LocalizedString name) {
        this.name = name;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(final LocalizedString description) {
        this.description = description;
    }

    public String getListenerClass() {
        return listenerClass;
    }

    public void setListenerClass(final String listenerClass) {
        this.listenerClass = listenerClass;
    }

    public FormSection getFormSection() {
        return formSection;
    }

    protected void setFormSection(final FormSection formSection) {
        this.formSection = formSection;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(final long order) {
        this.order = order;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + Objects.hashCode(name);
        hash = 37 * hash + Objects.hashCode(description);
        hash = 37 * hash + Objects.hashCode(listenerClass);
        hash = 37 * hash + Objects.hashCode(formSection);
        hash = 37 * hash + (int) (order ^ (order >>> 32));
        return hash;
    }

    @Override
    //We can't reduce the complexity now
    @SuppressWarnings("PMD.NPathComplexity")
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ProcessListener)) {
            return false;
        }
        final ProcessListener other = (ProcessListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(name, other.getName())) {
            return false;
        }
        if (!Objects.equals(description, other.getDescription())) {
            return false;
        }
        if (!Objects.equals(listenerClass, other.getListenerClass())) {
            return false;
        }
        if (!Objects.equals(formSection, other.getFormSection())) {
            return false;
        }
        return order == other.getOrder();
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ProcessListener;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", name = %s, "
                                                + "description = %s, "
                                                + "listenerClass = \"%s\", "
                                                + "formSection = %s, "
                                                + "order = %d%s",
                                            Objects.toString(name),
                                            Objects.toString(description),
                                            listenerClass,
                                            Objects.toString(formSection),
                                            order,
                                            data));
    }

}
