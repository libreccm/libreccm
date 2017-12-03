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
import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.ComponentModels;
import org.libreccm.pagemodel.PageModelComponentModel;

import java.util.Optional;
import java.util.stream.Stream;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class PageModelComponentModelTypesDataProvider
    extends AbstractDataProvider<PageModelComponentModel, String> {

    private static final long serialVersionUID = -27393177360237040L;

    @Inject
    private ComponentModels componentModels;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Override
    public boolean isInMemory() {
        return true;
    }

    @Override
    public int size(final Query<PageModelComponentModel, String> query) {

        final Optional<String> filter = query.getFilter();

        if (filter.isPresent()) {
            return (int) componentModels
                .findAvailableComponentModels()
                .stream()
                .filter(model -> filterModels(model, filter.get()))
                .count();

        } else {
            return componentModels
                .findAvailableComponentModels()
                .size();
        }
    }

    @Override
    public Stream<PageModelComponentModel> fetch(
        final Query<PageModelComponentModel, String> query) {

        final Optional<String> filter = query.getFilter();

        if (filter.isPresent()) {
            return componentModels
                .findAvailableComponentModels()
                .stream()
                .filter(model -> filterModels(model, filter.get()));
        } else {
            return componentModels
                .findAvailableComponentModels()
                .stream();
        }
    }

    private boolean filterModels(final PageModelComponentModel model,
                                 final String filter) {

        final LocalizedTextsUtil textsUtil = globalizationHelper
            .getLocalizedTextsUtil(model.descBundle());

        final String title = textsUtil
            .getText(textsUtil.getText(model.titleKey()));

        return title.startsWith(filter);
    }

}
