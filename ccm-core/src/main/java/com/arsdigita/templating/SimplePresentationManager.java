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
package com.arsdigita.templating;

/**
 * An Implementation of a Presentation Manager as specified by the
 * {@link PresentationManager} interface which may be used as a default.
 *
 * As bebop is currently the only one providing a presenation layer it simply
 * links to the bebop implementation. At the same time it makes shure an
 * implementation exists which can be used as default in the templating
 * configuration registry.
 */
/* NON Javadoc comment:
 * Used to be deprecated in version 6.6.0. Reverted to non-deprecated in version
 * 6.6.0 release 3. Package templating provides the basic mechanism for CCM
 * templating system an should provide an implementation of the Presentation
 * Manager interface to be complete.  
 * @ deprecated Use {@link com.arsdigita.bebop.page.PageTransformer}
 * instead
 */
public class SimplePresentationManager
        extends com.arsdigita.bebop.page.PageTransformer 
        implements PresentationManager{
    // Empty
}
