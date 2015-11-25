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
package org.librecms.contenttypes.newsitem;

import static org.librecms.contenttypes.newsitem.NewsitemConstants.*;

import org.hibernate.envers.Audited;

import org.librecms.contentsection.ContentItem;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import org.hibernate.validator.constraints.NotEmpty;
import org.libreccm.l10n.LocalizedString;

/**
 * This content type represents a newsitem.
 *
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 25/11/2015
 */
@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class Newsitem extends ContentItem implements Serializable {

    /**
     * The tease/lead paragraph for the news item
     */
    @Column(name = "LEAD")
    private LocalizedString lead;

    /**
     * The date for the news item
     */
    @Column(name = "NEWSDATE")
    @Temporal(javax.persistence.TemporalType.DATE)
    private Date newsDate;
    
    /**
     * True, if it is a Homepage
     */
    @Column(name = "ISHOMEPAGE")
    @NotEmpty
    private boolean isHompage;

    
    //Getter and setter:
    public LocalizedString getLead() {
        return lead;
    }

    public void setLead(LocalizedString lead) {
        this.lead = lead;
    }

    public Date getNewsDate() {
        return newsDate;
    }

    public void setNewsDate(Date newsDate) {
        this.newsDate = newsDate;
    }

    public boolean isIsHompage() {
        return isHompage;
    }

    public void setIsHompage(boolean isHompage) {
        this.isHompage = isHompage;
    }
    
    
}
