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
package com.arsdigita.formbuilder;

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
public final class FormBuilderConfig {

    @Setting
    private String actionsHelpUrl;

    @Setting
    private String controlsHelpUrl;

    @Setting
    @SuppressWarnings("PMD.LongVariable")
    private Boolean interpolateEmailActions;

    public static FormBuilderConfig getConfig() {
        final ConfigurationManager confManager = CdiUtil.createCdiUtil()
            .findBean(ConfigurationManager.class);
        return confManager.findConfiguration(FormBuilderConfig.class);
    }

    public String getActionsHelpUrl() {
        return actionsHelpUrl;
    }

    public void setActionsHelpUrl(final String actionsHelpUrl) {
        this.actionsHelpUrl = actionsHelpUrl;
    }

    public String getControlsHelpUrl() {
        return controlsHelpUrl;
    }

    public void setControlsHelpUrl(final String controlsHelpUrl) {
        this.controlsHelpUrl = controlsHelpUrl;
    }

    public Boolean getInterpolateEmailActions() {
        return interpolateEmailActions;
    }

    @SuppressWarnings("PMD.LongVariable")
    public void setInterpolateEmailActions(final Boolean interpolateEmailActions) {
        this.interpolateEmailActions = interpolateEmailActions;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 71 * hash + Objects.hashCode(actionsHelpUrl);
        hash = 71 * hash + Objects.hashCode(controlsHelpUrl);
        hash = 71 * hash + Objects.hashCode(interpolateEmailActions);
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
        if (!(obj instanceof FormBuilderConfig)) {
            return false;
        }
        final FormBuilderConfig other = (FormBuilderConfig) obj;
        if (!Objects.equals(actionsHelpUrl, other.getActionsHelpUrl())) {
            return false;
        }
        if (!Objects.equals(controlsHelpUrl, other.getControlsHelpUrl())) {
            return false;
        }
        return Objects.equals(interpolateEmailActions,
                              other.getInterpolateEmailActions());
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "actionsHelpUrl = \"%s\", "
                                 + "controlsHelpUrl = \"%s\", "
                                 + "interpolateEmailActions = %b"
                                 + " }",
                             super.toString(),
                             actionsHelpUrl,
                             controlsHelpUrl,
                             interpolateEmailActions);
    }

}
