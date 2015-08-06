/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.formbuilder;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.log4j.Logger;


/**
 * @see com.arsdigita.bebop.Bebop
 * @author Justin Ross
 * @version $Id: FormBuilderConfig.java 1498 2007-03-19 16:22:15Z apevec $
 */
public final class FormBuilderConfig extends AbstractConfig {

    private static final Logger s_log = Logger.getLogger(FormBuilderConfig.class);

    private final Parameter m_actionsHelp;
    private final Parameter m_controlsHelp;
    private final BooleanParameter m_interpolateEmailActionsToAddress;

    public FormBuilderConfig() {
        m_actionsHelp = new StringParameter
            ("waf.formbuilder.actions_help_url", Parameter.REQUIRED, "");

        m_controlsHelp = new StringParameter
            ("waf.formbuilder.controls_help_url", Parameter.REQUIRED, "");

        m_interpolateEmailActionsToAddress = new BooleanParameter
            ("waf.formbuilder.interpolate_email_actions_to_address",
             Parameter.OPTIONAL, Boolean.FALSE);

        register(m_actionsHelp);
        register(m_controlsHelp);
        register(m_interpolateEmailActionsToAddress);

        loadInfo();
    }

    /**
     *  This returns the string that can be used to create the URL to
     *  point to the help page.  If it starts with "/" then it is
     *  assumed to be located on this server.  If it starts with
     *  anything else, it is assumed to be a link to a foreign site.
     *  This can be null if no help link should appear.
     * 
     * NOTE: As of version 6.6 and earlier the help function is not working.
     * Returns null to deactivate the help link.
     */
    public String getActionsHelpLink() {
        //  return (String) get(m_actionsHelp);
        return null;
    }

    /**
     *  This returns the string that can be used to create the URL to
     *  point to the help page.  
     *  This can be null is no help link should appear
     * 
     * NOTE: See deactivated help system above.
     */
    public String getControlsHelpLink() {
        // return (String)get(m_controlsHelp);
        return null;
    }

    public boolean getInterpolateEmailActionsToAddress() {
        return get(m_interpolateEmailActionsToAddress).equals(Boolean.TRUE);
    }
}
