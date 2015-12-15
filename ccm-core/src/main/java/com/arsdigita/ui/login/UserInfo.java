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

import com.arsdigita.bebop.ListPanel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;
import org.libreccm.security.User;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;


/**
 * A reusable Bebop component to display the primary attributes of the currently
 * logged in user. Users can extend this class to provide information about
 * another user by overriding the {@link
 * UserInfo#register(Page)} and {@link UserInfo#getUser(PageState)} methods.
 *
 * @author Michael Bryzek
 * @author Roger Hsueh
 * @author Sameer Ajmani
 * @author Peter Boy (refactored to eliminate old type kernel.Package* /
 * SiteNode)
 * @since 2001-06-01
 * @version 1.0
 * @version $Id$
 */
public class UserInfo extends SimpleContainer {

    /**
     * Logger instance for debugging support
     */
    private static final Logger s_log = Logger.getLogger(UserInfo.class
        .getName());

    /**
     * Holds a list of content centers (Application instances) that exist on
     * this installation. Usually there is only ONE installed, but obviously
     * care is taken that one content-center may be installed per subsite.
     */
    private List<CcmApplication> m_contentCenters;

    private UserAuthenticationListener m_listener
                                           = new UserAuthenticationListener();

    /**
     * Constructor.
     */
    public UserInfo() {
        // add list of links
        ListPanel list = new ListPanel(false);
        list.add(new DynamicLink("login.userInfo.logoutLink",
                                 LoginServlet.getLogoutPageURL()));
        list.add(new DynamicLink("login.userInfo.editProfileLink",
                                 LoginServlet.getEditUserProfilePageURL()));
        list.add(new DynamicLink("login.userInfo.changePasswordLink",
                                 LoginServlet.getRecoverPasswordPageURL()));
        add(list);

        // add user info text
        add(new SimpleComponent() {

            @Override
            public void generateXML(PageState state, Element parent) {
                if (!isLoggedIn(state)) {
                    s_log.debug("user is not logged in, so no XML generated");
                    return;
                }
                final User user = getUser(state);

                final Element userElement = new Element(
                    "subsite:userInfo", LoginServlet.SUBSITE_NS_URI);

                // check if Content-Center (CMS Workspace) is installed and
                // instantiated, if so collect all instantiated workspace apps.
                if (m_contentCenters == null) {

                    m_contentCenters = new ArrayList<>();

                    final CdiUtil cdiUtil = new CdiUtil();
                    final ApplicationRepository appRepo = cdiUtil.findBean(
                            ApplicationRepository.class);
                    
                    m_contentCenters = appRepo.findByType(
                        "com.arsdigita.cms.ContentCenter");
                }

                // work on the list of installed (instantiated) 
                // workspaces (content-centers) if any and add some attributes
                // to XML generator
                if (m_contentCenters.size() > 0) {

                    Element workspaceURL;
                    Element contentCenters = userElement.newChildElement(
                        "subsite:contentCenters",
                        LoginServlet.SUBSITE_NS_URI);

                    // step through instantiated workspaces (content-centers)
                    for (CcmApplication application : m_contentCenters) {
                        // for each instance of Workspace = for each installed
                        // (instantiated) Workspace application:
                        // Add an Element
                        final Element center = contentCenters
                            .newChildElement("subsite:center",
                                             LoginServlet.SUBSITE_NS_URI);

                        // Add attribute name = URL without trailing/leading "/"
                        center.addAttribute("name", application.getTitle()
                                            .getValue(DispatcherHelper
                                                .getNegotiatedLocale()));

                        workspaceURL = center.newChildElement(
                            "subsite:url",
                            LoginServlet.SUBSITE_NS_URI);

                        // get URL of Workspace application (instance)
                        final URL url = URL.there(state.getRequest(),
                                                  application.getPrimaryUrl());
                        workspaceURL.setText(url.toString());
                    }
                }

                // in any case: add basic user attributes
                userElement.addAttribute("id",
                                         Long.toString(user.getPartyId()));
                if (!user.getEmailAddresses().isEmpty()) {
                    userElement.addAttribute("email",
                                             user.getEmailAddresses().get(0)
                                             .getAddress());
                }
                userElement.addAttribute(
                    "name", String.format("%s %s",
                                          user.getGivenName(),
                                          user.getFamilyName()));
                userElement.addAttribute("screenName", user.getName());

                parent.addContent(userElement);
            }

        });
    }

    /**
     * Adds a request listener to the page to ensure that the user is logged in.
     * Subclasses should override this method if they do not require users to be
     * logged in. This method may be changed as we find more examples of how
     * people are using this class.
     *
     * @pre p != null
     *
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.addRequestListener(m_listener);
    }

    /**
     * @param state
     * @return true if the user is logged in
     *
     */
    protected boolean isLoggedIn(final PageState state) {
        return m_listener.isLoggedIn(state);
    }

    /**
     * @param state
     * @return the User object for which we are generating information
     *
     * @throws IllegalStateException if user is not logged in. Call
     *                               isLoggedIn(state) to check for this case.
     *
     * @pre state != null
     * @post return != null
     *
     */
    protected User getUser(final PageState state) {
        if (!isLoggedIn(state)) {
            throw new IllegalStateException("user is not logged in");
        }
        
        final CdiUtil cdiUtil = new CdiUtil();
        final Shiro shiro = cdiUtil.findBean(Shiro.class);
        final User user = shiro.getUser();
        
        return user;
    }

}
