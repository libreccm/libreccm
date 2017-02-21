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
package org.libreccm.auditing;

import java.util.Optional;
import org.hibernate.envers.RevisionListener;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;


/**
 * {@link RevisionListener} setting the user for the {@link CcmRevision} entity.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CcmRevisionListener implements RevisionListener {

    @Override
    public void newRevision(final Object revisionEntity) {
        
        if (!(revisionEntity instanceof CcmRevision)) {
            throw new IllegalArgumentException(String.format(
                "Provided revision entity is not an instance of class \"%s\".",
                CcmRevision.class.getName()));
        }
        final CcmRevision revision = (CcmRevision) revisionEntity;
        
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final Shiro shiro = cdiUtil.findBean(Shiro.class);
        
        final Optional<User> user = shiro.getUser();
        if (user.isPresent()) {
            revision.setUserName(user.get().getName());
        }
    }

}
