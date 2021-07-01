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
package org.librecms.assets;

/**
 * DTO for properties of an image.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ImageProperties {
    
    /**
     * The width of the image.
     */
    private int width;
    
    /**
     * The height of the image
     */
    private int height;
    
    protected ImageProperties() {
        
    }

    public int getWidth() {
        return width;
    }

    protected void setWidth(final int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    protected void setHeight(final int height) {
        this.height = height;
    }
    
    
    
}
