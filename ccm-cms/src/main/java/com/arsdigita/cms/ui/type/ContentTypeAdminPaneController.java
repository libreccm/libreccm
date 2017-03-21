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
package com.arsdigita.cms.ui.type;

import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentSectionRepository;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;
import org.librecms.lifecycle.LifecycleDefinition;

import java.nio.charset.IllegalCharsetNameException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.Transactional;

/**
 * A controller class for the {@link ContentTypeAdminPane} and its associated
 * classes. For now it only contains methods which require or transaction (which
 * are controlled by the container now).
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
class ContentTypeAdminPaneController {

    @Inject
    private ContentTypeRepository typeRepo;

    @Inject
    private ContentTypesManager typesManager;

    @Inject
    private ContentSectionRepository sectionRepo;

    @Transactional(Transactional.TxType.REQUIRED)
    public List<String[]> getTypeList(final ContentSection section) {
        final List<ContentType> types = typeRepo.findByContentSection(section);

        return types.stream()
            .map(type -> generateListEntry(type))
            .collect(Collectors.toList());
    }

    private String[] generateListEntry(final ContentType type) {
        final String[] entry = new String[2];

        entry[0] = Long.toString(type.getObjectId());

        final ContentTypeInfo typeInfo = typesManager
            .getContentTypeInfo(type.getContentItemClass());
        final ResourceBundle labelBundle = ResourceBundle
            .getBundle(typeInfo.getLabelBundle());
        final String labelKey = typeInfo.getLabelKey();

        entry[1] = labelBundle.getString(labelKey);

        return entry;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<ContentTypeInfo> getNotAssociatedContentTypes(
        final ContentSection section) {

        final List<ContentTypeInfo> availableTypes = typesManager
            .getAvailableContentTypes();
        final List<ContentTypeInfo> associatedTypes = typeRepo
            .findByContentSection(section)
            .stream()
            .map(type -> typesManager.getContentTypeInfo(type))
            .collect(Collectors.toList());

        return availableTypes
            .stream()
            .filter(typeInfo -> !associatedTypes.contains(typeInfo))
            .collect(Collectors.toList());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public LifecycleDefinition getLifecycleDefinition(final ContentType type) {

        final ContentType contentType = typeRepo
            .findById(type.getObjectId())
            .orElseThrow(() -> new IllegalCharsetNameException(String.format(
            "No ContentType with Id %d in the database. "
                + "Where did that ID come from?",
            type.getObjectId())));

        return contentType.getDefaultLifecycle();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<String> getLifecycleDefinitionLabel(final ContentType type,
                                                        final Locale locale) {

        final LifecycleDefinition lifecycleDefinition = getLifecycleDefinition(
            type);

        if (lifecycleDefinition == null) {
            return Optional.empty();
        } else {
            return Optional.of(lifecycleDefinition.getLabel().getValue(locale));
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public WorkflowTemplate getWorkflowTemplate(final ContentType type) {

        final ContentType contentType = typeRepo
            .findById(type.getObjectId())
            .orElseThrow(() -> new IllegalCharsetNameException(String.format(
            "No ContentType with Id %d in the database. "
                + "Where did that ID come from?",
            type.getObjectId())));

        return contentType.getDefaultWorkflow();
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public Optional<String> getWorkflowTemplateName(final ContentType type,
                                                    final Locale locale) {

        final WorkflowTemplate workflowTemplate = getWorkflowTemplate(type);

        if (workflowTemplate == null) {
            return Optional.empty();
        } else {
            return Optional.of(workflowTemplate.getName().getValue(locale));
        }
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<LifecycleDefinition> getLifecycleDefinitions(
        final ContentSection section) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getLifecycleDefinitions());
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public List<WorkflowTemplate> getWorkflowTemplates(
        final ContentSection section) {

        final ContentSection contentSection = sectionRepo
            .findById(section.getObjectId())
            .orElseThrow(() -> new IllegalArgumentException(String.format(
            "No ContentSection with ID %d in the database. "
                + "Where did that ID come from?",
            section.getObjectId())));

        return new ArrayList<>(contentSection.getWorkflowTemplates());
    }

}
