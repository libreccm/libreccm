/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.categorization;

import org.libreccm.imexport.Exportable;

import java.util.Set;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public interface CategorizationImExporterDependenciesProvider {

    Set<Class<Exportable>> getCategorizableEntities();

}
