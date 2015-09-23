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
package com.arsdigita.templating;

import com.arsdigita.xml.Document;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for styling and serving XML documents to the response output
 * stream. 
 * 
 * The PresentationManager contains the code that determines which 
 * XSLT transformer(s) are to be applied to a given document. 
 * 
 * The (default) SimplePresentationManager just links to the bebop 
 * implementation. It should suffice for most cases.
 * 
 * A custom presentation manager is needed if an application needs to
 * dynamically apply a set of templates to an XML document in a custom
 * way. Typically, this occurs if the template selection
 * depends on the outcome of some application-specific logic.
 * 
 * @see com.arsdigita.templating.SimplePresentationManager
 *
 * @author Bill Schneider
 * @version ACS 4.6
 * @version $Id$
 */
public interface PresentationManager {

    /**
     * Serves a page whose content is defined by the input XML
     * document.  Gets an appropriate XSLT Transformer object and
     * uses the transformer to convert the DOM input to the final
     * output.
     *
     * @param doc the XML document whose content is to be displayed
     * to the output
     * @param req the servlet request
     * @param resp the servlet response
     */
    public void servePage(Document doc,
                          HttpServletRequest req,
                          HttpServletResponse resp);

    // WRS: I really would like to be able to define
    // "public static getInstance()" here to make the singleton pattern
    // enforced at compile time, but Java doesn't allow that declaration
    // in an interface or abstract class.
}
