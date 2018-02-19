/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 *
 */
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;

import org.librecms.contentsection.ContentItem;

/**
 * A wrapper around the {@link ItemSearchSection} which embeds the form section
 * in a form.
 *
 * @author Stanislav Freidin (sfreidin@arsdigita.com)
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemSearch extends Form implements Resettable {

    public static final String SINGLE_TYPE_PARAM = ItemSearchSection.SINGLE_TYPE_PARAM;
    
    private ItemSearchSection itemSearchSection;

    /**
     * Construct a new <code>ItemSearch</code> component Default to limit the
     * search to current content section
     *
     * @param context the context for the retrieved items. Should be
     * {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     */
    public ItemSearch(String context) {
        this(context, true);
    }

    /**
     * Construct a new <code>ItemSearch</code> component
     *
     * @param context the context for the retrieved items. Should be
     * {@link ContentItem#DRAFT} or {@link ContentItem#LIVE}
     * @param limitToContentSection limit the search to the current content
     * section
     */
    public ItemSearch(final String context, 
                      final boolean limitToContentSection) {
        super("itemSearch", new SimpleContainer());
        //setMethod("GET");
        itemSearchSection = createSearchSection(context, limitToContentSection);
        super.add(itemSearchSection);
    }

    protected ItemSearchSection createSearchSection(final String context,
                                                    boolean limitToContentSection) {
        return new ItemSearchSection(context, limitToContentSection);
    }

    @Override
    public void reset(final PageState state) {
        itemSearchSection.reset(state);
    }
}
