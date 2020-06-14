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
package org.libreccm.workflow;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIdentityReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import org.hibernate.annotations.Type;
import org.libreccm.core.CoreConstants;
import org.libreccm.core.Identifiable;
import org.libreccm.imexport.Exportable;
import org.libreccm.security.User;

import javax.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;

/**
 * A comment for a task. Comments are intended for other users, for example to
 * inform them about problems etc. with the object.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Table(name = "WORKFLOW_TASK_COMMENTS", schema = CoreConstants.DB_SCHEMA)
@NamedQueries({
        @NamedQuery(
                name = "TaskComment.findByUuid",
                query = "SELECT c FROM TaskComment c WHERE c.uuid = :uuid")
})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class,
                  resolver = TaskCommentIdResolver.class,
                  property = "uuid")
public class TaskComment implements Identifiable, Serializable, Exportable {

    private static final long serialVersionUID = 3842991529698351698L;

    /**
     * Database ID of the comment.
     */
    @Id
    @Column(name = "COMMENT_ID")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long commentId;

    /**
     * The UUID of the comment.
     */
    @Column(name = "UUID", unique = true, nullable = false)
    @NotNull
    private String uuid;

    /**
     * The comment.
     */
    @Column(name = "COMMENT")
    @Basic
    @Lob
    @Type(type = "org.hibernate.type.TextType")
    private String comment;

    /**
     * The author of the comment.
     */
    @OneToOne
    @JoinColumn(name = "AUTHOR_ID")
    @JsonIdentityReference(alwaysAsId = true)
    private User author;

    public long getCommentId() {
        return commentId;
    }

    protected void setCommentId(final long commentId) {
        this.commentId = commentId;
    }

    @Override
    public String getUuid() {
        return uuid;
    }

    public void setUuid(final String uuid) {
        this.uuid = uuid;
    }

    public String getComment() {
        return comment;
    }

    protected void setComment(final String comment) {
        this.comment = comment;
    }

    public User getAuthor() {
        return author;
    }

    protected void setAuthor(final User author) {
        this.author = author;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + (int) (commentId ^ (commentId >>> 32));
        hash = 67 * hash + Objects.hashCode(uuid);
        hash = 67 * hash + Objects.hashCode(comment);
        hash = 67 * hash + Objects.hashCode(author);
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
        if (!(obj instanceof TaskComment)) {
            return false;
        }
        final TaskComment other = (TaskComment) obj;
        if (!other.canEqual(this)) {
            return false;
        }

        if (commentId != other.getCommentId()) {
            return false;
        }
        if (!Objects.equals(uuid, other.getUuid())) {
            return false;
        }
        if (!Objects.equals(comment, other.getComment())) {
            return false;
        }
        return Objects.equals(author, other.getAuthor());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof TaskComment;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                     + "commentId = %d, "
                                     + "uuid = \"%s\""
                                     + "comment = \"%s\", "
                                     + "author = %s%s"
                                     + " }",
                             super.toString(),
                             commentId,
                             uuid,
                             comment,
                             Objects.toString(author),
                             data);
    }

}
