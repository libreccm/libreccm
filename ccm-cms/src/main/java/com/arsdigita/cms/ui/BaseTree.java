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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.TreeExpansionEvent;
import com.arsdigita.bebop.event.TreeExpansionListener;
import com.arsdigita.bebop.tree.TreeModelBuilder;

/**
 * A convenience class for CMS trees.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public class BaseTree extends Tree {

    public BaseTree(final TreeModelBuilder builder) {
        super(builder);

        addChangeListener(new Change());
        addTreeExpansionListener(new TreeExpansion());
    }

    private class Change implements ChangeListener {
        public final void stateChanged(final ChangeEvent e) {
            final PageState state = e.getPageState();
            final Object key = BaseTree.this.getSelectedKey(state);

            if (key != null) {
                expand(key.toString(), state);
            }
        }
    }

    private class TreeExpansion implements TreeExpansionListener {
        public final void treeExpanded(final TreeExpansionEvent e) {
            //s_log.error("expanded");
        }

        public final void treeCollapsed(final TreeExpansionEvent e) {
            //s_log.error("collapsed");
        }
    }
}
