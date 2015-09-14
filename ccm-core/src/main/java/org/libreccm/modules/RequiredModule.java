/*
 * Copyright (C) 2015 LibreCCM Foundation.
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
package org.libreccm.modules;

/**
 * Annotation for describing a dependency relation between a module and another
 * module.
 * 
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public @interface RequiredModule {
    
    /**
     * The module class required by the module.
     * 
     * @return 
     */
    Class<? extends CcmModule> module();
    
    /**
     * The minimal version required by the module.
     * @return 
     */
    String minVersion() default "";
    
    /**
     * The maximum version required by the module.
     * 
     * @return 
     */
    String maxVersion() default "";
    
}
