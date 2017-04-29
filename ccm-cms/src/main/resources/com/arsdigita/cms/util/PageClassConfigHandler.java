/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.util;


import org.xml.sax.helpers.DefaultHandler;

import java.util.Map;

/**
 *
 * SAX event handler class for parsing configuration file.
 *
 * Parse URL-to-Page/Dispatcher/Servlet mappings from a file.
 *
 * Format of the file is XML:
 * <pre>
 * &lt;dispatcher-configuration&gt;
 *   &lt;url-mapping
 *     &lt;url&gt;my-page&lt;/url&gt;
 *     OR &lt;page-class&gt;com.arsdigita.Page.class&lt;/page-class&gt;
 *   &lt;url-mapping
 * &lt;/dispatcher-configuration&gt;
 * </pre>
 */
public class PageClassConfigHandler extends DefaultHandler {

    private Map m_map;
    private Map m_rmap;
    private StringBuffer m_buffer;
    private String m_url;
    private String m_className;
    
    /** 
     * @param map A map to configure (pages-> classes)
     * @param rmap A map to configure (classes-> pages)
     *
     * @pre md.m_map != null
     */
    public PageClassConfigHandler(Map map, Map rmap) {
        m_map = map;
        // reverse map
        m_rmap = rmap;
        m_buffer = new StringBuffer();
    }

    @Override
    public void characters(char[] ch, int start, int len) {
        for (int i = 0; i < len; i++) {
            m_buffer.append(ch[start + i]);
        }
    }

    @Override
    public void endElement(String uri, String localName, String qn) {
        if ( qn.equals("url") ) {
            m_url = m_buffer.toString().trim();
        } else if ( qn.equals("page-class") ) {
            m_className = m_buffer.toString().trim();
        } else if ( qn.equals("url-mapping") ) {
            m_map.put(m_url, m_className);
            m_rmap.put(m_className, m_url);
        }
        m_buffer = new StringBuffer();
    }
}
