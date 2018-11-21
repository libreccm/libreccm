/*
 * Copyright (C) 2018 LibreCCM Foundation.
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
package org.libreccm.imexport;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class implements topological sorting to determine the order in which
 * entities are imported.
 *
 * The class is used by creating an instance with the parameterless constructor.
 * To create the tree/graph call the null {@link #generateTree(java.util.List)}
 * method. With the returned list of nodes call the
 * {@link #orderImExporters(java.util.List)} method. The list returned by
 * {@link #orderImExporters(java.util.List)} contains all
 * {@link AbstractEntityImExporter}s in the order.
 *
 * This class is <strong>not</strong> not part of the public API.
 *
 ** More information about topological sorting:
 * <a href="https://en.wikipedia.org/wiki/Topological_sorting">https://en.wikipedia.org/wiki/Topological_sorting</a>
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class EntityImExporterTreeManager {

    private static final Logger LOGGER = LogManager
        .getLogger(EntityImExporterTreeManager.class);

    /**
     * Initialises the tree with the provided list of
     * {@link AbstractEntityImExporter}s.
     *
     * @param imExporters The available {@link AbstractEntityImExporter}s.
     *
     * @return An ordered list of the tree nodes.
     *
     * @throws DependencyException If something is wrong with the dependency
     *                             tree. For example if a module on which
     *                             another module depends is missing or if a
     *                             cycle is detected in the dependency tree.
     */
    public List<EntityImExporterTreeNode> generateTree(
        final List<AbstractEntityImExporter<?>> imExporters)
        throws DependencyException {

        LOGGER.info("Starting to generate dependency tree...");

        //Create the tree nodes. A HashMap is used to avoid duplicates and 
        //the lookup the nodes based on their name.
        final Map<String, EntityImExporterTreeNode> nodes = imExporters
            .stream()
            .map(EntityImExporterTreeNode::new)
            .collect(Collectors.toMap(
                node -> node
                    .getEntityImExporter()
                    .getClass()
                    .getAnnotation(Processes.class).value().getName(),
                node -> node));

        //Add the dependency relations to the nodes
        for (final AbstractEntityImExporter<?> imExporter : imExporters) {
            addDependencyRelations(imExporter, nodes);
        }

        //Generate the node list
        final List<EntityImExporterTreeNode> nodeList = new ArrayList<>();
        for (final Map.Entry<String, EntityImExporterTreeNode> entry
                 : nodes.entrySet()) {

            nodeList.add(entry.getValue());
        }

        LOGGER.info("Dependency tree generated.");

        return nodeList;
    }

    /**
     * Generates an ordered list of the tree nodes which can be used to process
     * the imports in the correct order.
     *
     * In this method the topological sorting happens.
     *
     * @param nodes The nodes of the dependency tree.
     *
     * @return A ordered list of the tree nodes.
     *
     * @throws DependencyException If something is wrong with dependency graph.
     */
    public List<EntityImExporterTreeNode> orderImExporters(
        final List<EntityImExporterTreeNode> nodes)
        throws DependencyException {

        LOGGER.info("Creating an ordered list from the dependency tree...");

        //List for the ordered and resolved nodes.
        final List<EntityImExporterTreeNode> orderedNodes = new ArrayList<>();
        final List<EntityImExporterTreeNode> resolvedNodes = new ArrayList<>();

        LOGGER.info("Looking for EntityImExporters which do not depend on any "
                        + "other EntityImExporters.");
        //Find all nodes which do not depend on any other nodes. These 
        //nodes are used as starting point for the sorting.
        for (final EntityImExporterTreeNode node : nodes) {

            if (node.getDependsOn().isEmpty()) {
                LOGGER.info(
                    "\tNode \"{}\" does not depend on any other module",
                    node.getEntityImExporter().getClass().getName());
                resolvedNodes.add(node);
            }
        }

        LOGGER.info("Ordering remaining nodes...");
        while (!resolvedNodes.isEmpty()) {

            //Remove the first node from the resolved nodes list
            final EntityImExporterTreeNode current = resolvedNodes.remove(0);
            LOGGER.info("\tProcessing node for EntityImExporter \"{}\"...",
                        current.getEntityImExporter().getClass().getName());

            //Add the node to the ordered modules list.
            orderedNodes.add(current);

            //Remove the edges to the current node.
            for (final EntityImExporterTreeNode dependent
                     : current.getDependentImExporters()) {

                dependent.removeDependsOn(current);

                //If the dependent node has no more dependsOn relations put
                //the node into the resolved modules list.
                if (dependent.getDependsOn().isEmpty()) {
                    resolvedNodes.add(dependent);
                }
            }
        }

        //Check if all nodes have been ordered. If not the tree has at least on
        //on cycle and can't be processed.
        if (orderedNodes.size() == nodes.size()) {

            LOGGER.info("EntityImExporter dependency graph processed "
                            + "successfully. EntityImExporters in order:");
            for (final EntityImExporterTreeNode node : orderedNodes) {
                LOGGER.info("\t{}",
                            node.getEntityImExporter().getClass().getName());
            }

            return orderedNodes;

        } else {

            LOGGER.fatal("The EntityImExporter dependency graph has at least "
                             + "one cycle.");
            throw new DependencyException("The EntityImExporter dependency "
                                              + "graph has at least one cycle.");
        }

    }

    /**
     * Helper method for adding the dependency relations for an
     * {@link AbstractEntityImExporter} to the nodes.
     *
     * @param imExporter The current {@link EntityImExporter}.
     * @param nodes      The map of nodes.
     *
     * @throws DependencyException If something goes wrong.
     */
    private void addDependencyRelations(
        final AbstractEntityImExporter<?> imExporter,
        final Map<String, EntityImExporterTreeNode> nodes)
        throws DependencyException {

        //Get the name of the module from the module info.
        final String className = imExporter.getClass().getName();
        LOGGER
            .info("Adding dependency relations for EntityImExporter \"{}\"...",
                  className);

        //Check if the nodes map has an entry for the EntityImExporter.
        if (!nodes.containsKey(className)) {

            LOGGER.fatal("EntityImExporter nodes map does contain an entry for "
                             + "\"{}\". That should not happen.",
                         className);
            throw new IllegalArgumentException(String.format(
                "The nodes map does not contain a node for "
                    + "EntityImExporter \"%s\"."
                    + "This should not happen.",
                className));
        }

        //Get the node from the map
        final EntityImExporterTreeNode node = nodes.get(className);
        LOGGER
            .info("Processing required modules for EntityImExporter \"{}\"...",
                  className);
        //Process the EntityImExporter required by the current module and add 
        //the dependency relations.
        for (final Class<? extends Exportable> clazz
                 : imExporter.getRequiredEntities()) {

            addDependencyRelation(nodes, node, clazz);
        }
    }

    /**
     * Helper method for adding a single dependency relation.
     *
     * @param nodes         The map of tree nodes.
     * @param node          The node to which the dependency relations are
     *                      added.
     * @param requiredClass The type which is required by the current
     *                      module/node.
     *
     * @throws DependencyException
     */
    private void addDependencyRelation(
        final Map<String, EntityImExporterTreeNode> nodes,
        EntityImExporterTreeNode node,
        Class<? extends Exportable> requiredClass)
        throws DependencyException {

        LOGGER.info("\tEntityImExporter for \"{}\" requires "
                        + "EntityImExporter for \"{}\".",
                    node.getEntityImExporter().getClass().getName(),
                    requiredClass.getName());

        //Check if the nodes list has an entry for the required module.
        if (!nodes.containsKey(requiredClass.getName())) {

            LOGGER.fatal("Required EntityImExporter for \"{}\" no found.",
                         requiredClass.getName());
            throw new DependencyException(String.format(
                "EntityImExporter for type \"%s\" depends on type \"%s\" "
                    + "but no EntityImExporter for type \"%s\" is available.",
                node
                    .getEntityImExporter()
                    .getClass()
                    .getAnnotation(Processes.class).value().getName(),
                requiredClass.getName(),
                requiredClass.getName()));
        }

        final EntityImExporterTreeNode dependencyNode = nodes
            .get(requiredClass.getName());

        //Create the dependencies relations.
        node.addDependsOn(dependencyNode);
        dependencyNode.addDependentImExporter(node);
    }

}
