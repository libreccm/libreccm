/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.librecms.contentsection.privileges.ItemPrivileges;

import javax.enterprise.context.Dependent;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Dependent
public class CurrentUserDocumentPermissions
    extends AbstractCurrentUserPermissions {

    @Override
    protected Class<?> getPrivilegesClass() {
        return ItemPrivileges.class;
    }

}
