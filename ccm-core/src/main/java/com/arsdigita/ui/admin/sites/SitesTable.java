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
package com.arsdigita.ui.admin.sites;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.globalization.GlobalizedMessage;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SitesTable extends Table {

    public static final int COL_SITE_DOMAIN = 0;
    public static final int COL_IS_DEFAULT_SITE = 1;
    public static final int COL_DEFAULT_THEME = 2;

    public SitesTable() {

        super();

        super.setIdAttr("sitesTable");
        super.setStyleAttr("width: 30em");

        setEmptyView(new Label(new GlobalizedMessage("ui.admin.sites.no_sites",
                                                     ADMIN_BUNDLE)));

        final TableColumnModel columnModel = getColumnModel();
        columnModel.add(new TableColumn(
            COL_SITE_DOMAIN,
            new Label(new GlobalizedMessage("ui.admin.sites.table.domain"))));
        columnModel.add(new TableColumn(
            COL_IS_DEFAULT_SITE,
            new Label(new GlobalizedMessage("ui.admin.sites.table.default_site"))));
        columnModel.add(new TableColumn(
            COL_DEFAULT_THEME,
            new Label(new GlobalizedMessage("ui.admin.sites.table.default_theme"))));
        
        super.setModelBuilder(new SitesTableModelBuilder());
    }

}
