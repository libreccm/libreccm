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
package org.libreccm.categorization;

import org.libreccm.imexport.AbstractEntityImExporter;
import org.libreccm.imexport.DependsOn;
import org.libreccm.imexport.Processes;
import org.libreccm.web.CcmApplication;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(DomainOwnership.class)
@DependsOn({CcmApplication.class, Domain.class})
public class DomainOwnershipImExporter 
    extends AbstractEntityImExporter<DomainOwnership>{

    @Inject
    private EntityManager entityManager;
    
    @Override
    protected Class<DomainOwnership> getEntityClass() {
        
        return DomainOwnership.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void saveImportedEntity(final DomainOwnership entity) {
        
        entityManager.persist(entity);
    }
    
}
