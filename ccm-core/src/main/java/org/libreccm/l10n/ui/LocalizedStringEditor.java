package org.libreccm.l10n.ui;

import com.vaadin.data.provider.ListDataProvider;
import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.server.SerializableToIntFunction;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.l10n.LocalizedString;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
/**
 * A editor widget for properties of type {@link LocalizedString}. The widget
 * consists of the following components:
 *
 * <ul>
 * <li>A table displaying a variants of the localised string.</li>
 * <li>An editor for the values.</li>
 * </ul>
 *
 * If the localised string is a single line string, the strings can be edited
 * directly in the table. Otherwise a popup window with a text area is opened.
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LocalizedStringEditor extends CustomComponent {

    private static final long serialVersionUID = 1275927615085548961L;

    private static final String COL_LOCALE = "col_locale";
    private static final String COL_VALUE = "col_value";
    private static final String COL_EDIT = "col_edit";
    private static final String COL_REMOVE = "col_remove";

    private final GlobalizationHelper globalizationHelper;

    private boolean multiline;
    private LocalizedString localizedString;

    private final Grid<LocalizedStringValue> grid;

    public LocalizedStringEditor(final GlobalizationHelper globalizationHelper) {
        super();

        this.globalizationHelper = globalizationHelper;

        multiline = false;

        grid = new Grid<>();
        grid.setDataProvider(this::fetchValues,
                             () -> localizedString.getAvailableLocales().size());
        grid.addColumn(LocalizedStringValue::getLocaleLabel)
            .setCaption("Language")
            .setId(COL_LOCALE);
        grid.addColumn(LocalizedStringValue::getValue)
            .setCaption("Value")
            .setId(COL_VALUE);
        grid.addComponentColumn(this::buildEditButton)
            .setCaption("Edit")
            .setId(COL_EDIT);
        grid.addComponentColumn(this::buildDeleteButton)
            .setCaption("Delete")
            .setId(COL_REMOVE);

        final ComboBox<Locale> localeSelect = new ComboBox<>();
        localeSelect.setDataProvider(this::fetchAvailableLocales,
                                     this::countAvailableLocales);
        localeSelect.setDescription("Select locale to add");
        localeSelect.addStyleName(ValoTheme.COMBOBOX_TINY);
        localeSelect.setEmptySelectionAllowed(false);
        localeSelect.setTextInputAllowed(false);
        localeSelect.setItemCaptionGenerator(Locale::toString);
        final Button addButton = new Button("Add value");
        addButton.addStyleName(ValoTheme.BUTTON_TINY);
        addButton.setIcon(VaadinIcons.PLUS_CIRCLE_O);
        addButton.addClickListener(event -> {
        });

        final HeaderRow headerRow = grid.prependHeaderRow();
        final HeaderCell headerCell = headerRow.join(COL_LOCALE,
                                                     COL_VALUE,
                                                     COL_EDIT,
                                                     COL_REMOVE);
        headerCell.setComponent(new HorizontalLayout(localeSelect, addButton));

        localizedString = new LocalizedString();
        super.setCompositionRoot(grid);
    }

    public LocalizedStringEditor(final LocalizedString localizedString,
                                 final GlobalizationHelper globalizationHelper) {

        this(globalizationHelper);

        this.localizedString = localizedString;
        multiline = false;
    }

    public LocalizedStringEditor(final boolean multiline,
                                 final GlobalizationHelper globalizationHelper) {
        this(globalizationHelper);
        this.multiline = multiline;
        localizedString = new LocalizedString();
    }

    public LocalizedStringEditor(final LocalizedString localizedString,
                                 final boolean multiline,
                                 final GlobalizationHelper globalizationHelper) {
        this(globalizationHelper);
        this.localizedString = localizedString;
        this.multiline = multiline;
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(final boolean multiline) {
        this.multiline = multiline;
    }

    public LocalizedString getLocalizedString() {
        return localizedString;
    }

    public void setLocalizedString(final LocalizedString localizedString) {
        this.localizedString = localizedString;
    }

    private Stream<Locale> fetchAvailableLocales(final String filter,
                                                 final int offset,
                                                 final int limit) {

        final List<Locale> values = globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> localizedString.hasValue(locale))
            .sorted((locale1, locale2) -> {
                return locale1.toString().compareTo(locale2.toString());
            })
            .collect(Collectors.toList());
        
        if (values.size() > limit) {
            return values.subList(offset, offset + limit).stream();
        } else {
            return values.stream();
        }
    }

    private int countAvailableLocales(final String value) {

        return (int) globalizationHelper
            .getAvailableLocales()
            .stream()
            .filter(locale -> localizedString.hasValue(locale))
            .count();
    }

    private Component buildEditButton(final LocalizedStringValue value) {

        final Button button = new Button("Edit");
        button.setIcon(VaadinIcons.EDIT);
        button.addStyleName(ValoTheme.BUTTON_TINY);
        button.addClickListener(event -> {
        });

        return button;
    }

    private Component buildDeleteButton(final LocalizedStringValue value) {

        final Button button = new Button("Edit");
        button.setIcon(VaadinIcons.MINUS_CIRCLE_O);
        button.addStyleNames(ValoTheme.BUTTON_TINY,
                             ValoTheme.BUTTON_DANGER);
        button.addClickListener(event -> {
        });

        return button;
    }

    private class LocalizedStringValue {

        private Locale locale;
        private String value;

        public LocalizedStringValue(final Locale locale,
                                    final String value) {
            this.locale = locale;
            this.value = value;
        }

        public Locale getLocale() {
            return locale;
        }

        public String getLocaleLabel() {
            return locale
                .getDisplayName(globalizationHelper.getNegotiatedLocale());
        }

        public void setLocale(final Locale locale) {
            this.locale = locale;
        }

        public String getValue() {
            return value;
        }

        public String getText() {
            if (multiline) {
                final String withoutHtml = value
                    .replaceAll("<[\\w/]*>", " ")
                    .replaceAll("\\s{2,}", " ").trim();

                return String.format("%s...", withoutHtml.substring(0, 256));

            } else {
                return value;
            }
        }

        public void setValue(final String value) {
            this.value = value;
        }

    }

    private Stream<LocalizedStringValue> fetchValues(
        final List<QuerySortOrder> sortOrder,
        final int offset,
        final int limit) {

        final List<Locale> locales = new ArrayList<>(localizedString
            .getAvailableLocales());
        locales.sort((locale1, locale2) -> {
            return Objects
                .toString(locale1)
                .compareTo(Objects.toString(locale2));
        });

        if (locales.size() > limit) {
            return locales
                .subList(offset, limit)
                .stream()
                .map(locale -> new LocalizedStringValue(
                locale, localizedString.getValue(locale)));
        } else {
            return locales
                .subList(offset, locales.size())
                .stream()
                .map(locale -> new LocalizedStringValue(
                locale, localizedString.getValue(locale)));
        }
    }

}
