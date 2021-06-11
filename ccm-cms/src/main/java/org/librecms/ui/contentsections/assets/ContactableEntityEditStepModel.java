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
import org.librecms.assets.PostalAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.ServletContext;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsContactableEditStepModel")
public class ContactableEntityEditStepModel {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ServletContext servletContext;

    private Map<String, String> availableContactEntryKeys;

    private String baseUrl;

    private List<ContactEntryListItemModel> contactEntries;

    private PostalAddress postalAddress;

    public String getContextPath() {
        return servletContext.getContextPath();
    }

    public String getBaseUrl() {
        return baseUrl;
    }
    
    protected void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public Map<String, String> getAvailableContactEntryKeys() {
        return Collections.unmodifiableMap(availableContactEntryKeys);
    }

    public void setAvailableContactEntryKeys(
        final Map<String, String> availableContactEntryKeys
    ) {
        this.availableContactEntryKeys = new HashMap<>(
            availableContactEntryKeys
        );
    }

    public List<ContactEntryListItemModel> getContactEntries() {
        return Collections.unmodifiableList(contactEntries);
    }

    protected void setContactEntries(
        final List<ContactEntryListItemModel> contactEntries
    ) {
        this.contactEntries = new ArrayList<>(contactEntries);
    }

    public PostalAddress getPostalAddress() {
        return postalAddress;
    }

    public String getPostalAddressType() {
        return PostalAddress.class.getName();
    }

    protected void setPostalAddress(final PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
    }

    public String getPostalAddressCountry() {
        return Optional
            .ofNullable(postalAddress)
            .map(PostalAddress::getIsoCountryCode)
            .map(code -> new Locale("", code))
            .map(
                locale -> locale.getDisplayCountry(
                    globalizationHelper.getNegotiatedLocale()
                )
            ).orElse("");
    }

}
