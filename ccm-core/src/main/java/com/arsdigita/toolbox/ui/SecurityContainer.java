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
package com.arsdigita.toolbox.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.PageState;

import org.libreccm.security.Party;

import com.arsdigita.xml.Element;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.Shiro;



/**
 * <p>A <code>SecurityContainer</code> adds an access check to a
 * {@link com.arsdigita.bebop.Component}. The child component is made
 * invisible if the current user cannot access the it.</p>
 *
 * <p><b>Warning:</b> - A call to <code>setVisible(state, true)</code> does
 * not necessarily mean that <code>isVisible(state)</code> will return
 * true, since the <code>isVisible</code> also takes security checks
 * into account.</p>
 *
 * <p>General usage of the <code>SecurityContainer</code> is as follows:</p>
 *
 * <blockquote><code><pre>
 * MyComponent c = new MyComponent();
 * SecurityContainer sc = new SecurityContainer(c) {
 *   protected boolean canAccess(User user, PageState state) {
 *     return ( user != null );
 *   }
 * };
 * add(sc);
 * </pre></code></blockquote>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author Michael Pih 
 */
public abstract class SecurityContainer extends SimpleContainer {

    /**
     * This default constructor should be followed by calls to
     * <code>add</code>.
     * */
    public SecurityContainer() {}

    /**
     * Create a <code>SecurityContainer</code> around a child component.
     *
     * @param component The child component
     */
    public SecurityContainer(final Component component) {
        add(component);
    }

    /**
     * Is the component visible?
     *
     * @param state The page state
     * @return true if the component is visible, false otherwise
     */
    @Override
    public boolean isVisible(final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final Shiro shiro = cdiUtil.findBean(Shiro.class);
        final Party party = shiro.getUser().get();
        return ( super.isVisible(state) && canAccess(party, state) );
    }

    /**
     * Returns true if the current user can access the child component.
     *
     * @param party The party
     * @param state The page state
     * @return true if the access checks pass, false otherwise
     */
    protected abstract boolean canAccess(final Party party, 
                                         final PageState state);

    /**
     * Generates the XML for the child component if this
     * component is visible.
     *
     * @param state The page state
     * @param parent The parent DOM element
     */
    @Override
    public void generateXML(final PageState state, 
                            final Element parent) {
        if ( isVisible(state) ) {
            super.generateXML(state, parent);
        }
    }

}
