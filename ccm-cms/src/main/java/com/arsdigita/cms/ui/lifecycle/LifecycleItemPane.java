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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.table.DefaultTableCellRenderer;

import org.librecms.lifecycle.LifecycleDefinition;

import com.arsdigita.cms.ui.BaseItemPane;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.Property;
import com.arsdigita.toolbox.ui.PropertyList;
import com.arsdigita.toolbox.ui.Section;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.lifecycle.PhaseDefinititionRepository;

import java.util.Locale;

/**
 * This class contains the component which displays the information for a
 * particular lifecycle, with the ability to edit and delete. This information
 * also includes the associated phases for this lifecycle, also with the ability
 * to add, edit, and delete.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author Michael Pih
 * @author Jack Chung
 * @author <a href="mailto:xdmoon@redhat.com">Xixi D'Moon</a>
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 */
class LifecycleItemPane extends BaseItemPane {

    private final LifecycleDefinitionRequestLocal m_cycle;
    private final PhaseRequestLocal m_phase;

    private final Container m_detailPane;
    private final Table m_phases;

    public LifecycleItemPane(final LifecycleDefinitionRequestLocal cycle,
                             final ActionLink editLink,
                             final ActionLink deleteLink) {
        m_cycle = cycle;
        m_phase = new SelectionRequestLocal();

        m_phases = new PhaseTable();

        m_detailPane = new SimpleContainer();
        add(m_detailPane);
        setDefault(m_detailPane);

        m_detailPane.add(new SummarySection(editLink, deleteLink));

        final ActionLink phaseAddLink = new ActionLink(new Label(gz(
            "cms.ui.lifecycle.phase_add")));

        m_detailPane.add(new PhaseSection(phaseAddLink));

        final AddPhaseForm phaseAddForm = new AddPhaseForm(m_cycle);
        final EditPhaseForm phaseEditForm = new EditPhaseForm(m_phase);
        final DeletePhaseForm phaseDeleteForm = new DeletePhaseForm(m_phase);

        add(phaseAddForm);
        add(phaseEditForm);
        add(phaseDeleteForm);

        connect(phaseAddLink, phaseAddForm);
        connect(phaseAddForm);
        connect(m_phases, 4, phaseEditForm);
        connect(phaseEditForm, m_phases.getRowSelectionModel());
        connect(m_phases, 5, phaseDeleteForm);
        connect(phaseDeleteForm, m_phases.getRowSelectionModel());
    }

    private class SelectionRequestLocal extends PhaseRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final String id = m_phases.getRowSelectionModel().getSelectedKey(
                state).toString();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PhaseDefinititionRepository phaseDefRepo = cdiUtil.findBean(
                PhaseDefinititionRepository.class);

            return phaseDefRepo.findById(Long.parseLong(id));
        }

    }

    private class SummarySection extends Section {

        public SummarySection(final ActionLink editLink,
                              final ActionLink deleteLink) {
            setHeading(new Label(gz("cms.ui.lifecycle.details")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(new Properties());
            group.addAction(new LifecycleAdminContainer(editLink),
                            ActionGroup.EDIT);
            group.addAction(new LifecycleAdminContainer(deleteLink),
                            ActionGroup.DELETE);
        }

        private class Properties extends PropertyList {

            @Override
            protected final java.util.List properties(final PageState state) {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ConfigurationManager confManager = cdiUtil.findBean(
                    ConfigurationManager.class);
                final KernelConfig kernelConfig = confManager.findConfiguration(
                    KernelConfig.class);
                final Locale defaultLocale = kernelConfig.getDefaultLocale();

                final java.util.List props = super.properties(state);
                final LifecycleDefinition cycle = m_cycle
                    .getLifecycleDefinition(state);

                props.add(new Property(
                    gz("cms.ui.name"),
                    cycle.getLabel().getValue(defaultLocale)));
                props.add(new Property(
                    gz("cms.ui.description"),
                    cycle.getDescription().getValue(defaultLocale)));

                return props;
            }

        }

    }

    private class PhaseSection extends Section {

        public PhaseSection(final ActionLink addLink) {
            setHeading(new Label(gz("cms.ui.lifecycle.phases")));

            final ActionGroup group = new ActionGroup();
            setBody(group);

            group.setSubject(m_phases);
            group.addAction(new LifecycleAdminContainer(addLink),
                            ActionGroup.ADD);
        }

    }

    // XXX fix this
    private static final String[] s_headers = {
        lz("cms.ui.name"),
        lz("cms.ui.description"),
        lz("cms.ui.lifecycle.phase_delay"),
        lz("cms.ui.lifecycle.phase_duration"),
        "",
        ""
    };

    private class PhaseTable extends Table {

        public PhaseTable() {
            super(new PhaseTableModelBuilder(m_cycle), s_headers);

            setEmptyView(new Label(gz("cms.ui.lifecycle.phase_none")));

            getColumn(4).setCellRenderer(new DefaultTableCellRenderer(true));

            getColumn(5).setCellRenderer(new DefaultTableCellRenderer(true));
        }

        @Override
        public final void register(final Page page) {
            super.register(page);

            // Hide the action columns if the user does not have
            // proper access.
            page.addActionListener(new ActionListener() {

                @Override
                public final void actionPerformed(final ActionEvent e) {
                    final PageState state = e.getPageState();

                    final boolean hasLifecycleAdmin = hasAdmin(state);
                    getColumn(4).setVisible(state, hasLifecycleAdmin);
                    getColumn(5).setVisible(state, hasLifecycleAdmin);
                }

            });
        }

    }

    private boolean hasAdmin(final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);

        return permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_LIFECYLES);
    }

    @Override
    public final void reset(final PageState state) {
        super.reset(state);

        m_phases.clearSelection(state);
    }

}
