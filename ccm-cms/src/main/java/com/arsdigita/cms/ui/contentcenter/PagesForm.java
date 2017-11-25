/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.contentcenter;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;

import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainRepository;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.librecms.CmsConstants;
import org.librecms.pages.Pages;
import org.librecms.pages.PagesRepository;

import java.util.List;
import java.util.TooManyListenersException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PagesForm extends Form {

    private final static String PRIMARY_URL_FIELD = "primaryUrl";
    private final static String SITE_SELECT = "site";
    private final static String CATEGORY_DOMAIN_SELECT = "categoryDomain";

    private final PagesPane pagesPane;
    private final ParameterSingleSelectionModel<String> selectedPages;

    private final TextField primaryUrlField;
    private final SingleSelect siteSelect;
    private final SingleSelect categoryDomainSelect;
    private final SaveCancelSection saveCancelSection;

    PagesForm(final PagesPane pagesPane,
              final ParameterSingleSelectionModel<String> selectedPages) {

        super("pagesForm");

        this.pagesPane = pagesPane;
        this.selectedPages = selectedPages;

        primaryUrlField = new TextField(PRIMARY_URL_FIELD);
        primaryUrlField.setLabel(new GlobalizedMessage(
            "cms.ui.pages.form.primary_url_field.label",
            CmsConstants.CMS_BUNDLE));
        super.add(primaryUrlField);

        siteSelect = new SingleSelect(SITE_SELECT);
        try {
            siteSelect.addPrintListener(this::populateSiteSelect);
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        siteSelect.setLabel(new GlobalizedMessage(
            "cms.ui.pages.form.site_select.label",
            CmsConstants.CMS_BUNDLE));
        super.add(siteSelect);

        categoryDomainSelect = new SingleSelect(CATEGORY_DOMAIN_SELECT);
        try {
            categoryDomainSelect
                .addPrintListener(this::populateCategoryDomainSelect);
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        categoryDomainSelect.setLabel(new GlobalizedMessage(
            "cms.ui.pages.form.category_domain_select.label",
            CmsConstants.CMS_BUNDLE));
        super.add(categoryDomainSelect);

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection);

        super.addInitListener(this::init);
        super.addValidationListener(this::validate);
        super.addProcessListener(this::process);
    }

    private void populateSiteSelect(final PrintEvent event) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final SiteRepository siteRepository = cdiUtil
            .findBean(SiteRepository.class);

        final List<Site> sites = siteRepository.findAll();

        final SingleSelect target = (SingleSelect) event.getTarget();
        target.clearOptions();

        for (final Site site : sites) {
            final Text label;
            if (site.isDefaultSite()) {
                label = new Text(String.format("%s *", site.getDomainOfSite()));
            } else {
                label = new Text(site.getDomainOfSite());
            }

            target.addOption(new Option(Long.toString(site.getObjectId()),
                                        label));
        }
        
        if (selectedPages.getSelectedKey(event.getPageState()) != null) {
            target.setDisabled();
        }
    }

    private void populateCategoryDomainSelect(final PrintEvent event) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final DomainRepository domainRepo = cdiUtil
            .findBean(DomainRepository.class);

        final List<Domain> categoryDomains = domainRepo.findAll();

        final SingleSelect target = (SingleSelect) event.getTarget();
        target.clearOptions();

        for (final Domain domain : categoryDomains) {

            target.addOption(new Option(Long.toString(domain.getObjectId()),
                                        new Text(domain.getDomainKey())));
        }
        
        if (selectedPages.getSelectedKey(event.getPageState()) != null) {
            target.setDisabled();
        }
    }

    private void init(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (selectedPages.getSelectedKey(state) != null) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PagesRepository pagesRepo = cdiUtil
                .findBean(PagesRepository.class);

            final Pages pages = pagesRepo
                .findById(Long.parseLong(selectedPages.getSelectedKey(state)))
                .orElseThrow(() -> new UnexpectedErrorException(String
                .format("No Pages with ID %s in the database.",
                        selectedPages.getSelectedKey(state))));

            primaryUrlField.setValue(state, pages.getPrimaryUrl());

            siteSelect.setValue(state,
                                Long.toString(pages.getSite().getObjectId()));

            categoryDomainSelect
                .setValue(state,
                          Long.toString(pages.getCategoryDomain().getObjectId()));
        }
    }

    private void validate(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final FormData data = event.getFormData();

            final String primaryUrl = data.getString(PRIMARY_URL_FIELD);
            if (primaryUrl == null
                    || primaryUrl.isEmpty()
                    || primaryUrl.matches("\\s*")) {

                data.addError(PRIMARY_URL_FIELD,
                              new GlobalizedMessage(
                                  "cms.ui.pages.form.primary_url_field.error",
                                  CmsConstants.CMS_BUNDLE));
            }

            final String selectedSite = data.getString(SITE_SELECT);
            if (selectedSite == null
                    || selectedSite.isEmpty()) {

                data.addError(PRIMARY_URL_FIELD,
                              new GlobalizedMessage(
                                  "cms.ui.pages.form.site_select.error",
                                  CmsConstants.CMS_BUNDLE));
            }

            final String selectedDomain = data.getString(CATEGORY_DOMAIN_SELECT);
            if (selectedDomain == null
                    || selectedDomain.isEmpty()) {

                data.addError(PRIMARY_URL_FIELD,
                              new GlobalizedMessage(
                                  "cms.ui.pages.form.category_domain_select.error",
                                  CmsConstants.CMS_BUNDLE));
            }
        }

    }

    private void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PagesPaneController controller = cdiUtil
                .findBean(PagesPaneController.class);

            final FormData data = event.getFormData();

            final String primaryUrl = data.getString(PRIMARY_URL_FIELD);
            final String selectedSiteId = data.getString(SITE_SELECT);
            final String selectedDomainId = data.getString(
                CATEGORY_DOMAIN_SELECT);

            if (selectedPages.getSelectedKey(state) == null) {
                controller.createPages(primaryUrl,
                                       Long.parseLong(selectedSiteId),
                                       Long.parseLong(selectedDomainId));
            } else {
                controller
                    .updatePages(
                        Long.parseLong(selectedPages.getSelectedKey(state)),
                        primaryUrl);
            }
        }

        pagesPane.showPagesTable(state);
    }

}
