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
package org.librecms.contenttypes;

import com.arsdigita.bebop.Component;

import java.util.Objects;

/**
 * Encapsulates the information about an authoring step.
 *
 * @see AuthoringStep
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AuthoringStepInfo {

    /**
     * The bundle which provides the label for the authoring step.
     */
    private String labelBundle;
    /**
     * The key of label for the authoring step in the {@link #labelBundle}
     */
    private String labelKey;
    /**
     * The bundle which provides the description for the authoring step.
     */
    private String descriptionBundle;
    /**
     * The key of the description for the authoring step in the
     * {@link #descriptionBundle}.
     */
    private String descriptionKey;

    private int order;
    private Class<? extends Component> component;

    protected AuthoringStepInfo() {
        super();
    }

    public String getLabelBundle() {
        return labelBundle;
    }

    public void setLabelBundle(final String labelBundle) {
        this.labelBundle = labelBundle;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(final String labelKey) {
        this.labelKey = labelKey;
    }

    public String getDescriptionBundle() {
        return descriptionBundle;
    }

    public void setDescriptionBundle(final String descriptionBundle) {
        this.descriptionBundle = descriptionBundle;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(final String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(final int order) {
        this.order = order;
    }

    public Class<? extends Component> getComponent() {
        return component;
    }

    public void setComponent(final Class<? extends Component> component) {
        this.component = component;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(labelBundle);
        hash = 53 * hash + Objects.hashCode(labelKey);
        hash = 53 * hash + Objects.hashCode(descriptionBundle);
        hash = 53 * hash + Objects.hashCode(descriptionKey);
        hash = 53 * hash + Objects.hashCode(order);
        hash = 53 * hash + Objects.hashCode(component);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AuthoringStepInfo)) {
            return false;
        }
        final AuthoringStepInfo other = (AuthoringStepInfo) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(this.labelBundle, other.getLabelBundle())) {
            return false;
        }
        if (!Objects.equals(this.labelKey, other.getLabelKey())) {
            return false;
        }
        if (!Objects.equals(this.descriptionBundle,
                            other.getDescriptionBundle())) {
            return false;
        }
        if (!Objects.equals(this.descriptionKey, other.getDescriptionKey())) {
            return false;
        }

        if (order != other.getOrder()) {
            return false;
        }

        return Objects.equals(this.component, other.getComponent());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof AuthoringStepInfo;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "labelBundle = \"%s\","
                                 + "labelKey = \"%s\", "
                                 + "descriptionBundle = \"%s\","
                                 + "descriptionKey = \"%s\","
                                 + "order = %d, "
                                 + "component = \"%s\"%s }",
                             super.toString(),
                             labelBundle,
                             labelKey,
                             descriptionKey,
                             descriptionBundle,
                             order,
                             Objects.toString(component),
                             data);
    }

}
