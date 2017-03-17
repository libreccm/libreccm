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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;

import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.PhaseDefinition;

import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This class contains a form component to add a lifecycle phase definition.
 *
 * @author Jack Chung
 * @author Xixi D'Moon
 * @author Michael Pih
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class AddPhaseForm extends CMSForm {
    
    private final static String LABEL = "label";
    private final static String DESCRIPTION = "description";
    private final static String DELAY_DAYS = "delay_days";
    private final static String DELAY_HOURS = "delay_hours";
    private final static String DELAY_MINUTES = "delay_minutes";
    private final static String DURATION_DAYS = "duration_days";
    private final static String DURATION_HOURS = "duration_hours";
    private final static String DURATION_MINUTES = "duration_minutes";
    private final static String SUBMIT = "submit";
    private final static String CANCEL = "cancel";
    
    private final LifecycleDefinitionRequestLocal selectedLifecycle;
    
    private final TextField phaseLabel;
    private final TextArea phaseDescription;
    private final TextField delayDays;
    private final TextField delayHours;
    private final TextField delayMinutes;
    private final TextField durationDays;
    private final TextField durationHours;
    private final TextField durationMinutes;
    private final Submit submit;
    private final Submit cancel;

    /**
     * Constructor.
     *
     * @param cycles The cycle selection model. This is to tell the form which
     *               cycle definition is selected since phase definitions are
     *               associated to cycle definitions
     */
    public AddPhaseForm(final LifecycleDefinitionRequestLocal selectedLifecycle) {
        super("LifecyclePhaseDefinition");
        
        this.selectedLifecycle = selectedLifecycle;
        
        final Label heading = new Label(gz("cms.ui.lifecycle.phase_add"));
        heading.setFontWeight(Label.BOLD);
        add(heading, ColumnPanel.FULL_WIDTH);
        add(new FormErrorDisplay(this), ColumnPanel.FULL_WIDTH);
        
        add(new Label(gz("cms.ui.lifecycle.phase.name")));
        phaseLabel = new TextField(new TrimmedStringParameter(LABEL));
        phaseLabel.addValidationListener(new NotEmptyValidationListener());
        phaseLabel.setSize(40);
        phaseLabel.setMaxLength(1000);
        add(phaseLabel);
        
        add(new Label(gz("cms.ui.lifecycle.phase.description")));
        phaseDescription = new TextArea(new TrimmedStringParameter(DESCRIPTION));
        phaseDescription.addValidationListener(
            new StringLengthValidationListener(
                4000));
        phaseDescription.setCols(40);
        phaseDescription.setRows(5);
        phaseDescription.setWrap(TextArea.SOFT);
        add(phaseDescription);

        // phase delay
        add(new Label(gz("cms.ui.lifecycle.phase_start_delay")));
        delayDays = new TextField(new IntegerParameter(DELAY_DAYS));
        delayHours = new TextField(new IntegerParameter(DELAY_HOURS));
        delayMinutes = new TextField(new IntegerParameter(DELAY_MINUTES));

        //max value: days: 60 years, hours: 7 days, minutes: 1 day
        delayDays.addValidationListener(
            new NumberInRangeValidationListener(0, 21900));
        delayHours.addValidationListener(new NumberInRangeValidationListener(
            0, 168));
        delayMinutes.addValidationListener(
            new NumberInRangeValidationListener(0, 1440));
        delayDays.setSize(7);
        delayHours.setSize(7);
        delayMinutes.setSize(7);
        delayDays.setClassAttr("DaysField");
        delayHours.setClassAttr("HoursField");
        delayMinutes.setClassAttr("MinutesField");
        
        final SimpleContainer delayContainer = new SimpleContainer();
        delayContainer.add(new Label(gz("cms.ui.lifecycle.phase_days")));
        delayContainer.add(delayDays);
        delayContainer.add(new Label(gz("cms.ui.lifecycle.phase_hours")));
        delayContainer.add(delayHours);
        delayContainer.add(new Label(gz("cms.ui.lifecycle.phase_minutes")));
        delayContainer.add(delayMinutes);
        add(delayContainer);

        // phase duration
        add(new Label(gz("cms.ui.lifecycle.phase_duration")));
        durationDays = new TextField(new IntegerParameter(DURATION_DAYS));
        durationHours = new TextField(new IntegerParameter(DURATION_HOURS));
        durationMinutes = new TextField(
            new IntegerParameter(DURATION_MINUTES));

        //max value: days: 60 years, hours: 7 days, minutes: 1 day
        durationDays.addValidationListener(
            new NumberInRangeValidationListener(0, 21900));
        durationHours.addValidationListener(
            new NumberInRangeValidationListener(0, 168));
        durationMinutes.addValidationListener(
            new NumberInRangeValidationListener(0, 1440));
        durationDays.setSize(7);
        durationHours.setSize(7);
        durationMinutes.setSize(7);
        durationDays.setClassAttr("DaysField");
        durationHours.setClassAttr("HoursField");
        durationMinutes.setClassAttr("MinutesField");
        
        final SimpleContainer durationContainer = new SimpleContainer();
        durationContainer.add(new Label(gz("cms.ui.lifecycle.phase_days")));
        durationContainer.add(durationDays);
        durationContainer.add(new Label(gz("cms.ui.lifecycle.phase_hours")));
        durationContainer.add(durationHours);
        durationContainer.add(new Label(gz("cms.ui.lifecycle.phase_minutes")));
        durationContainer.add(durationMinutes);
        add(durationContainer);
        
        final SimpleContainer submitCancel = new SimpleContainer();
        submit = new Submit(SUBMIT, gz("cms.ui.lifecycle.phase.add_submit"));
        submitCancel.add(submit);
        cancel = new Submit(CANCEL, gz("cms.ui.lifecycle.phase.add_cancel"));
        submitCancel.add(cancel);
        add(submitCancel, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);
        
        addSubmissionListener(new FormSecurityListener(
            AdminPrivileges.ADMINISTER_LIFECYLES));
        
        addValidationListener(new FormValidationListener() {
            
            @Override
            public final void validate(final FormSectionEvent event)
                throws FormProcessException {
                final PageState state = event.getPageState();
                validateDuration(state);
                validateUniqueName(state);
            }
            
        });
        
        addProcessListener(new FormProcessListener() {
            
            @Override
            public final void process(final FormSectionEvent event)
                throws FormProcessException {
                addPhase(event.getPageState());
            }
            
        });
    }

    /**
     * Returns true if this form was cancelled.
     *
     * @param state The page state
     *
     * @return True if the form was cancelled, false otherwise
     *
     * @pre state != null
     */
    @Override
    public boolean isCancelled(final PageState state) {
        return cancel.isSelected(state);
    }

    /**
     * Add a new phase using data from the form.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    protected void addPhase(final PageState state) throws FormProcessException {
        final String label = (String) phaseLabel.getValue(state);
        final String description = (String) phaseDescription.getValue(state);
        final int delDays = (int) Optional
            .ofNullable(delayDays.getValue(state))
            .orElseGet(() -> 0);
        final int delHours = (int) Optional
            .ofNullable(delayHours.getValue(state))
            .orElseGet(() -> 0);
        final int delMinutes = (int) Optional
            .ofNullable(delayMinutes.getValue(state))
            .orElseGet(() -> 0);
        final int durDays = (int) Optional
            .ofNullable(durationDays.getValue(state))
            .orElseGet(() -> 0);
        final int durHours = (int) Optional
            .ofNullable(durationHours.getValue(state))
            .orElseGet(() -> 0);
        final int durMinutes = (int) Optional
            .ofNullable(durationMinutes.getValue(state))
            .orElseGet(() -> 0);
        
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final LifecycleAdminPaneController controller = cdiUtil
            .findBean(LifecycleAdminPaneController.class);
        controller.addPhaseDefinition(
            selectedLifecycle.getLifecycleDefinition(state),
            label,
            description,
            delDays,
            delHours,
            delMinutes,
            durDays,
            durHours,
            durMinutes);
    }

    /**
     * Validate name uniqueness.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    protected void validateUniqueName(final PageState state)
        throws FormProcessException {
        
        final String label = (String) phaseLabel.getValue(state);
        
        final LifecycleDefinition cycleDef = selectedLifecycle
            .getLifecycleDefinition(state);
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final LifecycleAdminPaneController controller = cdiUtil
            .findBean(LifecycleAdminPaneController.class);
        final List<PhaseDefinition> phaseDefs = controller
            .getPhaseDefinitions(cycleDef);
        
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();
        
        final boolean duplicateLabel = phaseDefs
            .stream()
            .map(phaseDef -> phaseDef.getLabel().getValue(defaultLocale))
            .anyMatch(phaseDefLabel -> phaseDefLabel.equalsIgnoreCase(label));
        
        if (duplicateLabel) {
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.lifecycle.phase_name_not_unique",
                CmsConstants.CMS_BUNDLE));
        }
    }

    /**
     * Validate the phase duration. The duration cannot be 0.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    protected void validateDuration(final PageState state)
        throws FormProcessException {
        
        final Integer durDays = (Integer) durationDays.getValue(state);
        final Integer durHours = (Integer) durationHours.getValue(state);
        final Integer durMinutes = (Integer) durationMinutes.getValue(state);

        // Phase duration is infinite, so the duration is valid.
        if (durDays == null && durHours == null && durMinutes == null) {
            return;
        }
        
        int days, hours, minutes;
        if (durDays != null) {
            days = durDays;
        } else {
            days = 0;
        }
        
        if (durHours != null) {
            hours = durHours;
        } else {
            hours = 0;
        }
        
        if (durMinutes != null) {
            minutes = durMinutes;
        } else {
            minutes = 0;
        }
        
        if ((days + hours + minutes) == 0) {
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.lifecycle.phase_duration_negative",
                CmsConstants.CMS_BUNDLE));
        }
    }
    
    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key,
                                     CmsConstants.CMS_BUNDLE);
    }
    
}
