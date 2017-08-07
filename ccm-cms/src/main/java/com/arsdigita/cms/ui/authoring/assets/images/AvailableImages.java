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
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PaginationModelBuilder;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AvailableImages extends BoxPanel {

    protected static final int COL_PREVIEW = 0;
    protected static final int COL_TITLE = 1;
    protected static final int COL_PROPERTIES = 2;
    protected static final int COL_CAPTION = 3;
    protected static final int COL_ADD = 4;

    public AvailableImages(final ImageStep imageStep,
                           final ItemSelectionModel itemSelectionModel,
                           final StringParameter selectedLanguageParam) {

        super(BoxPanel.VERTICAL);

        final Form filterForm = new Form("filter_available_images_form",
                                         new BoxPanel(BoxPanel.HORIZONTAL));
        final TextField filterField = new TextField("filter_available_images");
        filterField.setLabel(new GlobalizedMessage(
            "cms.ui.authoring.assets.imagestep.available_images.filter_label",
            CmsConstants.CMS_BUNDLE));
        final Submit submitFilter = new Submit(new GlobalizedMessage(
            "cms.ui.authoring.assets.imagestep.available_images.submit_filter",
            CmsConstants.CMS_BUNDLE));
        filterForm.add(filterField);
        filterForm.add(submitFilter);

        super.add(filterForm);

        final Paginator paginator = new Paginator(new PaginationModelBuilder() {

            @Override
            public int getTotalSize(final Paginator paginator,
                                    final PageState state) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ImageStepController controller = cdiUtil
                    .findBean(ImageStepController.class);

                return (int) controller
                    .getNumberOfAvailableImages(
                        itemSelectionModel.getSelectedItem(state),
                        (String) filterField.getValue(state));
            }

            @Override
            public boolean isVisible(final PageState state) {
                return true;
            }

        },
                                                  30);

        super.add(paginator);

        final Table table = new Table();
        final TableColumnModel columnModel = table.getColumnModel();
        columnModel.add(new TableColumn(
            COL_PREVIEW,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.available_images.preview_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_TITLE,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.available_images.title_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_PROPERTIES,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.available_images.properties_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_CAPTION,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.available_images.caption_header",
                CmsConstants.CMS_BUNDLE))));
        columnModel.add(new TableColumn(
            COL_ADD,
            new Label(new GlobalizedMessage(
                "cms.ui.authoring.assets.imagestep.available_images.select_header",
                CmsConstants.CMS_BUNDLE))));

        table.setModelBuilder(new AvailableImagesTableModelBuilder(
            itemSelectionModel, selectedLanguageParam, filterField, paginator));

        table
            .getColumn(COL_PREVIEW)
            .setCellRenderer(new ThumbnailCellRenderer());
        table
            .getColumn(COL_PROPERTIES)
            .setCellRenderer(new PropertiesCellRenderer());
        table
            .getColumn(COL_ADD)
            .setCellRenderer(new AddCellRenderer());

        table.setEmptyView(new Label(new GlobalizedMessage(
            "cms.ui.authoring.assets.imagestep.available_images.none",
            CmsConstants.CMS_BUNDLE)));

        table.addTableActionListener(new TableActionListener() {

            @Override
            public void cellSelected(final TableActionEvent event)
                throws FormProcessException {

                final long imageId = (long) event.getRowKey();

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ImageStepController controller = cdiUtil
                    .findBean(ImageStepController.class);
                controller
                    .attachImage(itemSelectionModel
                        .getSelectedItem(event.getPageState()),
                                 imageId);

                imageStep.showAssignedImages(event.getPageState());
            }

            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }

        });
        
        super.add(table);
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
                final com.arsdigita.bebop.Image image
                                                    = new com.arsdigita.bebop.Image(
                        (String) value, "");
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

    private class AddCellRenderer implements TableCellRenderer {

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
