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
package com.arsdigita.ui.admin.categories;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainManager;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.CcmApplication;

import java.util.List;
import java.util.TooManyListenersException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class DomainMappingAddForm extends Form {

    private static final String DOMAIN_MAPPING_OWNER = "domainMappingOwner";

    public DomainMappingAddForm(
        final ParameterSingleSelectionModel<String> selectedDomainId) {

        super("domainMappingAddForm", new BoxPanel(BoxPanel.HORIZONTAL));
//        
//        final Label heading = new Label(new GlobalizedMessage(
//            "ui.admin.categories.domain_details.mappings.add", ADMIN_BUNDLE));
//        heading.setClassAttr("heading");
//        add(heading);

        final SingleSelect appSelect = new SingleSelect(DOMAIN_MAPPING_OWNER);
        appSelect.setLabel(new GlobalizedMessage(
            "ui.admin.categories.domain_details.mappings.add", ADMIN_BUNDLE));
        try {
            appSelect.addPrintListener(e -> {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final ApplicationRepository appRepository = cdiUtil.findBean(
                    ApplicationRepository.class);
                final DomainRepository domainRepository = cdiUtil.findBean(
                    DomainRepository.class);
                final DomainManager domainManager = cdiUtil.findBean(
                    DomainManager.class);
                final Domain domain = domainRepository.findById(
                    Long.parseLong(
                        selectedDomainId.getSelectedKey(e.getPageState())),
                    "Domain.withOwners");

                final List<CcmApplication> applications = appRepository
                    .findAll();

                final SingleSelect target = (SingleSelect) e.getTarget();
                target.clearOptions();

                target.addOption(new Option("0", new Text("")));

                applications.forEach(a -> {
                    if (!domainManager.isDomainOwner(a, domain)) {
                        target.addOption(new Option(Long.toString(a
                            .getObjectId()),
                                                    new Text(a.getPrimaryUrl())));
                    }
                });
            });
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }
        add(appSelect);

        add(new Submit(new GlobalizedMessage(
            "ui.admin.categories.domain_details.mappings.create", ADMIN_BUNDLE)));

        addValidationListener(e -> {
            final PageState state = e.getPageState();
            final FormData data = e.getFormData();

            final String appId = data.getString(DOMAIN_MAPPING_OWNER);
            if ("0".equals(appId)) {
                data.addError(
                    DOMAIN_MAPPING_OWNER,
                    new GlobalizedMessage(
                        "ui.admin.categories.doamin_details.mappings.error"
                            + ".please_select_app",
                        ADMIN_BUNDLE));
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();
            final FormData data = e.getFormData();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final DomainRepository domainRepository = cdiUtil.findBean(
                DomainRepository.class);
            final DomainManager domainManager = cdiUtil.findBean(
                DomainManager.class);
            final ApplicationRepository appRepository = cdiUtil.findBean(
                ApplicationRepository.class);

            final Domain domain = domainRepository.findById(
                Long.parseLong(selectedDomainId.getSelectedKey(state)),
                "Domain.withOwners");
            final CcmApplication application = appRepository.findById(
                Long.parseLong(data.getString(DOMAIN_MAPPING_OWNER)),
                "CcmApplication.withDomains");

            domainManager.addDomainOwner(application, domain);
        });
    }

}
