/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.ui;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public enum CmsMessagesContext {

    PRIMARY("primary"),
    SECONDARY("secondary"),
    SUCCESS("success"),
    DANGER("danger"),
    WARNING("warning"),
    INFO("info"),
    LIGHT("light"),
    DARK("dark");

    private final String value;

    CmsMessagesContext(final String context) {
        this.value = context;
    }
    
    public String getValue() {
        return value;
    }

}
