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
package org.libreccm.security;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.portation.AbstractMarshaller;
import org.libreccm.portation.Marshals;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.transaction.Transactional;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created on 11/7/16
 */
@RequestScoped
@Marshals(RoleMembership.class)
public class RoleMembershipMarshaller extends AbstractMarshaller<RoleMembership> {

    private static final Logger LOGGER = LogManager.getLogger(RoleMembershipMarshaller.class);
    
    @Inject
    private EntityManager entityManager;

    @Override
    protected Class<RoleMembership> getObjectClass() {
        return RoleMembership.class;
    }

    @Override
    @Transactional(Transactional.TxType.REQUIRED)
    protected void insertIntoDb(final RoleMembership portableObject) {
        portableObject.setMembershipId(portableObject.getMembershipId() * -1);
        entityManager.merge(portableObject);
        entityManager.flush();
    }

}
