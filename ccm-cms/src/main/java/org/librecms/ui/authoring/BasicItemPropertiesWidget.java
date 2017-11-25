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

import com.vaadin.data.HasValue;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import org.libreccm.ui.LocalizedStringWidget;
import org.librecms.contentsection.ContentItem;
import org.librecms.ui.ContentSectionViewController;

import java.io.Serializable;
import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class BasicItemPropertiesWidget
    extends CustomComponent
    implements Serializable {

    private static final long serialVersionUID = 6560855454431178274L;

    private final ContentSectionViewController controller;
    private final ContentItem item;

    private final TextField nameField;
    private final LocalizedStringWidget titleWidget;

    public BasicItemPropertiesWidget(
        final ContentSectionViewController controller,
        final ContentItem item) {

        this.controller = controller;
        this.item = item;

        this.nameField = new TextField("Title");
        nameField.setValue(item.getName().getValue());
        nameField.addValueChangeListener(this::nameValueChanged);

        this.titleWidget = new LocalizedStringWidget(controller
            .getLocalizedStringWidgetController(),
                                                     item.getTitle(),
                                                     false);
        titleWidget.setCaption("Title");
    }

    protected void nameValueChanged(
        final HasValue.ValueChangeEvent<String> event) {

        final String result = nameField
            .getValue()
            .toLowerCase(Locale.ROOT)
            .replace(' ', '-')
            .replace('&', '-')
            .replace('/', '-')
            .replace('#', '-')
            .replace('?', '-')
            .replace("ä", "ae")
            .replace("ö", "oe")
            .replace("ü", "ue")
            .replace("ß", "ss")
            .replaceAll("-{2,}", "-");

        nameField.setValue(result);
    }

}
