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
package org.librecms.workflow;

import com.arsdigita.cms.workflow.TaskURLGenerator;

import org.libreccm.core.UnexpectedErrorException;
import org.librecms.contentsection.ContentItem;

import javax.enterprise.context.RequestScoped;

/**
 * Manager for {@link CmsTask}s.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class CmsTaskManager {
    
    /**
     * Retrieves the URL for finishing the task for a certain {@link ContentItem}.
     * 
     * @param item The item.
     * @param task The task.
     * @return The finish URL.
     */
    public String getFinishUrl(final ContentItem item, final CmsTask task) {
        final Class<? extends TaskURLGenerator> urlGeneratorClass = task
            .getTaskType().getUrlGenerator();
        final TaskURLGenerator urlGenerator;
        try {
            urlGenerator = urlGeneratorClass.class.getDeclaredConstructor().newInstance();
        } catch (IllegalAccessException
                 | InstantiationException ex) {
            throw new UnexpectedErrorException(ex);
        }
        
        return urlGenerator.generateURL(item.getObjectId(), task.getTaskId());
    }

}
