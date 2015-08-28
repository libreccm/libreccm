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
package org.libreccm.core.modules;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ModuleInfo {

    private static final String ARTIFACT_ID = "artifactId";
    private static final String GROUP_ID = "groupId";
    private static final String VERSION = "version";

    private static final Logger LOGGER = LogManager.getLogger(ModuleInfo.class);

    private transient String moduleName;
    private transient String moduleDataPackage;
    private transient Class<?>[] moduleEntities;
    private transient String moduleVersion;
    private transient RequiredModule[] requiredModules;

    public ModuleInfo() {
        //Nothing
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleDataPackage() {
        return moduleDataPackage;
    }

    public String getDbSchemaName() {
        return moduleName.toLowerCase(Locale.ROOT).replace("-", "_");
    }

    public String getDbScriptsLocation(final Connection connection)
        throws SQLException {
        final StringBuffer buffer
                               = new StringBuffer("classpath:/db/migrations/");
        buffer.append(moduleDataPackage);
        switch (connection.getMetaData().getDatabaseProductName()) {
            case "H2": {
                buffer.append("/h2");
                break;
            }
            case "MySQL":
                buffer.append("/mysql");
                break;
            case "PostgreSQL":
                buffer.append("/postgresql");
                break;
            default:
                throw new IntegrationException(String.format(
                    "Integration failed. Database \"%s\" is not supported yet.",
                    connection.getMetaData().
                    getDatabaseProductName()));
        }

        return buffer.toString();
    }

    public String getModuleVersion() {
        return moduleVersion;
    }

    public List<Class<?>> getModuleEntities() {
        return Collections.unmodifiableList(Arrays.asList(moduleEntities));
    }

    public List<RequiredModule> getRequiredModules() {
        return Collections.unmodifiableList(Arrays.asList(requiredModules));
    }

    public void load(final CcmModule module) {
        load(module.getClass());
    }

    public void load(final Class<? extends CcmModule> moduleClass) {
        LOGGER.info("Reading module info for {}...", moduleClass.getName());
        final Module annotation = moduleClass.getAnnotation(Module.class);

        final Properties properties = loadModuleInfoFile(moduleClass);
//        final Properties properties = new Properties();
//
//        final String path = String.format("/module-info/%s.properties",
//                                          moduleClass.getName());
//        LOGGER.info("Trying to retrieve module info for module {} from {}...",
//                    moduleClass.getName(),
//                    path);
//        final InputStream stream = moduleClass.getResourceAsStream(path);
//        if (stream == null) {
//            LOGGER.warn("No module info for {} found at {}",
//                        moduleClass.getName(),
//                        path);
//        } else {
//            try {
//                properties.load(stream);
//            } catch (IOException ex) {
//                LOGGER.error("Failed to read module info for {} at {}.",
//                             moduleClass.getName(),
//                             path);
//                LOGGER.error("Cause: ", ex);
//            }
//        }

        LOGGER.info("Reading module name...");
//        if (annotation.name() != null && !annotation.name().isEmpty()) {
//            moduleName = annotation.name();
//        } else if (properties.getProperty(ARTIFACT_ID) != null
//                       && !properties.getProperty(ARTIFACT_ID).isEmpty()) {
//            moduleName = properties.getProperty(ARTIFACT_ID);
//        } else {
//            LOGGER.warn(
//                "The module was not specificied by the module annotation "
//                    + "or by the module info file. Creating name from "
//                    + "simple name of the module class.");
//            moduleName = moduleClass.getSimpleName().toLowerCase();
//        }
        LOGGER.info("Reading module name...");
        moduleName = readModuleName(moduleClass, annotation, properties);
        LOGGER.info("Module name is \"{}\".", moduleName);

        LOGGER.info("Reading module package name...");
//        if (annotation.packageName() != null
//                && !annotation.packageName().isEmpty()) {
//            moduleDataPackage = annotation.packageName();
//        } else if (properties.getProperty(GROUP_ID) != null
//                       && !properties.getProperty(GROUP_ID).isEmpty()) {
//            moduleDataPackage = String.format("%s/%s",
//                                              properties.getProperty(GROUP_ID),
//                                              properties.
//                                              getProperty(ARTIFACT_ID).replace(
//                                                  "-", "_"));
//        } else {
//            LOGGER.warn("The module data package was specified by the module "
//                            + "annotation nore was an group id found in the module info"
//                        + "file. Creating data package name from the name of the "
//                        + "module class.");
//            moduleDataPackage = moduleClass.getName().toLowerCase();
//        }
        moduleDataPackage = readModulePackageName(moduleClass,
                                                  annotation,
                                                  properties);
        LOGGER.info("Module data package is \"{}\".", moduleDataPackage);

        LOGGER.info("Reading module version...");
//        if (annotation.version() != null && !annotation.version().isEmpty()) {
//            moduleVersion = annotation.version();
//        } else if (properties.getProperty(VERSION) != null
//                       && !properties.getProperty(VERSION).isEmpty()) {
//            moduleVersion = properties.getProperty(VERSION);
//        } else {
//            LOGGER.warn("Module version is not specified by the module "
//                            + "annotation or in the module info file. Module version is "
//                        + "undefinied. This can lead to all sorts of strange errors!");
//        }
        moduleVersion = readModuleVersion(annotation, properties);
        LOGGER.info("Module version is \"{}.\"", moduleVersion);

        requiredModules = annotation.requiredModules();
        moduleEntities = annotation.entities();
    }

    private Properties loadModuleInfoFile(
        final Class<? extends CcmModule> moduleClass) {

        final Properties moduleInfo = new Properties();
        final String path = String.format("/module-info/%s.properties",
                                          moduleClass.getName());
        LOGGER.info("Trying to retrieve module info for module {} from {}...",
                    moduleClass.getName(),
                    path);
        final InputStream stream = moduleClass.getResourceAsStream(path);
        if (stream == null) {
            LOGGER.warn("No module info for {} found at {}",
                        moduleClass.getName(),
                        path);
        } else {
            try {
                moduleInfo.load(stream);
            } catch (IOException ex) {
                LOGGER.error("Failed to read module info for {} at {}.",
                             moduleClass.getName(),
                             path);
                LOGGER.error("Cause: ", ex);
            }
        }

        return moduleInfo;
    }

    private String readModuleName(final Class<? extends CcmModule> moduleClass,
                                  final Module annotation,
                                  final Properties moduleInfo) {
        @SuppressWarnings("PMD.LongVariable")
        final boolean annotationHasModuleName = annotation.name() != null
                                              && !annotation.name().isEmpty();
        @SuppressWarnings("PMD.LongVariable")
        final boolean moduleInfoHasModuleName = moduleInfo.getProperty(ARTIFACT_ID)
                                              != null && !moduleInfo
            .getProperty(ARTIFACT_ID).isEmpty();

        if (annotationHasModuleName) {
            return annotation.name();
        } else if (moduleInfoHasModuleName) {
            return moduleInfo.getProperty(ARTIFACT_ID);
        } else {
            LOGGER.warn(
                "The module was not specificied by the module annotation "
                    + "or by the module info file. Creating name from "
                    + "simple name of the module class.");
            return moduleClass.getSimpleName().toLowerCase();
        }
    }

    private String readModulePackageName(
        final Class<? extends CcmModule> moduleClass,
        final Module annotation,
        final Properties moduleInfo) {

        @SuppressWarnings("PMD.LongVariable")
        final boolean annotationHasPackageName = annotation.packageName() != null
                                               && !annotation.packageName()
            .isEmpty();
        @SuppressWarnings("PMD.LongVariable")
        final boolean moduleInfoHasPackageName = moduleInfo.getProperty(GROUP_ID)
                                               != null
                                               && !moduleInfo.getProperty(
                GROUP_ID).isEmpty();
        if (annotationHasPackageName) {
            return annotation.packageName();
        } else if (moduleInfoHasPackageName) {
            return String.format("%s/%s",
                                 moduleInfo.getProperty(GROUP_ID),
                                 moduleInfo.
                                 getProperty(ARTIFACT_ID).replace(
                                     "-", "_"));
        } else {
            LOGGER.warn("The module data package was specified by the module "
                            + "annotation nore was an group id found in the module info"
                        + "file. Creating data package name from the name of the "
                        + "module class.");
            return moduleClass.getName().toLowerCase();
        }
    }

    private String readModuleVersion(
        final Module annotation,
        final Properties moduleInfo) {

        @SuppressWarnings("PMD.LongVariable")
        final boolean annotationHasVersion = annotation.version() != null
                                           && !annotation.version().isEmpty();
        @SuppressWarnings("PMD.LongVariable")
        final boolean moduleInfoHasVersion = moduleInfo.getProperty(VERSION) != null
                                           && !moduleInfo.getProperty(VERSION)
            .isEmpty();

        if (annotationHasVersion) {
            return annotation.version();
        } else if (moduleInfoHasVersion) {
            return moduleInfo.getProperty(VERSION);
        } else {
            LOGGER.warn("Module version is not specified by the module "
                            + "annotation or in the module info file. Module version is "
                        + "undefinied. This can lead to all sorts of strange errors!");
            return "";
        }
    }

}
