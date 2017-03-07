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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.ui.admin.GlobalizationUtil;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Party;
import org.libreccm.security.PartyRepository;
import org.libreccm.security.Role;
import org.libreccm.security.RoleManager;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Creates a table model based on the {@link Party parties} of the given
 * {@link Role role}.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: MemberTableModelBuilder.java 287 2005-02-22 00:29:02Z sskracic
 * $
 */
class MemberTableModelBuilder extends AbstractTableModelBuilder {

    private final RoleRequestLocal roleRequestLocal;

    MemberTableModelBuilder(final RoleRequestLocal role) {
        roleRequestLocal = role;
    }

    @Override
    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        final Role role = roleRequestLocal.getRole(state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final RoleAdminPaneController controller = cdiUtil.findBean(
            RoleAdminPaneController.class);
        final List<Party> members = controller.createRoleMemberList(role);

        return new Model(members);
    }

    private static class Model implements TableModel {

        private Party m_party;
        private final Collection<Party> m_parties;
        private final Iterator<Party> iterator;

        Model(final Collection<Party> parties) {
            m_parties = parties;
            iterator = m_parties.iterator();
        }

        @Override
        public final int getColumnCount() {
            return 3;
        }

        @Override
        public final boolean nextRow() {
            if (iterator.hasNext()) {
                m_party = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public final Object getKeyAt(final int column) {
            return m_party.getPartyId();
        }

        @Override
        public final Object getElementAt(final int column) {
            switch (column) {
                case 0:
                    return m_party.getName();
                case 1:
                    return lz("cms.ui.none");
                case 2:
                    return lz("cms.ui.role.member.remove");
                default:
                    throw new IllegalStateException();
            }
        }

    }

    protected static String lz(final String key) {
        return (String) GlobalizationUtil.globalize(key).localize();
    }

}
