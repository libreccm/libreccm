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
import org.libreccm.categorization.DomainRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;

import java.time.format.DateTimeFormatter;
import java.util.List;
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
@Named("CategorySystemsTableModel")
public class CategorySystemsTableModel {

    @Inject
    private DomainRepository domainRepository;

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional
    public List<CategorySystemTableRow> getCategorySystems() {
        return domainRepository
            .findAll()
            .stream()
            .map(this::buildTableRow)
            .sorted()
            .collect(Collectors.toList());
    }

    private CategorySystemTableRow buildTableRow(final Domain domain) {
        final CategorySystemTableRow row = new CategorySystemTableRow();
        
        row.setDomainId(domain.getObjectId());
        row.setDomainKey(domain.getDomainKey());
        row.setUri(domain.getUri());
        row.setVersion(domain.getVersion());
        row.setReleased(
            DateTimeFormatter.ISO_DATE_TIME.format(
                domain.getReleased().toInstant()
            )
        );
        row.setTitle(
            domain
                .getTitle()
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
        return row;
    }
}
