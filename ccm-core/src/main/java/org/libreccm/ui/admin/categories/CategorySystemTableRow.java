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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorySystemTableRow implements
    Comparable<CategorySystemTableRow> {

    private long domainId;

    private String domainKey;

    private String uri;

    private Map<String, String> title;

    private String version;

    private String released;

    public long getDomainId() {
        return domainId;
    }

    void setDomainId(final long domainId) {
        this.domainId = domainId;
    }

    public String getIdentifier() {
        return String.format("ID-%d", domainId);
    }

    public String getDomainKey() {
        return domainKey;
    }

    void setDomainKey(final String domainKey) {
        this.domainKey = domainKey;
    }

    public String getUri() {
        return uri;
    }

    void setUri(final String uri) {
        this.uri = uri;
    }

    public Map<String, String> getTitle() {
        return Collections.unmodifiableMap(title);
    }

    void setTitle(final Map<String, String> title) {
        this.title = new HashMap<>(title);
    }

    public String getVersion() {
        return version;
    }

    void setVersion(final String version) {
        this.version = version;
    }

    public String getReleased() {
        return released;
    }

    void setReleased(final String released) {
        this.released = released;
    }

    @Override
    public int compareTo(final CategorySystemTableRow other) {
        int result;
        result = Objects.compare(
            domainKey, other.getDomainKey(), String::compareTo
        );

        if (result == 0) {
            result = Objects.compare(uri, uri, String::compareTo);
        }

        if (result == 0) {
            result = Objects.compare(
                domainId, other.getDomainId(), Long::compare
            );
        }

        return result;
    }

}
