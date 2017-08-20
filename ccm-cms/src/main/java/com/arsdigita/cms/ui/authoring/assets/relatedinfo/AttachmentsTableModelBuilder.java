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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.assets.AttachmentListSelectionModel;
import com.arsdigita.cms.ui.authoring.assets.AttachmentSelectionModel;
import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.AttachmentList;
import org.librecms.contentsection.ContentItem;

import java.util.List;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AttachmentsTableModelBuilder
    extends LockableImpl
    implements TableModelBuilder {

    private final ItemSelectionModel itemSelectionModel;
    private final AttachmentListSelectionModel listSelectionModel;
    private final AttachmentSelectionModel moveAttachmentModel;
    private final StringParameter selectedLanguageParam;

    public AttachmentsTableModelBuilder(
        final ItemSelectionModel itemSelectionModel,
        final AttachmentListSelectionModel listSelectionModel,
        final AttachmentSelectionModel moveAttachmentModel,
        final StringParameter selectedLanguageParam) {

        super();

        this.itemSelectionModel = itemSelectionModel;
        this.listSelectionModel = listSelectionModel;
        this.moveAttachmentModel = moveAttachmentModel;
        this.selectedLanguageParam = selectedLanguageParam;
    }

    @Override
    public TableModel makeModel(final Table table, final PageState state) {

        final ContentItem selectedItem = itemSelectionModel
            .getSelectedItem(state);
        final AttachmentList list = listSelectionModel.getSelectedAttachmentList(state);
        final String selectedLanguage = (String) state
            .getValue(selectedLanguageParam);
        final Locale selectedLocale = new Locale(selectedLanguage);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final RelatedInfoStepController controller = cdiUtil
            .findBean(RelatedInfoStepController.class);

        final List<AttachmentTableRow> rows = controller
            .retrieveAttachments(selectedItem, list, selectedLocale);

        return new AttachmentsTableModel(rows, state, moveAttachmentModel);
    }

}
