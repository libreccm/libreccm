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

import org.libreccm.modules.annotations.Module;
import org.libreccm.modules.annotations.RequiredModule;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class ModuleUtil {

    private ModuleUtil() {
        //Nothing
    }

    private static Module getModuleAnnotation(
        final ModuleDescriptor module) {
        final Module annotation = module.getClass().getAnnotation(Module.class);

        if (annotation == null) {
            throw new IllegalArgumentException(String.format(
                "The provided implementation of the "
                    + "org.libreccm.modules.Module interface, \"%s\", is not"
                    + "annotated with org.libreccm.modules.annotations.Module",
                module.getClass().getName()));
        } else {
            return annotation;
        }
    }

    public static String getModuleName(final ModuleDescriptor module) {
        return getModuleAnnotation(module).name();
    }

    public static String getVersion(final ModuleDescriptor module) {
        return getModuleAnnotation(module).version();
    }

    public static RequiredModule[] getRequiredModules(final ModuleDescriptor module) {
        return getModuleAnnotation(module).requiredModules();
    }

    public static String getModuleName(
        final Class<? extends ModuleDescriptor> module) {
        
        final Module annotation = module.getAnnotation(Module.class);

        if (annotation == null) {
            throw new IllegalArgumentException(String.format(
                "The provided implementation of the "
                    + "org.libreccm.modules.Module interface, \"%s\", is not"
                    + "annotated with org.libreccm.modules.annotations.Module",
                module.getClass().getName()));
        } else {
            return annotation.name();
        }

    }

}
