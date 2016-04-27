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

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.Date;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainManager;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class DomainForm extends Form {

    private static final String RELEASED = "released";
    private static final String VERSION = "version";
    private static final String DOMAIN_URI = "domainUri";
    private static final String DOMAIN_KEY = "domainKey";
    private static final String ROOT_CATEGORY_NAME = "rootCategoryName";

    private final ParameterSingleSelectionModel<String> selectedDomainId;

    private final TextField domainKey;
    private final TextField domainUri;
    private final TextField version;
    private final Date released;
    private final TextField rootCategoryName;
    private final SaveCancelSection saveCancelSection;

    public DomainForm(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedDomainId) {
        super("domainForm");

        this.selectedDomainId = selectedDomainId;

        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();
            final Label target = (Label) e.getTarget();
            if (selectedDomainId.getSelectedKey(state) == null) {
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.categories.domain_form.heading.create_new",
                    ADMIN_BUNDLE));
            } else {
                final DomainRepository domainRepository = CdiUtil.
                    createCdiUtil().findBean(DomainRepository.class);
                final Domain domain = domainRepository.findById(Long.parseLong(
                    selectedDomainId.getSelectedKey(state)));
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.categories.domain_form.heading.edit",
                    ADMIN_BUNDLE,
                    new String[]{domain.getDomainKey()}));
            }
        });
        heading.setClassAttr("heading");
        add(heading);

        domainKey = new TextField(DOMAIN_KEY);
        domainKey.setLabel(new GlobalizedMessage(
            "ui.admin.categories.domain_form.fields.domain_key",
            ADMIN_BUNDLE));
        add(domainKey);

        domainUri = new TextField(DOMAIN_URI);
        domainUri.setLabel(new GlobalizedMessage(
            "ui.admin.categories.domain_form.fields.domain_uri",
            ADMIN_BUNDLE));
        add(domainUri);

        version = new TextField(VERSION);
        version.setLabel(new GlobalizedMessage(
            "ui.admin.categories.domain_form.fields.version",
            ADMIN_BUNDLE));
        add(version);

        released = new Date(RELEASED);
        released.setLabel(new GlobalizedMessage(
            "ui.admin.categories.domain_form.fields.released",
            ADMIN_BUNDLE));
        add(released);

        rootCategoryName = new TextField(ROOT_CATEGORY_NAME);
        rootCategoryName.setLabel(new GlobalizedMessage(
            "ui.admin.categories.domain_form.fields.root_category_name",
            ADMIN_BUNDLE));
        add(rootCategoryName);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(e -> {
            final PageState state = e.getPageState();

            if (selectedDomainId.getSelectedKey(state) == null) {
                rootCategoryName.setVisible(state, true);
            } else {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final DomainRepository domainRepository = cdiUtil.findBean(
                    DomainRepository.class);
                final Domain domain = domainRepository.findById(Long.parseLong(
                    selectedDomainId.getSelectedKey(state)));

                domainKey.setValue(state, domain.getDomainKey());
                domainUri.setValue(state, domain.getUri());
                version.setValue(state, domain.getVersion());
                released.setValue(state, domain.getReleased());

                rootCategoryName.setVisible(state, false);
            }
        });

        addValidationListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final FormData data = e.getFormData();
                final String domainKeyData = data.getString(DOMAIN_KEY);
                final String versionData = data.getString(VERSION);

                if (Strings.isBlank(domainKeyData)) {
                    data.addError(
                        DOMAIN_KEY,
                        new GlobalizedMessage(
                            "ui.admin.categories.domain_form.errors.domain_key_blank",
                            ADMIN_BUNDLE));
                }

                if (Strings.isBlank(versionData)) {
                    data.addError(
                        DOMAIN_KEY,
                        new GlobalizedMessage(
                            "ui.admin.categories.domain_form.errors.version_blank",
                            ADMIN_BUNDLE));
                }
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final DomainRepository domainRepository = cdiUtil.findBean(
                    DomainRepository.class);
                final DomainManager domainManager = cdiUtil.findBean(
                    DomainManager.class);

                final FormData data = e.getFormData();
                final String domainKeyData = data.getString(DOMAIN_KEY);
                final String domainUriData;
                if (Strings.isBlank(data.getString(DOMAIN_URI))) {
                    domainUriData = null;
                } else {
                    domainUriData = data.getString(DOMAIN_URI);
                }
                final String versionData = data.getString(VERSION);
                final java.util.Date releasedData = (java.util.Date) data.get(
                    RELEASED);
                final String rootCategoryNameData = data.getString(
                    ROOT_CATEGORY_NAME);

                final Domain domain;
                if (selectedDomainId.getSelectedKey(state) == null) {
                    if (Strings.isBlank(rootCategoryNameData)) {
                        domain = domainManager.createDomain(domainKeyData,
                                                            domainKeyData);
                    } else {
                        domain = domainManager.createDomain(
                            domainKeyData, rootCategoryNameData);
                    }
                } else {
                    domain = domainRepository.findById(Long.parseLong(
                        selectedDomainId.getSelectedKey(state)));
                }
                domain.setDomainKey(domainKeyData);
                domain.setUri(domainUriData);
                domain.setVersion(versionData);
                domain.setReleased(releasedData);

                domainRepository.save(domain);
            }

            categoriesTab.hideDomainForm(state);
        });

    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(rootCategoryName, false);
    }

}
