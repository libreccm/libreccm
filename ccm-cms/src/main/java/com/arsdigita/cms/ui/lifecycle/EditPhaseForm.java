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
import org.librecms.lifecycle.PhaseDefinititionRepository;

import java.util.Locale;

/**
 * This class contains a form component to edit a lifecycle phase definition.
 *
 * @author Jack Chung
 * @author Xixi D'Moon
 * @author Michael Pih
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class EditPhaseForm extends CMSForm {

    private final static String PHASE_ID = "id";
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

    private final PhaseRequestLocal m_phase;

    private final TextField m_label;
    private final TextArea m_description;
    private final TextField m_delayDays;
    private final TextField m_delayHours;
    private final TextField m_delayMinutes;
    private final TextField m_durDays;
    private final TextField m_durHours;
    private final TextField m_durMinutes;
    private final Submit m_submit;
    private final Submit m_cancel;

    /**
     * Constructor.
     *
     * @param m A single selection model for phase definitions. This is to tell
     *          the form which phase definition is selected for editing.
     *
     * @pre phases != null
     */
    public EditPhaseForm(final PhaseRequestLocal phase) {
        super("EditPhaseDefinition");

        m_phase = phase;

        add(new Label());
        add(new FormErrorDisplay(this));

        add(new Label(gz("cms.ui.name")));
        m_label = new TextField(new TrimmedStringParameter(LABEL));
        m_label.addValidationListener(new NotEmptyValidationListener());
        m_label.setSize(40);
        m_label.setMaxLength(1000);
        add(m_label);

        add(new Label(gz("cms.ui.description")));
        m_description = new TextArea(new TrimmedStringParameter(DESCRIPTION));
        m_description.addValidationListener(new StringLengthValidationListener(
            4000));
        m_description.setCols(40);
        m_description.setRows(5);
        m_description.setWrap(TextArea.SOFT);
        add(m_description);

        // Phase duration
        // Max value: days: 60 years, hours: 7 days, minutes: 1 day
        m_delayDays = new TextField(new IntegerParameter(DELAY_DAYS));
        m_delayDays.addValidationListener(new NumberInRangeValidationListener(0,
                                                                              21900));
        m_delayDays.setSize(7);
        m_delayDays.setClassAttr("DaysField");

        m_delayHours = new TextField(new IntegerParameter(DELAY_HOURS));
        m_delayHours.addValidationListener(
            new NumberInRangeValidationListener(0, 168));
        m_delayHours.setClassAttr("HoursField");
        m_delayHours.setSize(7);

        m_delayMinutes = new TextField(new IntegerParameter(DELAY_MINUTES));
        m_delayMinutes.addValidationListener(
            new NumberInRangeValidationListener(0, 1440));
        m_delayMinutes.setSize(7);
        m_delayMinutes.setClassAttr("MinutesField");

        add(new Label(new GlobalizedMessage("cms.ui.lifecycle.start_delay",
                                            CmsConstants.CMS_BUNDLE)));
        SimpleContainer de = new SimpleContainer();
        de.add(new Label(gz("cms.ui.lifecycle.phase_days")));
        de.add(m_delayDays);
        de.add(new Label(gz("cms.ui.lifecycle.phase_hours")));
        de.add(m_delayHours);
        de.add(new Label(gz("cms.ui.lifecycle.phase_mins")));
        de.add(m_delayMinutes);
        add(de);

        // Phase duration
        // Max value: days: 60 years, hours: 7 days, minutes: 1 day
        m_durDays = new TextField(new IntegerParameter(DURATION_DAYS));
        m_durDays.addValidationListener(new NumberInRangeValidationListener(0,
                                                                            21900));
        m_durDays.setSize(7);
        m_durDays.setClassAttr("DaysField");

        m_durHours = new TextField(new IntegerParameter(DURATION_HOURS));
        m_durHours.addValidationListener(new NumberInRangeValidationListener(0,
                                                                             168));
        m_durHours.setSize(7);
        m_durHours.setClassAttr("HoursField");

        m_durMinutes = new TextField(new IntegerParameter(DURATION_MINUTES));
        m_durMinutes.addValidationListener(
            new NumberInRangeValidationListener(0, 1440));
        m_durMinutes.setSize(7);
        m_durMinutes.setClassAttr("MinutesField");

        add(new Label(gz("cms.ui.lifecycle.duration")));
        SimpleContainer du = new SimpleContainer();
        du.add(new Label(gz("cms.ui.lifecycle.phase_days")));
        du.add(m_durDays);
        du.add(new Label(gz("cms.ui.lifecycle.phase_hours")));
        du.add(m_durHours);
        du.add(new Label(gz("cms.ui.lifecycle.phase_mins")));
        du.add(m_durMinutes);
        add(du);

        // Submit and cancel buttons
        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit(SUBMIT);
        m_submit.setButtonLabel("Edit Phase");
        s.add(m_submit);
        m_cancel = new Submit(CANCEL);
        m_cancel.setButtonLabel("Cancel");
        s.add(m_cancel);
        add(s, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

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
        return m_cancel.isSelected(state);
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
        PhaseDefinition pd = m_phase.getPhase(state);
        m_label.setValue(state, pd.getLabel());
        m_description.setValue(state, pd.getDescription());

        long[] delay = Duration.formatDHM(pd.getDefaultDelay());
        m_delayDays.setValue(state, delay[0]);
        m_delayHours.setValue(state, delay[1]);
        m_delayMinutes.setValue(state, delay[2]);

        Long duration = pd.getDefaultDuration();
        if (duration != null) {
            Long[] dhm = Duration.formatDHM(duration);
            m_durDays.setValue(state, dhm[0]);
            m_durHours.setValue(state, dhm[1]);
            m_durMinutes.setValue(state, dhm[2]);
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

        final String label = (String) m_label.getValue(state);
        final String description = (String) m_description.getValue(state);
        final Integer delayDays = (Integer) m_delayDays.getValue(state);
        final Integer delayHours = (Integer) m_delayHours.getValue(state);
        final Integer delayMinutes = (Integer) m_delayMinutes.getValue(state);
        final Integer durDays = (Integer) m_durDays.getValue(state);
        final Integer durHours = (Integer) m_durHours.getValue(state);
        final Integer durMinutes = (Integer) m_durMinutes.getValue(state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        final PhaseDefinititionRepository phaseDefRepo = cdiUtil.findBean(
            PhaseDefinititionRepository.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final PhaseDefinition phaseDefinition = m_phase.getPhase(state);
        phaseDefinition.getLabel().addValue(defaultLocale, label);
        phaseDefinition.getDescription().addValue(defaultLocale, description);
        phaseDefinition.setDefaultDelay(delayDays * delayHours * delayMinutes
                                        * 60);
        phaseDefinition.setDefaultDuration(durDays * durHours * durMinutes * 60);
        phaseDefRepo.save(phaseDefinition);
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
        final String newLabel = (String) m_label.getValue(state);

//        PhaseDefinition pd = m_phase.getPhase(state);
//        PhaseDefinitionCollection phaseDefs = pd.getLifecycleDefinition()
//            .getPhaseDefinitions();
//
//        // If the name has changed, check for uniqueness.
//        if (!pd.getLabel().equalsIgnoreCase(newLabel)) {
//            while (phaseDefs.next()) {
//                PhaseDefinition phaseDef = phaseDefs.getPhaseDefinition();
//
//                if (phaseDef.getLabel().equalsIgnoreCase(newLabel)) {
//                    phaseDefs.close();
//                    throw new FormProcessException(GlobalizationUtil.globalize(
//                        "cms.ui.lifecycle.phase_name_not_unique"));
//                }
//            }
//        }
    }

    /**
     * Validate the phase duration. The duration cannot be 0.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    private void validateDuration(final PageState state)
        throws FormProcessException {
        final Integer durDays = (Integer) m_durDays.getValue(state);
        final Integer durHours = (Integer) m_durHours.getValue(state);
        final Integer durMinutes = (Integer) m_durMinutes.getValue(state);

        // Phase duration is infinite, so the duration is valid.
        if (durDays == null && durHours == null && durMinutes == null) {
            return;
        }

        int days, hours, minutes;
        if (durDays != null) {
            days = durDays.intValue();
        } else {
            days = 0;
        }

        if (durHours != null) {
            hours = durHours.intValue();
        } else {
            hours = 0;
        }

        if (durMinutes != null) {
            minutes = durMinutes.intValue();
        } else {
            minutes = 0;
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

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
