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

import org.libreccm.admin.ui.PageModelComponentEditorController;
import org.libreccm.pagemodel.PageModel;
import org.libreccm.pagemodel.PageModelComponentModel;
import org.librecms.pagemodel.CategorizedItemComponent;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CategorizedItemComponentEditor
    extends AbstractContentItemComponentEditor<CategorizedItemComponent> {

    private static final long serialVersionUID = 7641211643041787151L;

    public CategorizedItemComponentEditor(
        final PageModel pageModel,
        final PageModelComponentModel componentModel,
        final PageModelComponentEditorController controller) {

        super(pageModel, componentModel, controller);
    }

    public CategorizedItemComponentEditor(
        final PageModel pageModel,
        final CategorizedItemComponent componentModel,
        final PageModelComponentEditorController controller) {

        super(pageModel, componentModel, controller);
    }

    @Override
    protected boolean validate() {
        //Nothing to validate here.
        return true;
    }

    @Override
    protected CategorizedItemComponent createComponentModel() {

        return new CategorizedItemComponent();
    }

}
