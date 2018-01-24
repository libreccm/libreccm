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

import com.fasterxml.jackson.annotation.ObjectIdGenerator;

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 3/23/17
 */
public class GroupMembershipIdGenerator extends ObjectIdGenerator<String> {
    private static final long serialVersionUID = 5256221797846627835L;

    @Override
    public Class<?> getScope() {
        return GroupMembership.class;
    }

    @Override
    public boolean canUseFor(final ObjectIdGenerator<?> gen) {
        return gen instanceof GroupMembershipIdGenerator;
    }

    @Override
    public ObjectIdGenerator<String> forScope(final Class<?> scope) {
        return this;
    }

    @Override
    public ObjectIdGenerator<String> newForSerialization(final Object context) {
        return this;
    }

    @Override
    public IdKey key(final Object key) {
        if (key == null) {
            return null;
        }
        return new IdKey(GroupMembership.class, GroupMembership.class, key);
    }

    @Override
    public String generateId(final Object forPojo) {
        if (!(forPojo instanceof GroupMembership)) {
            throw new IllegalArgumentException(
                    "Only GroupMembership instances are supported.");
        }

        final GroupMembership membership = (GroupMembership) forPojo;

        return String.format("{%s}{%s}",
                membership.getGroup().getName(),
                membership.getMember().getName());
    }
}
