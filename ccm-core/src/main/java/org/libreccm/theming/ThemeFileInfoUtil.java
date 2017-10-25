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

import org.libreccm.core.UnexpectedErrorException;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import javax.enterprise.context.RequestScoped;

/**
 * Utility for building a {@link ThemeFileInfo} object for a file.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ThemeFileInfoUtil implements Serializable {

    private static final long serialVersionUID = -3382896567742774318L;

    /**
     * Build a {@link ThemeFileInfo} object for a file. Before calling this
     * method the caller should check if the file to {@code path} points exists.
     * 
     * @param path The path of the file.
     * @return A {@link ThemeFileInfo} object with informations about the file.
     */
    public ThemeFileInfo buildThemeInfo(final Path path) {

        Objects.requireNonNull(path);
        
        try {
            final ThemeFileInfo fileInfo = new ThemeFileInfo();
            fileInfo.setName(path.getFileName().toString());
            fileInfo.setDirectory(Files.isDirectory(path));
            fileInfo.setWritable(Files.isWritable(path));
            if (!Files.isDirectory(path)) {
                fileInfo.setSize(Files.size(path));
            }
            fileInfo.setMimeType(Files.probeContentType(path));

            return fileInfo;
        } catch (IOException ex) {
            throw new UnexpectedErrorException(ex);
        }
    }

}
