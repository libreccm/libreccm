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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import org.apache.logging.log4j.util.Strings;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;

import java.util.Locale;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class DomainTitleForm extends Form {

    private static final String LOCALIZED_DOMAIN_TITLE = "title";

    private final CategoriesTab categoriesTab;

    private final TextField title;
    private final SaveCancelSection saveCancelSection;

    public DomainTitleForm(
        final CategoriesTab categoriesTab,
        final ParameterSingleSelectionModel<String> selectedDomainId,
        final ParameterSingleSelectionModel<String> selectedLanguage) {

        super("domainTitleForm", new BoxPanel(BoxPanel.VERTICAL));

        this.categoriesTab = categoriesTab;

        final ActionLink backLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.categories.domain.title.edit.back", ADMIN_BUNDLE));
        backLink.addActionListener(e -> {
            categoriesTab.hideDomainTitleForm(e.getPageState());
        });
        add(backLink);

        final Label heading = new Label(e -> {
            final PageState state = e.getPageState();

            final DomainRepository domainRepository = CdiUtil.createCdiUtil()
                .findBean(DomainRepository.class);
            final Domain selectedDomain = domainRepository.findById(
                Long.parseLong(selectedDomainId.getSelectedKey(state))).get();

            final Locale selectedLocale = new Locale(selectedLanguage
                .getSelectedKey(state));

            final Label target = (Label) e.getTarget();

            if (selectedDomain.getTitle().hasValue(selectedLocale)) {
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.categories.domain.title.edit_for_lang",
                    ADMIN_BUNDLE,
                    new String[]{selectedDomain.getDomainKey(),
                                 selectedLocale.toString()}));
            } else {
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.categories.domain.title.add_for_lang",
                    ADMIN_BUNDLE,
                    new String[]{selectedDomain.getDomainKey(),
                                 selectedLocale.toString()}));
            }
        });
        heading.setClassAttr("heading");
        add(heading);

        title = new TextField(LOCALIZED_DOMAIN_TITLE);
        title.setLabel(new GlobalizedMessage(
            "ui.admin.categories.domain.title.label", ADMIN_BUNDLE));
        add(title);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(e -> {
            final PageState state = e.getPageState();

            final DomainRepository domainRepository = CdiUtil.createCdiUtil()
                .findBean(DomainRepository.class);
            final Domain selectedDomain = domainRepository.findById(
                Long.parseLong(selectedDomainId.getSelectedKey(state))).get();

            final Locale selectedLocale = new Locale(selectedLanguage
                .getSelectedKey(state));

            if (selectedDomain.getTitle().hasValue(selectedLocale)) {
                title.setValue(state, selectedDomain.getTitle().getValue(
                               selectedLocale));
            }
        });

        addValidationListener(e -> {

            if (saveCancelSection.getSaveButton().isSelected(
                e.getPageState())) {
                final FormData data = e.getFormData();

                final String titleData = data.getString(LOCALIZED_DOMAIN_TITLE);

                if (Strings.isBlank(titleData)) {
                    data.addError(
                        LOCALIZED_DOMAIN_TITLE,
                        new GlobalizedMessage(
                            "ui.admin.categories.domain.title.error.not_blank",
                            ADMIN_BUNDLE));
                }
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {
                final DomainRepository domainRepository = CdiUtil
                    .createCdiUtil()
                    .findBean(DomainRepository.class);
                final Domain selectedDomain = domainRepository.findById(
                    Long.parseLong(selectedDomainId.getSelectedKey(state)))
                    .get();

                final Locale selectedLocale = new Locale(selectedLanguage
                    .getSelectedKey(state));

                final String titleData = e.getFormData().getString(
                    LOCALIZED_DOMAIN_TITLE);

                selectedDomain.getTitle().addValue(selectedLocale, titleData);
                domainRepository.save(selectedDomain);
            }

            categoriesTab.hideDomainTitleForm(state);
        });

    }

}
