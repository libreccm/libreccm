/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package org.librecms.ui.contentsections;

import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Role;
import org.libreccm.workflow.Workflow;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeManager;
import org.librecms.contentsection.ContentTypeMode;
import org.librecms.contentsection.privileges.TypePrivileges;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;
import org.librecms.lifecycle.LifecycleDefinition;
import org.librecms.ui.CmsAdminMessages;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.Controller;
import javax.mvc.Models;
import javax.transaction.Transactional;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Controller for managing the document types assigned to a
 * {@link ContentSection}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/documenttypes")
public class ConfigurationDocumentTypesController {

    /**
     * Checks admin permissions for {@link ContentSection}s.
     */
    @Inject
    private AdminPermissionsChecker adminPermissionsChecker;

    /**
     * Provides some localized messages.
     */
    @Inject
    private CmsAdminMessages cmsAdminMessages;

    /**
     * Model for the current content section.
     */
    @Inject
    private ContentSectionModel sectionModel;

    /**
     * Provides functions for working with content sections.
     */
    @Inject
    private ContentSectionManager sectionManager;

    /**
     * Common functions for controllers working with content sections.
     */
    @Inject
    private ContentSectionsUi sectionsUi;

    /**
     * Provides function for managing the content types assigned to a content
     * section.
     */
    @Inject
    private ContentTypeManager typeManager;

    /**
     * Provides functions for working with content types.
     *
     */
    @Inject
    private ContentTypesManager typesManager;

    /**
     * Model for displaying a list of document types.
     */
    @Inject
    private DocumentTypesModel documentTypesModel;

    /**
     * Provides functions for working with {@link LocalizedString}s.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * Used to provide data for the views without a named bean.
     */
    @Inject
    private Models models;

    /**
     * Checks permissions.
     */
    @Inject
    private PermissionChecker permissionChecker;

    /**
     * Shows a list of {@link ContentType}s assigned to a content section.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     *
     * @return The template of the list of document types.
     */
    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listDocumentTypes(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerContentTypes(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        documentTypesModel.setAssignedTypes(
            section
                .getContentTypes()
                .stream()
                .map(this::buildDocumentTypesTableRowModel)
                .collect(Collectors.toList())
        );
        documentTypesModel.setAvailableTypes(
            typesManager
                .getAvailableContentTypes()
                .stream()
                .filter(
                    type -> !sectionManager.hasContentType(
                        type.getContentItemClass(), section
                    )
                )
                .map(this::buildDocumentTypeInfoModel)
                .collect(
                    Collectors.toMap(
                        model -> model.getContentItemClass(),
                        model -> model
                    )
                )
        );

        documentTypesModel.setAvailableLifecycles(
            section
                .getLifecycleDefinitions()
                .stream()
                .map(def -> buildLifecycleModel(def, false))
                .collect(
                    Collectors.toMap(
                        model -> model.getUuid(),
                        model -> model.getLabel()
                    )
                )
        );
        documentTypesModel.setAvailableWorkflows(
            section
                .getWorkflowTemplates()
                .stream()
                .map(workflow -> buildWorkflowModel(workflow, false))
                .collect(
                    Collectors.toMap(
                        model -> model.getUuid(),
                        model -> model.getName()
                    )
                )
        );

        return "org/librecms/ui/contentsection/configuration/documenttypes.xhtml";
    }

    /**
     * Adds a document type to a {@link ContentSection}.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param contentItemClass       The class of the content type to add.
     * @param defaultLifecycleUuid   The UUID of the default lifecycle for the
     *                               type.
     * @param defaultWorkflowUuid    The UUID of the default workflow for the
     *                               type.
     *
     * @return A redirect to the list of document types.
     */
    @POST
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String addDocumentType(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @FormParam("contentItemClass") final String contentItemClass,
        @FormParam("defaultLifecycleUuid") final String defaultLifecycleUuid,
        @FormParam("defaultWorkflowUuid") final String defaultWorkflowUuid
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerContentTypes(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<ContentTypeInfo> typeInfo = typesManager
            .getAvailableContentTypes()
            .stream()
            .filter(
                type -> type.getContentItemClass().getName().equals(
                    contentItemClass
                )
            ).findAny();

        if (!typeInfo.isPresent()) {
            models.put(
                "errors",
                cmsAdminMessages.getMessage(
                    "contentsection.configuration.documenttypes.type_not_available",
                    new String[]{sectionIdentifierParam, contentItemClass}
                )
            );

            return listDocumentTypes(sectionIdentifierParam);
        }

        final Optional<LifecycleDefinition> defaultLifecycle = section
            .getLifecycleDefinitions()
            .stream()
            .filter(def -> def.getUuid().equals(defaultLifecycleUuid))
            .findAny();

        if (!defaultLifecycle.isPresent()) {
            models.put(
                "errors",
                cmsAdminMessages.getMessage(
                    "contentsection.configuration.documenttypes.selected_lifecycle_not_available",
                    new String[]{sectionIdentifierParam, defaultLifecycleUuid}
                )
            );

            return listDocumentTypes(sectionIdentifierParam);
        }

        final Optional<Workflow> defaultWorkflow = section
            .getWorkflowTemplates()
            .stream()
            .filter(def -> def.getUuid().equals(defaultWorkflowUuid))
            .findAny();

        if (!defaultWorkflow.isPresent()) {
            models.put(
                "errors",
                cmsAdminMessages.getMessage(
                    "contentsection.configuration.documenttypes.selected_workflow_not_available",
                    new String[]{sectionIdentifierParam, defaultWorkflowUuid}
                )
            );

            return listDocumentTypes(sectionIdentifierParam);
        }

        sectionManager.addContentTypeToSection(
            typeInfo.get().getContentItemClass(),
            section,
            defaultLifecycle.get(),
            defaultWorkflow.get()
        );

        return String.format(
            "redirect:%s/configuration/documenttypes", sectionIdentifierParam
        );
    }

    /**
     * Updates a document type.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param documentTypeParam      the identifier of the type to update.
     * @param defaultLifecycleUuid   The UUID of the new default lifecycle.
     * @param defaultWorkflowUuid    The UUID of the new default workflow.
     * @param roleUuids              The roles that are permitted to use the
     *                               type.
     *
     * @return A redirect to the list of document types.
     */
    @POST
    @Path("/{documentType}")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String updateDocumentType(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("documentType") final String documentTypeParam,
        @FormParam("defaultLifecycleUuid") final String defaultLifecycleUuid,
        @FormParam("defaultWorkflowUuid") final String defaultWorkflowUuid,
        @FormParam("roleUuids") final Set<String> roleUuids
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerContentTypes(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<ContentType> typeResult = section
            .getContentTypes()
            .stream()
            .filter(type -> type.getContentItemClass().equals(documentTypeParam))
            .findAny();

        if (!typeResult.isPresent()) {
            return "org/librecms/ui/contentsection/configuration/documenttype-not-found.xhtml";
        }
        final ContentType type = typeResult.get();

        final Optional<LifecycleDefinition> defaultLifecycle = section
            .getLifecycleDefinitions()
            .stream()
            .filter(def -> def.getUuid().equals(defaultLifecycleUuid))
            .findAny();

        if (!defaultLifecycle.isPresent()) {
            models.put(
                "errors",
                cmsAdminMessages.getMessage(
                    "contentsection.configuration.documenttypes.selected_lifecycle_not_available",
                    new String[]{defaultLifecycleUuid}
                )
            );

            return listDocumentTypes(sectionIdentifierParam);
        }

        final Optional<Workflow> defaultWorkflow = section
            .getWorkflowTemplates()
            .stream()
            .filter(def -> def.getUuid().equals(defaultWorkflowUuid))
            .findAny();

        if (!defaultWorkflow.isPresent()) {
            models.put(
                "errors",
                cmsAdminMessages.getMessage(
                    "contentsection.configuration.documenttypes.selected_workflow_not_available",
                    new String[]{defaultWorkflowUuid}
                )
            );

            return listDocumentTypes(sectionIdentifierParam);
        }

        typeManager
            .setDefaultLifecycle(type, defaultLifecycle.get());
        typeManager.setDefaultWorkflow(
            type, defaultWorkflow.get()
        );

        for (final Role role : section.getRoles()) {
            if (roleUuids.contains(role.getUuid())) {
                typeManager.grantUsageOfType(type, role);
            } else {
                typeManager.denyUsageOnType(type, role);
            }
        }

        return String.format(
            "redirect:%s/configuration/documenttypes/",
            sectionIdentifierParam
        );
    }

    /**
     * Removes a {@link ContentType} from a {@link ContentSection}.
     *
     * @param sectionIdentifierParam The identifier of the current content
     *                               section.
     * @param documentTypeParam      The identifier of the type to remove.
     *
     * @return A redirect to the list of document types.
     */
    @POST
    @Path("/{documentType}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDocumentType(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("documentType") final String documentTypeParam
    ) {
        final Optional<ContentSection> sectionResult = sectionsUi
            .findContentSection(sectionIdentifierParam);
        if (!sectionResult.isPresent()) {
            return sectionsUi.showContentSectionNotFound(sectionIdentifierParam);
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);
        if (!adminPermissionsChecker.canAdministerContentTypes(section)) {
            return sectionsUi.showAccessDenied(
                "sectionIdentifier", sectionIdentifierParam
            );
        }

        final Optional<ContentType> typeResult = section
            .getContentTypes()
            .stream()
            .filter(type -> type.getContentItemClass().equals(documentTypeParam))
            .findAny();

        if (!typeResult.isPresent()) {
            return "org/librecms/ui/contentsection/configuration/documenttype-not-found.xhtml";
        }

        final ContentTypeInfo typeInfo = typesManager.getContentTypeInfo(
            typeResult.get()
        );

        sectionManager.removeContentTypeFromSection(
            typeInfo.getContentItemClass(), section
        );

        return String.format(
            "redirect:%s/configuration/documenttypes/",
            sectionIdentifierParam
        );
    }

    /**
     * Builds a {@link DocumentTypesTableRowModel} for a {@link ContentType}.
     *
     * @param type The content type.
     *
     * @return The {@link DocumentTypesTableRowModel} for the the {@code type}.
     */
    private DocumentTypesTableRowModel buildDocumentTypesTableRowModel(
        final ContentType type
    ) {
        final ContentTypeInfo typeInfo = typesManager
            .getContentTypeInfo(type);

        final DocumentTypesTableRowModel model
            = new DocumentTypesTableRowModel();
        model.setDisplayName(type.getDisplayName());
        model.setUuid(type.getUuid());

        model.setContentItemClass(type.getContentItemClass());

        model.setDefaultLifecycleLabel(
            Optional
                .ofNullable(type.getDefaultLifecycle())
                .map(
                    lifecycle -> globalizationHelper
                        .getValueFromLocalizedString(
                            lifecycle.getLabel()
                        )
                ).orElse("")
        );
        model.setDefaultLifecycleUuid(
            Optional
                .ofNullable(type.getDefaultLifecycle())
                .map(LifecycleDefinition::getUuid)
                .map(uuid -> Arrays.asList(new String[]{uuid}))
                .orElse(Collections.emptyList())
        );
        model.setDefaultWorkflowLabel(
            Optional
                .ofNullable(type.getDefaultWorkflow())
                .map(
                    workflow -> globalizationHelper.getValueFromLocalizedString(
                        workflow.getName()
                    )
                ).orElse("")
        );
        model.setDefaultWorkflowUuid(
            Optional
                .ofNullable(type.getDefaultWorkflow())
                .map(Workflow::getUuid)
                .map(uuid -> Arrays.asList(new String[]{uuid}))
                .orElse(Collections.emptyList())
        );
        final LocalizedTextsUtil labelUtil = globalizationHelper
            .getLocalizedTextsUtil(typeInfo.getLabelBundle());
        model.setLabel(labelUtil.getText(typeInfo.getLabelKey()));

        final LocalizedTextsUtil descUtil = globalizationHelper
            .getLocalizedTextsUtil(typeInfo.getDescriptionBundle());
        model.setDescription(descUtil.getText(typeInfo.getDescriptionKey()));
        model.setMode(
            Optional
                .ofNullable(type.getMode())
                .map(ContentTypeMode::toString)
                .orElse("")
        );

        model.setPermissions(
            type
                .getContentSection()
                .getRoles()
                .stream()
                .map(role -> buildDocumentTypePermissionModel(type, role))
                .collect(Collectors.toList())
        );

        return model;
    }

    /**
     * Builds a {@link DocumentTypeInfoModel} from a {@link ContentTypeInfo}.
     *
     * @param typeInfo The type info.
     *
     * @return A {@link DocumentTypeInfoModel} for the {@code typeInfo}.
     */
    private DocumentTypeInfoModel buildDocumentTypeInfoModel(
        final ContentTypeInfo typeInfo
    ) {
        final DocumentTypeInfoModel model = new DocumentTypeInfoModel();
        model.setContentItemClass(typeInfo.getContentItemClass().getName());

        final LocalizedTextsUtil labelUtil = globalizationHelper
            .getLocalizedTextsUtil(typeInfo.getLabelBundle());
        model.setLabel(labelUtil.getText(typeInfo.getLabelKey()));

        final LocalizedTextsUtil descUtil = globalizationHelper
            .getLocalizedTextsUtil(typeInfo.getDescriptionBundle());
        model.setDescription(descUtil.getText(typeInfo.getDescriptionKey()));

        return model;
    }

    /**
     * Builds a {@link DocumentTypeLifecycleModel} for a
     * {@link LifecycleDefinition}.
     *
     * @param definition       The lifecycle definition.
     * @param defaultLifecycle Is the definition the default lifecycle for the
     *                         content types.
     *
     * @return The {@link DocumentTypeLifecycleModel} for the
     *         {@code definition}.
     */
    private DocumentTypeLifecycleModel buildLifecycleModel(
        final LifecycleDefinition definition,
        final boolean defaultLifecycle
    ) {
        final DocumentTypeLifecycleModel model
            = new DocumentTypeLifecycleModel();
        model.setDefaultLifecycle(defaultLifecycle);
        model.setDefinitionId(definition.getDefinitionId());
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                definition.getDescription()
            )
        );
        model.setLabel(
            globalizationHelper.getValueFromLocalizedString(
                definition.getLabel()
            )
        );
        model.setUuid(definition.getUuid());

        return model;
    }

    /**
     * Builds a {@link DocumentTypeWorkflowModel} for a {@link Workflow}.
     *
     * @param workflow        The workflow (template).
     * @param defaultWorkflow Is the workflow the default workflow for the type?
     *
     * @return A {@link DocumentTypeWorkflowModel} for the {@code workflow}.
     */
    private DocumentTypeWorkflowModel buildWorkflowModel(
        final Workflow workflow, final boolean defaultWorkflow
    ) {
        final DocumentTypeWorkflowModel model = new DocumentTypeWorkflowModel();
        model.setDefaultWorkflow(defaultWorkflow);
        model.setDescription(
            globalizationHelper.getValueFromLocalizedString(
                workflow.getDescription()
            )
        );
        model.setName(
            globalizationHelper.getValueFromLocalizedString(
                workflow.getName()
            )
        );
        model.setUuid(workflow.getUuid());
        model.setWorkflowId(workflow.getWorkflowId());
        return model;
    }

    /**
     * Builds a {@link DocumentTypePermissionModel} for a {@link ContentType}
     * and a {@code Role}.
     *
     * @param type The content type.
     * @param role The role.
     *
     * @return A {@link DocumentTypePermissionModel} for the {@link ContentType}
     *         and {@link Role}.
     */
    private DocumentTypePermissionModel buildDocumentTypePermissionModel(
        final ContentType type, final Role role
    ) {
        final DocumentTypePermissionModel model
            = new DocumentTypePermissionModel();
        model.setRoleName(role.getName());
        model.setRoleUuid(role.getUuid());
        model.setCanUse(
            permissionChecker.isPermitted(TypePrivileges.USE_TYPE, type, role)
        );
        return model;
    }

}
