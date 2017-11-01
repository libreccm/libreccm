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
package com.arsdigita.ui.admin.sites;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.sites.Site;
import org.libreccm.sites.SiteRepository;
import org.libreccm.theming.ThemeInfo;
import org.libreccm.theming.Themes;

import java.util.List;
import java.util.TooManyListenersException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SitesForm extends Form {

    private static final String DOMAIN_OF_SITE = "domainOfSite";
    private static final String DEFAULT_SITE = "defaultSite";
    private static final String THEME_SELECT = "themeSelect";

    private final SitesTab sitesTab;
    private final ParameterSingleSelectionModel<String> selectedSiteId;

    private final TextField domainOfSiteField;
    private final CheckboxGroup defaultSiteCheckbox;
    private final SingleSelect defaultThemeSelect;
    private final SaveCancelSection saveCancelSection;

    public SitesForm(
        final SitesTab sitesTab,
        final ParameterSingleSelectionModel<String> selectedSiteId) {

        super("sitesform");

        this.sitesTab = sitesTab;
        this.selectedSiteId = selectedSiteId;

        final Label heading = new Label(event -> {

            final PageState state = event.getPageState();
            final Label target = (Label) event.getTarget();

            final String selectedSiteIdStr = selectedSiteId
                .getSelectedKey(state);
            if (selectedSiteIdStr == null || selectedSiteIdStr.isEmpty()) {
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.sites.create_new",
                    ADMIN_BUNDLE));
            } else {
                target.setLabel(new GlobalizedMessage(
                    "ui.admin.sites.edit",
                    ADMIN_BUNDLE));
            }
        });
        heading.setClassAttr("heading");
        super.add(heading);

        domainOfSiteField = new TextField(DOMAIN_OF_SITE);
        domainOfSiteField.setLabel(new GlobalizedMessage(
            "ui.admin.sites.domain_of_site",
            ADMIN_BUNDLE));
        super.add(domainOfSiteField);

        defaultSiteCheckbox = new CheckboxGroup(DEFAULT_SITE);
        defaultSiteCheckbox
            .addOption(new Option("isDefault",
                                  new Label(new GlobalizedMessage(
                                      "ui.admin.sites.is_default_site",
                                      ADMIN_BUNDLE))));
        super.add(defaultSiteCheckbox);

        defaultThemeSelect = new SingleSelect(THEME_SELECT);
        try {
            defaultThemeSelect.addPrintListener(event -> {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final Themes themes = cdiUtil.findBean(Themes.class);

                final SingleSelect target = (SingleSelect) event.getTarget();
                target.clearOptions();

                final List<ThemeInfo> availableThemes = themes
                    .getAvailableThemes();
                for (final ThemeInfo info : availableThemes) {
                    target.addOption(new Option(info.getName(),
                                                new Text(info.getName())));
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        super.add(defaultThemeSelect);

        saveCancelSection = new SaveCancelSection();
        super.add(saveCancelSection);

        super.addValidationListener(new ValidationListener());
        super.addInitListener(new InitListener());
        super.addProcessListener(new ProcessListener());
    }

    private class ValidationListener implements FormValidationListener {

        @Override
        public void validate(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {

                final FormData data = event.getFormData();

                final String domainOfSite = data.getString(DOMAIN_OF_SITE);

                if (domainOfSite == null
                        || domainOfSite.isEmpty()
                        || domainOfSite.matches("\\s*")) {

                    data.addError(
                        DOMAIN_OF_SITE,
                        new GlobalizedMessage(
                            "ui.admin.sites.domain_of_site.error.empty",
                            ADMIN_BUNDLE));
                }

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final SitesController controller = cdiUtil
                    .findBean(SitesController.class);
                if (!controller.isUnique(domainOfSite)) {
                    data.addError(
                        DOMAIN_OF_SITE,
                        new GlobalizedMessage(
                            "ui.admin.sites.domain_of_site.error.not_unique",
                            ADMIN_BUNDLE));
                }
            }
        }

    }

    private class InitListener implements FormInitListener {

        @Override
        public void init(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            final String selectedSiteIdStr = selectedSiteId
                .getSelectedKey(state);

            if (selectedSiteIdStr != null && !selectedSiteIdStr.isEmpty()) {

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final SiteRepository siteRepo = cdiUtil
                    .findBean(SiteRepository.class);

                final Site site = siteRepo.findById(Long
                    .parseLong(selectedSiteIdStr))
                    .orElseThrow(() -> new IllegalArgumentException(String
                    .format("No Site with ID %s in the database.",
                            selectedSiteIdStr)));

                domainOfSiteField.setValue(state, site.getDomainOfSite());
                defaultSiteCheckbox
                    .setValue(state, new Boolean[]{site.isDefaultSite()});
                defaultThemeSelect.setValue(state, site.getDefaultTheme());
            }
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            if (saveCancelSection.getSaveButton().isSelected(state)) {

                final FormData data = event.getFormData();

                final String domainOfSite = data.getString(DOMAIN_OF_SITE);
                final Boolean[] defaultSite = ((Boolean[]) data
                                               .get(DEFAULT_SITE));
                final String defaultTheme = data.getString(THEME_SELECT);

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final SiteRepository siteRepo = cdiUtil
                    .findBean(SiteRepository.class);

                final String selectedSiteIdStr = selectedSiteId
                    .getSelectedKey(state);

                final Site site;
                if (selectedSiteIdStr == null || selectedSiteIdStr.isEmpty()) {
                    site = new Site();
                    site.setDomainOfSite(domainOfSite);
                    if (defaultSite == null || defaultSite.length == 0) {
                        site.setDefaultSite(false);
                    } else {
                        site.setDefaultSite(defaultSite[0]);
                    }
                    site.setDefaultTheme(defaultTheme);
                } else {
                    site = siteRepo
                        .findById(Long.parseLong(selectedSiteIdStr))
                        .orElseThrow(() -> new IllegalArgumentException(String
                        .format("No Site with ID %s in in the database.",
                                selectedSiteIdStr)));
                }
                siteRepo.save(site);
            }

            sitesTab.hideSiteForm(state);
        }

    }

}
