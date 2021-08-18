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
package org.librecms.lifecycle;

import org.hibernate.search.annotations.Field;
import org.libreccm.core.Identifiable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlElement;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "LIFECYCLES", schema = DB_SCHEMA)
public class Lifecycle implements Identifiable, Serializable {

    private static final long serialVersionUID = 184357562249530038L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "LIFECYCLE_ID")
    private long lifecycleId;

    /**
     *
     */
    @Column(name = "UUID", unique = true)
    @NotNull
    @Field
    @XmlElement(name = "uuid")
    private String uuid;

    @Column(name = "START_DATE_TIME")
    @Temporal(TemporalType.DATE)
    private Date startDateTime;

    @Column(name = "END_DATE_TIME")
    @Temporal(TemporalType.DATE)
    private Date endDateTime;

    @Column(name = "LISTENER", length = 1024)
    private String listener;

    @Column(name = "STARTED")
    private boolean started;

    @Column(name = "FINISHED")
    private boolean finished;

    @OneToOne
    @JoinColumn(name = "DEFINITION_ID")
    private LifecycleDefinition definition;

    @OneToMany(mappedBy = "lifecycle")
    private List<Phase> phases;

    public Lifecycle() {
        phases = new ArrayList<>();
    }

    public long getLifecycleId() {
        return lifecycleId;
    }

    public void setLifecycleId(final long lifecycleId) {
        this.lifecycleId = lifecycleId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public Date getStartDateTime() {
        return new Date(startDateTime.getTime());
    }

    public void setStartDateTime(final Date startDateTime) {
        this.startDateTime = new Date(startDateTime.getTime());
    }

    public Date getEndDateTime() {
        return Optional
            .ofNullable(endDateTime)
            .map(date -> new Date(date.getTime()))
            .orElse(null);
    }

    public void setEndDateTime(final Date endDateTime) {
        this.endDateTime = Optional
            .ofNullable(endDateTime)
            .map(date -> new Date(date.getTime()))
            .orElse(null);
    }

    public String getListener() {
        return listener;
    }

    public void setListener(final String listener) {
        this.listener = listener;
    }

    public boolean isStarted() {
        return started;
    }

    public void setStarted(final boolean started) {
        this.started = started;
    }

    public boolean isFinished() {
        return finished;
    }

    public void setFinished(final boolean finished) {
        this.finished = finished;
    }

    public LifecycleDefinition getDefinition() {
        return definition;
    }

    public void setDefinition(final LifecycleDefinition definition) {
        this.definition = definition;
    }

    public List<Phase> getPhases() {
        return Collections.unmodifiableList(phases);
    }

    protected void setPhases(final List<Phase> phases) {
        this.phases = new ArrayList<>(phases);
    }

    public void addPhase(final Phase phase) {
        phases.add(phase);
    }

    public void removePhase(final Phase phase) {
        phases.remove(phase);
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 79 * hash + (int) (lifecycleId ^ (lifecycleId >>> 32));
        hash = 79 * hash + Objects.hashCode(startDateTime);
        hash = 79 * hash + Objects.hashCode(endDateTime);
        hash = 79 * hash + Objects.hashCode(listener);
        hash = 79 * hash + (started ? 1 : 0);
        hash = 79 * hash + (finished ? 1 : 0);
        hash = 79 * hash + Objects.hashCode(definition);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (obj instanceof Lifecycle) {
            return false;
        }
        final Lifecycle other = (Lifecycle) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        if (lifecycleId != other.getLifecycleId()) {
            return false;
        }
        if (started != other.isStarted()) {
            return false;
        }
        if (finished != other.isFinished()) {
            return false;
        }
        if (!Objects.equals(listener, other.getListener())) {
            return false;
        }
        if (!Objects.equals(startDateTime, other.getStartDateTime())) {
            return false;
        }
        if (!Objects.equals(endDateTime, other.getEndDateTime())) {
            return false;
        }
        return Objects.equals(definition, other.getDefinition());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Lifecycle;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "lifecycleId = %d, "
                                 + "startDateTime = %tF %<tT, "
                                 + "endDateTime = %tF %<tT, "
                                 + "listener = \"%s\", "
                                 + "started = %b, "
                                 + "finished = %b, "
                                 + "definition = %s%s"
                                 + " }",
                             super.toString(),
                             lifecycleId,
                             startDateTime,
                             endDateTime,
                             listener,
                             started,
                             finished,
                             Objects.toString(definition),
                             data);
    }

}
