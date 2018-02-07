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
package org.libreccm.pagemodel;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.CcmApplication;

import javax.enterprise.context.RequestScoped;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for {@link PageModel}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class PageModelRepository extends AbstractEntityRepository<Long, PageModel> {

    private static final long serialVersionUID = -7240418542790568571L;

    @Override
    public Class<PageModel> getEntityClass() {
        return PageModel.class;
    }

    @Override
    public String getIdAttributeName() {
        return "pageModelId";
    }

    @Override
    public Long getIdOfEntity(final PageModel entity) {
        return entity.getPageModelId();
    }
    
    @Override
    public boolean isNew(final PageModel pageModel) {

        Objects.requireNonNull(pageModel);

        return pageModel.getPageModelId() == 0;
    }

    /**
     * Sets the UUID field of a new {@link PageModel}.
     *
     * @param pageModel The new {@link PageModel}.
     */
    @Override
    public void initNewEntity(final PageModel pageModel) {

        Objects.requireNonNull(pageModel);

        final String uuid = UUID.randomUUID().toString();

        pageModel.setUuid(uuid);
        if (pageModel.getModelUuid() == null
                || pageModel.getModelUuid().isEmpty()) {
            pageModel.setModelUuid(uuid);
        }
    }

    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(final PageModel pageModel) {
        super.save(pageModel);
    }

    /**
     * Find all draft versions of {@link PageModel}s.
     *
     * @return A list with all draft versions of {@link PageModel}s.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    @Transactional(Transactional.TxType.REQUIRED)
    public List<PageModel> findAllDraftModels() {

        final TypedQuery<PageModel> query = getEntityManager()
            .createNamedQuery("PageModel.findAllDraftModels", PageModel.class);

        return query.getResultList();
    }

    /**
     * Find all live versions of {@link PageModel}s.
     *
     * @return A list with all draft versions of {@link PageModel}s.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<PageModel> findAllLiveModels() {

        final TypedQuery<PageModel> query = getEntityManager()
            .createNamedQuery("PageModel.findAllLiveModels", PageModel.class);

        return query.getResultList();
    }

    /**
     * Finds the draft versions of all {@link PageModel}s for the provided
     * application.
     *
     * @param application The application for which the {@link PageModel}s are
     *                    retrieved.
     *
     * @return A list of the {@link PageModel}s defined for the provided
     *         {@code application}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public List<PageModel> findDraftByApplication(
        final CcmApplication application) {

        Objects.requireNonNull(application,
                               "Can't find page models for application null");

        final TypedQuery<PageModel> query = getEntityManager()
            .createNamedQuery("PageModel.findDraftByApplication",
                              PageModel.class);
        query.setParameter("application", application);

        return query.getResultList();
    }

    /**
     * Counts the {@link PageModel}s (draft version) defined for a application.
     *
     * @param application The application for which the {@link PageModels}s are
     *                    counted.
     *
     * @return The number of {@link PageModel}s defined for the provided
     *         {@code application}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public long countByApplication(final CcmApplication application) {

        Objects.requireNonNull(application,
                               "Can't count page models for application null");

        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "PageModel.countDraftByApplication", Long.class);
        query.setParameter("application", application);

        return query.getSingleResult();
    }

    /**
     * Finds a {@link PageModel} (draft version) by the application and its
     * {@code name}.
     *
     * @param application The application for which the {@link PageModel} is
     *                    defined.
     * @param name        The name of the {@link PageModel}.
     *
     * @return An {@link Optional} containing the {@link PageModel} for the
     *         provided {@code application} with the provided {@code name}. If
     *         there is no {@link PageModel} matching the criteria an empty
     *         {@link Optional} is returned.
     */
    public Optional<PageModel> findDraftByApplicationAndName(
        final CcmApplication application,
        final String name) {

        Objects.requireNonNull(application,
                               "Can't find page models for application null");
        Objects.requireNonNull(name,
                               "The name of a page model can't be null or empty.");

        if (name.isEmpty() || name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a page model can't be null or empty.");
        }

        final long count = countLiveByApplicationAndName(application, name);
        if (count == 0) {
            return Optional.empty();
        }

        final TypedQuery<PageModel> query = getEntityManager().createNamedQuery(
            "PageModel.findDraftByApplicationAndName", PageModel.class);
        query.setParameter("application", application);
        query.setParameter("name", name);

        return Optional.of(query.getSingleResult());
    }

    /**
     * Counts the number of {@link PageModel} (draft version) defined for the
     * provided application with the provided name.
     *
     * @param application The application for which the {@link PageModel} is
     *                    defined.
     * @param name        The name of the {@link PageModel}.
     *
     * @return The number of {@link PageModel}s matching the criteria. Should be
     *         0 or 1.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    @AuthorizationRequired
    @RequiresPrivilege(CoreConstants.PRIVILEGE_ADMIN)
    public long countDraftByApplicationAndName(final CcmApplication application,
                                               final String name) {

        Objects.requireNonNull(application,
                               "Can't find page models for application null");
        Objects.requireNonNull(name,
                               "The name of a page model can't be null or empty.");

        if (name.isEmpty() || name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a page model can't be null or empty.");
        }

        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "PageModel.countDraftByApplicationAndName", Long.class);
        query.setParameter("application", application);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

    /**
     * Finds the live versions of all {@link PageModel}s for the provided
     * application.
     *
     * @param application The application for which the {@link PageModel}s are
     *                    retrieved.
     *
     * @return A list of the {@link PageModel}s defined for the provided
     *         {@code application}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public List<PageModel> findLiveByApplication(
        final CcmApplication application) {

        Objects.requireNonNull(application,
                               "Can't find page models for application null");

        final TypedQuery<PageModel> query = getEntityManager()
            .createNamedQuery("PageModel.findLiveByApplication",
                              PageModel.class);
        query.setParameter("application", application);

        return query.getResultList();
    }

    /**
     * Counts the {@link PageModel}s (live version) defined for a application.
     *
     * @param application The application for which the {@link PageModels}s are
     *                    counted.
     *
     * @return The number of {@link PageModel}s defined for the provided
     *         {@code application}.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public long countLiveByApplication(final CcmApplication application) {

        Objects.requireNonNull(application,
                               "Can't count page models for application null");

        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "PageModel.countLiveByApplication", Long.class);
        query.setParameter("application", application);

        return query.getSingleResult();
    }

    /**
     * Finds a {@link PageModel} (live version) by the application and its
     * {@code name}.
     *
     * @param application The application for which the {@link PageModel} is
     *                    defined.
     * @param name        The name of the {@link PageModel}.
     *
     * @return An {@link Optional} containing the {@link PageModel} for the
     *         provided {@code application} with the provided {@code name}. If
     *         there is no {@link PageModel} matching the criteria an empty
     *         {@link Optional} is returned.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<PageModel> findLiveByApplicationAndName(
        final CcmApplication application,
        final String name) {

        Objects.requireNonNull(application,
                               "Can't find page models for application null");
        Objects.requireNonNull(name,
                               "The name of a page model can't be null or empty.");

        if (name.isEmpty() || name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a page model can't be null or empty.");
        }

        final long count = countLiveByApplicationAndName(application, name);
        if (count == 0) {
            return Optional.empty();
        }

        final TypedQuery<PageModel> query = getEntityManager().createNamedQuery(
            "PageModel.findLiveByApplicationAndName", PageModel.class);
        query.setParameter("application", application);
        query.setParameter("name", name);

        return Optional.of(query.getSingleResult());
    }

    /**
     * Counts the number of {@link PageModel} (live version) defined for the
     * provided application with the provided name.
     *
     * @param application The application for which the {@link PageModel} is
     *                    defined.
     * @param name        The name of the {@link PageModel}.
     *
     * @return The number of {@link PageModel}s matching the criteria. Should be
     *         0 or 1.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public long countLiveByApplicationAndName(final CcmApplication application,
                                              final String name) {

        Objects.requireNonNull(application,
                               "Can't find page models for application null");
        Objects.requireNonNull(name,
                               "The name of a page model can't be null or empty.");

        if (name.isEmpty() || name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a page model can't be null or empty.");
        }

        final TypedQuery<Long> query = getEntityManager().createNamedQuery(
            "PageModel.countLiveByApplicationAndName", Long.class);
        query.setParameter("application", application);
        query.setParameter("name", name);

        return query.getSingleResult();
    }

}
