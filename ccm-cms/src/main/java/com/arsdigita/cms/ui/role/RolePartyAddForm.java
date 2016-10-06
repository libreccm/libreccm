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
import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.*;
import org.librecms.CmsConstants;

import java.util.Arrays;
import java.util.List;

/**
 * Adds a form which can add {@link Party parties} to {@link Role roles}.
 * Also enables searching for parties.
 *
 * NOTE: In earlier versions it was also possible to filter
 * parties using {@link User} attributes such as username, name, last name, etc.
 * This feature may be added later if still needed.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Michael Pih
 * @author Uday Mathur
 * @version $Id: RolePartyAddForm.java 287 2005-02-22 00:29:02Z sskracic $
 */
class RolePartyAddForm extends PartyAddForm {

    private static Logger s_log = Logger.getLogger
        (RolePartyAddForm.class);

    private SingleSelectionModel m_roles;

    RolePartyAddForm(SingleSelectionModel roles, TextField search) {
        super(search);

        m_roles = roles;

        getForm().addSubmissionListener
            (new FormSecurityListener(CmsConstants.PRIVILEGE_ADMINISTER_ROLES));
    }


    @Override
    protected List<Party> makeQuery(PageState s) {
        Assert.isTrue(m_roles.isSelected(s));

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PartyRepository partyRepository = cdiUtil.findBean(PartyRepository.class);

        final String searchQuery = (String) getSearchWidget().getValue(s);

        return partyRepository.searchByName(searchQuery);
    }

    @Override
    public void process(FormSectionEvent event) throws FormProcessException {
        FormData data = event.getFormData();
        PageState state = event.getPageState();
        Assert.isTrue(m_roles.isSelected(state));

        String[] parties = (String[]) data.get("parties");
        s_log.debug("PARTIES = " + Arrays.toString(parties));
        if (parties == null) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.ui.role.no_party_selected"));
        }

        final Long roleId = new Long((String) m_roles.getSelectedKey(state));

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final RoleRepository roleRepository = cdiUtil.findBean(RoleRepository.class);
        final PartyRepository partyRepository = cdiUtil.findBean(PartyRepository.class);
        final RoleManager roleManager = cdiUtil.findBean(RoleManager.class);

        final Role role = roleRepository.findById(roleId);

        // Add each checked party to the role
        Party party;
        for ( int i = 0; i < parties.length; i++ ) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("parties[" + i + "] = " + parties[i]);
            }
            party = partyRepository.findByName(parties[i]);
            roleManager.assignRoleToParty(role, party);
        }
    }
}
