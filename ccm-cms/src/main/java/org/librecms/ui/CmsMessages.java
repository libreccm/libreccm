/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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
