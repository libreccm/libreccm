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
package com.arsdigita.mail;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.ErrorList;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.ParameterError;
import com.arsdigita.util.parameter.StringParameter;
import com.arsdigita.util.parameter.URLParameter;
import com.arsdigita.util.UncheckedWrapperException;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * MailConfig
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Revision: #7 $ $Date: 2004/08/16 $
 * @version $Id: MailConfig.java 1513 2007-03-22 09:09:03Z chrisgilbert23 $
 */
public final class MailConfig extends AbstractConfig {

    private Properties m_props;

    private final Parameter m_debug = new BooleanParameter
        ("waf.mail.debug", Parameter.OPTIONAL, Boolean.FALSE);

    private final Parameter m_javamail = new PropertyFileParameter
        ("waf.mail.javamail.configuration", Parameter.OPTIONAL, null);

    /* used by Mail when the user is not logged in. */
    private final Parameter m_defaultFrom = new StringParameter
        ("waf.mail.default_from", Parameter.OPTIONAL, "");

    private final Parameter m_sendHTML = new BooleanParameter
    ("waf.mail.send_html_mail", Parameter.OPTIONAL, Boolean.FALSE);

    
    /**
     * Constructor registers the parameter ands loads values from config file.
     * 
     */
    public MailConfig() {
        register(m_javamail);
        register(m_debug);
        register(m_defaultFrom);
        register(m_sendHTML);

        loadInfo();
    }

    public Properties getJavamail() {
        if (m_props == null) {
            URL propsFile = (URL) get(m_javamail);
            if (propsFile == null) {
                m_props = new Properties();
                m_props.put("mail.transport.protocol", "smtp");
                m_props.put("mail.smtp.host", "localhost");
            } else {
                try {
                    m_props = PropertyFileParameter.getProperties(propsFile);
                } catch (IOException ioe) {
                    throw new UncheckedWrapperException
                        ("unable to retrieve properties file from "
                         + propsFile, ioe);
                }
            }
        }

        return m_props;
    }

    /**
     * 
     * @return 
     */
    public String getDefaultFrom() {
        String from  = (String) get(m_defaultFrom);
//TODO:  usage of arsdigita.web.Web, not sure if the class will be kept in ccm_ng
        
//        if (null == from) 
//           from = "notloggedin@" + Web.getConfig().getServer().getName();

        return from;
    }

    /**
     * 
     * @return 
     */
    public boolean isDebug() {
        return get(m_debug).equals(Boolean.TRUE);
    }

    /**
     * determine whether messages with mime type text/html
     * should be sent as html emails (with plain text alternative) or
     * just sent as translated plain text
     * @return
     */
    public boolean sendHTMLMessageAsHTMLEmail () {
    	return ((Boolean)get(m_sendHTML)).booleanValue();
    }



    /**
     * 
     */
    private static class PropertyFileParameter extends URLParameter {
        PropertyFileParameter(String name, int multiplicity, Object defaalt) {
            super(name, multiplicity, defaalt);
        }

        @Override
        protected void doValidate(Object value, ErrorList errors) {
            super.doValidate(value, errors);

            if (!errors.isEmpty()) {
                return;
            }

            try {
                getProperties((URL) value);
            } catch (IOException ioe) {
                errors.add(new ParameterError(this, ioe));
            }
        }

        public static Properties getProperties(URL url) throws IOException {
            Properties props = new Properties();
            props.load(url.openStream());
            return props;
        }
    }

}
