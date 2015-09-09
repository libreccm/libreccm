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
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;
import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@ApplicationScoped
public class ModuleManager {

    private static final Logger LOGGER = LogManager.getLogger(
        ModuleManager.class
    );

    @PersistenceContext(name = "LibreCCM")
    private EntityManager entityManager;

    private List<TreeNode> moduleNodes;

    @PostConstruct
    public void initDependencyTree() {
        LOGGER.info("Finding modules");
        final ServiceLoader<CcmModule> modules = ServiceLoader.load(
            CcmModule.class);

        LOGGER.info("Creating dependency tree these modules:");
        for (final CcmModule module : modules) {
            final ModuleInfo moduleInfo = new ModuleInfo();
            moduleInfo.load(module);
            LOGGER.info("\t{} {}",
                        //                        ModuleUtil.getModuleName(module),
                        //                        ModuleUtil.getVersion(module));
                        moduleInfo.getModuleName(),
                        moduleInfo.getModuleVersion());
        }

        final DependencyTreeManager treeManager = new DependencyTreeManager();

        try {
            final List<TreeNode> tree = treeManager.generateTree(modules);
            moduleNodes = treeManager.orderModules(tree);
        } catch (DependencyException ex) {
            throw new ModuleManagerException(ex);
        }

    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void initModules() {
        LOGGER.info("Initalising modules...");
        for (final TreeNode node : moduleNodes) {
            final InstallEvent installEvent = new InstallEvent();
            installEvent.setEntityManager(entityManager);

            final InstalledModule installedModule = entityManager.find(
                InstalledModule.class,
                node.getModule().getClass().getName().hashCode());
            if (installedModule != null
                    && installedModule.getStatus() == ModuleStatus.NEW) {
                node.getModule().install(installEvent);
                installedModule.setStatus(ModuleStatus.INSTALLED);
                entityManager.merge(installedModule);
            }

            final InitEvent initEvent = new InitEvent();
            initEvent.setEntityManager(entityManager);
            node.getModule().init(initEvent);

            LOGGER.info("Data from module-info.properties for {}:",
                        node.getModule().getClass().getName());
            final Properties moduleInfo = getModuleInfo(node.getModule());
            LOGGER
                .info("Module group id: {}", moduleInfo.getProperty(
                          "groupId"));
            LOGGER.info("Module artifact id: {}", moduleInfo.getProperty(
                        "artifactId"));
            LOGGER.info("Module version: {}", moduleInfo.getProperty("version"));

            LOGGER.info("Module build date: {}", moduleInfo.getProperty(
                        "build.date"));
        }
    }

    private Properties getModuleInfo(final CcmModule module) {
        final Properties moduleInfo = new Properties();

//        try {
        final String moduleInfoPath = String.format(
            "/module-info/%s.properties",
            module.getClass().getName());
        LOGGER.info("Path for module info: {}", moduleInfoPath);
        try (final InputStream stream = module.getClass().getResourceAsStream(
            moduleInfoPath)) {
            if (stream == null) {
                LOGGER.warn("No module info found.");
            } else {
                moduleInfo.load(stream);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to read module-info.properties for module {}.",
                         module.getClass().getName());
        }

        return moduleInfo;
    }

    @Transactional(Transactional.TxType.REQUIRED)
    public void shutdownModules() {
        LOGGER.info("Shutting down modules...");
        System.out.println("Shutting down modules...");
        for (final TreeNode node : moduleNodes) {
            final ShutdownEvent shutdownEvent = new ShutdownEvent();
            shutdownEvent.setEntityManager(entityManager);
            node.getModule().shutdown(shutdownEvent);
        }

        System.out.println("Modules shut down.");

        System.out.println("Checking for modules to uninstall...");
        for (final TreeNode node : moduleNodes) {
            System.out.printf("Checking status of module %s%n",
                              node.getModule().getClass().getName());
            final InstalledModule installedModule = entityManager.find(
                InstalledModule.class, node.
                getModule().getClass().getName().hashCode());
            LOGGER.info("Status of module {} ({}): {}",
                        node.getModuleInfo().getModuleName(),
                        node.getModule().getClass().getName(),
                        installedModule.getStatus());
            System.out.printf("Status of module %s (%s): %s%n",
                              node.getModuleInfo().getModuleName(),
                              node.getModule().getClass().getName(),
                              installedModule.getStatus());
            System.out.printf("Checked status of module %s%n",
                              node.getModule().getClass().getName());

            if (ModuleStatus.UNINSTALL.equals(installedModule.getStatus())) {
                System.out.printf("Module %s is scheduled for uninstall...%n",
                                  node.getModuleInfo().getModuleName());
                if (node.getDependentModules().isEmpty()) {
                    System.out.
                        printf("Calling uninstall method of module %s...%n",
                               node.getModuleInfo().getModuleName());
                    final UnInstallEvent unInstallEvent = new UnInstallEvent();
                    unInstallEvent.setEntityManager(entityManager);
                    node.getModule().uninstall(null);

                } else {
                    System.out.printf("There are other modules depending on "
                                          + "module %s. Module can't be "
                                          + "uninstalled. Depending modules:%n",
                                      node.getModuleInfo().getModuleName());
                    for (final TreeNode dependent : node.getDependentModules()) {
                        System.out.printf("\t%s%n",
                                          dependent.getModuleInfo()
                                          .getModuleName());
                    }
                    installedModule.setStatus(ModuleStatus.INSTALLED);
                    entityManager.merge(installedModule);
                }
            } else {
                System.out.printf(
                    "Module %s is *not* scheduled for uninstall.%n",
                    node.getModuleInfo().getModuleName());
            }
        }
    }

}
