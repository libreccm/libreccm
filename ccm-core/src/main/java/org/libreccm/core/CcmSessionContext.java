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

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;

/**
 * This bean stores several data about the current session, for example the
 * current party.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@SessionScoped
public class CcmSessionContext implements Serializable {

    private static final long serialVersionUID = 6110177865273823685L;

    private Subject currentSubject;
    private Subject effectiveSubject;

    public Subject getCurrentSubject() {
        return currentSubject;
    }

    public void setCurrentSubject(final Subject currentSubject) {
        this.currentSubject = currentSubject;
        this.effectiveSubject = currentSubject;
    }

    public Subject getEffectiveSubject() {
        return effectiveSubject;
    }

    protected void setEffectiveSubject(final Subject effectiveSubject) {
        this.effectiveSubject = effectiveSubject;
    }
    
    public boolean isLoggedIn() {
        return currentSubject != null;
    }
    
    /**
     * Execute code under different privileges. Useful if no current user is 
     * available, for example in the startup phase. 
     * 
     * The there is a current user the method will check if the current user
     * has the permission to use the {@code sudo} method.
     * 
     * @param subject The party with which permissions the code is executed.
     * @param runnable The code to execute.
     */
    public void sudo(final Subject subject, final Runnable runnable) {
        //ToDo: Check if current user is permitted to use sudo.
        
        effectiveSubject = subject;
        runnable.run();
        effectiveSubject = currentSubject;
    }

}
