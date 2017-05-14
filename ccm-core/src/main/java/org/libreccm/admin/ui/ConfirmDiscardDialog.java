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

import com.arsdigita.ui.admin.AdminUiConstants;

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ConfirmDiscardDialog extends Window {

    private static final long serialVersionUID = 7270363517221672796L;

    public ConfirmDiscardDialog(final Window window,
                                final String message) {

        if (window == this) {
            throw new IllegalArgumentException("ConfirmDiscardDialog can't be "
                                                   + "used with itself.");
        }

        setCaption(message);
        
        final Label label = new Label(message);

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final Button yesButton = new Button(bundle.getString("ui.admin.yes"));
        yesButton.addClickListener(event -> {
            close();
            UI.getCurrent().removeWindow(window);
        });

        final Button noButton = new Button(bundle.getString("ui.admin.no"));
        noButton.addClickListener(event -> close());

        final HorizontalLayout buttonsLayout = new HorizontalLayout(yesButton,
                                                                    noButton);
        final VerticalLayout layout = new VerticalLayout(label,
                                                         buttonsLayout);
        
        setContent(layout);
    }

}
