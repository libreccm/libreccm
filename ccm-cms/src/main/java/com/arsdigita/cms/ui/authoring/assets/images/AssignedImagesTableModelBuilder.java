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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.assets.ItemAttachmentSelectionModel;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItem;

import java.util.List;
import java.util.Locale;

/**
 * Creates the {@link AssignedImagesTableModel} which is used in
 * {@link ImageStep} for the table of assigned images. This
 * {@link TableModelBuilder} takes the selected item and retrieves all retrieved
 * images from the item. For each assigned image an instance
 * {@link AssignedImageTableRow} is created. The resulting list (which might be
 * empty) is than used to create the image model. This also means that all
 * interaction with the database is done by this {@link TableModelBuilder}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AssignedImagesTableModelBuilder extends LockableImpl implements
    TableModelBuilder {

    private final ItemSelectionModel itemSelectionModel;
    private final ItemAttachmentSelectionModel moveAttachmentModel;
    private final StringParameter selectedLanguageParam;

    /**
     * Constructor for this {@link TableModelBuilder}.
     *
     * @param itemSelectionModel    The model used to determine the selected
     *                              {@link ContentItem}.
     * @param selectedLanguageParam Parameter used to determine the selected
     *                              language variant of the selected
     *                              {@link ContentItem}.
     */
    protected AssignedImagesTableModelBuilder(
        final ItemSelectionModel itemSelectionModel,
        final ItemAttachmentSelectionModel moveAttachmentModel,
        final StringParameter selectedLanguageParam) {

        this.itemSelectionModel = itemSelectionModel;
        this.moveAttachmentModel = moveAttachmentModel;
        this.selectedLanguageParam = selectedLanguageParam;
    }

    @Override
    public TableModel makeModel(final Table table, final PageState state) {

        final ContentItem selectedItem = itemSelectionModel
            .getSelectedItem(state);
        final String selectedLanguage = (String) state
            .getValue(selectedLanguageParam);
        final Locale selectedLocale = new Locale(selectedLanguage);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ImageStepController controller = cdiUtil
            .findBean(ImageStepController.class);

        final List<AssignedImageTableRow> rows = controller
            .retrieveAssignedImages(selectedItem, selectedLocale);

        return new AssignedImagesTableModel(rows,
                                            state,
                                            moveAttachmentModel);
    }

}
