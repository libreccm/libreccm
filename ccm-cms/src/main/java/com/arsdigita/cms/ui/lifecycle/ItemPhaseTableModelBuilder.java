/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;

import org.libreccm.cdi.utils.CdiUtil;

import java.util.List;

/**
 * @author Xixi D'Moon &lt;xdmoon@arsdigita.com&gt;
 * @author Michael Pih
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ItemPhaseTableModelBuilder extends AbstractTableModelBuilder {

    private final LifecycleRequestLocal lifecycle;

    public ItemPhaseTableModelBuilder(final LifecycleRequestLocal lifecycle) {
        this.lifecycle = lifecycle;
    }

    @Override
    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ItemLifecycleAdminController controller = cdiUtil
        .findBean(ItemLifecycleAdminController.class);

        final List<ItemPhaseTableRow> rows = controller
        .findPhasesOfLifecycle(lifecycle.getLifecycle(state));
        
        return new ItemPhaseTableModel(rows);
    }
}
