/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.lifecycle;

import org.hibernate.boot.archive.scan.spi.ClassDescriptor;
import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ItemLifecycleAdminController {

    @Inject
    private ContentItemRepository itemRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isAssignedToAbstractCategory(final ContentItem item) {

        final ContentItem contentItem = itemRepo
            .findById(item.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No ContentItem with ID %d in the database.",
                    item.getObjectId())));

        final long count = contentItem
            .getCategories()
            .stream()
            .map(Categorization::getCategory)
            .filter(Category::isAbstractCategory)
            .count();

        return count > 0;
    }

}
