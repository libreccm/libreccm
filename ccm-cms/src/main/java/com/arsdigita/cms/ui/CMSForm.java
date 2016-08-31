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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.toolbox.ui.Cancellable;

/**
 * A convenience class for CMS forms. The "CMS Admin" class eliminates
 * the nested tables created by the Bebop ColumnPanel. This is mainly
 * to increase form rendering.
 *
 * @author Michael Pih
 */
public class CMSForm extends Form implements Cancellable {

    public static final String CLASS = "CMS Admin";

    public CMSForm(final String name) {
        super(name);

        setClassAttr(CLASS);
        getPanel().setClassAttr(CLASS);
    }

    public CMSForm(final String name, final Container panel) {
        super(name, panel);

        setClassAttr(CLASS);
        panel.setClassAttr(CLASS);
    }

    /**
     * Determines whether the form has been cancelled.
     * Override this method if the form can be cancelled.
     *
     * @param state The page state
     * @return true if the form is cancelled, false otherwise
     */
    public boolean isCancelled(PageState state) {
        return false;
    }
}
