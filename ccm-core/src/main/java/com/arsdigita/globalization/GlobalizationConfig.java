/*
 * Copyright (C) 2010 pboy (pboy@barkhof.uni-bremen.de) All Rights Reserved.
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

package com.arsdigita.globalization;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.Parameter;

import org.apache.log4j.Logger;


/**
 * A configuration record for configuration of the core globalization package
 *
 * Accessors of this class may return null. Developers should take care
 * to trap null return values in their code.
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: $
 */
public class GlobalizationConfig extends AbstractConfig {

    /** A logger instance.  */
    private static final Logger s_log = Logger.getLogger(GlobalizationConfig.class);

    /** Singelton config object.  */
    private static GlobalizationConfig s_conf;

    /**
     * Gain a UIConfig object.
     *
     * Singelton pattern, don't instantiate a lifecacle object using the
     * constructor directly!
     * @return
     */
    public static synchronized GlobalizationConfig getConfig() {
        if (s_conf == null) {
            s_conf = new GlobalizationConfig();
            s_conf.load();
        }

        return s_conf;
    }



    /**
     * Default character set for locales not explicitly listed above in the
     * locales parameter.
     * This parameter is read each time the system starts. Therefore, modifications
     * will take effect after a CCM restart.
     */
    // In OLD initializer: DEFAULT_CHARSET as String
    private final Parameter m_defaultCharset =
            new StringParameter(
                    "core.globalization.default_charset",
                    Parameter.REQUIRED, "UTF-8");


    /**
     * Constructs an empty RuntimeConfig object.
     *
     */
    public GlobalizationConfig() {
    // pboy: According to the comment for the getConfig() method a singleton
    // pattern is to be used. Therefore the constructor should be changed to
    // private!
    // private GlobalizationConfig() {
        register(m_defaultCharset);

        loadInfo();

    }

    

    /**
     * Retrieve the default char set to be used for locales not explicitly
     * listed in the supported locales list.
     *
     * @return root page url
     */
    public String getDefaultCharset() {
        return (String)get(m_defaultCharset) ;
    }

}
