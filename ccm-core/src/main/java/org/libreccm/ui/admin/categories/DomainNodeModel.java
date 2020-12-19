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

import org.libreccm.categorization.Domain;

/**
 * DTO with the data about a {@link Domain} shown in the UI.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DomainNodeModel {

    private long domainId;

    private String uuid;

    private String domainKey;

    public long getDomainId() {
        return domainId;
    }

    protected void setDomainId(final long domainId) {
        this.domainId = domainId;
    }

    public String getIdentifier() {
        return String.format("ID-%s", domainId);
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getDomainKey() {
        return domainKey;
    }

    protected void setDomainKey(final String domainKey) {
        this.domainKey = domainKey;
    }

}
