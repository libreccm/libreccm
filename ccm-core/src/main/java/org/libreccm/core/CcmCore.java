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
package org.libreccm.core;

import org.libreccm.core.modules.CcmModule;
import org.libreccm.core.modules.InitEvent;
import org.libreccm.core.modules.InstallEvent;
import org.libreccm.core.modules.Module;
import org.libreccm.core.modules.ShutdownEvent;
import org.libreccm.core.modules.UnInstallEvent;

import javax.persistence.EntityManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Module(entities = {org.libreccm.auditing.CcmRevision.class,
                    org.libreccm.categorization.Category.class,
                    org.libreccm.categorization.Categorization.class,
                    org.libreccm.categorization.Domain.class,
                    org.libreccm.categorization.DomainOwnership.class,
                    org.libreccm.core.CcmObject.class,
                    org.libreccm.core.Group.class,
                    org.libreccm.core.GroupMembership.class,
                    org.libreccm.core.Permission.class,
                    org.libreccm.core.Privilege.class,
                    org.libreccm.core.Resource.class,
                    org.libreccm.core.ResourceType.class,
                    org.libreccm.core.Role.class,
                    org.libreccm.core.Subject.class,
                    org.libreccm.core.User.class,
                    org.libreccm.core.modules.InstalledModule.class})
public class CcmCore implements CcmModule {

    @Override
    public void install(final InstallEvent event) {
        final EntityManager entityManager = event.getEntityManager();
        
        final User user = new User();
        user.setScreenName("public-user");
        final PersonName name = new PersonName();
        name.setFamilyName("ccm");
        name.setGivenName("public user");
        user.setName(name);
        final EmailAddress email = new EmailAddress();
        email.setAddress("public-user@localhost");
        user.addEmailAddress(email);
        
        entityManager.persist(user);
    }

    @Override
    public void init(final InitEvent event) {
        //Nothing
    }

    @Override
    public void shutdown(final ShutdownEvent event) {
        //Nothing
    }

    @Override
    public void uninstall(final UnInstallEvent event) {
        //Nothing
    }

}
