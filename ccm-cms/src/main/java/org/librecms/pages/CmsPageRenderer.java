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
package org.librecms.pages;

import org.libreccm.pagemodel.AbstractPageRenderer;
import org.libreccm.pagemodel.PageRenderer;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;

/**
 * Implementation of {@link PageRenderer} for CMS pages.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CmsPageRenderer extends AbstractPageRenderer {

    @Override
    public Map<String, Object> renderPage(final Map<String, Object> parameters) {
        
        final Map<String, Object> result = new HashMap<>();
        
        return result;
        
    }
    
    
    
}
