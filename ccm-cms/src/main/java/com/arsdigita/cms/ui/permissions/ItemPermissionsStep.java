/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.permissions;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.ResettableContainer;
import com.arsdigita.ui.CcmObjectSelectionModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.security.PermissionManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.ui.authoring.ContentItemAuthoringStep;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemAuthoringStep(
    labelBundle = CmsConstants.CMS_BUNDLE,
    labelKey = "item_permissions_step.label",
    descriptionBundle = CmsConstants.CMS_BUNDLE,
    descriptionKey = "item_permissions_step.description"
)
public class ItemPermissionsStep extends ResettableContainer {

    public ItemPermissionsStep(final ItemSelectionModel itemSelectionModel,
                               final AuthoringKitWizard authoringKitWizard,
                               final StringParameter selectedLanguage) {

        super("cms:permissionsStep", CMS.CMS_XML_NS);

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        super.add(panel);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionManager permissionManager = cdiUtil.findBean(
            PermissionManager.class);
        final List<String> privileges = permissionManager
            .listDefiniedPrivileges(ItemPrivileges.class);
        final Map<String, String> privNameMap = new HashMap<>();
        privileges.forEach(privilege -> privNameMap.put(privilege, privilege));

        final CcmObjectSelectionModel<CcmObject> objSelectionModel
                                                     = new CcmObjectSelectionModel<>(
                itemSelectionModel.getStateParameter().getName());

        final CMSPermissionsPane permissionsPane = new CMSPermissionsPane(
            privileges.toArray(new String[]{}),
            privNameMap,
            objSelectionModel);
        panel.add(permissionsPane);
    }

}
