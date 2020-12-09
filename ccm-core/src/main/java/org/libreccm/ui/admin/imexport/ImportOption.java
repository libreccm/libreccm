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
package org.libreccm.ui.admin.imexport;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImportOption implements Comparable<ImportOption> {

    private final String importName;

    private final String label;

    public ImportOption(
        final String importName,
        final String label
    ) {
        this.importName = importName;
        this.label = label;
    }

    public String getImportName() {
        return importName;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public int compareTo(final ImportOption other) {
        return importName.compareTo(
            Objects.requireNonNull(other).getImportName()
        );
    }

}
