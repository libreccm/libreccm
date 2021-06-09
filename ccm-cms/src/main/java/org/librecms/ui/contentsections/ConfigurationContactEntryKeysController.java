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
package org.librecms.ui.contentsections;

import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.security.AuthorizationRequired;
import org.librecms.assets.ContactEntryKey;
import org.librecms.assets.ContactEntryKeyRepository;
import org.librecms.contentsection.ContentSection;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controller for managing the available {@link ContactEntryKey}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/contactentrykeys")
public class ConfigurationContactEntryKeysController {

    /**
     * Checks admin permissions for the current content section.
     */
    @Inject
    private AdminPermissionsChecker adminPermissionsChecker;

    @Inject
    private ContactEntryKeysTableModel tableModel;

    @Inject
    private ContactEntryKeyRepository contactEntryKeyRepo;

    /**
     * Model for the current content section.
     */
    @Inject
    private ContentSectionModel sectionModel;

    /**
     * Provides common functions for controllers working with content sections.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to parse identifiers.
     */
    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private SelectedContactEntryKeyModel selectedEntryModel;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listContactEntryKeys(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerContentTypes(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        tableModel.setContactEntrykeys(
            contactEntryKeyRepo
                .findAll()
                .stream()
                .map(this::buildContactEntryKeyListItemModel)
                .collect(Collectors.toList())
        );

        return "org/librecms/ui/contentsection/configuration/contactentrykeys.xhtml";
    }

    private ContactEntryKeysTableRowModel buildContactEntryKeyListItemModel(
        final ContactEntryKey contactEntryKey
    ) {
        final ContactEntryKeysTableRowModel model
            = new ContactEntryKeysTableRowModel();

        model.setEntryKey(contactEntryKey.getEntryKey());
        model.setKeyId(contactEntryKey.getKeyId());
        model.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                contactEntryKey.getLabel()
            )
        );

        return model;
    }

    @GET
    @Path("/{contactEntryKey}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String showContactEntryKey(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("contactEntryKey") final String entryKey
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<ContactEntryKey> contactEntryKeyResult
            = contactEntryKeyRepo.findByEntryKey(entryKey);
        if (!contactEntryKeyResult.isPresent()) {
            return showContactEntryKeyNotFound(
                section, entryKey
            );
        }

        final ContactEntryKey contactEntryKey = contactEntryKeyResult.get();
        selectedEntryModel.setEntryId(contactEntryKey.getKeyId());
        selectedEntryModel.setKey(contactEntryKey.getEntryKey());

        final List<Locale> availableLocales = globalizationHelper
            .getAvailableLocales();

        selectedEntryModel.setLabels(
            contactEntryKey
                .getLabel()
                .getValues()
                .entrySet()
                .stream()
                .collect(
                    Collectors.toMap(
                        entry -> entry.getKey().toString(),
                        entry -> entry.getValue()
                    )
                )
        );
        final Set<Locale> labelLocales = contactEntryKey
            .getLabel()
            .getAvailableLocales();
        selectedEntryModel.setUnusedLabelLocales(
            availableLocales
                .stream()
                .filter(locale -> !labelLocales.contains(locale))
                .map(Locale::toString)
                .collect(Collectors.toList())
        );

        return "org/librecms/ui/contentsection/configuration/contactentrykey.xhtml";
    }

    @POST
    @Path("/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addContactEntryKey(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("contactEntryKey") final String entryKey
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        if (!contactEntryKeyRepo.findByEntryKey(entryKey).isPresent()) {
            final ContactEntryKey contactEntryKey = new ContactEntryKey();
            contactEntryKey.setEntryKey(entryKey);
            contactEntryKeyRepo.save(contactEntryKey);
        }

        return String.format(
            "redirect:/%s/configuration/contactentrykeys",
            sectionIdentifierParam
        );
    }

    @POST
    @Path("/{contactEntryKey}/@delete")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String deleteContactEntryKey(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("contactEntryKey") final String entryKey
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<ContactEntryKey> contactEntryKeyResult
            = contactEntryKeyRepo.findByEntryKey(entryKey);
        if (!contactEntryKeyResult.isPresent()) {
            return showContactEntryKeyNotFound(
                section, entryKey
            );
        }

        final ContactEntryKey contactEntryKey = contactEntryKeyResult.get();
        contactEntryKeyRepo.delete(contactEntryKey);

        return String.format(
            "redirect:/%s/configuration/contactentrykeys",
            sectionIdentifierParam
        );
    }

    @POST
    @Path("/{contactEntryKey}/label/@add")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addEntryLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("contactEntryKey") final String entryKey,
        @FormParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<ContactEntryKey> contactEntryKeyResult
            = contactEntryKeyRepo.findByEntryKey(entryKey);
        if (!contactEntryKeyResult.isPresent()) {
            return showContactEntryKeyNotFound(
                section, entryKey
            );
        }

        final ContactEntryKey contactEntryKey = contactEntryKeyResult.get();
        contactEntryKey.getLabel().addValue(new Locale(localeParam), value);
        contactEntryKeyRepo.save(contactEntryKey);

        return String.format(
            "redirect:/%s/configuration/contactentrykeys/%s",
            sectionIdentifierParam,
            entryKey
        );
    }

    @POST
    @Path("/{contactEntryKey}/label/@add/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String editEntryLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("contactEntryKey") final String entryKey,
        @PathParam("locale") final String localeParam,
        @FormParam("value") final String value
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<ContactEntryKey> contactEntryKeyResult
            = contactEntryKeyRepo.findByEntryKey(entryKey);
        if (!contactEntryKeyResult.isPresent()) {
            return showContactEntryKeyNotFound(
                section, entryKey
            );
        }

        final ContactEntryKey contactEntryKey = contactEntryKeyResult.get();
        contactEntryKey.getLabel().addValue(new Locale(localeParam), value);
        contactEntryKeyRepo.save(contactEntryKey);

        return String.format(
            "redirect:/%s/configuration/contactentrykeys/%s",
            sectionIdentifierParam,
            entryKey
        );
    }

    @POST
    @Path("/{contactEntryKey}/label/@remove/{locale}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeEntryLabel(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("contactEntryKey") final String entryKey,
        @FormParam("locale") final String localeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerLifecycles(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<ContactEntryKey> contactEntryKeyResult
            = contactEntryKeyRepo.findByEntryKey(entryKey);
        if (!contactEntryKeyResult.isPresent()) {
            return showContactEntryKeyNotFound(
                section, entryKey
            );
        }

        final ContactEntryKey contactEntryKey = contactEntryKeyResult.get();
        contactEntryKey.getLabel().removeValue(new Locale(localeParam));
        contactEntryKeyRepo.save(contactEntryKey);

        return String.format(
            "redirect:/%s/configuration/contactentrykeys/%s",
            sectionIdentifierParam,
            entryKey
        );
    }

    private String showContactEntryKeyNotFound(
        final ContentSection section, final String entryKey
    ) {
        models.put("sectionIdentifier", section.getLabel());
        models.put("entryKey", entryKey);

        return "org/librecms/ui/contentsection/configuration/contactentrykey-not-found.xhtml";
    }

}
