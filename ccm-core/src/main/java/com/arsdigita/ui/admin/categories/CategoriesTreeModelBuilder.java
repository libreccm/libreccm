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
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.tree.TreeModel;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.util.LockableImpl;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class CategoriesTreeModelBuilder
        extends LockableImpl
        implements TreeModelBuilder {

    private final ParameterSingleSelectionModel<String> selectedDomainId;

    public CategoriesTreeModelBuilder(
            final ParameterSingleSelectionModel<String> selectedDomainId) {

        this.selectedDomainId = selectedDomainId;

    }

    @Override
    public TreeModel makeModel(final Tree tree,
                               final PageState state) {
        final DomainRepository domainRepository = CdiUtil.createCdiUtil().
                findBean(DomainRepository.class);
        final Domain domain = domainRepository.findById(Long.parseLong(
                selectedDomainId.getSelectedKey(state)));
        
        return new CategoriesTreeModel(domain);
    }

}
