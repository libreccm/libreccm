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
package org.librecms.pagemodel;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemL10NManager;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.privileges.ItemPrivileges;
import org.librecms.pagemodel.contentitems.AbstractContentItemRenderer;
import org.librecms.pagemodel.contentitems.ContentItemRenderers;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;
import javax.transaction.Transactional;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;

import static org.librecms.pages.PagesConstants.*;

import org.libreccm.pagemodel.ComponentRenderer;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public abstract class AbstractContentItemComponentRenderer<T extends ContentItemComponent>
    implements ComponentRenderer<T> {

    @Inject
    private ConfigurationManager confManager;
    
    @Inject
    private ContentItemRenderers contentItemRenderers;

    @Inject
    private ContentItemL10NManager iteml10nManager;

    @Inject
    private ContentItemManager itemManager;

    @Inject
    private PermissionChecker permissionChecker;

    protected abstract ContentItem getContentItem(
        T componentModel, final Map<String, Object> parameters);

    @Transactional(Transactional.TxType.REQUIRED)
    @Override
    public Map<String, Object> renderComponent(
        final T componentModel,
        final Map<String, Object> parameters) {
        
        Objects.requireNonNull(componentModel);
        Objects.requireNonNull(parameters);

        final ContentItem contentItem = getContentItem(componentModel,
                                                       parameters);

        if (Boolean.TRUE.equals(parameters.get("showDraftItem"))) {

            final ContentItem draftItem = itemManager
                .getDraftVersion(contentItem, contentItem.getClass());

            if (permissionChecker.isPermitted(ItemPrivileges.PREVIEW, draftItem)) {
                final Map<String, Object> result = generateItem(componentModel,
                                                                parameters,
                                                                draftItem);
                result.put("showDraftItem", Boolean.TRUE);

                return result;
            } else {
                throw new WebApplicationException(
                    "You are not permitted to view the draft version of this item.",
                    Response.Status.UNAUTHORIZED);
            }

        } else {

            final ContentItem liveItem = itemManager
                .getLiveVersion(contentItem, contentItem.getClass())
                .orElseThrow(() -> new NotFoundException(
                "This content item does not "
                    + "have a live version."));

            if (permissionChecker.isPermitted(ItemPrivileges.VIEW_PUBLISHED,
                                              liveItem)) {
                return generateItem(componentModel,
                                    parameters,
                                    liveItem);
            } else {
                throw new WebApplicationException(
                    "You are not permitted to view the live version of "
                        + "this item.",
                    Response.Status.UNAUTHORIZED);
            }
        }
    }

    protected Map<String, Object> generateItem(
        final T componentModel,
        final Map<String, Object> parameters,
        final ContentItem item) {

        final Locale language;
        if (parameters.containsKey("language")) {
            language = new Locale((String) parameters.get(PARAMETER_LANGUAGE));
        } else {
            final KernelConfig kernelConfig = confManager
                .findConfiguration(KernelConfig.class);
            language = kernelConfig.getDefaultLocale();
        }

        if (iteml10nManager.hasLanguage(item, language)) {
            
            final AbstractContentItemRenderer renderer = contentItemRenderers
            .findRenderer(item.getClass(), componentModel.getMode());
            
            return renderer.render(item, language);            
        } else {
            throw new NotFoundException("Requested language is not available.");
        }
    }
}
