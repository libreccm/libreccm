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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Provides the steps for creating and viewing and editing a document (content
 * item). The classes provided for {@link #createStep()} and 
 * {@link #authoringSteps() } provide information about the steps.
 *
 * This annotation can only be used on classes extending the {@link ContentItem}
 * class.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MvcAuthoringKit {

    /**
     * Controller of the create step for a document type.
     *
     * @return Descriptor class for the create step.
     */
    Class<? extends MvcDocumentCreateStep<?>> createStep();

    /**
     * The authoring steps for editing the properties of the document. They are
     * used in the same order as they are provided here.
     *
     * @return The authoring steps for the annotated document type.
     */
    Class<?>[] authoringSteps();

    /**
     * If set to {@code true} some authoring steps like categorization or
     * related info are not shown. If set to {@code true} some of these steps
     * can still be added by adding them to {@link #authoringSteps() }.
     *
     * @return {@code true} if the default steps should be excluded.
     */
    boolean excludeDefaultAuthoringSteps() default false;

}
