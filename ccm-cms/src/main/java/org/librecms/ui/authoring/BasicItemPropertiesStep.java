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
package org.librecms.ui.authoring;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;
import org.libreccm.ui.LocalizedStringWidget;
import org.librecms.contentsection.ContentItem;
import org.librecms.ui.ContentSectionViewController;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 * @param <T>
 */
public class BasicItemPropertiesStep<T extends ContentItem>
    extends CustomComponent
    implements Serializable {

    private static final long serialVersionUID = 3881230433270571344L;

    private final ContentSectionViewController controller;
    private final ContentItem item;

    private final TextField nameField;
    private final LocalizedStringWidget titleWidget;

    public BasicItemPropertiesStep(final ContentSectionViewController controller,
                                   final T item) {

        Objects.requireNonNull(controller);
        Objects.requireNonNull(item);

        this.controller = controller;
        this.item = item;

        nameField = new TextField("Name");
        nameField.setValue(item.getName().getValue());
        nameField.addValueChangeListener(event -> {
        });

        titleWidget = new LocalizedStringWidget(
            controller.getLocalizedStringWidgetController(),
            item.getTitle(),
            false);
        titleWidget.setCaption("Title");

        final Button saveButton = new Button("Save");
        saveButton.addClickListener(event -> {
        });

        final Button cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
        });

        final FormLayout layout = new FormLayout(nameField,
                                                 titleWidget,
                                                 saveButton,
                                                 cancelButton);
        super.setCompositionRoot(layout);
    }

}
