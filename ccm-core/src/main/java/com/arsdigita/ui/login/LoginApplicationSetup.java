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
package com.arsdigita.ui.login;

import java.util.UUID;
import org.libreccm.modules.InstallEvent;
import org.libreccm.web.AbstractCcmApplicationSetup;
import org.libreccm.web.CcmApplication;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LoginApplicationSetup extends AbstractCcmApplicationSetup {

    public static final String LOGIN_APP_NAME = "Login";

    public LoginApplicationSetup(final InstallEvent event) {
        super(event);
    }

    @Override
    public void setup() {
        final CcmApplication login = new CcmApplication();
        login.setUuid(UUID.randomUUID().toString());
        login.setApplicationType(LoginConstants.LOGIN_APP_TYPE);
        login.setPrimaryUrl(LoginConstants.LOGIN_PAGE_URL);

        getEntityManager().persist(login);
    }

}
