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
package com.arsdigita.cms.ui.workflow;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;
import java.util.List;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentSection;
import org.libreccm.workflow.Task;
import org.libreccm.workflow.WorkflowTemplate;

/**
 * Builds a list of workflow templates registered to the current content
 * section.
 *
 * @author Uday Mathur (umathur@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class WorkflowsOptionPrintListener implements PrintListener {

    protected List<WorkflowTemplate> getCollection(final PageState state) {
        final ContentSection section = getContentSection(state);

        return section.getWorkflowTemplates();
    }

    protected ContentSection getContentSection(final PageState state) {
        return CMS.getContext().getContentSection();
    }

    @Override
    public void prepare(final PrintEvent event) {
        final PageState state = event.getPageState();

        final OptionGroup target = (OptionGroup) event.getTarget();
        target.clearOptions();

        final List<WorkflowTemplate> templates = getCollection(state);

        for (final WorkflowTemplate template : templates) {
            target.addOption(new Option(
                    Long.toString(template.getWorkflowId()),
                    template.getName().getValue(KernelConfig
                            .getConfig()
                            .getDefaultLocale())));
        }
    }

}
