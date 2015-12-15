/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.categorization;

import java.util.List;

import org.libreccm.core.CcmObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CcmObjectRepository;

/**
 * The {@code CategoryManager} provides several helper methods for managing
 * categories, their sub categories and the objects assigned to a categories.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CategoryManager {

    private static final Logger LOGGER = LogManager.getLogger(
        CategoryManager.class);

    /**
     * A {@link CategoryRepository} instance used to interact with the database.
     */
    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private CcmObjectRepository ccmObjectRepo;

    @Inject
    private EntityManager entityManager;

    /**
     * Assigns an category to an object.
     *
     * Please note: Because the association between {@link Category} and {@code
     * CcmObject} is a many-to-many association we use an association object to
     * store the additional attributes of the association. The
     * {@link Categorization} entity is completely managed by this class.
     *
     * If either {@code object} or the {@code category} parameter are
     * {@code null} an {@link IllegalArgumentException} is thrown because
     * passing {@code null} to this method indicates a programming error.
     *
     * @param object   The object to assign to the category. Can never be
     *                 {@code null}.
     * @param category The category to which the object should be assigned. Can
     *                 never be {@code null}.
     */
    public void addObjectToCategory(final CcmObject object,
                                    final Category category) {
        if (object == null) {
            throw new IllegalArgumentException(
                "Null can't be added to a category.");
        }

        if (category == null) {
            throw new IllegalArgumentException(
                "Can't add an object to category 'null'.");
        }

        final Categorization categorization = new Categorization();
        categorization.setCategorizedObject(object);
        categorization.setCategory(category);
        categorization.setCategoryOrder(object.getCategories().size() + 1);
        categorization.setObjectOrder(category.getObjects().size() + 1);

        object.addCategory(categorization);
        category.addObject(categorization);

        entityManager.persist(categorization);
        categoryRepo.save(category);
        ccmObjectRepo.save(object);
    }

    /**
     * Removes a object from a category. Additionally to removing the object
     * from the category this method also upgrades the order of all objects
     * sorted in after the removed object so that the values are consistent
     * without gaps (which may cause trouble otherwise).
     *
     * If either the {@code object} or the {@code category} parameter are
     * {@code null} an {@link IllegalArgumentException} exception is thrown
     * because passing {@code null} to either parameter indicates a programming
     * error.
     *
     * @param object   The object to remove from the category. Can never be
     *                 {@code null}.
     * @param category The category from which the object should be removed. Can
     *                 never be {@code null}.
     *
     * @throws ObjectNotAssignedToCategoryException Thrown is the provided
     *                                              object is <em>not</em>
     * assigned to the provided category.
     */
    public void removeObjectFromCategory(final CcmObject object,
                                         final Category category)
        throws ObjectNotAssignedToCategoryException {

        if (object == null) {
            throw new IllegalArgumentException(
                "Can't remove object 'null' from a category");
        }

        if (category == null) {
            throw new IllegalArgumentException(
                "Can't remove an object from category 'null'");
        }

        final TypedQuery<Categorization> query = entityManager.createNamedQuery(
            "Categorization.find", Categorization.class);
        query.setParameter("category", category);
        query.setParameter("object", object);

        final Categorization categorization;
        try {
            categorization = query.getSingleResult();
        } catch (NoResultException ex) {
            LOGGER.warn(String.format(
                "No categorization for category %s and object %s found."
                    + "Ignoring. Orginal exception: ",
                category.toString(),
                object.toString()),
                        ex);
            return;
        }

        object.removeCategory(categorization);
        category.removeObject(categorization);
        entityManager.remove(categorization);
        categoryRepo.save(category);
        ccmObjectRepo.save(object);

        final List<Categorization> categories = object.getCategories();
        for (int i = 0; i < categories.size(); i++) {
            categories.get(i).setCategoryOrder(i);
            entityManager.merge(categories.get(i));
        }

        final List<Categorization> objects = category.getObjects();
        for (int i = 0; i < objects.size(); i++) {
            objects.get(i).setObjectOrder(i);
            entityManager.merge(objects.get(i));
        }
    }

    /**
     * Increases the value of the {@code order} property of the provided object.
     * The value of the {@code order} property of the object after the provided
     * is decreased by one. Effectively the two objects are swapped.
     *
     * @param object   The object which {@code order} property is decreased.
     *                 Can't be {@code null}.
     * @param category The category to which the object is assigned. Can't be
     *                 {@code null}.
     *
     * @throws ObjectNotAssignedToCategoryException Throws if the provided
     *                                              object is not assigned to
     *                                              the provided category.
     */
    public void increaseObjectOrder(final CcmObject object,
                                    final Category category)
        throws ObjectNotAssignedToCategoryException {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    /**
     * Decreases the value of the {@code order} property of the provided object.
     * The value of the {@code order} property of the object before the provided
     * is increased by one. Effectively the two objects are swapped.
     *
     * @param object   The object which {@code order} property is decreased.
     *                 Can't be {@code null}.
     * @param category The category to which the object is assigned. Can't be
     *                 {@code null}.
     *
     * @throws ObjectNotAssignedToCategoryException Throws if the provided
     *                                              object is not assigned to
     *                                              the provided category.
     */
    public void decreaseObjectOrder(final CcmObject object,
                                    final Category category)
        throws ObjectNotAssignedToCategoryException {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    /**
     * Swaps two objects assigned to the same category. More exactly the values
     * of the {@code order} property of the {@link Categorization} of the
     * provided objects are swapped.
     *
     * @param objectA  Th first object. Can't be {@code null}.
     * @param objectB  The second object. Can't be {@code null}.
     * @param category Can't be {@code null}. The category to which both objects
     *                 are assigned. Can't be {@code null}.
     *
     * @throws ObjectNotAssignedToCategoryException Thrown if one or both of the
     *                                              provided objects are not
     *                                              assigned to the provided
     *                                              category.
     */
    public void swapObjects(final CcmObject objectA,
                            final CcmObject objectB,
                            final Category category)
        throws ObjectNotAssignedToCategoryException {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a category as an subcategory to another category. If the category is
     * assigned to another category that association is removed.
     *
     * @param subCategory    The category to add as subcategory. Can't be
     *                       {@code null}.
     * @param parentCategory The category to which the category is added as
     *                       subcategory. Can't be {@code null}.
     */
    public void addSubCategoryToCategory(final Category subCategory,
                                         final Category parentCategory) {
        if (subCategory.getParentCategory() != null) {
            final Category oldParent = subCategory.getParentCategory();
            removeSubCategoryFromCategory(subCategory, oldParent);
        }

        final int order = parentCategory.getCategories().size() + 1;
        parentCategory.addSubCategory(subCategory);
        subCategory.setParentCategory(parentCategory);
        subCategory.setCategoryOrder(order);

        categoryRepo.save(parentCategory);
        categoryRepo.save(subCategory);
    }

    /**
     * Removes a sub category from its parent category. If the category is not
     * assigned to another parent category (or as root category to a
     * {@link Domain} the category becomes orphaned.
     *
     * @param subCategory    The subcategory to remove from the parent category.
     *                       Can't be {@code null}.
     * @param parentCategory The parent category. Can't be {@code null}.
     *
     * @throws IllegalArgumentException If the provided subcategory is not
     *                                  assigned to the provided parent
     *                                  category.
     */
    public void removeSubCategoryFromCategory(final Category subCategory,
                                              final Category parentCategory) {

        if (subCategory.getParentCategory() == null
                || !subCategory.getParentCategory().equals(parentCategory)) {
            throw new IllegalArgumentException(String.format(
                "Category %s is not a subcategory of category %s.",
                subCategory.toString(),
                parentCategory.toString()));
        }

        parentCategory.removeSubCategory(subCategory);
        subCategory.setParentCategory(null);

        final List<Category> subCategories = parentCategory.getSubCategories();
        for (int i = 0; i < subCategories.size(); i++) {
            subCategories.get(i).setCategoryOrder(i);
            categoryRepo.save(subCategories.get(i));
        }

        categoryRepo.save(parentCategory);
        categoryRepo.save(subCategory);
    }

    /**
     * Increases the value of the {@code order} property of the provided
     * category by one. The value of the {@code order} property of the following
     * objects is decreased by one. If the object is the last one this method
     * has not effect.
     *
     * @param subCategory    The category which order property is increased.
     *                       Can't be {@code null}.
     * @param parentCategory The parent category of the category. Can't be
     *                       {@code null}.
     *
     * @throws IllegalArgumentException If the provided subcategory is not a
     *                                  subcategory of the provided parent
     *                                  category.
     */
    public void increaseCategoryOrder(final Category subCategory,
                                      final Category parentCategory) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    /**
     * Decreases the value of the {@code order} property of the provided
     * category by one. The value of the {@code order} property of the
     * preceeding objects is increased by one. If the object is the last one
     * this method has not effect.
     *
     * @param subCategory    The category which order property is increased.
     *                       Can't be {@code null}.
     * @param parentCategory The parent category of the category. Can't be
     *                       {@code null}.
     *
     * @throws IllegalArgumentException If the provided subcategory is not a
     *                                  subcategory of the provided parent
     *                                  category.
     */
    public void decreaseCategoryOrder(final Category subCategory,
                                      final Category parentCategory) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    /**
     * Swaps the values of the {@code order} properties of two categories.
     *
     * @param subCategoryA   The first category. Can't be {@code null}.
     * @param subCategoryB   The second category. Can't be {@code null}.
     * @param parentCategory The parent category of both subcategories. Can't be
     *                       {@code null}.
     *
     * @throws IllegalArgumentException If one or both categories are not
     *                                  subcategories of the provided parent
     *                                  category.qq
     */
    public void swapCategories(final Category subCategoryA,
                               final Category subCategoryB,
                               final Category parentCategory) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

}
