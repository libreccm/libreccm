/*
 * Copyright (C) 2017 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.ui.admin.sites;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Text;
import com.arsdigita.toolbox.ui.LayoutPanel;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SitesTab extends LayoutPanel {

    public SitesTab() {
        super();

        super.setClassAttr("sidebarNavPanel");
        
        final BoxPanel left = new BoxPanel(BoxPanel.VERTICAL);
        
        
        final BoxPanel right = new BoxPanel(BoxPanel.VERTICAL);
        
        right.add(new Text("Sites placeholder"));
        
        setLeft(left);
        setRight(right);
    }

}
