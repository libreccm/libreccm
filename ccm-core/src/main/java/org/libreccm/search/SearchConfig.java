/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.search;

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.Setting;

import java.util.Objects;

/**
 * Configuration for Hibernate Search. Some of the options in this configuration
 * are directly applied to Hibernate search. Please refer to the Hibernate
 * Search documentation for details and valid values.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class SearchConfig {

    public static final String DIRECTORY_PROVIDER = "directoryProvider";
    public static final String INDEX_BASE = "indexBase";
    
    @Setting
    private String directoryProvider;

    @Setting
    private String indexBase;

    public String getDirectoryProvider() {
        return directoryProvider;
    }

    public void setDirectoryProvider(final String directoryProvider) {
        this.directoryProvider = directoryProvider;
    }

    public String getIndexBase() {
        return indexBase;
    }

    public void setIndexBase(final String indexBase) {
        this.indexBase = indexBase;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 83 * hash + Objects.hashCode(directoryProvider);
        hash = 83 * hash + Objects.hashCode(indexBase);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof SearchConfig)) {
            return false;
        }
        final SearchConfig other = (SearchConfig) obj;
        if (!Objects.equals(this.directoryProvider,
                            other.getDirectoryProvider())) {
            return false;
        }
        return Objects.equals(this.indexBase, other.getIndexBase());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "directoryProvider = \"%s\", "
                                 + "indexBase = \"%s\""
                                 + " }",
                             super.toString(),
                             directoryProvider,
                             indexBase);
    }

}
