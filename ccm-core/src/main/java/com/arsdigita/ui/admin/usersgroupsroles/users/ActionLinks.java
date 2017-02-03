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
package com.arsdigita.ui.admin.usersgroupsroles.users;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Text;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.ChallengeManager;
import org.libreccm.security.User;
import org.libreccm.security.UserRepository;

import javax.mail.MessagingException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * A panel contains several action links used in the {@link UserDetails}
 * component.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ActionLinks extends BoxPanel {

    private static final Logger LOGGER = LogManager.getLogger(ActionLinks.class);

    public ActionLinks(final UserAdmin userAdmin,
                       final ParameterSingleSelectionModel<String> selectedUserId) {

        super(BoxPanel.HORIZONTAL);

        setIdAttr("userDetailsActionLinks");

        final ActionLink editUserDetailsLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.edit", ADMIN_BUNDLE));
        editUserDetailsLink.addActionListener(e -> {
            userAdmin.showUserEditForm(e.getPageState());
        });
        add(editUserDetailsLink);
        add(new Text(" | "));

        final ActionLink setPasswordLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.set_password",
                                  ADMIN_BUNDLE));
        setPasswordLink.addActionListener(e -> {
            userAdmin.showPasswordSetForm(e.getPageState());
        });
        add(setPasswordLink);
        add(new Text(" | "));

        final ActionLink generatePasswordLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.generate_password",
                                  ADMIN_BUNDLE));
        generatePasswordLink.setConfirmation(new GlobalizedMessage(
            "ui.admin.user_details.generate_password.confirm",
            ADMIN_BUNDLE));
        generatePasswordLink.addActionListener(e -> {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final UserRepository userRepository = cdiUtil.findBean(
                UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(
                selectedUserId.getSelectedKey(e.getPageState()))).get();
            final ChallengeManager challengeManager = cdiUtil.findBean(
                ChallengeManager.class);
            try {
                challengeManager.sendPasswordRecover(user);
            } catch (MessagingException ex) {
                LOGGER.error("Failed to send email to user.", ex);
            }
        });
        add(generatePasswordLink);
    }

}
