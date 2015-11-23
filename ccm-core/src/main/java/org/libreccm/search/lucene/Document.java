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
package org.libreccm.search.lucene;

import static org.libreccm.core.CoreConstants.*;

import org.libreccm.security.User;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "LUCENE_DOCUMENTS", schema = DB_SCHEMA)
//Can't reduce complexity yet. Not sure what to do about the God class warning.
//Maybe we have to put some of the properties into an extra class.
@SuppressWarnings({"PMD.CyclomaticComplexity",
                   "PMD.StdCyclomaticComplexity",
                   "PMD.ModifiedCyclomaticComplexity",
                   "PMD.GodClass"})
public class Document implements Serializable {

    private static final long serialVersionUID = 3363154040440909619L;

    @Id
    @Column(name = "DOCUMENT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long documentId;

    @Column(name = "TYPE")
    private String type;

    @Column(name = "TYPE_SPECIFIC_INFO", length = 512)
    private String typeSpecificInfo;

    @Column(name = "DOCUMENT_TIMESTAMP")
    @Temporal(TemporalType.TIMESTAMP)
    private Date timeStamp;

    @Column(name = "DIRTY")
    private long dirty;

    @Column(name = "DOCUMENT_LANGUAGE", length = 8)
    private String language;

    @Column(name = "COUNTRY", length = 8)
    private String country;

    @Column(name = "TITLE", length = 4096)
    private String title;

    @Column(name = "SUMMARY", length = 4096)
    private String summary;

    @Column(name = "CONTENT")
    @Lob
    private String content;

    @Column(name = "CREATED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date created;

    @OneToOne
    @JoinColumn(name = "CREATED_BY_PARTY_ID")
    private User createdBy;

    @Column(name = "LAST_MODIFIED")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastModified;

    @OneToOne
    @JoinColumn(name = "LAST_MODIFIED_BY")
    private User lastModifiedBy;

    @Column(name = "CONTENT_SECTION", length = 512)
    private String contentSection;

    public Document() {
        super();
    }

    public long getDocumentId() {
        return documentId;
    }

    public void setDocumentId(final long documentId) {
        this.documentId = documentId;
    }

    public String getType() {
        return type;
    }

    public void setType(final String type) {
        this.type = type;
    }

    public String getTypeSpecificInfo() {
        return typeSpecificInfo;
    }

    public void setTypeSpecificInfo(final String typeSpecificInfo) {
        this.typeSpecificInfo = typeSpecificInfo;
    }

    public Date getTimeStamp() {
        if (timeStamp == null) {
            return null;
        } else {
            return new Date(timeStamp.getTime());
        }
    }

    public void setTimeStamp(final Date timeStamp) {
        if (timeStamp == null) {
            this.timeStamp = null;
        } else {
            this.timeStamp = new Date(timeStamp.getTime());
        }
    }

    public long getDirty() {
        return dirty;
    }

    public void setDirty(final long dirty) {
        this.dirty = dirty;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(final String language) {
        this.language = language;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(final String country) {
        this.country = country;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(final String summary) {
        this.summary = summary;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public Date getCreated() {
        if (created == null) {
            return null;
        } else {
            return new Date(created.getTime());
        }
    }

    public void setCreated(final Date created) {
        if (created == null) {
            this.created = null;
        } else {
            this.created = new Date(created.getTime());
        }
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(final User createdBy) {
        this.createdBy = createdBy;
    }

    public Date getLastModified() {
        if (lastModified == null) {
            return null;
        } else {
            return new Date(lastModified.getTime());
        }
    }

    public void setLastModified(final Date lastModified) {
        if (lastModified == null) {
            this.lastModified = null;
        } else {
            this.lastModified = new Date(lastModified.getTime());
        }
    }

    public User getLastModifiedBy() {
        return lastModifiedBy;
    }

    public void setLastModifiedBy(final User lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public String getContentSection() {
        return contentSection;
    }

    public void setContentSection(final String contentSection) {
        this.contentSection = contentSection;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + (int) (documentId ^ (documentId >>> 32));
        hash = 53 * hash + Objects.hashCode(type);
        hash = 53 * hash + Objects.hashCode(typeSpecificInfo);
        hash = 53 * hash + Objects.hashCode(timeStamp);
        hash = 53 * hash + (int) (dirty ^ (dirty >>> 32));
        hash = 53 * hash + Objects.hashCode(language);
        hash = 53 * hash + Objects.hashCode(country);
        hash = 53 * hash + Objects.hashCode(title);
        hash = 53 * hash + Objects.hashCode(summary);
        hash = 53 * hash + Objects.hashCode(content);
        hash = 53 * hash + Objects.hashCode(created);
        hash = 53 * hash + Objects.hashCode(createdBy);
        hash = 53 * hash + Objects.hashCode(lastModified);
        hash = 53 * hash + Objects.hashCode(lastModifiedBy);
        hash = 53 * hash + Objects.hashCode(contentSection);
        return hash;
    }

    @Override
    //Can't reduce complexity yet
    @SuppressWarnings({"PMD.CyclomaticComplexity",
                       "PMD.StdCyclomaticComplexity",
                       "PMD.ModifiedCyclomaticComplexity", 
                       "PMD.NPathComplexity"})
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof Document)) {
            return false;
        }
        final Document other = (Document) obj;
        if (!other.canEqual(this)) {
            return false;
        }
        
        if (documentId != other.getDocumentId()) {
            return false;
        }
        if (!Objects.equals(type, other.getType())) {
            return false;
        }
        if (!Objects.equals(typeSpecificInfo, other.getTypeSpecificInfo())) {
            return false;
        }
        if (!Objects.equals(timeStamp, other.getTimeStamp())) {
            return false;
        }
        if (dirty != other.getDirty()) {
            return false;
        }
        if (!Objects.equals(language, other.getLanguage())) {
            return false;
        }
        if (!Objects.equals(country, other.getCountry())) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        if (!Objects.equals(summary, other.getSummary())) {
            return false;
        }
        if (!Objects.equals(content, other.getContent())) {
            return false;
        }
        if (!Objects.equals(created, other.getCreated())) {
            return false;
        }
        if (!Objects.equals(createdBy, other.getCreatedBy())) {
            return false;
        }
        if (!Objects.equals(lastModified, other.getLastModified())) {
            return false;
        }
        if (!Objects.equals(lastModifiedBy, other.getLastModifiedBy())) {
            return false;
        }
        return Objects.equals(contentSection, other.getContentSection());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof Document;
    }
    
    @Override
    public String toString() {
        return String.format("%s{ "
                                 + "documentId = %d, "
                                 + "type = \"%s\", "
                                 + "documentTimeStamp = %tF %<tT,"
                                 + "dirty = %d, "
                                 + "documentLanguage = \"%s\", "
                                 + "country = \"%s\", "
                                 + "title = \"%s\", "
                                 + "created = %tF %<tT, "
                                 + "createdBy = %s, "
                                 + "lastModified = %tF %<tT, "
                                 + "lastModifiedBy = %s, "
                                 + "contentSection = \"%s\""
                                 + " }",
                             super.toString(),
                             documentId,
                             type,
                             timeStamp,
                             dirty,
                             language,
                             country,
                             title,
                             created,
                             Objects.toString(createdBy),
                             lastModified,
                             Objects.toString(lastModifiedBy),
                             contentSection);
    }

}
