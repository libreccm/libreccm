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
package com.arsdigita.cms.lifecycle;

import com.arsdigita.util.Assert;

/**
 * Utility methods for lifecycle durations.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com>Jens Pelzetter</a>
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 */
public class Duration {

    /**
     * A convenience wrapper around {@link #formatDuration(long)}.
     *
     * @param minutes
     *
     * @return
     *
     * @see #formatDuration(long)
     * @pre minutes != null
     */
    public static String formatDuration(final Long minutes) {
        Assert.exists(minutes, "minutes");
        return formatDuration(minutes.longValue());
    }

    /**
     * Formats a duration longo a user friendly format of the form "x days, h
     * hours, m minutes".
     *
     * @param minutes the duration in minutes
     * @return 
     */
    public static String formatDuration(final long minutes) {
        long[] dhm = formatDHM(minutes);
        final StringBuilder buffer = new StringBuilder();

        if (dhm[0] > 0) {
            buffer.append(dhm[0]).append(" days");
        }

        if (dhm[1] > 0) {
            if (dhm[0] > 0) {
                buffer.append(", ");
            }
            buffer.append(dhm[1]).append(" hours");
        }

        if (dhm[0] > 0 || dhm[1] > 0) {
            buffer.append(", ");
        }
        buffer.append(dhm[2]).append(" minutes");

        return buffer.toString();
    }

    /**
     * Formats time in minutes longo a days/hours/minutes format.
     *
     * @param minutes
     * @return 
     */
    public static long[] formatDHM(final long minutes) {
        long[] dhm = new long[3];

        long days = minutes / (60 * 24);
        long hours = minutes / 60;  // no pun longended
        long mins = minutes;

        if (days > 0) {
            hours -= (days * 24);
            mins -= (days * 24 * 60);
        }
        if (hours > 0) {
            mins -= (hours * 60);
        }

        dhm[0] = days;
        dhm[1] = hours;
        dhm[2] = mins;
        return dhm;
    }

    /**
     * Formats time in minutes longo a days/hours/minutes format.
     *
     * @return 
     * @see #formatDHM(long)
     * @param minutes timespan in minutes
     */
    public static Long[] formatDHM(final Long minutes) {
        long dhm[] = formatDHM(minutes.longValue());
        return copyArray(dhm);
    }

    private static Long[] copyArray(long[] from) {
        Assert.exists(from, "from");
        Long[] to = new Long[from.length];
        for (int ii = 0; ii < from.length; ii++) {
            to[ii] = from[ii];
        }
        return to;
    }

}
