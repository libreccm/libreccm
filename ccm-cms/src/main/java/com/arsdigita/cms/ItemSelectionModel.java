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
package com.arsdigita.cms;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.ui.CcmObjectSelectionModel;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;


import java.math.BigDecimal;

/**
 * <p>
 * Loads a subclass of a {@link com.arsdigita.cms.ContentItem} from the
 * database. This model should be used as a parameter to the constructor of
 * authoring kit components.</p>
 *
 * <p>
 * It is possible to instantiate this model with a {@link
 * com.arsdigita.cms.ContentType} as a constructor parameter. In this case, the
 * model will only instantiate items that are of the specified content type, or
 * one of it subclasses.</p>
 *
 * @author Stanislav Freidin (stas@arsdigita.com)
 * @version $Revision$ $DateTime: 2004/08/17 23:15:09 $
 * @see com.arsdigita.kernel.ui.ACSObjectSelectionModel
 * @see com.arsdigita.bebop.SingleSelectionModel
 */
public class ItemSelectionModel extends CcmObjectSelectionModel {

    private Long m_typeId;

    private static final Logger s_log = Logger.getLogger(
        ItemSelectionModel.class);

    /**
     * Construct a new <code>ItemSelectionModel</code>
     *
     * @param type          The content type for the items this model will
     *                      generate
     *
     * @param parameterName The name of the state parameter which will be used
     *                      to store the item.
     */
    public ItemSelectionModel(ContentType type, String parameterName) {
        this(type, new LongParameter(parameterName));
    }

    /**
     * Construct a new <code>ItemSelectionModel</code>
     *
     * @param type      The content type for the items this model will generate
     *
     * @param parameter The state parameter which should be used by this item
     *
     */
    public ItemSelectionModel(ContentType type, LongParameter parameter) {
        super(type.getContentItemClass(), parameter);
        m_typeId = type.getObjectId();
    }

    /**
     * Construct a new <code>ItemSelectionModel</code>
     *
     * @param type  The content type for the items this model will generate
     *
     * @param model The {@link SingleSelectionModel} which will supply a
     *              {@link BigDecimal} id of the currently selected object
     *
     */
    public ItemSelectionModel(ContentType type, SingleSelectionModel model) {
        super(type.getContentItemClass(), model);
        m_typeId = type.getObjectId();
    }

    /**
     * A convenience method that gets the currently selected object and casts it
     * to a <code>ContentItem</code>
     *
     * @param s the current page state
     *
     * @return the currently selected <code>ContentItem</code>, or null if no
     *         item was selected.
     */
    public final ContentItem getSelectedItem(PageState s) {
        return (ContentItem) getSelectedObject(s);
    }

    /**
     *
     * @return The content type of the items which are produced by this model,
     *         or null if the content type has not been specified in the
     *         constructor.
     */
    public ContentType getContentType() {

        ContentType type = null;

        if (m_typeId != null) {
            type = CdiUtil.createCdiUtil().findBean(ContentTypeRepository.class).findById(m_typeId);
        }

        return type;
    }

}
