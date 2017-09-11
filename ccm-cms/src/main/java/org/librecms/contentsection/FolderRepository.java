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
package org.librecms.contentsection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class FolderRepository extends AbstractEntityRepository<Long, Folder> {

    private static final Logger LOGGER = LogManager.getLogger(
        FolderRepository.class);

    @Inject
    private ContentSectionRepository sectionRepo;

    @Override
    public Class<Folder> getEntityClass() {
        return Folder.class;
    }

    @Override
    public boolean isNew(final Folder folder) {
        return folder.getObjectId() == 0;
    }

    @Override
    public void initNewEntity(final Folder folder) {
        folder.setUuid(UUID.randomUUID().toString());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<Folder> getRootDocumentFolders() {
        final TypedQuery<Folder> query = getEntityManager().createNamedQuery(
            "Folder.rootFolders", Folder.class);
        query.setParameter("type", FolderType.DOCUMENTS_FOLDER);

        return query.getResultList();
    }

    public List<Folder> getRootAssetFolders() {
        final TypedQuery<Folder> query = getEntityManager().createNamedQuery(
            "Folder.rootFolders", Folder.class);
        query.setParameter("type", FolderType.ASSETS_FOLDER);

        return query.getResultList();
    }

    public Optional<Folder> findByPath(final String path,
                                       final FolderType type) {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("Path can't be null or empty.");
        }

        if (type == null) {
            throw new IllegalArgumentException("No folder type provided.");
        }

        final String[] tokens = path.split(":");
        if (tokens.length > 2) {
            throw new InvalidFolderPathException(
                "The provided path is invalid: More than one colon found. "
                    + "Valid path format: domainKey:path");
        }

        if (tokens.length < 2) {
            throw new InvalidFolderPathException(
                "The provided path is invalid: No content section found in path. "
                + "Valid path format: contentSection:path");
        }

        final ContentSection section = sectionRepo
            .findByLabel(tokens[0])
            .orElseThrow(() -> new InvalidFolderPathException(String.format(
            "No content section identified by label \"%s\" found.",
            tokens[0])));

        return findByPath(section, tokens[1], type);
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<Folder> findByPath(final ContentSection section,
                                       final String path,
                                       final FolderType type) {

        Objects.requireNonNull(section, "section can't be null");

        Objects.requireNonNull(path, "Path can't be null.");
        if (path.isEmpty()) {
            throw new IllegalArgumentException("Path can't be empty.");
        }

//        String normalizedPath = path.replace('.', '/');
//        if (normalizedPath.charAt(0) == '/') {
//            normalizedPath = normalizedPath.substring(1);
//        }
//
//        if (normalizedPath.endsWith("/")) {
//            normalizedPath = normalizedPath.substring(0,
//                                                      normalizedPath.length());
//        }
//        
        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .get();

        final String normalizedPath = PathUtil.normalizePath(path);

        LOGGER.debug("Trying to find folder with path \"{}\" and type {} in"
                         + "content section \"{}\".",
                     normalizedPath,
                     type,
                     contentSection.getLabel());
        final String[] tokens = normalizedPath.split("/");
        Folder current;
        switch (type) {
            case ASSETS_FOLDER:
                current = contentSection.getRootAssetsFolder();
                break;
            case DOCUMENTS_FOLDER:
                current = contentSection.getRootDocumentsFolder();
                break;
            default:
                throw new IllegalArgumentException(String.format(
                    "Unexpected folder type %s", type));
        }
        if (normalizedPath.isEmpty()) {
            return Optional.of(current);
        }
        for (final String token : tokens) {
            if (current.getSubCategories() == null) {
                return Optional.empty();
            }

            final Optional<Category> result = current.getSubCategories()
                .stream()
                .filter(category -> category.getName().equals(token))
                .findFirst();

            if (result.isPresent()
                    && result.get() instanceof Folder) {
                current = (Folder) result.get();
            } else {
                return Optional.empty();
            }
        }

        return Optional.of(current);
    }

    /**
     * Counts the subfolders of a folder.
     *
     * @param parent The folder.
     *
     * @return The number of subfolders in the folder.
     */
    public long countSubFolders(final Folder parent) {

        final TypedQuery<Long> query = getEntityManager()
            .createNamedQuery("Folder.countSubFolders", Long.class);
        query.setParameter("parent", parent);

        return query.getSingleResult();
    }

    public List<Folder> findSubFolders(final Folder parent) {
        
        final TypedQuery<Folder> query = getEntityManager()
            .createNamedQuery("Folder.findSubFolders", Folder.class);
        query.setParameter("parent", parent);
        
        return query.getResultList();
    }
    
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void save(
        @RequiresPrivilege(ItemPrivileges.CREATE_NEW)
        final Folder folder) {

        super.save(folder);
    }

    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public void delete(
        @RequiresPrivilege(ItemPrivileges.CREATE_NEW)
        final Folder folder) {

        super.delete(folder);
    }

}
