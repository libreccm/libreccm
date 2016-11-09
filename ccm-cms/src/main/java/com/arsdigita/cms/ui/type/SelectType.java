/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;

import com.arsdigita.cms.ui.CMSForm;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.GlobalizationUtil;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.ContentTypeManager;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

/**
 * This class contains a form component to that allows adding already-existing
 * content type to a content section.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author Jack Chung
 * @author Michael Pih
 */
public class SelectType extends CMSForm implements PrintListener,
                                                   FormSubmissionListener,
                                                   FormProcessListener {

    private final static String TYPES = "types";
    private CheckboxGroup m_typesCheckbox;
    private Submit m_submit;
    private Submit m_cancel;

    public SelectType() {
        super("ContentTypeSelect");

        m_typesCheckbox = new CheckboxGroup(TYPES);
        try {
            m_typesCheckbox.addPrintListener(this);
        } catch (TooManyListenersException e) {
            throw new UncheckedWrapperException("TooManyListeners: " + e
                .getMessage());
        }

        add(new Label(new GlobalizedMessage("cms.ui.type.available_types",
                                            CmsConstants.CMS_BUNDLE)));
        add(m_typesCheckbox);

        SimpleContainer s = new SimpleContainer();
        m_submit = new Submit("submit");
        m_submit.setButtonLabel("Add Selected Content Types");
        s.add(m_submit);
        m_cancel = new Submit("cancel");
        m_cancel.setButtonLabel("Cancel");
        s.add(m_cancel);
        add(s, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);

        addProcessListener(this);
        addSubmissionListener(new TypeSecurityListener());
        addSubmissionListener(this);
    }

    /**
     * Generate a checkbox list of all content type not associated with the
     * current content section
     */
    @Override
    public void prepare(final PrintEvent event) {

        final CheckboxGroup target = (CheckboxGroup) event.getTarget();

        // Get the current content section
        final ContentSection section = CMS.getContext().getContentSection();

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypesManager typesManager = cdiUtil.findBean(
            ContentTypesManager.class);

        final List<ContentTypeInfo> availableTypes = typesManager
            .getAvailableContentTypes();
        final List<String> assignedTypes = section.getContentTypes()
            .stream()
            .map(contentType -> contentType.getContentItemClass())
            .collect(Collectors.toList());

        final List<ContentTypeInfo> notAssignedTypes = availableTypes.stream()
            .filter(type -> assignedTypes.contains(type.getContentItemClass()
            .getName()))
            .collect(Collectors.toList());

        for (final ContentTypeInfo typeInfo : notAssignedTypes) {
            addOption(target, typeInfo);
        }
    }

    private void addOption(final CheckboxGroup target,
                           final ContentTypeInfo typeInfo) {
        final Label label = new Label(new GlobalizedMessage(typeInfo
            .getLabelKey(), typeInfo.getLabelBundle()));
        target.addOption(new Option(typeInfo.getContentItemClass().getName(),
                                    label));
    }

    /**
     * Form submission listener. If the cancel button was pressed, do not
     * process the form.
     *
     * @param event The submit event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void submitted(final FormSectionEvent event) throws
        FormProcessException {
        PageState state = event.getPageState();
        if (isCancelled(state)) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                "cms.ui.cancelled"));
        }
    }

    /**
     * Returns true if this form was cancelled.
     *
     * @return true if the form was cancelled, false otherwise
     */
    @Override
    public boolean isCancelled(final PageState state) {
        return m_cancel.isSelected(state);
    }

    /**
     * Processes form listener which updates a life cycle
     *
     * @param event
     *
     * @throws com.arsdigita.bebop.FormProcessException
     */
    @Override
    public void process(final FormSectionEvent event)
        throws FormProcessException {

        final ContentSection section = CMS.getContext().getContentSection();

        final FormData data = event.getFormData();
        final String[] types = (String[]) data.get(TYPES);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypeManager typeManager = cdiUtil.findBean(
            ContentTypeManager.class);
        final ContentSectionManager sectionManager = cdiUtil.findBean(
            ContentSectionManager.class);

        if (types != null) {
            for (String type : types) {
                sectionManager.addContentTypeToSection(
                    typeManager.classNameToClass(type),
                    section,
                    section.getLifecycleDefinitions().get(0),
                    section.getWorkflowTemplates().get(0));
            }
        }
    }

}
