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
package com.arsdigita.util.parameter;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import org.apache.oro.text.perl.Perl5Util;

/**
 * Subject to change.
 *
 * A parameter representing an <code>InternetAddress</code>.
 *
 * @see javax.mail.internet.InternetAddress
 * @see Parameter
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class EmailParameter extends StringParameter {

    private static final Perl5Util s_perl = new Perl5Util();
    private static final String s_regex =
        "/^[^@<>\"\t ]+@[^@<>\".\t]+([.][^@<>\".\n ]+)+$/";

    public EmailParameter(final String name) {
        super(name);
    }

    protected Object unmarshal(final String value, final ErrorList errors) {
        try {
            return new InternetAddress(value);
        } catch (AddressException ae) {
            errors.add(new ParameterError(this, ae));
            return null;
        }
    }

    protected void doValidate(final Object value, final ErrorList errors) {
        super.doValidate(value, errors);

        final InternetAddress email = (InternetAddress) value;

        if (!s_perl.match(s_regex, email.toString())) {
            final ParameterError error = new ParameterError
                (this, "The value is not a valid email address");

            errors.add(error);
        }
    }
}
