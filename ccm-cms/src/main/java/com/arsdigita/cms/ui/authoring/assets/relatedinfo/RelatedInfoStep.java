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
package com.arsdigita.cms.ui.authoring.assets.relatedinfo;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.authoring.AuthoringKitWizard;
import com.arsdigita.cms.ui.authoring.ResettableContainer;

import org.librecms.CmsConstants;
import org.librecms.ui.authoring.ContentItemAuthoringStep;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ContentItemAuthoringStep(
    labelBundle = CmsConstants.CMS_BUNDLE,
    labelKey = "related_info_step.label",
    descriptionBundle = CmsConstants.CMS_BUNDLE,
    descriptionKey = "related_info_step.description")
public class RelatedInfoStep extends ResettableContainer {

    public RelatedInfoStep(final ItemSelectionModel itemSelectionModel,
                           final AuthoringKitWizard authoringKitWizard,
                           final StringParameter selectedLanguage) {

        super();

        super.add(new Text("Related Info Step placeholder"));
    }

    protected void showAttachmentList(final PageState state) {
        
    }
    
}
