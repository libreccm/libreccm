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
package org.libreccm.ui;

import com.arsdigita.kernel.KernelConfig;

import org.libreccm.configuration.ConfigurationManager;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 * Provides access to the CDI controlled beans used by the
 * {@link LocalizedStringWidget}.
 *
 * @see LocalizedStringWidget
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class LocalizedStringWidgetController implements Serializable {

    private static final long serialVersionUID = -8390792440087872905L;

    @Inject
    private ConfigurationManager confManager;

    public Locale getDefaultLocale() {

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);

        return kernelConfig.getDefaultLocale();
    }

    public List<Locale> getSupportedLocales() {

        final KernelConfig kernelConfig = confManager
            .findConfiguration(KernelConfig.class);

        return kernelConfig
            .getSupportedLanguages()
            .stream()
            .sorted((lang1, lang2) -> lang1.compareTo(lang2))
            .map(Locale::new)
            .collect(Collectors.toList());

    }

}
