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

import com.arsdigita.cms.ui.authoring.article.ArticlePropertiesStep;
import com.arsdigita.cms.ui.authoring.article.ArticleTextBody;
import com.arsdigita.cms.ui.authoring.PageCreateForm;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.AssociationOverride;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Table;

import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;

import static org.librecms.CmsConstants.*;

import org.librecms.contentsection.ContentItem;

/**
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "ARTICLES", schema = DB_SCHEMA)
@ContentTypeDescription(labelBundle = "org.librecms.contenttypes.Article",
                        descriptionBundle = "org.librecms.contenttypes.Article")
@AuthoringKit(
    createComponent = PageCreateForm.class,
    steps = {
        @AuthoringStep(
            component = ArticlePropertiesStep.class,
            labelBundle = "org.librecms.CmsResources",
            labelKey = "cms.contenttypes.shared.basic_properties.title",
            descriptionBundle = "org.librecms.CmsResources",
            descriptionKey = "cms.contenttypes.shared.basic_properties"
                                 + ".description",
            order = 1),
        @AuthoringStep(
            component = ArticleTextBody.class,
            labelBundle = "org.librecms.CmsResources",
            labelKey = "cms.contenttypes.shared.body_text.title",
            descriptionBundle = "org.librecms.CmsResources",
            descriptionKey = "cms.contenttypes.shared.body_text.description",
            order = 2
        )
    })
public class Article extends ContentItem implements Serializable {

    private static final long serialVersionUID = 3832010184748095822L;

    @Embedded
    @AssociationOverride(
        name = "values",
        joinTable = @JoinTable(name = "ARTICLE_TEXTS",
                               schema = DB_SCHEMA,
                               joinColumns = {
                                   @JoinColumn(name = "OBJECT_ID")}
        ))
    private LocalizedString text;

    public Article() {
        super();
        text = new LocalizedString();
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(final LocalizedString text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 29 * hash + Objects.hashCode(text);
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

        if (!(obj instanceof Article)) {
            return false;
        }
        final Article other = (Article) obj;
        if (!(other.canEqual(this))) {
            return false;
        }

        return Objects.equals(text, other.getText());
    }

    @Override
    public boolean canEqual(final Object obj) {
        return obj instanceof Article;
    }

    @Override
    public String toString(final String data) {
        return super.toString(String.format(", text = %s%s",
                                            Objects.toString(text),
                                            data));
    }

}
