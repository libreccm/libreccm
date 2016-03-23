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
package com.arsdigita.ui.admin.usersgroupsroles;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.NotEmptyValidationListener;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.table.TableColumnModel;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.mail.Mail;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.EmailAddress;
import org.libreccm.security.User;
import org.libreccm.security.UserManager;
import org.libreccm.security.UserRepository;

import java.util.logging.Level;

import javax.mail.MessagingException;

import static com.arsdigita.ui.admin.AdminUiConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class UserAdmin extends BoxPanel {
    
    private static final Logger LOGGER = LogManager.getLogger(UserAdmin.class);
    
    private final StringParameter userIdParameter;
    private final StringParameter emailParameter;
    private final ParameterSingleSelectionModel<String> selectedUserId;
    private final ParameterSingleSelectionModel<String> selectedEmailAddress;
    private final TextField usersTableFilter;
    private final BoxPanel usersTablePanel;
    private final UsersTable usersTable;
    private final ActionLink backToUsersTable;
    private final PropertySheet userProperties;
    private final BoxPanel actionLinks;
//    private final UserDetails userDetails;
    private final BoxPanel userDetails;
    private final Form emailForm;
    
    public UserAdmin() {
        super();
        
        setIdAttr("userAdmin");
        
        usersTablePanel = new BoxPanel();
        usersTablePanel.setIdAttr("usersTablePanel");
        
        final Form filterForm = new Form("usersTableFilterForm");
        usersTableFilter = new TextField("usersTableFilter");
        usersTableFilter.setLabel(new GlobalizedMessage(
            "ui.admin.users.table.filter.term", ADMIN_BUNDLE));
        filterForm.add(usersTableFilter);
        filterForm.add(new Submit(new GlobalizedMessage(
            "ui.admin.users.table.filter.submit", ADMIN_BUNDLE)));
        final ActionLink clearLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.users.table.filter.clear", ADMIN_BUNDLE));
        clearLink.addActionListener((e) -> {
            final PageState state = e.getPageState();
            usersTableFilter.setValue(state, null);
        });
        filterForm.add(clearLink);
        usersTablePanel.add(filterForm);
        
        userIdParameter = new StringParameter("selected_user_id");
        selectedUserId = new ParameterSingleSelectionModel<>(userIdParameter);
        //selectedUserId = new ParameterSingleSelectionModel<>(USER_ID_PARAM);

        emailParameter = new StringParameter("selected_email_address");
        selectedEmailAddress = new ParameterSingleSelectionModel<>(
            emailParameter);
        
        usersTable = new UsersTable(this, usersTableFilter, selectedUserId);
        usersTablePanel.add(usersTable);
        
        add(usersTablePanel);

//        final Text text = new Text();
//        text.setPrintListener((final PrintEvent e) -> {
//            final Text target = (Text) e.getTarget();
//            final PageState state = e.getPageState();
//            if (selectedUserId.isSelected(state)) {
//                target.setText(selectedUserId.getSelectedKey(state));
//            }
//        });
//        add(text);
//        userDetails = new UserDetails(this, selectedUserId);
//        add(new UserDetails(this, selectedUserId));
        userDetails = new BoxPanel();
        userDetails.setIdAttr("userDetails");
        
        backToUsersTable = new ActionLink(new GlobalizedMessage(
            "ui.admin.user_details.back", ADMIN_BUNDLE));
        backToUsersTable.setIdAttr("userDetailsBackLink");
        backToUsersTable.addActionListener(
            e -> closeUserDetails(e.getPageState()));
        userDetails.add(backToUsersTable);
        
        userProperties = new PropertySheet(new UserPropertySheetModelBuilder(
            this, selectedUserId));
        userProperties.setIdAttr("userProperties");
        userDetails.add(userProperties);
        
        actionLinks = new BoxPanel(BoxPanel.HORIZONTAL);
        actionLinks.setIdAttr("userDetailsActionLinks");
        final ActionLink editUserDetailsLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.edit", ADMIN_BUNDLE));
        editUserDetailsLink.addActionListener(e -> {
            //ToDo
        });
        actionLinks.add(editUserDetailsLink);
        actionLinks.add(new Text(" | "));
        
        final ActionLink setPasswordLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.set_password",
                                  ADMIN_BUNDLE));
        setPasswordLink.addActionListener(e -> {
            //ToDo
        });
        actionLinks.add(setPasswordLink);
        actionLinks.add(new Text(" | "));
        
        final ActionLink generatePasswordLink = new ActionLink(
            new GlobalizedMessage("ui.admin.user_details.generate_password",
                                  ADMIN_BUNDLE));
        generatePasswordLink.setConfirmation(new GlobalizedMessage(
            "ui.admin.user_details.generate_password.confirm",
            ADMIN_BUNDLE));
        generatePasswordLink.addActionListener(e -> {
            final UserRepository userRepository = CdiUtil.createCdiUtil().findBean(UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(
                selectedUserId.getSelectedKey(e.getPageState())));
            
            final Mail mail = new Mail(
                user.getPrimaryEmailAddress().getAddress(), 
                "libreccm.example", 
                "New password has been generated.");
            mail.setBody("Das eine Test-Email");
            
            try {
                mail.send();
            } catch (MessagingException ex) {
                LOGGER.error("Failed to send email to user.", ex);
            }
        });
        actionLinks.add(generatePasswordLink);
        userDetails.add(actionLinks);
        
        final Table primaryEmailTable = new Table();
        primaryEmailTable.setModelBuilder(
            new UserPrimaryEmailTableModelBuilder(selectedUserId));
        final TableColumnModel primaryEmailTableColModel = primaryEmailTable
            .getColumnModel();
        primaryEmailTableColModel.add(new TableColumn(
            UserPrimaryEmailTableModel.COL_ADDRESS,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.address",
                ADMIN_BUNDLE))));
        primaryEmailTableColModel.add(new TableColumn(
            UserPrimaryEmailTableModel.COL_VERIFIED,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.verified",
                ADMIN_BUNDLE))));
        primaryEmailTableColModel.add(new TableColumn(
            UserPrimaryEmailTableModel.COL_BOUNCING,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.bouncing",
                ADMIN_BUNDLE))));
        primaryEmailTableColModel.add(new TableColumn(
            UserPrimaryEmailTableModel.COL_ACTION,
            new Label(new GlobalizedMessage(
                "ui.admin.user.primary_email.action",
                ADMIN_BUNDLE))));
        primaryEmailTableColModel.get(
            UserPrimaryEmailTableModel.COL_ACTION).setCellRenderer(
                new TableCellRenderer() {
                
                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    return new ControlLink((Label) value);
                }
                
            });
        
        primaryEmailTable.addTableActionListener(new TableActionListener() {
            
            @Override
            public void cellSelected(final TableActionEvent event) {
                final String key = (String) event.getRowKey();
                selectedEmailAddress.setSelectedKey(event.getPageState(), key);
                showEmailForm(event.getPageState());
            }
            
            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }
            
        });
        
        userDetails.add(primaryEmailTable);
        
        final Table emailTable = new Table();
        emailTable.setModelBuilder(
            new UserEmailTableModelBuilder(selectedUserId));
        final TableColumnModel emailTableColumnModel = emailTable
            .getColumnModel();
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_ADDRESS,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.address",
                ADMIN_BUNDLE))));
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_VERIFIED,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.verified",
                ADMIN_BUNDLE))));
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_BOUNCING,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.bouncing",
                ADMIN_BUNDLE))));
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_EDIT,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.edit",
                ADMIN_BUNDLE))));
        emailTableColumnModel.add(new TableColumn(
            UserEmailTableModel.COL_DELETE,
            new Label(new GlobalizedMessage(
                "ui.admin.user.email_addresses.delete",
                ADMIN_BUNDLE))));
        emailTableColumnModel.get(UserEmailTableModel.COL_EDIT).setCellRenderer(
            new TableCellRenderer() {
            
            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {
                return new ControlLink((Component) value);
            }
            
        });
        emailTableColumnModel.get(UserEmailTableModel.COL_DELETE)
            .setCellRenderer(
                new TableCellRenderer() {
                
                @Override
                public Component getComponent(final Table table,
                                              final PageState state,
                                              final Object value,
                                              final boolean isSelected,
                                              final Object key,
                                              final int row,
                                              final int column) {
                    final ControlLink link = new ControlLink((Component) value);
                    if (column == UserEmailTableModel.COL_DELETE) {
                        link.setConfirmation(new GlobalizedMessage(
                            "ui.admin.user.email_addresses.delete.confirm",
                            ADMIN_BUNDLE));
                    }                    
                    return link;
                }
                
            });
        emailTable.addTableActionListener(new TableActionListener() {
            
            @Override
            public void cellSelected(final TableActionEvent event) {
                final PageState state = event.getPageState();
                
                final String key = (String) event.getRowKey();
                
                switch (event.getColumn()) {
                    case UserEmailTableModel.COL_EDIT:
                        selectedEmailAddress.setSelectedKey(state, key);
                        showEmailForm(state);
                        break;
                    case UserEmailTableModel.COL_DELETE:
                        final String userIdStr = selectedUserId.getSelectedKey(
                            state);
                        final UserRepository userRepository = CdiUtil
                            .createCdiUtil().findBean(UserRepository.class);
                        final User user = userRepository.findById(Long
                            .parseLong(userIdStr));
                        EmailAddress email = null;
                        for (EmailAddress current : user.getEmailAddresses()) {
                            if (current.getAddress().equals(key)) {
                                email = current;
                                break;
                            }
                        }
                        
                        if (email != null) {
                            user.removeEmailAddress(email);
                            userRepository.save(user);
                        }
                }
            }
            
            @Override
            public void headSelected(final TableActionEvent event) {
                //Nothing
            }
            
        });
        emailTable.setEmptyView(new Label(new GlobalizedMessage(
            "ui.admin.user.email_addresses.none", ADMIN_BUNDLE)));
        
        userDetails.add(emailTable);
        
        final ActionLink addEmailLink = new ActionLink(new GlobalizedMessage(
            "ui.admin.user.email_addresses.add", ADMIN_BUNDLE));
        addEmailLink.addActionListener(e -> {
            showEmailForm(e.getPageState());
        });
        userDetails.add(addEmailLink);
        
        emailForm = new Form("email_form");
//        emailForm.add(new Label(new GlobalizedMessage(
//            "ui.admin.user.email_form.address",
//            ADMIN_BUNDLE)));
        final TextField emailFormAddress = new TextField("email_form_address");
        emailFormAddress.setLabel(new GlobalizedMessage(
            "ui.admin.user.email_form.address", ADMIN_BUNDLE));
        emailFormAddress.addValidationListener(new NotEmptyValidationListener(
            new GlobalizedMessage("ui.admin.user.email_form.address.not_empty",
                                  ADMIN_BUNDLE)));
        emailForm.add(emailFormAddress);
        final CheckboxGroup emailFormVerified = new CheckboxGroup(
            "email_form_verified");
        emailFormVerified.addOption(
            new Option("true",
                       new Label(new GlobalizedMessage(
                           "ui.admin.user.email_form.verified",
                           ADMIN_BUNDLE))));
        emailForm.add(emailFormVerified);
        final CheckboxGroup emailFormBouncing = new CheckboxGroup(
            "email_form_bouncing");
        emailFormBouncing.addOption(
            new Option("true",
                       new Label(new GlobalizedMessage(
                           "ui.admin.user.email_form.bouncing",
                           ADMIN_BUNDLE))));
        emailForm.add(emailFormBouncing);
        
        emailForm.add(new SaveCancelSection());
        
        emailForm.addInitListener(e -> {
            final PageState state = e.getPageState();
            
            final String selected = selectedEmailAddress.getSelectedKey(state);
            final String userIdStr = selectedUserId.getSelectedKey(state);
            if (selected != null && !selected.isEmpty()) {
                final UserRepository userRepository = CdiUtil.createCdiUtil()
                    .findBean(UserRepository.class);
                final User user = userRepository.findById(Long.parseLong(
                    userIdStr));
                EmailAddress email = null;
                if (user.getPrimaryEmailAddress().getAddress().equals(selected)) {
                    email = user.getPrimaryEmailAddress();
                } else {
                    for (EmailAddress current : user.getEmailAddresses()) {
                        if (current.getAddress().equals(selected)) {
                            email = current;
                            break;
                        }
                    }
                }
                
                if (email != null) {
                    emailFormAddress.setValue(state, email.getAddress());
                    if (email.isVerified()) {
                        emailFormVerified.setValue(state, "true");
                    }
                    if (email.isBouncing()) {
                        emailFormBouncing.setValue(state, "true");
                    }
                }
            }
        });
        
        emailForm.addProcessListener(e -> {
            final PageState state = e.getPageState();
            
            final String selected = selectedEmailAddress.getSelectedKey(state);
            final String userIdStr = selectedUserId.getSelectedKey(state);
            
            final UserRepository userRepository = CdiUtil.createCdiUtil()
                .findBean(UserRepository.class);
            final User user = userRepository.findById(Long.parseLong(
                userIdStr));
            EmailAddress email = null;
            if (selected == null) {
                email = new EmailAddress();
                user.addEmailAddress(email);
            } else if (user.getPrimaryEmailAddress().getAddress().equals(
                selected)) {
                email = user.getPrimaryEmailAddress();
            } else {
                for (EmailAddress current : user.getEmailAddresses()) {
                    if (current.getAddress().equals(selected)) {
                        email = current;
                        break;
                    }
                }
            }
            
            if (email != null) {
                email.setAddress((String) emailFormAddress.getValue(state));
                
                final String[] verifiedValues = (String[]) emailFormVerified
                    .getValue(state);
                if (verifiedValues != null && verifiedValues.length > 0) {
                    if ("true".equals(verifiedValues[0])) {
                        email.setVerified(true);
                    } else {
                        email.setVerified(false);
                    }
                } else {
                    email.setVerified(false);
                }
                
                final String[] bouncingValues = (String[]) emailFormBouncing
                    .getValue(state);
                if (bouncingValues != null && bouncingValues.length > 0) {
                    if ("true".equals(bouncingValues[0])) {
                        email.setBouncing(true);
                    } else {
                        email.setBouncing(false);
                    }
                } else {
                    email.setBouncing(false);
                }
            }
            
            userRepository.save(user);
            closeEmailForm(e.getPageState());
        });
        
        emailForm.addCancelListener(e -> closeEmailForm(e.getPageState()));
        
        add(emailForm);
        
        add(userDetails);
        
    }
    
    @Override
    public void register(final Page page) {
        super.register(page);
        
        page.addGlobalStateParam(userIdParameter);
        page.addGlobalStateParam(emailParameter);
        
        page.setVisibleDefault(usersTablePanel, true);
        page.setVisibleDefault(userDetails, false);
        page.setVisibleDefault(emailForm, false);
    }
    
    protected void showUserDetails(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        emailForm.setVisible(state, false);
    }
    
    protected void closeUserDetails(final PageState state) {
        selectedUserId.clearSelection(state);
        usersTablePanel.setVisible(state, true);
        userDetails.setVisible(state, false);
        emailForm.setVisible(state, false);
    }
    
    protected void showEmailForm(final PageState state) {
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, false);
        emailForm.setVisible(state, true);
    }
    
    protected void closeEmailForm(final PageState state) {
        selectedEmailAddress.clearSelection(state);
        usersTablePanel.setVisible(state, false);
        userDetails.setVisible(state, true);
        emailForm.setVisible(state, false);
    }
    
}
