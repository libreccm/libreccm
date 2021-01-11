/*
 * Copyright (C) 2021 LibreCCM Foundation.
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

import org.libreccm.l10n.GlobalizationHelper;

import java.text.MessageFormat;
import java.util.AbstractMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Base class for implementing a named bean which provides easy access to a
 * resource bundle from an MVC template.. The only method to implement is
 * {@link #getResourceBundle} which provides the name of the resource bundle to
 * use. An implementation must also be annotated with the following annotations:
 * <ul>
 * <li>A scope annotation like {@link RequestScoped} or
 * {@link ApplicationScoped}.</li>
 * <li>The {@link Named} annotation to make the implementing bean available in
 * MVC templates.</li>
 * </ul>
 *
 * By default the name under which the bean will available is the simple class
 * name, but starting with a lower case letter. A different name can be provided
 * using the value of the {@link Named} annotation. For example by default the
 * bean {@code com.example.ui.ExampleMessages} would be available as
 * {@code exampleMessages}. To make it available as {@code ExampleMessages} the
 * {@code @Named} annotation has to look like this:
 * {@code @Named("ExamplesMessages")}. This bean will be used to illustrate the
 * usage of an implementation in the following examples.
 *
 * For simple messages an implementation of this class can be used like a map in
 * MVC templates, for example in a Facelets template to get the message with the
 * key {@code some.message.key} you can simple write
 * <pre>
 * #{ExampleMessages['some.message.key']}
 * </pre>
 *
 * Messages with placeholders can be retrieved using {@link {@link #getMessage(java.lang.String, java.util.List)} or
 * {@link #getMessage(java.lang.String, java.lang.Object[])}, for example in a
 * Facelets template
 * <pre>
 * #{ExampleMessages.getMessage('some.message.with.placeholder', ['foo', 'bar']
 * </pre>
 *
 *
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AbstractMessagesBean extends AbstractMap<String, String> {

    /**
     * Provides access to the locale negoiated by LibreCCM.
     */
    @Inject
    private GlobalizationHelper globalizationHelper;

    /**
     * The {@link ResourceBundle} to use.
     */
    private ResourceBundle messages;

    protected abstract String getMessageBundle();

    /**
     * Loads the resource bundle.
     */
    @PostConstruct
    private void init() {
        messages = ResourceBundle.getBundle(
            getMessageBundle(),
            globalizationHelper.getNegotiatedLocale()
        );
    }

    /**
     * Retrieves a message from the resource bundle.
     *
     * @param key The key of the message.
     *
     * @return The translated message or {@code ???message???} if the the key is
     *         not found in the resource bundle (message is replaced with the
     *         key).
     */
    public String getMessage(final String key) {
        if (messages.containsKey(key)) {
            return messages.getString(key);
        } else {
            return String.format("???%s???", key);
        }
    }

    /**
     * Retrieves a message with placeholders.
     *
     * @param key        The key of the message.
     * @param parameters The parameters for the placeholders.
     *
     * @return The translated message or {@code ???message???} if the the key is
     *         not found in the resource bundle (message is replaced with the
     *         key).
     */
    public String getMessage(
        final String key, final List<Object> parameters
    ) {
        return getMessage(key, parameters.toArray());
    }

    /**
     * The translated message or {@code ???message???} if the the key is not
     * found in the resource bundle (message is replaced with the key).
     *
     * @param key        The key of the message.
     * @param parameters The parameters for the placeholders.
     *
     * @return The translated message or {@code ???message???} if the the key is
     *         not found in the resource bundle (message is replaced with the
     *         key).
     */
    public String getMessage(
        final String key, final Object[] parameters
    ) {
        if (messages.containsKey(key)) {
            return MessageFormat.format(messages.getString(key), parameters);
        } else {
            return String.format("???%s???", key);
        }
    }

    @Override
    public String get(final Object key) {
        return get((String) key);
    }

    public String get(final String key) {
        return getMessage(key);
    }

    @Override
    public Set<Entry<String, String>> entrySet() {
        return messages
            .keySet()
            .stream()
            .collect(
                Collectors.toMap(key -> key, key -> messages.getString(key))
            )
            .entrySet();
    }

}
