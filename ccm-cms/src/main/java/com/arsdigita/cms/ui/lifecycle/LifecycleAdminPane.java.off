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
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.SecurityManager;
import com.arsdigita.cms.lifecycle.LifecycleDefinition;
import com.arsdigita.cms.ui.BaseAdminPane;
import com.arsdigita.cms.ui.BaseDeleteForm;
import com.arsdigita.cms.ui.FormSecurityListener;
import org.apache.log4j.Logger;

import java.math.BigDecimal;

/**
 * <p>This class contains the split pane for the lifecycle administration
 * interface.</p>
 *
 * @author Michael Pih
 * @author Jack Chung
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: LifecycleAdminPane.java 1942 2009-05-29 07:53:23Z terry $
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
        protected final Object initialValue(final PageState state) {
            final String id = m_model.getSelectedKey(state).toString();

            return new LifecycleDefinition(new BigDecimal(id));
        }
    }

    private final class DeleteForm extends BaseDeleteForm {
        DeleteForm() {
            super(new Label(gz("cms.ui.lifecycle.delete_prompt")));

            addSubmissionListener
                (new FormSecurityListener(SecurityManager.LIFECYCLE_ADMIN));
        }

        public final void process(final FormSectionEvent e)
                throws FormProcessException {
            final PageState state = e.getPageState();
            final ContentSection section =
                CMS.getContext().getContentSection();
            final LifecycleDefinition definition =
                m_definition.getLifecycleDefinition(state);

            section.removeLifecycleDefinition(definition);
            section.save();

            definition.delete();

            m_model.clearSelection(state);
        }
    }
}
