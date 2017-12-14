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

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "LIFECYLE_PHASES", schema = DB_SCHEMA)
public class Phase implements Serializable {

    private static final long serialVersionUID = -1683874069942019941L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "PHASE_ID")
    private long phaseId;

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

    @ManyToOne
    @JoinColumn(name = "LIFECYCLE_ID")
    private Lifecycle lifecycle;

    @OneToOne
    @JoinColumn(name = "DEFINITION_ID")
    private PhaseDefinition definition;

    public long getPhaseId() {
        return phaseId;
    }

    public void setPhaseId(final long phaseId) {
        this.phaseId = phaseId;
    }

    public Date getStartDateTime() {
        if (startDateTime == null) {
            return null;
        } else {
            return new Date(startDateTime.getTime());
        }
    }

    public void setStartDateTime(final Date startDateTime) {
        if (startDateTime == null) {
            this.startDateTime = startDateTime;
        } else {
            this.startDateTime = new Date(startDateTime.getTime());
        }
    }

    public Date getEndDateTime() {
        if (endDateTime == null) {
            return null;
        } else {
            return new Date(endDateTime.getTime());
        }
    }

    public void setEndDateTime(final Date endDateTime) {
        if (endDateTime == null) {
            this.endDateTime = endDateTime;
        } else {
            this.endDateTime = new Date(endDateTime.getTime());
        }
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

    public Lifecycle getLifecycle() {
        return lifecycle;
    }

    public void setLifecycle(final Lifecycle lifecycle) {
        this.lifecycle = lifecycle;
    }

    public PhaseDefinition getDefinition() {
        return definition;
    }

    protected void setDefinition(final PhaseDefinition definition) {
        this.definition = definition;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + (int) (phaseId ^ (phaseId >>> 32));
        hash = 37 * hash + Objects.hashCode(startDateTime);
        hash = 37 * hash + Objects.hashCode(endDateTime);
        hash = 37 * hash + Objects.hashCode(listener);
        hash = 37 * hash + (started ? 1 : 0);
        hash = 37 * hash + (finished ? 1 : 0);
        hash = 37 * hash + Objects.hashCode(lifecycle);
        hash = 37 * hash + Objects.hashCode(definition);
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
        if (!(obj instanceof Phase)) {
            return false;
        }
        final Phase other = (Phase) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (phaseId != other.getPhaseId()) {
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
        if (!Objects.equals(lifecycle, other.getLifecycle())) {
            return false;
        }
        return Objects.equals(definition, other.getDefinition());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Phase;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "phaseId = %d, "
                                 + "startDateTime = %tF %<tT,"
                                 + "endDateTime = %tF %<tT, "
                                 + "listener = \"%s\", "
                                 + "started = %b, "
                                 + "finished = %b, "
                                 + "lifecycle = %s, "
                                 + "definition = %s%s"
                                 + " }",
                             super.toString(),
                             phaseId,
                             startDateTime,
                             endDateTime,
                             listener,
                             started,
                             finished,
                             Objects.toString(lifecycle),
                             Objects.toString(definition),
                             data);
    }

}
