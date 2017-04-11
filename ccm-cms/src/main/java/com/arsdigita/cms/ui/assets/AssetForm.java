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
package com.arsdigita.cms.ui.assets;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;

import java.util.Optional;

import org.libreccm.categorization.CategoryManager;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.assets.AssetL10NManager;
import org.librecms.contentsection.Folder;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.TooManyListenersException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AssetForm extends Form implements FormInitListener,
                                                        FormProcessListener,
                                                        FormSubmissionListener {

    private static final String ASSET_TITLE = "asset-title";

    private final AssetPane assetPane;
    private final SingleSelectionModel<Long> selectionModel;

    private BoxPanel showLocalePanel;
    private SingleSelect showLocaleSelect;
    private Submit showLocaleSubmit;

    private BoxPanel addLocalePanel;
    private SingleSelect addLocaleSelect;
    private Submit addLocaleSubmit;

    private TextField title;
    private SaveCancelSection saveCancelSection;

    public AssetForm(final AssetPane assetPane) {
        super("asset-form", new ColumnPanel(1));

        this.assetPane = assetPane;
        selectionModel = assetPane.getSelectedAssetModel();

        initComponents();
    }

    private void initComponents() {
        
        showLocalePanel = new BoxPanel(BoxPanel.HORIZONTAL);
        final Label showLocaleLabel = new Label(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Optional<Asset> selectedAsset = getSelectedAsset(state);
                final Label target = (Label) event.getTarget();
                if (selectedAsset.isPresent()) {
                    target.setLabel(new GlobalizedMessage(
                        "cms.ui.asset.show_locale",
                        CmsConstants.CMS_BUNDLE));
                } else {
                    target.setLabel(new GlobalizedMessage(
                        "cms.ui.asset.initial_locale",
                        CmsConstants.CMS_BUNDLE));
                }
            }

        }
        );
        showLocaleSelect = new SingleSelect("selected-locale");
        try {
            showLocaleSelect.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final PageState state = event.getPageState();

                    final Optional<Asset> selectedAsset
                                              = getSelectedAsset(state);
                    if (selectedAsset.isPresent()) {
                        final SingleSelect target = (SingleSelect) event
                            .getTarget();

                        target.clearOptions();;

                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final AssetL10NManager l10nManager = cdiUtil
                            .findBean(AssetL10NManager.class);
                        final List<Locale> availableLocales = new ArrayList<>(
                            l10nManager.availableLocales(selectedAsset.get()));
                        availableLocales.sort((locale1, locale2) -> {
                            return locale1
                                .toString()
                                .compareTo(locale2.toString());
                        });
                        availableLocales.forEach(locale -> target.addOption(
                            new Option(locale.toString(),
                                       new Text(locale.toString()))));
                    } else {
                        final SingleSelect target = (SingleSelect) event
                            .getTarget();

                        target.clearOptions();

                        final List<String> langs = new ArrayList<>(
                            KernelConfig.getConfig().getSupportedLanguages());
                        langs.sort((lang1, lang2) -> lang1.compareTo(lang2));

                        langs.forEach(lang -> {
                            target.addOption(new Option(lang, new Text(lang)));
                        });
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        showLocaleSubmit = new Submit(new GlobalizedMessage(
            "cms.ui.asset.show_locale",
            CmsConstants.CMS_BUNDLE)) {
                
                @Override
                public boolean isVisible(final PageState state) {
                    return getSelectedAsset(state).isPresent();
                }
            };
        showLocalePanel.add(showLocaleLabel);
        showLocalePanel.add(showLocaleSelect);
        showLocalePanel.add(showLocaleSubmit);
        add(showLocalePanel);

        addLocalePanel = new BoxPanel(BoxPanel.HORIZONTAL) {

            @Override
            public boolean isVisible(final PageState state) {
                return getSelectedAsset(state).isPresent();
            }

        };
        final Label addLocaleLabel = new Label(
            new GlobalizedMessage("cms.ui.asset.add_locale",
                                  CmsConstants.CMS_BUNDLE));
        addLocaleSelect = new SingleSelect("add-locale-select");
        try {
            addLocaleSelect.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final PageState state = event.getPageState();

                    final Optional<Asset> selectedAsset
                                              = getSelectedAsset(state);
                    if (selectedAsset.isPresent()) {
                        final SingleSelect target = (SingleSelect) event
                            .getTarget();

                        target.clearOptions();

                        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                        final AssetL10NManager l10nManager = cdiUtil
                            .findBean(AssetL10NManager.class);
                        final List<Locale> creatableLocales = new ArrayList<>(
                            l10nManager.creatableLocales(selectedAsset.get()));
                        creatableLocales.sort((locale1, locale2) -> {
                            return locale1
                                .toString()
                                .compareTo(locale2.toString());
                        });
                        creatableLocales.forEach(locale -> target.addOption(
                            new Option(locale.toString(),
                                       new Text(locale.toString()))));
                    }
                }

            });
        } catch (TooManyListenersException ex) {
            throw new UnexpectedErrorException(ex);
        }
        addLocaleSubmit = new Submit(new GlobalizedMessage(
            "cms.ui.asset.add_locale",
            CmsConstants.CMS_BUNDLE));
        addLocalePanel.add(addLocaleLabel);
        addLocalePanel.add(addLocaleSelect);
        addLocalePanel.add(addLocaleSubmit);
        add(addLocalePanel);

        add(new Label(new GlobalizedMessage("cms.ui.asset.title",
                                            CmsConstants.CMS_BUNDLE)));
        title = new TextField(ASSET_TITLE);
        add(title);

        addWidgets();

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);

        addInitListener(this);
        addProcessListener(this);
        addSubmissionListener(this);
    }

    protected void addWidgets() {
        //Nothing here
    }

    protected String getTitle(final PageState state) {
        return (String) title.getValue(state);
    }

    protected Optional<Asset> getSelectedAsset(final PageState state) {

        if (selectionModel.getSelectedKey(state) == null) {
            return Optional.empty();
        } else {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AssetRepository assetRepo = cdiUtil.findBean(
                AssetRepository.class);
            final Asset asset = assetRepo
                .findById(selectionModel.getSelectedKey(state))
                .orElseThrow(() -> new IllegalArgumentException(String.
                format(
                    "No asset with ID %d in the database.",
                    selectionModel.getSelectedKey(state))));
            return Optional.of(asset);
        }
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        final PageState state = event.getPageState();

        final Optional<Asset> selectedAsset = getSelectedAsset(state);

        if (selectedAsset.isPresent()) {

            showLocaleSelect.setValue(state,
                                      KernelConfig
                                          .getConfig()
                                          .getDefaultLocale()
                                          .toString());

            title.setValue(state,
                           selectedAsset
                               .get()
                               .getTitle()
                               .getValue(getSelectedLocale(state)));
        } else {
            showLocaleSelect.setValue(state,
                                      KernelConfig
                                          .getConfig()
                                          .getDefaultLocale()
                                          .toString());
        }

        initForm(state, selectedAsset);
    }

    protected Locale getSelectedLocale(final PageState state) {
        final String selected = (String) showLocaleSelect.getValue(state);
        if (selected == null) {
            return KernelConfig.getConfig().getDefaultLocale();
        } else {
            return new Locale(selected);
        }
    }

    protected abstract void initForm(final PageState state,
                                     final Optional<Asset> selectedAsset);

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();

        if (showLocaleSubmit.isSelected(state)) {

            final Optional<Asset> selectedAsset = getSelectedAsset(state);

            if (selectedAsset.isPresent()) {

                title.setValue(state,
                               selectedAsset
                                   .get()
                                   .getTitle()
                                   .getValue(getSelectedLocale(state)));
                showLocale(state);
            }
            return;
        }

        if (addLocaleSubmit.isSelected(state)) {
            final AssetL10NManager l10nManager = cdiUtil
                .findBean(AssetL10NManager.class);
            final Locale add = new Locale((String) addLocaleSelect
                .getValue(state));
            final Optional<Asset> selectedAsset = getSelectedAsset(state);
            l10nManager.addLanguage(selectedAsset.get(), add);
        }

        if (saveCancelSection.getSaveButton().isSelected(state)) {
            final Optional<Asset> selectedAsset = getSelectedAsset(state);
            final Asset asset;
            if (selectedAsset.isPresent()) {
                asset = selectedAsset.get();
                updateAsset(asset, state);
            } else {
                asset = createAsset(state);
            }

            asset.getTitle().addValue(getSelectedLocale(state),
                                      (String) title.getValue(state));

            final AssetRepository assetRepo = cdiUtil
                .findBean(AssetRepository.class);
            assetRepo.save(asset);

            if (!selectedAsset.isPresent()) {
                //Set display name
                asset.setDisplayName((String) title.getValue(state));
                assetRepo.save(asset);

                //Add new asset to currently selected folder
                final Folder selectedFolder = assetPane
                    .getFolderSelectionModel()
                    .getSelectedObject(state);
                final CategoryManager categoryManager = cdiUtil
                    .findBean(CategoryManager.class);
                categoryManager.addObjectToCategory(
                    asset,
                    selectedFolder,
                    CmsConstants.CATEGORIZATION_TYPE_FOLDER);
            }

            assetPane.browseMode(state);
        }
    }

    protected abstract void showLocale(final PageState state);

    protected abstract Asset createAsset(final PageState state)
        throws FormProcessException;

    protected abstract void updateAsset(final Asset asset,
                                        final PageState state)
        throws FormProcessException;

    @Override
    public void submitted(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getCancelButton().isSelected(state)) {
            selectionModel.clearSelection(state);
            assetPane.browseMode(state);
        }
    }

}
