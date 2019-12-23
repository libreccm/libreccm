/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contenttypes;

import org.libreccm.imexport.Processes;
import org.librecms.contentsection.AbstractContentItemImExporter;

import javax.enterprise.context.RequestScoped;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@RequestScoped
@Processes(Event.class)
public class EventImExporter extends AbstractContentItemImExporter<Event> {

    @Override
    protected Class<Event> getEntityClass() {
        return Event.class;
    }

}
