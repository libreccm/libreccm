/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.templating;

import javax.servlet.http.HttpServletRequest;

/**
 * Generates a pattern for based on the outputType request
 * parameter
 */
public class OutputTypePatternGenerator implements PatternGenerator {
    public String[] generateValues(String key,
                                   HttpServletRequest req) {
        String query = req.getQueryString();
        if (query != null) {
            int typeIndex = query.indexOf("outputType");
            if (typeIndex > -1) {
                int secondaryIndex = query.indexOf("&", typeIndex);
                String type = null;
                if (secondaryIndex > -1) {
                    type = query.substring(typeIndex, secondaryIndex);
                } else {
                    type = query.substring(typeIndex);
                }
                type = type.toLowerCase();
                if (type.indexOf("text/javascript") > -1) {
                    return new String[] { "text-javascript" };
                } else if (type.indexOf("text/html") > -1) {
                    return new String[] { "text-html" };
                } else if (type.indexOf("text/plain") > -1) {
                    return new String[] { "text-plain" };
                }
            }
        }
        
        return new String[] { };
    }
}
