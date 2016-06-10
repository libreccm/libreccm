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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SimpleContainer;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

import java.util.Optional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractAppSettingsPane extends SimpleContainer {

    private final ParameterSingleSelectionModel<String> selectedAppType;
    private final ParameterSingleSelectionModel<String> selectedAppInstance;

    public AbstractAppSettingsPane(
        final ParameterSingleSelectionModel<String> selectedAppType,
        final ParameterSingleSelectionModel<String> selectedAppInstance) {
        
        super();

        this.selectedAppType = selectedAppType;
        this.selectedAppInstance = selectedAppInstance;

        createWidgets();
    }

    protected abstract void createWidgets();

    protected ApplicationType getSelectedAppType(final PageState state) {
        final org.libreccm.web.ApplicationManager appManager = CdiUtil
            .createCdiUtil().findBean(org.libreccm.web.ApplicationManager.class);

        return appManager.getApplicationTypes().get(selectedAppType
            .getSelectedKey(state));
    }

    protected Optional<CcmApplication> getSelectedAppInstance(
        final PageState state) {

        if (selectedAppInstance.getSelectedKey(state) == null
                || selectedAppInstance.getSelectedKey(state).isEmpty()) {
            return Optional.empty();
        } else {
            final ApplicationRepository appRepo = CdiUtil.createCdiUtil()
                .findBean(ApplicationRepository.class);

            final CcmApplication result = appRepo.findById(Long.parseLong(
                selectedAppInstance.getSelectedKey(state)));
            return Optional.of(result);
        }
    }

}
