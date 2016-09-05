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
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.cms.ui.VisibilityComponent;
import com.arsdigita.cms.util.SecurityConstants;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;
import org.apache.log4j.Logger;
import org.libreccm.security.Role;
import org.librecms.contentsection.ContentSection;

import java.math.BigDecimal;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: BaseRoleItemPane.java 287 2005-02-22 00:29:02Z sskracic $
 */
class BaseRoleItemPane extends BaseItemPane {

    private static final Logger s_log = Logger.getLogger
        (BaseRoleItemPane.class);

    private final RoleRequestLocal m_role;
    private final SingleSelectionModel m_model;

    //private final MemberTable m_members;
    //private final AdminTable m_admins;

    private final SimpleContainer m_detailPane;

    public BaseRoleItemPane(final SingleSelectionModel model,
                            final RoleRequestLocal role,
                            final ActionLink editLink,
                            final ActionLink deleteLink) {
        m_model = model;
        m_role = role;

        //m_members = new MemberTable();
        //m_admins = new AdminTable();

        final ActionLink memberAddLink = new ActionLink
            (new Label(gz("cms.ui.role.member.add")));

        final ActionLink adminAddLink = new ActionLink
            (new Label(gz("cms.ui.role.admin.add")));

        m_detailPane = new SimpleContainer();
        add(m_detailPane);
        setDefault(m_detailPane);

        m_detailPane.add(new SummarySection(editLink, deleteLink));
        m_detailPane.add(new MemberSection(memberAddLink));
        m_detailPane.add(new AdminSection(adminAddLink));

        /*
        final PartySearchForm memberSearchForm = new PartySearchForm();
        add(memberSearchForm);

        final RolePartyAddForm memberAddForm = new RolePartyAddForm
            (m_model, memberSearchForm.getSearchWidget());
        add(memberAddForm);

        final PartySearchForm adminSearchForm = new PartySearchForm();
        add(adminSearchForm);

        final RoleAdminAddForm adminAddForm = new RoleAdminAddForm
            (m_model, adminSearchForm.getSearchWidget());
        add(adminAddForm);

        connect(memberAddLink, memberSearchForm);
        connect(memberSearchForm, memberAddForm);
        memberAddForm.getForm().addSubmissionListener
            (new CancelListener(memberAddForm.getForm()));
        resume(memberAddForm.getForm(), m_detailPane);

        connect(adminAddLink, adminSearchForm);
        connect(adminSearchForm, adminAddForm);
        adminAddForm.getForm().addSubmissionListener
            (new CancelListener(adminAddForm.getForm()));
        resume(adminAddForm.getForm(), m_detailPane);
        */
    }

    /*
    private boolean hasAdmin(final PageState state) {
        return CMS.getContext().getSecurityManager()..canAccess
            (state.getRequest(), SecurityConstants.STAFF_ADMIN);
    }*/

    private class AdminVisible extends VisibilityComponent {
        AdminVisible(final Component child) {
            super(child, SecurityConstants.STAFF_ADMIN);
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

        private class Properties extends PropertyList {
            protected final java.util.List properties(final PageState state) {
                final java.util.List props = super.properties(state);
                final Role role = m_role.getRole(state);

                props.add(new Property(lz("cms.ui.name"),
                                       role.getName()));
                //props.add(new Property(lz("cms.ui.description"),
                //                       role.getDescription()));

                final StringBuffer buffer = new StringBuffer();
                final ContentSection section = CMS.getContext
                    ().getContentSection();
                /*
                final DataQuery privs = RoleFactory.getRolePrivileges
                    (section.getID(), role.getGroup().getID());

                while (privs.next()) {
                    buffer.append((String) privs.get("prettyName") + ", ");
                }

                privs.close();*/

                final int length = buffer.length();

                if (length > 0) {
                    props.add(new Property(lz("cms.ui.role.privileges"),
                                           buffer.toString().substring
                                               (0, length - 2)));
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

            //group.setSubject(m_members);
            group.addAction(new AdminVisible(memberAddLink), ActionGroup.ADD);
        }
    }

    private class AdminSection extends Section {
        AdminSection(final ActionLink adminAddLink) {
            setHeading(gz("cms.ui.role.admins"));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            //group.setSubject(m_admins);
            group.addAction(new AdminVisible(adminAddLink), ActionGroup.ADD);
        }
    }

    // XXX globz
    private static final String[] s_memberColumns = new String[] {
        lz("cms.ui.name"),
        lz("cms.ui.role.member.email"),
        lz("cms.ui.role.member.remove")
    };
/*
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
                final PageState state = e.getPageState();

                if (e.getColumn().intValue() == 2 && hasAdmin(state)) {
                    final Role role = m_role.getRole(state);
                    final Party party = (Party) DomainObjectFactory.newInstance
                        (new OID(Party.BASE_DATA_OBJECT_TYPE,
                                 new BigDecimal(e.getRowKey().toString())));

                    role.remove(party);
                    role.save();

                    getRowSelectionModel().clearSelection(state);
                }
            }
        }
    }*/

    private static final String[] s_adminColumns = new String[] {
        lz("cms.ui.name"),
        lz("cms.ui.role.admin.email"),
        lz("cms.ui.role.admin.remove")
    };

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
                final PageState state = e.getPageState();

                if (e.getColumn().intValue() == 2 && hasAdmin(state)) {
                    final Role role = m_role.getRole(state);
                    final Party party = (Party) DomainObjectFactory.newInstance
                        (new OID(Party.BASE_DATA_OBJECT_TYPE,
                                 new BigDecimal(e.getRowKey().toString())));

                    final PermissionDescriptor perm =
                        new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
                                                 role.getGroup(),
                                                 party);

                    PermissionService.revokePermission(perm);

                    getRowSelectionModel().clearSelection(state);
                }
            }
        }

    }*/
}
