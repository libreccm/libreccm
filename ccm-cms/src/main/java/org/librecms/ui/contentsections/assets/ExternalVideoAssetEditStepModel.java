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

import org.librecms.assets.LegalMetadata;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsExternalVideoAssetEditStepModel")
public class ExternalVideoAssetEditStepModel {

    private String baseUrl;

    private LegalMetadata legalMetadata;

    public LegalMetadata getLegalMetadata() {
        return legalMetadata;
    }

    protected void setLegalMetadata(final LegalMetadata legalMetadata) {
        this.legalMetadata = legalMetadata;
    }

    public String getLegalMetadataType() {
        return LegalMetadata.class.getName();
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    protected void setBaseUrl(final String baseUrl) {
        this.baseUrl = baseUrl;
    }

}
