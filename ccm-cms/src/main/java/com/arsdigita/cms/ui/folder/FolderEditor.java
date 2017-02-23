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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.kernel.KernelConfig;
import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.configuration.ConfigurationManager;


/**
 * Implements functionality for renaming a folder. Most code taken from FolderCreator. Need to refactor out base
 * functionality of FolderEditor & Creator.
 *
 * @author <a href="mailto:jorris@arsdigita.com">Jon Orris</a>
 *  @author <a href="mailto:jens.pelzetter@googlemail.com">Jens >Pelzetter</a>
 *
 */

public class FolderEditor extends FolderForm {

    public FolderEditor(final String name, final FolderSelectionModel folder) {
        super(name, folder);
    }

    /**
     * Initialise the form with name & label of folder being edited.
     * @param event
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final Category folder = getCurrentFolder(state);
        data.put(NAME, folder.getName());
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ConfigurationManager confManager = cdiUtil.findBean(ConfigurationManager.class);
        final KernelConfig kernelConfig = confManager.findConfiguration(
                KernelConfig.class);
        data.put(TITLE, 
                 folder.getTitle().getValue(kernelConfig.getDefaultLocale()));
    }


    @Override
    public void process(final FormSectionEvent event) throws FormProcessException {
        final PageState state = event.getPageState();
        final FormData data = event.getFormData();
        final Category folder = getCurrentFolder(state);

        updateFolder(folder, (String)data.get(NAME), (String)data.get(TITLE));
    }
}
