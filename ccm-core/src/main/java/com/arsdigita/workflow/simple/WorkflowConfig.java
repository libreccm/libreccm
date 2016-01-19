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
package com.arsdigita.workflow.simple;

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
public final class WorkflowConfig {

    @Setting
    private Boolean simpleAlertsEnabled = true;

    @Setting
    private String alertsSender;

    public static WorkflowConfig getConfig() {
        final CdiUtil cdiUtil = new CdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        return confManager.findConfiguration(WorkflowConfig.class);
    }

    public Boolean getSimpleAlertsEnabled() {
        return simpleAlertsEnabled;
    }

    public void setSimpleAlertsEnabled(final Boolean simpleAlertsEnabled) {
        this.simpleAlertsEnabled = simpleAlertsEnabled;
    }

    public String getAlertsSender() {
        return alertsSender;
    }

    public void setAlertsSender(final String alertsSender) {
        this.alertsSender = alertsSender;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(simpleAlertsEnabled);
        hash = 47 * hash + Objects.hashCode(alertsSender);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof WorkflowConfig) {
            return false;
        }
        final WorkflowConfig other = (WorkflowConfig) obj;
        if (!Objects.equals(alertsSender, other.getAlertsSender())) {
            return false;
        }
        return Objects.equals(simpleAlertsEnabled, other
                              .getSimpleAlertsEnabled());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "simpleAlertsEnabled = %b, "
                                 + "alertsSender = %s"
                                 + " }",
                             super.toString(),
                             simpleAlertsEnabled,
                             alertsSender);
    }

}
