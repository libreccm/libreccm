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
package com.arsdigita.cms.ui.category;

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import org.libreccm.categorization.DomainOwnership;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemVersion;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class CategoryAdminController {

    @Inject
    private CategoryManager categoryManager;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<DomainOwnership> retrieveDomains(final ContentSection section) {

        Objects.requireNonNull(section);

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getDomains());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<CategoryListItem> generateSubCategoryList(
        final Category forCategory) {

        Objects.requireNonNull(forCategory);

        final Category category = categoryRepo
            .findById(forCategory.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the datbase.",
                    forCategory.getObjectId())));

        return category
            .getSubCategories()
            .stream()
            .map(this::createCategoryListItem)
            .collect(Collectors.toList());
    }

    private CategoryListItem createCategoryListItem(final Category category) {

        final CategoryListItem item = new CategoryListItem();
        item.setCategoryId(category.getObjectId());

        final String label = globalizationHelper
            .getValueFromLocalizedString(category.getTitle(), category::getName);
        item.setLabel(label);

        return item;
    }

    @Transactional
    protected List<ContentItem> retrieveAssignedContentItems(
        final Category fromCategory) {

        final Category category = categoryRepo
            .findById(fromCategory.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No Category with ID %d in the datbase.",
                    fromCategory.getObjectId())));

        return category
            .getObjects()
            .stream()
            .map(Categorization::getCategorizedObject)
            .filter(obj -> obj instanceof ContentItem)
            .map(obj -> (ContentItem) obj)
            .filter(item -> itemManager.isLive(item))
            .filter(item -> item.getVersion() == ContentItemVersion.LIVE)
            .collect(Collectors.toList());
    }

}
