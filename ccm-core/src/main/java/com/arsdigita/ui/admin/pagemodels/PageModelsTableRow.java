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
package com.arsdigita.ui.admin.pagemodels;

import java.io.Serializable;

/**
 * Data for one row of the {@link PageModelsTable}.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class PageModelsTableRow implements Comparable<PageModelsTableRow>,
                                    Serializable {

    private static final long serialVersionUID = 7497498047332094014L;

    private long modelId;

    private String name;

    private boolean live;

    private String title;

    private String description;

    private String applicationName;

    public long getModelId() {
        return modelId;
    }

    public void setModelId(final long modelId) {
        this.modelId = modelId;
    }

    public String getName() {
        return name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(final boolean live) {
        this.live = live;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(final String applicationName) {
        this.applicationName = applicationName;
    }

    @Override
    public int compareTo(final PageModelsTableRow other) {

        int result;

        result = applicationName.compareTo(other.getApplicationName());
        if (result != 0) {
            return result;
        }

        result = name.compareTo(other.getName());
        if (result != 0) {
            return result;
        }

        return title.compareTo(other.getTitle());
    }

}
