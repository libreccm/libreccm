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


import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.ParameterModel;

/**
 * Encapsulates the selection of a single object from many
 * possibilities. The <code>SingleSelectionModel</code> allows components to
 * communicate selections without tying the component that manages the
 * selection in the user interface (for example a {@link List}) to the
 * components that consume the selection (such as an edit form that needs
 * to know which object should be edited).
 *
 * <p> Selections are identified by a key, which must identify the
 * underlying object uniquely among all objects that could possibly be
 * selected. For objects stored in a database, this is usually a suitable
 * representation of the object's primary key. The model relies on the
 * key's <code>equals</code> method to compare keys, and requires that the
 * key's <code>toString</code> method produces a representation of the key
 * that can be used in URL strings and hidden form controls.
 *
 * Edit for CCM NG: Added generics.
 * 
 * @param <T> Type for the key
 * 
 * @author David Lutterkort 
 * @author Jens Pelzetter
 */
public interface SingleSelectionModel<T> {

    /**
     * Returns <code>true</code> if there is a selected element.
     *
     * @param state the state of the current request
     * @return <code>true</code> if there is a selected component;
     * <code>false</code> otherwise.
     */
    boolean isSelected(PageState state);

    /**
     * Returns the key that identifies the selected element.
     *
     * @param state a <code>PageState</code> value
     * @return a <code>String</code> value.
     */
    T getSelectedKey(PageState state);

    /**
     * Sets the selected key. If <code>key</code> is not in the collection of
     * objects underlying this model, an
     * <code>IllegalArgumentException</code> is thrown.
     *
     * @param state the state of the current request
     * @param key the selected key
     * @throws IllegalArgumentException if the supplied <code>key</code> can not
     * be selected in the context of the current request.
     */
    void setSelectedKey(PageState state, T key);

    /**
     * Clears the selection.
     *
     * @param state the state of the current request
     * @post ! isSelected(state)
     */
    void clearSelection(PageState state);

    /**
     * Adds a change listener to the model. The listener's
     * <code>stateChanged</code> method is called whenever the selected key changes.
     *
     * @param changeListener a listener to notify when the selected key changes
     */
    void addChangeListener(ChangeListener changeListener);

    /**
     * Removes a change listener from the model.
     *
     * @param changeListener the listener to remove
     */
    void removeChangeListener(ChangeListener changeListener);

    /**
     * Returns the state parameter that will be used to keep track
     * of the currently selected key. Typically, the implementing
     * class will simply call:<br>
     * <code><pre>return new StringParameter("foo");</pre></code><br>
     * This method may return null if a state parameter is not
     * appropriate in the context of the implementing class.
     *
     * @return the state parameter to use to keep
     *         track of the currently selected component, or
     *         null if a state parameter is not appropriate.
     */
    ParameterModel getStateParameter();
}
