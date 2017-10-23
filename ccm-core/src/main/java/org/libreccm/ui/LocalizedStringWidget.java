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

import com.vaadin.data.provider.QuerySortOrder;
import com.vaadin.icons.VaadinIcons;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.NativeSelect;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.components.grid.HeaderCell;
import com.vaadin.ui.components.grid.HeaderRow;
import com.vaadin.ui.themes.ValoTheme;
import org.libreccm.l10n.LocalizedString;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A editor component for properties of type {@link LocalizedString}.
 *
 * The component contains a {@link Grid} which shows all values of the localized
 * string. The Grid provides buttons editing, removing and adding values. The
 * add and remove buttons can be enabled and disabled by setting
 * {@link #addAndRemoveEnabled}.
 *
 * Please note that this widget does <em>not</em> save the modifications done
 * the the edited {@link LocalizedString} to the database. That is the
 * responsibility of the caller.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class LocalizedStringWidget extends CustomComponent {

    private static final long serialVersionUID = -3089484568782865789L;

    private static final String COL_LOCALE = "col_locale";
    private static final String COL_VALUE = "col_value";
    private static final String COL_EDIT = "col_edit";
    private static final String COL_REMOVE = "col_remove";

    /**
     * CDI bean providing access to some other CDI beans which provide
     * functionality required by this widget.
     */
    private final LocalizedStringWidgetController controller;
    /**
     * Implementation of {@link TextEditorBuilder} which is used to create the
     * editor for the texts if {@link #multiline} is set to {@code true}.
     */
    private final TextEditorBuilder textEditorBuilder;

    /**
     * The localised string to edit.
     */
    private LocalizedString localizedString;

    /**
     * Enable the add and remove buttons? (default: {@code false})
     */
    private boolean addAndRemoveEnabled = false;
    /**
     * Is the text to edit a multi line text or simple single line string?
     */
    private boolean multiline;
    /**
     * Is HTML permitted in the string. This property is only useful for multi
     * line strings. The value is passed to the {@link #textEditorBuilder}.
     */
    private boolean htmlPermitted;

    /**
     * The {@link Grid} showing the values of the {@link #localizedString}.
     */
    private Grid<Map.Entry<Locale, String>> valuesGrid;
    /**
     * The header row containing the add button.
     */
    private HorizontalLayout actionsRow;

    /**
     * Creates a new {@code LocalizedStringWidget}.
     * 
     * @param controller 
     */
    public LocalizedStringWidget(
        final LocalizedStringWidgetController controller) {

        super();

        this.controller = controller;
        textEditorBuilder = htmlPermitted -> new SimpleTextEditor("Text");

        valuesGrid = new Grid<>();
        valuesGrid.setDataProvider(this::fetchValues, this::countValues);
        valuesGrid
            .addColumn(Map.Entry::getKey)
            .setId(COL_LOCALE)
            .setCaption("Locale");
        valuesGrid
            .addColumn(Map.Entry::getValue)
            .setId(COL_VALUE)
            .setCaption("Value");
        valuesGrid.addComponentColumn(value -> {
            final Button button = new Button("Edit", VaadinIcons.EDIT);
            button.addClickListener(event -> showTextEditor(value.getKey()));
            return button;
        })
            .setId(COL_EDIT);
        valuesGrid.addComponentColumn(value -> {
            final Button button = new Button("Remove",
                                             VaadinIcons.MINUS_CIRCLE_O);
            button.addClickListener(event -> {
            });
            return button;
        })
            .setId(COL_REMOVE);

        valuesGrid.getColumn(COL_REMOVE).setHidden(!addAndRemoveEnabled);

        final NativeSelect<Locale> localeSelect = new NativeSelect<>();
        final Button addButton = new Button("Add value",
                                            VaadinIcons.PLUS_CIRCLE_O);
        addButton.addClickListener(event -> addValue(localeSelect.getValue()));
        addButton.setEnabled(false);
        localeSelect.addSelectionListener(event -> {
            addButton.setEnabled(event.getSelectedItem().isPresent());
        });
        actionsRow = new HorizontalLayout(localeSelect, addButton);

        final HeaderRow headerRow = valuesGrid.prependHeaderRow();
        final HeaderCell actionsCell = headerRow.join(COL_LOCALE,
                                                      COL_VALUE,
                                                      COL_EDIT,
                                                      COL_REMOVE);
        actionsCell.setComponent(actionsRow);

        actionsRow.setVisible(addAndRemoveEnabled);
    }

    public LocalizedStringWidget(
        final LocalizedStringWidgetController controller,
        final boolean addAndRemoveEnabled) {

        this(controller);
        this.addAndRemoveEnabled = addAndRemoveEnabled;
        valuesGrid.getColumn(COL_REMOVE).setHidden(!addAndRemoveEnabled);
        actionsRow.setVisible(addAndRemoveEnabled);
    }

    public LocalizedStringWidget(
        final LocalizedStringWidgetController controller,
        final LocalizedString localizedString) {

        this(controller);
        this.localizedString = localizedString;
        valuesGrid.getDataProvider().refreshAll();
    }

    public LocalizedStringWidget(
        final LocalizedStringWidgetController controller,
        final LocalizedString localizedString,
        final boolean addAndRemoveEnabled) {

        this(controller, localizedString);
        this.addAndRemoveEnabled = addAndRemoveEnabled;
        valuesGrid.getColumn(COL_REMOVE).setHidden(!addAndRemoveEnabled);
        actionsRow.setVisible(addAndRemoveEnabled);
    }

    public LocalizedString getLocalizedString() {
        return this.localizedString;
    }

    public void setLocalizedString(final LocalizedString localizedString) {
        this.localizedString = localizedString;
        valuesGrid.getDataProvider().refreshAll();
    }

    public boolean isAddAndRemoveEnabled() {
        return addAndRemoveEnabled;
    }

    public void setAddAndRemoveEnabled(final boolean addAndRemoveEnabled) {
        this.addAndRemoveEnabled = addAndRemoveEnabled;
        valuesGrid.getColumn(COL_REMOVE).setHidden(!addAndRemoveEnabled);
        actionsRow.setVisible(addAndRemoveEnabled);
    }

    public boolean isMultiline() {
        return multiline;
    }

    public void setMultiline(final boolean multiline) {
        this.multiline = multiline;
    }

    public boolean isHtmlPermitted() {
        return htmlPermitted;
    }

    public void setHtmlPermitted(final boolean htmlPermitted) {
        this.htmlPermitted = htmlPermitted;
    }

    private Stream<Map.Entry<Locale, String>> fetchValues(
        final List<QuerySortOrder> sortOrder,
        final int offset,
        final int limit) {

        if (localizedString == null) {
            return Stream.empty();
        } else {
            final List<Map.Entry<Locale, String>> values = new ArrayList<>(
                localizedString.getValues().entrySet());
            if (limit >= values.size()) {
                return values.stream();
            } else {
                return values.subList(offset, limit).stream();
            }

        }
    }

    private Integer countValues() {
        if (localizedString == null) {
            return 0;
        } else {
            return localizedString.getValues().size();
        }
    }

    private void addValue(final Locale forLocale) {

        final String value;
        if (localizedString.hasValue(controller.getDefaultLocale())) {
            value = localizedString.getValue(controller.getDefaultLocale());
        } else {
            final Set<Locale> availableLocales = localizedString
                .getAvailableLocales();
            if (availableLocales.isEmpty()) {
                value = "";
            } else {
                final Optional<Locale> firstLocale = availableLocales
                    .stream()
                    .sorted((locale1, locale2) -> {
                        return locale1.toString().compareTo(locale2.toString());
                    })
                    .findFirst();

                if (firstLocale.isPresent()) {
                    value = localizedString.getValue(firstLocale.get());
                } else {
                    value = "";
                }
            }
        }

        localizedString.addValue(forLocale, value);
    }

    private void showTextEditor(final Locale locale) {

        final Window window = new Window();

        final Component editComponent;
        final Button saveButton;
        if (multiline) {
            final TextEditor textEditor = textEditorBuilder.buildTextEditor(
                htmlPermitted);
            textEditor.setText(localizedString.getValue(locale));
            editComponent = textEditor;

            saveButton = new Button("Save", VaadinIcons.CHECK_CIRCLE_O);
            saveButton.addClickListener(event -> {
                localizedString.addValue(locale, textEditor.getText());
                valuesGrid.getDataProvider().refreshAll();
                window.close();
            });
        } else {
            final TextField textField = new TextField("Text");
            textField.setValue(localizedString.getValue(locale));
            editComponent = textField;

            saveButton = new Button("Save", VaadinIcons.CHECK_CIRCLE_O);
            saveButton.addClickListener(event -> {
                localizedString.addValue(locale, textField.getValue());
                valuesGrid.getDataProvider().refreshAll();
                window.close();
            });
        }

        final Button cancelButton = new Button("Cancel");
        cancelButton.addStyleName(ValoTheme.BUTTON_DANGER);
        cancelButton.addClickListener(event -> window.close());

        final HorizontalLayout buttonsLayout = new HorizontalLayout(saveButton,
                                                                    cancelButton);

        final VerticalLayout layout = new VerticalLayout(editComponent,
                                                         buttonsLayout);

        window.setContent(layout);
        window.setCaption(String.format("Edit text for locale \"%s\"",
                                        locale.toString()));
        window.setModal(true);

        UI.getCurrent().addWindow(window);
    }

    private static class SimpleTextEditor 
        extends TextArea 
        implements TextEditor {

        private static final long serialVersionUID = -1189747199799719077L;

        public SimpleTextEditor() {
            super();
        }

        public SimpleTextEditor(final String caption) {
            super(caption);
        }

        @Override
        public String getText() {
            return getValue();
        }

        @Override
        public void setText(final String text) {
            setValue(text);
        }

    }

}
