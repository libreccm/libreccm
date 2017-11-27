/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.pages;

import org.libreccm.web.ApplicationCreator;
import org.libreccm.web.ApplicationRepository;
import org.libreccm.web.ApplicationType;
import org.libreccm.web.CcmApplication;

import javax.inject.Inject;

/**
 * Implementation of {@link ApplicationCreator} for the {@link Pages} application.
 * 
 * Used in by the administration UI.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class PagesCreator implements ApplicationCreator<CcmApplication> {

    @Inject
    private ApplicationRepository appRepo;

    @Override
    public CcmApplication createInstance(final String primaryUrl,
                                         final ApplicationType type) {

        return appRepo.retrieveApplicationForPath(primaryUrl).get();
    }

}
