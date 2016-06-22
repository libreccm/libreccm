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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.web.ApplicationType;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DefaultApplicationSettingsPane
    extends AbstractAppSettingsPane {

    public DefaultApplicationSettingsPane(
        final ParameterSingleSelectionModel<String> selectedAppType,
        final ParameterSingleSelectionModel<String> selectedAppInstance) {

        super(selectedAppType, selectedAppInstance);
        
        final Label label = new Label();
        label.addPrintListener(e -> {
            final PageState state = e.getPageState();
            final ApplicationType appType = getSelectedAppType(state);
            
            final GlobalizedMessage message;
            if (appType.singleton()) {
                message = new GlobalizedMessage(
                    "ui.admin.applications.settings.singleton.no_setting_for",
                    ADMIN_BUNDLE,
                    new String[]{appType.name()});
            } else {
                message = new GlobalizedMessage(
                    "ui.admin.applications.settings.instance.no_setting_for",
                    ADMIN_BUNDLE,
                    new String[]{appType.name()});
            }
            
            final Label target = (Label) e.getTarget();
            target.setLabel(message);
        });

        add(label);
    }

    @Override
    protected void createWidgets() {

//        final Label label = new Label();
//        label.addPrintListener(e -> {
//            final PageState state = e.getPageState();
//            final ApplicationType appType = getSelectedAppType(state);
//            
//            final GlobalizedMessage message;
//            if (appType.singleton()) {
//                message = new GlobalizedMessage(
//                    "ui.admin.applications.settings.singleton.no_setting_for",
//                    ADMIN_BUNDLE,
//                    new String[]{appType.name()});
//            } else {
//                message = new GlobalizedMessage(
//                    "ui.admin.applications.settings.instance.no_setting_for",
//                    ADMIN_BUNDLE,
//                    new String[]{appType.name()});
//            }
//            
//            final Label target = (Label) e.getTarget();
//            target.setLabel(message);
//        });
//
//        add(label);

    }

}
