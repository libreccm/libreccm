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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
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

    private Map<String, String> title;

    private Map<String, String> description;

    private List<CategorySystemOwnerRow> owners;

    private final List<Message> messages;

    public CategorySystemDetailsModel() {
        messages = new ArrayList<>();
    }

    public long getCategorySystemId() {
        return categorySystemId;
    }

    public String getUuid() {
        return uuid;
    }

    public String getDomainKey() {
        return domainKey;
    }

    public String getUri() {
        return uri;
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

    @Transactional(Transactional.TxType.REQUIRED)
    protected void setCategorySystem(final Domain domain) {
        Objects.requireNonNull(domain);

        categorySystemId = domain.getObjectId();
        uuid = domain.getUuid();
        domainKey = domain.getDomainKey();
        uri = domain.getUri();
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
