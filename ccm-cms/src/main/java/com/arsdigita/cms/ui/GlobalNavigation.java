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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentCenter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.UI;
import com.arsdigita.ui.admin.AdminUiConstants;
import com.arsdigita.ui.login.LoginServlet;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.PermissionChecker;
import org.libreccm.web.ApplicationCreator;
import org.libreccm.web.ApplicationManager;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;
import org.librecms.CmsConstants;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 * Global navigation elements for the CMS admin UIs.</p>
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
// Made public (instead of unspecified, resulting in protected) in 6.6.8
public class GlobalNavigation extends SimpleComponent {

    private static final Logger s_log = Logger.getLogger(GlobalNavigation.class);
    private final String m_adminPath;
    private final String m_centerPath;
    private final String m_changePasswordPath;
    private final String m_helpPath;
    private final String m_signOutPath;
    private final String m_wspcPath;

    /**
     *
     */
    public GlobalNavigation() {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ApplicationManager appManager = cdiUtil.findBean(
            ApplicationManager.class);
        final ApplicationRepository appRepo = cdiUtil.findBean(
            ApplicationRepository.class);
        final Map<String, ApplicationType> appTypes = appManager
            .getApplicationTypes();
        final ApplicationType adminAppType = appTypes.get(
            AdminUiConstants.ADMIN_APP_TYPE);
        final List<CcmApplication> adminApps = appRepo.findByType(adminAppType
            .name());
        final ApplicationType contentCenterAppType = appTypes.get(
            CmsConstants.CONTENT_CENTER_APP_TYPE);
        final List<CcmApplication> contentCenterApps = appRepo.findByType(
            contentCenterAppType.name());

        m_adminPath = adminApps.get(0).getPrimaryUrl();
        m_centerPath = contentCenterApps.get(0).getPrimaryUrl();
        m_changePasswordPath = LoginServlet.getChangePasswordPageURL();
        m_helpPath = "/nowhere"; // We don't have this yet XXX.        
        m_signOutPath = LoginServlet.getLogoutPageURL();
        m_wspcPath = UI.getWorkspaceURL();
    }

    /**
     *
     * @param state
     * @param parent
     */
    @Override
    public void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final HttpServletRequest sreq = state.getRequest();

            final Element nav = parent.newChildElement("cms:globalNavigation",
                                                       CMS.CMS_XML_NS);
            final String centerTitle = (String) new GlobalizedMessage(
                "cms.ui.content_center", CmsConstants.CMS_BUNDLE).localize();
            final String adminTitle = (String) new GlobalizedMessage(
                "cms.ui.admin_center", CmsConstants.CMS_BUNDLE).localize();
            final String wspcTitle = (String) new GlobalizedMessage(
                "cms.ui.my_workspace", CmsConstants.CMS_BUNDLE).localize();
            final String signOutTitle = (String) new GlobalizedMessage(
                "cms.ui.sign_out", CmsConstants.CMS_BUNDLE).localize();
            final String helpTitle = (String) new GlobalizedMessage(
                "cms.ui.help", CmsConstants.CMS_BUNDLE).localize();
            final String changePasswordTitle = (String) new GlobalizedMessage(
                "cms.ui.change_password", CmsConstants.CMS_BUNDLE).localize();

            link(sreq, nav, "cms:contentCenter", m_centerPath, centerTitle);

            /* If the current user has admin permissions, insert a link to the admin center */
            if (CdiUtil.createCdiUtil().findBean(PermissionChecker.class)
                .isPermitted(CoreConstants.PRIVILEGE_ADMIN)) {
                link(sreq, nav, "cms:adminCenter", m_adminPath, adminTitle);
            }

            link(sreq, nav, "cms:workspace", m_wspcPath, wspcTitle);
            link(sreq, nav, "cms:changePassword", m_changePasswordPath,
                 changePasswordTitle);
            link(sreq, nav, "cms:signOut", m_signOutPath, signOutTitle);
            link(sreq, nav, "cms:help", m_helpPath, helpTitle);
        }
    }

    /**
     *
     * @param sreq
     * @param parent
     * @param name
     * @param path
     * @param title
     *
     * @return
     */
    private static Element link(final HttpServletRequest sreq,
                                final Element parent,
                                final String name,
                                final String path,
                                final String title) {
        final Element link = parent.newChildElement(name, CMS.CMS_XML_NS);

        link.addAttribute("href", URL.there(sreq, path).toString());
        link.addAttribute("title", title);

        return link;
    }

}
