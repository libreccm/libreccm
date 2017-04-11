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

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ui.assets.AssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;
import org.librecms.assets.Bookmark;
import org.librecms.contentsection.Asset;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class BookmarkForm extends AssetForm {

    private TextArea description;
    private TextField url;

    public BookmarkForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        add(new Label(
            new GlobalizedMessage("cms.ui.assets.bookmark.description",
                                  CmsConstants.CMS_BUNDLE)));
        description = new TextArea("bookmark-description");
        add(description);

        add(new Label(new GlobalizedMessage("cms.ui.assets.bookmark.url",
                                            CmsConstants.CMS_BUNDLE)));
        url = new TextField("bookmark-url");
        add(url);

        addValidationListener(new FormValidationListener() {

            @Override
            public void validate(final FormSectionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();
                final FormData data = event.getFormData();

                try {
                    new URL((String) url.getValue(state));
                } catch (MalformedURLException ex) {
                    data.addError(new GlobalizedMessage(
                        "cms.ui.assets.bookmark.url.malformed",
                        CmsConstants.CMS_BUNDLE));
                }
            }

        });

    }

    @Override
    protected void initForm(final PageState state,
                            final Optional<Asset> selectedAsset) {

        if (selectedAsset.isPresent()) {

            if (!(selectedAsset.get() instanceof Bookmark)) {
                throw new IllegalArgumentException(String.format(
                    "The provided asset must be an instanceof of class '%s' or "
                        + "an subclass but is an instanceof of class '%s'.",
                    Bookmark.class.getName(),
                    selectedAsset.get().getClass().getName()));
            }

            final Bookmark bookmark = (Bookmark) selectedAsset.get();

            description.setValue(state,
                                 bookmark
                                     .getDescription()
                                     .getValue(getSelectedLocale(state)));
            url.setValue(state, bookmark.getUrl());

        }

    }

    @Override
    protected void showLocale(final PageState state) {
        final Optional<Asset> selectedAsset = getSelectedAsset(state);

        if (selectedAsset.isPresent()) {
            if (!(getSelectedAsset(state).get() instanceof Bookmark)) {
                throw new IllegalArgumentException(
                    "Selected asset is not a bookmark");
            }

            final Bookmark bookmark = (Bookmark) selectedAsset.get();

            description.setValue(state,
                                 bookmark
                                     .getDescription()
                                     .getValue(getSelectedLocale(state)));
        }
    }

    @Override
    protected Asset createAsset(final PageState state)
        throws FormProcessException {

        Objects.requireNonNull(state);

        final Bookmark bookmark = new Bookmark();

        bookmark
            .getDescription()
            .addValue(getSelectedLocale(state),
                      (String) description.getValue(state));

        bookmark.setUrl((String) url.getValue(state));

        return bookmark;
    }

    @Override
    protected void updateAsset(final Asset asset, final PageState state)
        throws FormProcessException {

        Objects.requireNonNull(asset);
        Objects.requireNonNull(state);

        if (!(asset instanceof Bookmark)) {
            throw new IllegalArgumentException(String.format(
                "Provided asset is not an instance of class (or sub class of) "
                    + "'%s' but is an instance of class '%s'",
                Bookmark.class.getName(),
                asset.getClass().getName()));
        }

        final Bookmark bookmark = (Bookmark) asset;

        bookmark
            .getDescription()
            .addValue(getSelectedLocale(state),
                      (String) description.getValue(state));

        bookmark.setUrl((String) url.getValue(state));
    }

}
