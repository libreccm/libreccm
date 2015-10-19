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
package org.librecms.article;

import static org.librecms.article.ArticleConstants.*;

import org.hibernate.envers.Audited;

import org.librecms.contentsection.ContentItem;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Table;
import org.librecms.contenttypes.GenericArticle;

/**
 * This type simply exposes the {@link GenericArticle} type from the
 * @code{ccm-cms} module.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@Entity
@Audited
@Table(name = "${type_name}", schema = DB_SCHEMA)
public class Article extends ContentItem implements Serializable {

    private static final long serialVersionUID = 1L;

}
