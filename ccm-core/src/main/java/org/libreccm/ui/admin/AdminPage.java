/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin;

import java.util.ResourceBundle;
import java.util.Set;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface AdminPage {

    /**
     * Classes implementing the controllers of the page.
     *
     * @return A set of controllers to be added to the {@link AdminApplication}.
     */
    Set<Class<?>> getControllerClasses();

    /**
     * The entry point for the admin module. Used to generate the URL for an
     * admin page in the navigation etc. For example, with the return value will
     * {@code systeminformation} the generated link is
     * {@code #{contextPath}/@admin/#{path}}.
     *
     * @return The path fragment of the entry point of the admin page/module.
     */
    String getPath();

    /**
     * Gets the resourcebundle which provides the label of the admin page.
     *
     * @return The bundle to use for retrieving the label of the page.
     */
    String getLabelBundle();

    /**
     * Gets the key for retrieving the label of the page from the label bundle.
     *
     * @return The key of the label.
     */
    String getLabelKey();

    /**
     * Gets the resourcebundle which provides the description of the admin page.
     *
     * @return The bundle to use for retrieving the label of the page.
     */
    String getDescriptionBundle();

    /**
     * Gets the key for retrieving the description of the page from the
     * description bundle.
     *
     * @return The key of the label.
     */
    String getDescriptionKey();

    /**
     * Name of icon to use.
     *
     * @return The icon to use for the page.
     */
    String getIcon();

    /**
     * Gets the position of the page in the admin nav bar.
     *
     * @return The position of the page in the admin navigation.
     */
    int getPosition();

}
