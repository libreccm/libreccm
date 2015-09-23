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
package com.arsdigita.bebop;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;

import org.apache.log4j.Logger;


/**
 * A (Simple) Component with various descriptive information, specifically 'hints'
 * with explanations about it's proper usage. These hints provide a kind of
 * online manual.
 * 
 * @author Peter Boy (pb@zes.uni-bremen.de)
 * @version $Id: TextStylable.java 287 2005-02-22 00:29:02Z sskracic $
 */
abstract public class DescriptiveComponent extends SimpleComponent {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.bebop.DescriptiveComponent=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log = Logger.getLogger(DescriptiveComponent.class);

    /** Property to store informational text for the user about the Link, e.g. 
     *  how to use it, or when to use it (or not to use it).                  */
    private GlobalizedMessage m_hint; //= GlobalizationUtil.globalize("bebop.hint.no_entry_yet");

    /** Property to store a (localized) label (or title) of this widget. A 
     *  label is the text (name) displayed for the user to identify and 
     *  distinguish the various elements on the screem.                       */
    private GlobalizedMessage m_label;

    /**
     * Sets a popup hint for the component. It usually contains some explanation 
     * for the user about the component, how to use, why it is there, etc.
     * 
     * @param hint GlobalizedMessage object with the information text.
     */
    public void setHint(GlobalizedMessage hint) {
        m_hint = hint;
    }

    /**
     * Retrieve the popup hint for the component. It is specifically meant for
     * client classes which have to generate the xml on their own and can not
     * use the generateDescriptionXML method provided.
     * 
     * @return popup hint message for the component
     */
    public GlobalizedMessage getHint() {
        return m_hint;
    }

    /**
     * Sets a popup hint for the Link. It usually contains some explanation for
     * the user about the link, how to use, why it is there, etc.
     * 
     * @param label GlobalizedMessage object with the text to identify and 
     *              distinguish the component.
     */
    public void setLabel(GlobalizedMessage label) {
        m_label = label;
    }

    /**
     * Retrieve the label for the component. It is specifically meant for
     * client classes which have to generate the XML on their own and can not
     * use the generateDescriptionXML method provided.
     * 
     * @return popup hint message for the component
     */
    public GlobalizedMessage getLabel() {
        return m_label;
    }

    /**
     * Generates a (J)DOM fragment for clients to include into their generated
     * XML.
     * 
     * @param state
     * @param parent the XML Element instance to add the attributes managed by 
     *               by this class
     */
    protected void generateDescriptionXML(final PageState state, 
                                          final Element parent) {
        
        if (m_label != null) {
            parent.addAttribute("label", (String) m_label.localize());
        }
        if (m_hint != null) {
            parent.addAttribute("hint", (String) m_hint.localize());
        }
        // Do we need this?
        //exportAttributes(parent);
    }

          
}
