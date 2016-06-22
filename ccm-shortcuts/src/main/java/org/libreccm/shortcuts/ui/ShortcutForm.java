/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.shortcuts.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.shortcuts.Shortcut;
import org.libreccm.shortcuts.ShortcutManager;
import org.libreccm.shortcuts.ShortcutRepository;
import org.libreccm.shortcuts.ShortcutsConstants;

import java.util.Locale;
import java.util.regex.Pattern;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ShortcutForm extends Form {

    

    private static final String URL_KEY = "urlKey";
    private static final String REDIRECT = "redirect";
    
    private final TextField urlKeyField;
    private final TextField redirectField;

    private final SaveCancelSection saveCancelSection;

    public ShortcutForm(
        final ShortcutsSettingsPane shortcutsPane,
        final ParameterSingleSelectionModel<String> selectedShortcut) {
        
        super("shortcutForm");

        urlKeyField = new TextField(URL_KEY);
        urlKeyField.setLabel(new GlobalizedMessage(
            "shortcuts.ui.admin.url_key.label",
            ShortcutsConstants.SHORTCUTS_BUNDLE));
        add(urlKeyField);

        redirectField = new TextField(REDIRECT);
        redirectField.setLabel(new GlobalizedMessage(
            "shortcuts.ui.admin.redirect.label",
            ShortcutsConstants.SHORTCUTS_BUNDLE));
        add(redirectField);

        saveCancelSection = new SaveCancelSection();
        add(saveCancelSection);
        
        addValidationListener(e -> {
            final PageState state = e.getPageState();
            final FormData data = e.getFormData();

            final String urlKey = data.getString(URL_KEY);
            if (urlKey == null || urlKey.trim().isEmpty()) {
                data.addError(URL_KEY, new GlobalizedMessage(
                              "shortcuts.ui.admin.url_key.error.not_empty",
                              ShortcutsConstants.SHORTCUTS_BUNDLE));
                return;
            }

            // The URL to redirect must start with a '/' and end with a '/'. 
            // Between the starting and the ending '/' only the characters
            // 'a' to 'z', 'A' to 'Z', '0' to '9', '_', '-' and '.' may appear.
            if (!Pattern.matches("^/[-a-zA-Z0-9_./]+/$", urlKey)) {
                data.addError(URL_KEY, new GlobalizedMessage(
                              "shortcuts.ui.admin.url_key.error.invalid",
                              ShortcutsConstants.SHORTCUTS_BUNDLE));
                return;
            }

            if (data.getString(REDIRECT) == null
                    || data.getString(REDIRECT).trim().isEmpty()) {
                data.addError(URL_KEY, new GlobalizedMessage(
                              "shortcuts.ui.admin.redirect.not_empty",
                              ShortcutsConstants.SHORTCUTS_BUNDLE));
                return;
            }
            final String redirect = data.getString(REDIRECT).toLowerCase(
                Locale.ROOT);
            if (!redirect.startsWith("http://")
                    && !redirect.startsWith("https://")
                    && !redirect.startsWith("/")) {
                data.addError(URL_KEY, new GlobalizedMessage(
                              "shortcuts.ui.admin.redirect.error.invalid",
                              ShortcutsConstants.SHORTCUTS_BUNDLE));
            }
        });

        addInitListener(e -> {
            final PageState state = e.getPageState();
            final FormData data = e.getFormData();

            if (selectedShortcut.isSelected(state)) {
                final ShortcutRepository repo = CdiUtil.createCdiUtil()
                    .findBean(ShortcutRepository.class);
                final Shortcut shortcut = repo.findById(Long.parseLong(
                    selectedShortcut.getSelectedKey(state)));
                urlKeyField.setValue(state, shortcut.getUrlKey());
                redirectField.setValue(state, shortcut.getRedirect());
            }
        });

        addProcessListener(e -> {
            final PageState state = e.getPageState();
            final FormData data = e.getFormData();

            if (saveCancelSection.getSaveButton().isSelected(state)) {

                final Shortcut shortcut;
                if (selectedShortcut.isSelected(state)) {
                    final ShortcutRepository repo = CdiUtil.createCdiUtil()
                        .findBean(ShortcutRepository.class);
                    shortcut = repo.findById(Long.parseLong(selectedShortcut
                        .getSelectedKey(state)));

                    shortcut.setUrlKey(data.getString(URL_KEY));
                    shortcut.setRedirect(data.getString(REDIRECT));

                    repo.save(shortcut);
                } else {
                    final ShortcutManager shortcutManager = CdiUtil
                        .createCdiUtil().findBean(ShortcutManager.class);
                    shortcutManager.createShortcut(data.getString(URL_KEY),
                                                   data.getString(REDIRECT));
                }
            }

            selectedShortcut.clearSelection(state);
            shortcutsPane.showShortcutsTable(state);
        });
    }

}
