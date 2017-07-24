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

import java.util.Objects;
import java.util.Optional;

import org.librecms.CmsConstants;
import org.librecms.assets.SideNote;
import org.librecms.contentsection.Asset;

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
                            final Optional<SideNote> selectedAsset) {

        if (selectedAsset.isPresent()) {
            if (!(selectedAsset.get() instanceof SideNote)) {
                throw new IllegalArgumentException(String.format(
                        "The provided asset must be an instance of '%s' or"
                                + "an subclass but is an instance of '%s'",
                        SideNote.class.getName(),
                        selectedAsset.get().getClass().getName()));
            }

            final SideNote sideNote = selectedAsset.get();
            
            text.setValue(state,
                          sideNote
                                  .getText()
                                  .getValue(getSelectedLocale(state)));
        }

    }

    @Override
    protected void showLocale(final PageState state) {

        final Optional<SideNote> selectedAsset = getSelectedAsset(state);

        if (selectedAsset.isPresent()) {
            if (!(selectedAsset.get() instanceof SideNote)) {
                throw new IllegalArgumentException(String.format(
                        "The provided asset must be an instance of '%s' or"
                                + "an subclass but is an instance of '%s'",
                        SideNote.class.getName(),
                        selectedAsset.get().getClass().getName()));
            }

            final SideNote sideNote =selectedAsset.get();

            text.setValue(state,
                          sideNote
                                  .getText()
                                  .getValue(getSelectedLocale(state)));
        }
    }

    @Override
    protected Class<SideNote> getAssetClass() {
        return SideNote.class;
    }

    
    
//    @Override
//    protected Asset createAsset(final FormSectionEvent event) throws
//            FormProcessException {
//
//        Objects.requireNonNull(event);
//
//        final PageState state = event.getPageState();
//        
//        final SideNote sideNote = new SideNote();
//
//        sideNote
//                .getText()
//                .addValue(getSelectedLocale(state),
//                          (String) text.getValue(state));
//
//        return sideNote;
//    }

    @Override
    protected void updateAsset(final Asset asset, 
                               final FormSectionEvent event)
            throws FormProcessException {

        Objects.requireNonNull(asset);
        Objects.requireNonNull(event);
        
        final PageState state = event.getPageState();

        if (!(asset instanceof SideNote)) {
            throw new IllegalArgumentException(String.format(
                    "The provided asset must be an instance of '%s' or"
                            + "an subclass but is an instance of '%s'",
                    SideNote.class.getName(),
                    asset.getClass().getName()));
        }

        final SideNote sideNote = (SideNote) asset;

        sideNote
                .getText()
                .addValue(getSelectedLocale(state),
                          (String) text.getValue(state));
    }

}
