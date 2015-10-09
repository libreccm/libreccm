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
import java.util.Iterator;
import java.util.List;
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
    private transient ShortcutRepository shortcutRepository;

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
     * @param url the Url you want to validate
     * @return true if you can succesfully connect to the url, therefore is
     * valid.
     */
    private boolean validateURL(final String url) {
        //TODO
        return false;

    }

    /**
     * Finds the first shortcut with the specified urlKey.
     *
     * @param urlKey the wanted urlKey
     * @return Shortcut a shortcut with the specified urlKey
     */
    public Shortcut findByUrlKey(final String urlKey) {
        //get all Shortcuts:
        List shortcutlist = shortcutRepository.findAll();
        //search for the right one:
        Iterator<Shortcut> iterator = shortcutlist.iterator();
        while (iterator.hasNext()) {
            Shortcut shortcut = iterator.next();
            if (shortcut.getUrlKey().equals(urlKey)) {
                return shortcut;
            }
        }

        return null;
    }

    /**
     * Finds all shortcuts with the specified redirect.
     *
     * @param redirect the wanted redirect
     * @return List<Shortcut> a List of Shortcuts with the specified redirect
     */
    public List<Shortcut> findByRedirect(final String redirect) {
        //get all Shortcuts:
        List shortcutlist = shortcutRepository.findAll();
        //removes all shortcuts that don't fit
        Iterator<Shortcut> iterator = shortcutlist.iterator();
        while (iterator.hasNext()) {
            Shortcut shortcut = iterator.next();
            if (!shortcut.getRedirect().equals(redirect)) {
                shortcutlist.remove(shortcut);
            }
        }
        return shortcutlist;
    }
}
