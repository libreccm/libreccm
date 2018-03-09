/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.theming.db;

import org.libreccm.core.CoreConstants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

/**
 * Directory in the file structure of a theme stored in the database.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "theme_directories", schema = CoreConstants.DB_SCHEMA)
public class Directory extends ThemeFile {

    private static final long serialVersionUID = 3553722448470575337L;
    
    @OneToMany(mappedBy = "parent")
    @OrderBy("name ASC")
    private List<ThemeFile> files;

    public List<ThemeFile> getFiles() {
        return Collections.unmodifiableList(files);
    }

    protected void setFiles(final List<ThemeFile> files) {
        this.files = new ArrayList<>(files);
    }
    
    protected void addFile(final ThemeFile file) {
        files.add(file);
    }
    
    protected void removeFile(final ThemeFile file) {
        files.remove(file);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 47 * hash;
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof Directory)) {
            return false;
        }
        final Directory other = (Directory) obj;
        return other.canEqual(this);
    }
    
    @Override
    public boolean canEqual(final Object other) {
        return other instanceof Directory;
    }
    
}
