/*
 * Copyright (C) 2014 Peter Boy, University of Bremen. All Rights Reserved.
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

import static com.arsdigita.bebop.Component.BEBOP_XML_NS;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.xml.Element;

/**
 * Injects arbitrary content as a String into the xml output. It is not for
 * any semantic type of data und it is not localizable. Specifically it is 
 * meant for data as Javascript and alike.
 * 
 * It generates some fixed string to be included in the XML output.
 * 
 * It resembles the Label methods for String parameters and currently 
 * generates the same XML attributes in order to avoid any need to modify the
 * themes.
 * 
 * @author pb
 */
public class Embedded extends SimpleComponent {
    
    private final String m_content;
     /** The setting for output escaping affects how markup in the 
     *  <code>content</code> is handled. 
     *  <UL><LI>If output escaping is in effect (true), &lt;b>example&lt;/b>
     *  will appear literally.</LI> 
     *  <LI>If output escaping is disabled, &lt;b>example&lt;/b> appears as the 
     *  String "example" in bold (i.e. retaining the markup.</LI></UL>
     *  Default is false.                                                     */ 
   private boolean m_escaping = false;  // default for a primitive
    private PrintListener m_printListener;
    
    /**
     * Default constructor creates a new <code>Embedded</code> with the empty 
     * content.
     * 
     * @param content 
     */
    public Embedded() {
        m_content = "";
    }
    
    /**
     * Constructor creates a new <code>Embedded</code> with the specified 
     * (fixed) content.
     * 
     * @param content 
     */
    public Embedded(String content) {
        m_content = content;
    }
    
    /**
     * Constructor creates a new <code>Embedded</code> with the specified 
     * content and output escaping turned on if <code>escaping</code> is
     * <code>true</code>. 
     * 
     * The setting for output escaping affects how markup in the
     * <code>content</code> is handled. For example: <UL><LI>If output escaping
     * is in effect, &lt;b>content&lt;/b> will appear literally.</LI> <LI>If 
     * output escaping is disabled, &lt;b>content&lt;/b> appears as the String 
     * "context" in bold.</LI></UL>
     * 
     * @param content the content to inject into the output.
     * @param escaping <code>true</code> if output escaping will be in effect;
     * <code>false</code> if output escaping will be disabled
     */
    public Embedded(String content, boolean escaping) {
        m_content = content;
        m_escaping = escaping;
    }

    /**
     * Generates the (J)DOM fragment for a embedded.
     * <p><pre>
     * &lt;bebop:link href="..." type="..." %bebopAttr;/>
     * </pre>
     * 
     * @param state The current {@link PageState}.
     * @param parent The XML element to attach the XML to.
     */
    @Override
    public void generateXML(PageState state, Element parent) {

        if (!isVisible(state)) {
            return;
        }

        Embedded target = firePrintEvent(state);

        Element content = parent.newChildElement("bebop:label", BEBOP_XML_NS);
        target.exportAttributes(content);

        if (!target.m_escaping) {
            content.addAttribute("escape", "yes");
        } else {
            content.addAttribute("escape", "no");
        }

        content.setText(m_content);
    }

    /**
     * 
     * @param state
     * @return 
     */
    protected Embedded firePrintEvent(PageState state) {
        Embedded e = this;

        if (m_printListener != null) {
            try {
                e = (Embedded) this.clone();
                m_printListener.prepare(new PrintEvent(this, state, e));
            } catch (CloneNotSupportedException nse) {
                throw new RuntimeException(
                        "Couldn't clone Embedded for PrintListener. "
                        + "This probably indicates a serious programming error: "
                        + nse.getMessage());
            }
        }

        return e;
    }

}
