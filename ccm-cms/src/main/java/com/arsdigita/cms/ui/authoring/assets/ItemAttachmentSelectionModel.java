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
package com.arsdigita.cms.ui.authoring.assets;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.ParameterModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ItemAttachment;
import org.librecms.contentsection.ItemAttachmentManager;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemAttachmentSelectionModel implements SingleSelectionModel<Long>{
    
    private final SingleSelectionModel<Long> model;

    public ItemAttachmentSelectionModel(final LongParameter parameter) {
        this.model = new ParameterSingleSelectionModel<>(parameter);
    }
    
    public ItemAttachmentSelectionModel(final String parameterName) {
        this(new LongParameter(parameterName));
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

    public ItemAttachment<?> getSelectedAttachment(final PageState state) {
        final Long key = getSelectedKey(state);
        final ItemAttachmentManager manager = CdiUtil
        .createCdiUtil()
        .findBean(ItemAttachmentManager.class);
        return manager.findById(key).get();
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
