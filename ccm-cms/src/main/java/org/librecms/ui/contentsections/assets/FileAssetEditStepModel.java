/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections.assets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsFileAssetEditStepModel")
public class FileAssetEditStepModel {

    private Map<String, String> descriptionValues;

    private List<String> descriptionLocales;

    private String fileName;

    private String mimeType;

    private long size;
    
    private String sizeLabel;

    public Map<String, String> getDescriptionValues() {
        return Collections.unmodifiableMap(descriptionValues);
    }

    protected void setDescriptionValues(
        final Map<String, String> descriptionValues
    ) {
        this.descriptionValues = new HashMap<>(descriptionValues);
    }

    public List<String> getDescriptionLocales() {
        return Collections.unmodifiableList(descriptionLocales);
    }

    protected void setUnusedDescriptionLocales(
        final List<String> descriptionLocales
    ) {
        this.descriptionLocales = new ArrayList<>(descriptionLocales);
    }

    public String getFileName() {
        return fileName;
    }

    protected void setFileName(final String fileName) {
        this.fileName = fileName;
    }

    public String getMimeType() {
        return mimeType;
    }

    protected void setMimeType(final String mimeType) {
        this.mimeType = mimeType;
    }

    public long getSize() {
        return size;
    }

    protected void setSize(final long size) {
        this.size = size;
    }
    
    public String getSizeLabel() {
        return sizeLabel;
    }
    
    protected void setSizeLabel(final String sizeLabel) {
        this.sizeLabel = sizeLabel;
    }

}
