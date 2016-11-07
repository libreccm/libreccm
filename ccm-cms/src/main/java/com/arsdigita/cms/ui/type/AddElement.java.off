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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.cms.util.GlobalizationUtil;


/**
 * This class contains the form component for selecting which
 * type of element to add to a content type
 *
 * @author Xixi D'Moon (xdmoon@arsdigita.com)
 * @version $Revision: #11 $ $Date: 2004/08/17 $
 */
public class AddElement extends CMSForm {

    private SingleSelect m_elementType;
    private Submit m_submit;

    public static final String TEXT_ELEMENT = "text";
    public static final String NUMBER_ELEMENT = "number";
    public static final String DATE_ELEMENT = "date";
    public static final String TEXT_ASSET_ELEMENT = "textAsset";
    public static final String IMAGE_ELEMENT = "image";
    public static final String FILE_ELEMENT = "file";
    public static final String CONTENT_ITEM_ELEMENT = "contentItem";

    /**
     * Constructor
     */

    public AddElement() {
        super("ContentTypeAddElement", new BoxPanel(BoxPanel.HORIZONTAL));

        //possible types of elements that can be added to a user-defined
        //content type
        add(new Label(GlobalizationUtil.globalize("cms.ui.type.element.type")));
        m_elementType = new SingleSelect("elementTypeSelect");
        m_elementType.setClassAttr("AddElementSelectType");
        m_elementType.addOption(new Option("text", "Text"));
        m_elementType.addOption(new Option("number", "Number"));
        m_elementType.addOption(new Option("date", "Date"));
        m_elementType.addOption(new Option("textAsset", "Text Asset"));
        m_elementType.addOption(new Option("image", "Image"));
        m_elementType.addOption(new Option("contentItem", "Content Item"));
        m_elementType.addOption(new Option("file", "File"));
        //m_elementType.addOption(new Option("document", "Document"));
        //m_elementType.addOption(new Option("multimedia", "Multimedia"));
        add(m_elementType);

        m_submit = new Submit("submit");
        m_submit.setButtonLabel("Add Element");
        add(m_submit, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);

        //add the listeners
        //does not do anything other than ui logic in OneType.java
        //addProcessListener(this);
    }

    protected SingleSelect getElementTypeSelect(){
        return m_elementType;
    }

    protected Submit getSubmit(){
        return m_submit;
    }

    /**
     * Processes the form
     */
    /* public void process(FormSectionEvent e) throws FormProcessException {
       PageState state = e.getPageState();
       FormData data = e.getFormData();

       String type = (String) data.get(m_elementType.getName());

       }*/

    /**
     * Retrieve the type of the element that the user wants to add
     * during form processing
     */
    public String getElementType(FormSectionEvent e) {
        return (String)m_elementType.getValue(e.getPageState());
    }

}
