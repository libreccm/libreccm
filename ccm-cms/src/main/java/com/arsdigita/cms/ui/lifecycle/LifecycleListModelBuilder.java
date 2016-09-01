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
import org.librecms.contentsection.ContentSection;
import com.arsdigita.util.LockableImpl;
import java.util.List;

import java.util.NoSuchElementException;
import org.librecms.lifecycle.LifecycleDefinition;

/**
 * Loads all the current lifecycles from the database so that they may be
 * displayed in a list.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author <a href="mailto:pihman@arsdigita.com">Michael Pih</a>
 * @author <a href="mailto:flattop@arsdigita.com">Jack Chung</a>
 */
public final class LifecycleListModelBuilder extends LockableImpl
        implements ListModelBuilder {

    @Override
    public final ListModel makeModel(final com.arsdigita.bebop.List list,
                                     final PageState state) {
        return new Model(state);
    }

    private class Model implements ListModel {

        private final List<LifecycleDefinition> m_cycles;
        private int index = -1;

        public Model(final PageState state) {
            m_cycles = getCollection(state);
        }

        private List<LifecycleDefinition> getCollection(final PageState state) {
            final ContentSection section = CMS.getContext().getContentSection();

            final List<LifecycleDefinition> cycles = section.getLifecycleDefinitions();

            return cycles;
        }

        @Override
        public boolean next() throws NoSuchElementException {
            index++;
            return index < m_cycles.size();
        }

        @Override
        public Object getElement() {
            return m_cycles.get(index).getLabel();
        }

        @Override
        public String getKey() {
            return Long.toString(m_cycles.get(index).getDefinitionId());
        }
    }
}
