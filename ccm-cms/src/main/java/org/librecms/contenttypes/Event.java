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
package org.librecms.contenttypes;

import com.arsdigita.cms.ui.authoring.event.EventPropertiesStep;
import com.arsdigita.cms.ui.authoring.event.EventTextBody;
import com.arsdigita.cms.ui.contenttypes.EventCreateForm;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.envers.Audited;

import org.libreccm.l10n.LocalizedString;
import org.librecms.CmsConstants;

import org.librecms.contentsection.ContentItem;

import javax.validation.constraints.NotNull;

import static org.librecms.CmsConstants.*;

/**
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "EVENTS", schema = DB_SCHEMA)
@ContentTypeDescription(labelBundle = "org.librecms.contenttypes.Event",
                        descriptionBundle = "org.librecms.contenttypes.Event")
@AuthoringKit(
    createComponent = EventCreateForm.class,
    steps = {
        @AuthoringStep(
            component = EventPropertiesStep.class,
            labelBundle = CmsConstants.CMS_BUNDLE,
            labelKey = "cms.contenttypes.shared.basic_properties.title",
            descriptionBundle = CmsConstants.CMS_BUNDLE,
            descriptionKey = "cms.contenttypes.shared.basic_properties"
                                 + ".description",
            order = 1)
        ,
        @AuthoringStep(
            component = EventTextBody.class,
            labelBundle = CmsConstants.CMS_BUNDLE,
            labelKey = "cms.contenttypes.shared.body_text.title",
            descriptionBundle = CmsConstants.CMS_BUNDLE,
            descriptionKey = "cms.contenttypes.shared.body_text.description",
            order = 2
        )
    })
public class Event extends ContentItem implements Serializable {

    private static final long serialVersionUID = -9104886733503414635L;

    /**
     * Short description of the news, usually used as teaser.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "EVENT_TEXTS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString text;

    @Column(name = "START_DATE", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    @NotNull
    private Date startDate;

    @Column(name = "END_DATE")
    @Temporal(TemporalType.TIMESTAMP)
    private Date endDate;

    /**
     * Additional information about the date the event takes places.
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "EVENT_DATES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString eventDate;

    /**
     * The location of the event
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "EVENT_LOCATIONS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString location;

    /**
     * The main contributor for the event
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "EVENT_MAIN_CONTRIBUTORS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString mainContributor;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "EVENT_TYPES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString eventType;

    //ToDo: check if this is necessary or can be better handled using related links.
    @Column(name = "MAP_LINK")
    private String mapLink;

    /**
     * The cost of the event
     */
    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "EVENT_COSTS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString cost;

    public Event() {
        text = new LocalizedString();
        eventDate = new LocalizedString();
        location = new LocalizedString();
        mainContributor = new LocalizedString();
        eventType = new LocalizedString();
        cost = new LocalizedString();
    }
    
    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        Objects.requireNonNull(text);
        this.text = text;
    }

    public Date getStartDate() {
        if (startDate == null) {
            return null;
        } else {
            return new Date(startDate.getTime());
        }
    }

    public void setStartDate(final Date startDate) {
        if (startDate == null) {
            this.startDate = null;
        } else {
            this.startDate = new Date(startDate.getTime());
        }
    }

    public Date getEndDate() {
        if (endDate == null) {
            return null;
        } else {
            return new Date(endDate.getTime());
        }
    }

    public void setEndDate(final Date endDate) {
        if (endDate == null) {
            this.endDate = null;
        } else {
            this.endDate = new Date(endDate.getTime());
        }
    }

    public LocalizedString getEventDate() {
        return eventDate;
    }

    public void setEventDate(final LocalizedString eventDate) {
        Objects.requireNonNull(eventDate);
        this.eventDate = eventDate;
    }

    public LocalizedString getLocation() {
        return location;
    }

    public void setLocation(final LocalizedString location) {
        Objects.requireNonNull(location);
        this.location = location;
    }

    public LocalizedString getMainContributor() {
        return mainContributor;
    }

    public void setMainContributor(final LocalizedString mainContributor) {
        Objects.requireNonNull(mainContributor);
        this.mainContributor = mainContributor;
    }

    public LocalizedString getEventType() {
        return eventType;
    }

    public void setEventType(final LocalizedString eventType) {
        Objects.requireNonNull(eventType);
        this.eventType = eventType;
    }

    public String getMapLink() {
        return mapLink;
    }

    public void setMapLink(final String mapLink) {
        this.mapLink = mapLink;
    }

    public LocalizedString getCost() {
        return cost;
    }

    public void setCost(final LocalizedString cost) {
        Objects.requireNonNull(cost);
        this.cost = cost;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(text);
        hash = 97 * hash + Objects.hashCode(startDate);
        hash = 97 * hash + Objects.hashCode(endDate);
        hash = 97 * hash + Objects.hashCode(eventDate);
        hash = 97 * hash + Objects.hashCode(location);
        hash = 97 * hash + Objects.hashCode(mainContributor);
        hash = 97 * hash + Objects.hashCode(mapLink);
        hash = 97 * hash + Objects.hashCode(cost);
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
        if (!super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof Event)) {
            return false;
        }
        final Event other = (Event) obj;
        if (!other.canEqual(obj)) {
            return false;
        }

        if (!Objects.equals(mapLink, other.getMapLink())) {
            return false;
        }
        if (!Objects.equals(text, other.getText())) {
            return false;
        }
        if (!Objects.equals(startDate, other.getStartDate())) {
            return false;
        }
        if (!Objects.equals(endDate, other.getEndDate())) {
            return false;
        }
        if (!Objects.equals(eventDate, other.getEventDate())) {
            return false;
        }
        if (!Objects.equals(location, other.getLocation())) {
            return false;
        }
        if (!Objects.equals(mainContributor, other.getMainContributor())) {
            return false;
        }
        if (!Objects.equals(eventType, other.getEventType())) {
            return false;
        }
        return Objects.equals(cost, other.getCost());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Event;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", text = %s, "
                                                + "startDate = %tF %<tT, "
                                                + "endDate = %tF %<tT, "
                                                + "eventDate = %s, "
                                                + "location = %s,"
                                                + "mainContributor, %s, "
                                                + "eventType = %s, "
                                                + "mapLink = \"%s\", "
                                                + "cost = %s%s",
                                            Objects.toString(text),
                                            startDate,
                                            endDate,
                                            Objects.toString(eventDate),
                                            Objects.toString(location),
                                            Objects.toString(mainContributor),
                                            Objects.toString(eventType),
                                            mapLink,
                                            Objects.toString(cost),
                                            data));
    }

}
