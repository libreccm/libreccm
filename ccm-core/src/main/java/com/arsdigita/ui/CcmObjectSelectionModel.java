/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.ParameterModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.CcmObjectRepository;

/**
 * @param <T>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public class CcmObjectSelectionModel<T extends CcmObject>
        implements SingleSelectionModel<Long> {

    private final Class<T> clazz;
    private final SingleSelectionModel<Long> model;

    public CcmObjectSelectionModel(final LongParameter parameter) {
        this("", parameter);
    }

    public CcmObjectSelectionModel(final String parameterName) {
        this("", new LongParameter(parameterName));
    }

//    public CcmObjectSelectionModel(final SingleSelectionModel<T> model ) {
//        this(null, model);
//    }
//    
    public CcmObjectSelectionModel(final Class<T> clazz,
                                   final String parameterName) {
        this(clazz, new LongParameter(parameterName));
    }

    public CcmObjectSelectionModel(final String className,
                                   final String parameterName) {
        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        model = new ParameterSingleSelectionModel(new LongParameter(
                parameterName));
    }

    public CcmObjectSelectionModel(final Class<T> clazz,
                                   final LongParameter parameter) {
        this(clazz, new ParameterSingleSelectionModel<>(parameter));
    }

    public CcmObjectSelectionModel(final String className,
                                   final LongParameter parameter) {
        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        model = new ParameterSingleSelectionModel<>(parameter);
    }

    public CcmObjectSelectionModel(final Class<T> clazz,
                                   final SingleSelectionModel<Long> model) {
        this.clazz = clazz;
        this.model = model;
    }

    public CcmObjectSelectionModel(final String className,
                                   final SingleSelectionModel<Long> model) {
        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        this.model = model;
    }

    @Override
    public boolean isSelected(final PageState state) {
        return model.isSelected(state);
    }

    @Override
    public Long getSelectedKey(final PageState state) {
        return model.getSelectedKey(state);
    }

    @Override
    public void setSelectedKey(final PageState state, final Long key) {
        model.setSelectedKey(state, key);
    }

    public T getSelectedObject(final PageState state) {
        final Long key = getSelectedKey(state);
        final CcmObjectRepository repository = CdiUtil.createCdiUtil().findBean(
                CcmObjectRepository.class);
        @SuppressWarnings("unchecked")
        final T object = (T) repository.findById(key);
        return object;
    }

    @Override
    public void clearSelection(final PageState state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addChangeListener(final ChangeListener changeListener) {
        model.addChangeListener(changeListener);
    }

    @Override
    public void removeChangeListener(final ChangeListener changeListener) {
        model.addChangeListener(changeListener);;
    }

    @Override
    public ParameterModel getStateParameter() {
        return model.getStateParameter();
    }

}
