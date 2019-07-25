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
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.cms.ui.FileUploadSection;
import com.arsdigita.cms.ui.assets.AbstractAssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;
import com.arsdigita.globalization.GlobalizedMessage;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.librecms.CmsConstants;
import org.librecms.assets.BinaryAsset;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Base form for assets which extend {@link BinaryAsset}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> Type of binary asset
 */
public abstract class AbstractBinaryAssetForm<T extends BinaryAsset>
    extends AbstractAssetForm<T> {

    private TextArea description;

    private Text fileName;

    private Text mimeType;

    private Text size;

    private FileUploadSection fileUpload;

    public AbstractBinaryAssetForm(final AssetPane assetPane) {
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

        setEncType(CmsConstants.FORM_ENCTYPE_MULTIPART);
    }

    @Override
    protected void initForm(final PageState state,
                            final Map<String, Object> data) {

        super.initForm(state, data);

        if (!data.isEmpty()) {

            description.setValue(state,
                                 data.get("description"));

            if (data.containsKey("data")) {

                final byte[] binaryData = (byte[]) data.get("data");
                if (binaryData.length == 0) {
                    fileName.setText("-");
                    mimeType.setText("-");
                    size.setText("-");
                } else {
                    fileName.setText((String) data.get("fileName"));
                    mimeType.setText((String) data.get("mimeType"));
                    size.setText(Long.toString((long) data.get("size")));
                }

            } else {
                fileName.setText("-");
                mimeType.setText("-");
                size.setText("-");
            }
        }
    }

    @Override
    protected void showLocale(final PageState state) {

        final Long selectedAssetId = getSelectedAssetId(state);

        if (selectedAssetId != null) {

            final Map<String, Object> data = getController()
                .getAssetData(selectedAssetId,
                              getAssetClass(),
                              getSelectedLocale(state));

            description
                .setValue(
                    state,
                    data
                        .get(AbstractBinaryAssetFormController.DESCRIPTION));
        }
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event)
        throws FormProcessException {

        return getFileData(event);
    }

    private Map<String, Object> getFileData(final FormSectionEvent event)
        throws FormProcessException {

        final File file = fileUpload.getFile(event);
        if (file == null) {
            return Collections.emptyMap();
        } else {
            final Path path = file.toPath();
            final byte[] data;
            try {
                data = Files.readAllBytes(path);
            } catch (IOException ex) {
                throw new FormProcessException(ex);
            }

            final Map<String, Object> assetData = new HashMap<>();

            assetData.put(AbstractBinaryAssetFormController.DATA,
                          data);
            assetData.put(AbstractBinaryAssetFormController.FILE_NAME,
                          fileUpload.getFileName(event));
            assetData.put(AbstractBinaryAssetFormController.SIZE,
                          data.length);
            assetData.put(AbstractBinaryAssetFormController.MIME_TYPE,
                          fileUpload.getMimeType(event));

            return assetData;
        }
    }

}
