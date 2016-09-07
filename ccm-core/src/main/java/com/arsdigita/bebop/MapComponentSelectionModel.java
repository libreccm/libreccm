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
package com.arsdigita.bebop;

import java.util.Map;

import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.Assert;
import com.arsdigita.util.Lockable;

/**
 * A simple implementation of a {@link ComponentSelectionModel}. Uses a map to
 * bind keys to components.
 * <P>
 * This class also encapsulates a {@link SingleSelectionModel}, which is useful
 * if the {@link SingleSelectionModel} comes from a {@link List} or similar
 * class.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author David Lutterkort
 * @author Stanislav Freidin
 */
public class MapComponentSelectionModel<T> implements ComponentSelectionModel<T>,
                                                      Lockable {

    private SingleSelectionModel<T> m_selModel;
    private Map<T, Component> m_components;
    private boolean m_locked;

    /**
     * Constructs a new MapSingleSelectionModel, using selModel as the inner
     * selection model and encapsulating the components map.
     *
     * @param selModel   the single selection model to use to determine the
     *                   currently selected key/component
     * @param components the map of components that can be selected. The map is
     *                   stored by reference. Therefore, changes to the map will
     *                   affect the MapComponentSelectionModel instance.
     */
    public MapComponentSelectionModel(final SingleSelectionModel<T> selModel, 
                                      final Map<T, Component> components
    ) {
        m_components = components;
        m_selModel = selModel;
        m_locked = false;
    }

    /**
     * Constructs a new MapSingleSelectionModel, using a
     * DefaultSingleSelectionModel selection model and encapsulating the
     * components map.
     *
     * @param components the map of components that can be selected. The map is
     *                   stored by reference. Therefore, changes to the map will
     *                   affect the MapComponentSelectionModel instance.
     */
    public MapComponentSelectionModel(final Map<T, Component> components) {
        this(new DefaultSingleSelectionModel<>(), components);
    }

    /**
     * Retrieves the internal SingleSelectionModel.
     *
     * @return the internal SingleSelectionModel.
     */
    public final SingleSelectionModel<T> getSingleSelectionModel() {
        return m_selModel;
    }

    /**
     * Retrieves the internal Map of components. Deprecate ???
     *
     * @return the internal map of components.
     */
    public final Map<T, Component> getComponentsMap() {
        return m_components;
    }

    /**
     * Returns the component that should be used to output the currently
     * selected element.
     *
     * @param state the state of the current request
     *
     * @return the component used to output the selected element.
     */
    @Override
    public Component getComponent(final PageState state) {
        if (!isSelected(state)) {
            return null;
        }
        return m_components.get((m_selModel.getSelectedKey(state)));
    }

    /**
     * Adds another key-component mapping to the model. Passthrough to the
     * underlying Map.
     *
     * @param key the key for the mapping
     * @param component   the component for the mapping
     */
    public void add(final T key, final Component component) {
        Assert.isUnlocked(this);
        m_components.put(key, component);
    }

    // Passthrough to SingleSelectionModel
    /**
     * Returns <code>true</code> if there is a selected element.
     *
     * @param state the state of the current request
     *
     * @return <code>true</code> if there is a selected component
     *         <code>false</code> otherwise.
     */
    @Override
    public boolean isSelected(final PageState state) {
        return m_selModel.isSelected(state);
    }

    /**
     * Returns the key that identifies the selected element.
     *
     * @param state a <code>PageState</code> value
     *
     * @return a <code>String</code> value.
     */
    @Override
    public T getSelectedKey(final PageState state) {
        return m_selModel.getSelectedKey(state);
    }

    /**
     * Sets the selected key. If <code>key</code> is not in the collection of
     * objects underlying this model, an <code>IllegalArgumentException</code>
     * is thrown.
     *
     * @param state the state of the current request
     *
     * @throws IllegalArgumentException if the supplied <code>key</code> cannot
     *                                  be selected in the context of the
     *                                  current request.
     */
    @Override
    public void setSelectedKey(final PageState state, final T key) {
        m_selModel.setSelectedKey(state, key);
    }

    /**
     * Clears the selection.
     *
     * @param state the state of the current request
     *
     * @post ! isSelected(state)
     */
    @Override
    public void clearSelection(final PageState state) {
        m_selModel.clearSelection(state);
    }

    /**
     * Adds a change listener to the model. The listener's
     * <code>stateChanged</code> is called whenever the selected key changes.
     *
     * @param changeListener a listener to notify when the selected key changes
     */
    @Override
    public void addChangeListener(final ChangeListener changeListener) {
        Assert.isUnlocked(this);
        m_selModel.addChangeListener(changeListener);
    }

    /**
     * Removes a change listener from the model.
     *
     * @param changeListener the listener to remove
     */
    @Override
    public void removeChangeListener(final ChangeListener changeListener) {
        Assert.isUnlocked(this);
        m_selModel.removeChangeListener(changeListener);
    }

    /**
     * Returns the state parameter that will be used to keep track of the
     * currently selected key. Typically, the implementing class will simply
     * call:<br>
     * <code><pre>return new StringParameter("foo");</pre></code> This method
     * may return null if a state parameter is not appropriate in the context of
     * the implementing class.
     *
     * @return the state parameter to use to keep track of the currently
     *         selected component.
     */
    @Override
    public ParameterModel getStateParameter() {
        return m_selModel.getStateParameter();
    }

    // implement Lockable
    @Override
    public final void lock() {
        m_locked = true;
    }

    @Override
    public final boolean isLocked() {
        return m_locked;
    }

}
