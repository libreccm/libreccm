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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SelectedLanguageUtil;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.profilesite.ProfileSiteConstants;
import org.librecms.profilesite.ProfileSiteItem;

import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ProfileSiteItemInterestsForm
    extends BasicItemForm
    implements FormProcessListener, FormInitListener {

    private final StringParameter selectedLangParam;

    public ProfileSiteItemInterestsForm(
        final ItemSelectionModel itemModel,
        final StringParameter selectedLangParam
    ) {
        super("ProfileSiteItemEditInterests", itemModel, selectedLangParam);
        this.selectedLangParam = selectedLangParam;
    }

    @Override
    public void addWidgets() {
        add(
            new Label(
                new GlobalizedMessage(
                    "profile_site_item.ui.interests",
                    ProfileSiteConstants.BUNDLE
                )
            )
        );
        final ParameterModel interestsParam = new StringParameter(
            ProfileSiteItemController.POSITION);
        final TextArea interests = new TextArea(interestsParam);
        interests.setCols(80);
        interests.setRows(8);
        add(interests);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final ProfileSiteItem profile
                                  = (ProfileSiteItem) getItemSelectionModel()
                .getSelectedItem(state);

        data.put(ProfileSiteItemController.POSITION, profile.getInterests());

        setVisible(state, true);
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final ProfileSiteItem profile
                                  = (ProfileSiteItem) getItemSelectionModel()
                .getSelectedItem(state);

        if ((profile != null)
                && getSaveCancelSection().getSaveButton().isSelected(state)) {

            final ProfileSiteItemController controller = CdiUtil
                .createCdiUtil()
                .findBean(ProfileSiteItemController.class);

            final Locale selectedLocale = SelectedLanguageUtil.selectedLocale(
                state, selectedLangParam
            );

            controller.setInterests(
                profile.getObjectId(),
                (String) data.get(ProfileSiteItemController.POSITION),
                selectedLocale
            );
        }

        init(event);
    }

}
