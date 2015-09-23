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
import javax.servlet.http.HttpServletRequest;

/**
 *    A class that represents the model for date form parameters.
 *    This one will allow incomplete entry on date. Should be used in
 *    combination with an additional Boolean DB field to keep track
 *    of the incomplete entry.
 *
 *    @author Sören Bernstein <quasi@quasiweb.de>
 */
public class IncompleteDateParameter extends DateParameter {

    private boolean allowSkipYear = false;
    private boolean allowSkipMonth = false;
    private boolean allowSkipDay = false;
    private boolean skippedYear = false;
    private boolean skippedMonth = false;
    private boolean skippedDay = false;

    public IncompleteDateParameter(String name) {
        super(name);
    }

    public void allowSkipYear(boolean bool) {
        this.allowSkipYear = bool;
    }

    public boolean isSkipYearAllowed() {
        return this.allowSkipYear;
    }

    public void allowSkipMonth(boolean bool) {
        this.allowSkipMonth = bool;
    }

    public boolean isSkipMonthAllowed() {
        return this.allowSkipMonth;
    }

    public void allowSkipDay(boolean bool) {
        this.allowSkipDay = bool;
    }

    public boolean isSkipDayAllowed() {
        return this.allowSkipDay;
    }

    public boolean isDaySkipped() {
        return this.skippedDay;
    }

    public boolean isMonthSkipped() {
        return this.skippedMonth;
    }

    public boolean isYearSkipped() {
        return this.skippedYear;
    }

    public boolean isSkipped() {
        return this.skippedDay || this.skippedMonth || this.skippedYear;
    }

    /**
     * Computes a date object from multiple parameters in the
     * request. This method searches for parameters named
     * <code>getName() + ".year"<code>, <code>getName() +
     * ".month"<code> and <code>getName() + ".day"<code>.  It sets the
     * fields <code>HOUR</code>, <code>MINUTE</code> and
     * <code>SECOND</code> to 0, since they are by default the current
     * time.
     * */
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

            // If non-skippable field missing return null
            if ((this.allowSkipYear == false && (year == null || year.length() == 0))
                    || (this.allowSkipMonth == false && (month == null || month.length() == 0))
                    || (this.allowSkipDay == false && (day == null || day.length() == 0))) {
                return null;
            }

            if (year != null && year.length() > 0) {
                this.skippedYear = false;
                c.set(Calendar.YEAR, Integer.parseInt(year));
            } else {
                this.skippedYear = true;
                c.set(Calendar.YEAR, 0);
            }
            if (month != null && month.length() > 0) {
                this.skippedMonth = false;
                c.set(Calendar.MONTH, Integer.parseInt(month));
            } else {
                this.skippedMonth = true;
                // BE AWARE: Java month counting is of by 1 because someone decided
                // to start counting at 0, So january = 0 (not 1);
                c.set(Calendar.MONTH, 0);
            }
            if (day != null && day.length() > 0) {
                this.skippedDay = false;
                c.set(Calendar.DATE, Integer.parseInt(day));
            } else {
                this.skippedDay = true;
                c.set(Calendar.DATE, 1);
            }
            outVal = c.getTime();
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid Day of Month");
        }
        return outVal;
    }
}
