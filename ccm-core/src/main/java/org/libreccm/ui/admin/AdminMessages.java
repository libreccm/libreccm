/*
 * Copyright (C) 2020 LibreCCM Foundation.
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
package org.libreccm.ui.admin;

import org.libreccm.ui.AbstractMessagesBean;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Provides simple access to the messages in the admin bundle. The make it as
 * easy as possible to access the messages this class is implemented as a map a
 * made available as named bean. For simple messages, {@code AdminMesssages} can
 * be used like a map in a facelets template:
 *
 * <pre>
 * #{AdminMessages['some.message.key'])
 * </pre>
 *
 * Messages with placeholders can be retrieved using
 * {@link #getMessage(java.lang.String, java.util.List)} or
 * {@link #getMessage(java.lang.String, java.lang.Object[])}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("AdminMessages")
public class AdminMessages extends AbstractMessagesBean {

    @Override
    protected String getMessageBundle() {
        return AdminConstants.ADMIN_BUNDLE;
    }

//    /**
//     * Provides access to the locale negoiated by LibreCCM.
//     */
//    @Inject
//    private GlobalizationHelper globalizationHelper;
//
//    /**
//     * The {@link ResourceBundle} to use.
//     */
//    private ResourceBundle messages;
//
//    /**
//     * Loads the resource bundle.
//     */
//    @PostConstruct
//    private void init() {
//        messages = ResourceBundle.getBundle(
//            AdminConstants.ADMIN_BUNDLE,
//            globalizationHelper.getNegotiatedLocale()
//        );
//    }
//
//    /**
//     * Retrieves a message from the resource bundle.
//     * 
//     * @param key The key of the message.
//     * @return The translated message or {@code ???message???} if the the key is
//     * not found in the resource bundle (message is replaced with the key).
//     */
//    public String getMessage(final String key) {
//        if (messages.containsKey(key)) {
//            return messages.getString(key);
//        } else {
//            return String.format("???%s???", key);
//        }
//    }
//
//    /**
//     * Retrieves a message with placeholders.
//     * 
//     * @param key The key of the message.
//     * @param parameters The parameters for the placeholders.
//     * @return The translated message or {@code ???message???} if the the key is
//     * not found in the resource bundle (message is replaced with the key).
//     */
//    public String getMessage(
//        final String key, final List<Object> parameters
//    ) {
//        return getMessage(key, parameters.toArray());
//    }
//
//    /**
//     * The translated message or {@code ???message???} if the the key is
//     * not found in the resource bundle (message is replaced with the key).
//     * 
//      @param key The key of the message.
//     * @param parameters The parameters for the placeholders.
//     * @return The translated message or {@code ???message???} if the the key is
//     * not found in the resource bundle (message is replaced with the key).
//     */
//    public String getMessage(
//        final String key, final Object[] parameters
//    ) {
//        if (messages.containsKey(key)) {
//            return MessageFormat.format(messages.getString(key), parameters);
//        } else {
//            return String.format("???%s???", key);
//        }
//    }
//
//    @Override
//    public String get(final Object key) {
//        return get((String) key);
//    }
//    
//    public String get(final String key) {
//        return getMessage(key);
//    }
//
//    @Override
//    public Set<Entry<String, String>> entrySet() {
//        return messages
//            .keySet()
//            .stream()
//            .collect(
//                Collectors.toMap(key -> key, key -> messages.getString(key))
//            )
//            .entrySet();
//    }

}
