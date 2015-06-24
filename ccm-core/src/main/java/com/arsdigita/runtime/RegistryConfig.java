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
package com.arsdigita.runtime;

// import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.StringUtils;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringParameter;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * A config class used by the registry itself.
 *
 * Contains the parameters:
 * waf.config.packages: comma separated package-key list of installed packages
 * waf.config.parents :
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id$
 */
public class RegistryConfig extends AbstractConfig {

    /**
     * Helper method to unmarshal parameter values.
     * @param str A String of comma separated values
     * @return StringArray of the values
     */
    private static String[] array(String str) {
        if (str == null) {
            return null;
        } else {
            return StringUtils.split(str, ',');
        }
    }

    /**
     * List of installed packages.
     * 
     * Provided as a comma separated package-key list of installed packages.
     * The parameter overwrites the default marshal and unmarshal methods to
     * allow the String parameter to hold a list of values.
     * 
     * TODO: Replace the type String parameter by StringArray parameter which
     * provides exactly the required functionality (doesn't it?).
     */
    private Parameter m_packages = new StringParameter
        ("waf.config.packages", Parameter.OPTIONAL, new String[0]) {
        @Override
        protected Object unmarshal(String value, ErrorList errs) {
            return array(value);
        }

        @Override
        protected String marshal(Object obj) {
            return StringUtils.join((String[]) obj, ',');
        }
    };

    /**
     * List of parameter values, purpose currently unkown.
     * 
     * The parameter overwrites the default marshal and unmarshal methods to
     * allow the String parameter to hold a list of values.
     */
    private Parameter m_parents = new StringParameter
        ("waf.config.parents", Parameter.OPTIONAL, new URL[0]) {
        @Override
        protected Object unmarshal(String value, ErrorList errs) {
            String[] strs = array(value);
            URL[] result = new URL[strs.length];
            for (int i = 0; i < result.length; i++) {
                try {
                    result[i] = new URL(strs[i]);
                } catch (MalformedURLException e) {
                    errs.add(new ParameterError(this, e));
                }
            }
            if (!errs.isEmpty()) {
                return null;
            }
            return result;
        }

        protected String marshal(Object obj) {
            URL[] urls = (URL[]) obj;
            String[] strs = new String[urls.length];
            for (int i = 0; i < strs.length; i++) {
                strs[i] = urls[i].toExternalForm();
            }
            return StringUtils.join(strs, ',');
        }
    };

    /**
     * Constructs a new and empty config object.
     */
    public RegistryConfig() {
        register(m_packages);
        register(m_parents);
    }

    /**
     * Returns the value of the waf.config.packages parameter.
     *
     * @return the value of the waf.config.packages parameter
     */
    public String[] getPackages() {
        return (String[]) get(m_packages);
    }

    /**
     * Returns the value of the waf.config.parents parameter.
     *
     * @return the value of the waf.config.parents parameter
     */
    public URL[] getParents() {
        return (URL[]) get(m_parents);
    }

}
