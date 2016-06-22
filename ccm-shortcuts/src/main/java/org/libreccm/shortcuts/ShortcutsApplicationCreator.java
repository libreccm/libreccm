/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.shortcuts;

import org.libreccm.web.ApplicationCreator;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Retrieves an instance of the {@code Shortcuts} application.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class ShortcutsApplicationCreator 
    implements ApplicationCreator<CcmApplication>{

    @Inject
    private ApplicationRepository appRepository;
    
    @Override
    public CcmApplication createInstance(final String primaryUrl, 
                                         final ApplicationType type) {
        if (!ShortcutsConstants.SHORTCUTS_PRIMARY_URL.equals(primaryUrl)) {
            throw new IllegalArgumentException(
                "Shortcuts is a singleton application which is mounted at "
                    + "/shortcuts");
        }
        
        return appRepository.retrieveApplicationForPath(primaryUrl);
    }
    
    
    
}
