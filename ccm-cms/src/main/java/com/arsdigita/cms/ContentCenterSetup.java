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
package com.arsdigita.cms;

import org.libreccm.modules.InstallEvent;
import org.libreccm.web.AbstractCcmApplicationSetup;
import org.libreccm.web.CcmApplication;
import org.librecms.CmsConstants;

import java.util.UUID;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentCenterSetup extends AbstractCcmApplicationSetup {

    public ContentCenterSetup(final InstallEvent event) {
        super(event);
    }

    @Override
    public void setup() {
        final CcmApplication contentCenter = new CcmApplication();
        contentCenter.setUuid(UUID.randomUUID().toString());
        contentCenter.setApplicationType(CmsConstants.CONTENT_CENTER_APP_TYPE);
        contentCenter.setPrimaryUrl(CmsConstants.CONTENT_CENTER_URL);

        getEntityManager().persist(contentCenter);
    }

}
