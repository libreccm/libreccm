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
package com.arsdigita.ui.admin.categories;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class DomainPropertySheetModelBuilder
    extends LockableImpl
    implements PropertySheetModelBuilder {

    private final ParameterSingleSelectionModel<String> selectedDomainId;

    public DomainPropertySheetModelBuilder(
        final ParameterSingleSelectionModel<String> selectedDomainId) {

        this.selectedDomainId = selectedDomainId;

    }

    @Override
    public PropertySheetModel makeModel(final PropertySheet sheet,
                                        final PageState state) {
        final String domainIdStr = selectedDomainId.getSelectedKey(state);
        final Domain selectedDomain;
        if (Strings.isBlank(domainIdStr)) {
            selectedDomain = null;
        } else {
            final DomainRepository domainRepository = CdiUtil.createCdiUtil()
                .findBean(DomainRepository.class);
            selectedDomain = domainRepository.findById(Long.parseLong(
                domainIdStr)).get();
        }

        return new DomainPropertySheetModel(selectedDomain);

    }

}
