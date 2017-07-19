/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.ui.authoring;

import com.arsdigita.bebop.Component;

import java.util.Objects;

/**
 * Information about a authoring step which is independent from the type of the
 * content item.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentItemAuthoringStepInfo {

    private Class<? extends Component> step;

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

    public Class<? extends Component> getStep() {
        return step;
    }

    public void setStep(Class<? extends Component> step) {
        this.step = step;
    }

    public String getLabelBundle() {
        return labelBundle;
    }

    public void setLabelBundle(String labelBundle) {
        this.labelBundle = labelBundle;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }

    public String getDescriptionBundle() {
        return descriptionBundle;
    }

    public void setDescriptionBundle(String descriptionBundle) {
        this.descriptionBundle = descriptionBundle;
    }

    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(String descriptionKey) {
        this.descriptionKey = descriptionKey;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(step);
        hash = 53 * hash + Objects.hashCode(labelBundle);
        hash = 53 * hash + Objects.hashCode(labelKey);
        hash = 53 * hash + Objects.hashCode(descriptionBundle);
        hash = 53 * hash + Objects.hashCode(descriptionKey);
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
        if (!(obj instanceof ContentItemAuthoringStepInfo)) {
            return false;
        }
        final ContentItemAuthoringStepInfo other
                                               = (ContentItemAuthoringStepInfo) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(labelBundle, other.getLabelBundle())) {
            return false;
        }
        if (!Objects.equals(labelKey, other.getLabelKey())) {
            return false;
        }
        if (!Objects.equals(descriptionBundle, other.getDescriptionBundle())) {
            return false;
        }
        if (!Objects.equals(descriptionKey, other.getDescriptionKey())) {
            return false;
        }
        return Objects.equals(step, other.getStep());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof ContentItemAuthoringStep;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "labelBundle = \"%s\", "
                                 + "labelKey = \"%s\", "
                                 + "descriptionBundle = \"%s\", "
                                 + "descriptionKey = \"%s\","
                                 + "step = \"%s\"%s }",
                             super.toString(),
                             labelBundle,
                             labelKey,
                             descriptionBundle,
                             descriptionKey,
                             Objects.toString(step),
                             data);
    }

}
