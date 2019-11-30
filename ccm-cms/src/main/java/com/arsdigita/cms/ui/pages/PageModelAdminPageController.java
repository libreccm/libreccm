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

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageModelAdminPageController {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private PageModelRepository pageModelRepository;

    @Transactional(Transactional.TxType.REQUIRED)
    public Map<String, Object> findDraftPageModelsByApplication(
        final Pages pages
    ) {
        final List<PageModel> pageModels = pageModelRepository
            .findDraftByApplication(pages);
        final Map<String, Object> result = new HashMap<>();
        for (final PageModel pageModel : pageModels) {
            result.put("pageModelId", pageModel.getPageModelId());
            final String title = globalizationHelper
                .getValueFromLocalizedString(pageModel.getTitle());
            result.put("title", title);
        }
        
        return result;
    }

}
