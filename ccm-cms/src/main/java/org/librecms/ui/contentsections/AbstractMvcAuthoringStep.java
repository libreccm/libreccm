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
package org.librecms.ui.contentsections;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentSection;
import org.librecms.ui.contentsections.documents.MvcAuthoringStep;

import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.mvc.Models;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractMvcAuthoringStep implements MvcAuthoringStep {

    @Inject
    private ContentItemManager itemManager;
    
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private Models models;

    private ContentSection section;

    private ContentItem document;

    @Override
    public ContentSection getContentSection() {
        return section;
    }

    @Override
    public void setContentSection(final ContentSection section) {
        this.section = section;
    }

    @Override
    public String getContentSectionLabel() {
        return section.getLabel();
    }

    @Override
    public String getContentSectionTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(section.getTitle());
    }

    @Override
    public ContentItem getContentItem() {
        return document;
    }

    @Override
    public void setContentItem(final ContentItem document) {
        this.document = document;
    }
    
     @Override
    public String getContentItemPath() {
        return itemManager.getItemPath(document);
    }

    @Override
    public String getContentItemTitle() {
        return globalizationHelper
            .getValueFromLocalizedString(document.getTitle());
    }

    protected boolean hasParameter(
        final Map<String, String[]> parameters,
        final String parameterName
    ) {
        Objects.requireNonNull(
            parameters,
            "parameters can't be null."
        );
        Objects.requireNonNull(
            parameterName,
            "parameterName can't be null."
        );
        return parameters.containsKey(parameterName)
                   && parameters.get(parameterName) != null
                   && parameters.get(parameterName).length != 0;
    }

    /**
     * Helper method to add a form parameter value to {@link #models}.
     *
     * @param parameters    The form parameters.
     * @param parameterName The parameter name
     */
    protected void addParameterValueToModels(
        final Map<String, String[]> parameters,
        final String parameterName
    ) {
        models.put(
            Objects.requireNonNull(
                parameterName,
                "parameterName can't be null"
            ),
            Objects.requireNonNull(
                parameters,
                "parameters can't be null."
            ).getOrDefault(parameterName, new String[]{""})[0]
        );
    }

}
