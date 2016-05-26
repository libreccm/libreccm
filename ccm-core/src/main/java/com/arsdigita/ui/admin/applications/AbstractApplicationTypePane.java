/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.globalization.GlobalizedMessage;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractApplicationTypePane extends BoxPanel {
    
    private final SimpleContainer pane;
    private final PropertySheet propertySheet;
    
    public AbstractApplicationTypePane() {
        super(BoxPanel.VERTICAL);
        
        final BoxPanel links = new BoxPanel(BoxPanel.HORIZONTAL);
        final ActionLink paneLink = new ActionLink(getPaneLabel());
        paneLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            showPane(state);
        });
        links.add(paneLink);
        final ActionLink propertySheetLink = new ActionLink(
                new GlobalizedMessage(
                        "ui.admin.appliations.type_pane.info_sheet", 
                        ADMIN_BUNDLE));
        propertySheetLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            showPropertySheet(state);
        });
        links.add(propertySheetLink);
        add(links);
        
        pane = createPane();
        add(pane);
        
        
    }
    
    protected abstract SimpleContainer createPane();
    
    protected abstract GlobalizedMessage getPaneLabel();
    
    @Override
    public void register(final Page page) {
        super.register(page);
        
        page.setVisibleDefault(pane, true);
        page.setVisibleDefault(propertySheet, false);
    }
    
    protected void showPane(final PageState state) {
        pane.setVisible(state, true);
        propertySheet.setVisible(state, false);
    }
    
    protected void showPropertySheet(final PageState state) {
        pane.setVisible(state, false);
        propertySheet.setVisible(state, false);
    }
    
}
