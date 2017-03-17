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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.cms.CMS;
import com.arsdigita.kernel.KernelConfig;

import org.librecms.contentsection.ContentSection;

import com.arsdigita.util.LockableImpl;

import org.libreccm.cdi.utils.CdiUtil;

import java.util.List;

import java.util.NoSuchElementException;

import org.librecms.lifecycle.LifecycleDefinition;

import java.util.Iterator;
import java.util.Locale;

/**
 * Loads all the current lifecycles from the database so that they may be
 * displayed in a list.
 *
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class LifecycleListModelBuilder extends LockableImpl
    implements ListModelBuilder {

    @Override
    public final ListModel makeModel(final com.arsdigita.bebop.List list,
                                     final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final LifecycleAdminPaneController controller = cdiUtil
            .findBean(LifecycleAdminPaneController.class);
        final ContentSection section = CMS.getContext().getContentSection();
        return new Model(controller.getLifecyclesForContentSection(section));
    }

    private class Model implements ListModel {

        private final Iterator<LifecycleDefinition> iterator;
        private LifecycleDefinition currentLifecycleDef;
        private final Locale defaultLocale;

        public Model(final List<LifecycleDefinition> lifecycles) {
            iterator = lifecycles.iterator();
            defaultLocale = KernelConfig.getConfig().getDefaultLocale();
        }
   @Override
        public boolean next() throws NoSuchElementException {
            if (iterator.hasNext()) {
                currentLifecycleDef = iterator.next();
                return true;
            } else {
                return false;
            }
        }

        @Override
        public Object getElement() {
            return currentLifecycleDef.getLabel().getValue(defaultLocale);
        }

        @Override
        public String getKey() {
            return Long.toString(currentLifecycleDef.getDefinitionId());
        }

    }

}
