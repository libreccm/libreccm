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
package com.arsdigita.cms.ui.authoring.article;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;

import com.arsdigita.bebop.parameters.StringParameter;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.BasicPageForm;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

import org.arsdigita.cms.CMSConfig;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;

import java.text.DateFormat;
import java.util.Objects;

/**
 * Authoring step to edit the simple attributes of the GenericArticle content
 * type (and its subclasses). The attributes edited are 'name', 'title',
 * 'article date', 'location', and 'article type'. This authoring step replaces
 * the {@code com.arsdigita.ui.authoring.PageEdit} step for this type.
 */
public class GenericArticlePropertiesStep extends SimpleEditStep {

    /**
     * The name of the editing sheet added to this step
     */
    public static final String EDIT_SHEET_NAME = "edit";

    private DomainObjectPropertySheet domainObjectPropertySheet;

    private final StringParameter selectedLanguageParam;

    public GenericArticlePropertiesStep(
        final ItemSelectionModel itemModel,
        final AuthoringKitWizard parent,
        final StringParameter selectedLanguageParam) {

        super(itemModel, parent, selectedLanguageParam);

        Objects.requireNonNull(selectedLanguageParam);

        this.selectedLanguageParam = selectedLanguageParam;

        setDefaultEditKey(EDIT_SHEET_NAME);
        createEditSheet(itemModel, selectedLanguageParam);

        setDisplayComponent(itemModel);
    }

    protected void createEditSheet(final ItemSelectionModel itemModel,
                                   final StringParameter selectedLanguageParam) {

        final BasicPageForm editSheet = new GenericArticlePropertyForm(
            itemModel,
            this,
            selectedLanguageParam);
        add(EDIT_SHEET_NAME,
            new GlobalizedMessage("cms.ui.edit", CmsConstants.CMS_BUNDLE),
            new WorkflowLockedComponentAccess(editSheet, itemModel),
            editSheet.getSaveCancelSection().getCancelButton());
    }

    protected void setDisplayComponent(final ItemSelectionModel itemModel) {
        setDisplayComponent(getGenericArticlePropertySheet(
            itemModel,
            selectedLanguageParam));
    }

    protected StringParameter getSelectedLanguageParam() {
        return selectedLanguageParam;
    }

    /**
     * Returns a component that displays the properties of the Article specified
     * by the ItemSelectionModel passed in.
     *
     * @param itemModel             The ItemSelectionModel to use
     * @param selectedLanguageParam
     *
     * @pre itemModel != null
     * @return A component to display the state of the basic properties of the
     *         release
     */
    public static Component getGenericArticlePropertySheet(
        final ItemSelectionModel itemModel,
        final StringParameter selectedLanguageParam) {

        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
            itemModel,
            false,
            selectedLanguageParam);

        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.title",
                                        CmsConstants.CMS_BUNDLE),
                  "title");
        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.name",
                                        CmsConstants.CMS_BUNDLE),
                  "name");
        if (!CMSConfig.getConfig().isHideLaunchDate()) {
            sheet.add(new GlobalizedMessage("cms.contenttypes.ui.launch_date",
                                            CmsConstants.CMS_BUNDLE),
                      "launchDate",
                      new DomainObjectPropertySheet.AttributeFormatter() {

                      @Override
                      public String format(final Object item,
                                           final String attribute,
                                           final PageState state) {
                          final ContentItem page = (ContentItem) item;
                          if (page.getLaunchDate() != null) {
                              return DateFormat
                                  .getDateInstance(DateFormat.LONG)
                                  .format(page.getLaunchDate());
                          } else {
                              return (String) new GlobalizedMessage(
                                  "cms.ui.unknown",
                                  CmsConstants.CMS_BUNDLE)
                                  .localize();
                          }
                      }

                  });
        }
        return sheet;
    }

}
