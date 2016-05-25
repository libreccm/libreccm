/*
 * Copyright (c) 2010 Jens Pelzetter, ScientificCMS.org team
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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Helper class for managing the implementations of the {@link ApplicationManager} interface
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: ApplicationManagers.java 2294 2013-08-05 18:39:46Z jensp $
 */
public class ApplicationManagers {
    
    private Map<String, ApplicationManager<?>> appManagers = new HashMap<String, ApplicationManager<?>>();
    
    /**
     * The one and only instance of this class. The pattern used here ensures that
     * the instance is created at the first access, but not earlier.
     */
    private static class Instance {
        private static final ApplicationManagers INSTANCE = new ApplicationManagers();
    }
    
     /**
     * Private constructor to ensure that no instances of this class can be created.
     */
    private ApplicationManagers() {
        //Nothing
    }
    
      /**
     * @return The instance of this class.
     */
    public static ApplicationManagers getInstance() {
        return Instance.INSTANCE;
    }
    
    public static void register(final ApplicationManager<?> appManager) {
        getInstance().registerApplicationManager(appManager);
    }
    
    public void registerApplicationManager(final ApplicationManager<?> appManager) {
        appManagers.put(appManager.getApplication().getName(), appManager);
    }
    
    protected Map<String, ApplicationManager<?>> getApplicationManagers() {
        return Collections.unmodifiableMap(appManagers);
    }
    
    protected ApplicationManager<?> getApplicationManager(final String appClassName) {
        return appManagers.get(appClassName);
    }
    
}
