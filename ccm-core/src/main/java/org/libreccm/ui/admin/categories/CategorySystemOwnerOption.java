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
package org.libreccm.ui.admin.categories;

import org.libreccm.web.CcmApplication;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorySystemOwnerOption
    implements Comparable<CategorySystemOwnerOption> {

    private final long applicationId;

    private final String applicationUuid;

    private final String applicationName;

    public CategorySystemOwnerOption(final CcmApplication application) {
        applicationId = application.getObjectId();
        applicationUuid = application.getUuid();
        if (application.getDisplayName() == null) {
            applicationName = application.getApplicationType();
        } else {
            applicationName = application.getDisplayName();
        }
    }

    public long getApplicationId() {
        return applicationId;
    }

    public String getApplicationUuid() {
        return applicationUuid;
    }

    public String getApplicationName() {
        return applicationName;
    }

    @Override
    public int compareTo(final CategorySystemOwnerOption other) {
        return Objects.compare(
            applicationName,
            Objects.requireNonNull(other).getApplicationName(),
            String::compareTo
        );
    }

}
