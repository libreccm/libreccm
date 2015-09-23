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
package com.arsdigita.util;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/**
 * A library of methods for dealing with I/O streams.
 */
public class IO {
    
    /**
     * Copies the contents of an input stream to another
     * output stream.
     *
     * @param src the source file to be sent
     * @param dst the destination to send the file to
     */
    public static void copy(InputStream src,
                            OutputStream dst) 
        throws IOException {
        
        byte buf[] = new byte[4096];
        int ret;

        while ((ret = src.read(buf)) != -1) {
            dst.write(buf, 0, ret);
        }
        
        dst.flush();
    }
    
    
    // XXX add a method that, given a InputStream
    // figures out what character set the containing
    // document is, ie reads the XML prolog.
    // Also have similar char set discovery APIs
    // for HttpServletRequest objects.
}
