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

import com.arsdigita.runtime.RegistryConfig;

import java.util.Arrays;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ModuleManager {

    @Inject
    private transient Instance<ModuleDescriptor> modules;

    /**
     * Checks for new or upgraded modules and executes database migrations if
     * necessary. If a new module is installed the database tables for this
     * module are generated first. After that the {@code prepare()} method of
     * the module is called (see {@link ModuleDescriptor#prepare()}).
     */
    public void loadModules() {
        
    }

    /**
     * Checks if a module is already installed.
     *
     * @param moduleDescriptor The descriptor of the module.
     *
     * @return {@code true} if the module is already installed, {@code false}
     *         otherwise.
     */
    private boolean isInstalled(final ModuleDescriptor moduleDescriptor) {
        final RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.load();
        final String[] packages = registryConfig.getPackages();
        final List<String> packageList = Arrays.asList(packages);
        
        return packageList.contains(ModuleUtil.getModuleName(moduleDescriptor));
    }

    /**
     * Called to uninstall a module. First the {@code uninstal()} method of the
     * module is called (see {@link ModuleDescriptor#uninstall()}). After that
     * the database tables of the module are removed.
     *
     * @param module The module to uninstall.
     */
    public void uninstallModule(final ModuleDescriptor module) {

    }

    /**
     * Initialises all modules by calling their {@code init()} method (see
     * {@link ModuleDescriptor#init()}.
     */
    public void initModules() {

    }

    /**
     * Shutdown all modules by calling their {@link shutdown()} method (see
     * {@link ModuleDescriptor#shutdown()}).
     */
    public void shutdownModules() {

    }

}
