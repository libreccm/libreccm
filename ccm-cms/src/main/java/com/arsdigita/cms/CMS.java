/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms;


import org.apache.log4j.Logger;

/**
 * <p>
 * A central location for commonly used CMS services and their accessories.</p>
 *
 * <p>
 * <b>Context.</b> {@link #getContext()} fetches the context record ({@link
 * com.arsdigita.kernel.KernelContext}) of the current thread.</p>
 *
 * @author Daniel Berrange
 * @see com.arsdigita.kernel.Kernel
 * @version $Id$
 */
public abstract class CMS {

    /**
     * Private Logger instance for debugging purpose.
     */
    private static final Logger s_log = Logger.getLogger(CMS.class);

    /**
     * The CMS XML namespace.
     */
    public final static String CMS_XML_NS = "http://www.arsdigita.com/cms/1.0";

    /**
     * Constant string used as key for creating Workspace (content-center) as a
     * legacy application.
     */
    public static final String WORKSPACE_PACKAGE_KEY = "content-center";

    /**
     * Constant string used as key for creating service package as a legacy
     * application.
     */
    public final static String SERVICE_PACKAGE_KEY = "cms-service";

    static final CMSContext s_initialContext = new CMSContext();

    private static final ThreadLocal s_context = new ThreadLocal() {

        @Override
        public Object initialValue() {
            return s_initialContext;
        }

    };

    /**
     * Get the context record of the current thread.
     *
     * @post return != null
     */
    public static final CMSContext getContext() {
        return (CMSContext) s_context.get();
    }

    static final void setContext(CMSContext context) {
        s_context.set(context);
    }

    
}
