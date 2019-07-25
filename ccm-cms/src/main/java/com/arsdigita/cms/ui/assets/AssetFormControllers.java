/*
 * Copyright (C) 2019 LibreCCM Foundation.
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

import org.librecms.contentsection.Asset;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetFormControllers {

    @Inject
    @Any
    private Instance<AssetFormController<?>> controllers;

    @SuppressWarnings("unchecked")
    public <T extends Asset> AssetFormController<T> findController(
        final Class<T> assetType) {

        final IsControllerForAssetTypeLiteral literal
                                                  = new IsControllerForAssetTypeLiteral(
                assetType);

        final Instance<AssetFormController<?>> instance = controllers
            .select(literal);

        if (instance.isUnsatisfied()) {
            throw new IllegalArgumentException(String.format(
                "No controller for asset type \"%s\".",
                assetType.getClass().getName()));
        } else {
            return (AssetFormController<T>) instance.iterator().next();
        }
    }

    private class IsControllerForAssetTypeLiteral
        extends AnnotationLiteral<IsControllerForAssetType>
        implements IsControllerForAssetType {

        private static final long serialVersionUID = 1L;

        private final Class<? extends Asset> assetType;

        public IsControllerForAssetTypeLiteral(
            final Class<? extends Asset> assetType) {

            this.assetType = assetType;
        }

        @Override
        public Class<? extends Asset> value() {

            return assetType;
        }

    }

}
