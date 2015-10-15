/*
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.SimpleContainer;
import org.libreccm.web.CcmApplication;

/**
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: ApplicationInstanceAwareContainer.java 2234 2013-06-29 12:41:57Z jensp $
 */
public class ApplicationInstanceAwareContainer extends SimpleContainer {
    
    private CcmApplication appInstance;
    
    public CcmApplication getAppInstance() {
        return appInstance;
    }
    
    public void setAppInstance(final CcmApplication appInstance) {
        this.appInstance = appInstance;
    }
    
}
