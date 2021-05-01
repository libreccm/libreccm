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

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;

import javax.ws.rs.Path;

/**
 * Base interface for authoring steps. For buidling authoring steps it is
 * recommanned to use the {@link AbstractMvcAuthoringStep} as base that
 * implements most of the methods defined by this interface.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface MvcAuthoringStep {

    Class<? extends MvcAuthoringStep> getStepClass();

    ContentSection getContentSection() throws ContentSectionNotFoundException;

    ContentItem getDocument() throws ContentSectionNotFoundException,
                                     DocumentNotFoundException;

    String getDocumentPath() throws ContentSectionNotFoundException,
                                    DocumentNotFoundException;

    /**
     * Gets the label for an authoring step.
     *
     * @return The label for the authoring step. If the implementing class is
     *         not annotated with {@link MvcAuthoringStepDef} the string
     *         {@code ???} is returned.
     */
    String getLabel();

    /**
     * Gets the description for an authoring step.
     *
     * @return The label for the authoring step. If the implementing class is
     *         not annotated with {@link MvcAuthoringStepDef} an empty stringis
     *         returned.
     */
    String getDescription();

    /**
     * If an authoring step alters the name of the content item and therefore
     * the path of the item, the step MUST call this method to update the
     * document path used by the step.
     *
     * @throws
     * org.librecms.ui.contentsections.documents.ContentSectionNotFoundException
     * @throws
     * org.librecms.ui.contentsections.documents.DocumentNotFoundException
     */
    void updateDocumentPath() throws ContentSectionNotFoundException,
                                     DocumentNotFoundException;

    /**
     * Builds the redirect path of the authoring step.This path is most often
     * used to implement the redirect after post pattern.
     *
     * @return The redirect path. If the the implementing class is not annotated
     *         with {@link Path} an empty string is returned.
     *
     * @throws
     * org.librecms.ui.contentsections.documents.ContentSectionNotFoundException
     * @throws
     * org.librecms.ui.contentsections.documents.DocumentNotFoundException
     */
    String buildRedirectPathForStep() throws ContentSectionNotFoundException,
                                             DocumentNotFoundException;

    /**
     * Builds the redirect path of the authoring step.This path is most often
     * used to implement the redirect after post pattern.
     *
     * @param subPath additional path elements that are appended to the path of
     *                the authoring step.
     *
     * @return The redirect path. If the the implemeting class is not annotated
     *         with {@link Path} an empty string is returned.
     *
     * @throws
     * org.librecms.ui.contentsections.documents.ContentSectionNotFoundException
     * @throws
     * org.librecms.ui.contentsections.documents.DocumentNotFoundException
     */
    String buildRedirectPathForStep(final String subPath) throws
        ContentSectionNotFoundException, DocumentNotFoundException;

}
