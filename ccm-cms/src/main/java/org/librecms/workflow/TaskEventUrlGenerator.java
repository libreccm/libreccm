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
package org.librecms.workflow;

import org.librecms.contentsection.ContentType;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "TASK_EVENT_URL_GENERATOR", schema = DB_SCHEMA)
public class TaskEventUrlGenerator implements Serializable {

    private static final long serialVersionUID = -1861545657474968084L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "GENERATOR_ID")
    private long generatorId;

    @Column(name = "EVENT", length = 256)
    private String event;

    @OneToOne
    @JoinColumn(name = "CONTENT_TYPE_ID")
    private ContentType contentType;

    @Column(name = "URL_GENERATOR_CLASS", length = 1024)
    private String urlGeneratorClass;

    public long getGeneratorId() {
        return generatorId;
    }

    public void setGeneratorId(final long generatorId) {
        this.generatorId = generatorId;
    }

    public String getEvent() {
        return event;
    }

    public void setEvent(final String event) {
        this.event = event;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(final ContentType contentType) {
        this.contentType = contentType;
    }

    public String getUrlGeneratorClass() {
        return urlGeneratorClass;
    }

    public void setUrlGeneratorClass(final String urlGeneratorClass) {
        this.urlGeneratorClass = urlGeneratorClass;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + (int) (generatorId ^ (generatorId >>> 32));
        hash = 47 * hash + Objects.hashCode(event);
        hash = 47 * hash + Objects.hashCode(contentType);
        hash = 47 * hash + Objects.hashCode(urlGeneratorClass);
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
        if (obj instanceof TaskEventUrlGenerator) {
            return false;
        }
        final TaskEventUrlGenerator other = (TaskEventUrlGenerator) obj;
        if (!(other.canEqual(this))) {
            return false;
        }

        if (generatorId != other.getGeneratorId()) {
            return false;
        }
        if (!Objects.equals(event, other.getEvent())) {
            return false;
        }
        if (!Objects.equals(urlGeneratorClass, other.getUrlGeneratorClass())) {
            return false;
        }
        return Objects.equals(contentType, other.getContentType());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof TaskEventUrlGenerator;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "generatorId = %d, "
                                 + "event = \"%s\", "
                                 + "contentType = %s, "
                                 + "urlGeneratorClass = \"%s\"%s"
                                 + " }",
                             super.toString(),
                             generatorId,
                             event,
                             Objects.toString(contentType),
                             urlGeneratorClass,
                             data);
    }

}
