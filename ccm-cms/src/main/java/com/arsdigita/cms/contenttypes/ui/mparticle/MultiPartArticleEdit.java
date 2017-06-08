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
package com.arsdigita.cms.contenttypes.ui.mparticle;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;

import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.ItemSelectionModel;

import org.librecms.contenttypes.MultiPartArticle;

import com.arsdigita.toolbox.ui.DomainObjectPropertySheet;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.SimpleEditStep;
import com.arsdigita.cms.ui.workflow.WorkflowLockedComponentAccess;
import com.arsdigita.globalization.GlobalizedMessage;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;

import java.text.DateFormat;

/**
 * A MultiPartArticle editing component.
 *
 * @author <a href="mailto:dturner@arsdigita.com">Dave Turner</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class MultiPartArticleEdit extends SimpleEditStep {

    /**
     * Constructor.
     *
     * @param itemSelectionModel    the ItemSelectionModel which holds the
     *                              current MutliPartArticle
     * @param authoringKitWizard    the parent wizard which contains the form
     * @param selectedLanguageParam
     */
    public MultiPartArticleEdit(final ItemSelectionModel itemSelectionModel,
                                final AuthoringKitWizard authoringKitWizard,
                                final StringParameter selectedLanguageParam) {

        super(itemSelectionModel, authoringKitWizard, selectedLanguageParam);

        setDefaultEditKey("edit");
        MultiPartArticleForm form = getForm(itemSelectionModel);
        add("edit",
            new GlobalizedMessage("cms.ui.edit",
                                  CmsConstants.CMS_BUNDLE),
            new WorkflowLockedComponentAccess(form, itemSelectionModel),
            form.getSaveCancelSection().getCancelButton()
        );

        setDisplayComponent(getMultiPartArticlePropertiesSheet(
            itemSelectionModel));
    }

    protected MultiPartArticleForm getForm(
        final ItemSelectionModel itemSelectionModel) {

        return new MultiPartArticleEditForm(itemSelectionModel, this);
    }

    public Component getMultiPartArticlePropertiesSheet(
        final ItemSelectionModel itemSelectionModel) {

        final DomainObjectPropertySheet sheet = new DomainObjectPropertySheet(
            itemSelectionModel);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil
            .findBean(ConfigurationManager.class);
        final CMSConfig cmsConfig = confManager
            .findConfiguration(CMSConfig.class);

        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.title",
                                        CmsConstants.CMS_BUNDLE),
                  "title");
        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.name",
                                        CmsConstants.CMS_BUNDLE),
                  "name");
        if (!cmsConfig.isHideLaunchDate()) {
            sheet.add(new GlobalizedMessage("cms.contenttypes.ui.launch_date",
                                            CmsConstants.CMS_BUNDLE),
                      "launchDate",
                      new LaunchDateAttributeFormatter());
        }
        sheet.add(new GlobalizedMessage("cms.contenttypes.ui.summary",
                                        CmsConstants.CMS_BUNDLE),
                  "summary");

        return sheet;
    }

}
