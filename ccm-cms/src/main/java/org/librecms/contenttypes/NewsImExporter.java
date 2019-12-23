/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.librecms.contenttypes;

import org.libreccm.imexport.Processes;
import org.librecms.contentsection.AbstractContentItemImExporter;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Processes(News.class)
public class NewsImExporter extends AbstractContentItemImExporter<News> {

    @Override
    protected Class<News> getEntityClass() {
        return News.class;
    }

}
