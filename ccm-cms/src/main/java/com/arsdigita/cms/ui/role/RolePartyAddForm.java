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

import java.math.BigDecimal;
import java.util.List;

/**
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

    private static final String NAME_FILTER = 
        "(upper(name) like ('%' || upper(:search) || '%'))" +
        " or " +
        "(upper(email) like ('%' || upper(:search) || '%'))";

    public RolePartyAddForm(SingleSelectionModel roles, TextField search) {
        super(search);

        m_roles = roles;

        getForm().addSubmissionListener
            (new FormSecurityListener(CmsConstants.PRIVILEGE_ADMINISTER_ROLES));
    }

    /*
    protected DataQuery makeQuery(PageState s) {
        Assert.isTrue(m_roles.isSelected(s));

        Session session = SessionManager.getSession();

        // XXX: Figure out how to use role directly here
        DataQuery dq =
            session.retrieveQuery("com.arsdigita.cms.searchToAddMemberParties");

        BigDecimal roleId = new BigDecimal((String) m_roles.getSelectedKey(s));
        String searchQuery = (String) getSearchWidget().getValue(s);

        makeFilter(dq, roleId, searchQuery);
        dq.addOrder("isUser desc, upper(name), upper(email)");
        return dq;
    }*/

    @Override
    protected List<Party> makeQuery(PageState state) {
        return null;
    }

    /**
     * Filters out members of the current group and parties whose name or email
     * address matches the search string.
     */
    /*
    private void makeFilter(DataQuery dq, BigDecimal roleId, String search) {

        dq.setParameter("excludedRoleId", roleId);

        // Add the search filter if the search query is not null.
        if (search != null) {

            dq.clearFilter();
            Filter filter = dq.addFilter(NAME_FILTER);
            filter.set("search", search);
        }
    }*/

    public void process(FormSectionEvent event) throws FormProcessException {
        FormData data = event.getFormData();
        PageState state = event.getPageState();
        Assert.isTrue(m_roles.isSelected(state));

        String[] parties = (String[]) data.get("parties");
        s_log.debug("PARTIES = " + parties);
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
