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

import org.libreccm.core.EmailAddress;
import org.libreccm.security.User;

import java.util.List;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class EmailTableModel implements TableModel {

    protected static final int COL_ADDRESS = 0;
    protected static final int COL_VERIFIED = 1;
    protected static final int COL_BOUNCING = 2;
    protected static final int COL_EDIT = 3;
    protected static final int COL_DELETE = 4;

    private final List<EmailAddress> emailAddresses;
    private int index = -1;
    private boolean finished;

    public EmailTableModel(final User user) {
        this.emailAddresses = user.getEmailAddresses();
    }

    @Override
    public int getColumnCount() {
        return 5;
    }

    @Override
    public boolean nextRow() {
        if (emailAddresses == null || emailAddresses.isEmpty()) {
            return false;
        }
//        if (index < emailAddresses.size()) {
//            index++;
//            return true;
//        } else {
//            return false;
//        }

        index++;
        return index < emailAddresses.size();
    }

    @Override
    public Object getElementAt(final int columnIndex) {
        switch (columnIndex) {
            case COL_ADDRESS:
                return emailAddresses.get(index).getAddress();
            case COL_VERIFIED:
                return Boolean.toString(emailAddresses.get(index).isVerified());
            case COL_BOUNCING:
                return Boolean.toString(emailAddresses.get(index).isBouncing());
            case COL_EDIT:
                return new Label(new GlobalizedMessage(
                    "ui.admin.user.email_addresses.edit",
                    ADMIN_BUNDLE));
            case COL_DELETE:
                return new Label(new GlobalizedMessage(
                    "ui.admin.user.email_addresses.delete",
                    ADMIN_BUNDLE));
            default:
                throw new IllegalArgumentException("Invalid column index.");
        }
    }

    @Override
    public Object getKeyAt(final int columnIndex) {
        return emailAddresses.get(index).getAddress();
    }

}
