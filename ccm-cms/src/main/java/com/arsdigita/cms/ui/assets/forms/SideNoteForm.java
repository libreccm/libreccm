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
package com.arsdigita.cms.ui.assets.forms;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.cms.ui.assets.AbstractAssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;
import org.librecms.assets.SideNote;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SideNoteForm extends AbstractAssetForm<SideNote> {

    private TextArea text;

    public SideNoteForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        add(new Label(new GlobalizedMessage("cms.ui.assets.sidenote.text",
                                            CmsConstants.CMS_BUNDLE)));
        text = new TextArea("sidenote-text");
        add(text);
    }

    @Override
    protected void initForm(final PageState state,
                            final Map<String, Object> data) {

        if (getSelectedAssetId(state) != null) {

            text.setValue(state, data.get(SideNoteFormController.TEXT));
        }

    }

    @Override
    protected void showLocale(final PageState state) {

        if (getSelectedAssetId(state) != null) {

            final Long selectedAssetId = getSelectedAssetId(state);
            final Map<String, Object> data = getController()
                .getAssetData(selectedAssetId,
                              SideNote.class,
                              getSelectedLocale(state));

            text.setValue(state, data.get(SideNoteFormController.TEXT));
        }
    }

    @Override
    protected Class<SideNote> getAssetClass() {
        return SideNote.class;
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event)
        throws FormProcessException {

        final Map<String, Object> data = new HashMap<>();
        final PageState state = event.getPageState();

        data.put(SideNoteFormController.TEXT, text.getValue(state));

        return data;
    }

}
