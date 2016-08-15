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
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.util.UncheckedWrapperException;

import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeRepository;

import javax.servlet.ServletException;

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
public class ItemSelectionModel extends ACSObjectSelectionModel {

    private Long m_typeId;

    private static final Logger s_log = Logger.getLogger(
        ItemSelectionModel.class);

    /**
     * Construct a new <code>ItemSelectionModel</code>. This model will produce
     * instances of <code>ContentItem</code> by automatically instantiating the
     * correct Java subclass using the
     * {@link com.arsdigita.domain.DomainObjectFactory}.
     *
     * @param parameter The state parameter which should be used to store the
     *                  object ID
     */
    public ItemSelectionModel(BigDecimalParameter parameter) {
        this(null, null, parameter);
    }

    /**
     * Construct a new <code>ItemSelectionModel</code>. This model will produce
     * instances of <code>ContentItem</code> by automatically instantiating the
     * correct Java subclass using the
     * {@link com.arsdigita.domain.DomainObjectFactory}.
     *
     * @param parameterName The name of the state parameter which will be used
     *                      to store the object ID.
     */
    public ItemSelectionModel(String parameterName) {
        this(null, null, new BigDecimalParameter(parameterName));
    }

    /**
     * Construct a new <code>ItemSelectionModel</code>. This model will produce
     * instances of <code>ContentItem</code> by automatically instantiating the
     * correct Java subclass using the
     * {@link com.arsdigita.domain.DomainObjectFactory}.
     *
     * @param model The {@link SingleSelectionModel} which will supply a
     *              {@link BigDecimal} ID of the currently selected item
     */
    public ItemSelectionModel(SingleSelectionModel model) {
        this(null, null, model);
    }

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
        this(type, new BigDecimalParameter(parameterName));
    }

    /**
     * Construct a new <code>ItemSelectionModel</code>
     *
     * @param type      The content type for the items this model will generate
     *
     * @param parameter The state parameter which should be used by this item
     *
     */
    public ItemSelectionModel(ContentType type, BigDecimalParameter parameter) {
        super(type.getContentItemClass(), type.getContentItemClass(), parameter);
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
        super(type.getContentItemClass(), type.getContentItemClass(), model);
        m_typeId = type.getObjectId();
    }

    /**
     * Construct a new <code>ItemSelectionModel</code>
     *
     * @param itemClass     The name of the Java class which represents the
     *                      content item. Must be a subclass of ContentItem. In
     *                      addition, the class must have a constructor with a
     *                      single OID parameter.
     * @param objectType    The name of the persistence metadata object type
     *                      which represents the content item. In practice, will
     *                      often be the same as the itemClass.
     * @param parameterName The name of the state parameter which will be used
     *                      to store the item.
     */
    public ItemSelectionModel(String itemClass, 
                              String objectType,
                              String parameterName) {
        super(itemClass, objectType, new BigDecimalParameter(parameterName));
    }

    /**
     * Construct a new <code>ItemSelectionModel</code>
     *
     * @param itemClass  The name of the Java class which represents the content
     *                   item. Must be a subclass of ContentItem. In addition,
     *                   the class must have a constructor with a single OID
     *                   parameter.
     * @param objectType The name of the persistence metadata object type which
     *                   represents the content item. In practice, will often be
     *                   the same as the itemClass.
     * @param parameter  The state parameter which should be used by this item
     */
    public ItemSelectionModel(String itemClass, String objectType,
                              BigDecimalParameter parameter) {
        super(itemClass, objectType, parameter);
    }

    /**
     * Construct a new <code>ItemSelectionModel</code>
     *
     * @param itemClass  The name of the Java class which represents the content
     *                   item. Must be a subclass of ContentItem. In addition,
     *                   the class must have a constructor with a single OID
     *                   parameter.
     * @param objectType The name of the persistence metadata object type which
     *                   represents the content item. In practice, will often be
     *                   the same as the itemClass.
     * @param model      The {@link SingleSelectionModel} which will supply a
     *                   {@link BigDecimal} id of the currently selected object
     *
     */
    public ItemSelectionModel(String itemClass, 
                              String objectType,
                              SingleSelectionModel model) {
        super(itemClass, objectType, model);
    }

    /**
     * A utility function which creates a new item with the given ID. Uses
     * reflection to create the instance of the class supplied in the
     * constructor to this ItemSelectionModel.
     *
     * @param id The id of the new item -- this is now ignored
     *
     * @return The newly created item
     *
     * @deprecated use createItem() instead
     */
    public ContentItem createItem(BigDecimal id) throws ServletException {
        return (ContentItem) createACSObject();
    }

    /**
     * A utility function which creates a new item. Uses reflection to create
     * the instance of the class supplied in the constructor to this
     * ItemSelectionModel.
     *
     * @return The newly created item
     */
    public ContentItem createItem() throws ServletException {
        return (ContentItem) createACSObject();
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
     * A utility function which creates a new item with the given ID. Uses
     * reflection to create the instance of the class supplied in the
     * constructor to this ItemSelectionModel.
     *
     * @param id The id of the new item -- this is now ignored
     *
     * @return The newly created item
     *
     * @deprecated Use createACSObject() instead
     */
    public CcmObject createACSObject(BigDecimal id) throws ServletException {
        return createACSObject();
    }

    /**
     * A utility function which creates a new item. Uses reflection to create
     * the instance of the class supplied in the constructor to this
     * ItemSelectionModel.
     *
     * @return The newly created item
     */
    public CcmObject createACSObject() throws ServletException {
        ContentType type = getContentType();
        ContentItem item = (ContentItem) super.createACSObject();

        if (type != null) {
            item.setContentType(type);
        }
        return item;
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
