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
package org.libreccm.webdav.xml.elements;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * /**
 * RFC 3339 date-time format
 *
 * The class is based on a class/interface from java.net WebDAV Project:
 *
 * <a href="https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java">https://gitlab.com/headcrashing/webdav-jaxrs/blob/master/src/main/java/net/java/dev/webdav/jaxrs/Headers.java</a>
 *
 *
 * This class formats and parses dates using the ISO 8601 compliant pattern
 * [YYYY]-[MM]-[DD]T[hh]:[mm]:[ss].([fff])[OFS]. Parsing and formatting are
 * handled in different ways to provide at-most stability and compatibility.
 * While formatting always will produce a String in full UTC notation
 * (containing time offset literal 'Z' and fraction of seconds), parsing is
 * flexible and can handle not only optional fraction of seconds, but also
 * numeric time offsets in addition to the time offset literal 'Z'.
 *
 * @author Markus KARG (mkarg@java.net)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public final class Rfc3339DateTimeFormat extends SimpleDateFormat {

    private static final long serialVersionUID = 5892844197992067945L;

    private static final String DEFAULT_PATTERN = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public Rfc3339DateTimeFormat() {
        super(DEFAULT_PATTERN, Locale.US);
        this.calendar = Calendar
            .getInstance(TimeZone.getTimeZone("UTC"));
    }

    @Override
    public final Date parse(final String text) throws ParseException {
        final String patchedText = this.ignoreTimeOffsetColon(text);

        final String pattern = this.buildPatternVariant(patchedText);

        if (!pattern.equals(DEFAULT_PATTERN)) {
            this.applyPattern(pattern);
        }

        return super.parse(patchedText);
    }

    private String buildPatternVariant(final String text) {

        final StringBuilder pattern = new StringBuilder("yyyy-MM-dd'T'HH:mm:ss");
        this.addFractionToPattern(pattern, text);
        this.addTimeOffsetToPattern(pattern, text);
        return pattern.toString();
    }

    private void addFractionToPattern(final StringBuilder pattern,
                                      final String text) {

        if (text.lastIndexOf('.') != -1) {
            pattern.append(".SSS");
        }
    }

    private void addTimeOffsetToPattern(final StringBuilder pattern,
                                        final String text) {

        final boolean appendAsLiteral = text.charAt(text.length() - 1) == 'Z';

        if (appendAsLiteral) {
            pattern.append('\'');
        }

        pattern.append('Z');

        if (appendAsLiteral) {
            pattern.append('\'');
        }
    }

    private String ignoreTimeOffsetColon(final String text)
        throws ParseException {
        
        if (text == null || text.isEmpty()) {
            throw new ParseException("Cannot parse empty text", 0);
        }

        if (text.charAt(text.length() - 1) == 'Z') {
            return text;
        }

        if (text.charAt(text.length() - 3) != ':') {
            throw new ParseException(
                "According to RFC 3339 time offset must contain colon separator.",
                text.length() - 2);
        }

        return new StringBuilder(text).deleteCharAt(text.length() - 3)
            .toString();
    }

}
