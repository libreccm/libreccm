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


import com.arsdigita.xml.Element;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.Widget;
import com.arsdigita.bebop.parameters.ParameterModel;
// This interface contains the XML element name of this class
// in a constant which is used when generating XML
import com.arsdigita.bebop.util.BebopConstants;



/**
 *    A class representing a textarea field in an HTML form.
 *
 *    @deprecated See {@link DHTMLEditor}
 *    @author Jim Parsons
 */
public class Deditor extends Widget implements BebopConstants {


    /**
     * Constant for specifying <tt>OFF</tt> value for the
     * <tt>WRAP</tt> attribute of this image input.  See <a
     * href="http://developer.netscape.com/docs/manuals/htmlguid/tags10.htm#1340340">here</a>
     * for a description of what this attribute does.  */

    public static final int OFF = 0;

    /**
     * Constant for specifying <tt>HARD</tt> value for the
     * <tt>WRAP</tt> attribute of this image input.  * See <a
     * //href="http://developer.netscape.com/docs/manuals/htmlguid/tags10.htm#1340340">here</a>
     * for a description of what this attribute does.
     */
    public static final int HARD = 1;

    /**
     * Constant for specifying <tt>SOFT</tt> value for the
     * <tt>WRAP</tt> attribute of this image input.  See <a
     * href="http://developer.netscape.com/docs/manuals/htmlguid/tags10.htm#1340340">here</a>
     * for a description of what this attribute does.
     */
    public static final int SOFT = 2;

    public Deditor(String name) {
        super(name);
    }

    public Deditor(ParameterModel model) {
        super(model);
    }


    /**
     *      Returns a string naming the type of this widget.
     */
    public String getType() {
        return "deditor";
    }


    /**
     * Set the default value (text)
     * @deprecated [since 17Aug2001] use {@link Widget#setDefaultValue(Object)}
     */
    public void setValue( String text ) {
        this.setDefaultValue(text);
    }

    /**
     *      Sets the <tt>ROWS</tt> attribute for the <tt>TEXTAREA</tt> tag.
     */
    public void setRows(int rows) {
        setAttribute("rows", String.valueOf(rows));
    }

    /**
     *      Sets the <tt>COLS</tt> attribute for the <tt>TEXTAREA</tt> tag.
     */
    public void setCols(int cols) {
        setAttribute("cols", String.valueOf(cols));
    }

    /**
     *      Sets the <tt>COLS</tt> attribute for the <tt>TEXTAREA</tt> tag.
     */
    public void setWrap(int wrap) {
        String wrapString = null;

        switch (wrap) {
        case OFF:
            wrapString = "off";
            break;
        case HARD:
            wrapString = "hard";
            break;
        case SOFT:
            wrapString = "soft";
            break;
        }

        if (wrapString != null) {
            setAttribute("wrap", wrapString);
        }
    }

    /**
     * Is this a compound widget?
     * @return false
     */
    public boolean isCompound() {
        return false;
    }

    /** The XML tag.
     *  @return The tag to be used for the top level DOM element
     *  generated for this type of Widget.  */
    protected String getElementTag() {
        return "bebop:deditor";
    }

    /**
     * Generates the DOM for the textarea widget
     * <p>Generates DOM fragment:
     * <p><code>&lt;bebop:textarea name=... value=... [onXXX=...]/>
     *  </code>
     */
    public void generateWidget( PageState state, Element parent ) {

        Element deditor = parent.newChildElement(getElementTag(), BEBOP_XML_NS);

        deditor.addAttribute("name", getName());

        String userAgent =
            state.getRequest().getHeader("user-agent").toLowerCase();
        boolean isIE55 =
            (userAgent != null &&
             ((userAgent.indexOf("msie 5.5") != -1) ||
              (userAgent.indexOf("msie 6") != -1)));

        deditor.addAttribute("isIE55", (new Boolean(isIE55)).toString());


        String value = getParameterData(state).marshal();
        if ( value == null ) {
            value = "";
        }
        Element texter = deditor.newChildElement("bebop:textcontent",BEBOP_XML_NS);
        texter.setCDATASection(value);
        exportAttributes(deditor);
    }

}
