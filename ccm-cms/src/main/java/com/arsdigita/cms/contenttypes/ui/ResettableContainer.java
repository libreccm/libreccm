/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.contenttypes.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A container which implements the {@link Resettable} interface and provides
 * other useful methods.
 *
 * @author <a href="mailto:phong@arsdigita.com">Phong Nguyen</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ResettableContainer extends SimpleContainer implements Resettable {

    /**
     * A list of all resettable components in this container
     */
    private final List<Component> resettableComponents = new ArrayList<>();

    /**
     * A list of all components that are not visible by default
     */
    private final List<Component> componentsNotVisibleByDefault
                                      = new ArrayList<>();

    /**
     * Constructs a new, empty {@code RessetableContainer}.
     *
     */
    public ResettableContainer() {
        super();
    }

    /**
     * Constructs a new, empty {@code ResettableContainer}.
     *
     * @param key The key for this container.
     *
     */
    public ResettableContainer(final String key) {
        super();
        setKey(key);
    }

    /**
     * Constructs a new, empty {@code RessetableContainer}. The container will
     * wrap its children in the specified tag.
     *
     * @param tag       The name of the XML element that will be used to wrap
     *                  the children of this container.
     * @param namespace The namespace for the tag.
     *
     */
    public ResettableContainer(final String tag, final String namespace) {
        super(tag, namespace);
    }

    /**
     * Adds a component to container.
     *
     * @param component The component to be added.
     *
     */
    @Override
    public void add(final Component component) {
        add(component, true);
    }

    /**
     * Add a component to this container
     *
     * @param component   The component to be added.
     * @param constraints This parameter is ignored. Child classes should
     *                    override the add method if they wish to provide
     *                    special handling of constraints.
     *
     */
    @Override
    public void add(final Component component, final int constraints) {
        add(component);
    }

    /**
     * Adds the component to this pane with the specified default visibility.
     *
     * @param component
     * @param defaultVisibility The default visibility of this component
     *
     *
     */
    public void add(final Component component,
                    final boolean defaultVisibility) {

        super.add(component);
        if (component instanceof Resettable) {
            resettableComponents.add(component);
        }
        if (!defaultVisibility) {
            componentsNotVisibleByDefault.add(component);
        }
    }

    /**
     * Sets the visibility of all child components to false, except for the
     * component with the specified key.
     *
     * @param state The state of the current request.
     * @param key   The key of the component. There will be no visibility
     *              changes if key is null.
     *
     */
    public void onlyShowComponent(final PageState state,
                                  final String key) {

        if (key == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        final Iterator<Component> iter = children();
        Component child;
        while (iter.hasNext()) {
            child = iter.next();
            child.setVisible(state, key.equals(child.getKey()));
        }
    }

    /**
     * Sets the visibility of all child components to false, except for the
     * specified component.
     *
     * @param state     The state of the current request.
     * @param component The key of the component. There will be no visibility
     *                  changes if {@code component} is null.
     *
     */
    public void onlyShowComponent(final PageState state,
                                  final Component component) {

        if (component == null) {
            return;
        }

        @SuppressWarnings("unchecked")
        final Iterator<Component> iter = children();
        Component child;
        while (iter.hasNext()) {
            child = iter.next();
            child.setVisible(state, child.equals(component));
        }
    }

    /**
     * Resets all resettable components added to this container.
     *
     * @param state The state of the current request.
     *
     */
    @Override
    public void reset(final PageState state) {
        // Reset all resettable components automatically
        final Iterator<Component> iter = resettableComponents.iterator();
        while (iter.hasNext()) {
            ((Resettable) iter.next()).reset(state);
        }
    }

    /**
     * Registers with page that this container belongs to and sets the default
     * visibility of child components.
     *
     * @param page The page this container belongs to.
     *
     */
    @Override
    public void register(final Page page) {
        final Iterator<Component> iter = componentsNotVisibleByDefault
            .iterator();
        while (iter.hasNext()) {
            page.setVisibleDefault(iter.next(), false);
        }
    }

}
