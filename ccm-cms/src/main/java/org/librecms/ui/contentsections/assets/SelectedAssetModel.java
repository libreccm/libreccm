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

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.ui.contentsections.FolderBreadcrumbsModel;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

/**
 * Model/named bean providing data about the currently selected asset for
 * several views.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsSelectedAssetModel")
public class SelectedAssetModel {

    /**
     * Checks if edit step classes have all required annotations.
     */
    @Inject
    private AssetEditStepsValidator stepsValidator;

    @Inject
    private AssetManager assetManager;

    @Inject
    private FolderManager folderManager;

    /**
     * Used to retrieve some localized data.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private HttpServletRequest request;

    /**
     * Used to check permissions
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Used to get the current user.
     */
    @Inject
    private Shiro shiro;

    /**
     * The current asset.
     */
    private Asset asset;

    /**
     * The name of the current asset.
     */
    private String assetName;

    /**
     * The title of the current asset. This value is determined from
     * {@link Asset#title} using {@link GlobalizationHelper#getValueFromLocalizedString(org.libreccm.l10n.LocalizedString)
     * }.
     */
    private String assetTitle;

    /**
     * The path of the current asset.
     */
    private String assetPath;

    /**
     * The breadcrumb trail of the folder of the current item.
     */
    private List<FolderBreadcrumbsModel> parentFolderBreadcrumbs;

    public String getAssetName() {
        return assetName;
    }

    public String getAssetTitle() {
        return assetTitle;
    }

    public String getAssetPath() {
        return assetPath;
    }

    public List<FolderBreadcrumbsModel> getParentFolderBreadcrumbs() {
        return Collections.unmodifiableList(parentFolderBreadcrumbs);
    }

    /**
     * Sets the current asset and sets the properties of this model based on the
     * asset.
     *
     * @param asset
     */
    void setAsset(final Asset asset) {
        this.asset = Objects.requireNonNull(asset);
        assetName = asset.getDisplayName();
        assetTitle = globalizationHelper.getValueFromLocalizedString(
            asset.getTitle()
        );
        assetPath = assetManager.getAssetPath(asset).substring(1); // Without leasding slash.
        parentFolderBreadcrumbs = assetManager
            .getAssetFolders(asset)
            .stream()
            .map(this::buildFolderBreadcrumbsModel)
            .collect(Collectors.toList());
    }

    /**
     * Helper method for building the breadcrumb trail for the folder of the
     * current item.
     *
     * @param folder The folder of the current item.
     *
     * @return The breadcrumb trail of the folder.
     */
    private FolderBreadcrumbsModel buildFolderBreadcrumbsModel(
        final Folder folder
    ) {
        final FolderBreadcrumbsModel model = new FolderBreadcrumbsModel();
        model.setCurrentFolder(false);
        model.setPath(folderManager.getFolderPath(folder));
        model.setPathToken(folder.getName());
        return model;
    }

}
