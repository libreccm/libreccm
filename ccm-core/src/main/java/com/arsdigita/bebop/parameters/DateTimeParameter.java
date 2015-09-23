/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.parameters;

import com.arsdigita.globalization.Globalization;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.servlet.http.HttpServletRequest;

/**
 *    A class that represents the model for date and time form parameters.
 *    (based on the code in DateParameter.java)
 *
 *    @author Scot Seago 
 *    @author Uday Mathur 
 *    @version $Id$
 */
public class DateTimeParameter extends ParameterModel {

    public DateTimeParameter(String name) {
        super(name);
    }


    /**
     * This method returns a new Calendar object that is manipulated
     * within transformValue to create a Date Object. This method should
     * be overridden if you wish to use a Calendar other than the
     * lenient GregorianCalendar.
     *
     * @param request the servlet request from which Locale can be
     * extracted if needed
     *
     * @return a new Calendar object
     * */
    protected Calendar getCalendar(HttpServletRequest request) {
        return new GregorianCalendar();
    }

    /**
     * Computes a dateTime object from multiple parameters in the
     * request. This method searches for parameters named
     * <code>getName() + ".year"<code>,
     * <code>getName() + ".month"<code>,
     * <code>getName() + ".day"<code>,
     * <code>getName() + ".hour"<code>,
     * <code>getName() + ".minute"<code>,
     * <code>getName() + ".second"<code>, and
     * <code>getName() + ".amOrPm"<code>.
     * */
    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException {

        Calendar c = getCalendar(request);
        c.clear();

        String year = Globalization.decodeParameter(request, getName()+".year");
        String month = Globalization.decodeParameter(request, getName()+".month");
        String day = Globalization.decodeParameter(request, getName()+".day");
        String hour = Globalization.decodeParameter(request, getName()+".hour");
        String minute = Globalization.decodeParameter(request, getName()+".minute");
        String second = Globalization.decodeParameter(request, getName()+".second");
        String amOrPm = Globalization.decodeParameter(request, getName()+".amOrPm");

        // when submitting a non-compulsory datetime widget, we used to 
        // get an 'For input string: ""' error message; hence the datetime
        // was compulsory anyways.
        
        // check correctly that *something* was entered...
        //if ( year == null && month == null && day == null ) {
        if ((day == null || "".equals(day)) &&
            (hour == null || "".equals(hour)) &&
            (minute == null || "".equals(minute))) {
            return transformSingleValue(request);
        }

        // don't just check nulls (which won't happen), but also ""
        if ( year != null && !"".equals(year) ) {
            c.set(Calendar.YEAR, Integer.parseInt(year));
        }
        if ( month != null && !"".equals(month) ) {
            c.set(Calendar.MONTH, Integer.parseInt(month));
        }
        if ( day != null && !"".equals(day) ) {
            c.set(Calendar.DATE, Integer.parseInt(day));
        }
        if ( hour != null && !"".equals(hour) ) {
            c.set(Calendar.HOUR, Integer.parseInt(hour));
        }
        if ( minute != null && !"".equals(minute) ) {
            c.set(Calendar.MINUTE, Integer.parseInt(minute));
        }
        if ( second != null && !"".equals(second) ) {
            c.set(Calendar.SECOND, Integer.parseInt(second));
        }
        if ( amOrPm != null && !"".equals(amOrPm) ) {
            c.set(Calendar.AM_PM, Integer.parseInt(amOrPm));
        }
        return c.getTime();
    }

    public Object unmarshal(String encoded) {
        try {
            return new Date(Long.parseLong(encoded));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot unmarshal dateTime '"
                                               + encoded +"': " + e.getMessage());
        }
    }

    public String marshal(Object value) {
        return Long.toString(((Date) value).getTime());
    }

    public Class getValueClass() {
        return Date.class;
    }

}
