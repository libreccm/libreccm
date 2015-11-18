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

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import org.hibernate.envers.Audited;
import org.libreccm.l10n.LocalizedString;
import static org.librecms.CmsConstants.DB_SCHEMA;

/**
 * object type to hold sections for multi-part article
 *
 * @author Koalamann
 */
@Entity
@Audited
@Table(name = "DECISIONTREE", schema = DB_SCHEMA) //?
public class ArticleSection implements Serializable {

    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue
    @Column(name = "ID")
    private Long id;

    @Column(name = "TITLE")
    private LocalizedString title;

    @Column(name = "RANK")
    private int rank;

    @Column(name = "PAGEBREAK")
    private boolean pageBreak;

    @Column(name = "TEXT")
    private LocalizedString text;

    @Column(name = "IMAGE") 
    private LocalizedString image;
    
    @ManyToOne
    private MultiPartArticle mparticle;
    
    
    //Getter and setter:

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalizedString getTitle() {
        return title;
    }

    public void setTitle(LocalizedString title) {
        this.title = title;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public boolean isPageBreak() {
        return pageBreak;
    }

    public void setPageBreak(boolean pageBreak) {
        this.pageBreak = pageBreak;
    }

    public LocalizedString getText() {
        return text;
    }

    public void setText(LocalizedString text) {
        this.text = text;
    }

}
