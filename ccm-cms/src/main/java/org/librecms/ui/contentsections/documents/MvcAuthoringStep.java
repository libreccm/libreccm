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
package org.librecms.ui.contentsections.documents;

import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import java.util.Map;

import javax.inject.Named;

/**
 * An authoring step for a document (content item). Implementing classes are
 * used as subresources by {@link DocumentController#editDocument(java.lang.String, java.lang.String, java.lang.String)
 * }. An implementation must be a named CDI bean (annotated with {@link Named},
 * annotated with the {@link AuthoringStepPathFragment} qualifier annotation.
 *
 * An implementation may contain multiple subresource paths for for displaying
 * forms and apply changes from these forms.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface MvcAuthoringStep {

    /**
     * Authoring steps only support a specific type, and all subtypes.
     *
     * @return The document type supported by the authoring step.
     */
    Class<? extends ContentItem> supportedDocumentType();

    /**
     * Gets the localized label of the authoring step. The language variant to
     * return should be selected using the locale returned by
     * {@link GlobalizationHelper#getNegotiatedLocale()}.
     *
     * @return The localized label of the authoring step.
     */
    String getLabel();

    /**
     * Gets the localized description of the authoring step. The language
     * variant to return should be selected using the locale returned by
     * {@link GlobalizationHelper#getNegotiatedLocale()}.
     *
     * @return The localized description of the authoring step.
     */
    String getDescription();

    /**
     * Gets the name of the resource bundle providing the localized label and
     * description.
     *
     * @return The resource bundle providing the localized label and
     *         description.
     */
    String getBundle();

    /**
     * The current content section.
     *
     * @return The current content section.
     */
    ContentSection getContentSection();

    /**
     * Convinient method for getting the label of the current content section.
     *
     * @return The label of the current content section.
     */
    String getContentSectionLabel();

    /**
     * Convinient method for getting the title of the current content section.
     *
     * @return The title of the current content section for the current locale.
     */
    String getContentSectionTitle();

    /**
     * The current content section is provided by the
     * {@link DocumentController}.
     *
     * @param section The current content section.
     */
    void setContentSection(final ContentSection section);

    /**
     * The selected document/content item.
     *
     * @return The selected document/content item.
     */
    ContentItem getContentItem();

    /**
     * Gets the path of the selected content item.
     *
     * @return The path of the selected content item.
     */
    String getContentItemPath();

    /**
     * Gets the title of the selected content item.
     *
     * @return The title of the selected content item.
     */
    String getContentItemTitle();

    /**
     * The current document/content item is provided by the
     * {@link DocumentController}.
     *
     * @param document The document/content item to edit.
     */
    void setContentItem(ContentItem document);

    /**
     * Endpoint displaying the authoring step. This should not show the form,
     * only an overview. The actual form(s) are provided by endpoints added by
     * the implementation.
     *
     * @return The template of the edit step.
     */
    String showStep();

    /**
     * Apply changes from the from of the script.Authoring steps that have
     * multiple forms may choose to implement this method as any no-op method
     * that simply redirects to the template view (same as {@link #showStep()}.
     *
     * @param formParameters The form parameters submitted.
     *
     * @return The template of the view to show.
     */
    String applyEdits(Map<String, String[]> formParameters);

}
