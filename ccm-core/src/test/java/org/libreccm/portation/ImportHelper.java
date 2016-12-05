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

import java.util.List;

/**
 * Helper to implement the specifics for the importations. Makes source code
 * in the actual test class is shorter and more readable.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 12/1/16
 */
class ImportHelper {
    private static String pathName =
            "/home/tosmers/Svn/libreccm/ccm-ng/ccm-core/src/test/resource" +
                    "/portation/trunk-iaw-export";
    private static boolean indentation = false;

    private static String blub = "Hallo";

    static void importCategories() {
        CategoryMarshaller categoryMarshaller = new
                CategoryMarshaller();
        categoryMarshaller.prepare(Format.XML, pathName,
                "categories", indentation);
        List<Category> categories = categoryMarshaller.importFile();
    }

    static void importCategorizations() {
        CategorizationMarshaller categorizationMarshaller = new
                CategorizationMarshaller();
        categorizationMarshaller.prepare(Format.XML, pathName,
                "categorizations", indentation);
        categorizationMarshaller.importFile();
    }

    static void importUsers() {
        UserMarshaller userMarshaller = new UserMarshaller();
        userMarshaller.prepare(Format.XML, pathName,
                "users", indentation);
        userMarshaller.importFile();
    }

    static void importGroups() {
        GroupMarshaller groupMarshaller = new GroupMarshaller();
        groupMarshaller.prepare(Format.XML, pathName,
                "groups", indentation);
        groupMarshaller.importFile();
    }

    static void importGroupMemberships() {
        GroupMembershipMarshaller groupMembershipMarshaller = new
                GroupMembershipMarshaller();
        groupMembershipMarshaller.prepare(Format.XML, pathName,
                "groupMemberships", indentation);
        groupMembershipMarshaller.importFile();
    }

    static void importRoles() {
        RoleMarshaller roleMarshaller = new RoleMarshaller();
        roleMarshaller.prepare(Format.XML, pathName,
                "roles", indentation);
        roleMarshaller.importFile();
    }

    static void importRoleMemberships() {
        RoleMembershipMarshaller roleMembershipMarshaller = new
                RoleMembershipMarshaller();
        roleMembershipMarshaller.prepare(Format.XML, pathName,
                "roleMemberships", indentation);
        roleMembershipMarshaller.importFile();
    }

    static void importWorkflowTemplates() {
        WorkflowTemplateMarshaller workflowTemplateMarshaller = new
                WorkflowTemplateMarshaller();
        workflowTemplateMarshaller.prepare(Format.XML, pathName,
                "workflowTemplates", indentation);
        workflowTemplateMarshaller.importFile();
    }

    static void importWorkflows() {
        WorkflowMarshaller workflowMarshaller = new
                WorkflowMarshaller();
        workflowMarshaller.prepare(Format.XML, pathName,
                "workflows", indentation);
        workflowMarshaller.importFile();
    }

    static void importAssignableTasks() {
        AssignableTaskMarshaller assignableTaskMarshaller = new
                AssignableTaskMarshaller();
        assignableTaskMarshaller.prepare(Format.XML, pathName,
                "assignableTasks", indentation);
        assignableTaskMarshaller.importFile();
    }

    static void importTaskAssignments() {
        TaskAssignmentMarshaller taskAssignmentMarshaller = new
                TaskAssignmentMarshaller();
        taskAssignmentMarshaller.prepare(Format.XML, pathName,
                "taskAssignments", indentation);
        taskAssignmentMarshaller.importFile();
    }

    static void importPermissions() {
        PermissionMarshaller permissionMarshaller = new
                PermissionMarshaller();
        permissionMarshaller.prepare(Format.XML, pathName,
                "permissions", indentation);
        permissionMarshaller.importFile();
    }

}
