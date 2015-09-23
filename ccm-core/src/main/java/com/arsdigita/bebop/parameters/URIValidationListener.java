/*
 * Copyright (C) Permeance Technologies Pty Ltd. All Rights Reserved.
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
package com.arsdigita.bebop.parameters;

import java.net.URI;
import java.net.URISyntaxException;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * A parameter listener that ensures a parameter is a URI formatted 
 * according to <a href="http://www.ietf.org/rfc/rfc2396.txt">RFC2396</a>:
 * 
 * <blockquote>
 * <pre>
 * The following examples illustrate URI that are in common use.
 * 
 * ftp://ftp.is.co.za/rfc/rfc1808.txt
 *    -- ftp scheme for File Transfer Protocol services
 * 
 * gopher://spinaltap.micro.umn.edu/00/Weather/California/Los%20Angeles
 *    -- gopher scheme for Gopher and Gopher+ Protocol services
 * 
 * http://www.math.uio.no/faq/compression-faq/part1.html
 *    -- http scheme for Hypertext Transfer Protocol services
 * 
 * mailto:mduerst@ifi.unizh.ch
 *    -- mailto scheme for electronic mail addresses
 * 
 * news:comp.infosystems.www.servers.unix
 *    -- news scheme for USENET news groups and articles
 * 
 * telnet://melvyl.ucop.edu/
 *    -- telnet scheme for interactive services via the TELNET Protocol
 * </pre>
 * </blockquote>
 *
 * @author <a href="https://sourceforge.net/users/terry_permeance/">terry_permeance</a>
 */
public class URIValidationListener extends GlobalizedParameterListener {

    public URIValidationListener() {
        setError(new GlobalizedMessage("uri_parameter_is_invalid", getBundleBaseName()));
    }
    
    /**
     * @see ParameterListener#validate(ParameterEvent)
     */
    public void validate(ParameterEvent e) throws FormProcessException {

        ParameterData d = e.getParameterData();
        String value = (String)d.getValue();
    
    	if (value != null && value.length() > 0) {
            try {
                URI uri = new URI(value);                
                if (!uri.isAbsolute()) {
                    d.addError(this.getError());
                }
            } catch (URISyntaxException ex) {
                d.addError(this.getError());
            }
        }
    }
}
