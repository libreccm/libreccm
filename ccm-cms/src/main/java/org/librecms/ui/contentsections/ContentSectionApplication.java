/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.ui.IsAuthenticatedFilter;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationPath("/@contentsections")
public class ContentSectionApplication extends Application {

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
        classes.add(IsAuthenticatedFilter.class);

        return classes;
    }

}
