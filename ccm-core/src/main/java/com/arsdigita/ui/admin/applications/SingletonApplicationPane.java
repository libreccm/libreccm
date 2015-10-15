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
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.ui.admin.GlobalizationUtil;
import org.libreccm.web.ApplicationType;

/**
 * Pane for managing singleton applications. Shows a form to edit application specific settings.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id$
 */
public class SingletonApplicationPane extends BaseApplicationPane {

    public SingletonApplicationPane(final ApplicationType applicationType, final SimpleContainer appAdminPane) {
        super(applicationType);

        if (appAdminPane == null) {
            addSegment(new Label(GlobalizationUtil.globalize(
                    "ui.admin.SingletonApplicationPane.manage.heading")),
                       new Label(GlobalizationUtil.globalize(
                    "ui.admin.SingletonApplicationPane.manage.no_admin_pane_found",
                    new String[]{applicationType.name()})));
        } else {
            addSegment(new Label(GlobalizationUtil.globalize(
                    "ui.admin.SingletonApplicationPane.manage.heading")),
                       appAdminPane);
        }
    }

}
