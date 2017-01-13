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
package com.arsdigita.cms.ui.item;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.RowData;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItem;

import java.util.List;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemLanguagesTableModelBuilder 
    extends LockableImpl
    implements TableModelBuilder {

    private final ItemSelectionModel itemSelectionModel;

    protected ItemLanguagesTableModelBuilder(
        final ItemSelectionModel itemSelectionModel) {
        this.itemSelectionModel = itemSelectionModel;
    }

    @Override
    public TableModel makeModel(final Table table, final PageState state) {
        final ContentItem item = itemSelectionModel.getSelectedItem(state);
        
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ItemLanguagesController controller = cdiUtil.findBean(ItemLanguagesController.class);
        final List<RowData<String>> rows = controller.retrieveLanguageVariants(
            item);
        
        return new ItemLanguagesTableModel(rows);
    }

}
