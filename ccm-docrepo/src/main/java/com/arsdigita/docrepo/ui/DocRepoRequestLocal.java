/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.Resource;
import org.libreccm.docrepo.ResourceRepository;

/**
 * Same as the {@link RequestLocal} but overrides the
 * {@code initialValue} uniquely for {@code DocRepo} classes.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version 02/12/2015
 */
public class DocRepoRequestLocal extends RequestLocal
        implements Constants {

    Logger log = Logger.getLogger(DocRepoRequestLocal.class);

    @Override
    protected Object initialValue(PageState state) {
        Long id = (Long) state.getValue(FILE_ID_PARAM);
        File file = null;
        final CdiUtil cdiUtil = new CdiUtil();
        final ResourceRepository resourceRepository;
        try {
            resourceRepository = cdiUtil.findBean(ResourceRepository.class);
            Resource resource = resourceRepository.findById(id);
            file = resource.isFile() ? (File) resource : null;
        } catch(CdiLookupException ex) {
            log.error("Failed to find bean for the ResourceRepository.", ex);
        }
        return file;
    }
}
