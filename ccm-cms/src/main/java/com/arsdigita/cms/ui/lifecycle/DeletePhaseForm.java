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
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;

import org.librecms.lifecycle.PhaseDefinition;

import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.lifecycle.PhaseDefinititionRepository;

/**
 * This class handles the deleting of a phase definition.
 *
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class DeletePhaseForm extends CMSForm
    implements FormProcessListener, FormInitListener {

    private final PhaseRequestLocal selectedPhaseDef;

    private final Hidden selectedPhaseDefId;
    private final Submit deleteWidget;
    private final Submit cancelWidget;

    /**
     * @param m The phase selection model. This tells the form which phase
     *          definition is selected.
     */
    public DeletePhaseForm(final PhaseRequestLocal selectedPhaseDef) {
        super("PhaseDefinitionDelete");

        this.selectedPhaseDef = selectedPhaseDef;

        selectedPhaseDefId = new Hidden(new LongParameter("id"));
        add(selectedPhaseDefId);
        selectedPhaseDefId
            .addValidationListener(new NotNullValidationListener());

        final BoxPanel buttons = new BoxPanel(BoxPanel.HORIZONTAL);
        deleteWidget = new Submit("delete",
                                  new GlobalizedMessage(
                                      "cms.ui.lifecycle.phase.delete_submit",
                                      CmsConstants.CMS_BUNDLE));
        deleteWidget.setClassAttr("deletePhase");
        buttons.add(deleteWidget);

        cancelWidget = new Submit("cancel",
                                  new GlobalizedMessage(
                                      "cms.ui.lifecycle.phase.delete_cancel",
                                      CmsConstants.CMS_BUNDLE));
        cancelWidget.setClassAttr("canceldeletePhase");
        buttons.add(cancelWidget);

        add(buttons, ColumnPanel.CENTER | ColumnPanel.FULL_WIDTH);

        addInitListener(this);

        addSubmissionListener(new FormSecurityListener(
            AdminPrivileges.ADMINISTER_LIFECYLES));

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
        return cancelWidget.isSelected(state);
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
        final Long key = (Long) data.get(selectedPhaseDefId.getName());

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PhaseDefinititionRepository phaseDefRepo = cdiUtil.findBean(
            PhaseDefinititionRepository.class);

        // Check if the object is already deleted for double click
        // protection.
        final PhaseDefinition phaseDef = phaseDefRepo.findById(key).get();
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

        final Long phaseDefId = selectedPhaseDef.getPhase(state)
            .getDefinitionId();

        data.put(selectedPhaseDefId.getName(), phaseDefId);
    }

}
