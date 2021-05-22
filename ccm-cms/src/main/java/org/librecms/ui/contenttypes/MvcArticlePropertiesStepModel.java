/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contenttypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsArticlePropertiesStep")
public class MvcArticlePropertiesStepModel {

    private String name;

    private boolean canEdit;

    private Map<String, String> titleValues;

    private List<String> unusedTitleLocales;

    private Map<String, String> descriptionValues;

    private List<String> unusedDescriptionLocales;

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean getCanEdit() {
        return canEdit;
    }

    public void setCanEdit(final boolean canEdit) {
        this.canEdit = canEdit;
    }

    public Map<String, String> getTitleValues() {
        return Collections.unmodifiableMap(titleValues);
    }

    public void setTitleValues(final Map<String, String> titleValues) {
        this.titleValues = new HashMap<>(titleValues);
    }

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

    public void setUnusedTitleLocales(List<String> unusedTitleLocales) {
        this.unusedTitleLocales = new ArrayList<>(unusedTitleLocales);
    }

    public Map<String, String> getDescriptionValues() {
        return Collections.unmodifiableMap(descriptionValues);
    }

    public void setDescriptionValues(
        final Map<String, String> descriptionValues
    ) {
        this.descriptionValues = new HashMap<>(descriptionValues);
    }

    public List<String> getUnusedDescriptionLocales() {
        return Collections.unmodifiableList(unusedDescriptionLocales);
    }

    public void setUnusedDescriptionLocales(
        final List<String> unusedDescriptionLocales
    ) {
        this.unusedDescriptionLocales = new ArrayList<>(
            unusedDescriptionLocales
        );
    }

}
