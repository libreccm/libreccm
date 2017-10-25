/*
 * Copyright (C) 2017 LibreCCM Foundation.
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
package org.librecms.ui.authoring.article;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import org.librecms.contentsection.ContentItem;
import org.librecms.contenttypes.Article;
import org.librecms.ui.ContentSectionViewController;

import java.io.Serializable;
import java.util.Objects;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ArticlePropertiesStep 
    extends CustomComponent
    implements Serializable {

    private static final long serialVersionUID = 1587965921855375545L;

    private final ContentSectionViewController controller;
    private final Article article;
    
    public ArticlePropertiesStep(final ContentSectionViewController controller,
                                 final ContentItem item) {
        
        Objects.requireNonNull(controller);
        Objects.requireNonNull(item);
        
        if (!(item instanceof Article)) {
            throw new IllegalArgumentException(String
                .format("The provided ContentItem is not an instance "
                    + "of class \"%s\" but of class \"%s\".",
                        Article.class.getName(),
                        item.getClass().getName()));
        }
        
        this.controller =  controller;
        article = (Article) item;
        
        final TextField titleField = new TextField("Title");
        
    }

}
