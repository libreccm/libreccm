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


import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentTextField;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;


/**
 * This class contains the form component for adding a number element to
 * a content type
 *
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Revision: #13 $ $Date: 2004/08/17 $
 */
public class AddNumberElement extends ElementAddForm {

    private CheckboxGroup m_valReq;  //whether a value is requred

    /**
     * Constructor
     */
    public AddNumberElement(ACSObjectSelectionModel types) {
        super("ContentTypeAddNumberElement", "Add a Number Element", types);

/*
        add(new Label(GlobalizationUtil.globalize("cms.ui.type.element.value_required")));
        m_valReq = new CheckboxGroup("AddNumberElementValReq");
        m_valReq.addOption(new Option("yes", "Yes"));
        add(m_valReq);
*/
        
        add(m_buttons, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);
    }

    protected final void addAttribute(DynamicObjectType dot, String label,
                                      PageState state)
        throws FormProcessException {

//        String[] valReq = (String[]) m_valReq.getValue(state);
        // Quasimodo
        // Disable the value requierd feature
        String[] valReq = null;

        if (valReq == null) {
            dot.addOptionalAttribute(label, MetadataRoot.BIGDECIMAL);
        } else {
            dot.addRequiredAttribute(label, MetadataRoot.BIGDECIMAL, "0");
        }
    }

    protected final void addFormComponent(PersistentForm pForm, String label,
                                          PageState state)
        throws FormProcessException {

        PersistentTextField pTextField = PersistentTextField.create(label);
        pTextField.setParameterModel
            ("com.arsdigita.bebop.parameters.BigDecimalParameter");
        pTextField.save();
        pForm.addComponent(pTextField);
    }
}
