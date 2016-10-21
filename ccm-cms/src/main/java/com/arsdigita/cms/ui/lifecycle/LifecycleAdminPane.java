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
package com.arsdigita.cms.ui.lifecycle;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;
import org.librecms.lifecycle.LifecycleDefinition;

import com.arsdigita.cms.ui.BaseAdminPane;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.cms.ui.FormSecurityListener;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSectionManager;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.lifecycle.Lifecycle;
import org.librecms.lifecycle.LifecycleDefinitionRepository;

import java.math.BigDecimal;

/**
 * <p>
 * This class contains the split pane for the lifecycle administration
 * interface.</p>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author Michael Pih
 * @author Jack Chung
 * @author <a href="mailto:jross@redhat.com">Justin Ross</a>
 */
public class LifecycleAdminPane extends BaseAdminPane {

    private static Logger s_log = Logger.getLogger(LifecycleAdminPane.class);

    private final SingleSelectionModel m_model;
    private final LifecycleDefinitionRequestLocal m_definition;

    public LifecycleAdminPane() {
        super(new Label(gz("cms.ui.lifecycles")),
              new LifecycleListModelBuilder());

        m_model = getSelectionModel();
        m_definition = new SelectionRequestLocal();

        // XXX secvis
        //add(new LifecycleAdminContainer(m_addLink));
        setAdd(gz("cms.ui.lifecycle.add"),
               new LifecycleAddForm(m_model));
        setEdit(gz("cms.ui.lifecycle.edit"),
                new LifecycleEditForm(m_definition));
        setDelete(gz("cms.ui.lifecycle.delete"), new DeleteForm());

        setIntroPane(new Label(gz("cms.ui.lifecycle.intro")));
        setItemPane(new LifecycleItemPane(m_definition,
                                          getEditLink(),
                                          getDeleteLink()));

        addAction(new LifecycleAdminContainer(getAddLink()));
    }

    private class SelectionRequestLocal
        extends LifecycleDefinitionRequestLocal {

        @Override
        protected final Object initialValue(final PageState state) {
            final String id = m_model.getSelectedKey(state).toString();

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final LifecycleDefinitionRepository lifecycleDefRepo = cdiUtil
                .findBean(LifecycleDefinitionRepository.class);

            return lifecycleDefRepo.findById(Long.parseLong(id));
        }

    }

    private final class DeleteForm extends BaseDeleteForm {

        DeleteForm() {
            super(new Label(gz("cms.ui.lifecycle.delete_prompt")));

            addSubmissionListener(new FormSecurityListener(
                AdminPrivileges.ADMINISTER_LIFECYLES));
        }

        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final ContentSection section = CMS.getContext().getContentSection();
            final LifecycleDefinition definition = m_definition
                .getLifecycleDefinition(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentSectionManager sectionManager = cdiUtil.findBean(
                ContentSectionManager.class);
            final LifecycleDefinitionRepository lifecycleDefRepo = cdiUtil
                .findBean(LifecycleDefinitionRepository.class);

            sectionManager.removeLifecycleDefinitionFromContentSection(
                definition, section);
            lifecycleDefRepo.delete(definition);

            m_model.clearSelection(state);
        }

    }

}
