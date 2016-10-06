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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionAdapter;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.PartySearchForm;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;
import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.core.CoreConstants;
import org.libreccm.security.*;
import org.librecms.CmsConstants;

import java.util.stream.Collectors;

/**
 * TODO Needs a description
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: BaseRoleItemPane.java 287 2005-02-22 00:29:02Z sskracic $
 */
class BaseRoleItemPane extends BaseItemPane {

    private static final Logger s_log = Logger.getLogger
        (BaseRoleItemPane.class);

    private final RoleRequestLocal m_role;
    private final SingleSelectionModel m_model;

    private final MemberTable m_members;
    //private final AdminTable m_admins;

    private final SimpleContainer m_detailPane;

    public BaseRoleItemPane(final SingleSelectionModel model,
                            final RoleRequestLocal role,
                            final ActionLink editLink,
                            final ActionLink deleteLink) {
        m_model = model;
        m_role = role;

        m_members = new MemberTable();
        //m_admins = new AdminTable();

        final ActionLink memberAddLink = new ActionLink
            (new Label(gz("cms.ui.role.member.add")));

        /*
        final ActionLink adminAddLink = new ActionLink
            (new Label(gz("cms.ui.role.admin.add")));*/

        m_detailPane = new SimpleContainer();
        add(m_detailPane);
        setDefault(m_detailPane);

        m_detailPane.add(new SummarySection(editLink, deleteLink));
        m_detailPane.add(new MemberSection(memberAddLink));
        //m_detailPane.add(new AdminSection(adminAddLink));


        final PartySearchForm memberSearchForm = new PartySearchForm();
        add(memberSearchForm);

        final RolePartyAddForm memberAddForm = new RolePartyAddForm
            (m_model, memberSearchForm.getSearchWidget());
        add(memberAddForm);

        final PartySearchForm adminSearchForm = new PartySearchForm();
        add(adminSearchForm);

        /*
        final RoleAdminAddForm adminAddForm = new RoleAdminAddForm
            (m_model, adminSearchForm.getSearchWidget());
        add(adminAddForm);*/

        connect(memberAddLink, memberSearchForm);
        connect(memberSearchForm, memberAddForm);
        memberAddForm.getForm().addSubmissionListener
            (new CancelListener(memberAddForm.getForm()));
        resume(memberAddForm.getForm(), m_detailPane);

        /*
        connect(adminAddLink, adminSearchForm);
        connect(adminSearchForm, adminAddForm);
        adminAddForm.getForm().addSubmissionListener
            (new CancelListener(adminAddForm.getForm()));
        resume(adminAddForm.getForm(), m_detailPane);*/

    }

    private class AdminVisible extends VisibilityComponent {
        AdminVisible(final Component child) {
            super(child, CmsConstants.PRIVILEGE_ADMINISTER_ROLES);
        }
    }

    private class SummarySection extends Section {
        SummarySection(final ActionLink editLink,
                       final ActionLink deleteLink) {
            setHeading(gz("cms.ui.role.details"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new Properties());
            group.addAction(new AdminVisible(editLink), ActionGroup.EDIT);
            group.addAction(new AdminVisible(deleteLink), ActionGroup.DELETE);
        }

        @SuppressWarnings("unchecked")
        private class Properties extends PropertyList {
            protected final java.util.List properties(final PageState state) {
                final java.util.List props = super.properties(state);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ConfigurationManager manager = cdiUtil.findBean(ConfigurationManager.class);
                final KernelConfig config = manager.findConfiguration(KernelConfig.class);

                final Role role = m_role.getRole(state);

                props.add(new Property(lz("cms.ui.name"),
                                       role.getName()));
                // Right now just loads the default locale description.
                props.add(new Property(lz("cms.ui.description"),
                                       role.getDescription().getValue(config.getDefaultLocale())));

                // Since Permissions don't seem to have a "pretty" form, the granted privilege is used.
                final String permissions = role.getPermissions().stream()
                        .map(Permission::getGrantedPrivilege)
                        .collect(Collectors.joining(", "));

                if (permissions.length() > 0) {
                    props.add(new Property(lz("cms.ui.role.privileges"), permissions));
                } else {
                    props.add(new Property(lz("cms.ui.role.privileges"),
                                           lz("cms.ui.role.privilege.none")));
                }

                return props;
            }
        }
    }

    private class MemberSection extends Section {
        MemberSection(final ActionLink memberAddLink) {
            setHeading(gz("cms.ui.role.members"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_members);
            group.addAction(new AdminVisible(memberAddLink), ActionGroup.ADD);
        }
    }

    /*
    private class AdminSection extends Section {
        AdminSection(final ActionLink adminAddLink) {
            setHeading(gz("cms.ui.role.admins"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_admins);
            group.addAction(new AdminVisible(adminAddLink), ActionGroup.ADD);
        }
    }*/


    private static final String[] s_memberColumns = new String[] {
        lz("cms.ui.name"),
        lz("cms.ui.role.member.email"),
        lz("cms.ui.role.member.remove")
    };

    private class MemberTable extends Table {
        MemberTable() {
            super(new MemberTableModelBuilder(m_role), s_memberColumns);

            setEmptyView(new Label(gz("cms.ui.role.member.none")));

            getColumn(2).setCellRenderer
                (new DefaultTableCellRenderer(true));

            addTableActionListener(new Listener());
        }

        private class Listener extends TableActionAdapter {
            public final void cellSelected(final TableActionEvent e) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PageState state = e.getPageState();
                final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);

                if (e.getColumn() == 2 && permissionChecker.isPermitted(CmsConstants.PRIVILEGE_ADMINISTER_ROLES)) {
                    final Role role = m_role.getRole(state);
                    long itemId = Long.parseLong(e.getRowKey().toString());

                    final PartyRepository partyRepository = cdiUtil.findBean(PartyRepository.class);
                    final RoleManager roleManager = cdiUtil.findBean(RoleManager.class);
                    final Party party = partyRepository.findById(itemId);

                    roleManager.removeRoleFromParty(role, party);

                    getRowSelectionModel().clearSelection(state);
                }
            }
        }
    }

    /*
    private static final String[] s_adminColumns = new String[] {
        lz("cms.ui.name"),
        lz("cms.ui.role.admin.email"),
        lz("cms.ui.role.admin.remove")
    };*/

    /*
    private class AdminTable extends Table {
        AdminTable() {
            super(new AdminTableModelBuilder(m_role), s_adminColumns);

            setEmptyView(new Label(gz("cms.ui.role.admin.none")));

            getColumn(2).setCellRenderer
                (new DefaultTableCellRenderer(true));

            addTableActionListener(new Listener());
        }

        private class Listener extends TableActionAdapter {
            public final void cellSelected(final TableActionEvent e) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final PageState state = e.getPageState();
                final PermissionChecker permissionChecker = cdiUtil.findBean(PermissionChecker.class);

                if (e.getColumn() == 2 && permissionChecker.isPermitted(CmsConstants.PRIVILEGE_ADMINISTER_ROLES)) {
                    final Role role = m_role.getRole(state);
                    final PermissionManager permissionManager = cdiUtil.findBean(PermissionManager.class);

                    permissionManager.revokePrivilege(CoreConstants.ADMIN_PRIVILEGE, role);

                    getRowSelectionModel().clearSelection(state);
                }
            }
        }

    }*/
}
