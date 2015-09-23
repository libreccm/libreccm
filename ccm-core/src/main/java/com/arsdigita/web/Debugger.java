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
package com.arsdigita.web;

import com.arsdigita.kernel.KernelConfig;

import java.util.ArrayList;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

/**
 * Debugger contains static methods for registering debuggers.
 * Typically, debuggers are written to display the contents of
 * internal CCM data structures e.g., the XML representation of a page
 * prior to transformation. Subclass this class to add a particular
 * type of debugger.
 *
 * @see TransformationDebugger
 *
 * @author Justin Ross
 * @version $Id: Debugger.java 287 2005-02-22 00:29:02Z sskracic $
 */
public abstract class Debugger {

    private static final Logger s_log = Logger.getLogger(Debugger.class);

    public static final String DEBUG_PARAMETER = "debug";
    public static final ThreadLocal s_debuggers = new DebuggerListLocal();

    public static class DebugParameterListener implements ParameterListener {
        public void run(HttpServletRequest sreq, ParameterMap map) {
            if (KernelConfig.getConfig().isDebugEnabled()) {
                final String value = sreq.getParameter(DEBUG_PARAMETER);

                if (value != null) {
                    map.setParameter(DEBUG_PARAMETER, value);
                }
            }
        }
    }

    public static final void addDebugger(Debugger debugger) {
        ArrayList list = (ArrayList) s_debuggers.get();
        list.add(debugger);
    }

    public static final String getDebugging(HttpServletRequest sreq) {
        ArrayList list = (ArrayList) s_debuggers.get();
        Iterator iter = list.iterator();
        StringBuffer buffer = new StringBuffer();

        while (iter.hasNext()) {
            Debugger debug = (Debugger) iter.next();

            if (debug.isRequested(sreq)) {
                buffer.append(debug.debug());
            }
        }

        return buffer.toString();
    }

    public abstract boolean isRequested(HttpServletRequest sreq);

    public abstract String debug();

    private static class DebuggerListLocal extends InternalRequestLocal {
        @Override
        protected Object initialValue() {
            if (KernelConfig.getConfig().isDebugEnabled()) {
                return new ArrayList();
            } else {
                return null;
            }
        }

        @Override
        protected void clearValue() {
            if (KernelConfig.getConfig().isDebugEnabled()) {
                ArrayList list = (ArrayList) get();
                list.clear();
            }
        }
    }
}

