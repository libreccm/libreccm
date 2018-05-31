/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.PageState;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ContextBar;
import com.arsdigita.web.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;
import org.librecms.CmsConstants;

import java.util.List;

/**
 * <p>
 * The context bar of the content center UI.</p>
 *
 * @author Justin Ross
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
// Made public (instead of unspecified) in 6.6.8
public class WorkspaceContextBar extends ContextBar {

    /**
     * A logger instance, primarily to assist debugging .
     */
    private static final Logger LOGGER = LogManager.getLogger(
        WorkspaceContextBar.class);

    /**
     *
     * @param state
     *
     * @return
     */
    @Override
    protected List<Entry> entries(final PageState state) {

        final List<Entry> entries = super.entries(state);

        final String centerTitle = (String) new GlobalizedMessage(
            "cms.ui.content_center", CmsConstants.CMS_BUNDLE).localize();
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ApplicationRepository appRepo = cdiUtil.findBean(
            ApplicationRepository.class);
        final List<CcmApplication> apps = appRepo.findByType(
            CmsConstants.CONTENT_CENTER_APP_TYPE);

        final String centerPath = apps.get(0).getPrimaryUrl();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Got Url: " + centerPath);
        }
        final URL url = URL.there(state.getRequest(), centerPath);
        entries.add(new Entry(centerTitle, url));

        return entries;
    }

}
