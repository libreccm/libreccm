/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetPermissionsModel implements PermissionsModel {

    private boolean grantedCreateNew;

    private boolean grantedDelete;

    private boolean grantedUse;

    private boolean grantedEdit;

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
