/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.xml.formatters;

import com.arsdigita.xml.Formatter;
import java.util.Locale;
import java.util.Date;
import java.text.DateFormat;

/**
 * An alternate formatter for java.util.Date objects, outputting the date in
 * 'medium' format. The time is omitted.
 *
 * @author unknown
 * @author Sören Bernstein <quasi@quasiweb.de>
 */
public class DateFormatter implements Formatter {

    private static DateFormatterConfig m_config;

    public static final DateFormatterConfig getConfig() {
        if (m_config == null) {
            m_config = new DateFormatterConfig();
            m_config.load();
        }
        return m_config;
    }

    @Override
    public String format(Object value) {
        Date date = (Date) value;

        //Locale locale = GlobalizationHelper.getNegotiatedLocale();
        final Locale locale = Locale.getDefault();

        DateFormat format = DateFormat
            .getDateInstance(DateFormat.MEDIUM, locale);

        return format.format(date);
    }

}
