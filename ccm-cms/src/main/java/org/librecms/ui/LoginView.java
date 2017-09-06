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
package org.librecms.ui;

import com.vaadin.cdi.CDIView;
import org.libreccm.ui.AbstractLoginView;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@CDIView(value = LoginView.VIEWNAME,
         uis = {CmsUI.class})
public class LoginView extends AbstractLoginView {

    private static final long serialVersionUID = -8076055152219333275L;

    public static final String VIEWNAME = "cmslogin";

    @Override
    protected String getTargetView() {
        return CmsView.VIEWNAME;
    }

}
