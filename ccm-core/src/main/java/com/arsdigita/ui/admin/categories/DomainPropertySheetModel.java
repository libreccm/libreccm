/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.categories;

import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.categorization.Domain;

import java.util.Arrays;
import java.util.Iterator;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class DomainPropertySheetModel implements PropertySheetModel {

    private static enum DomainProperty {
        DOMAIN_KEY,
        URI,
        VERSION,
        RELEASED
    }

    private final Domain selectedDomain;
    private final Iterator<DomainProperty> propertyIterator;
    private DomainProperty currentProperty;

    public DomainPropertySheetModel(final Domain selectedDomain) {
        this.selectedDomain = selectedDomain;
        propertyIterator = Arrays.asList(DomainProperty.values()).iterator();
    }

    @Override
    public boolean nextRow() {
        if (selectedDomain == null) {
            return false;
        }

        if (propertyIterator.hasNext()) {
            currentProperty = propertyIterator.next();
            return true;
        } else {
            return false;
        }
    }

    @Override
    public String getLabel() {
        return currentProperty.toString();
    }

    private GlobalizedMessage generatedGlobalizedLabel(
        final DomainProperty property) {

        final String key = String.join(
            "",
            "ui.admin.categories.domain.property_sheet.",
            property.toString().toLowerCase());
        return new GlobalizedMessage(key, ADMIN_BUNDLE);
    }

    @Override
    public GlobalizedMessage getGlobalizedLabel() {
        return generatedGlobalizedLabel(currentProperty);
    }

    @Override
    public String getValue() {
        switch (currentProperty) {
            case DOMAIN_KEY:
                return selectedDomain.getDomainKey();
            case URI:
                return selectedDomain.getUri();
            case VERSION:
                return selectedDomain.getVersion();
            case RELEASED:
                if (selectedDomain.getReleased() == null) {
                    return "";
                } else {
                    return String.format("%tY-%<tm-%<td",
                                         selectedDomain.getReleased());
                }
            default:
                return "";
        }
    }

}
