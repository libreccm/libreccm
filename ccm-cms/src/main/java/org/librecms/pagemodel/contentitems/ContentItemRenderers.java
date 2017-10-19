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
package org.librecms.pagemodel.contentitems;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.librecms.contentsection.ContentItem;

import java.util.Locale;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ContentItemRenderers {

    private static final Logger LOGGER = LogManager
        .getLogger(ContentItemRenderers.class);

    @Inject
    private Instance<AbstractContentItemRenderer> renderers;

    public AbstractContentItemRenderer findRenderer(
        final Class<? extends ContentItem> itemType) {

        LOGGER.debug("Trying to find default renderer for item type \"{}\"...",
                     itemType.getName());
        return findRenderer(itemType, "--DEFAULT--");
    }

    public AbstractContentItemRenderer findRenderer(
        final Class<? extends ContentItem> itemType, final String mode) {

        LOGGER.debug("Trying to find default renderer for item type \"{}\""
                         + "and mode \"{}\"...",
                     itemType.getName(),
                     mode);

        final ContentItemRendererLiteral literal
                                             = new ContentItemRendererLiteral(
                itemType, mode);

        final Instance<AbstractContentItemRenderer> instance = renderers
            .select(literal);

        if (instance.isUnsatisfied()) {
            if ("--DEFAULT--".equals(mode)) {

                LOGGER.warn("No renderer for item type \"{}\" and mode "
                                + "\"--DEFAULT--\". Returning default renderer.",
                            itemType.getName());
                return new AbstractContentItemRenderer() {

                    @Override
                    public void renderItem(final ContentItem item,
                                           final Locale language,
                                           final Map<String, Object> result) {
                        //Nothing here.
                    }

                };
            } else {
                LOGGER.warn("No renderer for item type \"{}\" and mode "
                                + "\"{}\". Trying to find renderer for "
                                + "mode \"--DEFAULT--\".",
                            itemType.getName(),
                            mode);
                return findRenderer(itemType);
            }
        } else {
            final AbstractContentItemRenderer renderer = instance
                .iterator()
                .next();

            @SuppressWarnings("unchecked")
            final AbstractContentItemRenderer result
                                                     = renderer;
            return result;
        }

    }

    private class ContentItemRendererLiteral
        extends AnnotationLiteral<ContentItemRenderer>
        implements ContentItemRenderer {

        private static final long serialVersionUID = 6104170621944116228L;

        private final Class<? extends ContentItem> renders;
        private final String mode;

        public ContentItemRendererLiteral(
            final Class<? extends ContentItem> renders,
            final String mode) {

            this.renders = renders;
            this.mode = mode;
        }

        @Override
        public Class<? extends ContentItem> renders() {
            return renders;
        }

        @Override
        public String mode() {
            return mode;
        }

    }

}
