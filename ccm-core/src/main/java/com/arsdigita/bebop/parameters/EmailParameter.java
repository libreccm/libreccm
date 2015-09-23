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
package com.arsdigita.bebop.parameters;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.servlet.http.HttpServletRequest;
import org.apache.oro.text.perl.Perl5Util;

/**
 * An email address parameter. The parameter in the request is only
 * accepted if it represents a valid email address such as
 * <tt>webmaster@foo.com</tt>. The email address from the
 * request is converted into a {@link
 * javax.mail.internet.InternetAddress} if it looks like a valid email
 * address. If it does not, the parameter flags a parameter validation
 * error.
 *
 * <p> The request value looks like a valid email address if it matches the
 * regular expression
 * <tt>^[^@&lt;&gt;\"\t ]+@[^@&lt;&gt;\".\t]+([.][^@&lt;&gt;\".\n]+)+$</tt>
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Rory Solomon
 */

public class EmailParameter extends StringParameter {

    private static Perl5Util re = new Perl5Util();

    /**
     * Create a new email parameter corresponding to a request parameter
     * with the given name.
     *
     * @param name the name of the request parameter from which the email
     * address is read.
     */
    public EmailParameter(String name) {
        super(name);
    }

    /**
     * Retrieve the email address from the request. Returns
     * <code>null</code> if the request parameter does not look like a
     * valid email address.
     *
     * @param request represents the current request
     * @return the transformed email address as a {@link
     * javax.mail.internet.InternetAddress} or <code>null</code> if
     * there is no request parameter with the email parameter's name.
     * @throws IllegalArgumentException if the request parameter does not
     * look like a valid email address.
     */
    public Object transformValue(HttpServletRequest request)
        throws IllegalArgumentException {
        return transformSingleValue(request);
    }

    public Object unmarshal(String encoded)
        throws IllegalArgumentException {

        // As stated above, if we get an invalid address just return null.

        if (encoded == null || encoded.length() < 1)
            return null;

        InternetAddress emailValue;
        try {
            emailValue = new InternetAddress(encoded);
        } catch (AddressException e) {
            throw new IllegalArgumentException
                (getName() +
                 " is not a valid email address: '" + encoded + "'; " +
                 e.getMessage());
        }

        // rogerh@arsdigita.com
        // using InternetAddress constructor actually does very little error checking
        // eg, new InternetAddress("blahblahblah") is considered a valid address
        // thus we use good old ACS regex function here to valid email entery.

        if (!isValidAddress(encoded)) {
            //TODO: fix error display so html tags don't become quoted
            throw new IllegalArgumentException("The email address that you typed " +
                                               "doesn't look right to us. Examples of " +
                                               "valid email addresses are: " +
                                               "<ul>\n" +
                                               "  <li> Alice1234@aol.com\n" +
                                               "  <li> joe_smith@hp.com\n" +
                                               "  <li> pierre@inria.fr\n" +
                                               "</ul>");
        }
        return emailValue;
    }

    public Class getValueClass() {
        return InternetAddress.class;
    }

    /**
     * this is copy and pasted from com.arsdigita.kernel.acs.Utilities.
     * to work around project dependency issue.
     */
    private static boolean isValidAddress(String email) {
        return re.match("/^[^@<>\"\t ]+@[^@<>\".\t]+([.][^@<>\".\n ]+)+$/", email);
    }

}
