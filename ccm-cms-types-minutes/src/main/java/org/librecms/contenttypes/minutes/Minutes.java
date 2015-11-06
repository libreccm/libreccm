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
package org.librecms.contenttypes.minutes;

import static org.librecms.contenttypes.minutes.MinutesConstants.*;

import org.hibernate.envers.Audited;

import java.io.Serializable;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;

/**
 * This content type represents a minutes.
 *
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 3/11/2015
 */
@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class Minutes extends ContentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * The minute number for the minutes
     */
    @Column(name = "MINUTENUMBER")
    private LocalizedString minuteNumber;

    /**
     * The description of the minutes
     */
    @Column(name = "DESCRIPTION")
    private LocalizedString description;

    /**
     * Action item(s) for the minutes
     */
    @Column(name = "ACTIONITEM")
    private LocalizedString actionItem;

    /**
     * The attendees for the minutes
     */
    @Column(name = "ATTENDEES")
    private LocalizedString attendees;

    /**
     * The description for the minutes
     */
    @Column(name = "DESCRIPTIONOFMINUTES")
    private LocalizedString descriptionOfMinutes;

    
    // Getter and setter:
    
    public LocalizedString getMinuteNumber() {
        return minuteNumber;
    }

    public void setMinuteNumber(LocalizedString minuteNumber) {
        this.minuteNumber = minuteNumber;
    }

    public LocalizedString getDescription() {
        return description;
    }

    public void setDescription(LocalizedString description) {
        this.description = description;
    }

    public LocalizedString getActionItem() {
        return actionItem;
    }

    public void setActionItem(LocalizedString actionItem) {
        this.actionItem = actionItem;
    }

    public LocalizedString getAttendees() {
        return attendees;
    }

    public void setAttendees(LocalizedString attendees) {
        this.attendees = attendees;
    }

    public LocalizedString getDescriptionOfMinutes() {
        return descriptionOfMinutes;
    }

    public void setDescriptionOfMinutes(LocalizedString descriptionOfMinutes) {
        this.descriptionOfMinutes = descriptionOfMinutes;
    }

}
