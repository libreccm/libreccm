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
package org.libreccm.files;

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.Setting;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration(descBundle
                   = "org.libreccm.files.CcmFilesConfiguration.properties",
               titleKey = "title",
               descKey = "description")
public class CcmFilesConfiguration {

    /**
     * Data of the directory used by CcmFiles to resolve paths.
     */
    @Setting()
    private String dataPath;

    /**
     * Sets the {@link FileSystemAdapter} implementation to use. 
     * Only required if multiple implementations (besides the default
     * implementation {@link NIOFileSystemAdapter}) of the
     * {@link FileSystemAdapter} are available in the classpath.
     */
    @Setting
    private String activeFileSystemAdapter;

    public String getDataPath() {
        return dataPath;
    }

    public void setDataPath(final String dataPath) {
        this.dataPath = dataPath;
    }

    public String getActiveFileSystemAdapter() {
        return activeFileSystemAdapter;
    }

    public void setActiveFileSystemAdapter(final String activeFileSystemAdapter) {
        this.activeFileSystemAdapter = activeFileSystemAdapter;
    }

    
    
}
