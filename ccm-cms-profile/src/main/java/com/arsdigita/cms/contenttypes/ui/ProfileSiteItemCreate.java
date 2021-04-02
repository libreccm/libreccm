/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.assets.AssetSearchWidget;
import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.PageCreateForm;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.assets.Person;
import org.librecms.contentsection.ContentItemInitializer;
import org.librecms.profilesite.ProfileSiteConstants;
import org.librecms.profilesite.ProfileSiteItem;

import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ProfileSiteItemCreate extends PageCreateForm {

    private final static String OWNER_SEARCH = "owner";

    private AssetSearchWidget ownerSearch;

    public ProfileSiteItemCreate(
        final ItemSelectionModel itemModel,
        final CreationSelector creationSelector,
        final StringParameter selectedLanguageParam
    ) {
        super(itemModel, creationSelector, selectedLanguageParam);
    }

    @Override
    public void addWidgets() {
        ownerSearch = new AssetSearchWidget(OWNER_SEARCH, Person.class);
        ownerSearch.setLabel(
            new GlobalizedMessage(
                "profile_site.owner.label", ProfileSiteConstants.BUNDLE
            )
        );
        add(ownerSearch);
    }

    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {
        super.validate(event);
        final FormData formData = event.getFormData();

        if (!formData.containsKey(OWNER_SEARCH)
                || formData.get(OWNER_SEARCH) == null) {
            formData.addError(
                new GlobalizedMessage(
                    "profile_site.owner.not_selected",
                    ProfileSiteConstants.BUNDLE
                )
            );
        }
    }

    @Override
    protected ContentItemInitializer<ProfileSiteItem> getItemInitializer(
        final FormData formData, final PageState state
    ) {
        return (item) -> item.setOwner((Person) formData.get(OWNER_SEARCH));
    }

    
}
