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
import com.arsdigita.cms.ui.assets.AbstractAssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;
import org.librecms.assets.Bookmark;
import org.librecms.contentsection.Asset;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

/**
 * Abstract base form for all forms for {@link BookmarkAsset}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> Type of the Bookmark Asset.
 */
public abstract class AbstractBookmarkForm<T extends Bookmark>
    extends AbstractAssetForm<T> {

    private TextArea description;

    private TextField url;

    public AbstractBookmarkForm(final AssetPane assetPane) {
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
                            final Map<String, Object> data) {

        super.initForm(state, data);
        
        if (!data.isEmpty()) {

            description
                .setValue(
                    state,
                    data.get(AbstractBookmarkFormController.DESCRIPTION));
            url.setValue(state, data.get(AbstractBookmarkFormController.URL));

        }
    }

    @Override
    protected void showLocale(final PageState state) {

        final Long selectedAssetId = getSelectedAssetId(state);

        if (selectedAssetId != null) {

            final Map<String, Object> data = getController()
                .getAssetData(selectedAssetId,
                              getAssetClass(),
                              getSelectedLocale(state));

            description.setValue(
                state,
                data.get(AbstractBinaryAssetFormController.DESCRIPTION));
        }
    }

    protected void updateData(final Bookmark bookmark,
                              final PageState state) {
        bookmark
            .getDescription()
            .addValue(getSelectedLocale(state),
                      (String) description.getValue(state));

        bookmark.setUrl((String) url.getValue(state));
    }

}
