/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.list.AbstractListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.cms.CMS;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.categorization.DomainManager;
import org.libreccm.categorization.DomainOwnership;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.web.ApplicationManager;
import org.libreccm.web.CcmApplication;
import org.librecms.contentsection.ContentSection;

import java.util.Collection;
import java.util.Iterator;

/**
 * Builds a list of category use contexts for the current
 * content section.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Scott Seago 
 */
class CategoryUseContextModelBuilder extends AbstractListModelBuilder {

    private static String DEFAULT_USE_CONTEXT = "<default>";

    private static final Logger LOGGER = LogManager.getLogger(
            CategoryUseContextModelBuilder.class);

    public final ListModel makeModel(final List list, final PageState state) {
        return new Model();
    }

    private class Model implements ListModel {
        private final Iterator<DomainOwnership> m_roots;
        private DomainOwnership current;

        public Model() {
            final ContentSection section =
                CMS.getContext().getContentSection();

            m_roots = section.getDomains().iterator();
            current = null;
        }

        public boolean next() {
            current = m_roots.next();
            return current != null;
        }

        public Object getElement() {
            return current.getDomain().getRoot();
        }

        public String getKey() {
            return current.getContext() != null ? current.getContext() : DEFAULT_USE_CONTEXT;
        }
    }
}
