/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.globalization.Globalized;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;

/**
 *
 * This class holds methods to support consistent formatting across the system.
 *
 * @version $Revision$ $Date$
 * @version $Id$
 */
public class FormatStandards implements Globalized {

    private static RequestLocal s_dateFormat = new RequestLocal();
    private static RequestLocal s_dateTimeFormat = new RequestLocal();

    private static void initialize(PageState ps) {
        Locale l = CdiUtil.createCdiUtil().findBean(GlobalizationHelper.class).
                getNegotiatedLocale();
        s_dateFormat.set(ps, DateFormat.getDateInstance(DATE_DISPLAY_FORMAT, l));
        s_dateTimeFormat.set(ps, DateFormat.getDateTimeInstance(
                             DATE_DISPLAY_FORMAT, TIME_DISPLAY_FORMAT, l));
    }

    /**
     * @return A globalized DateFormat instance for formatting the date only.
     * @see #getDateTimeFormat()
     */
    public static DateFormat getDateFormat() {
        PageState ps = PageState.getPageState();
        Object obj = s_dateFormat.get(ps);

        if (obj == null) {
            initialize(ps);
            obj = s_dateFormat.get(ps);
        }

        return (DateFormat) obj;
    }

    /**
     * @return A globalized DateFormat instance for formatting the date and
     * time.
     * @see #getDateFormat()
     */
    public static DateFormat getDateTimeFormat() {
        PageState ps = PageState.getPageState();
        Object obj = s_dateTimeFormat.get(ps);

        if (obj == null) {
            initialize(ps);
            obj = s_dateTimeFormat.get(ps);
        }

        return (DateFormat) obj;
    }

    /**
     * Formats a date value according to formatting standards and localization.
     * In English This will show the date as "Mmm DD, YYYY" or "Jan 23, 2002."
     * This method discards the clock time.
     *
     * @param d The date to format.
     * @return A properly formatted date.
     * @see #formatDateTime(Date)
     */
    public static String formatDate(Date d) {
        return (d == null) ? null : getDateFormat().format(d);
    }

    /**
     * Formats a date and time value according to formatting standards and
     * localization. This method includes the date and the time. In English, it
     * will appear as "Mmm DD, YYYY HH:MM AM" or "Jan 23, 2002, 5:44 PM.
     *
     * @param d The date to format.
     * @return A properly formatted date and time.
     */
    public static String formatDateTime(Date d) {
        return (d == null) ? null : getDateTimeFormat().format(d);
    }
}
