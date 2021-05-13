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
import org.librecms.contentsection.Folder;

import java.util.Map;

/**
 * A create step for an asset. Implmenting classes MUST be CDI beans (request
 * scope is recommended). They are are retrieved by the {@link AssetController}
 * using CDI. The {@link AssetController} will first call
 * {@link #setContentSection(org.librecms.contentsection.ContentSection)} and {@link #setFolder(org.librecms.contentsection.Folder)
 * } to provided the current current content section and folder. After that,
 * dpending on the request method, either {@link #showCreateStep} or {@link #createAsset(java.util.Map)
 * } will be called.
 *
 * In most cases, {@link AbstractMvcAssetCreateStep} should be used as base for
 * implementations. {@link AbstractMvcAssetCreateStep} implements several common
 * operations.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> The asset type created by the create step.
 */
public interface MvcAssetCreateStep<T extends Asset> {

    /**
     * Return the template for the create step.
     *
     * @return
     */
    String showCreateStep();

    String createAsset(Map<String, String[]> formParams);

    /**
     * Should be set by the implementing class to indicate if the current user
     * can create document in the current folder.
     *
     * @return
     */
    boolean getCanCreate();

    /**
     * The asset type generated by the create step described by an instance of
     * this class.
     *
     * @return Asset type generated.
     */
    String getAssetType();

    /**
     * Localized description of the create step. The current locale as returned
     * by {@link GlobalizationHelper#getNegotiatedLocale()} should be used to
     * select the language variant to return.
     *
     * @return The localized description of the create step.
     */
    String getDescription();

    /**
     * Returns {@link ResourceBundle} providing the localized description of the
     * create step.
     *
     * @return The {@link ResourceBundle} providing the localized description of
     *         the create step.
     */
    String getBundle();

    /**
     * The locales that can be used for documents.
     *
     * @return The locales that can be used for documents.
     */
    Map<String, String> getAvailableLocales();

    /**
     * The current content section.
     *
     * @return The current content section.
     */
    ContentSection getContentSection();

    /**
     * Convinient method for getting the label of the current content section.
     *
     * @return The label of the current content section.
     */
    String getContentSectionLabel();

    /**
     * Convinient method for getting the title of the current content section.
     *
     * @return The title of the current content section for the current locale.
     */
    String getContentSectionTitle();

    /**
     * The current content section is provided by the
     * {@link DocumentController}.
     *
     * @param section The current content section.
     */
    void setContentSection(final ContentSection section);

    /**
     * The parent folder of the new asset.
     *
     * @return The parent folder of the new asset.
     */
    Folder getFolder();

    /**
     * Gets the path the the parent folder of the new asset.
     *
     * @return The path of the parent folder of the new asset.
     */
    String getFolderPath();

    /**
     * The parent folder of the new asset is provided by the
     * {@link DocumentController}.
     *
     * @param folder The parent folder of the new doucment.
     */
    void setFolder(final Folder folder);

    /**
     * Gets messages from the create step.
     *
     * @return
     */
    Map<String, String> getMessages();

}
