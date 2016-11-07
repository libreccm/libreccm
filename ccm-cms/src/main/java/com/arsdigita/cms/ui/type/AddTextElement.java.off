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
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentTextArea;
import com.arsdigita.formbuilder.PersistentTextField;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;


/**
 * This class contains the form component for adding an text element to
 * a content type
 *
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @version $Revision: #14 $ $Date: 2004/08/17 $
 */
public class AddTextElement extends ElementAddForm {

    private static final String INPUT_BOX = "inputBox";
    private static final String TEXT_AREA = "textArea";

    private TextField m_length;  //can be a number or default 4000
    private RadioGroup m_dataEntry;  //data entry method
    private CheckboxGroup m_valReq;

    /**
     * Constructor
     */
    public AddTextElement(ACSObjectSelectionModel types) {
        super("ContentTypeAddTextElement", "Add a Text Element", types);

        add(new Label(GlobalizationUtil.globalize
                      ("cms.ui.type.maximum_length")));
        m_length = new TextField(new IntegerParameter("length"));
        m_length.setSize(15);
        m_length.setMaxLength(10);
        add(m_length);

        add(new Label(GlobalizationUtil.globalize
                      ("cms.ui.type.data_entry_method")));
        m_dataEntry = new RadioGroup("TextElementDataEntryMethodSelect");
        m_dataEntry.setClassAttr("vertical");
        m_dataEntry.addOption(new Option( INPUT_BOX, "Input box"));
        m_dataEntry.addOption(new Option( TEXT_AREA, "Text Area"));
        add(m_dataEntry);

/*
        add(new Label(GlobalizationUtil.globalize
                      ("cms.ui.type.element.value_required")));
        m_valReq = new CheckboxGroup("AddTextElementValReq");
        m_valReq.addOption(new Option("yes", "Yes"));
        add(m_valReq);
*/
        
        add(m_buttons, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);
    }


    protected final void addAttribute(DynamicObjectType dot, String label,
                                      PageState state)
        throws FormProcessException {

        Integer length = (Integer) m_length.getValue(state);
//        String[] valReq = (String[]) m_valReq.getValue(state);
        // Quasimodo
        // Disable the value requierd feature
        String[] valReq = null;

        if (length == null) {
            length = new Integer(4000);
        }

        if (valReq == null) {
            dot.addOptionalAttribute(label, MetadataRoot.STRING,
                                     length.intValue());
        } else {
            dot.addRequiredAttribute(label, MetadataRoot.STRING,
                                     length.intValue(), "  ");
        }
    }

    protected final void addFormComponent(PersistentForm pForm, String label,
                                          PageState state)
        throws FormProcessException {

        String dataEntry = (String) m_dataEntry.getValue(state);
        Integer length = (Integer) m_length.getValue(state);

        if (dataEntry.equals(INPUT_BOX)) {

            PersistentTextField pTextField = PersistentTextField.create(label);
            if (length!=null) {
                pTextField.setMaxLength(length.intValue());
            }
            pTextField.save();
            pForm.addComponent(pTextField);
        } else if (dataEntry.equals(TEXT_AREA)) {
            PersistentTextArea pTextArea = PersistentTextArea.create(label);
            pTextArea.save();
            pForm.addComponent(pTextArea);
        }
    }

    /**
     * Sets default values for input fields.
     */
    protected final void doInit(FormSectionEvent e) {
        PageState state = e.getPageState();

        m_dataEntry.setValue(state, INPUT_BOX);
    }
}
