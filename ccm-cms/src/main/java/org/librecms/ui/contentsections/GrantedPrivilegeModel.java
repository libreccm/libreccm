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
public class GrantedPrivilegeModel {

    private String privilege;

    private boolean granted;

    private boolean inherited;

    public String getPrivilege() {
        return privilege;
    }

    protected void setPrivilege(final String privilege) {
        this.privilege = privilege;
    }

    public boolean isGranted() {
        return granted;
    }

    protected void setGranted(final boolean granted) {
        this.granted = granted;
    }

    public boolean isInherited() {
        return inherited;
    }

    protected void setInherited(final boolean inherited) {
        this.inherited = inherited;
    }

}
