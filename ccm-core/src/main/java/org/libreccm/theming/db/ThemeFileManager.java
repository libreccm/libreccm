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
import org.libreccm.theming.ThemeVersion;
import org.libreccm.theming.ThemingPrivileges;

import java.util.Date;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * Provides methods for managing the files of the theme stored in the database.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemeFileManager {

    @Inject
    private ThemeFileRepository fileRepository;

    @Inject
    private ThemeRepository themeRepository;

    /**
     * Creates a new, empty {@link DataFile}.
     *
     * @param theme  The {@link Theme} to which the file belongs.
     * @param parent The {@link Directory} in which the {@link DataFile} is
     *               created.
     * @param name   The name of the new {@link DataFile}.
     *
     * @return The new {@link DataFile}.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    @Transactional(Transactional.TxType.REQUIRED)
    public DataFile createDataFile(final Theme theme,
                                   final Directory parent,
                                   final String name) {

        Objects.requireNonNull(parent);
        Objects.requireNonNull(name);

        if (name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of file can't be empty.");
        }

        final Date now = new Date();
        final String path = String.join("/", parent.getPath(), name);

        final DataFile dataFile = new DataFile();
        dataFile.setCreationDate(now);
        dataFile.setLastModified(now);
        dataFile.setName(name);
        dataFile.setParent(parent);
        dataFile.setPath(path);
        dataFile.setTheme(theme);
        dataFile.setVersion(ThemeVersion.DRAFT);

        parent.addFile(dataFile);

        fileRepository.save(dataFile);
        fileRepository.save(parent);
        themeRepository.save(theme);

        return dataFile;
    }

    /**
     * Creates a new {@link Directory}.
     *
     * @param theme  The {@link Theme} to which the file belongs.
     * @param parent The parent directory of the new {@link Directory}.
     * @param name   The name of the new {@link Directory}
     *
     * @return The new {@link Directory}.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    @Transactional(Transactional.TxType.REQUIRED)
    public Directory createDirectory(final Theme theme,
                                     final Directory parent,
                                     final String name) {

        Objects.requireNonNull(parent);
        Objects.requireNonNull(name);

        if (name.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of file can't be empty.");
        }

        final String path = String.join("/", parent.getPath(), name);

        final Directory directory = new Directory();
        directory.setName(name);
        directory.setParent(parent);
        directory.setPath(path);
        directory.setTheme(theme);
        directory.setVersion(ThemeVersion.DRAFT);

        parent.addFile(directory);

        fileRepository.save(directory);
        fileRepository.save(parent);
        themeRepository.save(theme);

        return directory;
    }

    /**
     * Deletes a {@link ThemeFile} in the theme. If the file is a directory the
     * directory must be empty.
     *
     * @param file The {@link ThemeFile} to delete.
     */
    @RequiresPrivilege(ThemingPrivileges.EDIT_THEME)
    @Transactional(Transactional.TxType.REQUIRED)
    public void delete(final ThemeFile file) {

        Objects.requireNonNull(file);

        if (file instanceof DataFile) {
            final Directory parent = file.getParent();
            parent.removeFile(file);
            fileRepository.delete(file);
            fileRepository.save(parent);
        } else if (file instanceof Directory) {
            final Directory directory = (Directory) file;
            if (directory.getFiles().isEmpty()) {
                final Directory parent = file.getParent();
                parent.removeFile(file);
                fileRepository.delete(file);
                fileRepository.save(parent);
            } else {
                throw new IllegalArgumentException(String
                    .format("File \"%s\" is a directory and not empty.",
                            directory.getPath()));
            }
        } else {
            throw new IllegalArgumentException(String
                .format("Don't know how handle file type \"%s\".",
                        file.getClass().getName()));
        }
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
    @Transactional(Transactional.TxType.REQUIRED)
    public void deleteRecursive(final ThemeFile file) {

        Objects.requireNonNull(file);

        if (file instanceof DataFile) {
            delete(file);
        } else if (file instanceof Directory) {

            final Directory directory = (Directory) file;
            directory
                .getFiles()
                .forEach(subFile -> deleteRecursive(subFile));
            final Directory parent = file.getParent();
            parent.removeFile(file);
            fileRepository.delete(file);
            fileRepository.save(parent);
        } else {
            throw new IllegalArgumentException(String
                .format("Don't know how handle file type \"%s\".",
                        file.getClass().getName()));
        }
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
    @Transactional(Transactional.TxType.REQUIRED)
    public ThemeFile copy(final ThemeFile file, final Directory target) {

        Objects.requireNonNull(file);
        Objects.requireNonNull(target);

        return copy(file, target, file.getName());
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
    @Transactional(Transactional.TxType.REQUIRED)
    public ThemeFile copy(final ThemeFile file,
                          final Directory target,
                          final String nameOfCopy) {

        Objects.requireNonNull(file);
        Objects.requireNonNull(target);
        Objects.requireNonNull(nameOfCopy);

        if (nameOfCopy.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of the copy can't be empty.");
        }

        target
            .getFiles()
            .stream()
            .filter(subFile -> subFile.getName().equals(nameOfCopy))
            .findAny()
            .ifPresent(subFile -> {
                throw new IllegalArgumentException(String
                    .format("The target directory \"%s\"already contains a "
                                + "file with name \"%s\".",
                            target.getPath(),
                            nameOfCopy));
            });

        if (file instanceof DataFile) {

            final DataFile source = (DataFile) file;
            final DataFile copy = new DataFile();
            final Date now = new Date();
            copy.setCreationDate(now);
            copy.setData(source.getData());
            copy.setLastModified(now);
            copy.setName(nameOfCopy);
            copy.setParent(target);
            copy.setPath(String.join("/", target.getPath(), copy.getName()));
            copy.setSize(source.getSize());
            copy.setTheme(source.getTheme());
            copy.setType(source.getType());
            copy.setVersion(source.getVersion());

            fileRepository.save(copy);
            fileRepository.save(target);
            themeRepository.save(copy.getTheme());

            return copy;
        } else if (file instanceof Directory) {

            final Directory source = (Directory) file;
            final Directory copy = new Directory();
            copy.setName(nameOfCopy);
            copy.setParent(target);
            copy.setPath(String.join("/", target.getPath(), copy.getName()));
            copy.setTheme(source.getTheme());
            copy.setVersion(source.getVersion());

            fileRepository.save(copy);
            fileRepository.save(target);

            return copy;
        } else {
            throw new IllegalArgumentException(String
                .format("Don't know how handle file type \"%s\".",
                        file.getClass().getName()));
        }
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
    @Transactional(Transactional.TxType.REQUIRED)
    public ThemeFile copyRecursive(final ThemeFile file,
                                   final Directory target) {

        Objects.requireNonNull(file);
        Objects.requireNonNull(target);

        return copyRecursive(file, target, file.getName());
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
    @Transactional(Transactional.TxType.REQUIRED)
    public ThemeFile copyRecursive(final ThemeFile file,
                                   final Directory target,
                                   final String nameOfCopy) {

        Objects.requireNonNull(file);
        Objects.requireNonNull(target);
        Objects.requireNonNull(nameOfCopy);

        if (nameOfCopy.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a file can't be empty.");
        }

        final ThemeFile copy = copy(file, target, nameOfCopy);

        if (file instanceof Directory) {
            final Directory source = (Directory) file;
            final Directory copiedDirectory = (Directory) copy;

            source
                .getFiles()
                .forEach(subFile -> copyRecursive(subFile, copiedDirectory));
        }

        return copy;
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

        Objects.requireNonNull(file);
        Objects.requireNonNull(target);

        return move(file, target, file.getName());
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

        Objects.requireNonNull(file);
        Objects.requireNonNull(target);
        Objects.requireNonNull(newName);

        if (newName.matches("\\s*")) {
            throw new IllegalArgumentException(
                "The name of a file can't be empty.");
        }

        target
            .getFiles()
            .stream()
            .filter(subFile -> subFile.getName().equals(newName))
            .findAny()
            .ifPresent(subFile -> {
                throw new IllegalArgumentException(String
                    .format("The target directory \"%s\"already contains a "
                                + "file with name \"%s\".",
                            target.getPath(),
                            newName));
            });

        final Directory oldParent = file.getParent();

        file.setName(newName);
        oldParent.removeFile(file);
        target.addFile(file);
        file.setParent(target);

        fileRepository.save(file);
        fileRepository.save(oldParent);
        fileRepository.save(target);

        return file;

    }

}
