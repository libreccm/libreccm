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
package com.arsdigita.ui.admin.usersgroupsroles.users;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.security.User;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * Model for the {@link PrimaryEmailTable}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PrimaryEmailTableModel implements TableModel {

    protected static final int COL_ADDRESS = 0;
    protected static final int COL_VERIFIED = 1;
    protected static final int COL_BOUNCING = 2;
    protected static final int COL_ACTION = 3;
    
    private final User user;
    private boolean finished = false;
    
    public PrimaryEmailTableModel(final User user) {
        this.user = user;
    }
    
    @Override
    public int getColumnCount() {
        return 4;
    }

    @Override
    public boolean nextRow() {
        if (finished) {
            return false;
        } else {
            finished = true;
            return true;
        }
    }

    @Override
    public Object getElementAt(final int columnIndex) {
        switch(columnIndex) {
            case COL_ADDRESS:
                return user.getPrimaryEmailAddress().getAddress();
            case COL_VERIFIED:
                return Boolean.toString(user.getPrimaryEmailAddress().isVerified());
            case COL_BOUNCING:
                return Boolean.toString(user.getPrimaryEmailAddress().isBouncing());
            case COL_ACTION:
                return new Label(new GlobalizedMessage(
                    "ui.admin.user.primary_email_address.edit", ADMIN_BUNDLE));
            default:
                throw new IllegalArgumentException("Invalid column index.");
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return user.getPrimaryEmailAddress().getAddress();
    }
    
}
