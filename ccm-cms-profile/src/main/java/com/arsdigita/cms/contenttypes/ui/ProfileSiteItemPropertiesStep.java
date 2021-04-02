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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import org.librecms.assets.Person;
import org.librecms.profilesite.ProfileSiteConstants;
import org.librecms.profilesite.ProfileSiteItem;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ProfileSiteItemPropertiesStep extends SimpleEditStep {

    public static final String EDIT_SHEET_NAME = "editProfileSiteItem";

    public ProfileSiteItemPropertiesStep(
        final ItemSelectionModel itemModel,
        final AuthoringKitWizard parent,
        final StringParameter selectedLangParam
    ) {
        super(itemModel, parent, selectedLangParam);

        setDefaultEditKey(EDIT_SHEET_NAME);

        final SimpleEditStep basicProperties = new SimpleEditStep(
            itemModel, parent, selectedLangParam, EDIT_SHEET_NAME
        );
        final BasicPageForm editBasicSheet = new ProfileSiteItemPropertyForm(
            itemModel, this, selectedLangParam
        );

        basicProperties.add(
            EDIT_SHEET_NAME,
            new GlobalizedMessage(
                ProfileSiteConstants.BUNDLE,
                "profile_site.ui.edit_basic_properties"
            ),
            new WorkflowLockedComponentAccess(editBasicSheet, itemModel),
            editBasicSheet.getSaveCancelSection().getCancelButton()
        );

        basicProperties.setDisplayComponent(
            getProfileSiteItemPropertiesSheet(itemModel, selectedLangParam)
        );

        final SegmentedPanel segmentedPanel = new SegmentedPanel();
        segmentedPanel.addSegment(
            new Label(
                new GlobalizedMessage(
                    ProfileSiteConstants.BUNDLE,
                    "profile_site.ui.basic_properties"
                )
            ),
            basicProperties
        );

        setDisplayComponent(segmentedPanel);
    }

    public static Component getProfileSiteItemPropertiesSheet(
        final ItemSelectionModel itemModel,
        final StringParameter selectedLangParam
    ) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
            itemModel, false, selectedLangParam
        );

        sheet.add(
            new GlobalizedMessage(
                ProfileSiteConstants.BUNDLE, "profile_site.ui.OWNER"
            ),
            ProfileSiteItemController.OWNER,
            new OwnerFormatter()
        );

        return sheet;
    }

    private static class OwnerFormatter
        implements DomainObjectPropertySheet.AttributeFormatter {

        @Override
        public String format(
            final Object obj, final String attribute, final PageState state
        ) {
            final ProfileSiteItem profileSiteItem = (ProfileSiteItem) obj;

            final Person owner = profileSiteItem.getOwner();

            if (owner == null) {
                return "";
            } else {
                return owner.getDisplayName();
            }
        }

    }

}
