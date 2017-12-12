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

import org.libreccm.categorization.Categorization;
import org.libreccm.categorization.CategorizationMarshaller;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryMarshaller;
import org.libreccm.categorization.Domain;
import org.libreccm.categorization.DomainMarshaller;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.categorization.DomainOwnershipMarshaller;
import org.libreccm.core.ResourceType;
import org.libreccm.core.ResourceTypeMarshaller;
import org.libreccm.security.Group;
import org.libreccm.security.GroupMarshaller;
import org.libreccm.security.GroupMembership;
import org.libreccm.security.GroupMembershipMarshaller;
import org.libreccm.security.Permission;
import org.libreccm.security.PermissionMarshaller;
import org.libreccm.security.Role;
import org.libreccm.security.RoleMarshaller;
import org.libreccm.security.RoleMembership;
import org.libreccm.security.RoleMembershipMarshaller;
import org.libreccm.security.User;
import org.libreccm.security.UserMarshaller;

import org.libreccm.web.CcmApplication;
import org.libreccm.web.ApplicationMarshaller;
import org.libreccm.workflow.AssignableTask;
import org.libreccm.workflow.AssignableTaskMarshaller;
import org.libreccm.workflow.TaskAssignment;
import org.libreccm.workflow.TaskAssignmentMarshaller;
import org.libreccm.workflow.TaskComment;
import org.libreccm.workflow.TaskCommentMarshaller;
import org.libreccm.workflow.TaskDependency;
import org.libreccm.workflow.TaskDependencyMarshaller;
import org.libreccm.workflow.Workflow;
import org.libreccm.workflow.WorkflowMarshaller;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Helper to implement the specifics for the importations. Makes source code
 * in the actual test class is shorter and more readable.
 *
 * @author <a href="mailto:tosmers@uni-bremen.de>Tobias Osmers</a>
 * @version created on 12/1/16
 */
@RequestScoped
class ImportHelper {
    
    //private String repoPath = "/home/jensp/pwi/libreccm/ccm/";
    private final String repoPath = "/home/tosmers/Svn/libreccm/";
    private final String projectPath = "ccm_ng/ccm-core/src/test/resources/" +
                    "portation/trunk-iaw-exports";
    private final boolean indentation = false;

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
    @Marshals(Permission.class)
    private PermissionMarshaller permissionMarshaller;
    
    @Inject
    @Marshals(Category.class)
    private CategoryMarshaller categoryMarshaller;
    
    @Inject
    @Marshals(Categorization.class)
    private CategorizationMarshaller categorizationMarshaller;
    
    @Inject
    @Marshals(ResourceType.class)
    private ResourceTypeMarshaller resourceTypeMarshaller;
    
    @Inject
    @Marshals(CcmApplication.class)
    private ApplicationMarshaller applicationMarshaller;
    
    @Inject
    @Marshals(Domain.class)
    private DomainMarshaller domainMarshaller;
    
    @Inject
    @Marshals(DomainOwnership.class)
    private DomainOwnershipMarshaller domainOwnershipMarshaller;

    @Inject
    @Marshals(Workflow.class)
    private WorkflowMarshaller workflowMarshaller;
    
    @Inject
    @Marshals(TaskComment.class)
    private TaskCommentMarshaller taskCommentMarshaller;
    
    @Inject
    @Marshals(AssignableTask.class)
    private AssignableTaskMarshaller assignableTaskMarshaller;

    @Inject
    @Marshals(TaskDependency.class)
    private TaskDependencyMarshaller taskDependencyMarshaller;
    
    @Inject
    @Marshals(TaskAssignment.class)
    private TaskAssignmentMarshaller taskAssignmentMarshaller;



    boolean importUsers() {
        userMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "users.xml",
                indentation);
        return userMarshaller.importFile();
    }

    boolean importGroups() {
        groupMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "groups.xml",
                indentation);
        return groupMarshaller.importFile();
    }

    boolean importGroupMemberships() {
        groupMembershipMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "groupMemberships.xml",
                indentation);
        return groupMembershipMarshaller.importFile();
    }

    boolean importRoles() {
        roleMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "roles.xml",
                indentation);
        return roleMarshaller.importFile();
    }

    boolean importRoleMemberships() {
        roleMembershipMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "roleMemberships.xml",
                indentation);
        return roleMembershipMarshaller.importFile();
    }

    boolean importCategories() {
        categoryMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "categories.xml",
                indentation);
        return categoryMarshaller.importFile();
    }

    boolean importCategorizations() {
        categorizationMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "categorizations.xml",
                indentation);
        return categorizationMarshaller.importFile();
    }

    boolean importResourceTypes() {
        resourceTypeMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "resourceTypes.xml",
                indentation);
        return resourceTypeMarshaller.importFile();
    }

    boolean importCcmApplications() {
        applicationMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "ccmApplications.xml",
                indentation);
        return applicationMarshaller.importFile();
    }

    boolean importDomains() {
        domainMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "domains.xml",
                indentation);
        return domainMarshaller.importFile();
    }

    boolean importDomainOwnerships() {
        domainOwnershipMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "domainOwnerships.xml",
                indentation);
        return domainOwnershipMarshaller.importFile();
    }

    boolean importPermissions() {
        permissionMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "permissions.xml",
                indentation);
        return permissionMarshaller.importFile();
    }


    boolean importWorkflows() {
        workflowMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "workflows.xml",
                indentation);
        return workflowMarshaller.importFile();
    }

    boolean importTaskComments() {
        taskCommentMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "taskComments.xml",
                indentation);
        return taskCommentMarshaller.importFile();
    }

    boolean importAssignableTasks() {
        assignableTaskMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "assignableTasks.xml",
                indentation);
        return assignableTaskMarshaller.importFile();
    }

    boolean importTaskDependencies() {
        taskDependencyMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "taskDependencies.xml",
                indentation);
        return taskDependencyMarshaller.importFile();
    }

    boolean importTaskAssignments() {
        taskAssignmentMarshaller.prepare(
                Format.XML,
                repoPath + projectPath,
                "taskAssignments.xml",
                indentation);
        return taskAssignmentMarshaller.importFile();
    }

}
