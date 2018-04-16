/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.webdav;

import java.util.Collection;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * Guarantees that any unmarshalled enum constants effectively are the constant
 * Java instances itself, so that {@code ==} can be used for comparison.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 * Subclass must provide the constants to be used instead of an equal value by
 * {@link #getConstants()}.
 *
 * @author unknown
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class ConstantsAdapter<T> extends XmlAdapter<T, T> {

    @Override
    public T unmarshal(final T value) throws Exception {

        return value;

    }

    @Override
    public T marshal(final T value) throws Exception {

        return replaceValueByConstants(value, this.getConstants());
    }

    private static <T> T replaceValueByConstants(
        final T value, final Collection<T> constants) {
        
        return constants
            .stream()
            .filter(constant -> constant.equals(value))
            .findAny()
            .orElse(value);
        
    }

    /**
     * @return Constant instances to be returned by {@link #marshal(Object)} as
     *         a replacement for any equal instances. Must not be {@code null}.
     */
    protected abstract Collection<T> getConstants();

}

