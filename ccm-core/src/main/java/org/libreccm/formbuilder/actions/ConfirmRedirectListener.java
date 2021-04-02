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
package org.libreccm.formbuilder.actions;

import org.libreccm.formbuilder.ProcessListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.Objects;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "FORMBUILDER_CONFIRM_REDIRECT_LISTENERS", schema = DB_SCHEMA)
public class ConfirmRedirectListener
    extends ProcessListener
    implements Serializable {

    private static final long serialVersionUID = 7891034630202555922L;

    @Column(name = "URL")
    private String url;

    public String getUrl() {
        return url;
    }

    public void setUrl(final String url) {
        this.url = url;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.url);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof ConfirmRedirectListener)) {
            return false;
        }
        final ConfirmRedirectListener other = (ConfirmRedirectListener) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        return Objects.equals(this.url, other.url);
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof ConfirmRedirectListener;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", url = \"%s\"%s",
                                            url,
                                            data));
    }

}
