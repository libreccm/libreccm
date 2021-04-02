/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import org.librecms.contentsection.privileges.AssetPrivileges;

/**
 * Model describing the permissions granted to the current user for an
 * {@link Asset} or an assets {@link Folder}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetPermissionsModel implements PermissionsModel {

    /**
     * Has the user been granted the {@link AssetPrivileges#CREATE_NEW}
     * privilege?
     */
    private boolean grantedCreateNew;

    /**
     * Has the user been granted the {@link AssetPrivileges#DELETE} privilege?
     */
    private boolean grantedDelete;

    /**
     * Has the user been granted the {@link AssetPrivileges#USE} privilege?
     */
    private boolean grantedUse;

    /**
     * Has the user been granted the {@link AssetPrivileges#EDIT} privilege?
     */
    private boolean grantedEdit;

    /**
     * Has the user been granted the {@link AssetPrivileges#VIEW} privilege?
     */
    private boolean grantedView;

    public boolean isGrantedCreateNew() {
        return grantedCreateNew;
    }

    public void setGrantedCreateNew(final boolean grantedCreateNew) {
        this.grantedCreateNew = grantedCreateNew;
    }

    public boolean isGrantedDelete() {
        return grantedDelete;
    }

    public void setGrantedDelete(final boolean grantedDelete) {
        this.grantedDelete = grantedDelete;
    }

    public boolean isGrantedUse() {
        return grantedUse;
    }

    public void setGrantedUse(final boolean grantedUse) {
        this.grantedUse = grantedUse;
    }

    public boolean isGrantedEdit() {
        return grantedEdit;
    }

    public void setGrantedEdit(final boolean grantedEdit) {
        this.grantedEdit = grantedEdit;
    }

    public boolean isGrantedView() {
        return grantedView;
    }

    public void setGrantedView(final boolean grantedView) {
        this.grantedView = grantedView;
    }

}
