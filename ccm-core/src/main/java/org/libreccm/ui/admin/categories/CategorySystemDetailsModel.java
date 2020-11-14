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
import org.libreccm.ui.Message;

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CategorySystemDetailsModel")
public class CategorySystemDetailsModel {

    private long categorySystemId;

    private String uuid;

    private String domainKey;

    private String uri;

    private String version;

    private String released;

    private Map<String, String> title;

    private Map<String, String> description;

    private List<CategorySystemOwnerRow> owners;

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

    public Map<String, String> getDescription() {
        return Collections.unmodifiableMap(description);
    }

    public List<CategorySystemOwnerRow> getOwners() {
        return Collections.unmodifiableList(owners);
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

        owners = domain
            .getOwners()
            .stream()
            .map(this::buildOwnerRow)
            .sorted()
            .collect(Collectors.toList());
    }

    private CategorySystemOwnerRow buildOwnerRow(
        final DomainOwnership ownership
    ) {
        final CategorySystemOwnerRow ownerRow = new CategorySystemOwnerRow();
        ownerRow.setOwnershipId(ownership.getOwnershipId());
        ownerRow.setUuid(ownership.getUuid());
        ownerRow.setContext(ownership.getContext());
        ownerRow.setOwnerOrder(ownership.getOwnerOrder());
        ownerRow.setOwnerAppName(ownership.getOwner().getDisplayName());

        return ownerRow;
    }

}
