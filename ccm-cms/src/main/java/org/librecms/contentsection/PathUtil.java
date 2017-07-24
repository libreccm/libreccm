/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.librecms.contentsection;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class PathUtil {
    
    private PathUtil() {
        //Nothing
    }
    
    /**
     * Normalises a path so that the path can be easily processed. This is a 
     * helper method used by several classes in this package.
     * 
     * The method does the following:
     * <ul>
     *     <li>If the first character is a slash remove the character.</li>
     *     <li>If the last character is a slash remove the character.</li>
     * </ul>
     * 
     * @param path The path to normalise.
     * @return  The normalised path
     */
    protected static final String normalizePath(final String path) {
        String normalizedPath = path;
        if (normalizedPath.charAt(0) == '/') {
            normalizedPath = normalizedPath.substring(1);
        }

        if (normalizedPath.endsWith("/")) {
            normalizedPath = normalizedPath.substring(0,
                                                      normalizedPath.length());
        }
        
        return normalizedPath;
    }
    
}
