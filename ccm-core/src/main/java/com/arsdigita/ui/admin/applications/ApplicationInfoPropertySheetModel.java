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

import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;

import java.util.List;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

/**
 * A property sheet model for displaying informations about an
 * {@link ApplicationType} using a {@link PropertySheet}.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: ApplicationInfoPropertySheetModel.java 2220 2013-06-19
 * 15:26:58Z jensp $
 */
public class ApplicationInfoPropertySheetModel implements PropertySheetModel {

    private static final int APP_TITLE = 0;
    private static final int APP_CLASS = 1;
    private static final int APP_SINGLETON = 2;
    private static final int APP_DESC = 3;
    private static final int SINGLETON_PATH = 4;
    private final ApplicationType applicationType;
    private int currentIndex = -1;

    public ApplicationInfoPropertySheetModel(
        final ApplicationType applicationType) {
        this.applicationType = applicationType;
    }

    @Override
    public boolean nextRow() {
        if (applicationType.singleton() && currentIndex < SINGLETON_PATH) {
            currentIndex++;
            return true;
        } else if (!applicationType.singleton() && currentIndex < APP_DESC) {
            currentIndex++;
            return true;
        } else {
            currentIndex = -1;
            return false;
        }
    }

    public String getLabel() {
        switch (currentIndex) {
            case APP_TITLE:
                return (String) GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.title.label").
                    localize();
            case APP_CLASS:
                return (String) GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.app_class.label")
                    .localize();
            case APP_SINGLETON:
                return (String) GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.singleton.label")
                    .localize();
            case APP_DESC:
                return (String) GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.desc.label").
                    localize();
            case SINGLETON_PATH:
                return (String) GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.singleton_instance.path.label")
                    .localize();
            default:
                return "unknown";
        }
    }

    public GlobalizedMessage getGlobalizedLabel() {
        switch (currentIndex) {
            case APP_TITLE:
                return GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.title.label");
            case APP_CLASS:
                return GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.app_class.label");
            case APP_SINGLETON:
                return GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.singleton.label");
            case APP_DESC:
                return GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.desc.label");
            case SINGLETON_PATH:
                return GlobalizationUtil.globalize(
                    "ui.admin.applications.ApplicationInfoSection.singleton_instance.path.label");
            default:
                return GlobalizationUtil.globalize("unknown");
        }
    }

    @Override
    public String getValue() {
        switch (currentIndex) {
            case APP_TITLE:
                return applicationType.name();
            case APP_CLASS:
                return applicationType.name();
            case APP_SINGLETON:
                if (applicationType.singleton()) {
                    return (String) GlobalizationUtil.globalize(
                        "ui.admin.applications.ApplicationInfoSection.singleton.yes")
                        .localize();
                } else {
                    return (String) GlobalizationUtil.globalize(
                        "ui.admin.applications.ApplicationInfoSection.singleton.no")
                        .localize();
                }
            case APP_DESC:
                return applicationType.description();
            case SINGLETON_PATH:
                final String path;
                final CdiUtil cdiUtil = new CdiUtil();
                final ApplicationRepository appRepo = cdiUtil.findBean(
                    ApplicationRepository.class);
                final List<CcmApplication> instances
                                               = appRepo.findByType(
                        applicationType.name());
                if (instances.isEmpty()) {
                    path = "";
                } else {
                    path = instances.get(0).getPrimaryUrl();
                }

                return path;
            default:
                return "";
        }
    }

}
