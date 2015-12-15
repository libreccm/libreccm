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
package org.libreccm.configuration;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainManager;
import org.libreccm.categorization.DomainRepository;

import static org.libreccm.configuration.ConfigurationConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ConfigurationManager {
    
    @Inject
    private CategoryManager categoryManager;
    
    @Inject
    private DomainRepository domainRepository;
    
    @Inject
    private DomainManager domainManager;
    
    @Inject
    private EntityManager entityManager;
    
    
    
    public <T> AbstractConfigurationEntry<T> getEntry(final String name, 
                                                      final Class<T> clazz) {
        final String[] tokens = name.split(".");
        
        final Domain registry = domainRepository.findByDomainKey(REGISTRY_DOMAIN);
        
        throw new UnsupportedOperationException();
    }
    
}
