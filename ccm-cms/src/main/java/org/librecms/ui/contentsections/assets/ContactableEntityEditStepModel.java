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

import org.librecms.assets.PostalAddress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsContactableEditStepModel")
public class ContactableEntityEditStepModel {

    private Map<String, String> availableContactEntryKeys;

    private List<ContactEntryListItemModel> contactEntries;

    private PostalAddress postalAddress;

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

    protected void setPostalAddress(final PostalAddress postalAddress) {
        this.postalAddress = postalAddress;
    }

}
