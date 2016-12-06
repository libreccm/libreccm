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
package org.libreccm.pagemodel.bebop;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageFactory;
import com.arsdigita.web.Web;
import org.libreccm.pagemodel.AbstractPageBuilder;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractBebopPageBuilder extends AbstractPageBuilder<Page> {

    public static final String BEBOP = "Bebop";

    @Override
    protected String getType() {
        return BEBOP;
    }

    @Override
    protected void addComponent(final Page page, final Object component) {
        final Component bebopComponent = (Component) component;

        page.add(bebopComponent);
    }

    @Override
    public Page buildPage() {

        final String application = Web.getWebContext().getApplication().
                getPrimaryUrl();
        final Page page = PageFactory.buildPage(application, "");

        addDefaultComponents(page);

        return page;
    }

    public abstract void addDefaultComponents(final Page page);
}
