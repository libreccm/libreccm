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
package org.librecms.ui.pagemodel;

import com.vaadin.ui.TextField;
import org.libreccm.admin.ui.AbstractPageModelComponentEditor;
import org.libreccm.admin.ui.PageModelComponentEditorController;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.librecms.CmsConstants;
import org.librecms.pagemodel.ContentItemComponent;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractContentItemComponentEditor<T extends ContentItemComponent>
    extends AbstractPageModelComponentEditor<T> {

    private static final long serialVersionUID = -4872408582648018134L;

    private final PageModelComponentEditorController controller;

    private TextField modeField;

    public AbstractContentItemComponentEditor(
        final PageModel pageModel,
        final PageModelComponentModel componentModel,
        final PageModelComponentEditorController controller) {

        super(pageModel, componentModel, controller);

        this.controller = controller;

        addWidgets();
    }

    public AbstractContentItemComponentEditor(
        final PageModel pageModel,
        final T componentModel,
        final PageModelComponentEditorController controller) {

        super(pageModel, componentModel, controller);

        this.controller = controller;

        addWidgets();
    }

    private void addWidgets() {

        final LocalizedTextsUtil textsUtil = controller
            .getGlobalizationHelper()
            .getLocalizedTextsUtil(CmsConstants.CMS_BUNDLE);

        modeField = new TextField(textsUtil
            .getText("cms.ui.pagemodel.contentitem_component_form.mode.label"));
        addComponent(modeField);
    }

    @Override
    protected void initWidgets() {
        
        final T component = getComponentModel();
        
        if (component != null) {
            modeField.setValue(component.getMode());
        }
    }

    @Override
    protected void updateComponentModel() {
        
        final T component = getComponentModel();
        
        component.setMode(modeField.getValue());
    }

}
