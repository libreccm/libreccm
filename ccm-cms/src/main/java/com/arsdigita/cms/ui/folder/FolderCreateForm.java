/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.categorization.CategoryManager;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;

import static com.arsdigita.cms.ui.folder.FolderForm.*;

public class FolderCreateForm extends FolderForm {

    public FolderCreateForm(final String name, final FolderSelectionModel parent) {
        super(name, parent);
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FolderSelectionModel model = getFolderSelectionModel();
    }

    /**
     * Validates the form. Checks for name uniqueness.
     *
     * @param event
     * @throws com.arsdigita.bebop.FormProcessException
     *
     */
    @Override
    public void validate(final FormSectionEvent event)
        throws FormProcessException {
        
        final Folder folder = getCurrentFolder(event.getPageState());
        final FormData data = event.getFormData();
        final String name = data.getString(NAME);
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CategoryManager categoryManager = cdiUtil.findBean(
            CategoryManager.class);

        if (categoryManager.hasSubCategoryWithName(folder, name)) {
            data.addError(new GlobalizedMessage(
                "cms.ui.folderform.error.child.name_not_unique",
                CmsConstants.CMS_BUNDLE,
                new Object[]{name}));
        }

    }

    @Override
    public void process(final FormSectionEvent event) throws
        FormProcessException {

        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final Folder parent = getCurrentFolder(state);

        final String name = data.getString(NAME);
        final String title = data.getString(TITLE);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final FolderRepository folderRepo = cdiUtil.findBean(
            FolderRepository.class);
        final FolderManager folderManager = cdiUtil
            .findBean(FolderManager.class);
        final ConfigurationManager confManager = cdiUtil.findBean(
            ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
            KernelConfig.class);

        final Folder folder = folderManager.createFolder(name, parent);
        folder.getTitle().addValue(kernelConfig.getDefaultLocale(), title);
        folderRepo.save(folder);
    }

}
