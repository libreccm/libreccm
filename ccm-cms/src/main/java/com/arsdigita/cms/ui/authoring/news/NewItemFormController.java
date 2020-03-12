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
package com.arsdigita.cms.ui.authoring.news;

import org.libreccm.l10n.GlobalizationHelper;

import java.util.Collections;
import java.util.List;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.contenttypes.News;

import java.util.Locale;

/**
 * Controller class for the {@link NewItemForm}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class NewItemFormController {

    @Inject
    private EntityManager entityManager;

    @Inject
    private Shiro shiro;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private RoleRepository roleRepo;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentTypeRepository typeRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    protected boolean hasContentTypes(final ContentSection section) {
        Objects.requireNonNull(section, "Can't work with null for the section.");

        final Optional<User> user = shiro.getUser();
        if (!user.isPresent()) {
            return false;
        }

        final List<Role> roles = user.get().getRoleMemberships().stream()
            .map(membership -> membership.getRole())
            .collect(Collectors.toList());

        final TypedQuery<Boolean> query = entityManager.createNamedQuery(
            "ContentSection.hasUsableContentTypes", Boolean.class);
        query.setParameter("section", section);
        query.setParameter("roles", roles);
        query.setParameter("isSysAdmin", permissionChecker.isPermitted("*"));

        return query.getSingleResult();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    protected List<ContentType> getContentTypes(final ContentSection section) {
        Objects.requireNonNull(section);

        final Optional<User> user = shiro.getUser();
        if (!user.isPresent()) {
            return Collections.EMPTY_LIST;
        }

        final List<Role> roles = user.get().getRoleMemberships().stream()
            .map(membership -> membership.getRole())
            .collect(Collectors.toList());

        final TypedQuery<ContentType> query = entityManager.createNamedQuery(
            "ContentSection.findUsableContentTypes",
            ContentType.class);
        query.setParameter("section", section);
        query.setParameter("roles", roles);
        query.setParameter("isSysAdmin", permissionChecker.isPermitted("*"));

        return query.getResultList();
    }

   

}
