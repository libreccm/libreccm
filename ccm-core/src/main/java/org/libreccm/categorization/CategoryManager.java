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

import static org.libreccm.categorization.CategorizationConstants.*;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.Shiro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

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

    @Inject
    private Shiro shiro;

    @Inject
    private PermissionChecker permissionChecker;

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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addObjectToCategory(
        final CcmObject object,
        @RequiresPrivilege(MANAGE_CATEGORY_OBJECTS_PRIVILEGE)
        final Category category) {

        addObjectToCategory(object, category, null);
    }
    
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
     * @param type Type of the categorisation.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addObjectToCategory(
        final CcmObject object,
        @RequiresPrivilege(MANAGE_CATEGORY_OBJECTS_PRIVILEGE)
        final Category category,
        final String type) {

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
        categorization.setType(type);
        categorization.setIndex(false);

        object.addCategory(categorization);
        category.addObject(categorization);

        // Saving a category requires the manage_category privilege which
        // may has not been granted to a user which is allowed to assign objects
        // to a category. Therefore we bypass the this authorisation check here
        // by executing CategoryRepository#save(Category) as the system user.
        shiro.getSystemUser().execute(() -> {
            entityManager.persist(categorization);
            categoryRepo.save(category);
            ccmObjectRepo.save(object);
        });
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeObjectFromCategory(
        final CcmObject object,
        @RequiresPrivilege(MANAGE_CATEGORY_OBJECTS_PRIVILEGE)
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

        shiro.getSystemUser().execute(() -> {
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
        });
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void increaseObjectOrder(
        final CcmObject object,
        @RequiresPrivilege(MANAGE_CATEGORY_OBJECTS_PRIVILEGE)
        final Category category)
        throws ObjectNotAssignedToCategoryException {

        if (object == null) {
            throw new IllegalArgumentException("The object can't be null.");
        }

        if (category == null) {
            throw new IllegalArgumentException("The category can't be null");
        }

        final Categorization categorization;
        final Categorization nextCategorization;

        Categorization current = null;
        int index = 0;
        final List<Categorization> objects = new ArrayList<>(category
            .getObjects());
        objects.sort((o1, o2) -> {
            return Long.compare(o1.getObjectOrder(), o2.getObjectOrder());
        });
        while (index < objects.size()) {
            current = objects.get(index);
            if (current.getCategorizedObject().equals(object)) {
                break;
            }
            index++;
        }

        categorization = current;
        if ((index + 1) < objects.size()) {
            nextCategorization = objects.get(index + 1);
        } else {
            //No next object, returning silently.
            return;
        }

        if (categorization == null) {
            //Object is not part of the category.
            throw new ObjectNotAssignedToCategoryException(String.format(
                "The object %s is not assigned to the category %s (UUID: %s).",
                object.getUuid(),
                category.getName(),
                category.getUuid()));
        }

        final long order = categorization.getObjectOrder();
        final long nextOrder = nextCategorization.getObjectOrder();

        categorization.setObjectOrder(nextOrder);
        nextCategorization.setObjectOrder(order);

        shiro.getSystemUser().execute(() -> categoryRepo.save(category));
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void decreaseObjectOrder(
        final CcmObject object,
        @RequiresPrivilege(MANAGE_CATEGORY_OBJECTS_PRIVILEGE)
        final Category category)
        throws ObjectNotAssignedToCategoryException {

        if (object == null) {
            throw new IllegalArgumentException("The object can't be null.");
        }

        if (category == null) {
            throw new IllegalArgumentException("The category can't be null");
        }

        final Categorization categorization;
        final Categorization prevCategorization;

        Categorization current = null;
        int index = 0;
        final List<Categorization> objects = new ArrayList<>(category
            .getObjects());
        objects.sort((o1, o2) -> {
            return Long.compare(o1.getObjectOrder(), o2.getObjectOrder());
        });
        while (index < objects.size()) {
            current = objects.get(index);
            if (current.getCategorizedObject().equals(object)) {
                break;
            }
            index++;
        }

        categorization = current;
        if ((index - 1) >= 0) {
            prevCategorization = objects.get(index - 1);
        } else {
            //No previous object, returning silently.
            return;
        }

        if (categorization == null) {
            //Object is not part of the category.
            throw new ObjectNotAssignedToCategoryException(String.format(
                "The object %s is not assigned to the category %s (UUID: %s).",
                object.getUuid(),
                category.getName(),
                category.getUuid()));
        }

        final long order = categorization.getObjectOrder();
        final long prevOrder = prevCategorization.getObjectOrder();

        categorization.setObjectOrder(prevOrder);
        prevCategorization.setObjectOrder(order);

        shiro.getSystemUser().execute(() -> categoryRepo.save(category));
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
//    public void swapObjects(final CcmObject objectA,
//                            final CcmObject objectB,
//                            final Category category)
//        throws ObjectNotAssignedToCategoryException {
//        // TODO implement method
//        throw new UnsupportedOperationException();
//    }
    /**
     * Adds a category as an subcategory to another category. If the category is
     * assigned to another category that association is removed.
     *
     * @param subCategory    The category to add as subcategory. Can't be
     *                       {@code null}.
     * @param parentCategory The category to which the category is added as
     *                       subcategory. Can't be {@code null}.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addSubCategoryToCategory(
        final Category subCategory,
        @RequiresPrivilege(MANAGE_CATEGORY_PRIVILEGE)
        final Category parentCategory) {

        final Category sub = categoryRepo.findById(subCategory.getObjectId());
        final Category parent = categoryRepo.findById(parentCategory
            .getObjectId());

        if (sub.getParentCategory() != null) {
            final Category oldParent = sub.getParentCategory();
            removeSubCategoryFromCategory(sub, oldParent);
        }

        final int order = parent.getSubCategories().size() + 1;
        parent.addSubCategory(sub);
        sub.setParentCategory(parent);
        sub.setCategoryOrder(order);

        shiro.getSystemUser().execute(() -> {
            categoryRepo.save(parent);
            categoryRepo.save(sub);
        });
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeSubCategoryFromCategory(
        final Category subCategory,
        @RequiresPrivilege(MANAGE_CATEGORY_PRIVILEGE)
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

        shiro.getSystemUser().execute(() -> {
            categoryRepo.save(parentCategory);
            categoryRepo.save(subCategory);
        });
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void increaseCategoryOrder(
        final Category subCategory,
        @RequiresPrivilege(MANAGE_CATEGORY_PRIVILEGE)
        final Category parentCategory) {

        if (parentCategory == null) {
            throw new IllegalArgumentException("parentCategory can't be null.");
        }

        if (subCategory == null) {
            throw new IllegalArgumentException("subCategory can't be null.");
        }

        boolean found = false;
        int index = 0;
        final List<Category> subCategories = new ArrayList<>(parentCategory
            .getSubCategories());
        subCategories.sort((c1, c2) -> {
            return Long.compare(c1.getCategoryOrder(), c2.getCategoryOrder());
        });
        while (index < subCategories.size()) {
            if (subCategories.get(index).equals(subCategory)) {
                found = true;
                break;
            }
            index++;
        }

        if (!found) {
            throw new IllegalArgumentException(String.format(
                "The category %s (UUID: %s) is not a subcategory of the "
                    + "category %s (UUID: %s)",
                subCategory.getName(),
                subCategory.getUuid(),
                parentCategory.getName(),
                parentCategory.getUuid()));
        }

        final Category nextCategory;
        if ((index + 1) < subCategories.size()) {
            nextCategory = subCategories.get(index + 1);
        } else {
            //No next category, returning sliently.
            return;
        }

        final long order = subCategory.getCategoryOrder();
        final long nextOrder = nextCategory.getCategoryOrder();

        subCategory.setCategoryOrder(nextOrder);
        nextCategory.setCategoryOrder(order);

        shiro.getSystemUser().execute(() -> {
            categoryRepo.save(subCategory);
            categoryRepo.save(nextCategory);
        });
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
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void decreaseCategoryOrder(
        final Category subCategory,
        @RequiresPrivilege(MANAGE_CATEGORY_PRIVILEGE)
        final Category parentCategory) {

        if (parentCategory == null) {
            throw new IllegalArgumentException("parentCategory can't be null.");
        }

        if (subCategory == null) {
            throw new IllegalArgumentException("subCategory can't be null.");
        }

        boolean found = false;
        int index = 0;
        final List<Category> subCategories = new ArrayList<>(parentCategory
            .getSubCategories());
        subCategories.sort((c1, c2) -> {
            return Long.compare(c1.getCategoryOrder(), c2.getCategoryOrder());
        });
        while (index < subCategories.size()) {
            if (subCategories.get(index).equals(subCategory)) {
                found = true;
                break;
            }
            index++;
        }

        if (!found) {
            throw new IllegalArgumentException(String.format(
                "The category %s (UUID: %s) is not a subcategory of the "
                    + "category %s (UUID: %s)",
                subCategory.getName(),
                subCategory.getUuid(),
                parentCategory.getName(),
                parentCategory.getUuid()));
        }

        final Category prevCategory;
        if ((index - 1) >= 0) {
            prevCategory = subCategories.get(index - 1);
        } else {
            //No previous object, returning silently
            return;
        }

        final long order = subCategory.getCategoryOrder();
        final long prevOrder = prevCategory.getCategoryOrder();

        subCategory.setCategoryOrder(prevOrder);
        prevCategory.setCategoryOrder(order);

        shiro.getSystemUser().execute(() -> {
            categoryRepo.save(subCategory);
            categoryRepo.save(prevCategory);
        });
    }

    /**
     * Returns the path of a category as string. The path of a category are the
     * names of all its parent categories and the category joined together,
     * separated by a slash.
     *
     * @param category The category whose path is generated.
     *
     * @return The path of the category.
     */
    public String getCategoryPath(final Category category) {
        final List<String> tokens = new ArrayList<>();

        Category current = category;
        while (current.getParentCategory() != null) {
            tokens.add(current.getDisplayName());
            current = current.getParentCategory();
        }

        Collections.reverse(tokens);
        final StringJoiner joiner = new StringJoiner("/", "/", "");
        tokens.forEach(joiner::add);

        return joiner.toString();
    }

    public boolean hasIndexObject(final Category category) {
//        final TypedQuery<Long> hasIndexItemQuery = entityManager
//            .createNamedQuery("Categorization.hasIndexObject", Long.class);
//        hasIndexItemQuery.setParameter("category", category);
//        final long indexItems = hasIndexItemQuery.getSingleResult();
//        return indexItems > 0;
        final TypedQuery<Boolean> query = entityManager
            .createNamedQuery("Categorization.hasIndexObject", Boolean.class);
        query.setParameter("category", category);

        return query.getSingleResult();
    }

    /**
     * Retrieves to index object of a category. The caller is responsible for
     * checking if the current user has sufficient privileges to read the index
     * object!
     *
     * @param category The category of which the index object should be
     *                 retrieved.
     *
     * @return An {@link Optional} containing the index object of the provided
     *         category if the category has an index object.
     */
    public Optional<CcmObject> getIndexObject(final Category category) {
        if (hasIndexObject(category)) {
            final TypedQuery<CcmObject> query = entityManager.createNamedQuery(
                "Categorization.findIndexObject", CcmObject.class);
            query.setParameter("category", category);

            return Optional.of(query.getSingleResult());
        } else {
            return Optional.empty();
        }
    }

}
