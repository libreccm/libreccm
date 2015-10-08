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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import javax.activation.DataSource;

/**
 * Provides a simple DataSource that allows in-memory data
 * objects to be attached to a mail message.
 *
 * <p>Based on the sample code distributed with the JavaMail 1.2 API.
 *
 * @author Ron Henderson 
 * @version $Id$
 */

public class ByteArrayDataSource implements DataSource {

    /**
     * Holds the data for this DataSource
     */

    private byte[] m_data;

    /**
     * MIME type of the data
     */

    private String m_type;

    /**
     * Name of the data (optional)
     */

    private String m_name;

    /**
     * Creates a data source from an input stream.
     *
     * @param is the InputStream to read from
     * @param type the MIME type of the data
     * @param name the name of the data
     */

    public ByteArrayDataSource (InputStream is,
                                String type,
                                String name)
    {
        m_type = type;
        m_name = name;

        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int ch;

            while ((ch = is.read()) != -1) {
                os.write(ch);
            }

            m_data = os.toByteArray();
        } catch (IOException ioex) {
            // do something
        }
    }

    /**
     * Creates a data source from a byte array.
     *
     * @param data the byte array containing the data
     * @param type the MIME type of the data
     * @param name the name of the data
     */

    public ByteArrayDataSource (byte[] data,
                                String type,
                                String name)
    {
        m_data = data;
        m_type = type;
        m_name = name;
    }

    /**
     * Creates a data source from a String, assuming the data only
     * contains ASCII characters (iso-8859-1).
     *
     * @param data the String containing the data
     * @param type the MIME type of the data
     * @param name the name of the data
     */

    public ByteArrayDataSource (String data,
                                String type,
                                String name)
    {
        this(data,type,name,"iso-8859-1");
    }

    /**
     * Creates a data source from a String using a specified character
     * set.
     *
     * @param data the String containing the data
     * @param type the MIME type of the data
     * @param name the name of the data
     * @param charset the encoding used for the String
     */

    public ByteArrayDataSource (String data,
                                String type,
                                String name,
                                String charset)
    {
        m_type = type;
        m_name = name;

        try {
            m_data = data.getBytes(charset);
        } catch (UnsupportedEncodingException uex) {
            // do something
        }
    }

    /**
     * Returns an input stream for the data.
     */

    public InputStream getInputStream() throws IOException {
        if (m_data == null) {
            throw new IOException("no data");
        }
        return new ByteArrayInputStream(m_data);
    }

    /**
     * Required by the interface, but not available.
     */

    public OutputStream getOutputStream() throws IOException {
        throw new IOException("not implemented");
    }

    /**
     * Returns the MIME type of the content.
     */

    public String getContentType() {
        return m_type;
    }

    /**
     * Returns the name of the content.
     */

    public String getName() {
        return m_name;
    }
}
