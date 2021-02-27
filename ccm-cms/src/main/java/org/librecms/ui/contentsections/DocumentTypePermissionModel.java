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
public class DocumentTypePermissionModel {

    private String roleUuid;

    private String roleName;

    private boolean canUse;

    public String getRoleUuid() {
        return roleUuid;
    }

    public void setRoleUuid(final String roleUuid) {
        this.roleUuid = roleUuid;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }

    public boolean isCanUse() {
        return canUse;
    }

    public void setCanUse(final boolean canUse) {
        this.canUse = canUse;
    }

}
