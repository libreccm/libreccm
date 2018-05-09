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
package org.libreccm.webdav.xml.properties;

import org.libreccm.webdav.ConstantsAdapter;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

/**
 * WebDAV getcontentlanguage Property.
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @see
 * <a href="http://www.webdav.org/specs/rfc4918.html#PROPERTY_getcontentlanguage">Chapter
 * 15.3 "getcontentlanguage Property" of RFC 4918 "HTTP Extensions for Web
 * Distributed Authoring and Versioning (WebDAV)"</a>
 *
 */
@XmlJavaTypeAdapter(GetContentLanguage.Adapter.class)
@XmlRootElement(name = "getcontentlanguage")
public final class GetContentLanguage {

    public static final GetContentLanguage GETCONTENTLANGUAGE
                                               = new GetContentLanguage();

    @XmlValue
    private final String languageTag;

    private GetContentLanguage() {
        this.languageTag = "";
    }

    public GetContentLanguage(final String languageTag) {
        this.languageTag = Objects.requireNonNull(languageTag);
    }

    public final String getLanguageTag() {
        return this.languageTag;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 67 * hash + Objects.hashCode(languageTag);
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
        if (!(obj instanceof GetContentLanguage)) {
            return false;
        }
        final GetContentLanguage other = (GetContentLanguage) obj;
        return Objects.equals(languageTag, other.getLanguageTag());
    }

    @Override
    public String toString() {
        return String.format("%s{ languageTag = \"%s\" }",
                             languageTag);
    }

    /**
     * Guarantees that any unmarshalled enum constants effectively are the
     * constant Java instances itself, so that {@code ==} can be used form
     * comparison.
     *
     */
    protected static final class Adapter
        extends ConstantsAdapter<GetContentLanguage> {

        @Override
        protected final Collection<GetContentLanguage> getConstants() {
            return Collections.singleton(GETCONTENTLANGUAGE);
        }

    }

}
