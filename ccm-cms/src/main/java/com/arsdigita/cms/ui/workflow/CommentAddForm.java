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

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.TextArea;
import com.arsdigita.cms.ui.BaseForm;

import org.librecms.workflow.CmsTask;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.workflow.TaskManager;
import org.libreccm.workflow.TaskRepository;

/**
 * @author Justin Ross
 * @author <a href="mailto:jens.pelzetter">Jens Pelzetter</a>
 */
class CommentAddForm extends BaseForm {

    private static final Logger LOGGER = LogManager.getLogger(
        CommentAddForm.class);

    private final TaskRequestLocal selectedTask;
    private final TextArea comment;

    public CommentAddForm(final TaskRequestLocal task) {
        super("addComment", gz("cms.ui.workflow.task.comment.add"));

        this.selectedTask = task;

        comment = new TextArea("Comment");
        comment.setWrap(TextArea.SOFT);
        comment.setRows(5);
        comment.setCols(40);

        addComponent(comment);

        addAction(new Finish());
        addAction(new Cancel());

        addProcessListener(new ProcessListener());
    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            LOGGER.debug("Processing comment add");

            final PageState state = event.getPageState();
            if (comment.getValue(state) != null
                    && !((String) comment.getValue(state)).isEmpty()) {
                final CmsTask task = selectedTask.getTask(state);
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final TaskFinishFormController controller = cdiUtil
                    .findBean(TaskFinishFormController.class);
                controller.addComment(task, (String) comment.getValue(state));
            }
        }

    }

}
