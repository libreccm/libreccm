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

import org.libreccm.core.UnexpectedErrorException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TooManyListenersException;

/**
 * Basic Form for manipulating assets.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The type of the asset.
 */
public abstract class AbstractAssetForm<T extends Asset>
    extends Form implements FormInitListener,
                            FormProcessListener,
                            FormSubmissionListener {

    private static final String ASSET_TITLE = "asset-name";

    private static final String ASSET_NAME = "asset-title";

    private final AssetPane assetPane;

    private final SingleSelectionModel<Long> selectionModel;

    private BoxPanel showLocalePanel;

    private SingleSelect showLocaleSelect;

    private Submit showLocaleSubmit;

    private BoxPanel addLocalePanel;

    private SingleSelect addLocaleSelect;

    private Submit addLocaleSubmit;

    private TextField name;

    private TextField title;

    private SaveCancelSection saveCancelSection;

    public AbstractAssetForm(final AssetPane assetPane) {
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
                final Long selectedAssetId = getSelectedAssetId(state);
                final Label target = (Label) event.getTarget();
                if (selectedAssetId == null) {
                    target.setLabel(new GlobalizedMessage(
                        "cms.ui.asset.initial_locale",
                        CmsConstants.CMS_BUNDLE));
                } else {
                    target.setLabel(new GlobalizedMessage(
                        "cms.ui.asset.show_locale",
                        CmsConstants.CMS_BUNDLE));
                }
            }

        });
        showLocaleSelect = new SingleSelect("selected-locale");
        try {
            showLocaleSelect.addPrintListener(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final PageState state = event.getPageState();

                    final Long selectedAssetId = getSelectedAssetId(state);
                    if (selectedAssetId == null) {
                        final SingleSelect target = (SingleSelect) event
                            .getTarget();

                        target.clearOptions();

                        final List<String> langs = new ArrayList<>(
                            KernelConfig.getConfig().getSupportedLanguages());
                        langs.sort((lang1, lang2) -> lang1.compareTo(lang2));

                        langs.forEach(lang -> {
                            target.addOption(new Option(lang, new Text(lang)));
                        });
                    } else {
                        final SingleSelect target = (SingleSelect) event
                            .getTarget();

                        target.clearOptions();

                        final List<Locale> availableLocales = getController()
                            .availableLocales(selectedAssetId,
                                              getAssetClass());
                        availableLocales.sort((locale1, locale2) -> {
                            return locale1.toString().compareTo(locale2
                                .toString());
                        });
                        availableLocales.forEach(locale -> target.addOption(
                            new Option(locale.toString(),
                                       new Text(locale.toString()))));
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
                return getSelectedAssetId(state) != null;
            }

        };
        showLocalePanel.add(showLocaleLabel);
        showLocalePanel.add(showLocaleSelect);
        showLocalePanel.add(showLocaleSubmit);
        add(showLocalePanel);

        addLocalePanel = new BoxPanel(BoxPanel.HORIZONTAL) {

            @Override
            public boolean isVisible(final PageState state) {
                return getSelectedAssetId(state) != null;
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

                    final Long selectedAssetId = getSelectedAssetId(state);
                    if (selectedAssetId != null) {
                        final SingleSelect target = (SingleSelect) event
                            .getTarget();

                        target.clearOptions();

                        final List<Locale> creatableLocales = getController()
                            .creatableLocales(selectedAssetId,
                                              getAssetClass());
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

        add(new Label(new GlobalizedMessage("cms.ui.asset.name",
                                            CmsConstants.CMS_BUNDLE)));
        name = new TextField(ASSET_NAME);
        add(name);

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

    protected Long getSelectedAssetId(final PageState state) {

        final Object key = selectionModel.getSelectedKey(state);
        if (key == null) {
            return null;
        } else {
            return (Long) key;
        }
    }

    @Override
    public void init(final FormSectionEvent event) throws FormProcessException {

        final PageState state = event.getPageState();

        final Long selectedAssetId = getSelectedAssetId(state);

        final Map<String, Object> data;
        if (selectedAssetId == null) {
            showLocaleSelect.setValue(state,
                                      KernelConfig
                                          .getConfig()
                                          .getDefaultLocale()
                                          .toString());

            data = Collections.emptyMap();

        } else {

            showLocaleSelect.setValue(state,
                                      getSelectedLocale(state));

            data = getController().getAssetData(selectedAssetId,
                                                getAssetClass(),
                                                getSelectedLocale(state));

            name.setValue(state,
                          data.get(AbstractAssetFormController.DISPLAY_NAME));
            title.setValue(state,
                           data.get(AbstractAssetFormController.TITLE));
        }

        initForm(state, data);
    }

    protected Locale getSelectedLocale(final PageState state) {

        final Object selected = showLocaleSelect.getValue(state);
        if (selected == null) {
            return KernelConfig.getConfig().getDefaultLocale();
        } else if (selected instanceof Locale) {
            return (Locale) selected;
        } else if (selected instanceof String) {
            return new Locale((String) selected);
        } else {
            return new Locale(selected.toString());
        }
    }

    protected String getTitleValue(final PageState state) {
        return (String) title.getValue(state);
    }

    protected void initForm(final PageState state,
                            final Map<String, Object> data) {

        if (!data.isEmpty()) {

            name.setValue(state,
                          data.get(AbstractAssetFormController.DISPLAY_NAME));

//            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//            @SuppressWarnings("unchecked")
//            final AbstractAssetFormController<T> controller = cdiUtil
//                .findBean(AbstractAssetFormController.class);
            title.setValue(state,
                           data.get(AbstractAssetFormController.TITLE));
            showLocale(state);
        }
    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

//        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        if (showLocaleSubmit.isSelected(state)) {

            final Long selectedAssetId = getSelectedAssetId(state);

            initForm(state,
                     getController().getAssetData(selectedAssetId,
                                                  getAssetClass(),
                                                  getSelectedLocale(state)));

            return;
        }

        if (addLocaleSubmit.isSelected(state)) {
            final Locale add = new Locale((String) addLocaleSelect
                .getValue(state));
            final Long selectedAssetId = getSelectedAssetId(state);
            getController().addLocale(selectedAssetId, add, getAssetClass());
        }

        if (saveCancelSection.getSaveButton().isSelected(state)) {

            final Map<String, Object> data = new HashMap<>();
            data.put(AbstractAssetFormController.DISPLAY_NAME,
                     name.getValue(state));
            data.put(AbstractAssetFormController.TITLE,
                     title.getValue(state));
            data.putAll(collectData(event));

            final Long selectedAssetId;
            if (getSelectedAssetId(state) == null) {

                selectedAssetId = getController()
                    .createAsset(assetPane
                        .getFolderSelectionModel()
                        .getSelectedObject(state),
                                 getSelectedLocale(state),
                                 getAssetClass(),
                                 data);
            } else {
                selectedAssetId = getSelectedAssetId(state);
            }

            getController().updateAsset(selectedAssetId,
                                        getSelectedLocale(state),
                                        getAssetClass(),
                                        data);

            assetPane.browseMode(state);
        }
    }

    protected abstract Class<T> getAssetClass();

    protected abstract void showLocale(final PageState state);

    protected abstract Map<String, Object> collectData(
        final FormSectionEvent event)
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

    protected AssetFormController<T> getController() {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final AssetFormControllers controllers = cdiUtil
            .findBean(AssetFormControllers.class);

        return controllers.findController(getAssetClass());
    }

}
