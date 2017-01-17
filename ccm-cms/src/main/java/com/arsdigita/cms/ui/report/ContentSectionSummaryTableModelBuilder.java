/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
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
 */
package com.arsdigita.cms.ui.report;

import java.util.List;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.RowData;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.CMS;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentSection;

/**
 * TableModelBuilder that creates a model for the content section summary
 * report.
 *
 * @author
 * <a href="https://sourceforge.net/users/thomas-buckel/">thomas-buckel</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ContentSectionSummaryTableModelBuilder
        extends AbstractTableModelBuilder {

    @Override
    public TableModel makeModel(final Table table, final PageState state) {

        final ContentSection section = CMS.getContext().getContentSection();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentSectionSummaryController controller = cdiUtil.findBean(
                ContentSectionSummaryController.class);

        final List<RowData<Long>> rows = controller.createReportData(section);

        return new ContentSectionSummaryTableModel(rows);
    }
}
