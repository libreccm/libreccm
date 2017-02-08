/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.permissions;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.util.StringUtils;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Party;
import org.libreccm.security.Role;
import org.libreccm.security.RoleRepository;

import java.util.List;

import static com.arsdigita.cms.ui.permissions.CMSPermissionsConstants.*;

/**
 * User Search Form for permissions.
 *
 * @author Stefan Deusch (stefan@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CMSUserSearchForm extends Form implements FormProcessListener {

    private CMSPermissionsPane parent;
    private TextField searchField;

    public CMSUserSearchForm(CMSPermissionsPane parent) {
        this(DEFAULT_PRIVILEGES, parent);
    }

    public CMSUserSearchForm(final String[] privileges,
                             final CMSPermissionsPane parent) {
        super("RoleSearchUsers", new SimpleContainer());

        this.parent = parent;
        setMethod(Form.POST);

        addProcessListener(this);

        add(new Label(SEARCH_LABEL));
        add(new Label("&nbsp;", false));

        final StringParameter searchParam = new StringParameter(SEARCH_QUERY);
        searchField = new TextField(searchParam);
        searchField.addValidationListener(new NotEmptyValidationListener());
        searchField.setSize(20);
        add(searchField, ColumnPanel.RIGHT);

        final Submit submit = new Submit(SEARCH_BUTTON);
        add(submit, ColumnPanel.LEFT);
    }

    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {

        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final String search = StringUtils.stripWhiteSpace((String) data.get(
            SEARCH_QUERY));

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final RoleRepository roleRepo = cdiUtil.findBean(RoleRepository.class);

        final List<Role> roles = roleRepo.searchByName(search);

        if (roles.isEmpty()) {
//            parent.showNoResults(state);
        } else {
            // put search string into Page
            state.setValue(getSearchString(), data.get(SEARCH_QUERY));

            // put privileges into Page
            state.setValue(getPrivilegeModel(), getPrivileges());

//            parent.showGrant(state);
        }

    }

    /**
     * Hide Delegate pattern, if parent's implementation changes.
     */
    private ParameterModel getSearchString() {
        return parent.getSearchString();
    }

    /**
     * Detto
     */
    private ParameterModel getPrivilegeModel() {
        return parent.getPrivilegeParam();
    }

    /**
     * Detto
     */
    private Object[] getPrivileges() {
        return parent.getPrivileges();
    }

    public TextField getSearchWidget() {
        return searchField;
    }

}
