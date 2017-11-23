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
package org.libreccm.admin.ui;

import com.vaadin.cdi.ViewScoped;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelManager;
import org.libreccm.pagemodel.PageModelRepository;

import java.io.Serializable;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PageModelsController implements Serializable {

    private static final long serialVersionUID = 6204724295214879943L;

    @Inject
    private PageModelManager pageModelManager;

    @Inject
    private PageModelRepository pageModelRepo;

    @Inject
    private PageModelsTableDataProvider pageModelsTableDataProvider;

    protected PageModelManager getPageModelManager() {
        return pageModelManager;
    }

    protected PageModelRepository getPageModelRepo() {
        return pageModelRepo;
    }

    protected PageModelsTableDataProvider getPageModelsTableDataProvider() {
        return pageModelsTableDataProvider;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void deletePageModel(final long pageModelId) {

        final PageModel pageModel = pageModelRepo
            .findById(pageModelId)
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModelId)));

        pageModelRepo.delete(pageModel);
        pageModelsTableDataProvider.refreshAll();
    }

}
