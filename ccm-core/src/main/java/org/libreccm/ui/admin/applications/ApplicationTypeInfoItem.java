/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin.applications;

import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationTypeInfoItem implements
    Comparable<ApplicationTypeInfoItem> {

    private String name;

    private String title;

    private String description;

    private boolean singleton;

    private long numberOfInstances;
    
    private String controllerLink;

    protected ApplicationTypeInfoItem() {
        // Nothing
    }

    public String getName() {
        return name;
    }

    protected void setName(final String name) {
        this.name = name;
    }

    public String getTitle() {
        return title;
    }

    protected void setTitle(final String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    protected void setDescription(final String description) {
        this.description = description;
    }

    public boolean isSingleton() {
        return singleton;
    }

    protected void setSingleton(final boolean singleton) {
        this.singleton = singleton;
    }

    public long getNumberOfInstances() {
        return numberOfInstances;
    }

    protected void setNumberOfInstances(final long numberOfInstances) {
        this.numberOfInstances = numberOfInstances;
    }
    
    public String getControllerLink() {
        return controllerLink;
    }
    
    protected void setControllerLink(final String controllerLink) {
        this.controllerLink = controllerLink;
    }

    @Override
    public int compareTo(final ApplicationTypeInfoItem other) {
        if (other == null) {
            return 1;
        }

        int result = Objects.compare(title, other.getTitle(), String::compareTo);
        if (result == 0) {
            result = Objects.compare(name, other.getName(), String::compareTo);
        }

        return result;
    }

}
