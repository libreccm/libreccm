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
import java.util.Arrays;
import java.util.Iterator;
import org.libreccm.categorization.Category;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategoryPropertySheetModel implements PropertySheetModel {

    private static enum CategoryProperty {
        NAME
    }

    private final Category selectedCategory;
    private final Iterator<CategoryProperty> propertyIterator;
    private CategoryProperty currentProperty;

    public CategoryPropertySheetModel(final Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        propertyIterator = Arrays.asList(CategoryProperty.values()).iterator();
    }

    @Override
    public boolean nextRow() {
        if (selectedCategory == null) {
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
            final CategoryProperty property) {

        final String key = String.join(
                "",
                "ui.admin.categories.category.property_sheet.",
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
            case NAME:
                return selectedCategory.getName();
            default:
                return "";
        }
    }

}
