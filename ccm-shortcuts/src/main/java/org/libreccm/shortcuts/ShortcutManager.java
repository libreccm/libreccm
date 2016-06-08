/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.shortcuts;

import java.net.URI;
import java.net.URL;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * This class provides complex operations on {@link Shortcut} objects like
 * creating a Shortcut. To use this class add an injection point to your class.
 *
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 */
@RequestScoped
public class ShortcutManager {
    
    /**
     * {@link ShortcutRepository} for interacting with the database.
     */
    @Inject
    private ShortcutRepository shortcutRepository;

    /**
     * Creates a Shortcut
     *
     * @param url
     * @param redirect
     * @return the new Shortcut
     */
    public Shortcut createShortcut(final String url, final String redirect) {
        Shortcut shortcut = new Shortcut();
        shortcut.setUrlKey(url);
        shortcut.setRedirect(redirect);
        return shortcut;
    }

    /**
     * Creates a Shortcut
     *
     * @param url
     * @param redirect
     * @return the new Shortcut
     */
    public Shortcut createShortcut(final URL url, final URL redirect) {
        Shortcut shortcut = new Shortcut();
        shortcut.setUrlKey(url.toString());
        shortcut.setRedirect(redirect.toString());
        return shortcut;
    }

    /**
     * Creates a Shortcut
     *
     * @param uri
     * @param redirect
     * @return the new Shortcut
     */
    public Shortcut createShortcut(final URI uri, final URI redirect) {
        Shortcut shortcut = new Shortcut();
        shortcut.setUrlKey(uri.toString());
        shortcut.setRedirect(redirect.toString());
        return shortcut;
    }

    /**
     * checks if the Shortcut exists.
     *
     * @return true if the Shortcut exists
     */
    private boolean testShortcut(final Shortcut shortcut) {
        return true;
    }

    /**
     * checks if the given URL is valid
     *
     * @param url the URL you want to validate
     * @return true if you can successfully connect to the url, therefore is
     * valid.
     */
    private boolean validateURL(final String url) {
        //TODO
        return false;

    }

    
}
