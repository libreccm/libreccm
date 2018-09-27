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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class EntityImExporterTreeManager {

    public List<EntityImExporterTreeNode> generateTree(
        final List<EntityImExporter<?>> imExporters)
        throws DependencyException {

        final Map<String, EntityImExporterTreeNode> nodes = imExporters
            .stream()
            .map(EntityImExporterTreeNode::new)
            .collect(Collectors
                .toMap(
                    node -> node
                        .getEntityImExporter()
                        .getClass()
                        .getAnnotation(Processes.class)
                        .type()
                        .getName(),
                    node -> node));

        for (final EntityImExporter<?> imExporter : imExporters) {
            addDependencyRelations(imExporter, nodes);
        }
        
        final List<EntityImExporterTreeNode> nodeList = new ArrayList<>();
        for (final Map.Entry<String, EntityImExporterTreeNode> entry 
            : nodes.entrySet()) {
            
            nodeList.add(entry.getValue());
        }
        
        return nodeList;
    }

    private void addDependencyRelations(
        final EntityImExporter<?> imExporter,
        final Map<String, EntityImExporterTreeNode> nodes)
        throws DependencyException {

        final Processes processes = imExporter
            .getClass()
            .getAnnotation(Processes.class);
        final String className = imExporter.getClass().getName();

        if (!nodes.containsKey(className)) {

            throw new IllegalArgumentException(String.format(
                "The nodes map does not contain a node for "
                    + "EntityImExporter \"%s\"."
                    + "This should not happen.",
                className));
        }

        final EntityImExporterTreeNode node = nodes.get(className);

        for (final Class<? extends Exportable> clazz : processes.dependsOn()) {

            addDependencyRelation(nodes, node, clazz);
        }
    }

    private void addDependencyRelation(
        final Map<String, EntityImExporterTreeNode> nodes,
        EntityImExporterTreeNode node,
        Class<? extends Exportable> clazz)
        throws DependencyException {

        if (!nodes.containsKey(clazz.getName())) {

            throw new DependencyException(String.format(
                "EntityImExporter for type \"%s\" depends on type \"%s\" "
                    + "but no EntityImExporter for type \"%s\" is available.",
                node.getEntityImExporter().getClass().getAnnotation(
                    Processes.class).type().getName(),
                clazz.getName(),
                clazz.getName()));
        }

        final EntityImExporterTreeNode dependencyNode = nodes
            .get(clazz.getName());

        node.addDependsOn(dependencyNode);
        dependencyNode.addDependentImExporter(node);
    }

}
