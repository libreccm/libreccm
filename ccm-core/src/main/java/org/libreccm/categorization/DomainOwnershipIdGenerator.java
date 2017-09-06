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

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 8/10/17
 */
public class DomainOwnershipIdGenerator extends ObjectIdGenerator<String> {
    @Override
    public Class<?> getScope() {
        return DomainOwnership.class;
    }

    @Override
    public boolean canUseFor(ObjectIdGenerator<?> objectIdGenerator) {
        return objectIdGenerator instanceof DomainOwnershipIdGenerator;
    }

    @Override
    public ObjectIdGenerator<String> forScope(Class<?> aClass) {
        return this;
    }

    @Override
    public ObjectIdGenerator<String> newForSerialization(Object o) {
        return this;
    }

    @Override
    public IdKey key(Object key) {
        if (key == null)
            return null;
        return new IdKey(DomainOwnership.class, DomainOwnership.class, key);

    }

    @Override
    public String generateId(Object forPojo) {
        if (!(forPojo instanceof DomainOwnership)) {
            throw new IllegalArgumentException(
                    "Only DomainOwnerships instances are supported.");
        }

        final DomainOwnership domainOwnership = (DomainOwnership) forPojo;

        return String.format("{%s}{%s}",
                domainOwnership.getDomain().getDomainKey(),
                domainOwnership.getOwner().getPrimaryUrl());
    }
}
