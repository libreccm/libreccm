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
package com.arsdigita.bebop.util;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

import com.arsdigita.bebop.Component;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * <p></p>
 *
 * <p>A utility class for walking down a tree of Bebop components and
 * performing some work on each one.</p>
 *
 * <p>Uses a filter to perform the action only on certain components.
 * This filter may be used to skip only individual components or entire
 * subtrees.  The default filter matches all components.</p>
 *
 */
public abstract class Traversal {
    
    private static final Logger LOGGER = LogManager.getLogger(Traversal.class);

    /**
     * If <code>test</code> returns <code>PERFORM_ACTION</code>,
     * then the action is performed on the component and its children.
     * (This is the default.)
     */
    public final static int PERFORM_ACTION = 0;

    /**
     * If <code>test</code> returns <code>SKIP_COMPONENT</code>,
     * then the current component is skipped but its descendants are  still
     * traversed.
     */
    public final static int SKIP_COMPONENT = 1;

    /**
     * If <code>test</code> returns <code>SKIP_SUBTREE</code>,
     * then the current component and all of its descendants are skipped.
     */
    public final static int SKIP_SUBTREE = 2;


    private Set m_visiting = null;

    {
        if (LOGGER.isDebugEnabled()) {
            m_visiting = new HashSet();
        }
    }

    /**
     * Defines the action to be performed on each node.  Users of this
     * class should override this method with behavior for their
     * particular domain.
     *
     * @param c the component on which to perform this action.
     */
    protected abstract void act(Component c);

    /**
     * Invoke {@link #act} on this component, and then do the same for
     * each of its children for which the supplied
     * <code>test</code> condition is true.
     *
     * @param c the component on which to call {@link #act}.  */
    public void preorder(Component c) {
        if (LOGGER.isDebugEnabled() && m_visiting.contains(c)) {
            LOGGER.debug("Cycle detected at component " + c +
                        "; visiting nodes: " + m_visiting);
            throw new IllegalStateException
                ("Component " + c + " is part of a cycle");
        }
        
        LOGGER.debug("---");
        LOGGER.debug("Current component: '{}'", c);
        LOGGER.debug("Visiting: {}", m_visiting);

        //s_log.debug("preorder called for component " + c.toString());

        int flag = test(c);

        if (flag == PERFORM_ACTION) {
            act(c);
        }

        if (flag != SKIP_SUBTREE) {
            if (LOGGER.isDebugEnabled()) {
                m_visiting.add(c);
            }

            for (Iterator i = c.children(); i.hasNext(); ) {
                final Component component = (Component) i.next();
                LOGGER.debug("Calling preorder for component '{}'...", 
                             component);
                LOGGER.debug("---");
                preorder (component);
            }
        }

        if (LOGGER.isDebugEnabled()) {
            m_visiting.remove(c);
        }
    }

    /**
     * The default component test returns <code>PERFORM_ACTION</code>
     * to act on all components in the tree.  Override this method
     * to supply your own component test.
     * @param c the component to test
     * @return by default returns <code>PERFORM_ACTION</code> on all
     * components.
     */
    protected int test(Component c) {
        return PERFORM_ACTION;
    }
}
