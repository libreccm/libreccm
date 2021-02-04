/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import java.util.Collections;
import java.util.List;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PrivilegesGrantedToRoleModel {

    private String grantee;

    private List<GrantedPrivilegeModel> grantedPrivileges;

    public String getGrantee() {
        return grantee;
    }

    public void setGrantee(final String grantee) {
        this.grantee = grantee;
    }

    public List<GrantedPrivilegeModel> getGrantedPrivileges() {
        return Collections.unmodifiableList(grantedPrivileges);
    }

    public void setGrantedPrivileges(
        final List<GrantedPrivilegeModel> grantedPrivileges
    ) {
        this.grantedPrivileges = grantedPrivileges;
    }

}
