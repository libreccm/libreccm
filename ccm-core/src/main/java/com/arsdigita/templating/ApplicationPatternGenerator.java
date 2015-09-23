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

// import com.arsdigita.dispatcher.DispatcherHelper;
// import com.arsdigita.sitenode.SiteNodeRequestContext;
// import com.arsdigita.kernel.SiteNode;
import com.arsdigita.web.Web;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.libreccm.web.Application;
import org.libreccm.web.ApplicationType;

/**
 * Generates a set of pattern values based on the application key, eg
 * content-center, content-section.
 */
public class ApplicationPatternGenerator implements PatternGenerator {

    /**
     * Private logger instance for debugging purpose
     */
    private static final Logger s_log = Logger.getLogger(PatternGenerator.class);

    /**
     * Implementation iof the Interface class.
     *
     * @param key
     * @param req
     *
     * @return
     */
    @Override
    public String[] generateValues(String key,
                                   HttpServletRequest req) {

        s_log.debug("Processing Application with key: " + key);

        final Application app = Web.getWebContext().getApplication();
        if (app != null) {
            String[] returnValue = {((ApplicationType) app.getResourceType())
                .getTitle()};
            s_log.debug("Found application >>" + returnValue
                            + "<< in Application.");
            return returnValue;
        }

        s_log.debug("ApplicationType for >>" + key
                        + "<< not found. Trying SiteNodes instead.");

        throw new IllegalArgumentException(
            "No ApplicationType found for type name " + key);

    }

}
