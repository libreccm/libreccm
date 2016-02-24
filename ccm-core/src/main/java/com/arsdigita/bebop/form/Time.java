/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.DateTimeParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;
import com.arsdigita.bebop.parameters.NumberInRangeValidationListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.TimeParameter;

import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.util.Assert;
import com.arsdigita.xml.Element;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;

import java.text.DateFormat;

import java.text.DateFormatSymbols;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Locale;

/**
 * A class representing a time field in an HTML form.
 *
 * @see com.arsdigita.bebop.form.DateTime
 * @author Dave Turner
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Id$
 */
public class Time extends Widget implements BebopConstants {

    private TextField m_hour;
    private TextField m_minute;
    private TextField m_second;
    private OptionGroup m_amOrPm;
    private boolean m_showSeconds;
    private static final String ZERO = "0";

    private class HourFragment extends TextField {

        private Time parent;

        public HourFragment(String name, Time parent) {
            super(name);
            this.parent = parent;
            if (has12HourClock()) {
                this.addValidationListener(
                    new NumberInRangeValidationListener(1, 12));
            } else {
                this.addValidationListener(
                    new NumberInRangeValidationListener(1, 24));
            }
        }

        @Override
        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        @Override
        public Object getValue(PageState ps) {
            if (has12HourClock()) {
                return parent.getFragmentValue(ps, Calendar.HOUR);
            } else {
                return parent.getFragmentValue(ps, Calendar.HOUR_OF_DAY);
            }
        }

    }

    private class MinuteFragment extends TextField {

        private Time parent;

        public MinuteFragment(String name, Time parent) {
            super(name);
            this.parent = parent;
            this.addValidationListener(
                new NumberInRangeValidationListener(0, 59));
        }

        @Override
        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        @Override
        public Object getValue(PageState ps) {
            Integer min = (Integer) parent.getFragmentValue(ps, Calendar.MINUTE);
            if (min == null) {
                return null;
            }
            if (min.intValue() < 10) {
                return ZERO + min.toString();
            } else {
                return min.toString();
            }
        }

    }

    private class SecondFragment extends TextField {

        private Time parent;

        public SecondFragment(String name, Time parent) {
            super(name);
            this.parent = parent;
            this.addValidationListener(
                new NumberInRangeValidationListener(0, 59));
        }

        @Override
        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        @Override
        public Object getValue(PageState ps) {
            Integer sec = (Integer) parent.getFragmentValue(ps, Calendar.SECOND);
            if (sec == null) {
                return null;
            }
            if (sec.intValue() < 10) {
                return ZERO + sec.toString();
            } else {
                return sec.toString();
            }
        }

    }

    private class AmPmFragment extends SingleSelect {

        private Time parent;

        public AmPmFragment(String name, Time parent) {
            super(name);
            this.parent = parent;
        }

        @Override
        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        @Override
        public Object getValue(PageState ps) {
            return parent.getFragmentValue(ps, Calendar.AM_PM);
        }

    }

    /**
     * Constructor.
     */
    public Time(ParameterModel model) {
        this(model, false);
    }

    /**
     * Constructor.
     */
    public Time(ParameterModel model, boolean showSeconds) {
        super(model);

        if (!(model instanceof TimeParameter
              || model instanceof DateTimeParameter)) {
            throw new IllegalArgumentException(
                "The Time widget " + model.getName()
                    + " must be backed by a TimeParameter parameter model");
        }

        String name = model.getName();
        String nameHour = name + ".hour";
        String nameMinute = name + ".minute";
        String nameSecond = name + ".second";
        String nameAmOrPm = name + ".amOrPm";

        DateFormatSymbols dfs = new DateFormatSymbols();

        m_hour = new HourFragment(nameHour, this);
        m_minute = new MinuteFragment(nameMinute, this);
        m_showSeconds = showSeconds;
        if (m_showSeconds) {
            m_second = new SecondFragment(nameSecond, this);
        } else {
            m_second = null;
        }
        m_amOrPm = new AmPmFragment(nameAmOrPm, this);

        m_hour.setMaxLength(2);
        m_hour.setSize(2);
        m_minute.setMaxLength(2);
        m_minute.setSize(2);
        if (m_showSeconds) {
            m_second.setMaxLength(2);
            m_second.setSize(2);
        }

        String[] amPmStrings = dfs.getAmPmStrings();
        for (int i = 0; i < amPmStrings.length; i++) {
            m_amOrPm.addOption(new Option(String.valueOf(i), amPmStrings[i]));
        }

    }

    public Time(String name) {
        this(new TimeParameter(name));
    }

    /**
     * Returns a string naming the type of this widget.
     */
    public String getType() {
        return "time";
    }

    /**
     * Sets the <tt>MAXLENGTH</tt> attributes for the <tt>INPUT</tt> tag used to
     * render this form element.
     */
    public void setMaxLength(int length) {
        setAttribute("MAXLENGTH", String.valueOf(length));
    }

    public boolean isCompound() {
        return true;
    }

    /**
     * The XML tag for this derived class of Widget.
     */
    @Override
    protected String getElementTag() {
        return BEBOP_TIME;
    }

    @Override
    public void generateWidget(PageState ps, Element parent) {

        if (!isVisible(ps)) {
            return;
        }

        Element time = parent.newChildElement(getElementTag(), BEBOP_XML_NS);
        time.addAttribute("name", getParameterModel().getName());
        generateDescriptionXML(ps, time);
        generateLocalizedWidget(ps, time);

        // If Element could be null insert a extra widget to clear entry
        if (!hasValidationListener(new NotNullValidationListener())) {
            time.newChildElement("NoTime");
        }
    }

    public void generateLocalizedWidget(PageState ps, Element time) {
        m_hour.generateXML(ps, time);
        m_minute.generateXML(ps, time);
        if (m_showSeconds) {
            m_second.generateXML(ps, time);
        }
        if (has12HourClock()) {
            m_amOrPm.generateXML(ps, time);
        }
    }

    @Override
    public void setDisabled() {
        m_hour.setDisabled();
        m_minute.setDisabled();
        if (m_showSeconds) {
            m_second.setDisabled();
        }
        if (has12HourClock()) {
            m_amOrPm.setDisabled();
        }
    }

    @Override
    public void setReadOnly() {
        m_hour.setReadOnly();
        m_minute.setReadOnly();
        if (m_showSeconds) {
            m_second.setReadOnly();
        }
        if (has12HourClock()) {
            m_amOrPm.setReadOnly();
        }
    }

    /**
     * Sets the Form Object for this Widget. This method will throw an exception
     * if the _form pointer is already set. To explicity change the _form
     * pointer the developer must first call setForm(null)
     *
     * @param the <code>Form</code> Object for this Widget.
     *
     * @exception IllegalStateException if form already set.
     */
    @Override
    public void setForm(Form f) {
        super.setForm(f);
        m_hour.setForm(f);
        m_minute.setForm(f);
        if (m_showSeconds) {
            m_second.setForm(f);
        }
        m_amOrPm.setForm(f);
    }

    private Object getFragmentValue(PageState ps, int field) {
        Assert.exists(ps, "PageState");
        FormData f = getForm().getFormData(ps);
        if (f != null) {
            java.util.Date value = (java.util.Date) f.get(getName());
            if (value != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(value);
                int intVal = c.get(field);
                if (field == Calendar.HOUR && intVal == 0 && has12HourClock()) {
                    intVal = 12;
                }
                return new Integer(intVal);
            }
        }
        return null;
    }

    private boolean has12HourClock() {
        Locale locale = CdiUtil.createCdiUtil().findBean(
            GlobalizationHelper.class).getNegotiatedLocale();
        DateFormat format_12Hour = DateFormat.getTimeInstance(DateFormat.SHORT,
                                                              Locale.US);
        DateFormat format_locale = DateFormat.getTimeInstance(DateFormat.SHORT,
                                                              locale);

        String midnight = "";
        try {
            midnight = format_locale.format(format_12Hour.parse("12:00 AM"));
        } catch (ParseException ignore) {
        }

        return midnight.contains("12");
    }

}
