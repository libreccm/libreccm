/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

/**
 * Shows a duration stored in minutes in a human readable way as days, hours and
 * minutes.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Duration {

    /**
     * Days part of the duration.
     */
    private long days;

    /**
     * Hours part of the duration.
     */
    private long hours;

    /**
     * Minutes part of the duration.
     */
    private long minutes;

    /**
     * Create a {@code Duration} instance from a duration in minutes.
     *
     * @param value The duration in minutes.
     *
     * @return A {@code Duration} instance for displaying the duration as days,
     *         hours and minutes.
     */
    public static Duration fromMinutes(final long value) {
        final long days = value / (24 * 60);
        final long daysReminder = value % (24 * 60);
        final long hours = daysReminder / 60;
        final long minutes = daysReminder % 60;

        final Duration result = new Duration();
        result.setDays(days);
        result.setHours(hours);
        result.setMinutes(minutes);
        return result;
    }

    public long getDays() {
        return days;
    }

    public void setDays(final long days) {
        this.days = days;
    }

    public long getHours() {
        return hours;
    }

    public void setHours(final long hours) {
        this.hours = hours;
    }

    public long getMinutes() {
        return minutes;
    }

    public void setMinutes(final long minutes) {
        this.minutes = minutes;
    }

    public long toMinutes() {
        return days * 24 * 60 + hours * 60 + minutes;
    }

}
