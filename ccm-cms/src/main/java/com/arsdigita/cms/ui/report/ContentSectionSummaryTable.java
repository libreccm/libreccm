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

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.globalization.GlobalizedMessage;
import org.librecms.CmsConstants;

/**
 * Table component for content section summary report.
 *
 * @author
 * <a href="https://sourceforge.net/users/thomas-buckel/">thomas-buckel</a>
 */
public class ContentSectionSummaryTable extends Table {

    public static final int COL_FOLDER_NAME = 0;
    public static final int COL_SUBFOLDER_COUNT = 1;
    public static final int COL_CONTENT_TYPE = 2;
    public static final int COL_DRAFT_COUNT = 3;
    public static final int COL_LIVE_COUNT = 4;

    public ContentSectionSummaryTable() {
        super();

        setModelBuilder(new ContentSectionSummaryTableModelBuilder());
        
        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
                COL_FOLDER_NAME,
                new Label(new GlobalizedMessage("cms.ui.reports.css.folder",
                                                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
                COL_SUBFOLDER_COUNT,
                new Label(new GlobalizedMessage(
                        "cms.ui.reports.css.subfolderCount",
                        CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
                COL_CONTENT_TYPE,
                new Label(
                        new GlobalizedMessage("cms.ui.reports.css.contentType",
                                              CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
                COL_DRAFT_COUNT,
                new Label(new GlobalizedMessage("cms.ui.reports.css.draft",
                                                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
                COL_LIVE_COUNT,
                new Label(new GlobalizedMessage("cms.ui.reports.css.live",
                                                CmsConstants.CMS_BUNDLE))));

        setEmptyView(new Label(new GlobalizedMessage(
                "cms.ui.reports.css.emptyResult",
                CmsConstants.CMS_BUNDLE)));
    }

}
