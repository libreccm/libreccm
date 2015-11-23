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

import org.hibernate.envers.RevisionListener;

import javax.inject.Inject;

/**
 * {@link RevisionListener} setting the user for the {@link CcmRevision} entity.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CcmRevisionListener implements RevisionListener {

//    @Inject
//    private transient CcmSessionContext sessionContext;

    @Override
    public void newRevision(final Object revisionEntity) {
        if (!(revisionEntity instanceof CcmRevision)) {
            throw new IllegalArgumentException(String.format(
                "Provided revision entity is not an instance of class \"%s\".",
                CcmRevision.class.getName()));
        }

        final CcmRevision revision = (CcmRevision) revisionEntity;
        //ToDo: Add code using Shiro Subject
        
//        final Subject subject = sessionContext.getCurrentSubject();
//        if (subject instanceof User) {
//            final User user = (User) subject;
//            revision.setUserName(user.getScreenName());
//        }
    }

}
