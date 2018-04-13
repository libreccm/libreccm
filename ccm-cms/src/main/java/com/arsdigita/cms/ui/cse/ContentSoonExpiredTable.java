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
package com.arsdigita.cms.ui.cse;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.Web;

import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ContentSoonExpiredTable extends Table {

    protected static final int COL_AUTHOR_NAME = 0;
    protected static final int COL_ITEM_NAME = 1;
    protected static final int COL_VIEW = 2;
    protected static final int COL_EDIT = 3;
    protected static final int COL_END_DATE_TIME = 4;

    public ContentSoonExpiredTable() {
        
        super();

//        final ContentSection section = CMS.getContext().getContentSection();

        super.setModelBuilder(new ContentSoonExpiredTableModelBuilder());

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_AUTHOR_NAME,
            new Label(new GlobalizedMessage("cms.ui.cse.authorName",
                                            CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_ITEM_NAME,
            new Label(new GlobalizedMessage("cms.ui.cse.itemName",
                                            CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_VIEW,
            new Label(new GlobalizedMessage("cms.ui.cse.view",
                                            CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_EDIT,
            new Label(new GlobalizedMessage("cms.ui.cse.edit",
                                            CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_END_DATE_TIME,
            new Label(new GlobalizedMessage("cms.ui.cse.endDateTime",
                                            CmsConstants.CMS_BUNDLE))));

        columnModel.get(COL_VIEW).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                try {
                    final Link link = new Link(
                        new Label(new GlobalizedMessage(
                            "cms.ui.cse.viewLink",
                            CmsConstants.CMS_BUNDLE)),
                        String.format(
                            "%s/redirect/?oid=%s",
                            Web.getWebappContextPath(),
                            URLEncoder.encode(
                                (String) value, "UTF-8")));
                    return link;
                } catch (UnsupportedEncodingException ex) {
                    throw new UncheckedWrapperException(ex);
                }
            }

        });

        columnModel.get(COL_EDIT).setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                if ("--".equals(value)) {
                    //No access for current user
                    return new Text("");
                } else {
                    return new Link(new Label(new GlobalizedMessage(
                        "cms.ui.cse.editLink",
                        CmsConstants.CMS_BUNDLE)),
                                    ContentItemPage.getItemURL(
                                        (Long) key,
                                        ContentItemPage.AUTHORING_TAB));
                }
            }

        });

        setEmptyView(new Label(new GlobalizedMessage("cms.ui.cse.none",
                                                     CmsConstants.CMS_BUNDLE)));
    }

}
