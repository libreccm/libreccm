/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.util.LockableImpl;

import org.libreccm.core.EmailAddress;

import static com.arsdigita.ui.admin.AdminConstants.*;

import java.util.Iterator;

/**
 * Used to display and manage the list of additional email addresses
 * for a user.
 */

class EmailList extends List
    implements ListCellRenderer,
               AdminConstants,
               ActionListener
{

    /**
     * Constructor
     */
    public EmailList() {
        setModelBuilder(new EmailListModelBuilder());
        setCellRenderer(this);
        addActionListener(this);
    }

    /** 
     * 
     * @param list
     * @param state
     * @param value
     * @param key
     * @param index
     * @param isSelected
     * @return
     */
    @Override
    public Component getComponent(List list,
                                  PageState state,
                                  Object value,
                                  String key,
                                  int index,
                                  boolean isSelected)
    {
        SimpleContainer c = new SimpleContainer();

        if (value != null) {
            ControlLink link =
                new ControlLink(USER_FORM_DELETE_ADDITIONAL_EMAIL);
            link.setClassAttr("deleteLink");

            c.add(new Label(value.toString()));
            c.add(link);
        }

        return c;
    }

    /**
     * This actionlister is executed when the user clicks the "delete"
     * link next to an email address.
     */
    @Override
    public void actionPerformed (final ActionEvent event) {
        final PageState state = event.getPageState();

        final Long userId = (Long) state.getValue(USER_ID_PARAM);
        if (userId != null) {
//            final CdiUtil cdiUtil = new CdiUtil();
//            final UserRepository userRepository;

//                userRepository = cdiUtil.findBean(UserRepository.class);
//            
//            final User user = userRepository.findById(userId);
//            if (user == null) {
//                return;
//            } else {
//                final String email = (String) getSelectedKey(state);
//                
//                for(EmailAddress addr : user.getEmailAddresses()) {
//                    if (addr.getAddress().equals(email)) {
//                        user.removeEmailAddress(addr);
//                    }
//                }
//                
//                userRepository.save(user);
//            }
        }
    }
}

/**
 * 
 * 
 */
class EmailListModelBuilder extends LockableImpl
    implements ListModelBuilder,
               AdminConstants
{

    /**
     * 
     */
    private class EmailListModel implements ListModel {
        private Iterator m_emails;
        private EmailAddress m_currentEmail;

        /**
         * 
         * @param emails
         */
        public EmailListModel(Iterator emails) {
            m_emails = emails;
        }

        /**
         * 
         * @return
         */
        public boolean next() {
            if (m_emails.hasNext()) {
                m_currentEmail = (EmailAddress) m_emails.next();
                return true;
            } else {
                return false;
            }
        }

        /**
         * 
         * @return
         */
        @Override
        public String getKey() {
            return m_currentEmail.getAddress();
        }

        @Override
        public Object getElement() {
            return m_currentEmail.getAddress();
        }
    }

    /**
     * 
     * @param l
     * @param state
     * @return
     */
    @Override
    public ListModel makeModel(List l, PageState state) {

        return null;
        
//        final Long userId = (Long) state.getValue(USER_ID_PARAM);
//        if (userId == null) {
//            return null;
//        } else {
//            final CdiUtil cdiUtil = new CdiUtil();
//            final UserRepository userRepository;

//                userRepository = cdiUtil.findBean(UserRepository.class);
//            final User user = userRepository.findById(userId);
//            
//            return new EmailListModel(user.getEmailAddresses().iterator());
//        }
    }
}
