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
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.assets.Image;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ItemAttachment;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AvailableImagesTableModelBuilder
    extends LockableImpl
    implements TableModelBuilder {

    private final ItemSelectionModel itemSelectionModel;
    private final StringParameter selectedLanguageParam;
    private final TextField filterField;
    private final Paginator paginator;

    public AvailableImagesTableModelBuilder(
        final ItemSelectionModel itemSelectionModel,
        final StringParameter selectedLanguageParam,
        final TextField filterField,
        final Paginator paginator) {

        this.itemSelectionModel = itemSelectionModel;
        this.selectedLanguageParam = selectedLanguageParam;
        this.filterField = filterField;
        this.paginator = paginator;
    }

    @Override
    public TableModel makeModel(final Table table,
                                final PageState state) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ImageStepController controller = cdiUtil
            .findBean(ImageStepController.class);

        final ContentItem selectedItem = itemSelectionModel
            .getSelectedItem(state);

        final List<ItemAttachment<Image>> imageAttachments = controller
        .retrieveAssignedImages(selectedItem);
        
        final List<Image> excludedImages = imageAttachments
            .stream()
            .map(attachment -> attachment.getAsset())
            .collect(Collectors.toList());

        //Paginator count from 1, JPA from 0
        final int firstResult = paginator.getFirst(state) - 1;
        final int maxResults = paginator.getPageSize(state);

        final Locale selectedLocale = new Locale((String) state.getValue(selectedLanguageParam));
        
        final List<AvailableImageTableRow> rows = controller
            .getAvailableImageRows(excludedImages,
                                   selectedLocale,
                                   (String) filterField.getValue(state),
                                   firstResult,
                                   maxResults);

        return new AvailableImagesTableModel(rows);
    }

}
