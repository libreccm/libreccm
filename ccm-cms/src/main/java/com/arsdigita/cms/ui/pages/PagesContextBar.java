/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.pages;

import com.arsdigita.bebop.PageState;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ContextBar;
import com.arsdigita.web.URL;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;
import org.librecms.CmsConstants;
import org.librecms.pages.Pages;

import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PagesContextBar extends ContextBar {

    private Pages pagesInstance;

    @Override
    public List<Entry> entries(final PageState state) {

        final List<Entry> entries = super.entries(state);

        final String centerTitle = (String) new GlobalizedMessage(
            "cms.ui.content_center", CmsConstants.CMS_BUNDLE).localize();
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ApplicationRepository appRepo = cdiUtil.findBean(
            ApplicationRepository.class);
        final List<CcmApplication> apps = appRepo.findByType(
            CmsConstants.CONTENT_CENTER_APP_TYPE);

        final String centerPath = apps.get(0).getPrimaryUrl();
        final URL url = URL.there(state.getRequest(), centerPath);
        entries.add(new Entry(centerTitle, url));

        final URL pagesUrl = URL.there(
            state.getRequest(),
            pagesInstance.getPrimaryUrl());
        entries.add(new Entry(String.format("Pages:: %s",
                                            pagesInstance.getPrimaryUrl()),
                              pagesUrl));

        return entries;

    }

    protected Pages getPagesInstance() {
        return pagesInstance;
    }

    protected void setPagesInstance(final Pages pagesInstance) {
        this.pagesInstance = pagesInstance;
    }

}
