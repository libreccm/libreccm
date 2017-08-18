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
package com.arsdigita.cms.ui.authoring.assets.relatedinfo;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.assets.AttachmentListSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.AttachmentList;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class RelatedInfoListTable extends Table {

    protected static final int COL_NAME = 0;
    protected static final int COL_TITLE = 1;
    protected static final int COL_DESC = 2;
    protected static final int COL_EDIT = 3;
    protected static final int COL_MOVE = 4;
    protected static final int COL_DELETE = 5;

    private final RelatedInfoStep relatedInfoStep;
    private final ItemSelectionModel itemSelectionModel;
    private final AttachmentListSelectionModel selectedListModel;
    private final AttachmentListSelectionModel moveListModel;
    private final StringParameter selectedLanguageParam;

    protected RelatedInfoListTable(
        final RelatedInfoStep relatedInfoStep,
        final ItemSelectionModel itemSelectionModel,
        final AttachmentListSelectionModel selectedListModel,
        final AttachmentListSelectionModel moveListModel,
        final StringParameter selectedLanguageParam) {

        super();
        this.relatedInfoStep = relatedInfoStep;
        this.itemSelectionModel = itemSelectionModel;
        this.selectedListModel = selectedListModel;
        this.moveListModel = moveListModel;
        this.selectedLanguageParam = selectedLanguageParam;

        final TableColumnModel columnModel = super.getColumnModel();
        columnModel.add(new TableColumn(
            COL_NAME,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.list.name",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_TITLE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.list.title",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DESC,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.list.description",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_EDIT,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.list.edit",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MOVE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.list.move",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_DELETE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.list.delete",
                CmsConstants.CMS_BUNDLE))));

        super
            .setModelBuilder(new RelatedInfoListTableModelBuilder(
                itemSelectionModel,
                moveListModel,
                selectedLanguageParam));

        super
            .getColumn(COL_EDIT)
            .setCellRenderer(new EditCellRenderer());
        super
            .getColumn(COL_MOVE)
            .setCellRenderer(new MoveCellRenderer());
        super
            .getColumn(COL_DELETE)
            .setCellRenderer(new DeleteCellRenderer());

        super
            .addTableActionListener(new TableActionListener() {

                @Override
                public void cellSelected(final TableActionEvent event)
                    throws FormProcessException {

                    final PageState state = event.getPageState();

                    final TableColumn column = getColumnModel()
                        .get(event.getColumn());

                    switch (column.getModelIndex()) {
                        case COL_EDIT:
                            selectedListModel
                                .setSelectedKey(state,
                                                Long.parseLong((String) event
                                                    .getRowKey()));
                            relatedInfoStep.showListEditForm(state);
                            break;
                        case COL_MOVE:
                            if (moveListModel.getSelectedKey(state) == null) {

                                moveListModel
                                    .setSelectedKey(state,
                                                    Long.parseLong(
                                                        (String) event
                                                            .getRowKey()));
                            } else {
                                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                                final RelatedInfoStepController controller
                                                                    = cdiUtil
                                        .findBean(
                                            RelatedInfoStepController.class);

                                final AttachmentList selectedList
                                                         = moveListModel
                                        .getSelectedAttachmentList(state);

                                final Long destId = Long
                                    .parseLong((String) event.getRowKey());

                                controller.moveAfter(itemSelectionModel
                                    .getSelectedItem(state),
                                                     selectedList,
                                                     destId);
                                moveListModel.clearSelection(state);
                            }
                            break;
                        case COL_DELETE:
                            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                            final RelatedInfoStepController controller = cdiUtil
                                .findBean(RelatedInfoStepController.class);
                            controller.deleteList(Long
                                .parseLong((String) event.getRowKey()));
                    }
                }

                @Override

                public void headSelected(final TableActionEvent event) {
                    //Nothing
                }

            });
    }

    private class MoveCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            return new ControlLink((Component) value);
        }

    }

    private class EditCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            return new ControlLink((Component) value);
        }

    }

    private class DeleteCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            return new ControlLink((Component) value);
        }

    }

}
