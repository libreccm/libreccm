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

import com.vaadin.cdi.CDIUI;
import com.vaadin.cdi.CDIViewProvider;
import com.vaadin.cdi.URLMapping;
import com.vaadin.navigator.Navigator;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.UI;
import org.apache.shiro.subject.Subject;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@URLMapping("vaadin")
@CDIUI("cms")
public class CmsUI extends UI {

    private static final long serialVersionUID = 1867619939266841203L;

    @Inject
    private CDIViewProvider viewProvider;

    @Inject
    private Subject subject;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Override
    protected void init(final VaadinRequest request) {

        final Navigator navigator = new Navigator(this, this);
        navigator.addProvider(viewProvider);

//        navigator.addViewChangeListener(new AuthNavListener());

        if (subject.isAuthenticated()) {
            navigator.navigateTo(CmsView.VIEWNAME);
        } else {
            navigator.navigateTo(LoginView.VIEWNAME);
        }
    }

}
