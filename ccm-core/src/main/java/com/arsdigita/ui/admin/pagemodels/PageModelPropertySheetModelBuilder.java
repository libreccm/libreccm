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
package com.arsdigita.ui.admin.pagemodels;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelRepository;

/**
 * Implementation of {@link PropertySheetModelBuilder} for the the property
 * sheet used in {@link PageModelDetails} for displaying the basic properties of
 * a {@link PageModel}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelPropertySheetModelBuilder
    extends LockableImpl
    implements com.arsdigita.bebop.PropertySheetModelBuilder {

    private final ParameterSingleSelectionModel<String> selectedModelId;

    public PageModelPropertySheetModelBuilder(
        final ParameterSingleSelectionModel<String> selectedModelId) {

        this.selectedModelId = selectedModelId;
    }

    @Override
    public PropertySheetModel makeModel(final PropertySheet sheet,
                                        final PageState state) {

        final String selectedModelIdStr = selectedModelId.getSelectedKey(
            state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PageModelRepository pageModelRepo = cdiUtil
            .findBean(PageModelRepository.class);
        final PageModel pageModel = pageModelRepo
            .findById(Long.parseLong(selectedModelIdStr))
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No PageModel with ID %s in the database.",
                    selectedModelIdStr)));

        return new PageModelPropertySheetModel(pageModel);
    }

}
