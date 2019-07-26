/*
 * Copyright (C) 2019 LibreCCM Foundation.
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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ui.assets.AbstractAssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.assets.PostalAddress;

import java.util.HashMap;
import java.util.Map;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PostalAddressForm extends AbstractAssetForm<PostalAddress> {

    private TextArea addressArea;
    private TextField postalCodeField;
    private TextField cityField;
    private TextField stateField;
    private TextField isoCountryCodeField;

    public PostalAddressForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        super.addWidgets();

        addressArea = new TextArea("address");
        addressArea.setLabel(new GlobalizedMessage(
            "cms.ui.authoring.assets.postaladdress.address", CMS_BUNDLE));
        addressArea.setCols(80);
        addressArea.setRows(10);
        add(addressArea);

        postalCodeField = new TextField("postalCode");
        postalCodeField.setLabel(new GlobalizedMessage(
            "cms.ui.authoring.assets.postaladdress.postalcode", CMS_BUNDLE));
        add(postalCodeField);

        cityField = new TextField("city");
        cityField.setLabel(new GlobalizedMessage(
            "cms.ui.authoring.assets.postaladdress.city", CMS_BUNDLE));
        add(cityField);

        stateField = new TextField("state");
        stateField.setLabel(new GlobalizedMessage(
            "cms.ui.authoring.assets.postaladdress.state", CMS_BUNDLE));
        add(stateField);
    }

    @Override
    protected Class<PostalAddress> getAssetClass() {
        return PostalAddress.class;
    }

    @Override
    protected void showLocale(final PageState state) {
        // Nothing
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        final Map<String, Object> data = new HashMap<>();

        data.put(PostalAddressFormController.ADDRESS,
                 addressArea.getValue(state));
        data.put(PostalAddressFormController.CITY,
                 cityField.getValue(state));
        data.put(PostalAddressFormController.POSTAL_CODE,
                 postalCodeField.getValue(state));
        data.put(PostalAddressFormController.STATE,
                 stateField.getValue(state));

        return data;
    }

}
