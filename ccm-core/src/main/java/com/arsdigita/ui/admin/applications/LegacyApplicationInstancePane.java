/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.ui.admin.GlobalizationUtil;
import org.libreccm.web.CcmApplication;



/**
 * This pane shows informations about a specific instance of a multi instance application, like
 * title, parent application (if any) and the path. Also it contains a form for editing settings
 * specific to the instance.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class LegacyApplicationInstancePane extends SegmentedPanel {

    private CcmApplication application;
    private final ApplicationInstanceAwareContainer appAdminPane;
    private final LegacyApplicationInstancePropertySheetModelBuilder modelBuilder;

    public LegacyApplicationInstancePane(
            final ApplicationInstanceAwareContainer appAdminPane) {

        super();
        this.appAdminPane = appAdminPane;

        modelBuilder = new LegacyApplicationInstancePropertySheetModelBuilder();
        final PropertySheet appInstInfoPanel = new PropertySheet(modelBuilder);

        addSegment(new Label(GlobalizationUtil.globalize(
            "ui.admin.applications.ApplicationInstancePane.info.heading")),
                   appInstInfoPanel);

        if (appAdminPane == null) {

            final Label noAdminPaneLabel = new Label();
            noAdminPaneLabel.addPrintListener(new PrintListener() {
                @Override
                public void prepare(final PrintEvent event) {
                    final Label target = (Label) event.getTarget();

                    target.setLabel(GlobalizationUtil.globalize(
                        "ui.admin.MultiInstancePane.manage.no_instance_admin_pane_found",
                        new String[]{application.getApplicationType()}));
                }

            });

            addSegment(new Label(GlobalizationUtil.globalize(
                "ui.admin.MultiInstanceApplicationPane.manage.heading")),
                       noAdminPaneLabel);
        } else {
            //appAdminPane.setAppInstance(appInstance);
            addSegment(new Label(GlobalizationUtil.globalize(
                "ui.admin.applications.ApplicationInstancePane.manage.heading")),
                       appAdminPane);
        }
    }

    public void setApplication(final CcmApplication application) {
        this.application = application;
        appAdminPane.setAppInstance(application);
        modelBuilder.setApplication(application);
    }

}
