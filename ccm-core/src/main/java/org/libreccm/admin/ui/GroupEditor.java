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

import com.vaadin.data.HasValue;
import com.vaadin.server.Page;
import com.vaadin.server.UserError;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import org.libreccm.security.Group;
import org.libreccm.security.GroupRepository;

import java.util.ResourceBundle;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
class GroupEditor extends Window {

    private static final long serialVersionUID = -5834095844674226692L;

    private final Group group;
    private final GroupRepository groupRepo;

    private boolean dataHasChanged = false;

    private TextField groupName;

    protected GroupEditor(final GroupRepository groupRepo) {

        super("Create new group");

        group = null;
        this.groupRepo = groupRepo;

        addWidgets();
    }

    public GroupEditor(final Group group,
                       final GroupRepository groupRepo) {

        super(String.format("Edit group %s", group.getName()));

        this.group = group;
        this.groupRepo = groupRepo;

        addWidgets();
    }

    private void addWidgets() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        final DataHasChangedListener dataHasChangedListener
                                         = new DataHasChangedListener();

        groupName = new TextField(bundle
            .getString("ui.admin.group_edit.groupname.label"));
        groupName.setRequiredIndicatorVisible(true);
        groupName.addValueChangeListener(dataHasChangedListener);

        final Button submit = new Button();
        if (group == null) {
            submit.setCaption(bundle
                .getString("ui.admin.group.createpanel.header"));
        } else {
            submit.setCaption(bundle.getString("ui.admin.save"));
        }
        submit.addClickListener(event -> saveGroup());

        final Button cancel = new Button(bundle.getString("ui.admin.cancel"));
        cancel.addClickListener(event -> close());

        final HorizontalLayout buttons = new HorizontalLayout(submit, cancel);

        final FormLayout formLayout = new FormLayout(groupName);

        final VerticalLayout layout = new VerticalLayout(formLayout, buttons);

        final Panel panel = new Panel(layout);
        if (group == null) {
            panel.setCaption(bundle
                .getString("ui.admin.group.createpanel.header"));
        } else {
            panel.setCaption(bundle
                .getString("ui.admin.group_details.edit"));
        }

        if (group != null) {
            groupName.setValue(group.getName());
        }

        setContent(panel);
    }

    @Override
    public void close() {

        if (dataHasChanged) {
            final ResourceBundle bundle = ResourceBundle
                .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                           UI.getCurrent().getLocale());

            final ConfirmDiscardDialog dialog = new ConfirmDiscardDialog(
                this,
                bundle.getString("ui.admin.group_edit.discard_confirm"));
            dialog.setModal(true);
            UI.getCurrent().addWindow(dialog);
        } else {
            super.close();
        }
    }

    protected void saveGroup() {

        final ResourceBundle bundle = ResourceBundle
            .getBundle(AdminUiConstants.ADMIN_BUNDLE,
                       UI.getCurrent().getLocale());

        boolean valid = true;

        if (groupName.getValue() == null
                || groupName.getValue().trim().isEmpty()) {

            groupName.setComponentError(new UserError(
                bundle.getString("ui.admin.group_edit.groupname.error.notempty")));
            valid = false;
        }

        if (!valid) {
            return;
        }

        final Group currentGroup;
        final String notificationText;
        if (group == null) {

            currentGroup = new Group();
            currentGroup.setName(groupName.getValue());
            notificationText = String.format("Created new group %s",
                                             currentGroup.getName());
        } else {
            currentGroup = group;
            group.setName(groupName.getValue());
            notificationText = String.format("Saved changes to group %s",
                                             currentGroup.getName());
        }

        groupRepo.save(currentGroup);

        dataHasChanged = false;
        close();
        new Notification(notificationText, Notification.Type.TRAY_NOTIFICATION)
            .show(Page.getCurrent());
    }

    private class DataHasChangedListener
        implements HasValue.ValueChangeListener<String> {

        private static final long serialVersionUID = -1410903365203533072L;

        @Override
        public void valueChange(final HasValue.ValueChangeEvent<String> event) {
            dataHasChanged = true;
        }

    }

}
