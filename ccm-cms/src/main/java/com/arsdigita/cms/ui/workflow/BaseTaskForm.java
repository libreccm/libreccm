/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
 *
 * The contents of this file are subject to the CCM Public
 * License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the
 * License at http://www.redhat.com/licenses/ccmpl.html.
 *
 * Software distributed under the License is distributed on an
 * "AS IS" basis, WITHOUT WARRANTY OF ANY KIND, either express
 * or implied. See the License for the specific language
 * governing rights and limitations under the License.
 *
 */
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.cms.ui.BaseForm;

import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.UncheckedWrapperException;

import org.libreccm.workflow.Task;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.CircularTaskDependencyException;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.TaskRepository;
import org.librecms.CmsConstants;
import org.librecms.contentsection.privileges.AdminPrivileges;
import org.librecms.workflow.CmsTaskType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TooManyListenersException;
import java.util.stream.Collectors;

/**
 * @author <a href="jross@redhat.com">Justin Ross</a>
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class BaseTaskForm extends BaseForm {

    final WorkflowRequestLocal m_workflow;

    final TextField m_name;
    final TextArea m_description;
    final OptionGroup m_type;
    final OptionGroup m_deps;

    BaseTaskForm(final String key,
                 final GlobalizedMessage message,
                 final WorkflowRequestLocal workflow) {
        super(key, message);

        m_workflow = workflow;

        m_name = new Name("name", 200, true);
        addField(gz("cms.ui.name"), m_name);

        m_type = new SingleSelect(new IntegerParameter("task_type"));
        addField(gz("cms.ui.workflow.task.type"), m_type);

        try {
            m_type.addPrintListener(new TaskTypePrintListener());
        } catch (TooManyListenersException ex) {
            throw new UncheckedWrapperException(ex);
        }

        m_description = new Description("desc", 4000, true);
        addField(gz("cms.ui.description"), m_description);

        m_deps = new CheckboxGroup("dep");
        addField(gz("cms.ui.workflow.task.dependencies"), m_deps);

        addAction(new Finish());
        addAction(new Cancel());

        addSecurityListener(AdminPrivileges.ADMINISTER_WORKFLOW);
        addValidationListener(new ValidationListener());
    }

    private class ValidationListener implements FormValidationListener {

        @Override
        public final void validate(final FormSectionEvent e)
            throws FormProcessException {
            final String name = (String) m_name.getValue(e.getPageState());

            // XXX do a dupe check here ala commented out code below
        }

    }

    /*
    protected void addValidationListener() {
        addValidationListener(new DataQueryExistsListener(ERROR_MSG) {
                private final String QUERY_NAME =
                    "com.arsdigita.workflow.simple.getTasks";

                public void validate(FormSectionEvent event)
                    throws FormProcessException {
                    String name = (String) m_name.getValue(event.getPageState());
                    if ( name != null ) {
                        super.validate(event);
                    } else {
                        // Do nothing. Let the NotNullValidationListener fire.
                    }
                }

                public DataQuery getDataQuery(FormSectionEvent e) {
                    PageState s = e.getPageState();
                    Session session = SessionManager.getSession();
                    DataQuery query = session.retrieveQuery(QUERY_NAME);
                    Filter f = query.addFilter("lower(taskLabel) = lower(:label)");
                    f.set("label", ((String) m_name.getValue(s)).trim());
                    Filter parentFilter = query.addFilter("taskParentId = :parent_id");
                    parentFilter.set("parent_id", m_processes.getSelectedKey(s));
                    Filter itemFilter = query.addNotEqualsFilter
                        ("taskId", (BigDecimal)m_id.getValue(s));

                    return query;
                }
            });
    }
     */
    // Fix this one too
    private class TaskTypePrintListener implements PrintListener {

        @Override
        public void prepare(final PrintEvent event) {
            final OptionGroup target = (OptionGroup) event.getTarget();

            for (final CmsTaskType type : CmsTaskType.values()) {
                final GlobalizedMessage label = new GlobalizedMessage(
                    String.format("cms.workflow.task_type.%s", type.toString()),
                    CmsConstants.CMS_BUNDLE);

                target.addOption(new Option(type.toString(), new Label(label)));
            }
        }

    }

    /**
     * This method decides which dependencies have to be removed and which ones
     * newly added. Unfortunately we cannot just do "remove all", and add the
     * new ones in since it is possible that Tasks will fire events when
     * dependencies are added and removed.
     *
     * XXX domlay
     */
    final void processDependencies(final Task task,
                                   final String[] selectedDependencies) {
        final List<Task> dependencies = task.getDependentTasks();
        final Map<Long, Task> toAdd = new HashMap<>();
        // Everything is to be removed unless it is in the array.
        final Map<Long, Task> toRemove = dependencies.stream()
            .collect(Collectors.toMap(Task::getTaskId,
                                      dependency -> dependency));

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final TaskRepository taskRepo = cdiUtil.findBean(TaskRepository.class);
        final TaskManager taskManager = cdiUtil.findBean(TaskManager.class);

        Long selectedId;
        Object addedTask;
        if (selectedDependencies != null) {
            for (String selectedDependency : selectedDependencies) {
                selectedId = Long.parseLong(selectedDependency);
                addedTask = toRemove.remove(selectedId);
                if (addedTask == null) {
                    toAdd.put(selectedId, taskRepo.findById(selectedId));
                }
            }
        }

        for (final Task taskToRemove : toRemove.values()) {
            taskManager.removeDependentTask(task, taskToRemove);
        }

        for (final Task taskToAdd : toAdd.values()) {
            try {
            taskManager.addDependentTask(task, taskToAdd);
            } catch(CircularTaskDependencyException ex) {
                throw new UncheckedWrapperException(ex);
            }
        }
    }

}
