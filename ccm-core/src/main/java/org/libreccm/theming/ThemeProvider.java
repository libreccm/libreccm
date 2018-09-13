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
package org.libreccm.theming;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * A theme provider provides access to the files of a theme. It abstracts from
 * the location and method of loading.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface ThemeProvider extends Serializable {

    /**
     * Provides a list of all themes provided by this theme provider. The list
     * should be ordered by the name of the theme.
     *
     * @return A list of all themes provided by this theme provider. If the
     *         implementation supports draft and live themes the list should
     *         contain all draft themes.
     */
    List<ThemeInfo> getThemes();

    /**
     * Provides a list of all live themes provided by this theme provider.
     *
     * @return A list of all live themes provided by this theme provider. If the
     *         implementation does not support draft/live themes the
     *         implementation of this method returns the same list as
     *         {@link #getThemes()}.
     */
    List<ThemeInfo> getLiveThemes();

    /**
     * Provide information about a theme.
     *
     * @param theme   The theme.
     * @param version The version of the theme. Implementations which do not
     *                support draft/live themes will ignore this parameter.
     *
     * @return Informations about the theme identified by the provided name. If
     *         there is no such theme provided by this {@code ThemeProvider} an
     *         empty optional is returned.
     */
    Optional<ThemeInfo> getThemeInfo(String theme, ThemeVersion version);

    /**
     * This method can be used to determine if a theme provider provides a
     * specific theme.
     *
     * @param theme   The name of the theme.
     * @param version The version. Implementations which do not support
     *                live/draft themes should ignore this parameter.
     *
     * @return {@code true} if the provider has a theme with the provided name
     *         in the provided version, {@code false} otherwise.
     */
    boolean providesTheme(String theme, ThemeVersion version);

    /**
     * Creates a new theme.
     *
     * The theme should be empty besides the manifest file. If a theme with the
     * provided name already exists implementations should throw an
     * {@link IllegalArgumentException}.
     *
     * {@code ThemeProvider} implementations which do not support the the
     * creation of new themes the implementation of the method should throw a
     * {@link UnsupportedOperationException}.
     *
     * @param themeName The name of the new theme.
     *
     * @return The {@link ThemeInfo} about the new theme.
     */
    ThemeInfo createTheme(String themeName);

    /**
     * Deletes a theme and all its content.
     *
     * If the is live implementations should throw an exception.
     *
     * {@code ThemeProvider} implementations which do not support the the
     * deletion of themes the implementation of the method should throw a
     * {@link UnsupportedOperationException}.
     *
     * @param themeName The theme to delete.
     */
    void deleteTheme(String themeName);

    /**
     * List all files in a theme at the specified path.
     *
     * @param theme   The theme of which the files are listed.
     * @param version The version of the theme for which the files are listed.
     *                Implementations which do not support draft/live themes
     *                will ignore this parameter.
     * @param path    The path of the directory of which the files are listed.
     *                The path is relative to the root of the theme.To get the
     *                root directory provide an empty string. Implementations
     *                should throw an NullPointerException if {@code null} is
     *                provided as path.
     *
     * @return A list of all files in the provided directory. If there is no
     *         such path in the theme the list is empty. If the path is the path
     *         of a file and not a directory the list should have one element,
     *         the data about the file itself.
     *
     * @throws IllegalArgumentException If {@code theme} is an empty string, if
     *                                  there is no theme with the name provided
     *                                  by {@code theme} or if there is no
     *                                  file/directory with the provided path in
     *                                  the theme.
     */
    List<ThemeFileInfo> listThemeFiles(String theme,
                                       ThemeVersion version,
                                       String path);

    /**
     * Retrieve a file from a theme. We use an {@link InputStream} here because
     * that is the most universal interface in the Java API which works for all
     * sorts of resources and is independent from any other API. Hint: In most
     * cases it is recommended to wrap the {@link InputStream} provided by this
     * method in a {@link InputStreamReader} by using one of constructors of
     * {@link InputStreamReader} which allows the caller to set the charset of
     * the data read (which should be UTF-8 in most cases).
     *
     * @param theme   The theme from which the file is retrieved.
     * @param version The version of the theme from which the file is retrieved.
     *                Implementations which do not support draft/live themes
     *                will ignore this parameter.
     * @param path    The path of file to retrieve relative to the root of the
     *                theme.
     *
     * @return An {@link Optional} containing an {@link InputStream} for the
     *         requested file or an empty optional if the theme has no such
     *         file.
     */
    Optional<InputStream> getThemeFileAsStream(String theme,
                                               ThemeVersion version,
                                               String path);

    /**
     * Creates an {@link OutputStream} for a theme file. Implementations which
     * do not support changes to the theme files should throw an
     * {@link UnsupportedOperationException}. If the file is not writable for
     * some reason an {@link IllegalArgumentException} should be thrown.
     *
     * If an implementation supports draft/live themes the {@link OutputStream}
     * always changes the file in the draft version of the theme. The live theme
     * should only be changed by {@link #publishTheme(String).
     *
     * If the file does not exist it the file is created.
     *
     * If not all directories in the provided path already exist an
     * implementation should create the missing directories.
     *
     * @param theme The theme to which the file belongs.
     * @param path  The path of the file to update
     *
     * @return An {@link OutputStream} for the file.
     */
    OutputStream getOutputStreamForThemeFile(String theme, String path);

    void deleteThemeFile(String theme, String path);

    /**
     * Determines if the implementation supports changes to the files of the
     * themes.
     *
     * @return
     */
    boolean supportsChanges();

    /**
     * Determines if the implementation supports draft/live themes.
     *
     * @return
     */
    boolean supportsDraftThemes();

    /**
     * Publishes all changes done to a draft theme to its live version. For
     * implementations which do not support draft/live themes the implementation
     * of this method should be a noop, but not throw an exception.
     *
     * @param theme The theme to publish.
     */
    void publishTheme(String theme);

}
