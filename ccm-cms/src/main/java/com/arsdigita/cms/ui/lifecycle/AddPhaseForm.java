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
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.bebop.parameters.StringLengthValidationListener;
import com.arsdigita.bebop.parameters.TrimmedStringParameter;

import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.lifecycle.PhaseDefinition;

import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.lifecycle.LifecycleDefinitionRepository;
import org.librecms.lifecycle.PhaseDefinititionRepository;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.Locale;

/**
 * This class contains a form component to add a lifecycle phase definition.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author Jack Chung
 * @author Xixi D'Moon
 * @author Michael Pih
 */
class AddPhaseForm extends CMSForm {

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

    private final LifecycleDefinitionRequestLocal m_cycle;

    private final Hidden m_id;
    private final TextField m_label;
    private final TextArea m_description;
    private final TextField m_delay_days;
    private final TextField m_delay_hours;
    private final TextField m_delay_minutes;
    private final TextField m_duration_days;
    private final TextField m_duration_hours;
    private final TextField m_duration_minutes;
    private final Submit m_submit;
    private final Submit m_cancel;

    /**
     * Constructor.
     *
     * @param cycles The cycle selection model. This is to tell the form which
     *               cycle definition is selected since phase definitions are
     *               associated to cycle definitions
     */
    public AddPhaseForm(final LifecycleDefinitionRequestLocal cycle) {
        super("LifecyclePhaseDefinition");

        m_cycle = cycle;

        m_id = new Hidden(new BigDecimalParameter(PHASE_ID));
        add(m_id);
        m_id.addValidationListener(new NotNullValidationListener());

        Label heading = new Label(gz("cms.ui.lifecycle.phase_add"));
        heading.setFontWeight(Label.BOLD);
        add(heading, ColumnPanel.FULL_WIDTH);
        add(new FormErrorDisplay(this), ColumnPanel.FULL_WIDTH);

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

        // phase delay
        add(new Label(gz("cms.ui.lifecycle.phase_start_delay")));
        m_delay_days = new TextField(new IntegerParameter(DELAY_DAYS));
        m_delay_hours = new TextField(new IntegerParameter(DELAY_HOURS));
        m_delay_minutes = new TextField(new IntegerParameter(DELAY_MINUTES));

        //max value: days: 60 years, hours: 7 days, minutes: 1 day
        m_delay_days.addValidationListener(
            new NumberInRangeValidationListener(0, 21900));
        m_delay_hours.addValidationListener(new NumberInRangeValidationListener(
            0, 168));
        m_delay_minutes.addValidationListener(
            new NumberInRangeValidationListener(0, 1440));
        m_delay_days.setSize(7);
        m_delay_hours.setSize(7);
        m_delay_minutes.setSize(7);
        m_delay_days.setClassAttr("DaysField");
        m_delay_hours.setClassAttr("HoursField");
        m_delay_minutes.setClassAttr("MinutesField");

        SimpleContainer de = new SimpleContainer();
        de.add(new Label(gz("cms.ui.lifecycle.phase_days")));
        de.add(m_delay_days);
        de.add(new Label(gz("cms.ui.lifecycle.phase_hours")));
        de.add(m_delay_hours);
        de.add(new Label(gz("cms.ui.lifecycle.phase_mins")));
        de.add(m_delay_minutes);
        add(de);

        // phase duration
        add(new Label(gz("cms.ui.lifecycle.phase_duration")));
        m_duration_days = new TextField(new IntegerParameter(DURATION_DAYS));
        m_duration_hours = new TextField(new IntegerParameter(DURATION_HOURS));
        m_duration_minutes = new TextField(
            new IntegerParameter(DURATION_MINUTES));

        //max value: days: 60 years, hours: 7 days, minutes: 1 day
        m_duration_days.addValidationListener(
            new NumberInRangeValidationListener(0, 21900));
        m_duration_hours.addValidationListener(
            new NumberInRangeValidationListener(0, 168));
        m_duration_minutes.addValidationListener(
            new NumberInRangeValidationListener(0, 1440));
        m_duration_days.setSize(7);
        m_duration_hours.setSize(7);
        m_duration_minutes.setSize(7);
        m_duration_days.setClassAttr("DaysField");
        m_duration_hours.setClassAttr("HoursField");
        m_duration_minutes.setClassAttr("MinutesField");

        SimpleContainer du = new SimpleContainer();
        du.add(new Label(gz("cms.ui.lifecycle.phase_days")));
        du.add(m_duration_days);
        du.add(new Label(gz("cms.ui.lifecycle.phase_hours")));
        du.add(m_duration_hours);
        du.add(new Label(gz("cms.ui.lifecycle.phase_mins")));
        du.add(m_duration_minutes);
        add(du);

        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit(SUBMIT);
        m_submit.setButtonLabel("Add Phase");
        s.add(m_submit);
        m_cancel = new Submit(CANCEL);
        m_cancel.setButtonLabel("Cancel");
        s.add(m_cancel);
        add(s, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        addInitListener(new FormInitListener() {

            public final void init(final FormSectionEvent event)
                throws FormProcessException {
                initializePhase(event.getPageState());
            }

        });

        addSubmissionListener(new FormSecurityListener(
            CmsConstants.PRIVILEGE_ADMINISTER_LIFECYLES));

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
        return m_cancel.isSelected(state);
    }

    /**
     * Generate a unique ID for the new phase.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    protected void initializePhase(final PageState state)
        throws FormProcessException {
        // Not neccessary with JPA
//        if (m_id.getValue(state) == null) {
//            try {
//                m_id.setValue(state, Sequences.getNextValue());
//            } catch (SQLException e) {
//                throw new UncheckedWrapperException(e);
//            }
//        }
    }

    /**
     * Add a new phase using data from the form.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    protected void addPhase(final PageState state) throws FormProcessException {
        final String label = (String) m_label.getValue(state);
        final String description = (String) m_description.getValue(state);
        final Integer delDays = (Integer) m_delay_days.getValue(state);
        final Integer delHours = (Integer) m_delay_hours.getValue(state);
        final Integer delMinutes = (Integer) m_delay_minutes.getValue(state);
        final Integer durDays = (Integer) m_duration_days.getValue(state);
        final Integer durHours = (Integer) m_duration_hours.getValue(state);
        final Integer durMinutes = (Integer) m_duration_minutes.getValue(state);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final LifecycleDefinitionRepository lifecycleDefRepo = cdiUtil.findBean(
            LifecycleDefinitionRepository.class);
        final PhaseDefinititionRepository phaseDefRepo = cdiUtil.findBean(
            PhaseDefinititionRepository.class);
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        // Check if the object already exists for double click protection.
        final PhaseDefinition phaseDef = new PhaseDefinition();
        final LifecycleDefinition cycleDef = m_cycle.getLifecycleDefinition(
            state);
        cycleDef.addPhaseDefinition(phaseDef);

        phaseDef.getLabel().addValue(defaultLocale, label);
        phaseDef.getDescription().addValue(defaultLocale, description);
        phaseDef.setDefaultDelay(delDays * delHours * delMinutes * 60);
        phaseDef.setDefaultDuration(durDays * durHours * durMinutes * 60);

        phaseDefRepo.save(phaseDef);
        lifecycleDefRepo.save(cycleDef);
    }

    /**
     * Validate name uniqueness.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    protected void validateUniqueName(PageState state)
        throws FormProcessException {
        String label = (String) m_label.getValue(state);

        final LifecycleDefinition cycleDef = m_cycle.getLifecycleDefinition(
            state);
        final List<PhaseDefinition> phaseDefs = cycleDef.getPhaseDefinitions();
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        for (final PhaseDefinition phaseDef : phaseDefs) {
            if (phaseDef.getLabel().getValue(defaultLocale).equalsIgnoreCase(
                label)) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.lifecycle.phase_name_not_unique",
                    CmsConstants.CMS_BUNDLE));
            }
        }
    }

    /**
     * Validate the phase duration. The duration cannot be 0.
     *
     * @param state The page state
     *
     * @pre state != null
     */
    protected void validateDuration(PageState state)
        throws FormProcessException {
        Integer durDays = (Integer) m_duration_days.getValue(state);
        Integer durHours = (Integer) m_duration_hours.getValue(state);
        Integer durMinutes = (Integer) m_duration_minutes.getValue(state);

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
                "cms.ui.lifecycle.phase_duration_negative",
                CmsConstants.CMS_BUNDLE));
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key,
                                     CmsConstants.CMS_BUNDLE);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
