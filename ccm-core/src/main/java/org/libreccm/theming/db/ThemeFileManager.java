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

import org.libreccm.security.RequiresPrivilege;
import org.libreccm.theming.ThemingPrivileges;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Provides methods for managing the files of the theme stored in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemeFileManager {

    @Inject
    private ThemeFileRepository fileRepository;

    /**
     * Creates a new {@link DataFile}.
     *
     * @param parent The directory in which the {@link DataFile} is created.
     * @param name   The name of the new {@link DataFile}.
     *
     * @return The new {@link DataFile}.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public DataFile createDataFile(final Directory parent,
                                   final String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a new {@link Directory}.
     *
     * @param parent The parent directory of the new {@link Directory}.
     * @param name   The name of the new {@link Directory}
     *
     * @return The new {@link Directory}.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public Directory createDirectory(final Directory parent,
                                     final String name) {
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes a {@link ThemeFile} in the theme. If the file is a directory the
     * directory must be empty.
     *
     * @param file The {@link ThemeFile} to delete.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public void delete(final ThemeFile file) {
        throw new UnsupportedOperationException();
    }

    /**
     * Deletes a {@link ThemeFile} recursivly. If the file is a {@link DataFile}
     * the behaviour is identical to
     * {@link #delete(org.libreccm.theming.db.ThemeFile)}. If the the file is a
     * directory all files in the directory and its subdirectories are also
     * deleted.
     *
     * @param file The {@link ThemeFile} to delete.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public void deleteRecursive(final ThemeFile file) {
        throw new UnsupportedOperationException();
    }

    /**
     * Creates a copy of the {@link ThemeFile}. If the file is a directory an
     * empty directory is created.
     *
     * @param file   The file to copy.
     * @param target The target directory.
     *
     * @return The newly created copy.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public ThemeFile copy(final ThemeFile file, final Directory target) {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies a {@link ThemeFile} and changes its name.
     *
     * @param file       The file the copy.
     * @param target     The target directory.
     * @param nameOfCopy The name of the copy.
     *
     * @return The copy.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public ThemeFile copy(final ThemeFile file,
                          final Directory target,
                          final String nameOfCopy) {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies a {@link ThemeFile}. If the file is a directory all files and
     * subdirectories are also copied.
     *
     * @param file   The file to copy.
     * @param target The target directory.
     *
     * @return The copy.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public ThemeFile copyRecursive(final ThemeFile file,
                                   final Directory target) {
        throw new UnsupportedOperationException();
    }

    /**
     * Copies a {@link ThemeFile} and sets the name of copy. If the file is a
     * directory all files and subdirectories are also copied.
     *
     * @param file       The file to copy.
     * @param target     The target directory.
     * @param nameOfCopy The name of the copy.
     *
     * @return The copy.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public ThemeFile copyRecursive(final ThemeFile file,
                                   final Directory target,
                                   final String nameOfCopy) {
        throw new UnsupportedOperationException();
    }

    /**
     * Moves a {@link ThemeFile}.
     *
     * @param file   The file to move.
     * @param target The target directory.
     *
     * @return The moved file.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public ThemeFile move(final ThemeFile file,
                          final Directory target) {
        throw new UnsupportedOperationException();
    }

    /**
     * Moves a {@link ThemeFile} and changes its name.
     *
     * @param file    The file to move.
     * @param target  The target directory.
     * @param newName The new name of the file.
     *
     * @return The moved file.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    public ThemeFile move(final ThemeFile file,
                          final Directory target,
                          final String newName) {
        throw new UnsupportedOperationException();
    }

}
