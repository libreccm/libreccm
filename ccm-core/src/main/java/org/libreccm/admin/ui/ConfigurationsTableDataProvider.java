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

import com.arsdigita.kernel.KernelConfig;
import com.vaadin.cdi.ViewScoped;
import com.vaadin.data.provider.AbstractDataProvider;
import com.vaadin.data.provider.Query;
import org.libreccm.configuration.ConfigurationInfo;
import org.libreccm.configuration.ConfigurationManager;
import org.libreccm.l10n.GlobalizationHelper;

import javax.inject.Inject;
import java.util.Locale;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ViewScoped
class ConfigurationsTableDataProvider extends AbstractDataProvider<ConfigurationsGridRowData, String> {

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
    public int size(final Query<ConfigurationsGridRowData, String> query) {

        final Locale defaultLocale = confManager
            .findConfiguration(KernelConfig.class)
            .getDefaultLocale();

        if (filter == null || filter.trim().isEmpty()) {
            return confManager.findAllConfigurations().size();
        } else {
            return (int) confManager
                .findAllConfigurations()
                .stream()
                .filter(conf -> {
                    final ConfigurationInfo info = confManager
                        .getConfigurationInfo(conf);
                    if (filter == null || filter.isEmpty()) {
                        return true;
                    } else {
                        return info
                            .getTitle(globalizationHelper.getNegotiatedLocale(),
                                      defaultLocale)
                            .startsWith(filter);
                    }
                })
                .count();
        }
    }

    @Override
    public Stream<ConfigurationsGridRowData> fetch(
        final Query<ConfigurationsGridRowData, String> query) {

        final int fromIndex;
        final int toIndex;
        final int size = size(query);
        if (query.getOffset() > size - 1) {
            fromIndex = size - 1;
        } else {
            fromIndex = query.getOffset();
        }

        if ((query.getOffset() + query.getLimit()) > size) {
            toIndex = size;
        } else {
            toIndex = query.getOffset() + query.getLimit();
        }

        if (filter == null || filter.trim().isEmpty()) {
            return confManager
                .findAllConfigurations()
                .stream()
                .map(configurationClass -> createRowData(configurationClass))
                .sorted((rowData1, rowData2) -> {
                    return rowData1
                        .getConfigurationClass()
                        .getSimpleName()
                        .compareTo(rowData2.getConfigurationClass()
                            .getSimpleName());
                })
                .collect(Collectors.toList())
                .subList(fromIndex, toIndex)
                .stream();
        } else {
            return confManager
                .findAllConfigurations()
                .stream()
                .map(configurationClass -> createRowData(configurationClass))
                .filter(rowData -> {
                    if (filter == null || filter.isEmpty()) {
                        return true;
                    } else {
                        return rowData
                            .getTitle()
                            .startsWith(filter);
                    }
                })
                .sorted((rowData1, rowData2) -> {
                    return rowData1
                        .getConfigurationClass()
                        .getSimpleName()
                        .compareTo(rowData2.getConfigurationClass()
                            .getSimpleName());
                })
                .collect(Collectors.toList())
                .subList(fromIndex, toIndex)
                .stream();
        }
    }

    public void setFilter(final String filter) {
        this.filter = filter;
    }

    private ConfigurationsGridRowData createRowData(
        final Class<?> configurationClass) {

        final ConfigurationInfo info = confManager
            .getConfigurationInfo(configurationClass);

        final Locale defaultLocale = confManager
            .findConfiguration(KernelConfig.class)
            .getDefaultLocale();

        final ConfigurationsGridRowData rowData
                                            = new ConfigurationsGridRowData();
        rowData.setConfigurationClass(configurationClass);
        rowData.setName(info.getName());
        rowData.setTitle(info
            .getTitle(globalizationHelper.getNegotiatedLocale(),
                      defaultLocale));
        rowData.setDescription(info
            .getDescription(globalizationHelper.getNegotiatedLocale()));
        rowData.setConfigurationInfo(info);

        return rowData;

    }

}
