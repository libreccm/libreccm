/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;

import org.librecms.contentsection.ContentItem;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.dispatcher.Utilities;
import com.arsdigita.toolbox.ui.ComponentAccess;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.ItemPrivileges;

/**
 * A <code>ComponentAccess</code> implementation that respects workflow
 *
 * @author Stanislav Freidin
 * @author Uday Mathur
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class WorkflowLockedComponentAccess extends ComponentAccess {

    private static final Logger LOGGER = LogManager.getLogger(
        WorkflowLockedComponentAccess.class);
    private final ItemSelectionModel itemModel;

    /**
     * Constructor.
     *
     * @param component The component
     * @param itemModel
     */
    public WorkflowLockedComponentAccess(final Component component,
                                         final ItemSelectionModel itemModel) {
        super(component);
        this.itemModel = itemModel;
    }

    /**
     * Constructor.
     *
     * @param component The component
     * @param check     An access check
     */
    public WorkflowLockedComponentAccess(final Component component,
                                         final String check,
                                         final ItemSelectionModel itemModel) {
        super(component, check);
        this.itemModel = itemModel;
    }

    /**
     * Check if this item is locked from the workflow module. In addition check
     * if all the access checks registered to the component pass.
     *
     * @param state    The page state
     *
     * @return true if all the access checks pass, false otherwise
     *
     */
    @Override
    public boolean canAccess(final PageState state) {
        final ContentItem item = itemModel.getSelectedObject(state);

        if (isVisible(state) == true) {
            if (super.canAccess(state)) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PermissionChecker permissionChecker = cdiUtil.findBean(
                    PermissionChecker.class);

                final boolean result = permissionChecker.isPermitted(
                    ItemPrivileges.EDIT, item);
                LOGGER.debug(
                    "Superclass security check passed. Result of check "
                        + "for permission {}.{} is {}.",
                    ItemPrivileges.class.getName(),
                    ItemPrivileges.EDIT,
                    result);

                return result;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * Override this method to change visiblity of action link created by
     * SecurityPropertyEditor add-method. If this method returns false, the link
     * will be hidden, p.ex. to hide a delete link if the component is already
     * empty.
     *
     * @param state The page state
     *
     * @return true for default behavior
     */
    public boolean isVisible(final PageState state) {
        return true;
    }

}
