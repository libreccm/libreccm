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

import java.util.Collections;
import java.util.Set;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface MvcAssetEditSteps {

    public static final String PATH_PREFIX
        = "/{sectionIdentifier}/assets/{assetPath:(.+)?}/@";

    public static final String SECTION_IDENTIFIER_PATH_PARAM
        = "sectionIdentifier";

    public static final String ASSET_PATH_PATH_PARAM_NAME = "assetPath";

    public static final String ASSET_PATH_PATH_PARAM
        = ASSET_PATH_PATH_PARAM_NAME + ":(.+)?";

    Set<Class<?>> getClasses();

    default Set<Class<?>> getResourceClasses() {
        return Collections.emptySet();
    }

}
