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
package org.libreccm.theming.utils;

import java.util.Objects;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class TextUtils {
    
    /**
     * Truncate a string to given length but preserve words. If the provided
     * text is longer than the given length, this method will look for the 
     * last space before the index of the provided length can return the substring
     * from the beginning to that position.
     * 
     * @param text The text to truncate.
     * @param length The length of the truncated text.
     * @return The truncated text.
     */
    public String truncateText(final String text, 
                               final int length) {
        
        Objects.requireNonNull(text);
        
        if (text.length() <= length) {
            return text;
        } else {
            
            final int lastSpace = text.lastIndexOf(" ", length);
            
            return text.substring(0, lastSpace);
        }
    }
    
}
