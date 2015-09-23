/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.util;

/**
 * Special MIME types useful for typing Message objects.
 *
 * @author Ron Henderson 
 * @version $Id: MessageType.java 287 2005-02-22 00:29:02Z sskracic $
 */

public interface MessageType {

    /**
     * MIME type of "text/html"
     */
    public final static String TEXT_HTML = "text/html";

    /**
     * MIME type of "text/plain"
     */
    public final static String TEXT_PLAIN = "text/plain";

    /**
     * MIME type of "text/plain" with a special format qualifier that
     * text should displayed as formatted.
     */
    public final static String TEXT_PREFORMATTED =
        TEXT_PLAIN + "; format=preformatted";

    /**
     * MIME type of "text/plain" with a special format qualifier that
     * simple inline markup should be recognised
     */
    public final static String TEXT_SMART =
        TEXT_PLAIN + "; format=smart";
}
