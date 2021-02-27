/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui.contentsections;

import org.libreccm.api.Identifier;
import org.libreccm.api.IdentifierParser;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedTextsUtil;
import org.libreccm.security.AuthorizationRequired;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.libreccm.security.Role;
import org.libreccm.workflow.Workflow;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeManager;
import org.librecms.contentsection.ContentTypeMode;
import org.librecms.contentsection.privileges.AdminPrivileges;
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
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Controller
@Path("/{sectionIdentifier}/configuration/documenttypes")
public class ConfigurationDocumentTypesController {

    @Inject
    private CmsAdminMessages cmsAdminMessages;

    @Inject
    private ContentSectionModel sectionModel;

    @Inject
    private ContentSectionManager sectionManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Inject
    private ContentTypeManager typeManager;

    @Inject
    private ContentTypesManager typesManager;

    @Inject
    private DocumentTypeModel documentTypeModel;

    @Inject
    private DocumentTypesModel documentTypesModel;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Inject
    private IdentifierParser identifierParser;

    @Inject
    private Models models;

    @Inject
    private PermissionChecker permissionChecker;

    @Inject
    private PermissionManager permissionManager;

    @GET
    @Path("/")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String listDocumentTypes(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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

//    @GET
//    @Path("/{documentType}")
//    public String showDocumentType(
//        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
//        @PathParam("documentType") final String documentTypeParam
//    ) {
//        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
//            sectionIdentifierParam
//        );
//
//        final Optional<ContentSection> sectionResult;
//        switch (sectionIdentifier.getType()) {
//            case ID:
//                sectionResult = sectionRepo.findById(
//                    Long.parseLong(
//                        sectionIdentifier.getIdentifier()
//                    )
//                );
//                break;
//            case UUID:
//                sectionResult = sectionRepo.findByUuid(
//                    sectionIdentifier.getIdentifier()
//                );
//                break;
//            default:
//                sectionResult = sectionRepo.findByLabel(
//                    sectionIdentifier.getIdentifier()
//                );
//                break;
//        }
//
//        if (!sectionResult.isPresent()) {
//            models.put("sectionIdentifier", sectionIdentifier);
//            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
//        }
//        final ContentSection section = sectionResult.get();
//        sectionModel.setSection(section);
//
//        if (!permissionChecker.isPermitted(
//            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
//        )) {
//            models.put("sectionIdentifier", sectionIdentifier);
//            return "org/librecms/ui/contentsection/access-denied.xhtml";
//        }
//
//        final Optional<ContentType> typeResult = section
//            .getContentTypes()
//            .stream()
//            .filter(type -> type.getContentItemClass().equals(documentTypeParam))
//            .findAny();
//
//        if (!typeResult.isPresent()) {
//            return "org/librecms/ui/contentsection/configuration/documenttype-not-found.xhtml";
//        }
//
//        final ContentType type = typeResult.get();
//
//        documentTypeModel.setContentItemClass(sectionIdentifierParam);
//        documentTypeModel.setDescriptions(
//            type
//                .getDescription()
//                .getValues()
//                .entrySet()
//                .stream()
//                .collect(Collectors.toMap(
//                    entry -> entry.getKey().toString(),
//                    entry -> entry.getValue()
//                ))
//        );
//        documentTypeModel.setLabels(
//            type
//                .getLabel()
//                .getValues()
//                .entrySet()
//                .stream()
//                .collect(Collectors.toMap(
//                    entry -> entry.getKey().toString(),
//                    entry -> entry.getValue()
//                ))
//        );
//
//        final LifecycleDefinition defaultLifecycle = type.getDefaultLifecycle();
//        documentTypeModel.setLifecycles(
//            section
//                .getLifecycleDefinitions()
//                .stream()
//                .map(
//                    def -> buildLifecycleModel(
//                        def, def.equals(defaultLifecycle)
//                    )
//                ).collect(Collectors.toList())
//        );
//
//        final Workflow defaultWorkflow = type.getDefaultWorkflow();
//        documentTypeModel.setWorkflows(
//            section
//                .getWorkflowTemplates()
//                .stream()
//                .map(
//                    template -> buildWorkflowModel(
//                        template, template.equals(defaultWorkflow)
//                    )
//                ).collect(Collectors.toList())
//        );
//
//        return "org/librecms/ui/contentsection/configuration/documenttype.xhtml";
//    }
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
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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

//    @POST
//    @Path("/{documentType}/default-workflow")
//    public String setDefaultWorkflow(
//        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
//        @PathParam("documentType") final String documentTypeParam,
//        @FormParam("defaultWorkflowUuid") final String defaultWorkflowUuid
//    ) {
//        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
//            sectionIdentifierParam
//        );
//
//        final Optional<ContentSection> sectionResult;
//        switch (sectionIdentifier.getType()) {
//            case ID:
//                sectionResult = sectionRepo.findById(
//                    Long.parseLong(
//                        sectionIdentifier.getIdentifier()
//                    )
//                );
//                break;
//            case UUID:
//                sectionResult = sectionRepo.findByUuid(
//                    sectionIdentifier.getIdentifier()
//                );
//                break;
//            default:
//                sectionResult = sectionRepo.findByLabel(
//                    sectionIdentifier.getIdentifier()
//                );
//                break;
//        }
//
//        if (!sectionResult.isPresent()) {
//            models.put("sectionIdentifier", sectionIdentifier);
//            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
//        }
//        final ContentSection section = sectionResult.get();
//        sectionModel.setSection(section);
//
//        if (!permissionChecker.isPermitted(
//            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
//        )) {
//            models.put("sectionIdentifier", sectionIdentifier);
//            return "org/librecms/ui/contentsection/access-denied.xhtml";
//        }
//
//        final Optional<ContentType> typeResult = section
//            .getContentTypes()
//            .stream()
//            .filter(type -> type.getContentItemClass().equals(documentTypeParam))
//            .findAny();
//
//        if (!typeResult.isPresent()) {
//            return "org/librecms/ui/contentsection/configuration/documenttype-not-found.xhtml";
//        }
//
//        final Optional<Workflow> defaultWorkflow = section
//            .getWorkflowTemplates()
//            .stream()
//            .filter(def -> def.getUuid().equals(defaultWorkflowUuid))
//            .findAny();
//
//        if (!defaultWorkflow.isPresent()) {
//            models.put(
//                "errors",
//                cmsAdminMessages.getMessage(
//                    "contentsection.configuration.documenttypes.selected_workflow_not_available",
//                    new String[]{defaultWorkflowUuid}
//                )
//            );
//
//            return listDocumentTypes(sectionIdentifierParam);
//        }
//
//        typeManager.setDefaultWorkflow(
//            typeResult.get(), defaultWorkflow.get()
//        );
//
//        return String.format(
//            "redirect:%s/configuration/documenttypes/%s",
//            sectionIdentifierParam,
//            documentTypeParam
//        );
//    }
    @POST
    @Path("/{documentType}/@remove")
    @AuthorizationRequired
    @Transactional(Transactional.TxType.REQUIRED)
    public String removeDocumentType(
        @PathParam("sectionIdentifier") final String sectionIdentifierParam,
        @PathParam("documentType") final String documentTypeParam
    ) {
        final Identifier sectionIdentifier = identifierParser.parseIdentifier(
            sectionIdentifierParam
        );

        final Optional<ContentSection> sectionResult;
        switch (sectionIdentifier.getType()) {
            case ID:
                sectionResult = sectionRepo.findById(
                    Long.parseLong(
                        sectionIdentifier.getIdentifier()
                    )
                );
                break;
            case UUID:
                sectionResult = sectionRepo.findByUuid(
                    sectionIdentifier.getIdentifier()
                );
                break;
            default:
                sectionResult = sectionRepo.findByLabel(
                    sectionIdentifier.getIdentifier()
                );
                break;
        }

        if (!sectionResult.isPresent()) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/contentsection-not-found.xhtml";
        }
        final ContentSection section = sectionResult.get();
        sectionModel.setSection(section);

        if (!permissionChecker.isPermitted(
            AdminPrivileges.ADMINISTER_CONTENT_TYPES, section
        )) {
            models.put("sectionIdentifier", sectionIdentifier);
            return "org/librecms/ui/contentsection/access-denied.xhtml";
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
