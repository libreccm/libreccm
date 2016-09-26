/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.testutils;

import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.jboss.shrinkwrap.resolver.api.maven.PomEquippedResolveStage;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Some static helper methods to deal with the dependencies in Arquillian tests.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public final class DependenciesHelpers {

    /**
     * Private constructor to avoid accidental instantiation of this class.
     */
    private DependenciesHelpers() {

    }

    /**
     * This method implements the common part of the public methods in this
     * class.
     *
     * @param pomPath The path of the POM file to use.
     *
     * @return The dependencies defined in the POM file including their
     *         transitive dependencies. All dependencies whose name starts with
     *         {@code ccm-} (which should be modules of {@code LibreCCM} are
     *         excluded. Classes from CCM modules which are required for tests
     *         have to be included manually using the {@code addPackage} or
     *         {@code addClass} methods from ShrinkWrap.
     */
    private static File[] getDependenciesFromPom(final String pomPath) {
        final PomEquippedResolveStage pom = Maven
            .resolver()
            .loadPomFromFile(pomPath);
        final PomEquippedResolveStage dependencies = pom
            .importCompileAndRuntimeDependencies();
        final File[] libFiles = dependencies.resolve().withTransitivity()
            .asFile();

        final File[] libs = Arrays.stream(libFiles)
            .filter(lib -> !lib.getName().startsWith("ccm-"))
            .collect(Collectors.toList()).toArray(new File[0]);
        Arrays.stream(libs)
            .forEach(lib -> System.err.printf(
                "Found dependency '%s'...%n",
                lib.getName()));

        return libs;
    }

    /**
     * Gets the dependencies of the current module.
     *
     * @return The dependencies of the current module.
     *
     * @see #getDependenciesFromPom(java.lang.String)
     */
    public static File[] getModuleDependencies() {
        return getDependenciesFromPom("pom.xml");
    }

    /**
     * Gets the dependencies for another CCM module.
     *
     * @param module The module
     *
     * @return The dependencies of the module.
     *
     * @see #getDependenciesFromPom(java.lang.String)
     */
    public static File[] getDependenciesFromModule(final String module) {
        return getDependenciesFromPom(String.format("../%s/pom.xml",
                                                    module));
    }

    /**
     * Convenient method for getting the the dependencies of the
     * {@code ccm-core} module.
     *
     * @return The dependencies of the {@code ccm-core} module.
     * @see #getDependenciesFromModule(java.lang.String) 
     * @see #getDependenciesFromPom(java.lang.String) 
     */
    public static File[] getCcmCoreDependencies() {
        return getDependenciesFromModule("ccm-core");
    }

}
