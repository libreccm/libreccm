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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Iterator;

import org.apache.log4j.Logger;

import com.arsdigita.util.UncheckedWrapperException;

/**
 * A parameter representing a JEE <code>Resource</code> (input stream).
 *
 * This takes in a path and makes sure that the resource exists either
 * as a File or an actual resource.  If it does, it returns the
 * InputStream for the given Resource.  If it does not, and if it is
 * required, it logs an error.  Otherwise, it returns null.
 *
 * Development note / CHANGELOG
 * Had been deprecated for a while in favour of an URLParameter and a
 * application specific resource: protocol extension (c.ad.util.protocol.resource).
 * As of version 6.5 reverted to ResourceParameter to avoid non-standard extensions.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author Brett &lt;bprucha@users.sourceforge net&gt;
 * @author PBoy &lt;pboy@users.sourceforge net&gt;
 * @version $Id$
 */
public class ResourceParameter extends AbstractParameter {

    private static final Logger s_log = Logger.getLogger(ResourceParameter.class);

    private Object m_default = null;

    public ResourceParameter(final String name) {
        super(name, InputStream.class);
    }

    public ResourceParameter(final String name,
                             final int multiplicity,
                             final Object defaultValue) {

        super(name, multiplicity, defaultValue, InputStream.class);
        m_default = defaultValue;
    }

    /**
     * Get default value and return it as InputStream.
     *
     * Developers note:
     * This makes the trick to use Parameter.java interface rsp AbstractParameter
     * for other types of parameter as String. If you don't overwrite this
     * method, you will always get a casting error, because the parameter
     * returns a string instead of the intended object!
     *
     * @return default value as InputStream
     */
    public Object getDefaultValue() {

    	if(m_default instanceof String) {
        	ErrorList errors = new ErrorList();
        	InputStream stream = (InputStream)unmarshal((String)m_default, errors);
        	
        	if(!errors.isEmpty()) {
        		String strErrors = "";
        		for(Iterator i = errors.iterator(); i.hasNext(); ) {
        			ParameterError pe = (ParameterError)i.next();
        			strErrors += pe.getMessage() + "\r\n";
        		}
        		throw new UncheckedWrapperException(strErrors);
        	}
        	
        	return stream;
    	} else
    		return m_default;
    }

    /**
     * Unmarshals the encoded string value of the parameter to get the intended
     * object type. It tries first to find a file of the specified name in the
     * file system. If not successful it uses the classloader to find the file
     * in the class path / jar files.
     * 
     * @param value
     * @param errors
     * @return parameter value as an InputStream
     */
    @Override
    protected Object unmarshal(String value, final ErrorList errors) {

        // NOTE:
        // This implementation will never find the file in the file system.
        // The name has to be specified relativ to document root. So we must
        // precede value with the context path, e.g. using
        // c.ad.runtime.CCMResourceManager as soon as it's implementation is
        // fixed / stable (when all modifications of the runtime environment
        // are done).
        File file = new File(value);

        if (!file.exists()) {
            // it is not a standard file so lets try to see if it
            // is a resource
            if (value.startsWith("/")) {
                value = value.substring(1);
            }

            ClassLoader cload = Thread.currentThread().getContextClassLoader();
            URL url = cload.getResource(value);
            InputStream stream = cload.getResourceAsStream(value);
            if (stream == null && isRequired()) {
                s_log.error(value + " is not a valid file and is required");

                final ParameterError error = new ParameterError
                    (this, "Resource not found");
                errors.add(error);
            }
            return stream;
        } else {
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException ioe) {
                // we know the file exists so this should not
                // be an issue
                s_log.error(value + " is not a valid file and is required", ioe);

                errors.add(new ParameterError(this, ioe));

                return null;
            }
        }
    }
}
