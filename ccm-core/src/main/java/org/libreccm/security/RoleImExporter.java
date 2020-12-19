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
package org.libreccm.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.ExportException;
import org.libreccm.imexport.Exportable;
import org.libreccm.imexport.Processes;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.Dependent;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * Exporter/Importer for {@link Role}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
@Processes(Role.class)
public class RoleImExporter extends AbstractEntityImExporter<Role> {

    private static final Logger LOGGER = LogManager.getLogger(
        RoleImExporter.class);

    @Inject
    private EntityManager entityManager;

    @Inject
    private RoleRepository roleRepository;

    @Override
    public Class<Role> getEntityClass() {
        return Role.class;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public String exportEntity(final Exportable entity) throws ExportException {
        final Role role = roleRepository
            .findById(((Role) entity).getRoleId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Provided entity %d does not exist in database.",
                        entity)
                )
            );
        role.getDescription().getValues().forEach((locale, value) -> LOGGER
            .info("{}: {}", locale, value));
        return super.exportEntity(entity);
    }

    @Override
    protected void saveImportedEntity(final Role entity) {
        roleRepository.save(entity);
    }

    @Override
    protected Set<Class<? extends Exportable>> getRequiredEntities() {
        return Collections.emptySet();
    }

    @Override
    protected Role reloadEntity(final Role entity) {
        return roleRepository
            .findById(Objects.requireNonNull(entity).getRoleId())
            .orElseThrow(
                () -> new IllegalArgumentException(
                    String.format(
                        "Role entity %s not found in database.",
                        Objects.toString(entity)
                    )
                )
            );
    }

}
