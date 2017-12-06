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
package org.librecms.ui;

import com.vaadin.cdi.CDIView;
import com.vaadin.navigator.View;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@CDIView(value = CmsView.VIEWNAME,
         uis = {CmsUI.class})
class CmsView extends CustomComponent implements View {

    private static final long serialVersionUID = 8989848156887929448L;

    public static final String VIEWNAME = "cms";

    private final TabSheet tabSheet;
    
    @Inject
    CmsView(final CmsViewController controller) {

        super();
        
        tabSheet = new TabSheet();
        
        final ContentSectionsGrid sectionsGrid = new ContentSectionsGrid(controller);
        sectionsGrid.setWidth("100%");
        tabSheet.addTab(sectionsGrid, "Content Section");
        tabSheet.addTab(new PagesTab(controller), "Pages");
        tabSheet.addTab(new Label("Placeholder"), "Search");
        tabSheet.addTab(new Label("Placeholder"), "My tasks");
        
        super.setCompositionRoot(tabSheet);
    }

}
