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
package org.libreccm.categorization;

import org.libreccm.configuration.ConfigurationConstants;
import org.libreccm.modules.InstallEvent;

import javax.persistence.EntityManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RegistrySetup {

    private final EntityManager entityManager;

    public RegistrySetup(final InstallEvent event) {
        this.entityManager = event.getEntityManager();
    }

    public void setup() {
        final Domain registry = new Domain();
        registry.setDomainKey(ConfigurationConstants.REGISTRY_DOMAIN);
        registry.setVersion("1.0");
        registry.setDisplayName(ConfigurationConstants.REGISTRY_DOMAIN);
        
        final Category root = new Category();
        root.setName(ConfigurationConstants.REGISTRY_DOMAIN + "-root");
        root.setDisplayName(ConfigurationConstants.REGISTRY_DOMAIN + "-root");
        
        registry.setRoot(root);
        
        entityManager.persist(root);
        entityManager.persist(registry);
    }

}
