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
package org.librecms.contentsection.rs;

import org.librecms.contentsection.Asset;

import javax.json.JsonObjectBuilder;

/**
 * An helper interface for providing additional properties of assets which are 
 * provided as JSON object.
 * 
 * Implementations must be CDI beans (request scope is recommended) annotated
 * with the qualifier annotation {@link ProvidesProperties}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface AssetPropertiesProvider {
    
    void addProperties(Asset asset, JsonObjectBuilder builder);
    
}
