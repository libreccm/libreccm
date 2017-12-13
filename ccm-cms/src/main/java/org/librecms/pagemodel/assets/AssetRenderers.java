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
package org.librecms.pagemodel.assets;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.contentsection.Asset;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Any;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 * Provides access to all available implementations of
 * {@link AbstractAssetRenderer}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class AssetRenderers {

    private static final Logger LOGGER = LogManager
        .getLogger(AssetRenderers.class);

    @Inject
    @Any
    private Instance<AbstractAssetRenderer> renderers;

    /**
     * Tries to find an implementation of {@link AbstractAssetRenderer} for the
     * provided asset type. If no renderer is found for the provided
     * {@code assetType} an noop implementation of {@link AbstractAssetRenderer}
     * is returned. This means that only the common properties of the asset are
     * rendered.
     *
     * @param assetType The asset type.
     *
     * @return An renderer for the provided asset type.
     */
    public AbstractAssetRenderer findRenderer(
        final Class<? extends Asset> assetType) {

        LOGGER.debug("Trying to find renderer for asset type \"{}\"...",
                     assetType.getName());

        final AssetRendererLiteral literal = new AssetRendererLiteral(assetType);

        final Instance<AbstractAssetRenderer> instance = renderers
            .select(literal);

        if (instance.isUnsatisfied()) {

            LOGGER.warn("No renderer for asset type \"{}\". "
                            + "Returning default renderer.",
                        assetType.getName());

            return new AbstractAssetRenderer() {

                @Override
                protected void renderAsset(final Asset asset,
                                           final Locale language,
                                           final Map<String, Object> result) {

                    //Nothing here.
                }

            };
        } else {
            return instance.iterator().next();
        }
    }

    private class AssetRendererLiteral
        extends AnnotationLiteral<AssetRenderer>
        implements AssetRenderer {

        private static final long serialVersionUID = 2635180159989399554L;

        private final Class<? extends Asset> renders;

        public AssetRendererLiteral(final Class<? extends Asset> renders) {
            this.renders = renders;
        }

        @Override
        public Class<? extends Asset> renders() {
            return renders;
        }

    }

}
