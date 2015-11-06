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
package org.librecms.contenttypes.event;

import static org.librecms.contenttypes.event.EventConstants.*;

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
 * This class represent the content type event.
 * <br />
 * <p>
 * This class extends
 * {@link org.librecms.contentsection.ContentItem content item} and adds
 * extended attributes specific for an event:</p>
 * <dl>
 * <dt>lead</dt>            <dd>optional, standard text field, short description (summary),
 * used as lead text</dd>
 * <dt>startDate</dt>       <dd>mandatory, date when it will begin</dd>
 * <dt>startTime</dt>       <dd>mandatory, time when it will begin</dd>
 * <dt>endDate</dt>         <dd>optional, date when it will end</dd>
 * <dt>endTime</dt>         <dd>optional, time when it will end</dd>
 * <dt>eventDate</dt>       <dd>optional, rich text field. From pdl File: The date and
 * time of the event, stored as varchar for now so you can enter other
 * information configurable as hidden in the authoring kit</dd>
 * <dt>location</dt>        <dd>optional, rich text field, description of location</dd>
 * <dt>mainContributor</dt> <dd>optional, rich text field, configurable as
 * hidden in the authoring kit</dd>
 * <dt>eventType</dt>       <dd>optional, standard text field, type of event,
 * configurable as hidden in the authoring kit</dd>
 * <dt>mapLink</dt>         <dd>optional, standard text field, ling to a map,
 * configurable as hidden in the authoring kit</dd>
 * <dt>cost</dt>            <dd>optional, standard text field, costs, configurable as
 * hidden in the authoring kit</dd>
 * </dl>
 *
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 3/11/2015
 *
 */
@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class Event extends ContentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public Event() {
        super();
    }
    /**
     * The starting date and time of the event, so the events content type can
     * be used by calendar
     */
    @Column(name = "STARTDATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    @NotEmpty
    private Date startDate;

    /**
     * The ending date time of the event, so the events content type can be used
     * by calendar
     */
    @Column(name = "ENDDATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date endDate;

    /**
     * The starting time of the event
     */
    @Column(name = "STARTTIME")
    @Temporal(javax.persistence.TemporalType.TIME)
    @NotEmpty
    private Date startTime;

    /**
     * The ending time of the event
     */
    @Column(name = "ENDTIME")
    @Temporal(javax.persistence.TemporalType.TIME)
    private Date endTime;

    /**
     * The date and time of the event, stored as varchar for now so you can
     * enter other information
     */
    @Column(name = "EVENTDATE")
    private LocalizedString eventDate;

    /**
     * The location of the event
     */
    @Column(name = "LOCATION")
    private LocalizedString location;

    /**
     * The tease/lead information for the event
     */
    @Column(name = "LEAD")
    private LocalizedString lead;

    /**
     * The main contributor for the event
     */
    @Column(name = "MAINCONTRIBUTOR")
    private LocalizedString mainContributot;

    /**
     * The type of the event
     */
    @Column(name = "EVENTTYPE")
    private LocalizedString eventType;

    /**
     * The link to a map for the event
     */
    @Column(name = "MAPLINK")
    private LocalizedString mapLink;

    /**
     * The cost of the event
     */
    @Column(name = "COST")
    private LocalizedString cost;

    // Getter and Setter:  
    public Date getStartDate() {
        return new Date(startDate.getTime());
    }

    public void setStartDate(Date startDate) {
        this.startDate = new Date(startDate.getTime());
    }

    public Date getEndDate() {
        return new Date(endDate.getTime());
    }

    public void setEndDate(Date endDate) {
        this.endDate = new Date(endDate.getTime());
    }

    public Date getStartTime() {
        return new Date(startTime.getTime());
    }

    public void setStartTime(Date startTime) {
        this.startTime = new Date(startTime.getTime());
    }

    public Date getEndTime() {
        return new Date(endTime.getTime());
    }

    public void setEndTime(Date ENDTIME) {
        this.endTime = new Date(endTime.getTime());
    }

    public LocalizedString getEventdate() {
        return eventDate;
    }

    public void setEventdate(LocalizedString eventdate) {
        this.eventDate = eventdate;
    }

    public LocalizedString getLocation() {
        return location;
    }

    public void setLocation(LocalizedString location) {
        this.location = location;
    }

    public LocalizedString getLead() {
        return lead;
    }

    public void setLead(LocalizedString lead) {
        this.lead = lead;
    }

    public LocalizedString getMainContributot() {
        return mainContributot;
    }

    public void setMainContributot(LocalizedString mainContributot) {
        this.mainContributot = mainContributot;
    }

    public LocalizedString getEventType() {
        return eventType;
    }

    public void setEventType(LocalizedString eventType) {
        this.eventType = eventType;
    }

    public LocalizedString getMapLink() {
        return mapLink;
    }

    public void setMapLink(LocalizedString mapLink) {
        this.mapLink = mapLink;
    }

    public LocalizedString getCost() {
        return cost;
    }

    public void setCost(LocalizedString cost) {
        this.cost = cost;
    }

}
