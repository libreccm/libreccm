/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
