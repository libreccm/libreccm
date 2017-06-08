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

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public class EventConfig {

    @Setting
    private boolean hideDateDescription = false;

    @Setting
    private boolean hideMainContributor = false;

    @Setting
    private boolean hideEventType = false;

    @Setting
    private boolean hideLinkToMap = false;
    
    @Setting
    private boolean hideCost = false;
    
    @Setting
    private boolean useHtmlDateDescription = true;
    
    @Setting
    private int startYear = GregorianCalendar.getInstance().get(Calendar.YEAR)
                            - 1;
    @Setting
    private int endYearDelta = 3;
    
    @Setting
    private boolean leadTextOptional = false;
    
    @Setting
    private boolean startTimeOptional = false;

    public boolean isHideDateDescription() {
        return hideDateDescription;
    }

    public void setHideDateDescription(final boolean hideDateDescription) {
        this.hideDateDescription = hideDateDescription;
    }

    public final boolean isHideMainContributor() {
        return hideMainContributor;
    }

    public void setHideMainContributor(final boolean hideMainContributor) {
        this.hideMainContributor = hideMainContributor;
    }

    public final boolean isHideEventType() {
        return hideEventType;
    }

    public void setHideEventType(final boolean hideEventType) {
        this.hideEventType = hideEventType;
    }

    public final boolean isHideLinkToMap() {
        return hideLinkToMap;
    }

    public void setHideLinkToMap(final boolean hideLinkToMap) {
        this.hideLinkToMap = hideLinkToMap;
    }

    public final boolean isHideCost() {
        return hideCost;
    }

    public void setHideCost(final boolean hideCost) {
        this.hideCost = hideCost;
    }

    public final boolean isUseHtmlDateDescription() {
        return useHtmlDateDescription;
    }

    public void setUseHtmlDateDescription(final boolean useHtmlDateDescription) {
        this.useHtmlDateDescription = useHtmlDateDescription;
    }

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

    public final boolean isLeadTextOptional() {
        return leadTextOptional;
    }

    public void setLeadTextOptional(final boolean leadTextOptional) {
        this.leadTextOptional = leadTextOptional;
    }

    public final boolean isStartTimeOptional() {
        return startTimeOptional;
    }

    public void setStartTimeOptional(final boolean startTimeOptional) {
        this.startTimeOptional = startTimeOptional;
    }

    
}
