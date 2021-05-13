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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Metadata of an authoring step for documents (content items).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface MvcAuthoringStepDef {

    /**
     * The name of the resource bundle providing the localized values for
     * {@link #labelKey} and {@link descriptionKey}.
     *
     * @return The resource bundle providing the localized labelKey and
     *         descriptionKey.
     */
    String bundle();

    /**
     * The key for the localized description of the step.
     *
     * @return The key for the localized description of the step.
     */
    String descriptionKey();

    /**
     * The key for the localized label of the authoring step..
     *
     * @return The key for the localized label of the authoring step...
     */
    String labelKey();

    /**
     * Authoring steps only support a specific type, and all subtypes.
     *
     * @return The document type supported by the authoring step.
     */
    Class<? extends ContentItem> supportedDocumentType();

}
