/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.ui.login.LoginConstants;
import com.arsdigita.ui.login.LoginServlet;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

import org.apache.shiro.subject.Subject;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import javax.servlet.http.HttpServletRequest;

import static com.arsdigita.ui.UI.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserBanner extends SimpleComponent {

    @Override
    public void generateXML(final PageState state,
                            final Element parentElem) {

        super.generateXML(state, parentElem);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final Subject subject = cdiUtil.findBean(Subject.class);

        final Element contentElem = parentElem.newChildElement("ui:userBanner",
                                                               UI_XML_NS);

        exportAttributes(contentElem);

        if (subject.isAuthenticated()) {
            final KernelConfig config = KernelConfig.getConfig();
            final UserRepository userRepository = cdiUtil.findBean(
                UserRepository.class);

            final User user;
            if ("email".equals(config.getPrimaryUserIdentifier())) {
                user = userRepository.findByEmailAddress(
                    (String) subject.getPrincipal());
            } else {
                user = userRepository
                    .findByName((String) subject.getPrincipal());
            }

            if (user != null) {
                contentElem.addAttribute("givenName", user.getGivenName());
                contentElem.addAttribute("familyName", user.getFamilyName());
                contentElem.addAttribute("screenName", user.getName());
                contentElem.addAttribute("primaryEmail",
                                         user.getPrimaryEmailAddress()
                                         .getAddress());
                contentElem.addAttribute("userID",
                                         Long.toString(user.getPartyId()));
            }
        }

        final HttpServletRequest request = state.getRequest();
        
        contentElem.addAttribute("changePasswordLabel",
                                 (String) new GlobalizedMessage(
                                     "ui.admin.change_password",
                                     UI_BUNDLE_NAME).localize(request));

        contentElem.addAttribute("helpLabel",
                                 (String) new GlobalizedMessage(
                                     "ui.admin.help",
                                     UI_BUNDLE_NAME).localize(request));

        contentElem.addAttribute("portalLabel",
                                 (String) new GlobalizedMessage(
                                     "ui.admin.portal",
                                     UI_BUNDLE_NAME).localize(request));

        contentElem.addAttribute("signoutLabel",
                                 (String) new GlobalizedMessage(
                                     "ui.admin.signout",
                                     UI_BUNDLE_NAME).localize(request));

        contentElem.addAttribute("greeting",
                                 (String) new GlobalizedMessage(
                                     "ui.admin.greeting",
                                     UI_BUNDLE_NAME).localize(request));

        contentElem.addAttribute("workspaceURL",
                                 URL.there(state.getRequest(),
                                           UI.getWorkspaceURL()).toString());

        contentElem.addAttribute("loginURL",
                                 URL.there(state.getRequest(),
                                           LoginConstants.LOGIN_PAGE_URL)
                                 .toString());

        contentElem.addAttribute("logoutURL",
                                 URL.there(state.getRequest(),
                                           LoginServlet.getLogoutPageURL())
                                 .toString());

        contentElem.addAttribute("changePasswordURL",
                                 URL.there(state.getRequest(),
                                           LoginServlet
                                           .getChangePasswordPageURL())
                                 .toString());

    }

}
