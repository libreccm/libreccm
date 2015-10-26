/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop;

import com.arsdigita.bebop.event.PrintListener;

/**
 * A link to an external (non-ACS) site.  Does not propagate ACS-specific
 * URL parameters.
 *
 * <p>See {@link BaseLink} for a description
 * of all Bebop Link classes.
 *
 * @version $Id: ExternalLink.java 287 2005-02-22 00:29:02Z sskracic $
 */
public class ExternalLink extends Link {

    public ExternalLink(Component child, String url) {
        super(child, url);
    }

    public ExternalLink(Component child, PrintListener l) {
        super(child, l);
    }

    public ExternalLink(String label, String url) {
        super(label, url);
    }

    public ExternalLink(String label, PrintListener l) {
        super(label, l);
    }

    public ExternalLink(PrintListener l) {
        super(l);
    }
    /**
     * Processes the URL for this link after the print listener runs.
     *
     * @param state the current page state
     * @param url the original URL
     *
     * @return the original, unchanged URL.
     **/
    protected String prepareURL(PageState state, String url) {
        return url;
    }
}
