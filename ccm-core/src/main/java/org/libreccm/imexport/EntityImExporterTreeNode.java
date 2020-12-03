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
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A node in the dependency tree managed by {@link EntityImExporterTreeManager}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
final class EntityImExporterTreeNode {

    private AbstractEntityImExporter<?> entityImExporter;

    private List<EntityImExporterTreeNode> dependentImExporters;

    private List<EntityImExporterTreeNode> dependsOn;

    public EntityImExporterTreeNode() {

        super();

        dependentImExporters = new ArrayList<>();
        dependsOn = new ArrayList<>();
    }

    public EntityImExporterTreeNode(
        final AbstractEntityImExporter<?> entityImExporter) {

        this();
        this.entityImExporter = entityImExporter;
    }

    public AbstractEntityImExporter<?> getEntityImExporter() {

        return entityImExporter;
    }

    void setEntityImExporter(
        final AbstractEntityImExporter<?> entityImExporter) {

        this.entityImExporter = entityImExporter;
    }

    public List<EntityImExporterTreeNode> getDependentImExporters() {
        return Collections.unmodifiableList(dependentImExporters);
    }

    void setDependentImExporters(
        final List<EntityImExporterTreeNode> dependentImExporters) {

        this.dependentImExporters = new ArrayList<>(dependentImExporters);
    }

    void addDependentImExporter(final EntityImExporterTreeNode node) {

        dependentImExporters.add(node);
    }

    void removeDependentImExporter(final EntityImExporterTreeNode node) {

        dependentImExporters.remove(node);
    }

    public List<EntityImExporterTreeNode> getDependsOn() {

        return Collections.unmodifiableList(dependsOn);
    }

    void setDependsOn(final List<EntityImExporterTreeNode> dependsOn) {

        this.dependsOn = new ArrayList<>(dependsOn);
    }

    void addDependsOn(final EntityImExporterTreeNode node) {

        dependsOn.add(node);
    }

    void removeDependsOn(final EntityImExporterTreeNode node) {

        dependsOn.remove(node);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 47
                   * hash
                   + Objects
                .hashCode(this.entityImExporter.getClass().getName());
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EntityImExporterTreeNode)) {
            return false;
        }
        final EntityImExporterTreeNode other = (EntityImExporterTreeNode) obj;
        return Objects.equals(
            this.entityImExporter.getClass().getName(),
            other.getEntityImExporter().getClass().getName());
    }

    @Override
    public String toString() {
        return String.format(
            "%s{ "
                + "entityImExporter: %s, "
                + "dependentImExporters: [%s], "
                + "dependsOn: [%s]"
                + " }",
            super.toString(),
            entityImExporter.getEntityClass().toString(),
            dependentImExporters
                .stream()
                .map(EntityImExporterTreeNode::getEntityImExporter)
                .map(AbstractEntityImExporter::getEntityClass)
                .map(Class::getName)
                .collect(Collectors.joining(", ")),
            dependsOn
                .stream()
                .map(EntityImExporterTreeNode::getEntityImExporter)
                .map(AbstractEntityImExporter::getEntityClass)
                .map(Class::getName)
                .collect(Collectors.joining(", "))
        );
    }

}
