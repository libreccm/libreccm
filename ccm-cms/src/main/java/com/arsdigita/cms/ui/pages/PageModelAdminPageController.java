/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
