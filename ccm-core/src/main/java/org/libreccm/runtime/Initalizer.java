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
package org.libreccm.runtime;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "inits")
public class Initalizer implements Serializable {

    private static final long serialVersionUID = 9150623897315380159L;

    @Id
    @Column(name = "initializer_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long initializerId;

    @Column(name = "class_name")
    private String className;

    @ManyToOne
    @JoinColumn(name = "required_by_id")
    private Initalizer requiredBy;

    @OneToMany(mappedBy = "requiredBy")
    private List<Initalizer> requiredInitializers;

    public Initalizer() {
        super();

        requiredInitializers = new ArrayList<>();
    }

    public long getInitializerId() {
        return initializerId;
    }

    public void setInitializerId(final long initializerId) {
        this.initializerId = initializerId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(final String className) {
        this.className = className;
    }

    public Initalizer getRequiredBy() {
        return requiredBy;
    }

    protected void setRequiredBy(final Initalizer requiredBy) {
        this.requiredBy = requiredBy;
    }

    public List<Initalizer> getRequiredInitializers() {
        if (requiredInitializers == null) {
            return null;
        } else {
            return Collections.unmodifiableList(requiredInitializers);
        }
    }

    protected void setRequiredInitializers(
        final List<Initalizer> requiredInitializers) {
        this.requiredInitializers = requiredInitializers;
    }

    protected void addRequiredInitalizer(final Initalizer initalizer) {
        requiredInitializers.add(initalizer);
    }

    protected void removeRequiredInitalizer(final Initalizer initalizer) {
        requiredInitializers.remove(initalizer);
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash
            = 37 * hash + (int) (initializerId ^ (initializerId >>> 32));
        hash = 37 * hash + Objects.hashCode(className);
        hash = 37 * hash + Objects.hashCode(requiredBy);
        hash = 37 * hash + Objects.hashCode(requiredInitializers);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Initalizer)) {
            return false;
        }
        final Initalizer other = (Initalizer) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (initializerId != other.getInitializerId()) {
            return false;
        }
        if (!Objects.equals(className, other.getClassName())) {
            return false;
        }
        if (!Objects.equals(requiredBy, other.getRequiredBy())) {
            return false;
        }
        return Objects.equals(requiredInitializers,
                              other.getRequiredInitializers());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Initalizer;
    }

    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "initializerId = %d, "
                                 + "className = \"%s\", "
                                 + "requiredBy = %s"
                                 + " }",
                             super.toString(),
                             initializerId,
                             className,
                             Objects.toString(requiredBy));
    }

}
