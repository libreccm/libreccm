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
package com.arsdigita.cms.dispatcher;


import com.arsdigita.cms.CMS;
import com.arsdigita.dispatcher.RequestContext;
import com.arsdigita.xml.Document;
import com.arsdigita.xml.Element;

import org.librecms.contentsection.ContentItem;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/***
 *
 * XMLPage
 *
 * Designed to allow you to output straight XML directly from the ContentItem
 * that implements XMLGenerator, with none of the surrounding headers, footers, etc
 *
 * @author slater@arsdigita.com
 *
 ***/

public class ItemXML extends ResourceHandlerImpl {

    public ItemXML() {
        super();
    }

    public void dispatch(HttpServletRequest request,
                         HttpServletResponse response,
                         RequestContext actx)
        throws IOException, ServletException {
        
        ContentItem item = getContentItem(request);
        
        Element content = new Element("cms:item", CMS.CMS_XML_NS);
        
//        ContentItemXMLRenderer renderer = 
//            new ContentItemXMLRenderer(content);
        //ToDo
//        renderer.setWrapAttributes(true);
//        renderer.setWrapRoot(false);
//        renderer.setWrapObjects(false);
//        
//        renderer.walk(item, SimpleXMLGenerator.ADAPTER_CONTEXT);
//ToDo End

        Document doc;
        try {
            doc = new Document(content);
        } catch (javax.xml.parsers.ParserConfigurationException e) {
            throw new javax.servlet.ServletException(e);
        }
        
        OutputStream out = response.getOutputStream();
        try {
            out.write(doc.toString(true).getBytes());
        } catch (IOException e) {
            throw new ServletException(e);
        } finally {
            out.close();
        }
    }
}
