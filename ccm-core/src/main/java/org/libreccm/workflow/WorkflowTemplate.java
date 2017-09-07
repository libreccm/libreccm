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
package org.libreccm.workflow;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.libreccm.core.CcmObject;
import org.libreccm.portation.Portable;
import org.libreccm.security.GroupIdResolver;

import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import java.io.Serializable;

import static org.libreccm.core.CoreConstants.DB_SCHEMA;

/**
 * Objects of these class are used as templates for new workflows. The tasks 
 * in the template are copied when a new workflow is generated.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOW_TEMPLATES", schema = DB_SCHEMA)
@NamedQueries({
        @NamedQuery(
                name = "WorkflowTemplate.findByUuid",
                query = "SELECT T FROM WorkflowTemplate t WHERE t.uuid = :uuid")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = WorkflowTemplateIdResolver.class,
                  property = "uuid")
public class WorkflowTemplate extends Workflow implements Serializable,
        Portable {

    private static final long serialVersionUID = 5770519379144947171L;

    /**
     * A workflow template has no object. Therefore the {@code setObject
     * (CcmObject)} method has been overwritten to throw an
     * {@link UnsupportedOperationException} when called on the workflow
     * template.
     * 
     * @param object 
     */
    @Override
    protected void setObject(final CcmObject object) {
        //throw new UnsupportedOperationException(
        //    "A WorkflowTemplate has no object.");
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof WorkflowTemplate)) {
            return false;
        }
        final WorkflowTemplate other = (WorkflowTemplate) obj;
        return other.canEqual(obj);
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof WorkflowTemplate;
    }

}
