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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;

import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;

import org.librecms.CmsConstants;

/**
 * Authoring step to edit the simple attributes of the Article content type (and
 * its subclasses). The attributes edited are 'name', 'title', 'article date',
 * 'location', 'lead', and 'article type'. This authoring step replaces the
 * {@code com.arsdigita.ui.authoring.PageEdit} step for this type.
 */
public class ArticlePropertiesStep extends GenericArticlePropertiesStep {

    /**
     * The name of the editing sheet added to this step
     */
    public final static String EDIT_SHEET_NAME = "edit";

    private StringParameter selectedLanuageParam;

    public ArticlePropertiesStep(final ItemSelectionModel itemModel,
                                 final AuthoringKitWizard parent,
                                 final StringParameter selectedLanguageParam) {
        super(itemModel, parent, selectedLanguageParam);
    }

    @Override
    protected void createEditSheet(final ItemSelectionModel itemModel) {
        BasicPageForm editSheet;
        editSheet = new ArticlePropertyForm(itemModel, this);
        add(EDIT_SHEET_NAME,
            new GlobalizedMessage("cms.ui.edit", CmsConstants.CMS_BUNDLE),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());
    }

    @Override
    protected void setDisplayComponent(
        final ItemSelectionModel itemModel) {
        setDisplayComponent(getArticlePropertySheet(itemModel, 
                                                    selectedLanuageParam));
    }

    /**
     * Returns a component that displays the properties of the Article specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemModel The ItemSelectionModel to use
     *
     * @pre itemModel != null
     * @return A component to display the state of the basic properties of the
     *         release
     */
    public static Component getArticlePropertySheet(
        final ItemSelectionModel itemModel,
        final StringParameter selectedLanguageParam) {

        final DomainObjectPropertySheet sheet
                                            = (DomainObjectPropertySheet) getGenericArticlePropertySheet(
                itemModel,
                selectedLanguageParam);

        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.lead",
                                        CmsConstants.CMS_BUNDLE),
                  "lead");

        return sheet;
    }

}
