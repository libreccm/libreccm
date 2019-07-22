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
import org.librecms.contentsection.Asset;

import java.util.Objects;

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
    protected void updateAsset(final Asset asset,
                               final FormSectionEvent event)
        throws FormProcessException {

        Objects.requireNonNull(asset);
        Objects.requireNonNull(event);

        final PageState state = event.getPageState();

        if (!(asset instanceof Organization)) {
            throw new IllegalArgumentException(String.format(
                "The provided asset is not an instance of \"%s\" "
                    + "or a subclass,but of %s.",
                Organization.class.getName(),
                asset.getClass().getName()));
        }

        final Organization organization = (Organization) asset;

        organization.setName((String) organizationName.getValue(state));
    }

}
