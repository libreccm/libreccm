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
import com.vaadin.data.provider.AbstractBackEndDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.pagemodel.ComponentModel;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;

import java.util.Objects;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PageModelComponentModelsTableDataProvider
    extends AbstractBackEndDataProvider<ComponentModel, String> {

    private static final long serialVersionUID = -8880329002442808769L;

    @Inject
    private PageModelRepository pageModelRepo;

    private PageModel pageModel;

    protected PageModel getPageModel() {
        return pageModel;
    }

    protected void setPageModel(final PageModel pageModel) {
        Objects.requireNonNull(pageModel);
        this.pageModel = pageModel;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected Stream<ComponentModel> fetchFromBackEnd(
        final Query<ComponentModel, String> query) {

        return retrievePageModel()
            .getComponents()
            .stream();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected int sizeInBackEnd(final Query<ComponentModel, String> query) {

        return retrievePageModel().getComponents().size();
    }

    private PageModel retrievePageModel() {

        return pageModelRepo
            .findById(pageModel.getPageModelId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %d in the database.",
                    pageModel.getPageModelId())));
    }

}
