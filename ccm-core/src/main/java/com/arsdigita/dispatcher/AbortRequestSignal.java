/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.dispatcher;

/**
 * When thrown, this throwable will percolate up the call stack to
 * the top level to abort all processing of this request.  The top
 * level (BaseDispatcherServlet) will treat it as a normal request
 * and try to commit the transaction
 */

public class AbortRequestSignal extends Error {

    // extending error is a bit of a misnomer but it's what
    // we want: an unchecked exception that won't get caught
    // by catch (Exception).
}
