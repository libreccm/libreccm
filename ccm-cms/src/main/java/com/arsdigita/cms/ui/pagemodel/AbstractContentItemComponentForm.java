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
package com.arsdigita.cms.ui.pagemodel;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.pagemodels.AbstractComponentModelForm;
import com.arsdigita.ui.admin.pagemodels.PageModelTab;

import org.librecms.CmsConstants;
import org.librecms.pagemodel.ContentItemComponent;

/**
 * @param <T>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public abstract class AbstractContentItemComponentForm<T extends ContentItemComponent>
    extends AbstractComponentModelForm<T> {

    private static final String ITEM_MODE = "itemMode";

    private TextField modeField;

    public AbstractContentItemComponentForm(
        final String name,
        final PageModelTab pageModelTab,
        final ParameterSingleSelectionModel<String> selectedModelId,
        final ParameterSingleSelectionModel<String> selectedComponentId) {

        super(name, pageModelTab, selectedModelId, selectedComponentId);
    }

    @Override
    protected void addWidgets() {

        modeField = new TextField(ITEM_MODE);
        modeField.setLabel(new GlobalizedMessage(
            "cms.ui.pagemodel.contentitem_component_form.mode.label",
        CmsConstants.CMS_BUNDLE));
        add(modeField);
    }

    @Override
    public void updateComponentModel(final ContentItemComponent componentModel,
                                     final PageState state,
                                     final FormData data) {

        final String modeValue = data.getString(ITEM_MODE);
        componentModel.setMode(modeValue);
    }

    @Override
    public void init(final FormSectionEvent event) 
        throws FormProcessException {
        
        super.init(event);
        
        final PageState state = event.getPageState();
        final ContentItemComponent component = getComponentModel();
        
        modeField.setValue(state, component.getMode());
    }
    
}
