/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.core.api;

import java.util.Objects;

import javax.enterprise.context.Dependent;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class IdentifierExtractor {

    public ExtractedIdentifier extractIdentifier(final String identifierParam) {
        Objects.requireNonNull(identifierParam, "identifier param is null.");

        if (identifierParam.startsWith(ApiConstants.IDENTIFIER_PREFIX_ID)) {
            final String identifier = identifierParam
                .substring(ApiConstants.IDENTIFIER_PREFIX_ID.length());
            return new ExtractedIdentifier(IdentifierType.ID, identifier);
        } else if (identifierParam.startsWith(
            ApiConstants.IDENTIFIER_PREFIX_UUID)) {
            final String identifier = identifierParam
                .substring(ApiConstants.IDENTIFIER_PREFIX_UUID.length());
            return new ExtractedIdentifier(IdentifierType.ID, identifier);
        } else {
            return new ExtractedIdentifier(
                IdentifierType.PROPERTY, identifierParam
            );
        }
    }

}
