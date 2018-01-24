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
package org.libreccm.categorization;

import org.libreccm.portation.AbstractMarshaller;
import org.libreccm.portation.Marshals;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import java.io.Serializable;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 8/22/17
 */
@RequestScoped
@Marshals(Domain.class)
public class DomainMarshaller extends AbstractMarshaller<Domain> {

    private static final long serialVersionUID = -2435714410695539890L;

    @Inject
    private DomainRepository domainRepository;

    @Override
    protected Class<Domain> getObjectClass() {
        return Domain.class;
    }

    @Override
    protected void insertIntoDb(Domain portableObject) {
        domainRepository.save(portableObject);
    }
}
