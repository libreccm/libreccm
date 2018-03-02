/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.role;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.cms.ui.PartyAddForm;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.Assert;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;

import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;
import org.libreccm.security.RoleRepository;
import org.libreccm.security.User;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.Arrays;
import java.util.List;

/**
 * Adds a form which can add {@link Party parties} to {@link Role roles}. Also
 * enables searching for parties.
 *
 * NOTE: In earlier versions it was also possible to filter parties using
 * {@link User} attributes such as username, name, last name, etc. This feature
 * may be added later if still needed.
 *
 *
 * @author Michael Pih
 * @author Uday Mathur
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 */
class RolePartyAddForm extends PartyAddForm {

    private static final Logger LOGGER = LogManager
        .getLogger(RolePartyAddForm.class);

    private final SingleSelectionModel<String> roleSelectionModel;

    RolePartyAddForm(final SingleSelectionModel<String> roleSelectionModel, 
                     final TextField search) {
        
        super(search);

        this.roleSelectionModel = roleSelectionModel;

        super
            .getForm()
            .addSubmissionListener(
                new FormSecurityListener(AdminPrivileges.ADMINISTER_ROLES));
    }

    @Override
    protected List<Party> makeQuery(final PageState state) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PartyRepository partyRepository = cdiUtil.findBean(
            PartyRepository.class);

        final String searchQuery = (String) getSearchWidget().getValue(state);

        return partyRepository.searchByName(searchQuery);
    }

    @Override
    public void process(FormSectionEvent event) throws FormProcessException {
        
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final String[] parties = (String[]) data.get("parties");
        LOGGER.debug("PARTIES = " + Arrays.toString(parties));
        if (parties == null) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                "cms.ui.role.no_party_selected"));
        }

        final Long roleId = Long
            .parseLong(roleSelectionModel.getSelectedKey(state));

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final RoleRepository roleRepository = cdiUtil.findBean(
//            RoleRepository.class);
//        final PartyRepository partyRepository = cdiUtil.findBean(
//            PartyRepository.class);
//        final RoleManager roleManager = cdiUtil.findBean(RoleManager.class);
        final RoleAdminPaneController controller = cdiUtil
            .findBean(RoleAdminPaneController.class);

//        final Role role = roleRepository.findById(roleId).get();

        // Add each checked party to the role
//        Party party;
        for (int i = 0; i < parties.length; i++) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("parties[" + i + "] = " + parties[i]);
            }
//            party = partyRepository.findById(Long.parseLong(parties[i])).get();
//            roleManager.assignRoleToParty(role, party);
            controller.assignRoleToParty(roleId, Long.parseLong(parties[i]));
        }
    }

}
