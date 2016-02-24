/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import java.text.DateFormatSymbols;

import java.util.Calendar;
import java.util.GregorianCalendar;

import com.arsdigita.util.Assert;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.DateParameter;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.parameters.DateTimeParameter;
import com.arsdigita.bebop.parameters.IncompleteDateParameter;
import com.arsdigita.bebop.parameters.NotNullValidationListener;

// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;

import com.arsdigita.bebop.util.GlobalizationUtil;

import com.arsdigita.xml.Element;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;

import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * A class representing a date field in an HTML form.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Michael Pih
 * @author Sören Bernstein <quasi@quasiweb.de>
 * @version $Id$
 */
public class Date extends Widget implements BebopConstants {

    protected OptionGroup m_year;
    protected OptionGroup m_month;
    protected TextField m_day;
    private int m_year_begin;
    private int m_year_end;
    private Locale m_locale;
    private boolean yearAsc = true;

    /**
     * Inner class for the year fragment
     */
    protected class YearFragment extends SingleSelect {

        protected Date parent;
        private boolean autoCurrentYear; //Decide wether to set the current year if year is null

        /**
         * Constructor.
         *
         * @param name
         * @param parent
         */
        public YearFragment(String name, Date parent) {
            super(name);
            this.parent = parent;
            setHint(GlobalizationUtil.globalize("bebop.date.year.hint"));
        }

        /**
         *
         * @param ps
         *
         * @return
         */
        @Override
        protected ParameterData getParameterData(PageState ps) {
            Object value = getValue(ps);
            if (value == null) {
                return null;
            }
            return new ParameterData(getParameterModel(), value);
        }

        /**
         *
         * @param autoCurrentYear
         */
        public void setAutoCurrentYear(final boolean autoCurrentYear) {
            this.autoCurrentYear = autoCurrentYear;
        }

        /**
         *
         * @param ps
         *
         * @return
         */
        @Override
        public Object getValue(PageState ps) {
            ParameterModel model = parent.getParameterModel();
            if (model instanceof IncompleteDateParameter) {
                if (((IncompleteDateParameter) model).isYearSkipped()) {
                    return null;
                }
            }
            Object value = parent.getFragmentValue(ps, Calendar.YEAR);
            if ((value == null) && autoCurrentYear) {
                Calendar currentTime = GregorianCalendar.getInstance();
                int currentYear = currentTime.get(Calendar.YEAR);
                value = new Integer(currentYear);
            }
            return value;
        }

    }

    /**
     *
     */
    protected class MonthFragment extends SingleSelect {

        protected Date parent;

        public MonthFragment(String name, Date parent) {
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
            ParameterModel model = parent.getParameterModel();
            if (model instanceof IncompleteDateParameter) {
                if (((IncompleteDateParameter) model).isMonthSkipped()) {
                    return null;
                }
            }
            return parent.getFragmentValue(ps, Calendar.MONTH);
        }

    }

    /**
     *
     */
    protected class DayFragment extends TextField {

        protected Date parent;

        public DayFragment(String name, Date parent) {
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
            ParameterModel model = parent.getParameterModel();
            if (model instanceof IncompleteDateParameter) {
                if (((IncompleteDateParameter) model).isDaySkipped()) {
                    return null;
                }
            }
            return parent.getFragmentValue(ps, Calendar.DATE);
        }

    }

    /**
     * Construct a new Date. The model must be a DateParameter
     */
    public Date(ParameterModel model) {
        super(model);

        if (!(model instanceof DateParameter
              || model instanceof DateTimeParameter)) {
            throw new IllegalArgumentException(
                "The Date widget " + model.getName()
                    + " must be backed by a DateParameter parmeter model");
        }

        String name = model.getName();
        String nameYear = name + ".year";
        String nameMonth = name + ".month";
        String nameDay = name + ".day";

        Calendar currentTime = GregorianCalendar.getInstance();

        m_year = new YearFragment(nameYear, this);
        m_month = new MonthFragment(nameMonth, this);
        m_day = new DayFragment(nameDay, this);

        m_day.setMaxLength(2);
        m_day.setSize(2);

        populateMonthOptions();

        int currentYear = currentTime.get(Calendar.YEAR);
        setYearRange(currentYear - 1, currentYear + 3);

    }

    /**
     * Constructor.
     *
     * @param name
     */
    public Date(String name) {
        this(new DateParameter(name));
    }

    public void setAutoCurrentYear(final boolean autoCurrentYear) {
        ((YearFragment) m_year).setAutoCurrentYear(autoCurrentYear);
    }

    public void setYearRange(int yearBegin, int yearEnd) {
        Assert.isUnlocked(this);
        if (yearBegin != m_year_begin || yearEnd != m_year_end) {
            m_year_begin = yearBegin;
            m_year_end = yearEnd;

            m_year.clearOptions();
            if (this.getParameterModel() instanceof IncompleteDateParameter) {
                // Create an empty year entry to unset a date, if either
                //      a) skipYearAllowed is true
                //      b) skipDayAllowed is true and skipMonthAllowed is true, to unset a date
                if (((IncompleteDateParameter) this.getParameterModel())
                    .isSkipYearAllowed()
                        || (((IncompleteDateParameter) this.getParameterModel())
                            .isSkipDayAllowed()
                            && ((IncompleteDateParameter) this
                                .getParameterModel())
                            .isSkipMonthAllowed())) {
                    m_year.addOption(new Option("", ""));
                }
            }
            if (yearAsc) {
                for (int year = m_year_begin; year <= m_year_end; year++) {
                    m_year.addOption(new Option(String.valueOf(year)));
                }
            } else {
                for (int year = m_year_end; year >= m_year_begin; year--) {
                    m_year.addOption(new Option(String.valueOf(year)));
                }
            }
        }
    }

    public boolean getYearAsc() {
        return yearAsc;
    }

    public void setYearAsc(final boolean yearAsc) {
        this.yearAsc = yearAsc;
    }

    public void addYear(java.util.Date date) {
        Calendar cal = new GregorianCalendar();
        cal.setTime(date);
        int year = (cal.get(Calendar.YEAR));
        if (year < m_year_begin) {
            m_year.prependOption(new Option(String.valueOf(year)));
        }

        if (year > m_year_end) {
            m_year.addOption(new Option(String.valueOf(year)));
        }
    }

    /**
     * Returns a string naming the type of this widget.
     *
     * @return
     */
    @Override
    public String getType() {
        return "date";
    }

    /**
     * Sets the <tt>MAXLENGTH</tt> attribute for the <tt>INPUT</tt> tag used to
     * render this form element.
     *
     * @param length
     */
    public void setMaxLength(int length) {
        setAttribute("MAXLENGTH", String.valueOf(length));
    }

    @Override
    public boolean isCompound() {
        return true;
    }

    /**
     * The XML tag for this derived class of Widget.
     *
     * @return
     */
    @Override
    protected String getElementTag() {
        return BEBOP_DATE;
    }

    /**
     *
     * @param ps
     * @param parent
     */
    @Override
    public void generateWidget(PageState ps, Element parent) {

        if (!isVisible(ps)) {
            return;
        }

        Element date = parent.newChildElement(getElementTag(), BEBOP_XML_NS);
        date.addAttribute("name", getParameterModel().getName());
        if (getLabel() != null) {
            date.addAttribute("label", (String) getLabel().localize(ps
                              .getRequest()));
        }
        exportAttributes(date);
        generateDescriptionXML(ps, date);
        generateLocalizedWidget(ps, date);

        // If Element could be null insert an extra widget to clear entry
        if (!hasValidationListener(new NotNullValidationListener())) {
            date.newChildElement("NoDate");
        }
    }

    // Resepct the localized
    public void generateLocalizedWidget(PageState ps, Element date) {

        Locale defaultLocale = Locale.getDefault();
        Locale locale = CdiUtil.createCdiUtil().findBean(
            GlobalizationHelper.class).getNegotiatedLocale();

        // Get the current Pattern
        // XXX This is really, really, really, really, really, really bad
        // but there is no way to get a SimpleDateFormat object for a
        // different locale the the system default (the one you get with
        // Locale.getDefault();). Also there is now way getting the pattern
        // in another way (up until JDK 1.1 there was), so I have to temporarly
        // switch the default locale to my desired locale, get a SimpleDateFormat
        // and switch back.
        Locale.setDefault(locale);
        String format = new SimpleDateFormat().toPattern();
        Locale.setDefault(defaultLocale);

        // Repopulate the options for the month select box to get them localized
        populateMonthOptions();

        char[] chars = format.toCharArray();
        for (int i = 0; i < chars.length; i++) {

            // Test for doublettes
            if (i >= 1 && chars[i - 1] == chars[i]) {
                continue;
            }

            switch (chars[i]) {
                case 'd':
                    m_day.generateXML(ps, date);
                    break;
                case 'M':
                    m_month.generateXML(ps, date);
                    break;
                case 'y':
                    m_year.generateXML(ps, date);
                    break;
                default:
                    break;
            }

        }

    }

    @Override
    public void setDisabled() {
        m_month.setDisabled();
        m_day.setDisabled();
        m_year.setDisabled();
    }

    @Override
    public void setReadOnly() {
        m_month.setReadOnly();
        m_day.setReadOnly();
        m_year.setReadOnly();
    }

    /**
     * Sets the Form Object for this Widget. This method will throw an exception
     * if the _form pointer is already set. To explicity change the _form
     * pointer the developer must first call setForm(null)
     *
     * @param f the <code>Form</code> Object for this Widget.
     *
     * @exception IllegalStateException if form already set.
     */
    @Override
    public void setForm(Form f) {
        super.setForm(f);
        m_year.setForm(f);
        m_month.setForm(f);
        m_day.setForm(f);
    }

    public Object getFragmentValue(PageState ps, int field) {
        Assert.exists(ps, "PageState");
        FormData f = getForm().getFormData(ps);
        if (f != null) {
            java.util.Date value = (java.util.Date) f.get(getName());
            if (value != null) {
                Calendar c = Calendar.getInstance();
                c.setTime(value);
                return new Integer(c.get(field));
            }
        }
        return null;
    }

    @Override
    public void setClassAttr(String at) {
        m_month.setClassAttr(at);
        m_year.setClassAttr(at);
        m_day.setClassAttr(at);
        super.setClassAttr(at);
    }

    private void populateMonthOptions() {

        Locale locale = CdiUtil.createCdiUtil().findBean(
            GlobalizationHelper.class).getNegotiatedLocale();

        if (m_locale == null || (locale != null && !m_locale.equals(locale))) {

            DateFormatSymbols dfs = new DateFormatSymbols(locale);
            String[] months = dfs.getMonths();

            m_month.clearOptions();

            if (this.getParameterModel() instanceof IncompleteDateParameter) {
                if (((IncompleteDateParameter) this.getParameterModel())
                    .isSkipMonthAllowed()) {
                    m_month.addOption(new Option("", ""));
                }
            }
            for (int i = 0; i < months.length; i += 1) {
                // This check is necessary because
                // java.text.DateFormatSymbols.getMonths() returns an array
                // of 13 Strings: 12 month names and an empty string.
                if (months[i].length() > 0) {
                    m_month.addOption(new Option(String.valueOf(i), months[i]));
                }
            }
            m_locale = CdiUtil.createCdiUtil().findBean(
                GlobalizationHelper.class).getNegotiatedLocale();
        }
    }

}
