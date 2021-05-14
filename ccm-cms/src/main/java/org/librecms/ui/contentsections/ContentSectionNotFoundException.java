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

/**
 * Used by the {@link MvcAuthoringStepService} to indicate that the requested
 * content section could not be found. The {@link MvcAuthoringStepService} has
 * already populated {@link Models} with all necessary information. To show the
 * error message the controller can simply return the string returned by
 * {@link #showErrorMessage()}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSectionNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    private final String errorMessageTemplate;

    /**
     * Creates a new instance of <code>ContentSectionNotFound</code> without
     * detail message.
     *
     * @param errorMessageTemplate Template for the error message.
     */
    public ContentSectionNotFoundException(final String errorMessageTemplate) {
        super();
        this.errorMessageTemplate = errorMessageTemplate;
    }

    /**
     * Constructs an instance of <code>ContentSectionNotFound</code> with the
     * specified detail message.
     *
     * @param msg                  The detail message.
     * @param errorMessageTemplate Template for the error message.
     */
    public ContentSectionNotFoundException(
        final String errorMessageTemplate, final String msg
    ) {
        super(msg);
        this.errorMessageTemplate = errorMessageTemplate;
    }

    public String showErrorMessage() {
        return errorMessageTemplate;
    }

}
