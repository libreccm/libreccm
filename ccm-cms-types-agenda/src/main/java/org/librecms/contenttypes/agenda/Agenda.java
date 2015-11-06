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
package org.librecms.contenttypes.agenda;

import static org.librecms.contenttypes.agenda.AgendaConstants.*;

import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.validator.constraints.NotEmpty;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;

/**
 * This content type represents an agenda data abject.
 *
 * This class extends ContentItem with additional attributes specific to an
 * agenda:
 * <dl>
 * <dt>summary</dt>      <dd>optional, standard text field, short description, used
 * as lead text</dd>
 * <dt>agendaDate</dt>   <dd>mandatory, date/time when it will happen</dd>
 * <dt>location</dt>     <dd>optional, standard text field, (short) description of
 * location, usable in a list</dd>
 * <dt>attendees</dt>    <dd>optional, standard text field</dd>
 * <dt>subjectItems</dt> <dd>optional, standard text field</dd>
 * <dt>contactInfo</dt>  <dd>optional, standard text field</dd>
 * <dt>creationDate</dt> <dd>automatic</dd>
 * </dl>
 *
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 24/10/2015
 */
@Entity
@Audited
@Table(name = "AGENDA", schema = DB_SCHEMA)
public class Agenda extends ContentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The date and time for the agenda
     */
    @Column(name = "AGENDADATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    @NotEmpty
    private Date agendaDate;

    /**
     * The location for the agenda
     */
    @Column(name = "LOCATION")
    private LocalizedString location;
    /**
     * The attendees for the agenda
     */
    @Column(name = "ATTENDEES")
    private LocalizedString attendees;
    /**
     * The subject items for the agenda
     */
    @Column(name = "SUBJECTITEMS")
    private LocalizedString subjectItems;
    /**
     * Contact information for the agenda
     */
    @Column(name = "CONTACTINFO")
    private LocalizedString contactInfo;
    /**
     * The summary of the agenda
     */
    @Column(name = "SUMMARY")
    private LocalizedString summary;

    /**
     * The date the agenda was created
     */
    @Column(name = "CREATIONDATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date creationDate;

    
    //getter and setter:
    
    public Date getAgendaDate() {
        return  new Date(agendaDate.getTime());
    }

    public void setAgendaDate(Date agendaDate) {
        this.agendaDate =  new Date(agendaDate.getTime());
    }

    public LocalizedString getLocation() {
        return location;
    }

    public void setLocation(LocalizedString location) {
        this.location = location;
    }

    public LocalizedString getAttendees() {
        return attendees;
    }

    public void setAttendees(LocalizedString attendees) {
        this.attendees = attendees;
    }

    public LocalizedString getSubjectItems() {
        return subjectItems;
    }

    public void setSubjectItems(LocalizedString subjectItems) {
        this.subjectItems = subjectItems;
    }

    public LocalizedString getContactInfo() {
        return contactInfo;
    }

    public void setContactInfo(LocalizedString contactInfo) {
        this.contactInfo = contactInfo;
    }

    public LocalizedString getSummary() {
        return summary;
    }

    public void setSummary(LocalizedString summary) {
        this.summary = summary;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }
}
