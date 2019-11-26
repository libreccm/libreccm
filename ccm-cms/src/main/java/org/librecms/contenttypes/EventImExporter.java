/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contenttypes;

import org.librecms.contentsection.AbstractContentItemImExporter;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class EventImExporter extends AbstractContentItemImExporter<Event> {

    @Override
    protected Class<Event> getEntityClass() {
        return Event.class;
    }

}
