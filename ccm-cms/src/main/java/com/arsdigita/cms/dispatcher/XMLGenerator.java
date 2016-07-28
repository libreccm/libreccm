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
package com.arsdigita.cms.dispatcher;

import com.arsdigita.bebop.PageState;
import com.arsdigita.xml.Element;


/**
 * <p>Generates XML representing a Content Item.</p>
 *
 * <p>As the last step of servicing a page, the
 * {@link com.arsdigita.cms.dispatcher.MasterPage} will go through the
 * hierarchy of its components and ask each of them to convert themselves
 * to XML. A MasterPage contains a special component that knows how to ask
 * its content section for the XML generator that should be applied. The
 * XML generator's <code>generateXML</code> method in turn asks the
 * containing page for the content item, the one that the
 * {@link com.arsdigita.cms.dispatcher.ItemResolver} found before, and
 * formats it as an XML document.</p>
 *
 * @author Michael Pih (pihman@arsdigita.com)
 * @version $Id$
 */
public interface XMLGenerator {

    /**
     * Generates the XML to render the content panel.
     *
     * @param state      The page state
     * @param parent     The parent DOM element
     * @param useContext The use context
     */
    public void generateXML(PageState state, Element parent, String useContext);

}
