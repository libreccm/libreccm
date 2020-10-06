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
package org.libreccm.ui;

import org.libreccm.l10n.GlobalizationHelper;

import java.util.Locale;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mvc.locale.LocaleResolver;
import javax.mvc.locale.LocaleResolverContext;

/**
 * A locale resolver implementation that simply passes the locale negoiated by
 * LibreCCM to Jakarta EE MVC.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
public class MvcLocaleResolver implements LocaleResolver {

    @Inject
    private GlobalizationHelper globalizationHelper;

    @Override
    public Locale resolveLocale(final LocaleResolverContext context) {
        return globalizationHelper.getNegotiatedLocale();
    }

}
