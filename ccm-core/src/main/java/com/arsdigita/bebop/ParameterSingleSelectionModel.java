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

import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.util.Assert;

/**
 * An implementation of {@link SingleSelectionModel} that uses a state parameter
 * for managing the currently selected key.
 * <p>
 *
 * A typical use case for this class is as follows.
 * <blockquote>
 * <pre>
 * <code>
 * public TheConstructor() {
 *   m_parameter = new StringParameter("my_key");
 *   m_sel = new ParameterSingleSelectionModel(m_parameter);
 * }
 *
 * public void register(Page p) {
 *   p.addComponent(this);
 *   p.addComponentStateParam(this, m_param);
 * }
 * </code>
 * </pre>
 * </blockquote>
 *
 * jensp 2016-02-26: Added generics
 *
 * @param <T> Generics parameter
 *
 * @author Stanislav Freidin
 * @author Jens Pelzetter
 *
 */
public class ParameterSingleSelectionModel<T>
        extends AbstractSingleSelectionModel<T> {

    private final ParameterModel m_parameter;

    /**
     * Constructs a new ParameterSingleSelectionModel.
     *
     * @param m the parameter model that will be used to keep track of the
     * currently selected key
     */
    public ParameterSingleSelectionModel(ParameterModel m) {
        super();

        m_parameter = m;
    }

    /**
     * Returns the key that identifies the selected element.
     *
     * @param state a <code>PageState</code> value
     *
     * @return a <code>String</code> value.
     */
    @Override
    @SuppressWarnings("unchecked")
    public T getSelectedKey(final PageState state) {
        final FormModel model = state.getPage().getStateModel();
        if (model.containsFormParam(m_parameter)) {
            return (T) state.getValue(m_parameter);
        } else {
            return null;
        }
    }

    @Override
    public final ParameterModel getStateParameter() {
        return m_parameter;
    }

    /**
     * Set the selected key.
     *
     * @param state represents the state of the current request
     * @param newKey the new selected key
     */
    @Override
    public void setSelectedKey(final PageState state, final Object newKey) {
        final Object oldKey = getSelectedKey(state);

        if (Assert.isEnabled()) {
            final FormModel model = state.getPage().getStateModel();
            Assert.isTrue(model.containsFormParam(m_parameter),
                          String.format(
                                  "Parameter %s is not part of the FormModel.",
                                  m_parameter.getName()));
        }

        state.setValue(m_parameter, newKey);

        if (newKey == null && oldKey == null) {
            return;
        }

        if (newKey != null && newKey.equals(oldKey)) {
            return;
        }

        fireStateChanged(state);
    }

}
