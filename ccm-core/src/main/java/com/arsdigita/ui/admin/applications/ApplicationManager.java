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

import com.arsdigita.bebop.Form;
import com.arsdigita.ui.admin.ApplicationsAdministrationTab;
import java.util.ServiceLoader;
import org.libreccm.web.CcmApplication;

/**
 * Implementations of this class are used by the new 
 * {@link ApplicationsAdministrationTab} to get the forms for editing the
 * configuration of an application and for creating new instances of an 
 * application. 
 * 
 * The {@link ApplicationsAdministrationTab} uses the {@link ServiceLoader} 
 * from the Java Standard Library to find all implementations of this interface.
 * To make implementations of this interface known add an file named 
 * {@code com.arsdigita.ui.admin.applications.ApplicationManager} to the
 * {@code META-INF/services} directory of the module which provides the 
 * application. In this file add a line with the full qualified class name
 * of each implementations of this interface provided by the module.
 * 
 * There a two abstract classes to help you with implementing this class. 
 * {@link AbstractSingletonApplicationManager} is suitable for singleton 
 * applications. {@link AbstractApplicationManager} is for multi-instance 
 * applications.
 * 
 * @param <T>  Type of the application for which this ApplicationManager 
 * provides the administration forms.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: ApplicationManager.java 2275 2013-07-26 14:20:28Z jensp $
 */
public interface ApplicationManager<T extends CcmApplication> {

    /**
     * Used to determine the Applications class for which this
     * manager provides the administration forms.
     * 
     * @return The class of the application for which this 
     * manager provides the administration forms.
     */
    Class<T> getApplication();

    /**
     * Provides a pane with administration forms for the application or for an
     * instance of the application if the application is not a singleton.
     *
     * @return A container containing one or more forms for managing instances 
     * of an application.
     */
    ApplicationInstanceAwareContainer getApplicationAdminForm();

    /**
     * Provides a form for creating new instances of applications. For
     * singleton applications an implementation of this method will return 
     * {@code null}.
     * 
     * @return A form for creating new instances of applications or 
     * {@code null} if the is a singleton.
     */
    Form getApplicationCreateForm();
       
}
