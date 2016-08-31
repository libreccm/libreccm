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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormModel;
import com.arsdigita.xml.Element;
import java.util.Iterator;
import java.util.Collections;
import javax.servlet.ServletException;

/**
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public final class NullComponent implements Component {

    public void generateXML(PageState state, Element parent) {
        // Empty
    }

    public void respond(PageState state) throws ServletException {
        // Empty
    }

    public Iterator children() {
        return Collections.EMPTY_LIST.iterator();
    }

    public void register(Page page) {
        // Empty
    }

    public void register(Form form, FormModel model) {
        // Empty
    }

    public String getClassAttr() {
        return null;
    }

    public void setClassAttr(String clacc) {
        // Empty
    }

    public String getStyleAttr() {
        return null;
    }

    public void setStyleAttr(String style) {
        // Empty
    }

    public String getIdAttr() {
        return null;
    }

    public void setIdAttr(String id) {
        // Empty
    }

    public Component setKey(String key) {
        return null;
    }

    public String getKey() {
        return null;
    }

    public boolean isVisible(PageState state) {
        return false;
    }

    public void setVisible(PageState state, boolean visible) {
        // Empty
    }

    public void lock() {
        // Empty
    }
    
    public boolean isLocked() {
        return true;
    }
}
