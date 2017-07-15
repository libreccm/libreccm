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
package org.librecms.contenttypes;

import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 * A section of a MultiPartArticle
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "MULTIPART_ARTICLE_SECTIONS", schema = DB_SCHEMA)
@NamedQueries({
    @NamedQuery(
        name = "MultiPartArticleSection.findById",
        query = "SELECT s FROM MultiPartArticleSection s "
                    + "WHERE s.sectionId = :sectionId")
    ,
    @NamedQuery(
        name = "MultiPartArticleSection.findArticleOfSection",
        query = "SELECT a FROM MultiPartArticle a "
                    + "WHERE :section MEMBER OF a.sections")
})
public class MultiPartArticleSection implements Serializable {

    private static final long serialVersionUID = 1109186628988745920L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "SECTION_ID")
    private long sectionId;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "MPA_SECTION_TITLES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString title;

    @Column(name = "RANK")
    private int rank;

    @Column(name = "PAGE_BREAK")
    private boolean pageBreak;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "MPA_SECTION_TEXTS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString text;

    public MultiPartArticleSection() {
        super();
        title = new LocalizedString();
        text = new LocalizedString();
    }

    public long getSectionId() {
        return sectionId;
    }

    protected void setSectionId(final long sectionId) {
        this.sectionId = sectionId;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(final LocalizedString title) {
        Objects.requireNonNull(title);
        this.title = title;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(final int rank) {
        this.rank = rank;
    }

    public boolean isPageBreak() {
        return pageBreak;
    }

    public void setPageBreak(final boolean pageBreak) {
        this.pageBreak = pageBreak;
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        Objects.requireNonNull(text);
        this.text = text;
    }

    //ToDo: Add image property
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 19 * hash + Objects.hashCode(title);
        hash = 19 * hash + rank;
        hash = 19 * hash + (pageBreak ? 1 : 0);
        hash = 19 * hash + Objects.hashCode(text);
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
        if (!(obj instanceof MultiPartArticleSection)) {
            return false;
        }
        final MultiPartArticleSection other = (MultiPartArticleSection) obj;
        if (rank != other.getRank()) {
            return false;
        }
        if (!other.canEqual(this)) {
            return false;
        }
        if (pageBreak != other.isPageBreak()) {
            return false;
        }
        if (!Objects.equals(title, other.getTitle())) {
            return false;
        }
        return Objects.equals(text, other.getText());
    }

    public boolean canEqual(final Object obj) {
        return obj instanceof MultiPartArticleSection;
    }

    @Override
    public final String toString() {
        return toString("");
    }

    public String toString(final String data) {
        return String.format("%s{ "
                                 + "title = %s, "
                                 + "rank = %d,"
                                 + "pageBreak = %b, "
                                 + "text = %s%d"
                                 + " }",
                             super.toString(),
                             title,
                             rank,
                             pageBreak,
                             text,
                             data
        );
    }

}
