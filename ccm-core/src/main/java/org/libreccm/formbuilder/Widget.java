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

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "formbuilder_widgets")
public class Widget extends Component implements Serializable {

    private static final long serialVersionUID = 1057792450655098288L;

    @Column(name = "parameter_name")
    private String parameterName;

    @Column(name = "parameter_model")
    private String parameterModel;

    @Column(name = "default_value")
    private String defaultValue;

    @OneToOne
    private WidgetLabel label;

    @OneToMany(mappedBy = "widget")
    private List<Listener> listeners;

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(final String parameterName) {
        this.parameterName = parameterName;
    }

    public String getParameterModel() {
        return parameterModel;
    }

    public void setParameterModel(final String parameterModel) {
        this.parameterModel = parameterModel;
    }

    public String getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(final String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public WidgetLabel getLabel() {
        return label;
    }

    protected void setLabel(final WidgetLabel label) {
        this.label = label;
    }

    public List<Listener> getListeners() {
        if (listeners == null) {
            return null;
        } else {
            return Collections.unmodifiableList(listeners);
        }
    }

    protected void setListeners(final List<Listener> listeners) {
        this.listeners = listeners;
    }

    protected void addListener(final Listener listener) {
        listeners.add(listener);
    }

    protected void removeListener(final Listener listener) {
        listeners.remove(listener);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 17 * hash + Objects.hashCode(parameterName);
        hash = 17 * hash + Objects.hashCode(parameterModel);
        hash = 17 * hash + Objects.hashCode(defaultValue);
        hash = 17 * hash + Objects.hashCode(label);
        hash = 17 * hash + Objects.hashCode(listeners);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Widget)) {
            return false;
        }
        final Widget other = (Widget) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(parameterName, other.getParameterName())) {
            return false;
        }
        if (!Objects.equals(parameterModel, other.getParameterModel())) {
            return false;
        }
        if (!Objects.equals(defaultValue, other.getDefaultValue())) {
            return false;
        }
        if (!Objects.equals(label, other.getLabel())) {
            return false;
        }

        return Objects.equals(listeners, other.getListeners());
    }
    
    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Widget;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", parameterName = \"%s\", "
                                                + "parameterModel = \"%s\", "
                                                + "defaultValue = \"%s\"%s",
                                            parameterName,
                                            parameterModel,
                                            defaultValue,
                                            data));
    }

}
