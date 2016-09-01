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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;

import org.librecms.lifecycle.PhaseDefinition;

import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.lifecycle.PhaseDefinititionRepository;

import java.math.BigDecimal;

/**
 * This class handles the deleting of a phase definition.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 */
class DeletePhaseForm extends CMSForm
    implements FormProcessListener, FormInitListener {

    private final PhaseRequestLocal m_phase;

    private final Hidden m_id;
    private final Submit m_deleteWidget;
    private final Submit m_cancelWidget;

    /**
     * @param m The phase selection model. This tells the form which phase
     *          definition is selected.
     */
    public DeletePhaseForm(final PhaseRequestLocal phase) {
        super("PhaseDefinitionDelete");

        m_phase = phase;

        m_id = new Hidden(new BigDecimalParameter("id"));
        add(m_id);
        m_id.addValidationListener(new NotNullValidationListener());

        final BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
        m_deleteWidget = new Submit("delete");
        m_deleteWidget.setButtonLabel("Delete");
        m_deleteWidget.setClassAttr("deletePhase");
        buttons.add(m_deleteWidget);

        m_cancelWidget = new Submit("cancel");
        m_cancelWidget.setButtonLabel("Cancel");
        m_cancelWidget.setClassAttr("canceldeletePhase");
        buttons.add(m_cancelWidget);

        add(buttons, ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);

        addInitListener(this);

        addSubmissionListener(new FormSecurityListener(
            CmsConstants.PRIVILEGE_ADMINISTER_LIFECYLES));

        addProcessListener(this);
    }

    /**
     * Returns true if this form was cancelled.
     *
     * @param state The page state
     *
     * @return true if the form was cancelled, false otherwise
     */
    @Override
    public boolean isCancelled(final PageState state) {
        return m_cancelWidget.isSelected(state);
    }

    /**
     * Form process listener. Deletes a phase definition
     *
     * @param event The form process event
     *
     * @exception FormProcessException
     */
    @Override
    public final void process(final FormSectionEvent event)
        throws FormProcessException {
        final FormData data = event.getFormData();
        final Long key = (Long) data.get(m_id.getName());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PhaseDefinititionRepository phaseDefRepo = cdiUtil.findBean(
            PhaseDefinititionRepository.class);

        // Check if the object is already deleted for double click
        // protection.
        final PhaseDefinition phaseDef = phaseDefRepo.findById(key);
        if (phaseDef != null) {
            phaseDefRepo.delete(phaseDef);
        }

    }

    /**
     * Init listener. gets the id of the selected phase definition
     *
     * @param event The form init event
     */
    @Override
    public final void init(final FormSectionEvent event) {
        final FormData data = event.getFormData();
        final PageState state = event.getPageState();

        final Long id = m_phase.getPhase(state).getDefinitionId();

        data.put(m_id.getName(), id);
    }

}
