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

/**
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers<\a>
 * @version created the 9/6/17
 */
@RequestScoped
public class WorkflowTemplateIdResolver implements ObjectIdResolver {
    @Override
    public void bindItem(ObjectIdGenerator.IdKey idKey, Object o) {
        // According to the Jackson JavaDoc, this method can be used to keep
        // track of objects directly in a resolver implementation. We don't need
        // this here therefore this method is empty.
    }

    @Override
    public Object resolveId(ObjectIdGenerator.IdKey id) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final WorkflowTemplateRepository workflowTemplateRepository = cdiUtil
                .findBean(WorkflowTemplateRepository.class);

        return workflowTemplateRepository
                .findByUuid(id.key.toString())
                .orElseThrow(() -> new IllegalArgumentException(String
                        .format("No workflow templates with uuid %s in the " +
                                "database.", id.key.toString())));
    }

    @Override
    public ObjectIdResolver newForDeserialization(Object o) {
        return new WorkflowTemplateIdResolver();
    }

    @Override
    public boolean canUseFor(ObjectIdResolver resolverType) {
        return resolverType instanceof WorkflowTemplateIdResolver;
    }
}
