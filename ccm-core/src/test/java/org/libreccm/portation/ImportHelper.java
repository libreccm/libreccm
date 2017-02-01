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
package org.libreccm.portation;

import org.libreccm.categorization.CategorizationMarshaller;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryMarshaller;
import org.libreccm.security.Group;
import org.libreccm.security.GroupMarshaller;
import org.libreccm.security.GroupMembershipMarshaller;
import org.libreccm.security.PermissionMarshaller;
import org.libreccm.security.RoleMarshaller;
import org.libreccm.security.RoleMembershipMarshaller;
import org.libreccm.security.UserMarshaller;
import org.libreccm.workflow.AssignableTaskMarshaller;
import org.libreccm.workflow.TaskAssignmentMarshaller;
import org.libreccm.workflow.WorkflowMarshaller;
import org.libreccm.workflow.WorkflowTemplateMarshaller;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import java.util.List;
import org.libreccm.categorization.Categorization;
import org.libreccm.security.GroupMembership;
import org.libreccm.security.Permission;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMembership;
import org.libreccm.security.User;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.TaskAssignment;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowTemplate;

/**
 * Helper to implement the specifics for the importations. Makes source code
 * in the actual test class is shorter and more readable.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 12/1/16
 */
@RequestScoped
class ImportHelper {
    private String pathName =
            "/home/jensp/pwi/libreccm/ccm/ccm_ng/ccm-core/src/test/resources/" +
                    "portation/trunk-iaw-exports";
    private boolean indentation = false;

    @Inject
    @Marshals(Category.class)
    private CategoryMarshaller categoryMarshaller;
    @Inject
    @Marshals(Categorization.class)
    private CategorizationMarshaller categorizationMarshaller;
    @Inject
    @Marshals(User.class)
    private UserMarshaller userMarshaller;
    @Inject
    @Marshals(Group.class)
    private GroupMarshaller groupMarshaller;
    @Inject
    @Marshals(GroupMembership.class)
    private GroupMembershipMarshaller groupMembershipMarshaller;
    @Inject
    @Marshals(Role.class)
    private RoleMarshaller roleMarshaller;
    @Inject
    @Marshals(RoleMembership.class)
    private RoleMembershipMarshaller roleMembershipMarshaller;
    @Inject
    @Marshals(WorkflowTemplate.class)
    private WorkflowTemplateMarshaller workflowTemplateMarshaller;
    @Inject
    @Marshals(Workflow.class)
    private WorkflowMarshaller workflowMarshaller;
    @Inject
    @Marshals(AssignableTask.class)
    private AssignableTaskMarshaller assignableTaskMarshaller;
    @Inject
    @Marshals(TaskAssignment.class)
    private TaskAssignmentMarshaller taskAssignmentMarshaller;
    @Inject
    @Marshals(Permission.class)
    private PermissionMarshaller permissionMarshaller;


    public void importCategories() {
        categoryMarshaller.prepare(Format.XML, pathName,
                "categories.xml", indentation);
        List<Category> categories = categoryMarshaller.importFile();
    }

    public void importCategorizations() {
        categorizationMarshaller.prepare(Format.XML, pathName,
                "categorizations.xml", indentation);
        categorizationMarshaller.importFile();
    }

    public void importUsers() {
        userMarshaller.prepare(Format.XML, pathName,
                "users.xml", indentation);
        userMarshaller.importFile();
    }

    public void importGroups() {
        groupMarshaller.prepare(Format.XML, pathName,
                "groups.xml", indentation);
        groupMarshaller.importFile();
    }

    public void importGroupMemberships() {

        groupMembershipMarshaller.prepare(Format.XML, pathName,
                "groupMemberships.xml", indentation);
        groupMembershipMarshaller.importFile();
    }

    public void importRoles() {
        roleMarshaller.prepare(Format.XML, pathName,
                "roles.xml", indentation);
        roleMarshaller.importFile();
    }

    public void importRoleMemberships() {
        roleMembershipMarshaller.prepare(Format.XML, pathName,
                "roleMemberships.xml", indentation);
        roleMembershipMarshaller.importFile();
    }

    public void importWorkflowTemplates() {
        workflowTemplateMarshaller.prepare(Format.XML, pathName,
                "workflowTemplates.xml", indentation);
        workflowTemplateMarshaller.importFile();
    }

    public void importWorkflows() {
        workflowMarshaller.prepare(Format.XML, pathName,
                "workflows.xml", indentation);
        workflowMarshaller.importFile();
    }

    public void importAssignableTasks() {
        assignableTaskMarshaller.prepare(Format.XML, pathName,
                "assignableTasks.xml", indentation);
        assignableTaskMarshaller.importFile();
    }

    public void importTaskAssignments() {
        taskAssignmentMarshaller.prepare(Format.XML, pathName,
                "taskAssignments.xml", indentation);
        taskAssignmentMarshaller.importFile();
    }

    public void importPermissions() {
        permissionMarshaller.prepare(Format.XML, pathName,
                "permissions.xml", indentation);
        permissionMarshaller.importFile();
    }

}
