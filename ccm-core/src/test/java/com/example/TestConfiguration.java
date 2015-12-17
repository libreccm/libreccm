/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package com.example;

import org.libreccm.configuration.Configuration;
import org.libreccm.configuration.Setting;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Configuration
public class TestConfiguration {
    
    @Setting
    private Boolean enabled = false;
    
    @Setting
    private Long itemsPerPage = 40L;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(final Boolean enabled) {
        this.enabled = enabled;
    }

    public Long getItemsPerPage() {
        return itemsPerPage;
    }

    public void setItemsPerPage(final Long itemsPerPage) {
        this.itemsPerPage = itemsPerPage;
    }
}
