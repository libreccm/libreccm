/*
 * Copyright (C) 2002-2005 Runtime Collective Ltd. All Rights Reserved.
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
 */

package com.arsdigita.xml.formatters;

//import com.arsdigita.kernel.Kernel;
import java.util.Calendar;
import java.util.Date;

/**
 * A DateFormatter which displays dates as:
 * <br>
 * &lt;yearNo&gt; | &lt;monthNo&gt; | &lt;dayOfMonthNo&gt; | &lt;dayOfWeekNo&gt;
 * | &lt;hour&gt; | &lt;minute&gt; | &lt;second&gt; | &lt;apm&gt; | &lt;localised date&gt;
 * <br>
 * the numbers are padded with 0s, so the positions of the fields are always
 * 0, 7, 12, 17, 21, 26, 31, 36, 41 (in Java), and 1, 8, 13, 18, 22, 27, 32, 37, 42 (in XSL).
 */
public class FullDateFormatter extends DateFormatter {

    public static String SEPARATOR = " | ";
    public static String AM = "am";
    public static String PM = "pm";
    public static char ZERO = '0';

    public String format(Object value) {

        String parentResult = super.format(value);

//        if (!XMLConfig.getConfig().getActivateFullTimeFormatter()) {
//            return parentResult;
//        }

        Date date = (Date) value;
        Calendar cal = Calendar.getInstance();
        StringBuffer result = new StringBuffer(60);

        cal.setTime(date);

        append(result, cal.get(Calendar.YEAR));
        appendMaybeSmall(result, cal.get(Calendar.MONTH));
        appendMaybeSmall(result, cal.get(Calendar.DAY_OF_MONTH));
        append(result, cal.get(Calendar.DAY_OF_WEEK));
        appendMaybeSmall(result, cal.get(Calendar.HOUR));
        appendMaybeSmall(result, cal.get(Calendar.MINUTE));
        appendMaybeSmall(result, cal.get(Calendar.SECOND));

        switch (cal.get(Calendar.AM_PM)) {
        case Calendar.AM: result.append(AM); break;
        case Calendar.PM: result.append(PM); break;
        }
    
        result.append(SEPARATOR)
            .append(parentResult);
        
        return result.toString();
    }

    public void appendMaybeSmall(StringBuffer sb, int value) {
        if (value < 10) {
            sb.append(ZERO);
        }
        append(sb, value);
    }

    public void append(StringBuffer sb, int value) {
        sb.append(value)
            .append(SEPARATOR);
    }
}
