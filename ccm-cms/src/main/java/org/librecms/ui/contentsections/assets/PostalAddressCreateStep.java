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
package org.librecms.ui.contentsections.assets;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.PostalAddress;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsPostalAddressCreateStep")
public class PostalAddressCreateStep
    extends AbstractMvcAssetCreateStep<PostalAddress> {

    private static final String FORM_PARAMS_NAME = "name";

    private static final String FORM_PARAMS_TITLE = "title";
    
    private static final String FORM_PARAM_INITIAL_LOCALE = "locale";

    @Inject
    private AssetManager assetManager;
    
    @Inject
    private AssetRepository assetRepo;
    
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Override
    public String showCreateStep() {
        return "/org/librecms/ui/assets/postaladdress/create-postaladdress.xhtml";
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String createAsset(final Map<String, String[]> formParams) {
        if (!formParams.containsKey(FORM_PARAMS_NAME)
                || formParams.get(FORM_PARAMS_NAME) == null
                || formParams.get(FORM_PARAMS_NAME).length == 0) {
            addMessage(
                "danger",
                globalizationHelper
                    .getLocalizedTextsUtil(getBundle())
                    .getText("postaladdress.createstep.name.error.missing")
            );
            return showCreateStep();
        }
        
        final String name = formParams.get(FORM_PARAMS_NAME)[0];
        if (!name.matches("^([a-zA-Z0-9_-]*)$")) {
            addMessage(
                "danger",
                globalizationHelper
                .getLocalizedTextsUtil(getBundle())
                .getText("postaladdress.createstep.name.error.invalid")
            );
            return showCreateStep();
        }

        if (!formParams.containsKey(FORM_PARAMS_TITLE)
                || formParams.get(FORM_PARAMS_TITLE) == null
                || formParams.get(FORM_PARAMS_TITLE).length == 0) {
            addMessage(
                "danger",
                globalizationHelper
                    .getLocalizedTextsUtil(getBundle())
                    .getText("postaladdress.createstep.title.error.missing")
            );
            return showCreateStep();
        }
        final String title = formParams.get(FORM_PARAMS_TITLE)[0];
        
        if (!formParams.containsKey(FORM_PARAM_INITIAL_LOCALE)
                || formParams.get(FORM_PARAM_INITIAL_LOCALE) == null
                || formParams.get(FORM_PARAM_INITIAL_LOCALE).length == 0) {
            addMessage(
                "danger",
                globalizationHelper.getLocalizedTextsUtil(
                    getBundle()
                ).getText("createstep.initial_locale.error.missing")
            );
            return showCreateStep();
        }
        final Locale locale = new Locale(
            formParams.get(FORM_PARAM_INITIAL_LOCALE)[0]
        );
        
        final String address = formParams.get("address")[0];
        final String postalCode = formParams.get("postalCode")[0];
        final String city = formParams.get("city")[0];
        final String state = formParams.get("state")[0];
        final String isoCountryCode = formParams.get("isoCountryCode")[0];
        
        final PostalAddress postalAddress = assetManager.createAsset(
            name, 
            title,
            locale, 
            getFolder(), 
            PostalAddress.class
        );
        
        postalAddress.setAddress(address);
        postalAddress.setPostalCode(postalCode);
        postalAddress.setCity(city);
        postalAddress.setState(state);
        postalAddress.setIsoCountryCode(isoCountryCode);
        
        assetRepo.save(postalAddress);
        
        return String.format(
            "redirect:/%s/assets/%s/%s/@postaladdress-edit",
            getContentSectionLabel(),
            getFolderPath(),
            name
        );
    }
    

    @Override
    public String getAssetType() {
        return PostalAddress.class.getName();
    }

    @Override
    public String getLabel() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("postal_address.label");
    }

    @Override
    public String getDescription() {
        return globalizationHelper
            .getLocalizedTextsUtil(getBundle())
            .getText("postal_address.description");
    }

    @Override
    public String getBundle() {
        return MvcAssetStepsConstants.BUNDLE;
    }

}
