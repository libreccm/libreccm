/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.usersgroupsroles;

import org.libreccm.core.EmailAddress;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("EmailFormModel")
public class EmailFormModel {

    private String userIdentifier;
    
    private int emailId;

    private String address;

    private boolean bouncing;

    private boolean verified;

    public void setEmailAddress(
        final String userIdentifier,
        final int emailId, 
        final EmailAddress emailAddress
    ) {
        this.userIdentifier = userIdentifier;
        this.emailId = emailId;
        address = emailAddress.getAddress();
        bouncing = emailAddress.isBouncing();
        verified = emailAddress.isVerified();
    }

    public boolean isNew() {
        return emailId == 0;
    }

       public String getUserIdentifier() {
        return userIdentifier;
    }

    public void setUserIdentifier(final String userIdentifier) {
        this.userIdentifier = userIdentifier;
    }
    
    public int getEmailId() {
        return emailId;
    }

    public void setEmailId(final int emailId) {
        this.emailId = emailId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public boolean isBouncing() {
        return bouncing;
    }

    public void setBouncing(boolean bouncing) {
        this.bouncing = bouncing;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

 

}
