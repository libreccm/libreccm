/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.ui.admin.applications.tree.LegacyApplicationTreeModelBuilder;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 * A tab for managing CcmApplication and application instances.
 *
 * @author pb
 * @author Jens Pelzetter
 */
public class ApplicationsAdministrationTab extends LayoutPanel {

    private final Tree applicationTree;
    private final Map<String, LegacyBaseApplicationPane> appPanes
                                                       = new HashMap<>();
    private final Map<String, LegacyApplicationInstancePane> instancePanes
                                                           = new HashMap<>();
    private final BoxPanel appPanel;

    /**
     * Constructor
     */
    public ApplicationsAdministrationTab() {

        super();

        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title", "Sitemap");

        applicationTree = new Tree(new LegacyApplicationTreeModelBuilder());
        applicationTree.addChangeListener(new TreeStateChangeListener());

        setClassAttr("navbar");

        setLeft(applicationTree);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final org.libreccm.web.ApplicationManager appManager = cdiUtil.findBean(
            org.libreccm.web.ApplicationManager.class);

        final Collection<ApplicationType> applicationTypes = appManager.
            getApplicationTypes().values();

        final Map<String, ApplicationManager<?>> appManagers
                                                     = ApplicationManagers.
            getInstance().
            getApplicationManagers();

        for (ApplicationType appType : applicationTypes) {
            if (appType.singleton()) {
                createSingletonAppPane(appType, appManagers);
            } else {
                createAppPane(appType, appManagers);
            }
        }

        appPanel = new BoxPanel();
        appPanel.setClassAttr("main");
        for (Map.Entry<String, LegacyBaseApplicationPane> entry : appPanes.entrySet()) {
            appPanel.add(entry.getValue());
        }

        for (Map.Entry<String, LegacyApplicationInstancePane> entry : instancePanes.
            entrySet()) {
            appPanel.add(entry.getValue());
        }

        //setRight(appPanel);
        setBody(appPanel);
    }

    private void createSingletonAppPane(final ApplicationType applicationType,
                                        final Map<String, ApplicationManager<?>> appManagers) {
        final String appObjectType = applicationType.name();

        final ApplicationManager<?> manager = appManagers.get(appObjectType);
        final LegacySingletonApplicationPane pane;
        if (manager == null) {
            pane = new LegacySingletonApplicationPane(applicationType, null);
        } else {
            pane = new LegacySingletonApplicationPane(
                applicationType, appManagers.get(appObjectType).
                getApplicationAdminForm());
        }
        appPanes.put(appObjectType, pane);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void createAppPane(
        final ApplicationType applicationType,
        final Map<String, ApplicationManager<?>> appManagers) {
        final ApplicationManager<?> appManager = appManagers.get(
            applicationType.name());
        final Form createForm;
        if (appManager == null) {
            createForm = null;
        } else {
            createForm = appManager.getApplicationCreateForm();
        }

        final LegacyMultiInstanceApplicationPane<?> appPane
                                                  = new LegacyMultiInstanceApplicationPane(
                applicationType, createForm);
        appPanes.put(applicationType.name(), appPane);
        createInstancePane(applicationType, appManagers);
    }

    private void createInstancePane(
        final ApplicationType applicationType,
        final Map<String, ApplicationManager<?>> managementForms) {
        final ApplicationManager<?> manager = managementForms.get(
            applicationType.name());
        final LegacyApplicationInstancePane instPane;
        if (manager == null) {
            instPane = new LegacyApplicationInstancePane(new Placeholder());
        } else {
            instPane = new LegacyApplicationInstancePane(managementForms.get(
                applicationType.name()).
                getApplicationAdminForm());
        }

        instancePanes.put(applicationType.name(), instPane);
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        for (Map.Entry<String, LegacyBaseApplicationPane> entry : appPanes.entrySet()) {
            page.setVisibleDefault(entry.getValue(), false);
        }
        for (Map.Entry<String, LegacyApplicationInstancePane> entry : instancePanes.
            entrySet()) {
            page.setVisibleDefault(entry.getValue(), false);
        }
    }

    private void setPaneVisible(final SimpleContainer pane,
                                final PageState state) {
        for (Map.Entry<String, LegacyBaseApplicationPane> entry : appPanes.entrySet()) {
            entry.getValue().setVisible(state, false);
        }
        for (Map.Entry<String, LegacyApplicationInstancePane> entry : instancePanes.
            entrySet()) {
            entry.getValue().setVisible(state, false);
        }

        pane.setVisible(state, true);
    }

    private class TreeStateChangeListener implements ChangeListener {

        public TreeStateChangeListener() {
            //Nothing
        }

        @Override
        public void stateChanged(final ChangeEvent event) {
            final PageState state = event.getPageState();

            final String selectedKey = (String) applicationTree.getSelectedKey(
                state);
            if (selectedKey != null) {
                if (selectedKey.contains(".")) {
                    // Selected key is a classname and therefore the key of an ApplicationPane
                    final LegacyBaseApplicationPane pane = appPanes.get(selectedKey);
                    if (pane != null) {
                        setPaneVisible(pane, state);
                    }
                } else {
                    // Selected key is the name of a instance pane
                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final ApplicationRepository appRepo = cdiUtil.findBean(
                        ApplicationRepository.class);

                    final CcmApplication application = appRepo
                        .retrieveApplicationForPath(selectedKey);

                    final LegacyApplicationInstancePane pane;
                    if (application != null) {
                        pane = instancePanes.get(application.getClass().
                            getName());
                        if (pane != null) {
                            pane.setApplication(application);
                        }
                    } else {
                        pane = null;
                    }

                    if (pane != null) {
                        setPaneVisible(pane, state);
                    }
                }
            }
        }

    }

    private class Placeholder extends ApplicationInstanceAwareContainer {

        public Placeholder() {
            super();
            final Label label = new Label(new GlobalizedMessage(
                "ui.admin.applications.placeholder", ADMIN_BUNDLE));
            add(label);
        }

    }

}
