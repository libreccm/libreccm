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
package org.libreccm.mvc.freemarker;

import org.libreccm.theming.ThemeInfo;

/**
 * Encapulates the data of a template.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class TemplateInfo {

    /**
     * Info about the theme providing the template.
     */
    private final ThemeInfo themeInfo;

    /**
     * The path of the template,
     */
    private final String filePath;

    public TemplateInfo(ThemeInfo themeInfo, String filePath) {
        this.themeInfo = themeInfo;
        this.filePath = filePath;
    }

    public ThemeInfo getThemeInfo() {
        return themeInfo;
    }

    public String getFilePath() {
        return filePath;
    }

}
