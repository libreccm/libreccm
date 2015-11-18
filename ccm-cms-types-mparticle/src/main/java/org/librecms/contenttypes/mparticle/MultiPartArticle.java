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
package org.librecms.contenttypes.mparticle;

import static org.librecms.contenttypes.mparticle.MultiPartArticleConstants.*;

import org.hibernate.envers.Audited;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import org.libreccm.l10n.LocalizedString;
import org.librecms.contentsection.ContentItem;

/*
 *  This class represents the content type MultiPartArctivle 
 *
 * @author <a href="mailto:konerman@tzi.de">Alexander Konermann</a>
 * @version 17/11/2015
 */
@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class MultiPartArticle extends ContentItem implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Column(name = "SUMMARY")
    private LocalizedString summary;

    @OneToMany(mappedBy = "article")
    private List<ArticleSection> articles;

    public LocalizedString getSummary() {
        return summary;
    }

    public void setSummary(LocalizedString summary) {
        this.summary = summary;
    }

    public List<ArticleSection> getArticles() {
        return articles;
    }

    public void setArticles(List<ArticleSection> articles) {
        this.articles = articles;
    }

}
