/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package com.arsdigita.cms.contenttypes.ui.mparticle;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.ParameterModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.UnexpectedErrorException;
import org.librecms.contenttypes.MultiPartArticleSection;
import org.librecms.contenttypes.MultiPartArticleSectionRepository;

/**
 * A {@link SingleSelectionModel} implementation for
 * {@link MultiPartArticleSection}s.
 *
 * @param <T>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class SectionSelectionModel<T extends MultiPartArticleSection>
    implements SingleSelectionModel<Long> {

    private final Class<T> clazz;
    private final SingleSelectionModel<Long> model;

    public SectionSelectionModel(final LongParameter parameter) {
        this(MultiPartArticleSection.class.getName(), parameter);
    }

    public SectionSelectionModel(final String parameterName) {
        this(MultiPartArticleSection.class.getName(),
             new LongParameter(parameterName));
    }

    public SectionSelectionModel(final Class<T> clazz,
                                 final String parameterName) {
        this(clazz, new LongParameter(parameterName));
    }

    @SuppressWarnings("unchecked")
    public SectionSelectionModel(final String className,
                                 final String parameterName) {

        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(ex);
        }

        model = new ParameterSingleSelectionModel<>(
            new LongParameter(parameterName));
    }

    public SectionSelectionModel(final Class<T> clazz,
                                 final LongParameter parameter) {
        this(clazz, new ParameterSingleSelectionModel<>(parameter));
    }

    @SuppressWarnings("unchecked")
    public SectionSelectionModel(final String className,
                                 final LongParameter parameter) {

        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(ex);
        }
        model = new ParameterSingleSelectionModel<>(parameter);
    }

    public SectionSelectionModel(final Class<T> clazz,
                                 final SingleSelectionModel<Long> model) {
        this.clazz = clazz;
        this.model = model;
    }

    @SuppressWarnings("unchecked")
    public SectionSelectionModel(final String className,
                                 final SingleSelectionModel<Long> model) {

        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException ex) {
            throw new UnexpectedErrorException(ex);
        }
        this.model = model;
    }

    @Override
    public boolean isSelected(final PageState state) {
        return model.isSelected(state);
    }

    @Override
    public Long getSelectedKey(final PageState state) {
        final Object key = model.getSelectedKey(state);
        if (key == null) {
            return null;
        } else if (key instanceof Long) {
            return (Long) key;
        } else if (key instanceof String) {
            return Long.parseLong((String) key);
        } else {
            return Long.parseLong(key.toString());
        }
    }

    @Override
    public void setSelectedKey(final PageState state, final Long key) {
        model.setSelectedKey(state, key);
    }

    public T getSelectedSection(final PageState state) {
        final Long key = getSelectedKey(state);
        final MultiPartArticleSectionRepository sectionRepo = CdiUtil
            .createCdiUtil()
            .findBean(MultiPartArticleSectionRepository.class);
        @SuppressWarnings("unchecked")
        final T object = (T) sectionRepo.findById(key).get();
        return object;
    }

    @Override
    public void clearSelection(final PageState state) {

        model.clearSelection(state);
    }

    @Override
    public void addChangeListener(final ChangeListener changeListener) {

        model.addChangeListener(changeListener);
    }

    @Override
    public void removeChangeListener(final ChangeListener changeListener) {
        
        model.removeChangeListener(changeListener);
    }

    @Override
    public ParameterModel getStateParameter() {
        
        return model.getStateParameter();
    }

}
