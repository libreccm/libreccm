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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A representation of the module metadata combining the data from the modules
 * info file and the annotations on the module class.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ModuleInfo {

    /**
     * Constant for the artifactId property in the module info file.
     */
    private static final String ARTIFACT_ID = "artifactId";
    /**
     * Constant for the groupId property in the module info file.
     */
    private static final String GROUP_ID = "groupId";
    /**
     * Constant for the version property in the module info file.
     */
    private static final String VERSION = "version";

    private static final Logger LOGGER = LogManager.getLogger(ModuleInfo.class);

    /**
     * The name of the module (artifact id).
     */
    private String moduleName;
    /**
     * The data package of the module (group id).
     */
    private String moduleDataPackage;
    /**
     * The version of the module.
     */
    private String moduleVersion;
    /**
     * The modules required by the described module.
     */
    private RequiredModule[] requiredModules;

    public ModuleInfo() {
        //Nothing
    }

    public String getModuleName() {
        return moduleName;
    }

    public String getModuleDataPackage() {
        return moduleDataPackage;
    }

//    /**
//     * Gets the database schema name for the module. That is the 
//     * {@link moduleName} with all hyphens replaced by underscore (hyphens can 
//     * be nasty in SQL).
//     * 
//     * @return The name of the database schema of the module.
//     */
//    public String getDbSchemaName() {
//        return moduleName.toLowerCase(Locale.ROOT).replace("-", "_");
//    }
//    /**
//     * 
//     * @param connection
//     * @return
//     * @throws SQLException 
//     */
//    public String getDbScriptsLocation(final Connection connection)
//        throws SQLException {
//        final StringBuffer buffer
//                               = new StringBuffer("classpath:/db/migrations/");
//        buffer.append(moduleDataPackage);
//        switch (connection.getMetaData().getDatabaseProductName()) {
//            case "H2": {
//                buffer.append("/h2");
//                break;
//            }
//            case "MySQL":
//                buffer.append("/mysql");
//                break;
//            case "PostgreSQL":
//                buffer.append("/postgresql");
//                break;
//            default:
//                throw new IntegrationException(String.format(
//                    "Integration failed. Database \"%s\" is not supported yet.",
//                    connection.getMetaData().
//                    getDatabaseProductName()));
//        }
//
//        return buffer.toString();
//    }
    public String getModuleVersion() {
        return moduleVersion;
    }

    public List<RequiredModule> getRequiredModules() {
        return Collections.unmodifiableList(Arrays.asList(requiredModules));
    }

    public void load(final CcmModule module) {
        load(module.getClass());
    }

    /**
     * Loads the module info data.
     *
     * @param moduleClass The module class for which the module data is loaded.
     */
    public void load(final Class<? extends CcmModule> moduleClass) {
        LOGGER.info("Reading module info for {}...", moduleClass.getName());
        final Module annotation = moduleClass.getAnnotation(Module.class);

        final Properties properties = loadModuleInfoFile(moduleClass);

        LOGGER.info("Reading module name...");
        LOGGER.info("Reading module name...");
        moduleName = readModuleName(moduleClass, annotation, properties);
        LOGGER.info("Module name is \"{}\".", moduleName);

        LOGGER.info("Reading module package name...");
        moduleDataPackage = readModulePackageName(moduleClass,
                                                  annotation,
                                                  properties);
        LOGGER.info("Module data package is \"{}\".", moduleDataPackage);

        LOGGER.info("Reading module version...");
        moduleVersion = readModuleVersion(annotation, properties);
        LOGGER.info("Module version is \"{}.\"", moduleVersion);

        requiredModules = annotation.requiredModules();
    }

    /**
     * Load the module info properties file.
     *
     * @param moduleClass The class for which the module info properties file is
     * loaded.
     *
     * @return The properties from the module info properties file.
     */
    private Properties loadModuleInfoFile(
            final Class<? extends CcmModule> moduleClass) {

        final Properties moduleInfo = new Properties();
        final String path = String.format("/module-info/%s.properties",
                                          moduleClass.getName());
        LOGGER.info("Trying to retrieve module info for module {} from {}...",
                    moduleClass.getName(),
                    path);
        try (final InputStream stream = moduleClass.getResourceAsStream(path)) {
            if (stream == null) {
                LOGGER.warn("No module info for {} found at {}",
                            moduleClass.getName(),
                            path);
            } else {
                moduleInfo.load(stream);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to read module info for {} at {}.",
                         moduleClass.getName(),
                         path);
            LOGGER.error("Cause: ", ex);
        }

        return moduleInfo;
    }

    /**
     * Reads the module name. If the module annotation of the module class has a
     * value for {@code name} that value is used. Otherwise the name from the
     * module info file is used. If this name is also empty the simple name of
     * the module class is used.
     *
     * @param moduleClass The module class.
     * @param annotation The module annotation of the module class.
     * @param moduleInfo The module info properties.
     * @return The name of the module.
     */
    private String readModuleName(final Class<? extends CcmModule> moduleClass,
                                  final Module annotation,
                                  final Properties moduleInfo) {
        @SuppressWarnings(
                "PMD.LongVariable")
        final boolean annotationHasModuleName = annotation.name() != null
                                                        && !annotation.name()
                .isEmpty();
        @SuppressWarnings("PMD.LongVariable")
        final boolean moduleInfoHasModuleName = moduleInfo.getProperty(
                ARTIFACT_ID)
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

    /**
     * Reads the module name. If the module annotation of the module class has a
     * value for {@code packageName} that value is used. Otherwise the groupId
     * from the module info file is used. If this name is also empty the package
     * name of the module class is used.
     *
     * @param moduleClass The module class.
     * @param annotation The module annotation of the module class.
     * @param moduleInfo The module info properties.
     * @return The name of the module.
     */
    private String readModulePackageName(
            final Class<? extends CcmModule> moduleClass,
            final Module annotation,
            final Properties moduleInfo) {

        @SuppressWarnings(
                "PMD.LongVariable")
        final boolean annotationHasPackageName = annotation.packageName()
                                                         != null
                                                         && !annotation
                .packageName()
                .isEmpty();
        @SuppressWarnings("PMD.LongVariable")
        final boolean moduleInfoHasPackageName = moduleInfo
                .getProperty(GROUP_ID) != null && !moduleInfo.getProperty(
                        GROUP_ID).isEmpty();
        if (annotationHasPackageName) {
            return annotation.packageName();
        } else if (moduleInfoHasPackageName) {
            return String.format("%s/%s",
                                 moduleInfo.getProperty(GROUP_ID),
                                 moduleInfo.
                                 getProperty(ARTIFACT_ID).replace("-",
                                                                  "_"));
        } else {
            LOGGER.warn("The module data package was not specified by the module "
                                + "annotation nore was an group id found in the module info"
                        + "file. Creating data package name from the name of the "
                        + "module class.");
            return moduleClass.getName().toLowerCase();
        }
    }

    /**
     * Reads the module version. If the module annotation on the module class
     * specifies a value for the version that value is used. Otherwise the value
     * from the module info properties is used. If the properties do not specify
     * a version the version is undefined. That should be avoided because it can
     * lead to strange errors.
     *
     * @param annotation The module annotation of the module.
     * @param moduleInfo The module info properties.
     * @return The version of the module or {@code null} if the version is
     * unspecified.
     */
    private String readModuleVersion(
            final Module annotation,
            final Properties moduleInfo) {

        @SuppressWarnings("PMD.LongVariable")
        final boolean annotationHasVersion = annotation.version() != null
                                                     && !annotation.version()
                .isEmpty();
        @SuppressWarnings("PMD.LongVariable")
        final boolean moduleInfoHasVersion = moduleInfo.getProperty(VERSION)
                                                     != null
                                                     && !moduleInfo.getProperty(
                        VERSION)
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
