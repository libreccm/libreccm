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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;
import org.librecms.assets.Organization;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class OrganizationForm extends AbstractContactableEntityForm<Organization> {

    private TextField organizationName;

    public OrganizationForm(final AssetPane assetPane) {

        super(assetPane);
    }

    @Override
    public void initForm(final PageState state,
                         final Map<String, Object> data) {

        super.initForm(state, data);

        if (getSelectedAssetId(state) != null) {

            organizationName.setValue(
                state,
                data.get(OrganizationFormController.ORGANIZATION_NAME));
        }
    }

    @Override
    protected void addPropertyWidgets() {

        add(new Label(
            new GlobalizedMessage("cms.ui.assets.organization.name",
                                  CmsConstants.CMS_BUNDLE)));
        organizationName = new TextField("organization-name");
        add(organizationName);
    }

    @Override
    protected Class<Organization> getAssetClass() {

        return Organization.class;
    }

    @Override
    protected void showLocale(final PageState state) {

        // Organization has no localizable fields.
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        final Map<String, Object> data = new HashMap<>();
        data.put(OrganizationFormController.ORGANIZATION_NAME,
                 organizationName.getValue(state));

        return data;
    }

}
