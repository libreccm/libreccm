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
package com.arsdigita.cms.ui.authoring.assets.images;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Image;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.assets.ItemAttachmentSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ItemAttachment;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AssignedImagesTable extends Table {

    protected static final int COL_PREVIEW = 0;
    protected static final int COL_TITLE = 1;
    protected static final int COL_IMAGE_DATA = 2;
    protected static final int COL_CAPTION = 3;
    protected static final int COL_MOVE = 4;
    protected static final int COL_REMOVE = 5;

    private final ItemSelectionModel itemSelectionModel;
    private final ItemAttachmentSelectionModel moveAttachmentModel;
    private final StringParameter selectedLanguageParam;

    public AssignedImagesTable(
        final ItemSelectionModel itemSelectionModel,
        final ItemAttachmentSelectionModel moveAttachmentModel,
        final StringParameter selectedLanguageParam) {

        super();
        this.itemSelectionModel = itemSelectionModel;
        this.moveAttachmentModel = moveAttachmentModel;
        this.selectedLanguageParam = selectedLanguageParam;

        final TableColumnModel columnModel = super.getColumnModel();
        columnModel.add(new TableColumn(
            COL_PREVIEW,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.preview_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_TITLE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.title_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_IMAGE_DATA,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.properties_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_CAPTION,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.caption_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_MOVE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.move_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_REMOVE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.remove_header",
                CmsConstants.CMS_BUNDLE))));

        super
            .setModelBuilder(new AssignedImagesTableModelBuilder(
                itemSelectionModel,
                moveAttachmentModel,
                selectedLanguageParam));

        super
            .getColumn(COL_PREVIEW)
            .setCellRenderer(new ThumbnailCellRenderer());
        super
            .getColumn(COL_IMAGE_DATA)
            .setCellRenderer(new PropertiesCellRenderer());
        super
            .getColumn(COL_MOVE)
            .setCellRenderer(new MoveCellRenderer());
        super
            .getColumn(COL_REMOVE)
            .setCellRenderer(new RemoveCellRenderer());

        super.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();

                final TableColumn column = getColumnModel()
                    .get(event.getColumn());

                switch (column.getModelIndex()) {
                    case COL_MOVE:
                        if (moveAttachmentModel
                            .getSelectedAttachment(state) == null) {

                            moveAttachmentModel
                                .setSelectedKey(state,
                                                Long.parseLong(column.getKey()));
                        } else {
                            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                            final ImageStepController controller = cdiUtil
                                .findBean(ImageStepController.class);

                            final ItemAttachment<?> selectedAttachment
                                                        = moveAttachmentModel
                                    .getSelectedAttachment(state);

                            final Long destId = Long
                                .parseLong((String) event.getRowKey());

                            controller.moveAfter(selectedAttachment, destId);
                        }
                        break;
                    case COL_REMOVE:
                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final ImageStepController controller = cdiUtil
                            .findBean(ImageStepController.class);
                        controller.deleteAttachment(Long
                            .parseLong((String) event.getRowKey()));
                        break;
                }
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });

        super.setEmptyView(new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.imagestep.assigned_images.none",
            CmsConstants.CMS_BUNDLE)));

    }

    private class ThumbnailCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {
            if (value == null) {
                return new Text("");
            } else {
                final Image image = new Image((String) value, "");
                return image;
            }
        }

    }

    private class PropertiesCellRenderer implements TableCellRenderer {

        @Override
        public Component getComponent(final Table table,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final Object key,
                                      final int row,
                                      final int column) {

            @SuppressWarnings("unchecked")
            final ImageProperties properties = (ImageProperties) value;
            final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);

            panel.add(new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.properties.filename",
                CmsConstants.CMS_BUNDLE,
                new Object[]{properties.getFilename()})));
            panel.add(new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.properties.size",
                CmsConstants.CMS_BUNDLE,
                new Object[]{properties.getWidth(), properties.getHeight()})));
            panel.add(new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.assigned_images.properties.type",
                CmsConstants.CMS_BUNDLE,
                new String[]{properties.getType()})));

            return panel;
        }

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

    private class RemoveCellRenderer implements TableCellRenderer {

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
