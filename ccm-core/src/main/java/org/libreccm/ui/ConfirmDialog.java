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

import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.core.UnexpectedErrorException;

import java.util.concurrent.Callable;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ConfirmDialog extends Window {

    private static final long serialVersionUID = -3953818045293474190L;

    private final Callable<Void> confirmedAction;
    private final Callable<Void> cancelAction;

    private Label messageLabel;
    private Button confirmButton;
    private Button cancelButton;

    public ConfirmDialog(final Callable<Void> confirmedAction) {
        this.confirmedAction = confirmedAction;
        this.cancelAction = () -> {
            close();
            return null;
        };
        addWidgets();
    }

    public ConfirmDialog(final Callable<Void> confirmedAction,
                         final Callable<Void> cancelAction) {
        this.confirmedAction = confirmedAction;
        this.cancelAction = cancelAction;
        addWidgets();
    }

    private void addWidgets() {

        messageLabel = new Label("");

        confirmButton = new Button("OK");
        confirmButton.addClickListener(event -> {
            try {
                confirmedAction.call();
            } catch (Exception ex) {
                throw new UnexpectedErrorException(ex);
            }
        });

        cancelButton = new Button("Cancel");
        cancelButton.addClickListener(event -> {
            try {
                cancelAction.call();
            } catch (Exception ex) {
                throw new UnexpectedErrorException(ex);
            }
        });

        final HorizontalLayout buttonsLayout = new HorizontalLayout(
            confirmButton, cancelButton);
        final VerticalLayout layout = new VerticalLayout(messageLabel,
                                                         buttonsLayout);
        setContent(layout);
    }

    public String getMessage() {
        return messageLabel.getValue();
    }
    
    public void setMessage(final String message) {
        messageLabel.setValue(message);
    }
    
    public Button getConfirmButton() {
        return confirmButton;
    }
    
    public Button getCancelButton() {
        return cancelButton;
    }
    
}
