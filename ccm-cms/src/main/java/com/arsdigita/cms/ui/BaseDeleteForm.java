/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * @author Justin Ross
 */
public abstract class BaseDeleteForm extends BaseForm
                                     implements FormProcessListener {

    protected final Submit m_delete;
    protected final Submit m_cancel;

    public BaseDeleteForm(final Component message) {
        super("delete", new Label(gz("cms.ui.attention")));

        addComponent(message);

        m_delete = new Submit("delete", gz("cms.ui.delete"));
        addAction(m_delete);

        m_cancel = new Submit("cancel", gz("cms.ui.cancel"));
        addAction(m_cancel);

        addProcessListener(this);
    }

    public BaseDeleteForm(final GlobalizedMessage message) {
        this(new Label(message));
    }

    @Override
    public final boolean isCancelled(final PageState state) {
        return m_cancel.isSelected(state);
    }
}
