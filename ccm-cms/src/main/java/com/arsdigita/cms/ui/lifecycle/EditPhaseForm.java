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
import com.arsdigita.bebop.event.FormInitListener;
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
import com.arsdigita.cms.lifecycle.Duration;

import org.librecms.lifecycle.PhaseDefinition;

import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.lifecycle.LifecycleDefinition;

import java.util.List;
import java.util.Locale;
import java.util.Optional;

/**
 * This class contains a form component to edit a lifecycle phase definition.
 *
 * @author Jack Chung
 * @author Xixi D'Moon
 * @author Michael Pih
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class EditPhaseForm extends CMSForm {

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
    private final PhaseRequestLocal selectedPhase;

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
     * @param m A single selection model for phase definitions. This is to tell
     *          the form which phase definition is selected for editing.
     *
     * @pre phases != null
     */
    public EditPhaseForm(
        final LifecycleDefinitionRequestLocal selectedLifecycle,
        final PhaseRequestLocal selectedPhase) {

        super("EditPhaseDefinition");

        this.selectedLifecycle = selectedLifecycle;
        this.selectedPhase = selectedPhase;

        add(new Label());
        add(new FormErrorDisplay(this));

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

        // Phase duration
        // Max value: days: 60 years, hours: 7 days, minutes: 1 day
        delayDays = new TextField(new IntegerParameter(DELAY_DAYS));
        delayDays.addValidationListener(new NumberInRangeValidationListener(0,
                                                                            21900));
        delayDays.setSize(7);
        delayDays.setClassAttr("DaysField");

        delayHours = new TextField(new IntegerParameter(DELAY_HOURS));
        delayHours.addValidationListener(
            new NumberInRangeValidationListener(0, 168));
        delayHours.setClassAttr("HoursField");
        delayHours.setSize(7);

        delayMinutes = new TextField(new IntegerParameter(DELAY_MINUTES));
        delayMinutes.addValidationListener(
            new NumberInRangeValidationListener(0, 1440));
        delayMinutes.setSize(7);
        delayMinutes.setClassAttr("MinutesField");

        add(new Label(
            new GlobalizedMessage("cms.ui.lifecycle.phase_start_delay",
                                  CmsConstants.CMS_BUNDLE)));
        final SimpleContainer delayContainer = new SimpleContainer();
        delayContainer.add(new Label(gz("cms.ui.lifecycle.phase_days")));
        delayContainer.add(delayDays);
        delayContainer.add(new Label(gz("cms.ui.lifecycle.phase_hours")));
        delayContainer.add(delayHours);
        delayContainer.add(new Label(gz("cms.ui.lifecycle.phase_minutes")));
        delayContainer.add(delayMinutes);
        add(delayContainer);

        // Phase duration
        // Max value: days: 60 years, hours: 7 days, minutes: 1 day
        durationDays = new TextField(new IntegerParameter(DURATION_DAYS));
        durationDays.addValidationListener(
            new NumberInRangeValidationListener(0,
                                                21900));
        durationDays.setSize(7);
        durationDays.setClassAttr("DaysField");

        durationHours = new TextField(new IntegerParameter(DURATION_HOURS));
        durationHours.addValidationListener(new NumberInRangeValidationListener(
            0,
            168));
        durationHours.setSize(7);
        durationHours.setClassAttr("HoursField");

        durationMinutes = new TextField(new IntegerParameter(DURATION_MINUTES));
        durationMinutes.addValidationListener(
            new NumberInRangeValidationListener(0, 1440));
        durationMinutes.setSize(7);
        durationMinutes.setClassAttr("MinutesField");

        add(new Label(gz("cms.ui.lifecycle.phase_duration")));
        final SimpleContainer durationContainer = new SimpleContainer();
        durationContainer.add(new Label(gz("cms.ui.lifecycle.phase_days")));
        durationContainer.add(durationDays);
        durationContainer.add(new Label(gz("cms.ui.lifecycle.phase_hours")));
        durationContainer.add(durationHours);
        durationContainer.add(new Label(gz("cms.ui.lifecycle.phase_minutes")));
        durationContainer.add(durationMinutes);
        add(durationContainer);

        // Submit and cancel buttons
        final SimpleContainer submitCancelContainer = new SimpleContainer();
        submit = new Submit(SUBMIT, gz("cms.ui.lifecycle.phase.edit_submit"));
        submitCancelContainer.add(submit);
        cancel = new Submit(CANCEL, gz("cms.ui.lifecycle.phase.edit_cancel"));
        submitCancelContainer.add(cancel);
        add(submitCancelContainer, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        // Add form listeners.
        addInitListener(new FormInitListener() {

            @Override
            public final void init(final FormSectionEvent event)
                throws FormProcessException {
                initializePhaseDefinition(event.getPageState());
            }

        });

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
            public void process(final FormSectionEvent event)
                throws FormProcessException {
                updatePhaseDefinition(event.getPageState());
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
     * Populate the form fields with the current phase definition attribute
     * values.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    private void initializePhaseDefinition(final PageState state)
        throws FormProcessException {

        final Locale defaultLocale = KernelConfig.getConfig().getDefaultLocale();

        final PhaseDefinition phaseDef = selectedPhase.getPhase(state);
        phaseLabel.setValue(state, phaseDef.getLabel().getValue(defaultLocale));
        phaseDescription.setValue(state,
                                  phaseDef
                                      .getDescription()
                                      .getValue(defaultLocale));

        final long[] delay = Duration.formatDHM(phaseDef.getDefaultDelay());
        delayDays.setValue(state, delay[0]);
        delayHours.setValue(state, delay[1]);
        delayMinutes.setValue(state, delay[2]);

        final Long duration = phaseDef.getDefaultDuration();
        if (duration != 0) {
            final Long[] dhm = Duration.formatDHM(duration);
            durationDays.setValue(state, dhm[0]);
            durationHours.setValue(state, dhm[1]);
            durationMinutes.setValue(state, dhm[2]);
        }
    }

    /**
     * Update the phase definition with values from the form.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    private void updatePhaseDefinition(final PageState state)
        throws FormProcessException {

        final String label = (String) phaseLabel.getValue(state);
        final String description = (String) phaseDescription.getValue(state);
        final int delDays = (int) Optional
            .ofNullable(delayDays.getValue(state))
            .orElseGet(() -> 0);
        final int delHours = (int) Optional
            .ofNullable(delayHours.getValue(state))
            .orElseGet(() -> 0);;
        final int delMinutes = (int) Optional
            .ofNullable(delayMinutes.getValue(state))
            .orElseGet(() -> 0);;
        final int durDays = (int) Optional
            .ofNullable(durationDays.getValue(state))
            .orElseGet(() -> 0);;
        final int durHours = (int) Optional
            .ofNullable(durationHours.getValue(state))
            .orElseGet(() -> 0);;
        final int durMinutes = (int) Optional
            .ofNullable(durationMinutes.getValue(state))
            .orElseGet(() -> 0);;

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final LifecycleAdminPaneController controller = cdiUtil
            .findBean(LifecycleAdminPaneController.class);

        controller.updatePhaseDefinition(selectedPhase.getPhase(state),
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
     * Ensures that the new name (if it has changed) is unique within the
     * lifecycle definition.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    private void validateUniqueName(final PageState state)
        throws FormProcessException {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();
        
        final String oldLabel = selectedPhase
            .getPhase(state)
            .getLabel()
            .getValue(defaultLocale);
        final String newLabel = (String) phaseLabel.getValue(state);
        
        if (oldLabel.equals(newLabel)) {
            //Label has node changed, no validation required.
            return;
        }

        final LifecycleDefinition cycleDef = selectedLifecycle
            .getLifecycleDefinition(state);
        final LifecycleAdminPaneController controller = cdiUtil
            .findBean(LifecycleAdminPaneController.class);
        final List<PhaseDefinition> phaseDefs = controller
            .getPhaseDefinitions(cycleDef);

        

        final boolean duplicateLabel = phaseDefs
            .stream()
            .map(phaseDef -> phaseDef.getLabel().getValue(defaultLocale))
            .anyMatch(phaseDefLabel -> {
                return phaseDefLabel.equalsIgnoreCase(newLabel);
            });

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
     *
     */
    private void validateDuration(final PageState state)
        throws FormProcessException {

        final Integer durDays = (Integer) durationDays.getValue(state);
        final Integer durHours = (Integer) durationHours.getValue(state);
        final Integer durMinutes = (Integer) durationMinutes.getValue(state);

        // Phase duration is infinite, so the duration is valid.
        if (durDays == null && durHours == null && durMinutes == null) {
            return;
        }

        int days, hours, minutes;
        if (durDays == null) {
            days = 0;
        } else {
            days = durDays;
        }

        if (durHours == null) {
            hours = 0;
        } else {
            hours = durHours;
        }

        if (durMinutes == null) {
            minutes = 0;
        } else {
            minutes = durMinutes;
        }

        if ((days + hours + minutes) == 0) {
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.phase.duration_negative",
                CmsConstants.CMS_BUNDLE));
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

}
