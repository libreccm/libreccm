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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;

import java.util.List;

/**
 * Migrated from the {@code DataQueryPrintListener} in the old system. Renamed 
 * and refactored to operate on a list.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T> Type of the objects in the list.
 */
public abstract class ListOptionPrintListener<T> implements PrintListener {

    public ListOptionPrintListener() {
    }

    protected abstract List<T> getDataQuery(final PageState state);

    @Override
    public void prepare(final PrintEvent event) {
        final PageState state = event.getPageState();
        final OptionGroup target = (OptionGroup) event.getTarget();
        final List<T> dataQuery = getDataQuery(state);

        dataQuery.forEach(item -> target.addOption(
            new Option(getKey(item),
                       getValue(item))));
    }

    public abstract String getKey(final T object);

    public String getValue(final T object) {
        return getKey(object);
    }

}
