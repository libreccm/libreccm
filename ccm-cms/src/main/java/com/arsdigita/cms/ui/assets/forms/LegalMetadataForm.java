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
package com.arsdigita.cms.ui.assets.forms;

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.cms.ui.assets.AssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;

import org.librecms.CmsConstants;
import org.librecms.assets.LegalMetadata;
import org.librecms.contentsection.Asset;

import java.util.Objects;
import java.util.Optional;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LegalMetadataForm extends AssetForm {

    private TextArea rightsHolder;
    private TextArea rights;
    private TextArea publisher;
    private TextArea creator;

    public LegalMetadataForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        
        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.legalmetadata.rightsholder",
            CmsConstants.CMS_BUNDLE)));
        rightsHolder = new TextArea("legalmetadata-rightsholder");
        panel.add(rightsHolder);

        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.legalmetadata.rights",
            CmsConstants.CMS_BUNDLE)));
        rights = new TextArea("legalmetadata-rights");
        panel.add(rights);

        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.legalmetadata.publisher",
            CmsConstants.CMS_BUNDLE)));
        publisher = new TextArea("legalmetadata-rights");
        panel.add(publisher);

        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.legalmetadata.creator",
            CmsConstants.CMS_BUNDLE)));
        creator = new TextArea("legalmetadata-creator");
        panel.add(creator);
        
        add(panel);
    }

    @Override
    protected void initForm(final PageState state,
                            final Optional<Asset> selectedAsset) {

        if (selectedAsset.isPresent()) {

            if (!(selectedAsset.get() instanceof LegalMetadata)) {
                throw new IllegalArgumentException(String.format(
                    "The provided asset must be an instanceof of class '%s' or "
                        + "an subclass but is an instanceof of class '%s'.",
                    LegalMetadata.class.getName(),
                    selectedAsset.get().getClass().getName()));
            }

            final LegalMetadata legalMetadata = (LegalMetadata) selectedAsset
                .get();

            rightsHolder.setValue(state, legalMetadata.getRightsHolder());
            rights.setValue(state,
                            legalMetadata
                                .getRights()
                                .getValue(getSelectedLocale(state)));
            publisher.setValue(state, legalMetadata.getPublisher());
            creator.setValue(state, legalMetadata.getCreator());
        }
    }

    @Override
    protected void showLocale(final PageState state) {
        final Optional<Asset> selectedAsset = getSelectedAsset(state);

        if (selectedAsset.isPresent()) {
            if (!(getSelectedAsset(state).get() instanceof LegalMetadata)) {
                throw new IllegalArgumentException(
                    "Selected asset is not a legal metadata");
            }

            final LegalMetadata legalMetadata = (LegalMetadata) selectedAsset.get();

            rights.setValue(state,
                            legalMetadata
                                .getRights()
                                .getValue(getSelectedLocale(state)));
        }
    }

    @Override
    protected Asset createAsset(final PageState state)
        throws FormProcessException {

        Objects.requireNonNull(state);

        final LegalMetadata legalMetadata = new LegalMetadata();

        legalMetadata.setRightsHolder((String) rightsHolder.getValue(state));
        legalMetadata.getRights().addValue(getSelectedLocale(state),
                                           (String) rights.getValue(state));

        legalMetadata.setPublisher((String) publisher.getValue(state));
        legalMetadata.setCreator((String) creator.getValue(state));

        return legalMetadata;
    }

    @Override
    protected void updateAsset(final Asset asset, final PageState state)
        throws FormProcessException {

        Objects.requireNonNull(asset);
        Objects.requireNonNull(state);

        if (!(asset instanceof LegalMetadata)) {
            throw new IllegalArgumentException(String.format(
                "Provided asset is not an instance of '%s' (or a sub class) "
                    + "but is an instance of class '%s'.",
                LegalMetadata.class
                    .getName(),
                asset.getClass().getName()));
        }

        final LegalMetadata legalMetadata = (LegalMetadata) asset;

        legalMetadata.setRightsHolder((String) rightsHolder.getValue(state));
        legalMetadata.getRights().addValue(getSelectedLocale(state),
                                           (String) rights.getValue(state));

        legalMetadata.setPublisher((String) publisher.getValue(state));
        legalMetadata.setCreator((String) creator.getValue(state));
    }

}
