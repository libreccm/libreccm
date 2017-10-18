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
package org.librecms.pagemodel;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.CategoryRepository;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CcmObject;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.pagemodel.ComponentBuilder;
import org.libreccm.pagemodel.ComponentModelType;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

import static org.librecms.pages.PagesConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@ComponentModelType(componentModel = CategoryTreeComponent.class)
public class CategoryTreeComponentBuilder
    implements ComponentBuilder<CategoryTreeComponent> {
    
    @Inject
    private DomainRepository domainRepo;
    
    @Inject
    private CategoryManager categoryManager;
    
    @Inject
    private CategoryRepository categoryRepo;
    
    @Inject
    private ConfigurationManager confManager;
    
    @Inject
    private GlobalizationHelper globalizationHelper;
    
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Map<String, Object> buildComponent(
        final CategoryTreeComponent componentModel,
        final Map<String, Object> parameters) {
        
        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);
        
        if (!parameters.containsKey(PARAMETER_CATEGORY)) {
            throw new IllegalArgumentException(
                "The parameters map passed to this GreetingItem component does "
                    + "not include the parameter \"category\"");
        }
        
        if (!(parameters.get(PARAMETER_CATEGORY) instanceof Category)) {
            throw new IllegalArgumentException(String
                .format("The parameters map passed to this GreetingItem "
                            + "component contains the parameter \"category\", but the "
                        + "parameter is not of type \"%s\" but of type \"%s\".",
                        Category.class.getName(),
                        parameters.get(PARAMETER_CATEGORY).getClass().getName()));
        }
        
        final Category category = categoryRepo
            .findById(((CcmObject) parameters.get(PARAMETER_CATEGORY))
                .getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No category with ID %d in the database.",
            ((CcmObject) parameters.get(PARAMETER_CATEGORY)).getObjectId())));
        
        final Locale language;
        if (parameters.containsKey(PARAMETER_LANGUAGE)) {
            language = new Locale((String) parameters.get(PARAMETER_LANGUAGE));
        } else {
            final KernelConfig kernelConfig = confManager
                .findConfiguration(KernelConfig.class);
            language = kernelConfig.getDefaultLocale();
        }
        
        final Map<String, Object> result = new HashMap<>();
        if (componentModel.isShowFullTree()) {
            
            final Category rootCategory = findRootCategory(category);
            
            result.put("categoryName", rootCategory.getName());
            result.put("categoryPath",
                       categoryManager.getCategoryPath(rootCategory));
            result.put("categoryTitle",
                       rootCategory.getTitle().getValue(language));
            result.put("selected", rootCategory.equals(category));
            
            final List<Map<String, Object>> subCategories = rootCategory
                .getSubCategories()
                .stream()
                .map(current -> generateCategoryWithTree(current,
                                                         category,
                                                         language))
                .collect(Collectors.toList());
            result.put("subCategories", subCategories);
        } else {
            result.put("categoryName", category.getName());
            result.put("categoryPath",
                       categoryManager.getCategoryPath(category));
            result.put("categoryTitle", category.getTitle().getValue(language));
            
            final List<Map<String, Object>> subCategories = category
                .getSubCategories()
                .stream()
                .map(current -> generateCategory(current, language))
                .collect(Collectors.toList());
            result.put("subCategories", subCategories);
        }
        return result;
    }
    
    protected Map<String, Object> generateCategory(final Category category,
                                                   final Locale language) {
        
        final Map<String, Object> result = new HashMap<>();
        result.put("categoryName", category.getName());
        result.put("categoryPath", categoryManager.getCategoryPath(category));
        result.put("categoryTitle", category.getTitle().getValue(language));
        return result;
    }
    
    protected Map<String, Object> generateCategoryWithTree(
        final Category category,
        final Category selectedCategory,
        final Locale language) {
        
        final Map<String, Object> result = new HashMap<>();
        result.put("categoryName", category.getName());
        result.put("categoryPath", categoryManager.getCategoryPath(category));
        result.put("categoryTitle", category.getTitle().getValue(language));
        result.put("selected", selectedCategory.equals(category));
        
        if (!category.getSubCategories().isEmpty()) {
            final List<Map<String, Object>> subCategories = category
                .getSubCategories()
                .stream()
                .map(current -> generateCategoryWithTree(current,
                                                         selectedCategory,
                                                         language))
                .collect(Collectors.toList());
            result.put("subCategories", subCategories);
        }
        
        return result;
    }
    
    protected Category findRootCategory(final Category category) {
        
        if (category.getParentCategory() == null) {
            return category;
        } else {
            return findRootCategory(category.getParentCategory());
        }
    }
    
}
