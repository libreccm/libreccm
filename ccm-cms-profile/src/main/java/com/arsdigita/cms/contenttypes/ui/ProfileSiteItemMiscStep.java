/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicItemForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import org.librecms.profilesite.ProfileSiteConstants;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ProfileSiteItemMiscStep extends SimpleEditStep {

    private String EDIT_POSITION_SHEET_NAME = "editMisc";

    public ProfileSiteItemMiscStep(
        final ItemSelectionModel itemModel,
        final AuthoringKitWizard parent,
        final StringParameter selectedLangParam
    ) {
        this(itemModel, parent, selectedLangParam, null);
    }

    public ProfileSiteItemMiscStep(
        final ItemSelectionModel itemModel,
        final AuthoringKitWizard parent,
        final StringParameter selectedLangParam,
        final String prefix
    ) {
        super(itemModel, parent, selectedLangParam, prefix);

        final BasicItemForm editMiscForm = new ProfileSiteItemMiscForm(
            itemModel, selectedLangParam
        );
        add(
            EDIT_POSITION_SHEET_NAME,
            new GlobalizedMessage(
                "profile_site_site.ui.misc.edit",
                ProfileSiteConstants.BUNDLE
            ),
            new WorkflowLockedComponentAccess(parent, itemModel),
            editMiscForm.getSaveCancelSection().getCancelButton()
        );

        setDisplayComponent(getProfileSiteItemMiscSheet(
            itemModel, selectedLangParam)
        );
    }

    public static final Component getProfileSiteItemMiscSheet(
        final ItemSelectionModel itemModel,
        final StringParameter selectedLangParam
    ) {
        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
            itemModel, false, selectedLangParam
        );

        sheet.add(
            new GlobalizedMessage(
                "profile_site_item.ui.misc",
                ProfileSiteConstants.BUNDLE
            ),
            ProfileSiteItemController.POSITION
        );

        return sheet;
    }

}
