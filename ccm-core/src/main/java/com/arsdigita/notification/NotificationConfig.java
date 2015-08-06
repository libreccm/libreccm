/*
 * Copyright (C) 2011 pboy (pboy@barkhof.uni-bremen.de) All Rights Reserved.
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
package com.arsdigita.notification;

import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.util.parameter.IntegerParameter;
import com.arsdigita.util.parameter.Parameter;

import org.apache.log4j.Logger;

/**
 * NotificationConfig
 *
 * @author Peter Boy &lt;pboy@barkhof.uni-bremen.de&gt;
 * @version $Id: NotificationConfig.java   $
 */
public class NotificationConfig extends AbstractConfig {

    /** Private Logger instance.  */
    private static final Logger s_log = Logger.getLogger(NotificationConfig.class);

    /** Private Object to hold one's own instance to return to users. */
    private static NotificationConfig s_conf;

    /**
     * Returns the singleton configuration record for the content section
     * environment.
     *
     * @return The <code>ContentSectionConfig</code> record; it cannot be null
     */
    public static synchronized NotificationConfig getInstance() {
        if (s_conf == null) {
            s_conf = new NotificationConfig();
            s_conf.load();
        }

        return s_conf;
    }


// /////////////////////////////////////////////////////////////////////////////
//
// Set of parameters controlling Overdue Task alerts:
// Currently there is no way to persist it nor to persist on a per section base.
// Therefore Initializer has to create overdue task alert mechanism using a
// configuration applied to every content section.
//
// /////////////////////////////////////////////////////////////////////////////



    /**
     * Request manager's delay in seconds.
     */
    private IntegerParameter m_requestManagerDelay = new IntegerParameter
        ("waf.notification.request_manager_delay", Parameter.REQUIRED,
        new Integer(900));
    
    /**
     * Request manager's period in seconds
     */
    private IntegerParameter m_requestManagerPeriod = new IntegerParameter
        ("waf.notification.request_manager_period", Parameter.REQUIRED,
        new Integer(900));

    /**
     * Digest queue's delay in seconds.
     */
    private IntegerParameter m_digestQueueDelay = new IntegerParameter
        ("waf.notification.digest_queue_delay", Parameter.REQUIRED,
        new Integer(900));

    /**
     * Digest queue's period in seconds
     */
    private IntegerParameter m_digestQueuePeriod = new IntegerParameter
        ("waf.notification.digest_queue_period", Parameter.REQUIRED,
        new Integer(900));

    /**
     * Simple queue's delay in seconds.
     */
    private IntegerParameter m_simpleQueueDelay = new IntegerParameter
        ("waf.notification.simple_queue_delay", Parameter.REQUIRED,
        new Integer(900));

    /**
     * Simple queue's period in seconds
     */
    private IntegerParameter m_simpleQueuePeriod = new IntegerParameter
        ("waf.notification.simple_queue_period", Parameter.REQUIRED,
        new Integer(900));

    /**
     * Constructor.
     * Do not use it directly! Singleton design pattern!
     */
    public NotificationConfig() {
        s_log.debug("Executing NotificationConfig Constructor.");
        register(m_requestManagerDelay);
        register(m_requestManagerPeriod);
        register(m_simpleQueueDelay);
        register(m_simpleQueuePeriod);
        register(m_digestQueueDelay);
        register(m_digestQueuePeriod);
        s_log.debug("NotificationConfig register executed.");
        
        s_log.debug("Executing NotificationConfig loadinfo.");
        loadInfo();
        s_log.debug("Leaving NotificationConfig Constructor.");
    }
    

    /**
     * Retrieve request manager's delay in seconds.
     * @return  delay, in seconds. 
     */
    public int getRequestManagerDelay() {
        s_log.debug("m_requestManagerDelay retrieved.");
        // return ((Integer) get(m_requestManagerDelay)).intValue();
        return 900;
    }

    /**
     * Retrieve request manager's period in seconds
     * @return  period, in seconds
     */
    public int getRequestManagerPeriod() {
        s_log.debug("m_requestManagerPeriod retrieved.");
        // return ((Integer) get(m_requestManagerPeriod)).intValue();
        return 900;
    }

    /**
     * Retrieve digest queue's delay in seconds.
     * @return  delay, in seconds.
     */
    public int getDigestQueueDelay() {
        s_log.debug("m_digestQueueDelay retrieved.");
        // return ((Integer) get(m_digestQueueDelay)).intValue();
        return 900;
    }

    /**
     * Retrieve digest queue's period in seconds
     * @return  period, in seconds
     */
    public int getDigestQueuePeriod() {
        s_log.debug("m_digestQueuePeriod retrieved.");
        // return ((Integer) get(m_digestQueuePeriod)).intValue();
        return 900;
    }

    /**
     * Retrieve simple queue's delay in seconds.
     * @return  delay, in seconds.
     */
    public int getSimpleQueueDelay() {
        s_log.debug("m_simpleQueueDelay retrieved.");
        // return ((Integer) get(m_simpleQueueDelay)).intValue();
        return 900;
    }

    /**
     * Retrieve simple queue's period in seconds
     * @return  period, in seconds
     */
    public int getSimpleQueuePeriod() {
        s_log.debug("m_simpleQueuePeriod retrieved.");
        // return ((Integer) get(m_simpleQueuePeriod)).intValue();
        return 900;
    }

}
