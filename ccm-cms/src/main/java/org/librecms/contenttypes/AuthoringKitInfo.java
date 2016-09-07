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

import com.arsdigita.cms.ui.item.ItemCreateForm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Encapsulates the informations about an authoring kit.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AuthoringKitInfo {

    /**
     * The create component (the form used to collect the mandatory data for 
     * the content type).
     */
    private Class<? extends ItemCreateForm> createComponent;

    /**
     * The authoring steps of the authoring kit.
     */
    private List<AuthoringStepInfo> authoringSteps;

    protected AuthoringKitInfo() {
        authoringSteps = new ArrayList<>();
    }

    public Class<? extends ItemCreateForm> getCreateComponent() {
        return createComponent;
    }

    public void setCreateComponent(
        final Class<? extends ItemCreateForm> createComponent) {

        this.createComponent = createComponent;
    }

    public List<AuthoringStepInfo> getAuthoringSteps() {
        return Collections.unmodifiableList(authoringSteps);
    }

    protected void setAuthoringSteps(
        final List<AuthoringStepInfo> authoringSteps) {
        this.authoringSteps = authoringSteps;
    }

    protected void addAuthoringStep(final AuthoringStepInfo authoringStep) {
        authoringSteps.add(authoringStep);
    }

    protected void removeAuthoringStep(final AuthoringStepInfo authoringStep) {
        authoringSteps.remove(authoringStep);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 59 * hash + Objects.hashCode(createComponent);
        hash = 59 * hash + Objects.hashCode(authoringSteps);
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
        if (!(obj instanceof AuthoringKitInfo)) {
            return false;
        }
        final AuthoringKitInfo other = (AuthoringKitInfo) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (!Objects.equals(this.createComponent, other.getCreateComponent())) {
            return false;
        }
        return Objects.equals(this.authoringSteps, other.getAuthoringSteps());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof AuthoringKitInfo;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "createComponent = \"%s\", "
                                 + "authoringSteps = { %s }%s"
                                 + " }",
                             super.toString(),
                             Objects.toString(createComponent),
                             Objects.toString(authoringSteps),
                             data);
    }

}
