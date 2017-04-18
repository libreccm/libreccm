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
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.assets.AssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import javax.activation.MimeType;
import org.librecms.CmsConstants;
import org.librecms.assets.BinaryAsset;
import org.librecms.contentsection.Asset;

/**
 * Base form for assets which extend {@link BinaryAsset}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class BinaryAssetForm extends AssetForm {

    private TextArea description;
    private Text fileName;
    private Text mimeType;
    private Text size;
    private FileUploadSection fileUpload;

    public BinaryAssetForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected void addWidgets() {

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);

        panel.add(new Label(new GlobalizedMessage(
            "cms.ui.assets.binaryasset.description",
            CmsConstants.CMS_BUNDLE)));
        description = new TextArea("binaryasset-description");
        panel.add(description);

        panel.add(new Label(
            new GlobalizedMessage("cms.ui.assets.binaryasset.filename",
                                  CmsConstants.CMS_BUNDLE)));
        fileName = new Text();
        panel.add(fileName);

        panel.add(new Label(
            new GlobalizedMessage("cms.ui.assets.binaryasset.mimetype",
                                  CmsConstants.CMS_BUNDLE)));
        mimeType = new Text();
        panel.add(mimeType);

        panel.add(new Label(
            new GlobalizedMessage("cms.ui.assets.binaryasset.size",
                                  CmsConstants.CMS_BUNDLE)));
        size = new Text();
        panel.add(size);

        fileUpload = new FileUploadSection(
            new GlobalizedMessage("cms.ui.assets.binaryasset.mimetype",
                                  CmsConstants.CMS_BUNDLE),
            "",
            "");
        panel.add(fileUpload);

        add(panel);
    }

    @Override
    protected void initForm(final PageState state,
                            final Optional<Asset> selectedAsset) {

        if (selectedAsset.isPresent()) {

            if (!(selectedAsset.get() instanceof BinaryAsset)) {
                throw new IllegalArgumentException(String.format(
                    "The provided asset must be an instanceof of class '%s' or "
                        + "an subclass but is na instanceof class '%s'.",
                    BinaryAsset.class.getName(),
                    selectedAsset.get().getClass().getName()));
            }

            final BinaryAsset binaryAsset = (BinaryAsset) selectedAsset.get();

            description.setValue(state,
                                 binaryAsset
                                     .getDescription()
                                     .getValue(getSelectedLocale(state)));

            if (binaryAsset.getData() == null
                    || binaryAsset.getData().length == 0) {
                fileName.setText("-");
                mimeType.setText("-");
                size.setText("-");
            } else {
                
                fileName.setText(binaryAsset.getFileName());
                mimeType.setText(binaryAsset.getMimeType().toString());
                size.setText(Long.toString(binaryAsset.getSize()));
            }
        }

    }

    @Override
    protected void showLocale(final PageState state) {

        final Optional<Asset> selectedAsset = getSelectedAsset(state);

        if (selectedAsset.isPresent()) {
            if (!(getSelectedAsset(state).get() instanceof BinaryAsset)) {
                throw new IllegalArgumentException(
                    "Selected asset is not a binary asset.");
            }

            final BinaryAsset binaryAsset = (BinaryAsset) selectedAsset.get();

            description.setValue(state,
                                 binaryAsset
                                     .getDescription()
                                     .getValue(getSelectedLocale(state)));
        }

    }

    @Override
    protected Asset createAsset(final FormSectionEvent event)
        throws FormProcessException {

        Objects.requireNonNull(event);

        final PageState state = event.getPageState();

        final BinaryAsset binaryAsset = createBinaryAsset(state);

        binaryAsset
            .getDescription()
            .addValue(getSelectedLocale(state),
                      (String) description.getValue(state));

        setFileData(event, binaryAsset);

        return binaryAsset;
    }

    protected abstract BinaryAsset createBinaryAsset(final PageState state);

    @Override
    protected void updateAsset(final Asset asset,
                               final FormSectionEvent event)
        throws FormProcessException {

        Objects.requireNonNull(asset);
        Objects.requireNonNull(event);

        final PageState state = event.getPageState();

        if (!(asset instanceof BinaryAsset)) {
            throw new IllegalArgumentException(String.format(
                "Provided asset is not an instance of '%s' (or a sub class) "
                    + "but is an instance of class '%s'.",
                BinaryAsset.class.getName(),
                asset.getClass().getName()));
        }

        final BinaryAsset binaryAsset = (BinaryAsset) asset;

        binaryAsset
            .getDescription()
            .addValue(getSelectedLocale(state),
                      (String) description.getValue(state));

        setFileData(event, binaryAsset);
    }

    private void setFileData(final FormSectionEvent event,
                             final BinaryAsset binaryAsset)
        throws FormProcessException {

        final File file = fileUpload.getFile(event);
        final Path path = file.toPath();
        final byte[] data;
        try {
            data = Files.readAllBytes(path);
        } catch (IOException ex) {
            throw new FormProcessException(ex);
        }
        binaryAsset.setData(data);
        binaryAsset.setSize(data.length);

        binaryAsset.setMimeType(fileUpload.getMimeType(event));
    }

}
