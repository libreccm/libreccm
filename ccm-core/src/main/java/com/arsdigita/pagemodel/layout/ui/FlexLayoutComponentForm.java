/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package com.arsdigita.pagemodel.layout.ui;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.ui.admin.pagemodels.AbstractComponentModelForm;
import com.arsdigita.ui.admin.pagemodels.PageModelsTab;

import org.libreccm.pagemodel.layout.FlexLayout;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FlexLayoutComponentForm
    extends AbstractComponentModelForm<FlexLayout> {

    public FlexLayoutComponentForm(
    final PageModelsTab pageModelsTab, 
    final ParameterSingleSelectionModel<String> selectedModelId,
    final ParameterSingleSelectionModel<String> selectedComponentId) {
        
        
        super("FlexLayoutComponentForm",
              pageModelsTab,
              selectedModelId,
              selectedComponentId);
    }
    
    @Override
    protected void addWidgets() {
        
        final BoxPanel horizontalPanel = new BoxPanel(BoxPanel.HORIZONTAL);
        
        add(horizontalPanel);
    }

    @Override
    protected FlexLayout createComponentModel() {
        return new FlexLayout();
    }

    @Override
    protected void updateComponentModel(final FlexLayout componentModel,
                                        final PageState state, 
                                        final FormData data) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    
    
}
