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
 */
package com.arsdigita.london.terms.ui;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.categorization.ui.ACSObjectCategoryForm;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentItem;

import com.arsdigita.cms.ui.authoring.ItemCategoryForm;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.core.CcmObject;

/**
 * Replacement for cms authoring ItemCategoryForm which replaces the category
 * widget with a terms based widget.
 *
 * Provides a ccm-cms specific concrete implementation of
 * {@link com.arsdigita.london.terms.ui.ACSObjectCategoryPicker}. 
 *
 * Is is activated / used by pointing the parameter
 * {@code com.arsdigita.cms.category_authoring_add_form} to it.
*/
public class ItemCategoryPicker extends ACSObjectCategoryPicker {

    private static final Logger LOGGER = LogManager
        .getLogger(ItemCategoryPicker.class);

    public ItemCategoryPicker(final LongParameter root,
                              final StringParameter mode) {
        super(root, mode);
        LOGGER.debug("instantiating ItemCategoryPicker");

    }

    /*
     * @see com.arsdigita.london.terms.ui.ACSObjectCategoryPicker#getForm(
     *                   com.arsdigita.bebop.parameters.BigDecimalParameter, 
     *                   com.arsdigita.bebop.parameters.StringParameter)
     */
    @Override
    protected ACSObjectCategoryForm getForm(final LongParameter root,
                                            final StringParameter mode) {
        LOGGER.debug("getForm");
        return new ItemCategoryForm(root, mode, new TermWidget(mode, this));
    }


    /* 
     * @see com.arsdigita.london.terms.ui.ACSObjectCategoryPicker#getObject()
     */
    @Override
    protected CcmObject getObject(final PageState state) {

        return CMS.getContext().getContentItem();
    }

}
