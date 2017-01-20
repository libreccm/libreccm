/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.permissions;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;

import org.libreccm.core.CcmObject;

import static com.arsdigita.cms.ui.permissions.CMSPermissionsConstants.*;

/**
 *
 * Component that Renders the Header of the Permissions Admin pages
 *
 * @author sdeusch@arsdigita.com
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CMSPermissionsHeader extends BoxPanel {

    private final CMSPermissionsPane parent;
    private final Label title;

    /**
     * Constructor
     */
    CMSPermissionsHeader(final CMSPermissionsPane parent) {
        this.parent = parent;
        title = new Label();
        title.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final Label target = (Label) event.getTarget();
                target.setLabel(PAGE_TITLE);
            }

        });
        title.setClassAttr("heading");
        add(title);

        // Used to render the object name in the navbar
        final Label objectName = new Label();
        objectName.addPrintListener(new PrintListener() {

            public void prepare(final PrintEvent event) {
                final Label target = (Label) event.getTarget();
                target.setLabel(getObjectName(event));
            }

        });

        final DimensionalNavbar navbar = new DimensionalNavbar();
        navbar.add(new Link(new Label(PERSONAL_SITE), "/pvt/home"));
        navbar.add(new Link(new Label(MAIN_SITE), "/"));
        navbar.add(new Link(new Label(PERMISSIONS_INDEX), "/permissions/"));
        navbar.add(objectName);
        navbar.setClassAttr("permNavBar");
        add(navbar);
    }

    private String getObjectName(final PrintEvent event) {
        final PageState state = event.getPageState();
        final CcmObject object = parent.getObject(state);
        final String objectName = String.format("%s (ID %d)",
                                                object.getDisplayName(),
                                                object.getObjectId());
        return objectName;
    }

    /**
     * Returns the object used to render the title of the panel.
     */
    Label getTitle() {
        return title;
    }

}
