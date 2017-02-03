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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

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
    private final BoxPanel managementLinks;
    private final Text placeholderInstances;
    private final Text placeholderSingletonSettings;
    private final BoxPanel appInfo;

    private final Map<String, AbstractAppInstanceForm> instanceForms;
    private final Map<String, AbstractAppSettingsPane> settingsPanes;

    public ApplicationsTab() {
        super();

        setClassAttr("sidebarNavPanel");

        selectedAppTypeParam = new StringParameter("selectedAppType");
        selectedAppType = new ParameterSingleSelectionModel<>(
            selectedAppTypeParam);

        selectedAppInstanceParam = new StringParameter("selectedAppInstance");
        selectedAppInstance = new ParameterSingleSelectionModel<>(
            selectedAppInstanceParam);

        instanceForms = new HashMap<>();
        settingsPanes = new HashMap<>();

        applicationTree = new Tree(new ApplicationTreeModelBuilder());
        applicationTree.addChangeListener(e -> {

            final PageState state = e.getPageState();
            final String key = (String) applicationTree.getSelectedKey(state);

            if (key.startsWith(
                ApplicationInstanceTreeNode.INSTANCE_NODE_KEY_PREFIX)) {

                //Instance is selected
                final String instanceId = key.substring(
                    ApplicationInstanceTreeNode.INSTANCE_NODE_KEY_PREFIX
                    .length());
                selectedAppInstance.setSelectedKey(state, instanceId);

                final ApplicationRepository appRepo = CdiUtil.createCdiUtil()
                    .findBean(ApplicationRepository.class);
                final Optional<CcmApplication> application = appRepo.findById(
                    Long.parseLong(instanceId));
                if (application.isPresent()) {
                    selectedAppType.setSelectedKey(
                        state, application.get().getApplicationType());
                }

                showAppSettings(state);

            } else if (key.startsWith(
                ApplicationTypeTreeNode.TYPE_NODE_KEY_PREFIX)) {

                //ApplicationType node is selected
                showManagementLinks(state);
                final String appTypeKey = key.substring(
                    ApplicationTypeTreeNode.TYPE_NODE_KEY_PREFIX.length());

                selectedAppType.setSelectedKey(state, appTypeKey);

                final org.libreccm.web.ApplicationManager appManager = CdiUtil
                    .createCdiUtil().findBean(
                        org.libreccm.web.ApplicationManager.class);
                final ApplicationType appType = appManager.getApplicationTypes()
                    .get(appTypeKey);

                if (appType == null) {
                    hideAllInstanceForms(state);
                    hideAllSettingsPanes(state);
                    hideAppInfo(state);
                    hideInstances(state);
                    hideManagementLinks(state);
                    hideSingletonAppSettings(state);
                } else if (appType.singleton()) {
                    showSingletonAppSettings(state);
                } else {
                    showInstances(state);
                }

            } else {
                throw new IllegalArgumentException(
                    "The key of the selected must be Long or a String");
            }

        });

        setLeft(applicationTree);

        final BoxPanel managementPanel = new BoxPanel(BoxPanel.VERTICAL);
        managementLinks = new BoxPanel(BoxPanel.HORIZONTAL);

        final ActionLink singletonAppSettingsLink
                             = new SingletonAppSettingsLink(
                new GlobalizedMessage("ui.admin.applications.singleton.link",
                                      ADMIN_BUNDLE));
        singletonAppSettingsLink.addActionListener(e -> {
            final PageState state = e.getPageState();

            showSingletonAppSettings(state);
        });
        managementLinks.add(singletonAppSettingsLink);

        final ActionLink instancesLink = new InstancesLink(
            new GlobalizedMessage("ui.admin.applications.instances.link",
                                  ADMIN_BUNDLE));
        instancesLink.addActionListener(e -> {
            final PageState state = e.getPageState();

            showInstances(state);
        });
        managementLinks.add(instancesLink);

        final ActionLink infoLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.applications.info.link", ADMIN_BUNDLE));
        infoLink.addActionListener(e -> {
            final PageState state = e.getPageState();

            showAppInfo(state);
        });
        managementLinks.add(infoLink);

        managementPanel.add(managementLinks);

        placeholderInstances = new Text("placeholder instances");
        managementPanel.add(placeholderInstances);

        placeholderSingletonSettings
            = new Text("placeholder singleton settings");
        managementPanel.add(placeholderSingletonSettings);

        appInfo = new BoxPanel(BoxPanel.VERTICAL);
        final Label appInfoHeading = new Label(new GlobalizedMessage(
            "ui.admin.applications.info.heading", ADMIN_BUNDLE));
        appInfoHeading.setClassAttr("heading");
        appInfo.add(appInfoHeading);

        final PropertySheet appInfoSheet = new PropertySheet(
            new ApplicationTypePropertySheetModelBuilder(selectedAppType));
        appInfo.add(appInfoSheet);
        managementPanel.add(appInfo);

        final org.libreccm.web.ApplicationManager appManager = CdiUtil
            .createCdiUtil().findBean(org.libreccm.web.ApplicationManager.class);
        final Map<String, ApplicationType> appTypes = appManager
            .getApplicationTypes();

        appTypes.entrySet().forEach(e -> {
            final String appTypeName = e.getKey();
            final Class<? extends AbstractAppInstanceForm> instanceFormClass
                                                               = e.getValue()
                .instanceForm();
            final Class<? extends AbstractAppSettingsPane> settingsPaneClass
                                                               = e.getValue()
                .settingsPane();
            final boolean singleton = e.getValue().singleton();

            if (singleton) {
                final AbstractAppSettingsPane settingsPane = createSettingsPane(
                    settingsPaneClass);
                managementPanel.add(settingsPane);
                settingsPanes.put(appTypeName, settingsPane);
            } else {
                final AbstractAppInstanceForm instanceForm = createInstanceForm(
                    String.format("%s_instance_form", appTypeName),
                    instanceFormClass);
                managementPanel.add(instanceForm);
                instanceForms.put(appTypeName, instanceForm);
                final AbstractAppSettingsPane settingsPane = createSettingsPane(
                    settingsPaneClass);
                managementPanel.add(settingsPane);
                settingsPanes.put(appTypeName, settingsPane);
            }

        });

        setBody(managementPanel);

    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(selectedAppTypeParam);
        page.addGlobalStateParam(selectedAppInstanceParam);

        page.setVisibleDefault(managementLinks, false);
        page.setVisibleDefault(placeholderInstances, false);
        page.setVisibleDefault(placeholderSingletonSettings, false);
        page.setVisibleDefault(appInfo, false);

        settingsPanes.forEach((k, v) -> {
            page.setVisibleDefault(v, false);
        });

        instanceForms.forEach((k, v) -> {
            page.setVisibleDefault(v, false);
        });

    }

    protected void showManagementLinks(final PageState state) {
        managementLinks.setVisible(state, true);
        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);
    }

    protected void hideManagementLinks(final PageState state) {
        managementLinks.setVisible(state, false);
        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);
    }

    protected void showInstances(final PageState state) {

        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);

        //placeholderInstances.setVisible(state, true);
        final String appType = selectedAppType.getSelectedKey(state);
        if (instanceForms.containsKey(appType)) {
            instanceForms.get(appType).setVisible(state, true);
        }
        placeholderSingletonSettings.setVisible(state, false);
        appInfo.setVisible(state, false);
    }

    protected void hideInstances(final PageState state) {
        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);
        placeholderInstances.setVisible(state, false);
    }

    protected void showSingletonAppSettings(final PageState state) {
        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);
        placeholderInstances.setVisible(state, false);
        //placeholderSingletonSettings.setVisible(state, true);

        final String appType = selectedAppType.getSelectedKey(state);
        if (settingsPanes.containsKey(appType)) {
            settingsPanes.get(appType).setVisible(state, true);
        }

        appInfo.setVisible(state, false);
    }

    protected void showAppSettings(final PageState state) {
        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);
        hideManagementLinks(state);
        placeholderInstances.setVisible(state, false);
        placeholderSingletonSettings.setVisible(state, false);

        if (settingsPanes.containsKey(selectedAppType.getSelectedKey(state))) {
            settingsPanes.get(selectedAppType.getSelectedKey(state)).setVisible(
                state, true);
        }
        
//        final String appId = selectedAppInstance.getSelectedKey(state);
//        final ApplicationRepository appRepo = CdiUtil.createCdiUtil().findBean(
//            ApplicationRepository.class);
//        final CcmApplication application = appRepo.findById(Long
//            .parseLong(appId));
//        if (application != null) {
//            final String appType = application.getApplicationType();
//            if (settingsPanes.containsKey(appType)) {
//                settingsPanes.get(appType).setVisible(state, true);
//            }
//        }
    }

    protected void hideSingletonAppSettings(final PageState state) {
        placeholderSingletonSettings.setVisible(state, false);
        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);
    }

    protected void showAppInfo(final PageState state) {
        placeholderInstances.setVisible(state, false);
        placeholderSingletonSettings.setVisible(state, false);
        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);
        appInfo.setVisible(state, true);
    }

    protected void hideAppInfo(final PageState state) {
        hideAllInstanceForms(state);
        hideAllSettingsPanes(state);
        appInfo.setVisible(state, false);
    }

    private void hideAllInstanceForms(final PageState state) {
        instanceForms.values().forEach(f -> {
            f.setVisible(state, false);
        });
    }

    private void hideAllSettingsPanes(final PageState state) {
        settingsPanes.values().forEach(p -> {
            p.setVisible(state, false);
        });
    }

    private AbstractAppInstanceForm createInstanceForm(
        final String name,
        final Class<? extends AbstractAppInstanceForm> instanceFormClass) {

        try {
            final Constructor<? extends AbstractAppInstanceForm> constructor
                                                                     = instanceFormClass
                .getConstructor(String.class,
                                ParameterSingleSelectionModel.class,
                                ParameterSingleSelectionModel.class);

            return constructor.newInstance(name,
                                           selectedAppType,
                                           selectedAppInstance);
        } catch (NoSuchMethodException |
                 SecurityException |
                 InstantiationException |
                 IllegalAccessException |
                 IllegalArgumentException |
                 InvocationTargetException ex) {
            throw new UncheckedWrapperException(ex);
        }

    }

    private AbstractAppSettingsPane createSettingsPane(
        Class<? extends AbstractAppSettingsPane> settingsPaneClass) {

        try {
            final Constructor<? extends AbstractAppSettingsPane> constructor
                                                                     = settingsPaneClass
                .getDeclaredConstructor(ParameterSingleSelectionModel.class,
                                        ParameterSingleSelectionModel.class);

            return constructor.newInstance(selectedAppType,
                                           selectedAppInstance);

        } catch (NoSuchMethodException |
                 SecurityException |
                 InstantiationException |
                 IllegalAccessException |
                 IllegalArgumentException |
                 InvocationTargetException ex) {
            throw new UncheckedWrapperException(ex);
        }
    }

    private class SingletonAppSettingsLink extends ActionLink {

        public SingletonAppSettingsLink(final GlobalizedMessage label) {
            super(label);
        }

        @Override
        public boolean isVisible(final PageState state) {
            if (super.isVisible(state)) {
                if (selectedAppType.getSelectedKey(state) == null) {
                    return false;
                } else {
                    final org.libreccm.web.ApplicationManager appManager
                                                                  = CdiUtil
                        .createCdiUtil().findBean(
                            org.libreccm.web.ApplicationManager.class);
                    final ApplicationType appType = appManager
                        .getApplicationTypes().get(selectedAppType
                            .getSelectedKey(state));

                    return appType.singleton();
                }
            } else {
                return false;
            }
        }

    }

    private class InstancesLink extends ActionLink {

        public InstancesLink(final GlobalizedMessage label) {
            super(label);
        }

        @Override
        public boolean isVisible(final PageState state) {
            if (super.isVisible(state)) {
                if (selectedAppType.getSelectedKey(state) == null) {
                    return false;
                } else {
                    final org.libreccm.web.ApplicationManager appManager
                                                                  = CdiUtil
                        .createCdiUtil().findBean(
                            org.libreccm.web.ApplicationManager.class);
                    final ApplicationType appType = appManager
                        .getApplicationTypes().get(selectedAppType
                            .getSelectedKey(state));

                    return !appType.singleton();
                }
            } else {
                return false;
            }
        }

    }

}
