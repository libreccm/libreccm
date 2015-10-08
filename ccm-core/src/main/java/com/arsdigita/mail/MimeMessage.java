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
package com.arsdigita.mail;

// JavaMail API
import javax.mail.Session;
import javax.mail.MessagingException;

/**
 * Extends the standard MimeMessage to supply the correct value for
 * MessageID on all outbound email.
 *
 * @version $Id$
 */

final class MimeMessage extends javax.mail.internet.MimeMessage {

    // Constants

    private static final String MESSAGE_ID = "Message-ID";

    private String m_messageID;

    /**
     * Default constructor.
     */

    public MimeMessage (Session session) {
        super(session);
    }

    /**
     * Called by the saveChanges() method to update the MIME headers.
     */

    protected void updateHeaders()
        throws MessagingException
    {
        super.updateHeaders();

        if (m_messageID != null) {
            try {
                setHeader(MESSAGE_ID, m_messageID);
            } catch (MessagingException mex) {
                // ignore
            }
        }
    }

    public void setMessageID(String messageID) {
        m_messageID = messageID;
    }

}
