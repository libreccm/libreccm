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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;

import java.util.HashSet;
import java.util.Set;
import java.util.TooManyListenersException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;
import static org.bouncycastle.asn1.x500.style.RFC4519Style.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class DomainDetails extends SegmentedPanel {

    private final CategoriesTab categoriesTab;
    private final ParameterSingleSelectionModel<String> selectedDomainId;
    private final ParameterSingleSelectionModel<String> selectedLanguage;
    private final DomainTitleAddForm domainTitleAddForm;
    private final DomainDescriptionAddForm domainDescriptionAddForm;

    public DomainDetails(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedDomainId,
        final ParameterSingleSelectionModel<String> selectedLanguage) {

        this.categoriesTab = categoriesTab;
        this.selectedDomainId = selectedDomainId;
        this.selectedLanguage = selectedLanguage;

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.categories.domain_details.back", ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            categoriesTab.hideDomainDetails(state);
        });
        addSegment("domain-details-back", backLink);

        final BoxPanel propertiesPanel = new BoxPanel(BoxPanel.VERTICAL);
        propertiesPanel.add(new PropertySheet(
            new DomainPropertySheetModelBuilder(selectedDomainId)));
        final ActionLink editBasicPropertiesLink = new ActionLink(
            new GlobalizedMessage(
                "ui.admin.categories.domain_details.basic_properties.edit",
                ADMIN_BUNDLE));
        editBasicPropertiesLink.addActionListener(e -> {
            final PageState state = e.getPageState();
            categoriesTab.showDomainForm(state);
        });
        propertiesPanel.add(editBasicPropertiesLink);
        addSegment(
            new Label(new GlobalizedMessage(
                "ui.admin.categories.domain_details.basic_properties",
                ADMIN_BUNDLE)),
            propertiesPanel);

        final BoxPanel titlesPanel = new BoxPanel(BoxPanel.VERTICAL);
        titlesPanel.add(new DomainTitleTable(categoriesTab,
                                             selectedDomainId,
                                             selectedLanguage));
        domainTitleAddForm = new DomainTitleAddForm();
        titlesPanel.add(domainTitleAddForm);
        addSegment(
            new Label(new GlobalizedMessage(
                "ui.admin.categories.domain_details.domain_title",
                ADMIN_BUNDLE)),
            titlesPanel);

        final BoxPanel descPanel = new BoxPanel(BoxPanel.VERTICAL);
        descPanel.add(new DomainDescriptionTable(categoriesTab,
                                                 selectedDomainId,
                                                 selectedLanguage));
        domainDescriptionAddForm = new DomainDescriptionAddForm();
        descPanel.add(domainDescriptionAddForm);
        addSegment(
            new Label(new GlobalizedMessage(
                "ui.admin.categories.domain_details.description",
                ADMIN_BUNDLE)),
            descPanel);

        addSegment(
            new Label(new GlobalizedMessage(
                "ui.admin.categories.domain_details.mappings",
                ADMIN_BUNDLE)),
            new Text("domain_mappings_placeholder"));
    }

    private class DomainTitleAddForm extends Form {

        private static final String TITLE_SELECT_LANG = "titleSelectLang";

        public DomainTitleAddForm() {
            super("domainAddTitleLang", new BoxPanel(BoxPanel.HORIZONTAL));

            final SingleSelect titleSelectLang = new SingleSelect(
                TITLE_SELECT_LANG);
            titleSelectLang.setLabel(new GlobalizedMessage(
                "ui.admin.categories.domain_details.domain_title.add.label",
                ADMIN_BUNDLE));
            try {
                titleSelectLang.addPrintListener(e -> {
                    final PageState state = e.getPageState();

                    final DomainRepository domainRepository = CdiUtil
                        .createCdiUtil().findBean(DomainRepository.class);
                    final Domain domain = domainRepository.findById(Long
                        .parseLong(
                            selectedDomainId.getSelectedKey(state)));
                    final KernelConfig kernelConfig = KernelConfig.getConfig();
                    final Set<String> supportedLanguages = kernelConfig
                        .getSupportedLanguages();
                    final Set<String> assignedLanguages = new HashSet<>();
                    domain.getTitle().getAvailableLocales().forEach(l -> {
                        assignedLanguages.add(l.toString());
                    });

                    final SingleSelect target = (SingleSelect) e.getTarget();

                    target.clearOptions();

                    supportedLanguages.forEach(l -> {
                        if (!assignedLanguages.contains(l)) {
                            target.addOption(new Option(l, new Text(l)));
                        }
                    });
                });
            } catch (TooManyListenersException ex) {
                throw new UncheckedWrapperException(ex);
            }

            add(titleSelectLang);
            add(new Submit(new GlobalizedMessage(
                "ui.admin.categories.domain_details.domain_title.add.submit",
                ADMIN_BUNDLE)));

            addProcessListener(e -> {
                final PageState state = e.getPageState();
                final FormData data = e.getFormData();

                final String language = data.getString(TITLE_SELECT_LANG);
                selectedLanguage.setSelectedKey(state, language);

                categoriesTab.showDomainTitleForm(state);
            });
        }

        @Override
        public boolean isVisible(final PageState state) {
            if (super.isVisible(state)) {

                final DomainRepository domainRepository = CdiUtil
                    .createCdiUtil().findBean(DomainRepository.class);
                final Domain domain = domainRepository.findById(Long
                    .parseLong(
                        selectedDomainId.getSelectedKey(state)));
                final KernelConfig kernelConfig = KernelConfig.getConfig();
                final Set<String> supportedLanguages = kernelConfig
                    .getSupportedLanguages();
                final Set<String> assignedLanguages = new HashSet<>();
                domain.getTitle().getAvailableLocales().forEach(l -> {
                    assignedLanguages.add(l.toString());
                });

                //If all supported languages are assigned the form is not 
                //visible
                return !assignedLanguages.equals(supportedLanguages);

            } else {
                return false;
            }
        }

    }

    private class DomainDescriptionAddForm extends Form {

        private static final String DESC_SELECT_LANG = "descSelectLang";

        public DomainDescriptionAddForm() {
            super("domainAddDescLang", new BoxPanel(BoxPanel.HORIZONTAL));

            final SingleSelect descSelectLang = new SingleSelect(
                DESC_SELECT_LANG);
            descSelectLang.setLabel(new GlobalizedMessage(
                "ui.admin.categories.domain_details.domain_desc.add.label",
                ADMIN_BUNDLE));
            try {
                descSelectLang.addPrintListener(e -> {
                    final PageState state = e.getPageState();

                    final DomainRepository domainRepository = CdiUtil
                        .createCdiUtil().findBean(DomainRepository.class);
                    final Domain domain = domainRepository.findById(Long
                        .parseLong(
                            selectedDomainId.getSelectedKey(state)));
                    final KernelConfig kernelConfig = KernelConfig.getConfig();
                    final Set<String> supportedLanguages = kernelConfig
                        .getSupportedLanguages();
                    final Set<String> assignedLanguages = new HashSet<>();
                    domain.getDescription().getAvailableLocales().forEach(l -> {
                        assignedLanguages.add(l.toString());
                    });

                    final SingleSelect target = (SingleSelect) e.getTarget();

                    target.clearOptions();

                    supportedLanguages.forEach(l -> {
                        if (!assignedLanguages.contains(l)) {
                            target.addOption(new Option(l, new Text(l)));
                        }
                    });
                });
            } catch (TooManyListenersException ex) {
                throw new UncheckedWrapperException(ex);
            }

            add(descSelectLang);
            add(new Submit(new GlobalizedMessage(
                "ui.admin.categories.domain_details.domain_desc.add.submit",
                ADMIN_BUNDLE)));

            addProcessListener(e -> {
                final PageState state = e.getPageState();
                final FormData data = e.getFormData();

                final String language = data.getString(DESC_SELECT_LANG);
                selectedLanguage.setSelectedKey(state, language);

                categoriesTab.showDomainDescriptionForm(state);

            });
        }
        
        @Override
        public boolean isVisible(final PageState state) {
            if (super.isVisible(state)) {

                final DomainRepository domainRepository = CdiUtil
                    .createCdiUtil().findBean(DomainRepository.class);
                final Domain domain = domainRepository.findById(Long
                    .parseLong(
                        selectedDomainId.getSelectedKey(state)));
                final KernelConfig kernelConfig = KernelConfig.getConfig();
                final Set<String> supportedLanguages = kernelConfig
                    .getSupportedLanguages();
                final Set<String> assignedLanguages = new HashSet<>();
                domain.getDescription().getAvailableLocales().forEach(l -> {
                    assignedLanguages.add(l.toString());
                });

                //If all supported languages are assigned the form is not 
                //visible
                return !assignedLanguages.equals(supportedLanguages);

            } else {
                return false;
            }
        }
    }
}
