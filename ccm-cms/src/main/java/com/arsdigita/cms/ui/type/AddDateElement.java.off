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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.formbuilder.PersistentDate;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;

/**
 * This class contains the form component for adding a date element to
 * a content type
 *
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Revision: #14 $ $Date: 2004/08/17 $
 */
public class AddDateElement extends ElementAddForm {

    private Date m_date;
    private CheckboxGroup m_valReq;  //whether a value is requred
    private TextField m_fromYear, m_toYear;

    /**
     * Constructor
     */
    public AddDateElement(ACSObjectSelectionModel types) {
        super("ContentTypeAddDateElement", "Add a Date Element", types);

/*
        add(new Label(GlobalizationUtil.globalize
                      ("cms.ui.type.element.value_required")));
        m_valReq = new CheckboxGroup("AddDateElementValReq");
        // XXX fix l18n wrt request
        m_valReq.addOption(new Option(lz("cms.ui.no"), lz("cms.ui.yes")));
        add(m_valReq);
*/
        
        add(new Label(GlobalizationUtil.globalize
                      ("cms.ui.type.default_date")));
        m_date = new Date("elementdate");
        long cur = System.currentTimeMillis();
        java.util.Date curtime = new java.util.Date(cur);
        m_date.setDefaultValue(curtime);
        m_date.setClassAttr("AddDateElementChooseDate");
        add(m_date);

        add(new Label(GlobalizationUtil.globalize
                      ("cms.ui.type.year_range")));

        m_fromYear = new TextField(new IntegerParameter("fromYear"));
        m_fromYear.setSize(6);
        m_fromYear.setMaxLength(4);
        m_toYear = new TextField(new IntegerParameter("toYear"));
        m_toYear.setSize(6);
        m_toYear.setMaxLength(4);

        FormSection rangeSec = new FormSection
            (new BoxPanel(BoxPanel.HORIZONTAL, false));

        rangeSec.add(new Label(GlobalizationUtil.globalize("cms.ui.type.from")));
        rangeSec.add(m_fromYear);
        rangeSec.add(new Label(GlobalizationUtil.globalize("cms.ui.type.to")));
        rangeSec.add(m_toYear);
        add(rangeSec);

        add(m_buttons, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);
    }


    protected final void addAttribute(DynamicObjectType dot, String label,
                                      PageState state)
        throws FormProcessException {

        java.util.Date date = (java.util.Date) m_date.getValue(state);
//        String[] valReq = (String[]) m_valReq.getValue(state);
        // Quasimodo
        // Disable the value requierd feature
        String[] valReq = null;

        if (valReq == null) {
            dot.addOptionalAttribute(label, MetadataRoot.DATE);
        } else {
            dot.addRequiredAttribute(label, MetadataRoot.DATE, date);
        }
    }


    protected final void addFormComponent(PersistentForm pForm, String label,
                                          PageState state)
        throws FormProcessException {

        PersistentDate pDate = PersistentDate.create(label);
        pDate.setParameterModel("com.arsdigita.bebop.parameters.DateParameter");
        pForm.addComponent(pDate);
    }

    /**
     * Initializes date widget to current date.
     */
    protected final void doInit(FormSectionEvent event) {
        java.util.Date date = new java.util.Date(System.currentTimeMillis());
        m_date.setValue(event.getPageState(), date);
    }

    protected final void doValidate(FormSectionEvent e)
        throws FormProcessException {

        PageState state = e.getPageState();

        Integer fromYear = (Integer) m_fromYear.getValue(state);
        Integer toYear = (Integer) m_toYear.getValue(state);

        if (!(fromYear != null && toYear != null)) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "cms.ui.type.year_range_not_balanced"));
        } else {
            if ((fromYear.intValue() < 0) || (toYear.intValue() < 0)) {
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.type.year_is_negative"));
            }

            if (fromYear.intValue() > toYear.intValue()) {
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.type.year_range_wrong_order"));
            }

            if ((toYear.intValue() - fromYear.intValue()) > 200) {
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.type.year_range_too_great"));
            }

            if ((fromYear.intValue() < 1900 || fromYear.intValue() > 2100) &&
                 (toYear.intValue() < 1900 || toYear.intValue() > 2100)) {
                throw new FormProcessException(GlobalizationUtil.globalize(
                        "cms.ui.type.year_too_anachronistic"));
            }
        }
    }

    private static String lz(final String key) {
        return (String) GlobalizationUtil.globalize(key).localize();
    }
}
