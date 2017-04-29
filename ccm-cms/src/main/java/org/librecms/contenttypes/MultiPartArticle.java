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

import com.arsdigita.cms.contenttypes.ui.mparticle.MultiPartArticleCreateForm;
import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "MULTIPART_ARTICLES", schema = DB_SCHEMA)
@ContentTypeDescription(labelBundle = "org.librecms.contenttypes.MultiPartArticle",
                        descriptionBundle = "org.librecms.contenttypes.MultiPartArticle")
@AuthoringKit(createComponent = MultiPartArticleCreateForm.class,
              steps = {})
public class MultiPartArticle extends ContentItem implements Serializable {

    private static final long serialVersionUID = -587374085831420868L;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "MPA_SUMMARIES",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString summary = new LocalizedString();

    @OneToMany
    @JoinColumn(name = "MULTIPART_ARTICLE_ID")
    private List<MultiPartArticleSection> sections;

    public MultiPartArticle() {
        this.sections = new ArrayList<>();
    }

    public LocalizedString getSummary() {
        return summary;
    }

    public void setSummary(final LocalizedString summary) {
        this.summary = summary;
    }

    public List<MultiPartArticleSection> getSections() {
        if (sections == null) {
            return null;
        } else {
            return Collections.unmodifiableList(sections);
        }
    }

    protected void setSections(final List<MultiPartArticleSection> sections) {
        this.sections = new ArrayList<>(sections);
    }

    public void addSection(final MultiPartArticleSection section) {
        sections.add(section);
    }

    public void removeSection(final MultiPartArticleSection section) {
        sections.remove(section);
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 59 * hash + Objects.hashCode(summary);
        hash = 59 * hash + Objects.hashCode(sections);
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
        if (super.equals(obj)) {
            return false;
        }

        if (!(obj instanceof MultiPartArticle)) {
            return false;
        }
        final MultiPartArticle other = (MultiPartArticle) obj;
        if (!other.canEqual(obj)) {
            return false;
        }

        if (!Objects.equals(summary, other.getSummary())) {
            return false;
        }
        return Objects.equals(sections, other.getSections());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof MultiPartArticle;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", summary = %s%s",
                                            Objects.toString(summary),
                                            data));
    }

}
