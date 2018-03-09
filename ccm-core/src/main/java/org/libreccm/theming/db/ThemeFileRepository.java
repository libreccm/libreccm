/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.db;

import org.libreccm.core.AbstractEntityRepository;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.theming.ThemeVersion;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import javax.enterprise.context.RequestScoped;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemeFileRepository extends AbstractEntityRepository<Long, ThemeFile> {

    private static final long serialVersionUID = -490998638396765429L;

    @Override
    public Class<ThemeFile> getEntityClass() {
        return ThemeFile.class;
    }

    @Override
    public String getIdAttributeName() {
        return "fileId";
    }

    @Override
    public Long getIdOfEntity(final ThemeFile entity) {
        return entity.getFileId();
    }

    @Override
    public boolean isNew(final ThemeFile entity) {
        return entity.getFileId() == 0;
    }

    @Override
    public void initNewEntity(final ThemeFile themeFile) {
        if (themeFile.getUuid() == null || themeFile.getUuid().isEmpty()) {
            themeFile.setUuid(UUID.randomUUID().toString());
        }
    }

    @Override
    public void save(final ThemeFile file) {

        if (file instanceof DataFile) {
            final DataFile dataFile = (DataFile) file;
            dataFile.setLastModified(new Date());
            try (final InputStream inputStream = new BufferedInputStream(
                new ByteArrayInputStream(dataFile.getData()))) {

                final String mimeType = URLConnection
                    .guessContentTypeFromStream(inputStream);
                dataFile.setType(mimeType);
            } catch (IOException ex) {
                throw new UnexpectedErrorException(ex);
            }
        }

        super.save(file);
    }

    public Optional<ThemeFile> findByUuid(final String uuid,
                                          final ThemeVersion version) {

        final TypedQuery<ThemeFile> query = getEntityManager()
            .createNamedQuery("ThemeFile.findByUuid", ThemeFile.class);
        query.setParameter("uuid", uuid);
        query.setParameter("version", version);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<ThemeFile> findByPath(final Theme theme,
                                          final String path,
                                          final ThemeVersion version) {

        final TypedQuery<ThemeFile> query = getEntityManager()
            .createNamedQuery("ThemeFile.findByPath", ThemeFile.class);
        query.setParameter("path", path);
        query.setParameter("version", version);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

    public Optional<ThemeFile> findByNameAndParent(final String name,
                                                   final Directory parent) {

        final TypedQuery<ThemeFile> query = getEntityManager()
            .createNamedQuery("ThemeFile.findByNameAndParent", ThemeFile.class);
        query.setParameter("name", name);
        query.setParameter("parent", parent);

        try {
            return Optional.of(query.getSingleResult());
        } catch (NoResultException ex) {
            return Optional.empty();
        }
    }

}
