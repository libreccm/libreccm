/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.ui.IsAuthenticatedFilter;
import org.librecms.ui.contentsections.documents.DocumentController;
import org.librecms.ui.contentsections.documents.DocumentLifecyclesController;
import org.librecms.ui.contentsections.documents.DocumentWorkflowController;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 * JAX-RS application for managing a content section.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationPath("/@contentsections")
public class ContentSectionApplication extends Application {

    private static final Logger LOGGER = LogManager.getLogger(
        ContentSectionApplication.class
    );

    @Override
    public Set<Class<?>> getClasses() {
        final Set<Class<?>> classes = new HashSet<>();

        classes.add(AssetFolderController.class);
        classes.add(CategoriesController.class);
        classes.add(ConfigurationController.class);
        classes.add(ConfigurationDocumentTypesController.class);
        classes.add(ConfigurationLifecyclesController.class);
        classes.add(ConfigurationRolesController.class);
        classes.add(ConfigurationWorkflowController.class);
        classes.add(ContentSectionController.class);
        classes.add(DocumentFolderController.class);
        classes.add(DocumentController.class);
        classes.add(DocumentLifecyclesController.class);
        classes.add(DocumentWorkflowController.class);
        classes.add(IsAuthenticatedFilter.class);

        return classes;
    }

   

}
