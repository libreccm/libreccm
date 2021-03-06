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
package org.libreccm.workflow;

import com.fasterxml.jackson.annotation.ObjectIdGenerator;
import com.fasterxml.jackson.annotation.ObjectIdResolver;
import org.libreccm.cdi.utils.CdiUtil;

import javax.enterprise.context.RequestScoped;
import java.io.Serializable;

/**
 * 
 * 
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version created on 3/23/17
 */
@Deprecated
@RequestScoped
public class WorkflowIdResolver implements Serializable, ObjectIdResolver {

    private static final long serialVersionUID = 1951611806946125510L;

    @Override
    public void bindItem(final ObjectIdGenerator.IdKey id,
                         final Object pojo) {
        // According to the Jackson JavaDoc, this method can be used to keep
        // track of objects directly in a resolver implementation. We don't need
        // this here therefore this method is empty.
    }

    @Override
    public Object resolveId(final ObjectIdGenerator.IdKey id) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final WorkflowRepository workflowRepository = cdiUtil
            .findBean(WorkflowRepository.class);

        return workflowRepository
            .findByUuid(id.key.toString())
            .orElseThrow(() -> new IllegalArgumentException(String
            .format("No workflows with uuid %s in the "
                        + "database.",
                    id.key.toString())));
    }

    @Override
    public ObjectIdResolver newForDeserialization(final Object context) {
        return new WorkflowIdResolver();
    }

    @Override
    public boolean canUseFor(final ObjectIdResolver resolverType) {
        return resolverType instanceof WorkflowIdResolver;
    }

}
