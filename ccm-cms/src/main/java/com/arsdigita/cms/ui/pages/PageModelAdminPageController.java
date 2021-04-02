/*
 * Copyright (C) 2021 LibreCCM Foundation.
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

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;
import org.librecms.pages.Pages;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class PageModelAdminPageController {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PageModelRepository pageModelRepository;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<PageModelData> findDraftPageModelsByApplication(
        final Pages pages
    ) {
        final List<PageModel> pageModels = pageModelRepository
            .findDraftByApplication(pages);
        return pageModels.stream().map(this::buildPageModelData).collect(
            Collectors.toList()
        );
    }

    private PageModelData buildPageModelData(final PageModel fromPageModel) {
        final PageModelData result = new PageModelData();
        result.setPageModelId(fromPageModel.getPageModelId());
        result.setTitle(globalizationHelper.getValueFromLocalizedString(
            fromPageModel.getTitle()));
        return result;
    }

}
