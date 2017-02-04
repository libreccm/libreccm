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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.cms.ui.UserAddForm;
import com.arsdigita.cms.ui.UserSearchForm;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.security.User;
import org.libreccm.workflow.AssignableTask;

import com.arsdigita.xml.Element;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.UserRepository;
import org.libreccm.workflow.WorkflowManager;
import org.librecms.CmsConstants;

import java.util.List;

/**
 * Contains forms to search and add users as Task Assignees.
 *
 * @author Uday Mathur
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 */
class TaskAddUser extends SimpleContainer {

    private final TaskRequestLocal m_task;

    private SearchForm m_search;
    private AddForm m_add;

    public TaskAddUser(final TaskRequestLocal task) {
        super();

        m_task = task;

        m_search = new SearchForm();
        add(m_search);

        m_add = new AddForm();
        add(m_add);
    }

    UserSearchForm getSearchForm() {
        return m_search;
    }

    UserAddForm getAddForm() {
        return m_add;
    }

    public final void generateXML(final PageState state,
                                  final Element parent) {
        if (isVisible(state)) {
            final FormData one = m_search.getFormData(state);
            final FormData two = m_add.getForm().getFormData(state);

            if (one != null && (one.isSubmission() || two.isSubmission())) {
                m_search.generateXML(state, parent);
                m_add.generateXML(state, parent);
            } else {
                m_search.generateXML(state, parent);
            }
        }
    }

    private static class SearchForm extends UserSearchForm {

        private final Submit m_cancel;

        public SearchForm() {
            super("user-search");

            m_cancel = new Submit("cancel", gz("cms.ui.cancel"));

            add(m_cancel);
        }

    }

    private class AddForm extends UserAddForm {

        public AddForm() {
            super(m_search.getSearchWidget());
        }

        @Override
        protected final List<User> makeQuery(final PageState state) {

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final UserRepository userRepo = cdiUtil.findBean(
                UserRepository.class);

            final String search = (String) getSearchWidget().getValue(state);

            return userRepo.filtered(search);
        }

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final FormData data = event.getFormData();
            final PageState state = event.getPageState();

            final String[] users = (String[]) data.get("users");

            if (users == null) {
                throw new FormProcessException(new GlobalizedMessage(
                    "cms.ui.workflow.no_users_were_selected",
                    CmsConstants.CMS_BUNDLE));
            } else {
                // Add each checked user to the task.

                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final WorkflowManager workflowManager = cdiUtil.findBean(
                    WorkflowManager.class);
                final UserRepository userRepo = cdiUtil.findBean(UserRepository.class);
                
                final AssignableTask task = m_task.getTask(state);
                User user;

                for (int i = 0; i < users.length; i++) {
                    user = userRepo.findById(Long.parseLong(users[i])).get();

                    //ToDo
                    
                }
            }
        }

    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
