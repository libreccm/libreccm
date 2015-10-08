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
package com.arsdigita.ui.login;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
// import com.arsdigita.kernel.security.LegacyInitializer;

/**
 * Package-private class that generates the URL for a link dynamically from
 * the kernel page map.  This class will be removed or changes when the page
 * map is replaced by package parameters.
 *
 * 2011-02-04: API change (pboy)
 * The page map is no retrieved from a set of parameters. The target is now a
 * String representation of the absolut url (leading slash) relativ to
 * document root. The target is now a targetUrl, no longer a targetKey.
 *
 * @author Sameer Ajmani
 * @version $Id$
 */
class DynamicLink extends Link {

    DynamicLink(final String labelKey, final String targetUrl) {

        super(new Label(LoginHelper.getMessage(labelKey)),
              new PrintListener() {
                  public void prepare(PrintEvent e) {
                      Link link = (Link) e.getTarget();

                  // see {@link com.arsdigita.bebopLink#Link(String,URL)}
                  // Url is now expected without leading context wich is handled
                  // by the new dispatcher. Therefore the req. is not needed.
                  // anymore.
                  //  String url = LegacyInitializer.getFullURL
                  //      (targetKey, e.getPageState().getRequest());

                      link.setTarget(targetUrl);
                  }
              });
    }
}
