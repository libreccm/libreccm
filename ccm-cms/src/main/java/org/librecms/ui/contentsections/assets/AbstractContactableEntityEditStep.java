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

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.ContactEntry;
import org.librecms.assets.ContactEntryKey;
import org.librecms.assets.ContactEntryKeyRepository;
import org.librecms.assets.ContactEntryRepository;
import org.librecms.assets.ContactableEntity;
import org.librecms.assets.ContactableEntityManager;
import org.librecms.assets.PostalAddress;
import org.librecms.contentsection.AssetRepository;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Controller
public abstract class AbstractContactableEntityEditStep
    extends AbstractMvcAssetEditStep {

    @Inject
    private AssetRepository assetRepo;

    @Inject
    private AssetUi assetUi;

    @Inject
    private ContactEntryRepository entryRepo;

    @Inject
    private ContactEntryKeyRepository entryKeyRepo;

    @Inject
    private ContactableEntityManager contactableManager;

    @Inject
    private ContactableEntityEditStepModel editStepModel;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Context
    private HttpServletRequest request;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void init() throws ContentSectionNotFoundException,
                                 AssetNotFoundException {
        super.init();

        if (getAsset() instanceof ContactableEntity) {
            editStepModel
                .setAvailableContactEntryKeys(
                    entryKeyRepo
                        .findAll()
                        .stream()
                        .map(this::buildContactEntryKeyListItemModel)
                        .collect(
                            Collectors.toMap(
                                item -> item.getEntryKey(),
                                item -> item.getLabel()
                            )
                        )
                );
            editStepModel
                .setContactEntries(
                    getContactableEntity()
                        .getContactEntries()
                        .stream()
                        .map(this::buildContactEntryListItemModel)
                        .collect(Collectors.toList())
                );
            editStepModel.setPostalAddress(
                getContactableEntity().getPostalAddress()
            );

            final StringBuilder baseUrlBuilder = new StringBuilder();
            editStepModel.setBaseUrl(
                baseUrlBuilder
                    .append(request.getScheme())
                    .append("://")
                    .append(request.getServerName())
                    .append(addServerPortToBaseUrl())
                    .append(addContextPathToBaseUrl())
                    .toString()
            );

        } else {
            throw new AssetNotFoundException(
                assetUi.showAssetNotFound(
                    getContentSection(), getAssetPath()
                ),
                String.format(
                    "No ContactableEntity for path %s found in section %s.",
                    getAssetPath(),
                    getContentSection().getLabel()
                )
            );
        }
    }

    protected ContactableEntity getContactableEntity() {
        return (ContactableEntity) getAsset();
    }

    @POST
    @Path("/contactentries")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addContactEntry(
        @FormParam("entryKey") final String entryKeyParam,
        @FormParam("entryValue") final String entryValue
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final Optional<ContactEntryKey> entryKeyResult = entryKeyRepo
            .findByEntryKey(entryKeyParam);
        if (!entryKeyResult.isPresent()) {
            return showContactEntryKeyNoFound(entryKeyParam);
        }
        final ContactEntryKey entryKey = entryKeyResult.get();

        final ContactableEntity contactable = getContactableEntity();
        final ContactEntry contactEntry = new ContactEntry();
        contactEntry.setKey(entryKey);
        contactEntry.setValue(entryValue);
        contactableManager.addContactEntryToContactableEntity(
            contactEntry, contactable
        );

        return buildRedirectPathForStep();
    }

    @POST
    @Path("/contactentries/{index}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateContactEntry(
        @PathParam("index") final int index,
        @FormParam("entryValue") final String entryValue
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final List<ContactEntry> entries = getContactableEntity()
            .getContactEntries();
        if (index >= entries.size()) {
            return showContactEntryNotFound(index);
        }

        final ContactEntry entry = entries.get(index);
        entry.setValue(entryValue);

        entryRepo.save(entry);

        return buildRedirectPathForStep();
    }

    @POST
    @Path("/contactentries/{index}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeContactEntry(
        @PathParam("index") final int index
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final List<ContactEntry> entries = getContactableEntity()
            .getContactEntries();
        if (index >= entries.size()) {
            return showContactEntryNotFound(index);
        }
        final ContactEntry entry = entries.get(index);

        contactableManager.removeContactEntryFromContactableEntity(
            entry, getContactableEntity()
        );

        return buildRedirectPathForStep();
    }

    @POST
    @Path("/postaladdress")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String setPostalAddress(
        @FormParam("postalAddressIdentifier")
        final String postalAddressIdentifier
    ) {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        final Identifier identifier = identifierParser
            .parseIdentifier(postalAddressIdentifier);
        final Optional<PostalAddress> postalAddressResult;
        switch (identifier.getType()) {
            case ID:
                postalAddressResult = assetRepo.findById(
                    Long.parseLong(identifier.getIdentifier()),
                    PostalAddress.class
                );
                break;
            case UUID:
                postalAddressResult = assetRepo.findByUuidAndType(
                    identifier.getIdentifier(),
                    PostalAddress.class
                );
                break;
            default:
                postalAddressResult = assetRepo
                    .findByPath(identifier.getIdentifier())
                    .map(result -> (PostalAddress) result);
                break;
        }
        if (!postalAddressResult.isPresent()) {
            return showPostalAddressNotFound(postalAddressIdentifier);
        }
        final PostalAddress postalAddress = postalAddressResult.get();

        contactableManager.addPostalAddressToContactableEntity(
            postalAddress, getContactableEntity()
        );

        return buildRedirectPathForStep();
    }

    @POST
    @Path("/postaladdress/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removePostalAddress() {
        try {
            init();
        } catch (ContentSectionNotFoundException ex) {
            return ex.showErrorMessage();
        } catch (AssetNotFoundException ex) {
            return ex.showErrorMessage();
        }

        contactableManager.removePostalAddressFromContactableEntity(
            getContactableEntity().getPostalAddress(), getContactableEntity()
        );

        return buildRedirectPathForStep();
    }

    private String showContactEntryNotFound(
        final int index
    ) {
        models.put("entryIndex", index);
        return "org/librecms/ui/contentsection/assets/contactable/entry-not-found.xhtml";
    }

    private String showContactEntryKeyNoFound(final String entryKeyParam) {
        models.put("entryKeyParam", entryKeyParam);
        return "org/librecms/ui/contentsection/assets/contactable/entry-key-not-found.xhtml";
    }

    private String showPostalAddressNotFound(
        final String postalAddressIdentifier
    ) {
        models.put("postalAddressIdentifier", postalAddressIdentifier);
        return "org/librecms/ui/contentsection/assets/contactable/postal-address-not-found.xhtml";
    }

    private ContactEntryKeyListItemModel buildContactEntryKeyListItemModel(
        final ContactEntryKey entryKey
    ) {
        final ContactEntryKeyListItemModel model
            = new ContactEntryKeyListItemModel();
        model.setKeyId(entryKey.getKeyId());
        model.setEntryKey(entryKey.getEntryKey());
        model.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                entryKey.getLabel())
        );

        return model;
    }

    private ContactEntryListItemModel buildContactEntryListItemModel(
        final ContactEntry entry
    ) {
        final ContactEntryListItemModel model = new ContactEntryListItemModel();
        model.setContactEntryId(entry.getContactEntryId());
        model.setEntryKey(entry.getKey().getEntryKey());
        model.setEntryKeyId(entry.getKey().getKeyId());
        model.setEntryKeyLabel(
            globalizationHelper.getValueFromLocalizedString(
                entry.getKey().getLabel()
            )
        );
        model.setOrder(entry.getOrder());
        model.setValue(entry.getValue());

        return model;
    }

    private String addServerPortToBaseUrl() {
        if (request.getServerPort() == 80 || request.getServerPort() == 443) {
            return "";
        } else {
            return String.format(":%d", request.getServerPort());
        }
    }

    private String addContextPathToBaseUrl() {
        if (request.getServletContext().getContextPath() == null
                || request.getServletContext().getContextPath().isEmpty()) {
            return "/";
        } else {
            return request.getServletContext().getContextPath();
        }
    }

}
