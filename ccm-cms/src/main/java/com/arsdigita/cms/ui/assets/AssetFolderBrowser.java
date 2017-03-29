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
package com.arsdigita.cms.ui.assets;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.DefaultTableColumnModel;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.globalization.GlobalizedMessage;

import javafx.scene.control.Pagination;
import org.librecms.CmsConstants;

/**
 * Browse folder and assets.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetFolderBrowser extends Table {

    protected static final String SORT_ACTION_UP = "sortActionUp";
    protected static final String SORT_ACTION_DOWN = "sortActionDown";
    protected final static String SORT_KEY_NAME = "name";
    protected final static String SORT_KEY_TITLE = "title";
    protected final static String SORT_KEY_TYPE = "type";
    protected final static String SORT_KEY_LAST_MODIFIED_DATE = "lastModified";
    protected final static String SORT_KEY_CREATION_DATE = "creationDate";

    private final FolderSelectionModel folderSelectionModel;
    private TableActionListener folderChanger;
    private TableActionListener tableDeleter;
    private TableColumn nameColumn;
    private TableColumn deleteColumn;
    private final StringParameter sortTypeParameter = new StringParameter(
        "sortType");
    private final StringParameter sortDirectionParameter = new StringParameter(
        "sortDir");

    private Paginator paginator;
    private long folderSize;

    public AssetFolderBrowser(final FolderSelectionModel folderSelectionModel) {
        super();
        sortTypeParameter.setDefaultValue(SORT_KEY_NAME);
        sortDirectionParameter.setDefaultValue(SORT_ACTION_UP);

        this.folderSelectionModel = folderSelectionModel;

        initComponents();
    }

    protected FolderSelectionModel getFolderSelectionModel() {
        return folderSelectionModel;
    }

    protected Paginator getPaginator() {
        return paginator;
    }

    protected String getSortType(final PageState state) {
        return (String) state.getValue(sortTypeParameter);
    }

    protected String getSortDirection(final PageState state) {
        return (String) state.getValue(sortDirectionParameter);
    }

    private void initComponents() {
        setModelBuilder(new AssetFolderBrowserTableModelBuilder());

        final GlobalizedMessage[] headers = {
            globalize("cms.ui.folder.name"),
            globalize("cms.ui.folder.languages"),
            globalize("cms.ui.folder.title"),
            globalize("cms.ui.folder.type"),
            globalize("cms.ui.folder.creation_date"),
            globalize("cms.ui.folder.last_modified"),
            globalize("cms.ui.folder.action")};
    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     */
    private GlobalizedMessage globalize(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_FOLDER_BUNDLE);

    }

}
