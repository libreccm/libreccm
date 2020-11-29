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
package org.libreccm.ui.admin;

import java.util.Comparator;

/**
 * Model for the data of an admin page.
 *
 * @see AdminPage
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AdminPageModel implements Comparable<AdminPageModel> {

    private String uriIdentifier;

    private String label;

    private String description;

    private String icon;

    private long position;

    public String getUriIdentifier() {
        return uriIdentifier;
    }

    protected void setUriIdentifier(final String uriIdentifier) {
        this.uriIdentifier = uriIdentifier;
    }

    public String getLabel() {
        return label;
    }

    protected void setLabel(final String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(final String description) {
        this.description = description;
    }

    public String getIcon() {
        return icon;
    }

    protected void setIcon(final String icon) {
        this.icon = icon;
    }

    public long getPosition() {
        return position;
    }

    protected void setPosition(final long position) {
        this.position = position;
    }

    @Override
    public int compareTo(final AdminPageModel other) {
        return Comparator
            .nullsFirst(
                Comparator
                    .comparing(AdminPageModel::getPosition)
                    .thenComparing(AdminPageModel::getLabel)
            ).compare(this, other);
    }

}
