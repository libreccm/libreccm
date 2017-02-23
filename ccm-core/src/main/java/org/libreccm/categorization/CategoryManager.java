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
import java.util.Objects;
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
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY_OBJECTS)
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
     * @param type     Type of the categorisation.
     */
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public void addObjectToCategory(
        final CcmObject object,
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY_OBJECTS)
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
        // to a category. Therefore we bypass the authorisation check here
        // by executing CategoryRepository#save(Category) as the system user.
        shiro.getSystemUser().execute(() -> {
            entityManager.persist(categorization);
            categoryRepo.save(category);
            ccmObjectRepo.save(object);
        });
    }

    public boolean hasSubCategories(final Category category) {

        Objects.requireNonNull(
            category,
            "Can't determine if Category null has sub categories.");

        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
            "Category.hasSubCategories", Boolean.class);
        query.setParameter("category", category);

        return query.getSingleResult();
    }

    public boolean hasObjects(final Category category) {

        Objects.requireNonNull(category,
                               "Can't determine if category null has objects.");

        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
            "Category.hasObjects", Boolean.class);
        query.setParameter("category", category);

        return query.getSingleResult();
    }

    /**
     * Check if an object is assigned to a category.
     *
     * @param category The category
     * @param object   The object
     *
     * @return {@code true} if the provided {@code object} is assigned to the
     *         provided {@code category}, {@code false} otherwise.
     */
    public boolean isAssignedToCategory(final Category category,
                                        final CcmObject object) {
        Objects.requireNonNull(category);
        Objects.requireNonNull(object);

        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
            "Categorization.isAssignedTo", Boolean.class);
        query.setParameter("category", category);
        query.setParameter("object", object);

        return query.getSingleResult();
    }

    /**
     * Check if an object is assigned to a category with a specific type. If you
     * only want to check if an object is assigned to a category regardless of
     * the type use
     * {@link #isAssignedToCategory(org.libreccm.categorization.Category, org.libreccm.core.CcmObject)}.
     *
     * @param category The category
     * @param object   The object
     * @param type     The type with which the object has been assigned to the
     *                 category. the type may be {@code null}.
     *
     * @return {@code true} if the provided {@code object} is assigned to the
     *         provided {@code category} using the provided {@code type}.
     */
    public boolean isAssignedToCategoryWithType(final Category category,
                                                final CcmObject object,
                                                final String type) {
        Objects.requireNonNull(category);
        Objects.requireNonNull(object);

        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
            "Categorization.isAssignedTo", Boolean.class);
        query.setParameter("category", category);
        query.setParameter("object", object);
        query.setParameter("type", type);

        return query.getSingleResult();
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
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY_OBJECTS)
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

        final List<Categorization> categorizations;
        try {
            categorizations = query.getResultList();
        } catch (NoResultException ex) {
            LOGGER.warn(String.format(
                "No categorization for category %s and object %s found."
                    + "Ignoring. Orginal exception: ",
                category.toString(),
                object.toString()),
                        ex);
            return;
        }

        categorizations.forEach(entityManager::remove);

//        shiro.getSystemUser().execute(() -> {
//            object.removeCategory(categorization);
//            category.removeObject(categorization);
//            entityManager.remove(categorization);
//            categoryRepo.save(category);
//            ccmObjectRepo.save(object);
//
//            final List<Categorization> categories = object.getCategories();
//            for (int i = 0; i < categories.size(); i++) {
//                categories.get(i).setCategoryOrder(i);
//                entityManager.merge(categories.get(i));
//            }
//
//            final List<Categorization> objects = category.getObjects();
//            for (int i = 0; i < objects.size(); i++) {
//                objects.get(i).setObjectOrder(i);
//                entityManager.merge(objects.get(i));
//            }
//        });
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
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY_OBJECTS)
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
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY_OBJECTS)
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
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY)
        final Category parentCategory) {

        if (subCategory == null) {
            throw new IllegalArgumentException("subCategory can't be null.");
        }
        if (parentCategory == null) {
            throw new IllegalArgumentException("parentCategory can't be null.");
        }

        final Optional<Category> sub = categoryRepo.findById(subCategory
            .getObjectId());
        final Optional<Category> parent = categoryRepo.findById(parentCategory
            .getObjectId());

        if (!sub.isPresent()) {
            throw new IllegalArgumentException(String.format(
                "The provided category to add as sub category {} was not found "
                    + "in the database.",
                subCategory.toString()));
        }
        if (!parent.isPresent()) {
            throw new IllegalArgumentException(String.format(
                "The category {} provided as parent category was not found in "
                    + "the database.",
                parentCategory.toString()));
        }

        if (hasSubCategoryWithName(parent.get(), sub.get().getName())) {
            throw new IllegalArgumentException(String.format(
                "The provided category already has a sub category with "
                    + "the name '%s'.",
                sub.get().getName()));
        }
        
        if (sub.get().getParentCategory() != null) {
            final Category oldParent = sub.get().getParentCategory();
            removeSubCategoryFromCategory(sub.get(), oldParent);
        }

        final int order = parent.get().getSubCategories().size() + 1;
        parent.get().addSubCategory(sub.get());
        sub.get().setParentCategory(parent.get());
        sub.get().setCategoryOrder(order);

        shiro.getSystemUser().execute(() -> {
            categoryRepo.save(parent.get());
            categoryRepo.save(sub.get());
        });
    }

    /**
     * Checks if a category has a sub category with the provided name.
     *
     * @param category The category.
     * @param name     The name.
     *
     * @return {@code true} if the provided {@code category} has a child with
     *         the provided {@code name}, {@code false} otherwise.
     *
     * @see {@link Category#name}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean hasSubCategoryWithName(final Category category,
                                          final String name) {
        Objects.requireNonNull(category, "category can't be null");
        Objects.requireNonNull(name, "name can't be null");

        if (name.trim().isEmpty()) {
            throw new IllegalArgumentException("name can't be empty.");
        }

        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
            "Category.hasSubCategoryWithName", Boolean.class);
        query.setParameter("name", name);
        query.setParameter("parent", category);

        return query.getSingleResult();
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
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY)
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
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY)
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
        @RequiresPrivilege(PRIVILEGE_MANAGE_CATEGORY)
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

    /**
     * Set the index object of a category. There can only be one index object
     * per category. Therefore this method first sets
     * {@link Categorization#index} to false for all categorisations of the
     * provided category. Then it retrieves the {@link Categorization} for the
     * provided {@link CcmObject} and sets {@link Categorization#index} to
     * {@code true} for this categorisation.
     *
     * @param category The category whose index object is set or changed.
     * @param object   The new index object for the category. The object must be
     *                 assigned to the category.
     *
     * @throws ObjectNotAssignedToCategoryException If the provided
     *                                              {@code object} is not
     *                                              assigned to the provided
     *                                              {@code category}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void setIndexObject(final Category category,
                               final CcmObject object)
        throws ObjectNotAssignedToCategoryException {

        Objects.requireNonNull(category);
        Objects.requireNonNull(object);

        // First, ensure that the provided object is assigned to the provided 
        // category.
        if (!isAssignedToCategory(category, object)) {
            throw new ObjectNotAssignedToCategoryException(String.format(
                "The provided object %s is not assigned to the provided category %s "
                + "and can therefore not be an index object of the category.",
                Objects.toString(category),
                Objects.toString(object)));
        }

        // If the category has already an index object we need to reset the 
        // index for the categorisation to ensure that the category has only
        // one index object
        resetIndexObject(category);

        // Now find the categorization for the provided object and set 
        // index = true
        final TypedQuery<Categorization> query = entityManager.createNamedQuery(
            "Categorization.find", Categorization.class);
        query.setParameter("category", category);
        query.setParameter("object", object);

        final Categorization categorization;
        try {
            categorization = query.getSingleResult();
        } catch (NoResultException ex) {
            throw new ObjectNotAssignedToCategoryException(String.format(
                "Strange. The previous check if the provided object %s is "
                    + "assigned to the provided category %s returned "
                    + "true, but the query for the categorization "
                    + "object returned no result. This should not happen. "
                    + "Please report a bug.",
                Objects.toString(object),
                Objects.toString(category)),
                                                           ex);
        }

        categorization.setIndex(true);
        entityManager.merge(categorization);
    }

    /**
     * Resets the index object of a category to none. This methods retrieves all
     * categorisations with {@link Categorization#index}{@code == true} of the
     * provided category and set them to {@code false}. Therefore this method
     * can also be used if a category has got multiple index objects due to some
     * circumstances which may causes problems.
     *
     * @param category The category which index object should be reset.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public void resetIndexObject(final Category category) {
        final TypedQuery<Categorization> query = entityManager.createNamedQuery(
            "Categorization.findIndexObjectCategorization",
            Categorization.class);
        query.setParameter("category", category);

        final List<Categorization> result = query.getResultList();
        result.forEach(categorization -> categorization.setIndex(false));
        result.forEach(categorization -> entityManager.merge(categorization));
    }

}
