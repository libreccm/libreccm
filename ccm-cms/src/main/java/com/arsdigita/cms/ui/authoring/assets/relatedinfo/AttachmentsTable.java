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
import com.arsdigita.cms.ui.authoring.assets.AttachmentSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ItemAttachment;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AttachmentsTable extends Table {

    protected static final int COL_TITLE = 0;
    protected static final int COL_TYPE = 1;
    protected static final int COL_MOVE = 2;
    protected static final int COL_REMOVE = 3;

    private final RelatedInfoStep relatedInfoStep;
    private final ItemSelectionModel itemSelectionModel;
    private final AttachmentListSelectionModel attachmentListSelectionModel;
    private final AttachmentSelectionModel selectedAttachmentModel;
    private final AttachmentSelectionModel moveAttachmentModel;
    private final StringParameter selectedLanguageParam;

    public AttachmentsTable(
        final RelatedInfoStep relatedInfoStep,
        final ItemSelectionModel itemSelectionModel,
        final AttachmentListSelectionModel attachmentListSelectionModel,
        final AttachmentSelectionModel selectedAttachmentModel,
        final AttachmentSelectionModel moveAttachmentModel,
        final StringParameter selectedLanguageParam) {

        super();

        this.relatedInfoStep = relatedInfoStep;
        this.itemSelectionModel = itemSelectionModel;
        this.attachmentListSelectionModel = attachmentListSelectionModel;
        this.selectedAttachmentModel = selectedAttachmentModel;
        this.moveAttachmentModel = moveAttachmentModel;
        this.selectedLanguageParam = selectedLanguageParam;

        final TableColumnModel columnModel = super.getColumnModel();
        columnModel.add(new TableColumn(
            COL_TITLE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.attachment.title",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_TYPE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.attachment.type",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MOVE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.attachment.move"))));
        columnModel.add(new TableColumn(
            COL_REMOVE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.related_info_step.attachment.remove"))));

        super
            .setModelBuilder(new AttachmentsTableModelBuilder(
                itemSelectionModel,
                attachmentListSelectionModel,
                moveAttachmentModel,
                selectedLanguageParam));

        super
            .getColumn(COL_MOVE)
            .setCellRenderer(new ControlLinkCellRenderer());
        super
            .getColumn(COL_REMOVE)
            .setCellRenderer(new ControlLinkCellRenderer());

        super
            .addTableActionListener(new TableActionListener() {

                @Override
                public void cellSelected(final TableActionEvent event)
                    throws FormProcessException {

                    final PageState state = event.getPageState();

                    final TableColumn column = getColumnModel()
                        .get(event.getColumn());

                    switch (column.getModelIndex()) {
                        case COL_MOVE: {
                            if (moveAttachmentModel
                                .getSelectedKey(state) == null) {
                                moveAttachmentModel
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

                                final ItemAttachment<?> attachment
                                                            = moveAttachmentModel
                                        .getSelectedAttachment(state);

                                final Long destId = Long.parseLong(
                                    (String) event.getRowKey());

                                controller.moveAfter(
                                    attachmentListSelectionModel
                                        .getSelectedAttachmentList(state),
                                    attachment,
                                    destId);
                                moveAttachmentModel.clearSelection(state);

                            }
                            break;
                        }
                        case COL_REMOVE: {
                            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                            final RelatedInfoStepController controller = cdiUtil
                                .findBean(RelatedInfoStepController.class);
                            controller.removeAttachment(Long.parseLong(
                                (String) event.getRowKey()));
                        }
                        default:
                            throw new IllegalArgumentException(String
                                .format("Illegal column index: %d",
                                        column.getModelIndex()));
                    }

                    throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
                }

                @Override
                public void headSelected(final TableActionEvent event) {
                    //Nothing
                }

            });

    }

    private class ControlLinkCellRenderer implements TableCellRenderer {

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
