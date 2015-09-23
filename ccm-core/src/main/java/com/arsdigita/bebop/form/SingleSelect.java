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

import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.parameters.ParameterModel;

/**
 * A class representing an HTML <code>SELECT</code> element with a single selection.
 *
 * @author Karl Goldstein
 * @author Uday Mathur
 * @author Rory Solomon
 * @author Michael Pih
 * @author Christian Brechb&uuml;hler (christian@arsdigita.com)
 * @version $Id$
 */
public class SingleSelect extends Select {

    /**
     * The XML tag.
     *
     * @return The tag to be used for the top level DOM element generated for this type of Widget.
     */
    protected String getElementTag() {
        return BEBOP_SELECT;
    }

    /**
     * Creates a new SingleSelect widget, using a StringParameter model with the given parameter
     * name. Since you can only have one item selected from a SingleSelect, the string parameter
     * returns the value of the selected option.
     * <p>
     * This is equivalent to <code>SingleSelect(new StringParameter(name))</code>
     *
     * @param name the name of the string parameter
     */
    public SingleSelect(final String name) {
        super(new StringParameter(name));
    }

    /**
     * Creates a new SingleSelect widget, using the given parameter model.
     *
     * @param model the parameter model
     */
    public SingleSelect(final ParameterModel model) {
        super(model);
    }

    public SingleSelect(final ParameterModel model,
                        final OptionGroup.SortMode sortMode) {
        super(model, sortMode);
    }

    public SingleSelect(final ParameterModel model,
                        final OptionGroup.SortMode sortMode,
                        final boolean excludeFirst) {
        super(model, sortMode, excludeFirst);
    }

    /**
     * State that this is a single select
     *
     * @return false
     */
    @Override
    public boolean isMultiple() {
        return false;
    }

}
