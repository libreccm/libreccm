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

import com.arsdigita.cms.ui.assets.AssetPane;

import org.librecms.assets.Bookmark;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class BookmarkForm extends AbstractBookmarkForm<Bookmark> {

//    private TextArea description;
//    private TextField url;

    public BookmarkForm(final AssetPane assetPane) {
        super(assetPane);
    }

//    @Override
//    protected void addWidgets() {
//
//        add(new Label(
//            new GlobalizedMessage("cms.ui.assets.bookmark.description",
//                                  CmsConstants.CMS_BUNDLE)));
//        description = new TextArea("bookmark-description");
//        add(description);
//
//        add(new Label(new GlobalizedMessage("cms.ui.assets.bookmark.url",
//                                            CmsConstants.CMS_BUNDLE)));
//        url = new TextField("bookmark-url");
//        add(url);
//
//        addValidationListener(new FormValidationListener() {
//
//            @Override
//            public void validate(final FormSectionEvent event)
//                throws FormProcessException {
//
//                final PageState state = event.getPageState();
//                final FormData data = event.getFormData();
//
//                try {
//                    new URL((String) url.getValue(state));
//                } catch (MalformedURLException ex) {
//                    data.addError(new GlobalizedMessage(
//                        "cms.ui.assets.bookmark.url.malformed",
//                        CmsConstants.CMS_BUNDLE));
//                }
//            }
//
//        });
//
//    }
//
//    @Override
//    protected void initForm(final PageState state,
//                            final Optional<Bookmark> selectedAsset) {
//
//        if (selectedAsset.isPresent()) {
//
//            if (!(selectedAsset.get() instanceof Bookmark)) {
//                throw new IllegalArgumentException(String.format(
//                    "The provided asset must be an instanceof of class '%s' or "
//                        + "an subclass but is an instanceof of class '%s'.",
//                    Bookmark.class.getName(),
//                    selectedAsset.get().getClass().getName()));
//            }
//
//            final Bookmark bookmark = selectedAsset.get();
//
//            description.setValue(state,
//                                 bookmark
//                                     .getDescription()
//                                     .getValue(getSelectedLocale(state)));
//            url.setValue(state, bookmark.getUrl());
//
//        }
//
//    }
//
//    @Override
//    protected void showLocale(final PageState state) {
//        final Optional<Bookmark> selectedAsset = getSelectedAsset(state);
//
//        if (selectedAsset.isPresent()) {
//            if (!(getSelectedAsset(state).get() instanceof Bookmark)) {
//                throw new IllegalArgumentException(
//                    "Selected asset is not a bookmark");
//            }
//
//            final Bookmark bookmark = selectedAsset.get();
//
//            description.setValue(state,
//                                 bookmark
//                                     .getDescription()
//                                     .getValue(getSelectedLocale(state)));
//        }
//    }

    @Override
    @SuppressWarnings("unchecked")
    protected Class<Bookmark> getAssetClass() {
        return Bookmark.class;
    }
    
//    @Override
//    protected Asset createAsset(final FormSectionEvent event)
//        throws FormProcessException {
//
//        Objects.requireNonNull(event);
//        
//        final PageState state = event.getPageState();
//
//        final Bookmark bookmark = new Bookmark();
//
//        updateData(bookmark, state);
//
//        return bookmark;
//    }

//    protected void updateData(final Bookmark bookmark,
//                              final PageState state) {
//        bookmark
//            .getDescription()
//            .addValue(getSelectedLocale(state),
//                      (String) description.getValue(state));
//        
//        bookmark.setUrl((String) url.getValue(state));
//    }
//    
//    
//    @Override
//    protected void updateAsset(final Asset asset, 
//                               final FormSectionEvent event)
//        throws FormProcessException {
//
//        Objects.requireNonNull(asset);
//        Objects.requireNonNull(event);
//        
//        final PageState state = event.getPageState();
//
//        if (!(asset instanceof Bookmark)) {
//            throw new IllegalArgumentException(String.format(
//                "Provided asset is not an instance of class (or sub class of) "
//                    + "'%s' but is an instance of class '%s'",
//                Bookmark.class.getName(),
//                asset.getClass().getName()));
//        }
//
//        final Bookmark bookmark = (Bookmark) asset;
//
//        updateData(bookmark, state);
//    }

}
