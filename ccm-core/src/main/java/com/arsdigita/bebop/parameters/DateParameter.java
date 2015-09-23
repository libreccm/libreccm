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
 * A class that represents the model for date form parameters.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @version $Id$
 */
public class DateParameter extends ParameterModel {

    public DateParameter(String name) {
        super(name);
    }

    /**
     * This method returns a new Calendar object that is manipulated within transformValue to create
     * a Date Object. This method should be overridden if you wish to use a Calendar other than the
     * lenient GregorianCalendar.
     *
     * @param request the servlet request from which Locale can be extracted if needed
     *
     * @return a new Calendar object
     *
     */
    protected Calendar getCalendar(HttpServletRequest request) {
        return new GregorianCalendar();
    }

    /**
     * Computes a date object from multiple parameters in the request. This method searches for
     * parameters named      <code>getName() + ".year"<code>, <code>getName() +
     * ".month"<code> and <code>getName() + ".day"<code>.  It sets the
     * fields <code>HOUR</code>, <code>MINUTE</code> and <code>SECOND</code> to 0, since they are by
     * default the current time.
     *
     */
    @Override
    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException {
        Calendar c = null;
        Object outVal = null;
        try {

            c = getCalendar(request);
            c.clear();
            //don't accept lenient dates like June 44
            c.setLenient(false);

            String year = Globalization.decodeParameter(request, getName() + ".year");
            String month = Globalization.decodeParameter(request, getName() + ".month");
            String day = Globalization.decodeParameter(request, getName() + ".day");

            if (year == null && month == null && day == null) {
                return transformSingleValue(request);
            }
            if (day == null || day.length() == 0) {
                return null;
            }
            if (year != null) {
                c.set(Calendar.YEAR, Integer.parseInt(year));
            }
            if (month != null) {
                c.set(Calendar.MONTH, Integer.parseInt(month));
            }
            if (day != null) {
                c.set(Calendar.DATE, Integer.parseInt(day));
            }
            outVal = c.getTime();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Day of Month");
        }
        return outVal;
    }

    public Object unmarshal(String encoded) {
        try {
            return new Date(Long.parseLong(encoded));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Cannot unmarshal date '"
                                               + encoded + "': " + e.getMessage());
        }
    }

    public String marshal(Object value) {
        if (value == null) {
            return null;
        } else {
            return Long.toString(((Date) value).getTime());
        }
    }

    public Class getValueClass() {
        return Date.class;
    }

}
