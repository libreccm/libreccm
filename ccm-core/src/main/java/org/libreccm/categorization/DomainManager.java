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

import org.libreccm.web.Application;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Provides several methods when managing the relations between {@link Domain}s
 * and their owning {@link Application}s.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class DomainManager {

    @Inject
    private transient DomainRepository domainRepo;

    /**
     * Adds a {@code Application} to the owners of a {@link Domain}. If the
     * provided {@code Application} is already an owner of the provided
     * {@code Domain} the method does nothing.
     *
     * @param application The {@code Application} to add to the owners of the
     *                    {@code Domain}.
     * @param domain      The {@code Domain} to which owners the
     *                    {@code Application is added}.
     */
    public void addDomainOwner(final Application application,
                               final Domain domain) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    /**
     * Removes a {@code Application} from the owners of a {@code Domain}. If the
     * provided {@code Application} is not an owner of the provided
     * {@code Domain} the method does nothing.
     *
     * @param application The {@code Application} to remove from the owners of
     *                    the provided {@code Domain}.
     * @param domain      The {@code Domain} from which owners the provided
     *                    {@code Application} should be removed.
     */
    public void removeDomainOwner(final Application application,
                                  final Domain domain) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

    /**
     * Determines if a {@link Application} is an owner of {@link Domain}.
     * 
     * @param application The {@code Application} to test.
     * @param domain The {@code Domain} to test.
     * @return {@code true} if the provided {@code Application} is an owner 
     * of the provided {@code Domain}, {@code false} otherwise.
     */
    public boolean isDomainOwner(final Application application,
                                 final Domain domain) {
        // TODO implement method
        throw new UnsupportedOperationException();
    }

}
