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
package com.arsdigita.bebop.form;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageErrorDisplay;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.util.LockableImpl;

import java.util.Collections;

/**
 * Displays validation errors on the form which were added by the form's
 * validation listener. Does not handle errors in the individual parameters;
 * these errors are handled by the form's template. This class is not
 * a form widget, since it does not produce a value.
 *
 *    @author Stanislav Freidin 
 *    @version $Id$
 *
 */
public class FormErrorDisplay extends PageErrorDisplay  {

    private Form m_form;

    /**
     * Construct a new <code>FormErrorDisplay</code>
     *
     * @param form The parent form whose errors will be displayed by
     *   this widget
     */
    public FormErrorDisplay(Form form) {
        super(new FormErrorModelBuilder(form));
        m_form = form;
    }

    /**
     * Return the form whose errors are to be displayed
     * @return the form whose errors are to be displayed
     */
    public final Form getForm() {
        return m_form;
    }

    /**
     * Determine if there are errors to display
     *
     * @param state the current page state
     * @return true if there are any errors to display; false otherwise
     */
    protected boolean hasErrors(PageState state) {
        FormData data = m_form.getFormData(state);
        return (data != null && data.getErrors().hasNext());
    }

    // A private class which builds a ListModel based on form errors
    private static class FormErrorModelBuilder extends LockableImpl
        implements ListModelBuilder {

        private Form m_form;

        public FormErrorModelBuilder(Form form) {
            super();
            m_form = form;
        }

        public ListModel makeModel(List l, PageState state) {
            FormData data = m_form.getFormData(state);
            if(data == null) {
                return new StringIteratorModel(Collections.EMPTY_LIST.iterator());
            } else {
                return new StringIteratorModel(data.getErrors());
            }
        }
    }

}
