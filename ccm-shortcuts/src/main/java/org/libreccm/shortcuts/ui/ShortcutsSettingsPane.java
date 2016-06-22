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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.ui.admin.applications.AbstractAppSettingsPane;

import org.libreccm.shortcuts.ShortcutsConstants;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ShortcutsSettingsPane extends AbstractAppSettingsPane {

    private final StringParameter selectedShortcutParam;
    private final ParameterSingleSelectionModel<String> selectedShortcut;
    private final ShortcutsTable shortcutsTable;
    private final ActionLink addShortcutLink;
    private final ShortcutForm shortcutForm;

    public ShortcutsSettingsPane(
        final ParameterSingleSelectionModel<String> selectedAppType,
        final ParameterSingleSelectionModel<String> selectedAppInstance) {

        super(selectedAppType, selectedAppInstance);

        selectedShortcutParam = new StringParameter("selectedShortcut");
        selectedShortcut = new ParameterSingleSelectionModel<>(
            selectedShortcutParam);

        final BoxPanel panel = new BoxPanel(BoxPanel.VERTICAL);
        final Label heading = new Label(new GlobalizedMessage(
            "shortcuts.ui.admin.heading", ShortcutsConstants.SHORTCUTS_BUNDLE));
        heading.setClassAttr("heading");
        panel.add(heading);

        shortcutsTable = new ShortcutsTable(this, selectedShortcut);
        panel.add(shortcutsTable);

        shortcutForm = new ShortcutForm(this, selectedShortcut);
        panel.add(shortcutForm);

        addShortcutLink = new ActionLink(new GlobalizedMessage(
            "shortcuts.ui.admin.add_shortcut",
            ShortcutsConstants.SHORTCUTS_BUNDLE));
        addShortcutLink.addActionListener(e -> {
            showShortcutForm(e.getPageState());
        });
        panel.add(addShortcutLink);

        add(panel);
    }

    @Override
    protected void createWidgets() {

    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.addGlobalStateParam(selectedShortcutParam);

        page.setVisibleDefault(shortcutsTable, true);
        page.setVisibleDefault(shortcutForm, false);
        page.setVisibleDefault(addShortcutLink, true);

    }

    void showShortcutForm(final PageState state) {
        shortcutsTable.setVisible(state, false);
        shortcutForm.setVisible(state, true);
        addShortcutLink.setVisible(state, false);
    }

    void showShortcutsTable(final PageState state) {
        shortcutsTable.setVisible(state, true);
        shortcutForm.setVisible(state, false);
        addShortcutLink.setVisible(state, true);
    }

}
