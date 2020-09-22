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
package org.libreccm.ui.admin;

import org.libreccm.l10n.GlobalizationHelper;

import java.util.AbstractMap;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("AdminMessages")
public class AdminMessages extends AbstractMap<String, String> {

    @Inject
    private GlobalizationHelper globalizationHelper;

    private ResourceBundle messages;

    

    @PostConstruct
    private void init() {
        messages = ResourceBundle.getBundle(
            AdminConstants.ADMIN_BUNDLE,
            globalizationHelper.getNegotiatedLocale()
        );
    }

    public String getMessage(final String key) {
        if (messages.containsKey(key)) {
            return messages.getString(key);
        } else {
            return "???key???";
        }
    }
    
    public String get(final String key) {
        return getMessage(key);
    }
    
    @Override
    public Set<Entry<String, String>> entrySet() {
        return messages
            .keySet()
            .stream()
            .collect(
                Collectors.toMap(key -> key, key-> messages.getString(key))
            )
            .entrySet();
    }

}
