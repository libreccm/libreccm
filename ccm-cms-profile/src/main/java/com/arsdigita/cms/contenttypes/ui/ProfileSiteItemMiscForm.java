/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
public class ProfileSiteItemMiscForm
    extends BasicItemForm
    implements FormProcessListener, FormInitListener {

    private final StringParameter selectedLangParam;

    public ProfileSiteItemMiscForm(
        final ItemSelectionModel itemModel,
        final StringParameter selectedLangParam
    ) {
        super("ProfileSiteItemEditMisc", itemModel, selectedLangParam);
        this.selectedLangParam = selectedLangParam;
    }

    @Override
    public void addWidgets() {
        add(
            new Label(
                new GlobalizedMessage(
                    "profile_site_item.ui.misc",
                    ProfileSiteConstants.BUNDLE
                )
            )
        );
        final ParameterModel miscParam = new StringParameter(
            ProfileSiteItemController.POSITION);
        final TextArea misc = new TextArea(miscParam);
        misc.setCols(80);
        misc.setRows(8);
        add(misc);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final ProfileSiteItem profile
                                  = (ProfileSiteItem) getItemSelectionModel()
                .getSelectedItem(state);

        data.put(ProfileSiteItemController.POSITION, profile.getMisc());

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

            controller.setMisc(
                profile.getObjectId(),
                (String) data.get(ProfileSiteItemController.POSITION),
                selectedLocale
            );
        }

        init(event);
    }

}
