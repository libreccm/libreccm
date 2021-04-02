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
package org.librecms.profilesite.pagemodel;

import org.librecms.profilesite.ProfileSiteItem;

import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface ProfileSiteContentRenderer {

    /**
     * Provides the category name for which this renderer is responsible.
     *
     * @return The category name for which this renderer is responsible.
     */
    String getCategoryName();

    /**
     * Renders special content for a profile site depending on the current
     * category.
     *
     * @param componentModel
     * @param parameters
     * @param profileSiteItem
     *
     * @return
     */
    Map<String, Object> renderContent(
        ProfileSiteComponent componentModel,
        Map<String, Object> parameters,
        ProfileSiteItem profileSiteItem
    );

}
