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

import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import java.io.Serializable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Provides several methods when managing the relations between {@link Domain}s
 * and their owning {@link CcmApplication}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class DomainManager implements Serializable {

    private static final long serialVersionUID = -8921596414159020455L;

    @Inject
    private ApplicationRepository applicationRepo;

    @Inject
    private DomainRepository domainRepo;

    @Inject
    private CategoryRepository categoryRepo;

    @Inject
    private EntityManager entityManager;

    /**
     * Creates a new domain with the provided key (name) and a root category
     * with the provided name. The domain and the root category can be further
     * customised after the creation.
     *
     * @param domainKey        The key (name) of the new domain.
     * @param rootCategoryName The name of the root category of the new domain.
     *
     * @return The new domain.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CategorizationConstants.PRIVILEGE_MANAGE_DOMAINS)
    @Transactional(Transactional.TxType.REQUIRED)
    public Domain createDomain(final String domainKey,
                               final String rootCategoryName) {
        final Domain domain = new Domain();
        domain.setDomainKey(domainKey);
        domain.setVersion("1.0");
        domain.setDisplayName(domainKey);

        final Category root = new Category();
        root.setName(rootCategoryName);
        root.setDisplayName(rootCategoryName);

        domain.setRoot(root);

        categoryRepo.save(root);
        domainRepo.save(domain);

        return domain;
    }

    /**
     * Adds a {@code CcmApplication} to the owners of a {@link Domain}. If the
     * provided {@code CcmApplication} is already an owner of the provided
     * {@code Domain} the method does nothing.
     *
     * @param application The {@code CcmApplication} to add to the owners of the
     *                    {@code Domain}.
     * @param domain      The {@code Domain} to which owners the
     *                    {@code CcmApplication is added}.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CategorizationConstants.PRIVILEGE_MANAGE_DOMAINS)
    @Transactional(Transactional.TxType.REQUIRED)
    public void addDomainOwner(final CcmApplication application,
                               final Domain domain) {
        final DomainOwnership ownership = new DomainOwnership();
        ownership.setUuid(UUID.randomUUID().toString());
        ownership.setDomain(domain);
        ownership.setOwner(application);
        ownership.setOwnerOrder(domain.getOwners().size() + 1);
        ownership.setDomainOrder(application.getDomains().size() + 1);

        application.addDomain(ownership);
        domain.addOwner(ownership);

        entityManager.persist(ownership);
        applicationRepo.save(application);
        domainRepo.save(domain);
    }

    /**
     * Removes a {@code CcmApplication} from the owners of a {@code Domain}. If
     * the provided {@code CcmApplication} is not an owner of the provided
     * {@code Domain} the method does nothing.
     *
     * @param application The {@code CcmApplication} to remove from the owners
     *                    of the provided {@code Domain}.
     * @param domain      The {@code Domain} from which owners the provided
     *                    {@code CcmApplication} should be removed.
     */
    @AuthorizationRequired
    @RequiresPrivilege(CategorizationConstants.PRIVILEGE_MANAGE_DOMAINS)
    @Transactional(Transactional.TxType.REQUIRED)
    public void removeDomainOwner(final CcmApplication application,
                                  final Domain domain) {
        if (application == null) {
            throw new IllegalArgumentException("Can't remove owner null.");
        }
        if (domain == null) {
            throw new IllegalArgumentException(
                "Can't remove the owner from domain null.");
        }

        final Optional<CcmApplication> owner = applicationRepo.findById(
            application.getObjectId(), "CcmApplication.withDomains");
        final Optional<Domain> ownedDomain = domainRepo.findById(
            domain.getObjectId(), "Domain.withOwners");

        if (!owner.isPresent()) {
            throw new IllegalArgumentException(String.format(
                "The provided owner %s does not exist in the database.",
                application.toString()));
        }
        if (!ownedDomain.isPresent()) {
            throw new IllegalArgumentException(String.format(
                "The provided domain %s does not exist in the database.",
                domain.toString()));
        }

        final TypedQuery<DomainOwnership> query = entityManager
            .createNamedQuery("DomainOwnership.findByOwnerAndDomain",
                              DomainOwnership.class);
        query.setParameter("owner", owner.get());
        query.setParameter("domain", ownedDomain.get());

        final List<DomainOwnership> result = query.getResultList();

        if (result != null) {
            result.forEach(o -> {
                ownedDomain.get().removeOwner(o);
                owner.get().removeDomain(o);
                entityManager.remove(o);
                domainRepo.save(ownedDomain.get());
                applicationRepo.save(owner.get());
            });
        }
    }

    /**
     * Determines if a {@link CcmApplication} is an owner of {@link Domain}.
     *
     * @param application The {@code CcmApplication} to test.
     * @param domain      The {@code Domain} to test.
     *
     * @return {@code true} if the provided {@code CcmApplication} is an owner
     *         of the provided {@code Domain}, {@code false} otherwise.
     */
    @Transactional(Transactional.TxType.REQUIRED)
    public boolean isDomainOwner(final CcmApplication application,
                                 final Domain domain) {
        final TypedQuery<DomainOwnership> query = entityManager
            .createNamedQuery("DomainOwnership.findByOwnerAndDomain",
                              DomainOwnership.class);
        query.setParameter("owner", application);
        query.setParameter("domain", domain);

        final List<DomainOwnership> result = query.getResultList();

        return (result != null && !result.isEmpty());
    }

}
