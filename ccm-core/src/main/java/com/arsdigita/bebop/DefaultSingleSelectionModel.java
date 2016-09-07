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

/**
 * @see SingleSelectionModel
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @author unknown
 */
public class DefaultSingleSelectionModel<T>
    extends AbstractSingleSelectionModel<T> {

    private final RequestLocal m_key;

    public DefaultSingleSelectionModel() {
        super();
        m_key = new RequestLocal();
    }

    @Override
    @SuppressWarnings("unchecked")
    public T getSelectedKey(final PageState state) {
        return (T) m_key.get(state);
    }

    @Override
    public ParameterModel getStateParameter() {
        return null;
    }

    @Override
    public void setSelectedKey(final PageState state, final T key) {
        if (key == null) {
            if (getSelectedKey(state) != null) {
                m_key.set(state, null);
                fireStateChanged(state);
            }
        } else if (!key.equals(getSelectedKey(state))) {
            m_key.set(state, key);
            fireStateChanged(state);
        }
    }

}
