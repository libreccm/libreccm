/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.lifecycle.LifecycleDefinition;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.kernel.KernelConfig;
import com.arsdigita.toolbox.ui.Property;
import com.arsdigita.toolbox.ui.PropertyList;

import org.libreccm.cdi.utils.CdiUtil;

import java.util.Locale;

import org.libreccm.workflow.WorkflowTemplate;
import org.librecms.CmsConstants;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * This component displays basic attributes of a content type including:
 *
 * label, description, default lifecycle definition, default workflow template
 *
 * @author Michael Pih
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class ContentTypePropertyList extends PropertyList {

    private final ContentTypeRequestLocal m_type;

    public ContentTypePropertyList(final ContentTypeRequestLocal type) {
        m_type = type;
    }

    @Override
    protected final List<Property> properties(final PageState state) {
        final List<Property> props = super.properties(state);
        final ContentType type = m_type.getContentType(state);
        final ContentSection section = CMS.getContext().getContentSection();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypesManager typesManager = cdiUtil
            .findBean(ContentTypesManager.class);
        final ContentTypeInfo typeInfo = typesManager.getContentTypeInfo(type);

        final KernelConfig kernelConfig = KernelConfig.getConfig();
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final ResourceBundle labelBundle = ResourceBundle
            .getBundle(typeInfo.getLabelBundle());
        final ResourceBundle descBundle = ResourceBundle
            .getBundle(typeInfo.getDescriptionBundle());

        props.add(new Property(gz("cms.ui.name"),
                               labelBundle.getString(typeInfo.getLabelKey())));
        props.add(new Property(gz("cms.ui.description"),
                               descBundle
                                   .getString(typeInfo.getDescriptionKey())));
//        props.add(new Property(gz("cms.ui.type.parent"),
//                               type.getParent().orElse(null)));
        props.add(new Property(gz("cms.ui.type.lifecycle"),
                               getLifecycle(section, type)));
        props.add(new Property(gz("cms.ui.type.workflow"),
                               getWorkflow(section, type)));

        return props;
    }

    private String getLifecycle(final ContentSection section,
                                final ContentType type) {
        final KernelConfig kernelConfig = KernelConfig.getConfig();
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypeAdminPaneController controller = cdiUtil
            .findBean(ContentTypeAdminPaneController.class);

        final Optional<String> label = controller
            .getLifecycleDefinitionLabel(type, defaultLocale);

        if (label.isPresent()) {
            return label.get();
        } else {
            return lz("cms.ui.type.lifecycle.none");
        }
    }

    private String getWorkflow(final ContentSection section,
                               final ContentType type) {
        final KernelConfig kernelConfig = KernelConfig.getConfig();
        final Locale defaultLocale = kernelConfig.getDefaultLocale();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypeAdminPaneController controller = cdiUtil
            .findBean(ContentTypeAdminPaneController.class);

        final Optional<String> name = controller
            .getWorkflowTemplateName(type, defaultLocale);

        if (name.isPresent()) {
            return name.get();
        } else {
            return lz("cms.ui.type.workflow.none");
        }
    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
