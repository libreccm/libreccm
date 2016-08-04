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
package org.libreccm.admin.ui;

import org.libreccm.modules.InstallEvent;
import org.libreccm.web.AbstractCcmApplicationSetup;
import org.libreccm.web.CcmApplication;

import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AdminJsfApplicationSetup extends AbstractCcmApplicationSetup {

    public static final String ADMIN_APP_NAME = "CcmAdminJsf";
    
    public AdminJsfApplicationSetup(final InstallEvent event) {
        super(event);
    }
    
    @Override
    public void setup() {
        final CcmApplication admin = new CcmApplication();
        admin.setUuid(UUID.randomUUID().toString());
        admin.setApplicationType("org.libreccm.ui.admin.AdminFaces");
        admin.setPrimaryUrl("/admin-jsf/");
        
        getEntityManager().persist(admin);
    }
    
    
    
}
