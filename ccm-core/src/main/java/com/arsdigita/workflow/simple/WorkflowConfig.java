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
package com.arsdigita.workflow.simple;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.StringParameter;

/**
 * WorkflowConfig
 *
 * @author Rafael H. Schloming &lt;rhs@mit.edu&gt;
 * @version $Id: WorkflowConfig.java 287 2005-02-22 00:29:02Z sskracic $
 */
public final class WorkflowConfig extends AbstractConfig {

    /** Private Object to hold one's own instance to return to users. */
    private static WorkflowConfig s_config;

    /**
     * Returns the singleton configuration record for the workflow 
     * configuration.
     *
     * @return The <code>ContentSectionConfig</code> record; it cannot be null
     */
    public static synchronized WorkflowConfig getInstance() {
        if (s_config == null) {
            s_config = new WorkflowConfig();
            s_config.load();
        }

        return s_config;
    }


// /////////////////////////////////////////////////////////////////////////////
//
// Set of parameters controlling workflow alerts.
//
// /////////////////////////////////////////////////////////////////////////////

    /** Turn on or off workflow alerts.                                       */
    private BooleanParameter m_alerts = new BooleanParameter
        ("waf.workflow.simple.alerts_enabled", Parameter.OPTIONAL,
         Boolean.TRUE);

    /** Default sender for workflow alerts, e.g. workflow@example.com        */
    private StringParameter m_sender = new StringParameter
        ("waf.workflow.simple.alerts_sender", Parameter.OPTIONAL, null);

    /** 
     * Constructor
     */
    public WorkflowConfig() {
        register(m_alerts);
        register(m_sender);
        loadInfo();
    }

    /**
     * Retrieve whether alerts are to be enabled or not.
     * @return true if alerts are enabled.
     */
    public boolean isAlertsEnabled() {
        return get(m_alerts).equals(Boolean.TRUE);
    }

    /**
     * Retrieve alert senders default mail address.
     * @return
     */
    public String getAlertsSender() {
        return (String) get(m_sender);
    }

}
