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
package org.librecms.ui;

import java.util.Collections;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;

/**
 * Provides a facility for setting messages.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Named("CmsMessages")
public class CmsMessages {

    private SortedMap<String, String> messages;

    public Map<String, String> getMessages() {
        return Collections.unmodifiableSortedMap(messages);
    }

    public void addMessage(
        final CmsMessagesContext context, final String message
    ) {
        messages.put(context.getValue(), message);
    }

    public void setMessages(final SortedMap<String, String> messages) {
        this.messages = new TreeMap<>(messages);
    }


}
