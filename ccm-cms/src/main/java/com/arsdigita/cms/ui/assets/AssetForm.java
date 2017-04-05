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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetRepository;

import java.util.Optional;

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

    private TextField title;
    private SaveCancelSection saveCancelSection;

    public AssetForm(final AssetPane assetPane) {
        super("asset-form", new ColumnPanel(2));

        this.assetPane = assetPane;
        selectionModel = assetPane.getSelectedAssetModel();

        initComponents();
    }

    private void initComponents() {
        add(new Label(new GlobalizedMessage("cms.ui.asset.title",
                                            CmsConstants.CMS_BUNDLE)));
        title = new TextField(ASSET_TITLE);
        add(title);

        addWidgets();

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);
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
                .orElseThrow(() -> new IllegalArgumentException(String.format(
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
            title.setValue(state,
                           selectedAsset
                               .get()
                               .getTitle()
                               .getValue(KernelConfig
                                   .getConfig()
                                   .getDefaultLocale()));
        }

    }

    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();

        if (saveCancelSection.getSaveButton().isSelected(state)) {
            final Optional<Asset> selectedAsset = getSelectedAsset(state);
            final Asset asset;
            if (selectedAsset.isPresent()) {
                asset = selectedAsset.get();
                updateAsset(asset, state);
            } else {
                asset = createAsset(state);
            }

            asset.getTitle().addValue(
                KernelConfig.getConfig().getDefaultLocale(),
                (String) title.getValue(state));

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final AssetRepository assetRepo = cdiUtil
                .findBean(AssetRepository.class);
            assetRepo.save(asset);
        }
    }

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
