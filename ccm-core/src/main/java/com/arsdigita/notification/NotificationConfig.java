/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.notification;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.configuration.Setting;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public final class NotificationConfig {

    @Setting
    private Integer requestManagerDelay = 900;

    @Setting
    private Integer requestManagerPeriod = 900;

    @Setting
    private Integer digestQueueDelay = 900;

    @Setting
    private Integer digestQueuePeriod = 900;

    @Setting
    private Integer simpleQueueDelay = 900;

    @Setting
    private Integer simpleQueuePeriod = 900;

    public static NotificationConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(NotificationConfig.class);
    }

    public NotificationConfig() {
        super();
    }

    public Integer getRequestManagerDelay() {
        return requestManagerDelay;
    }

    public void setRequestManagerDelay(final Integer requestManagerDelay) {
        this.requestManagerDelay = requestManagerDelay;
    }

    public Integer getRequestManagerPeriod() {
        return requestManagerPeriod;
    }

    public void setRequestManagerPeriod(final Integer requestManagerPeriod) {
        this.requestManagerPeriod = requestManagerPeriod;
    }

    public Integer getDigestQueueDelay() {
        return digestQueueDelay;
    }

    public void setDigestQueueDelay(final Integer digestQueueDelay) {
        this.digestQueueDelay = digestQueueDelay;
    }

    public Integer getDigestQueuePeriod() {
        return digestQueuePeriod;
    }

    public void setDigestQueuePeriod(final Integer digestQueuePeriod) {
        this.digestQueuePeriod = digestQueuePeriod;
    }

    public Integer getSimpleQueueDelay() {
        return simpleQueueDelay;
    }

    public void setSimpleQueueDelay(final Integer simpleQueueDelay) {
        this.simpleQueueDelay = simpleQueueDelay;
    }

    public Integer getSimpleQueuePeriod() {
        return simpleQueuePeriod;
    }

    public void setSimpleQueuePeriod(final Integer simpleQueuePeriod) {
        this.simpleQueuePeriod = simpleQueuePeriod;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(requestManagerDelay);
        hash = 89 * hash + Objects.hashCode(requestManagerPeriod);
        hash = 89 * hash + Objects.hashCode(digestQueueDelay);
        hash = 89 * hash + Objects.hashCode(digestQueuePeriod);
        hash = 89 * hash + Objects.hashCode(simpleQueueDelay);
        hash = 89 * hash + Objects.hashCode(simpleQueuePeriod);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof NotificationConfig)) {
            return false;
        }
        final NotificationConfig other = (NotificationConfig) obj;
        if (!Objects.equals(requestManagerDelay, other.getRequestManagerDelay())) {
            return false;
        }
        if (!Objects.equals(requestManagerPeriod,
                            other.getRequestManagerPeriod())) {
            return false;
        }
        if (!Objects.equals(digestQueueDelay, other.getDigestQueueDelay())) {
            return false;
        }
        if (!Objects.equals(digestQueuePeriod, other.getDigestQueuePeriod())) {
            return false;
        }
        if (!Objects.equals(simpleQueueDelay, other.getSimpleQueueDelay())) {
            return false;
        }
        return Objects.equals(simpleQueuePeriod, other.getSimpleQueuePeriod());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "requestManagerDelay = %d, "
                                 + "requestManagerPeriod = %d, "
                                 + "digestQueueDelay = %d, "
                                 + "digestQueuePeriod = %d, "
                                 + "simpleQueueDelay = %d, "
                                 + "simpleQueuePeriod = %d"
                                 + " }",
                             super.toString(),
                             requestManagerDelay,
                             requestManagerPeriod,
                             digestQueueDelay,
                             digestQueuePeriod,
                             simpleQueueDelay,
                             simpleQueuePeriod);
    }

}
