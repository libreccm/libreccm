/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.categories;

import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.ui.Message;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CategorySystemDetailsModel")
public class CategorySystemDetailsModel {

    @Inject
    private ApplicationRepository applicationRepository;

    @Inject
    private GlobalizationHelper globalizationHelper;

    private long categorySystemId;

    private String uuid;

    private String domainKey;

    private String uri;

    private String version;

    private String released;

    private Map<String, String> title;

    private List<String> unusedTitleLocales;

    private Map<String, String> description;

    private List<String> unusedDescriptionLocales;

    private List<CategorySystemOwnerRow> owners;

    private List<CategorySystemOwnerOption> ownerOptions;
    
    private String rootIdentifier;
    
    private List<CategoryTableRow> categories;

    private final List<Message> messages;

    private Set<String> invalidFields;

    public CategorySystemDetailsModel() {
        messages = new ArrayList<>();
        invalidFields = new HashSet<>();
    }

    public long getCategorySystemId() {
        return categorySystemId;
    }

    protected void setCategorySystemId(final long categorySystemId) {
        this.categorySystemId = categorySystemId;
    }

    public String getIdentifier() {
        return String.format("ID-%d", categorySystemId);
    }
    
    public String getRootIdentifier() {
        return String.format("UUID-%s", rootIdentifier);
    }
    
    protected void setRootIdentifier(final String rootIdentifier) {
        this.rootIdentifier = rootIdentifier;
    }

    public String getUuid() {
        return uuid;
    }

    protected void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getDomainKey() {
        return domainKey;
    }

    protected void setDomainKey(final String domainKey) {
        this.domainKey = domainKey;
    }

    public String getUri() {
        return uri;
    }

    protected void setUri(final String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return version;
    }

    protected void setVersion(final String version) {
        this.version = version;
    }

    public String getReleased() {
        return released;
    }

    protected void setReleased(final String released) {
        this.released = released;
    }

    protected void setReleased(final LocalDate released) {
        if (released == null) {
            this.released = "";
        } else {
            this.released = DateTimeFormatter.ISO_DATE.format(released);
        }
    }

    public Map<String, String> getTitle() {
        return Collections.unmodifiableMap(title);
    }

    public List<String> getUnusedTitleLocales() {
        return Collections.unmodifiableList(unusedTitleLocales);
    }

    public boolean hasUnusedTitleLocales() {
        return !unusedTitleLocales.isEmpty();
    }

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public List<String> getUnusedDescriptionLocales() {
        return Collections.unmodifiableList(unusedDescriptionLocales);
    }

    public boolean hasUnusedDescriptionLocales() {
        return !unusedDescriptionLocales.isEmpty();
    }

    public List<CategorySystemOwnerRow> getOwners() {
        return Collections.unmodifiableList(owners);
    }

    public List<CategorySystemOwnerOption> getOwnerOptions() {
        return Collections.unmodifiableList(ownerOptions);
    }

    public List<CategoryTableRow> getCategories() {
        return Collections.unmodifiableList(categories);
    }
    
    public boolean isNew() {
        return categorySystemId == 0;
    }

    public List<Message> getMessages() {
        return Collections.unmodifiableList(messages);
    }

    public void addMessage(final Message message) {
        messages.add(message);
    }

    public Set<String> getInvalidFields() {
        return Collections.unmodifiableSet(invalidFields);
    }

    protected void addInvalidField(final String invalidField) {
        invalidFields.add(invalidField);
    }

    protected void setInvalidFields(final Set<String> invalidFields) {
        this.invalidFields = new HashSet<>(invalidFields);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected void setCategorySystem(final Domain domain) {
        Objects.requireNonNull(domain);

        categorySystemId = domain.getObjectId();
        uuid = domain.getUuid();
        domainKey = domain.getDomainKey();
        uri = domain.getUri();
        version = domain.getVersion();
        if (domain.getReleased() == null) {
            released = "";
        } else {
            released = DateTimeFormatter.ISO_DATE
                .withZone(ZoneOffset.systemDefault())
                .format(domain.getReleased());
        }
        final List<Locale> availableLocales = globalizationHelper
            .getAvailableLocales();
        title = domain
            .getTitle()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
        final Set<Locale> titleLocales = domain
            .getTitle()
            .getAvailableLocales();
        unusedTitleLocales = availableLocales
            .stream()
            .filter(locale -> !titleLocales.contains(locale))
            .map(Locale::toString)
            .sorted()
            .collect(Collectors.toList());

        description = domain
            .getDescription()
            .getValues()
            .entrySet()
            .stream()
            .collect(
                Collectors.toMap(
                    entry -> entry.getKey().toString(),
                    entry -> entry.getValue()
                )
            );
        final Set<Locale> descriptionLocales = domain
            .getDescription()
            .getAvailableLocales();
        unusedDescriptionLocales = availableLocales
            .stream()
            .filter(locale -> !descriptionLocales.contains(locale))
            .map(Locale::toString)
            .sorted()
            .collect(Collectors.toList());

        owners = domain
            .getOwners()
            .stream()
            .map(this::buildOwnerRow)
            .sorted()
            .collect(Collectors.toList());

        final List<CcmApplication> ownerApplications = domain
            .getOwners()
            .stream()
            .map(DomainOwnership::getOwner)
            .collect(Collectors.toList());

        ownerOptions = applicationRepository
            .findAll()
            .stream()
            .filter(application -> !ownerApplications.contains(application))
            .map(CategorySystemOwnerOption::new)
            .sorted()
            .collect(Collectors.toList());
        

        rootIdentifier = String.format("UUID-%s", domain.getRoot().getUuid());
        
        categories = domain
            .getRoot()
            .getSubCategories()
            .stream()
            .map(CategoryTableRow::new)
            .sorted()
            .collect(Collectors.toList());
    }

    private CategorySystemOwnerRow buildOwnerRow(
        final DomainOwnership ownership
    ) {
        final CategorySystemOwnerRow ownerRow = new CategorySystemOwnerRow();
        ownerRow.setOwnershipId(ownership.getOwnershipId());
        ownerRow.setUuid(ownership.getOwner().getUuid());
        ownerRow.setContext(ownership.getContext());
        ownerRow.setOwnerOrder(ownership.getOwnerOrder());
        if (ownership.getOwner().getDisplayName() == null) {
            ownerRow.setOwnerAppName(ownership.getOwner().getApplicationType());
        } else {
            ownerRow.setOwnerAppName(ownership.getOwner().getDisplayName());
        }

        return ownerRow;
    }

}
