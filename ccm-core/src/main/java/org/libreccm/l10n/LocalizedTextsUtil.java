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
package org.libreccm.l10n;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Objects;
import java.util.ResourceBundle;

/**
 * This class provides an wrapper around the {@link ResourceBundle} class. Each
 * instance represents a particular {@link ResourceBundle}. Instances of this
 * class can be obtained using
 * {@link GlobalizationHelper#getLocalizedTexts(java.lang.String)}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LocalizedTextsUtil {

    private static final Logger LOGGER = LogManager.getLogger(
        LocalizedTextsUtil.class);

    /**
     * The name of the bundle to use.
     */
    private final String bundleName;
    /**
     * The {@link ResourceBundle} to use.
     */
    private final ResourceBundle bundle;

    /**
     * Internal constructor, only to be invoked by
     * {@link GlobalizationHelper#getLocalizedTexts(java.lang.String)}.
     *
     * @param bundleName The fully qualified name of the bundle.
     * @param bundle     The {@link ResourceBundle} to use.
     */
    LocalizedTextsUtil(final String bundleName, final ResourceBundle bundle) {

        Objects.requireNonNull(bundleName);

        this.bundleName = bundleName;
        this.bundle = bundle;
    }

    /**
     * Retrieves to string identified by {@code key} from {@link #bundle}. If
     * {@link #bundle} is {@code null} or if {@link #bundle} has no value of the
     * provided {@code key} the {@link #bundleName} and the key, separated by a
     * "{@code /}" are returned.
     *
     * @param key The key of the string to retrieve from the {@link #bundle}.
     *
     * @return The string.
     */
    public String getText(final String key) {
        if (bundle == null) {
            LOGGER.warn("No ResourceBundle \"{}\".");
        } else {
            try {
                return bundle.getString(key);
            } catch (MissingResourceException ex) {
                LOGGER.warn(
                    "No resource for key \"{}\" in ResourceBundle \"{}\"",
                    key,
                    bundleName);
            }
        }

        return String.format("%s/%s", bundleName, key);
    }

    /**
     * Retrieves to string identified by {@code key} from {@link #bundle} and
     * uses {@link MessageFormat} to replace the placeholders in the string with
     * the provided {@code arguments}. If {@link #bundle} is {@code null} or if
     * {@link #bundle} has no value of the provided {@code key} the
     * {@link #bundleName} and the key, separated by a "{@code /}" are returned.
     *
     * @param key The key of the string to retrieve from the {@link #bundle}.
     * @param arguments The parameters to use.
     *
     * @return The string.
     */
    public String getText(final String key, final Object[] arguments) {

        if (bundle == null) {
            LOGGER.warn("No ResourceBundle \"{}\".");
        } else {
            try {
                final String text = bundle.getString(key);
                return MessageFormat.format(text, arguments);
            } catch (MissingResourceException ex) {
                LOGGER.warn(
                    "No resource for key \"{}\" in ResourceBundle \"{}\"",
                    key,
                    bundleName);
            }
        }

        return String.format("%s/%s", bundleName, key);

    }

}
