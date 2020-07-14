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
package org.libreccm.security.authorization;

import org.apache.shiro.subject.Subject;
import org.libreccm.core.CcmObject;
import org.libreccm.security.AuthorizationInterceptorIT;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.RequiresPrivilege;
import org.libreccm.security.RequiresRole;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

/**
 * A simple bean used by the {@link AuthorizationInterceptorIT}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class LabBean {
    
    @Inject
    private Subject subject;
    
    @AuthorizationRequired
    @RequiresRole("role1")
    public void doSomethingWhichRequiresRole() {
        assertThat(subject.hasRole("role1"), is(true));
    }
    
    @AuthorizationRequired
    @RequiresPrivilege("privilege1")
    public void doSomethingWhichRequiresPermission() {
        assertThat(subject.isPermitted("privilege1"), is(true));
    }
    
    @AuthorizationRequired
    public void doSomethingWhichRequiresPermissionOnObject(
        @RequiresPrivilege("privilege2")
        final CcmObject object) {
        assertThat(subject.isPermitted(
            String.format("privilege2:%d", object.getObjectId())), 
                   is(true));
    }
}
