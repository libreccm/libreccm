/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.contenttypes;

import org.libreccm.configuration.Configuration;

import org.libreccm.configuration.Setting;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public class NewsConfig {
    
    /**
     * First year shown in date selector for the release date of a {@link News}.
     */
    @Setting
    private int startYear = 2010;
    
    /**
     * Delta to be added to the current year to get the last year shown
     * in the date selector for a {@link News}.
     */
    @Setting
    private int endYearDelta = 10;

    public int getStartYear() {
        return startYear;
    }

    public void setStartYear(final int startYear) {
        this.startYear = startYear;
    }

    public int getEndYearDelta() {
        return endYearDelta;
    }

    public void setEndYearDelta(final int endYearDelta) {
        this.endYearDelta = endYearDelta;
    }
    
    
    
}
