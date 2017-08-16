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
package org.libreccm.admin.ui;

import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;

import java.util.SortedSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
public class ConfigurationsTableDataProvider extends AbstractDataProvider<Class<?>, String> {

    private static final long serialVersionUID = -7001151229931864885L;

    private String filter;

    @Inject
    private ConfigurationManager confManager;

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Override
    public boolean isInMemory() {
        return false;
    }

    @Override
    public int size(final Query<Class<?>, String> query) {
        if (filter == null || filter.trim().isEmpty()) {
            return confManager.findAllConfigurations().size();
        } else {
            return (int) confManager
                .findAllConfigurations()
                .stream()
                .filter(conf -> {
                    final ConfigurationInfo info = confManager
                        .getConfigurationInfo(conf);
//                    return c.getName().startsWith(filterTerm);
                    if (filter == null || filter.isEmpty()) {
                        return true;
                    } else {
                        return info
                            .getTitle(globalizationHelper.getNegotiatedLocale())
                            .startsWith(
                                filter);
                    }
                })
                .count();
        }
    }

    @Override
    public Stream<Class<?>> fetch(final Query<Class<?>, String> query) {
        if (filter == null || filter.trim().isEmpty()) {
            return confManager.findAllConfigurations().stream();
        } else {
            return confManager
                .findAllConfigurations()
                .stream()
                .filter(conf -> {
                    final ConfigurationInfo info = confManager
                        .getConfigurationInfo(conf);
//                    return c.getName().startsWith(filterTerm);
                    if (filter == null || filter.isEmpty()) {
                        return true;
                    } else {
                        return info
                            .getTitle(globalizationHelper.getNegotiatedLocale())
                            .startsWith(
                                filter);
                    }
                })
                .collect(Collectors.toList())
                .subList(query.getOffset(), query.getOffset() + query.getLimit())
                .stream();
        }
    }

    public void setFilter(final String filter) {
        this.filter = filter;
    }

}
