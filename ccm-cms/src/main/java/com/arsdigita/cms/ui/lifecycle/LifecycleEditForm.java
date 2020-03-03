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
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;

import org.librecms.lifecycle.LifecycleDefinition;
import org.libreccm.cdi.utils.CdiUtil;

/**
 * This class contains a form component to edit a lifecycle definition.
 *
 * @author Jack Chung
 * @author Xixi D'Moon &lt;xdmoon@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class LifecycleEditForm extends BaseLifecycleForm {

    private final LifecycleDefinitionRequestLocal selectedDefinition;

    LifecycleEditForm(final LifecycleDefinitionRequestLocal selectedDefinition) {
        super("LifecycleEdit", gz("cms.ui.lifecycle.edit"));

        this.selectedDefinition = selectedDefinition;

        getLifecycleName().addValidationListener(
            new LifecycleNameUniqueListener(this, selectedDefinition));

        addInitListener(new InitListener());
        addProcessListener(new ProcessListener());
    }

    private class InitListener implements FormInitListener {

        @Override
        public final void init(final FormSectionEvent event) {
            final PageState state = event.getPageState();
            final LifecycleDefinition cycle = selectedDefinition
                .getLifecycleDefinition(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final LifecycleAdminPaneController controller = cdiUtil.findBean(
                LifecycleAdminPaneController.class
            );
            
            getLifecycleName().setValue(
                state, controller.getLifecycleDefinitionName(cycle)
            );
            getLifecycleDescription().setValue(
                state, controller.getLifecycleDefinitionDescription(cycle)
            );
        }

    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {
            final PageState state = event.getPageState();
            final LifecycleDefinition definition = selectedDefinition
                .getLifecycleDefinition(state);

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final LifecycleAdminPaneController controller = cdiUtil
                .findBean(LifecycleAdminPaneController.class);

            controller.updateLifecycleDefinition(
                definition,
                (String) getLifecycleName().getValue(state),
                (String) getLifecycleDescription().getValue(state));
        }

    }

}
