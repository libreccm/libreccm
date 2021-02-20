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
public class RoleMembershipModel {

    private String memberName;

    private String memberUuid;

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(final String memberName) {
        this.memberName = memberName;
    }

    public String getMemberUuid() {
        return memberUuid;
    }

    public void setMemberUuid(final String memberUuid) {
        this.memberUuid = memberUuid;
    }

}
