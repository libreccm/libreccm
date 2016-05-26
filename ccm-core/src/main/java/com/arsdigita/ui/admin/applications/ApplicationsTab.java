/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.toolbox.ui.LayoutPanel;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationsTab extends LayoutPanel {

    private final StringParameter selectedAppTypeParam;
    private final ParameterSingleSelectionModel<String> selectedAppType;

    private final StringParameter selectedAppInstanceParam;
    private final ParameterSingleSelectionModel<String> selectedAppInstance;

    private final Tree applicationTree;

    public ApplicationsTab() {
        super();

        setClassAttr("sidebarNavPanel");

        selectedAppTypeParam = new StringParameter("selectedAppType");
        selectedAppType = new ParameterSingleSelectionModel<>(
                selectedAppTypeParam);

        selectedAppInstanceParam = new StringParameter("selectedAppInstance");
        selectedAppInstance = new ParameterSingleSelectionModel<>(
                selectedAppInstanceParam);

        applicationTree = new Tree(new ApplicationTreeModelBuilder());
        applicationTree.addChangeListener(e -> {

        });

        setLeft(applicationTree);

    }

    @Override
    public void register(final Page page) {
        super.register(page);
        
        page.addGlobalStateParam(selectedAppTypeParam);
        page.addGlobalStateParam(selectedAppInstanceParam);

    }

}
