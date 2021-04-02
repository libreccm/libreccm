/*
 * Copyright (C) 2021 LibreCCM Foundation.
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

import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.Processes;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(ContentSection.class)
public class ContentSectionImExporter
    extends AbstractEntityImExporter<ContentSection> {

    @Inject
    private ContentSectionRepository sectionRepository;

    @Override
    public Class<ContentSection> getEntityClass() {
        return ContentSection.class;
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        return Collections.emptySet();
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final ContentSection entity) {
        sectionRepository.save(entity);
    }

    @Override
    protected ContentSection reloadEntity(final ContentSection entity) {
        return sectionRepository
            .findById(Objects.requireNonNull(entity).getObjectId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "ContentSection entity %s not found in database.",
                        Objects.toString(entity)
                    )
                )
            );
    }

}
