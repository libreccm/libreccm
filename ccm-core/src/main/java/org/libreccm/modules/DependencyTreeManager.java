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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.artifact.versioning.ComparableVersion;

/**
 * This class implements topological sorting to determine the order in which the
 * modules are loaded and initialised.
 *
 * The class is used by creating an instance with the parameterless constructor.
 * To create the tree/graph call the
 * {@link #generateTree(javax.enterprise.inject.Instance)} method. With the
 * returned list of nodes call the the {@link #orderModules(java.util.List)}
 * method. The list returned by {@link #orderModules(java.util.List)} contains
 * all modules in order.
 *
 * More information about topological sorting:
 * <a href="https://en.wikipedia.org/wiki/Topological_sorting">https://en.wikipedia.org/wiki/Topological_sorting</a>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class DependencyTreeManager {

    private static final Logger LOGGER = LogManager.getLogger(
        DependencyTreeManager.class);

    public List<TreeNode> generateTree(final Iterable<CcmModule> modules)
        throws DependencyException {

        LOGGER.info("Starting to generate dependency tree...");

        final Map<String, TreeNode> nodes = new HashMap<>();

        for (final CcmModule module : modules) {
            createTreeNode(module, nodes);
        }

        for (final CcmModule module : modules) {
            addDependencyRelations(module, nodes);
        }

        final List<TreeNode> nodeList = new ArrayList<>();
        for (final Map.Entry<String, TreeNode> entry : nodes.entrySet()) {
            nodeList.add(entry.getValue());
        }

        LOGGER.info("Dependency tree generated.");

        return nodeList;
    }

    public List<TreeNode> orderModules(final List<TreeNode> dependencyTree)
        throws DependencyException {
        LOGGER.info("Creating an ordered list from the dependency tree...");

        final List<TreeNode> orderedModules = new ArrayList<>();
        final List<TreeNode> resolvedModules = new ArrayList<>();

        LOGGER.info("Looking for modules which do not depend on any other "
                        + "modules...");
        for (final TreeNode node : dependencyTree) {
            if (node.getDependsOn().isEmpty()) {
                LOGGER.info(
                    "\tModule \"{}\" does not depend on any other module",
                    node.getModuleInfo().getModuleName());
                resolvedModules.add(node);
            }
        }

        LOGGER.info("Ordering remaining nodes...");
        while (!resolvedModules.isEmpty()) {

            final TreeNode current = resolvedModules.remove(0);
            LOGGER.info("\tProcessing node for module \"{}\"...",
                        current.getModuleInfo().getModuleName());

            orderedModules.add(current);

            for (final TreeNode dependent : current.getDependentModules()) {
                dependent.removeDependsOn(current);

                if (dependent.getDependsOn().isEmpty()) {
                    resolvedModules.add(dependent);
                }
            }
        }

        if (orderedModules.size() == dependencyTree.size()) {
            LOGGER.info("Dependency graph proceessed successfully. "
                            + "Modules in order:");
            for (final TreeNode node : orderedModules) {
                LOGGER.info("\t{}", node.getModuleInfo().getModuleName());
            }

            return orderedModules;
        } else {
            LOGGER.fatal("The dependency graph has a least one cycle.");
            throw new DependencyException(
                "The dependency graph has a least one cycle.");
        }
    }

    private void createTreeNode(final CcmModule module,
                                final Map<String, TreeNode> nodes) {
        final TreeNode node = new TreeNode(module);

        LOGGER.info("Creating node for module \"{}\"...",
                    node.getModuleInfo().getModuleName());
        nodes.put(node.getModuleInfo().getModuleName(), node);
    }

    private void addDependencyRelations(final CcmModule module,
                                        final Map<String, TreeNode> nodes)
        throws DependencyException {
        final ModuleInfo moduleInfo = new ModuleInfo();
        moduleInfo.load(module);

        LOGGER.info("Adding dependency relations for module \"{}\"...",
                    moduleInfo.getModuleName());

        final String moduleName = moduleInfo.getModuleName();

        if (!nodes.containsKey(moduleName)) {
            LOGGER.fatal("Modules nodes map does contain an entry for \"{}\". "
                             + "That should not happen.",
                         moduleInfo.getModuleName());
            throw new IllegalArgumentException(String.format(
                "The nodes map does not contain a node for module \"%s\". "
                    + "That should not happen.",
                moduleName));
        }

        final TreeNode node = nodes.get(moduleName);
        LOGGER.info("Processing required modules for module \"{}\"...",
                    node.getModuleInfo().getModuleName());
        for (final RequiredModule requiredModule : node.getModuleInfo().
            getRequiredModules()) {

//            final ModuleInfo requiredModuleInfo = new ModuleInfo();
//            requiredModuleInfo.load(requiredModule.module());
//
//            LOGGER.info("\tModule \"{}\" requires module \"{}\".",
//                        node.getModuleInfo().getModuleName(),
//                        requiredModuleInfo.getModuleName());
//
//            if (!nodes.containsKey(requiredModuleInfo.getModuleName())) {
//
//                LOGGER.fatal("Required module \"{}\" no found.",
//                             requiredModuleInfo.getModuleName());
//
//                throw new DependencyException(String.format(
//                    "Module \"%s\" depends on module \"%s\" but the dependency "
//                        + "tree does contain an entry for module \"%s\".",
//                    moduleInfo.getModuleName(),
//                    requiredModuleInfo.getModuleName(),
//                    requiredModuleInfo.getModuleName()));
//            }
//
//            final TreeNode dependencyNode = nodes.get(requiredModuleInfo.
//                getModuleName());
//
//            //Check version
//            if (!validateVersion(dependencyNode.getModuleInfo().
//                getModuleVersion(),
//                                 requiredModule.minVersion(),
//                                 requiredModule.maxVersion())) {
//                throw new DependencyException(String.format(
//                    "The required module is avialable but in the correct "
//                        + "version. "
//                        + "Available version: \"%s\"; "
//                        + "minimal required version: \"%s\"; "
//                        + "maximum required version: \"%s\"",
//                    dependencyNode.getModuleInfo().getModuleVersion(),
//                    requiredModule.minVersion(),
//                    requiredModule.maxVersion()));
//            }
//
//            node.addDependsOn(dependencyNode);
//            dependencyNode.addDependentModule(node);
            addDependencyRelation(nodes, node, requiredModule);
        }

    }

    private void addDependencyRelation(final Map<String, TreeNode> nodes,
                                       final TreeNode node,
                                       final RequiredModule requiredModule)
        throws DependencyException {
        final ModuleInfo requiredInfo = new ModuleInfo();
        requiredInfo.load(requiredModule.module());

        LOGGER.info("\tModule \"{}\" requires module \"{}\".",
                    node.getModuleInfo().getModuleName(),
                    requiredInfo.getModuleName());

        if (!nodes.containsKey(requiredInfo.getModuleName())) {
            LOGGER.fatal("Required module \"{}\" no found.",
                         requiredInfo.getModuleName());
            throw new DependencyException(String.format(
                "Module \"%s\" depends on module \"%s\" but the dependency "
                    + "tree does contain an entry for module \"%s\".",
                node.getModuleInfo().getModuleName(),
                requiredInfo.getModuleName(),
                requiredInfo.getModuleName()));
        }

        final TreeNode dependencyNode = nodes.get(requiredInfo.
            getModuleName());
        if (!validateVersion(dependencyNode.getModuleInfo().
            getModuleVersion(),
                             requiredModule.minVersion(),
                             requiredModule.maxVersion())) {
            throw new DependencyException(String.format(
                "The required module is avialable but in the correct "
                    + "version. "
                    + "Available version: \"%s\"; "
                    + "minimal required version: \"%s\"; "
                    + "maximum required version: \"%s\"",
                dependencyNode.getModuleInfo().getModuleVersion(),
                requiredModule.minVersion(),
                requiredModule.maxVersion()));
        }

        node.addDependsOn(dependencyNode);
        dependencyNode.addDependentModule(node);
    }

    /**
     * Helper method for checking if an dependency is available in the required
     * version.
     *
     * @param availableVersion   The available version. Can't be {@code null} or
     *                           empty.
     * @param minRequiredVersion The minimal version required. Can be
     *                           {@code null} or empty.
     * @param maxRequiredVersion The maximum version required. Can be
     *                           {@code null} or empty.
     *
     * @return {@code true} if the available version is in the required range,
     *         {@code false} if not.
     */
    //The names are fine. Shorter names would be less readable. Also removing 
    //the parentheses in the ifs would make the conditions less readable.
    @SuppressWarnings({"PMD.LongVariable",
                       "PMD.UselessParentheses",
                       "PMD.CyclomaticComplexity"})
    private boolean validateVersion(final String availableVersion,
                                    final String minRequiredVersion,
                                    final String maxRequiredVersion) {
        if (availableVersion == null || availableVersion.isEmpty()) {
            throw new IllegalArgumentException("No available version specified.");
        }

        if ((minRequiredVersion == null || minRequiredVersion.isEmpty())
                && (maxRequiredVersion == null || maxRequiredVersion.
                    isEmpty())) {
            return true;
        } else if ((minRequiredVersion != null && !minRequiredVersion.isEmpty())
                       && (maxRequiredVersion == null || maxRequiredVersion
                           .isEmpty())) {
            final ComparableVersion minVersion = new ComparableVersion(
                minRequiredVersion);
            final ComparableVersion version = new ComparableVersion(
                availableVersion);

            return minVersion.compareTo(version) <= 0;
        } else if ((minRequiredVersion == null || minRequiredVersion.isEmpty())
                       && (maxRequiredVersion != null && !maxRequiredVersion
                           .isEmpty())) {
            final ComparableVersion maxVersion = new ComparableVersion(
                maxRequiredVersion);
            final ComparableVersion version = new ComparableVersion(
                availableVersion);

            return version.compareTo(maxVersion) <= 0;
        } else {
            final ComparableVersion minVersion = new ComparableVersion(
                minRequiredVersion);
            final ComparableVersion maxVersion = new ComparableVersion(
                (maxRequiredVersion));
            final ComparableVersion version = new ComparableVersion(
                availableVersion);
            return minVersion.compareTo(version) <= 0 && version.compareTo(
                maxVersion) <= 0;
        }
    }

}
