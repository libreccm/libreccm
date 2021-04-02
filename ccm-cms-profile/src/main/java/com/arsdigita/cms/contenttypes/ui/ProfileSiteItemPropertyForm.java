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
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.assets.AssetSearchWidget;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.assets.Person;
import org.librecms.profilesite.ProfileSiteConstants;
import org.librecms.profilesite.ProfileSiteItem;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ProfileSiteItemPropertyForm
    extends BasicPageForm
    implements FormInitListener, FormProcessListener, FormValidationListener {

    public static final String ID = "PublicPersonalProfile_edit";

    private static final String OWNER_SEARCH = "ownerSearch";

    private final ItemSelectionModel itemModel;

    public ProfileSiteItemPropertyForm(
        final ItemSelectionModel itemModel,
        final ProfileSiteItemPropertiesStep step,
        final StringParameter selectedLangParam
    ) {
        super(ID, itemModel, selectedLangParam);
        this.itemModel = itemModel;
        addValidationListener(this);
    }

    @Override
    public void addWidgets() {
        super.addWidgets();

        final AssetSearchWidget ownerSearch = new AssetSearchWidget(
            OWNER_SEARCH, Person.class
        );
        add(ownerSearch);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final FormData formData = event.getFormData();
        final ProfileSiteItem profileSiteItem = (ProfileSiteItem) super
            .initBasicWidgets(event);
        formData.put(OWNER_SEARCH, profileSiteItem.getOwner());
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
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final ProfileSiteItem profileSiteItem = (ProfileSiteItem) super
            .processBasicWidgets(event);
        final FormData formData = event.getFormData();
        final Person owner = (Person) formData.get(OWNER_SEARCH);
        
        final ProfileSiteItemController controller = CdiUtil
        .createCdiUtil()
        .findBean(ProfileSiteItemController.class);
        controller.setOwner(profileSiteItem.getObjectId(), owner.getObjectId());
        
        init(event);
    }
    
    

}
