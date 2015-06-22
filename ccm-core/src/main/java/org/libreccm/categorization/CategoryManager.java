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

import org.libreccm.core.CcmObject;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * The {@code CategoryManager} provides several helper methods for managing
 * categories, their sub categories and the objects assigned to a categories.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CategoryManager {

    /**
     * A {@link CategoryRepository} instance used to interact with the database.
     */
    @Inject
    private transient CategoryRepository categoryRepo;

    /**
     * Assigns an category to an object. The object is added at the position
     * specified by the {@code order} parameter. If that position is already
     * occupied the object currently assigned to that position and the objects
     * after that object are moved one position down (the value of their
     * {@code order} property is increased by one).
     *
     * If the position provided by the {@code order} parameter is larger than
     * the value of the {@code order} property of the last object plus 1 the
     * order property is set the the value of the {@code order} property of the
     * last object plus one.
     *
     * If the order property is less than 0, the object is inserted at first
     * position and the value of the {@code order} property is set to {@code 0}.
     * The value of the {@code order} property of all other objects is increased
     * by one.
     *
     * If the object is already assigned to the category and the value of the
     * {@code order} property is different than the provided value the
     * {@code order} property is set the provided value. No further action will
     * executed.
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
     * @param order    Order value specifying the sort order of the objects
     *                 assigned to category.
     */
    public void addObjectToCategory(final CcmObject object,
                                    final Category category,
                                    final long order) {
        throw new UnsupportedOperationException();
    }

    /**
     * Removes a object from a category. Additionally to removing the object
     * from the category this method also upgrades the order of all objects
     * sorted in after the removed object so that the values are consistent
     * without gaps (which may cause trouble).
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
        throw new UnsupportedOperationException();
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
        throw new UnsupportedOperationException();
    }

    /**
     * Adds a category as an subcategory to another category. If the category is
     * assigned to another category that association is removed.
     *
     * The method will ensure that values of the {@code order} properties of all
     * subcategories will remain consistent. If the provided position is already
     * occupied a the values of the {@code order} properties of the object
     * occupying the provided positions and of all following objects are
     * increased by one.
     *
     * If the provided value is larger than the value of the {@code order}
     * property of the last object the value of the {@code property} is set the
     * value of the of the {@code order} property of the last object plus one.
     *
     * The provided value is less than {@code 0} the object will be the first
     * one and the value of the {@code order} property will be set to {@code 0}.
     *
     * If the provided category is already assigned to the provided parent
     * category only the value of the {@code order} property is updated.
     *
     * @param subCategory    The category to add as subcategory. Can't be
     *                       {@code null}.
     * @param parentCategory The category to which the category is added as
     *                       subcategory. Can't be {@code null}.
     * @param order          The value for the {@code order} property of the
     *                       association.
     */
    public void addSubCategoryToCategory(final Category subCategory,
                                         final Category parentCategory,
                                         final long order) {
        throw new UnsupportedOperationException();
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
     * @throws NotASubCategoryException If the provided subcategory is not
     *                                  assigned to the provided parent
     *                                  category.
     */
    public void removeSubCategoryFromCategory(final Category subCategory,
                                              final Category parentCategory)
        throws NotASubCategoryException {
        throw new UnsupportedOperationException();
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
     * @throws NotASubCategoryException If the provided subcategory is not a
     *                                  subcategory of the provided parent
     *                                  category.
     */
    public void increaseCategoryOrder(final Category subCategory,
                                      final Category parentCategory)
        throws NotASubCategoryException {
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
     * @throws NotASubCategoryException If the provided subcategory is not a
     *                                  subcategory of the provided parent
     *                                  category.
     */
    public void decreaseCategoryOrder(final Category subCategory,
                                      final Category parentCategory)
        throws NotASubCategoryException {
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
     * @throws NotASubCategoryException If one or both categories are not 
     * subcategories of the provided parent category.qq
     */
    public void swapCategories(final Category subCategoryA,
                               final Category subCategoryB,
                               final Category parentCategory)
        throws NotASubCategoryException {
        throw new UnsupportedOperationException();
    }

}
