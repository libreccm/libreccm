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

import org.librecms.contentsection.Asset;
import org.librecms.contentsection.ContentSection;
import org.librecms.ui.contentsections.ContentSectionNotFoundException;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface MvcAssetEditStep {

    Class<? extends MvcAssetEditStep> getStepClass();

    ContentSection getContentSection() throws ContentSectionNotFoundException;

    Asset getAsset() throws ContentSectionNotFoundException,
                            AssetNotFoundException;

    String getAssetPath() throws ContentSectionNotFoundException,
                                 AssetNotFoundException;

    /**
     * Can the current user edit the asset. This method MUST only return
     *
     * @return {@code true} if the current user can edit the asset, {
     *
     * @false} otherwise.
     */
    boolean getCanEdit();

    /**
     * If an edit step alters the name of the asset and therefore the path of
     * the asset, the step MUST call this method to update the asset path used
     * by the step.
     *
     * @throws ContentSectionNotFoundException
     * @throws AssetNotFoundException
     */
    void updateAssetPath() throws ContentSectionNotFoundException,
                                  AssetNotFoundException;

    String getStepPath();

    String buildRedirectPathForStep() throws ContentSectionNotFoundException,
                                         AssetNotFoundException;

    String buildRedirectPathForStep(final String subPath)
        throws ContentSectionNotFoundException, AssetNotFoundException;

}
