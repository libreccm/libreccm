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
package org.libreccm.notification;

import org.libreccm.core.CcmObject;
import org.libreccm.core.Party;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * 
 * Models the envelope information associated with a digest.
 *
 * When a digest is processed, all notifications associated with it are grouped
 * for delivery as a single unit to each receiver. The outbound email generated
 * for the receivers has a common subject, header, separator between the
 * individual messages, and signature (from the documentation of the 
 * {@code com.arsdigita.notification.Digest} class).
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "digests")
public class Digest extends CcmObject implements Serializable {

    private static final long serialVersionUID = -3526066971290670390L;

    @OneToOne
    @JoinColumn(name = "from_party_id")
    private Party fromParty;

    @Column(name = "subject", length = 255, nullable = false)
    private String subject;

    @Column(name = "header", length = 4096, nullable = false)
    private String header;

    @Column(name = "separator", length = 128, nullable = false)
    private String separator;

    @Column(name = "signature", length = 4096, nullable = false)
    private String signature;

    @Column(name = "frequency")
    private Integer frequency;

    @Column(name = "next_run")
    @Temporal(TemporalType.TIMESTAMP)
    private Date nextRun;

    public Party getFromParty() {
        return fromParty;
    }

    public void setFromParty(final Party fromParty) {
        this.fromParty = fromParty;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(final String subject) {
        this.subject = subject;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(final String header) {
        this.header = header;
    }

    public String getSeparator() {
        return separator;
    }

    public void setSeparator(final String separator) {
        this.separator = separator;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(final String signature) {
        this.signature = signature;
    }

    public Integer getFrequency() {
        return frequency;
    }

    public void setFrequency(final Integer frequency) {
        this.frequency = frequency;
    }

    public Date getNextRun() {
        return new Date(nextRun.getTime());
    }

    public void setNextRun(final Date nextRun) {
        this.nextRun = new Date(nextRun.getTime());
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 37 * hash + Objects.hashCode(fromParty);
        hash = 37 * hash + Objects.hashCode(subject);
        hash = 37 * hash + Objects.hashCode(header);
        hash = 37 * hash + Objects.hashCode(separator);
        hash = 37 * hash + Objects.hashCode(signature);
        hash = 37 * hash + Objects.hashCode(frequency);
        hash = 37 * hash + Objects.hashCode(nextRun);
        return hash;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }

        if (!super.equals(obj)) {
            return false;
        }

        final Digest other = (Digest) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (!Objects.equals(fromParty, other.getFromParty())) {
            return false;
        }
        if (!Objects.equals(subject, other.getSubject())) {
            return false;
        }
        if (!Objects.equals(header, other.getHeader())) {
            return false;
        }
        if (!Objects.equals(separator, other.getSeparator())) {
            return false;
        }
        if (!Objects.equals(signature, other.getSignature())) {
            return false;
        }
        if (!Objects.equals(frequency, other.getFrequency())) {
            return false;
        }
        return Objects.equals(nextRun, other.getNextRun());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Digest;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", fromParty = %s, "
                                                + "subject = \"%s\", "
                                                + "frequency = %d,"
                                                + "nextRun = %tF %<tT%s",
                                            Objects.toString(fromParty),
                                            subject,
                                            frequency,
                                            nextRun,
                                            data));
    }

}
