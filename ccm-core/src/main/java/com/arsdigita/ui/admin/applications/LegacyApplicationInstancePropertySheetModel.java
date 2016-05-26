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
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.GlobalizationUtil;
import org.libreccm.web.CcmApplication;

/**
 * A {@link PropertySheetModel} implementation for displaying informations about an instance of an application
 * using a {@link PropertySheet}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: LegacyApplicationInstancePropertySheetModel.java 2923 2014-10-27 18:55:26Z jensp $
 */
public class LegacyApplicationInstancePropertySheetModel implements PropertySheetModel {

    private static final int INST_TITLE = 0;
    private static final int INST_PARENT = 1;
    private static final int INST_PATH = 2;
    private static final int INST_DESC = 3;
    private final CcmApplication application;
    private int currentIndex = -1;
    
    
    public LegacyApplicationInstancePropertySheetModel(final CcmApplication application) {
        this.application = application;
    }

    @Override
    public boolean nextRow() {
        currentIndex++;
        return currentIndex <= INST_DESC;
    }

    @Override
    public String getLabel() {
        switch (currentIndex) {
            case INST_TITLE:
                return (String) GlobalizationUtil.globalize("ui.admin.applications.ApplicationInstancePane.title.label").
                        localize();
            case INST_PARENT:
                return (String) GlobalizationUtil.globalize(
                        "ui.admin.applications.ApplicationInstancePane.parent_app.label").localize();
            case INST_PATH:
                return (String) GlobalizationUtil.globalize("ui.admin.applications.ApplicationInstancePane.path.label").
                        localize();
            case INST_DESC:
                return (String) GlobalizationUtil.globalize("ui.admin.applications.ApplicationInstancePane.desc.label").
                        localize();
            default:
                return "";
        }
    }

    @Override
    public GlobalizedMessage getGlobalizedLabel() {
        switch (currentIndex) {
            case INST_TITLE:
                return GlobalizationUtil.globalize("ui.admin.applications.ApplicationInstancePane.title.label");
            case INST_PARENT:
                return GlobalizationUtil.globalize(
                        "ui.admin.applications.ApplicationInstancePane.parent_app.label");
            case INST_PATH:                
                return GlobalizationUtil.globalize("ui.admin.applications.ApplicationInstancePane.path.label");
            case INST_DESC:
                return GlobalizationUtil.globalize("ui.admin.applications.ApplicationInstancePane.desc.label");
            default:
                return GlobalizationUtil.globalize("unknown");
        }
    }

    @Override
    public String getValue() {
        switch (currentIndex) {
            case INST_TITLE:
                return application.getTitle().getValue();
            case INST_PARENT:
                if (application.getParent() == null) {
                    return "";
                } else {
                    return application.getParent().getTitle().getValue();
                }
            case INST_PATH:
                return application.getPrimaryUrl();
            case INST_DESC:
                return application.getDescription().getValue();
            default:
                return "";
        }
    }

}
