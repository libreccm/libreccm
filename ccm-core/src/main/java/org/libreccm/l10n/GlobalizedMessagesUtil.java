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

import com.arsdigita.globalization.GlobalizedMessage;

import java.util.ResourceBundle;

/**
 * A helper class for obtaining {@link GlobalizedMessage}s. This class is
 * intended to replace to numerous classes called {@code *GlobalizationUtil} in
 * the legacy code.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class GlobalizedMessagesUtil {

    /**
     * The fully qualified name of the bundle to use.
     */
    private final String bundleName;

    /**
     * Internal constructor only to be invoked by
     * {@link GlobalizationHelper#getGlobalizedMessagesUtil(java.lang.String)}.
     *
     * @param bundleName
     */
    GlobalizedMessagesUtil(final String bundleName) {
        this.bundleName = bundleName;
    }

    /**
     * Get a {@link GlobalizedMessage} for the provided {@code key} using the
     * {@link ResourceBundle} identified by {@link #bundleName}.
     *
     * @param key The key.
     *
     * @return The {@link GlobalizedMessage}.
     */
    public GlobalizedMessage getGlobalizedMessage(final String key) {
        return new GlobalizedMessage(key, bundleName);
    }

    /**
     * Get a {@link GlobalizedMessage} with placeholders for the provided
     * {@code key} using the {@link ResourceBundle} identified by
     * {@link #bundleName}.
     *
     * @param key The key.
     *
     * @return The {@link GlobalizedMessage}.
     */
    public GlobalizedMessage getGlobalizedMessage(final String key,
                                                  final Object[] arguments) {
        return new GlobalizedMessage(key, bundleName, arguments);
    }

}
