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
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.CMS;

import org.librecms.lifecycle.LifecycleDefinition;
import org.libreccm.cdi.utils.CdiUtil;

/**
 * @author Michael Pih
 * @author Jack Chung
 * @author <a href="mailto:xdmoon@redhat.com">Xixi D'Moon</a>
 * @author <a href="jross@redhat.com">Justin Ross</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class LifecycleAddForm extends BaseLifecycleForm {

    private final SingleSelectionModel<Long> selectedLifecycle;

    LifecycleAddForm(final SingleSelectionModel<Long> selectedLifecycle) {
        super("LifecycleDefinition", gz("cms.ui.lifecycle.add"));

        this.selectedLifecycle = selectedLifecycle;

        getLifecycleName().addValidationListener(
            new LifecycleNameUniqueListener(this));

        addProcessListener(new ProcessListener());
    }

    private class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final LifecycleAdminPaneController controller = cdiUtil
                .findBean(LifecycleAdminPaneController.class);

            final LifecycleDefinition definition = controller
                .createLifecycleDefinition(
                    CMS.getContext().getContentSection(),
                    (String) getLifecycleName().getValue(state),
                    (String) getLifecycleDescription().getValue(state));

            selectedLifecycle.setSelectedKey(state,
                                             definition.getDefinitionId());
        }

    }

}
