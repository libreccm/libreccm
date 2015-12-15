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
package com.arsdigita.ui.login;

// import com.arsdigita.kernel.security.LegacyInitializer;
import com.arsdigita.ui.UI;
import com.arsdigita.web.URL;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.parameters.URLParameter;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.ReturnSignal;

import javax.servlet.http.HttpServletRequest;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.EmailAddress;
import org.libreccm.security.User;

import org.libreccm.security.Shiro;
import org.libreccm.security.UserRepository;

/**
 * Edits a user. If returnURL is passed in to the form, then redirects to that
 * URL_MSG; otherwise redirects to the user workspace.
 *
 *
 * @author Sameer Ajmani
 *
 * @version $Id$
 *
 *
 */
public class UserEditForm extends UserForm
    implements FormProcessListener {

//    private static final Logger s_log = Logger.getLogger(UserEditForm.class);
    private UserAuthenticationListener m_listener
                                           = new UserAuthenticationListener();
    private final Hidden m_returnURL;
    private final RequestLocal m_user = new RequestLocal() {

        @Override
        public Object initialValue(final PageState ps) {
            final CdiUtil cdiUtil = new CdiUtil();
            final Shiro shiro = cdiUtil.findBean(Shiro.class);
            final User result = shiro.getUser();

            return result;
        }

    };

    public UserEditForm() {
        super("user-edit", new ColumnPanel(2), false);

        addProcessListener(this);

        // export return URL_MSG
        m_returnURL = new Hidden(new URLParameter(
            LoginHelper.RETURN_URL_PARAM_NAME));
        m_returnURL.setPassIn(true);
        add(m_returnURL);
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addRequestListener(m_listener);
    }

    @Override
    protected User getUser(final PageState state) {
        return (User) m_user.get(state);
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {
        FormData data = event.getFormData();
        PageState state = event.getPageState();

        final CdiUtil cdiUtil = new CdiUtil();
        final UserRepository userRepository = cdiUtil.findBean(
            UserRepository.class);

        User user = getUser(state);
        if (user == null) {
            throw new UncheckedWrapperException(
                "Failed to retrieve user from page state");
        }

        user.setGivenName((String) m_firstName.getValue(state));
        user.setFamilyName((String) m_lastName.getValue(state));
        user.setName((String) m_screenName.getValue(state));
        final EmailAddress newAddress = new EmailAddress();
        newAddress.setAddress(data.get(FORM_EMAIL).toString());
        user.setPrimaryEmailAddress(newAddress);
        userRepository.save(user);

        // redirect to workspace or return URL_MSG, if specified
        final HttpServletRequest req = state.getRequest();
        final String path = UI.getWorkspaceURL();
        final URL fallback = com.arsdigita.web.URL.there(req, path);
        throw new ReturnSignal(req, fallback);
    }

}
