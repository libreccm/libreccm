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

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

/**
 * Entity representing a single shortcut.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(schema = "CCM_SHORTCUTS", name = "SHORTCUTS")
@NamedQueries({
    @NamedQuery(
        name = "Shortcut.findByUrlKey",
        query = "SELECT s FROM Shortcut s WHERE s.urlKey = :urlKey"),
    @NamedQuery(
        name = "Shortcut.findByRedirect",
        query = "SELECT s FROM Shortcut s "
                    + "WHERE s.redirect = :redirect "
                    + "ORDER BY s.urlKey")
})
public class Shortcut implements Serializable {

    private static final long serialVersionUID = -5674633339633714327L;

    /**
     * Database internal ID of the shortcut.
     */
    @Id
    @Column(name = "SHORTCUT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long shortcutId;

    /**
     * The URL to redirect (the URL which a visitor calls).
     */
    @Column(name = "URL_KEY", length = 1024, unique = true)
    @NotEmpty
    private String urlKey;

    /**
     * The URL to which the requeset for {@link #urlKey} is redirected.
     */
    @Column(name = "REDIRECT", length = 1024)
    @NotEmpty
    private String redirect;

    public long getShortcutId() {
        return shortcutId;
    }

    public void setShortcutId(final long shortcutId) {
        this.shortcutId = shortcutId;
    }

    public String getUrlKey() {
        return urlKey;
    }

    public void setUrlKey(final String urlKey) {
        this.urlKey = urlKey;
    }

    public String getRedirect() {
        return redirect;
    }

    public void setRedirect(final String redirect) {
        this.redirect = redirect;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + (int) (shortcutId ^ (shortcutId >>> 32));
        hash = 47 * hash + Objects.hashCode(urlKey);
        hash = 47 * hash + Objects.hashCode(redirect);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Shortcut)) {
            return false;
        }
        final Shortcut other = (Shortcut) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (shortcutId != other.getShortcutId()) {
            return false;
        }
        if (!Objects.equals(urlKey, other.getUrlKey())) {
            return false;
        }
        return Objects.equals(redirect, other.getRedirect());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Shortcut;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "shortcutId = %d, "
                                 + "urlKey = %s, "
                                 + "redirect = %s"
                                 + " }",
                             super.toString(),
                             shortcutId,
                             urlKey,
                             redirect);
    }

}
