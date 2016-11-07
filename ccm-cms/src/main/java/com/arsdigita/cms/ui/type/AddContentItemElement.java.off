/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui.type;

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.ContentTypeCollection;
import com.arsdigita.cms.util.GlobalizationUtil;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.formbuilder.PersistentHidden;
import com.arsdigita.formbuilder.PersistentSingleSelect;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;
import com.arsdigita.util.Assert;
import com.arsdigita.util.UncheckedWrapperException;
import org.apache.log4j.Logger;

import java.math.BigDecimal;
import java.util.TooManyListenersException;

/**
 * This class contains the form component for adding a Content Item element to
 * a content type
 *
 * @author Scott Seago (scott@arsdigita.com)
 * @version $Revision: #15 $ $Date: 2004/08/17 $
 */
public class AddContentItemElement extends ElementAddForm {

    private static final Logger s_log =
            Logger.getLogger(AddContentItemElement.class);
    private SingleSelect m_itemTypeSelect;

    /**
     * Constructor
     */
    public AddContentItemElement(ACSObjectSelectionModel types) {
        super("ContentTypeAddContentItemElement", "Add a ContentItem Element", types);

        add(new Label(GlobalizationUtil.globalize("cms.ui.type.association_content_type")));
        m_itemTypeSelect = new SingleSelect(new BigDecimalParameter("AddContentItemTypeSelect"));
        try {
            m_itemTypeSelect.addPrintListener(new ItemTypeSelectPrintListener());
        } catch (TooManyListenersException ex) {
            s_log.error("too many listeners", ex);
            throw new UncheckedWrapperException(ex);
        }
        add(m_itemTypeSelect);

        add(m_buttons, ColumnPanel.FULL_WIDTH | ColumnPanel.CENTER);
    }

    private ContentType getItemType(PageState state)
            throws FormProcessException {

        BigDecimal itemTypeID =
                (BigDecimal) m_itemTypeSelect.getValue(state);

        ContentType itemType = null;
        Assert.exists(itemTypeID, "itemTypeID");
        try {
            itemType = new ContentType(itemTypeID);
        } catch (DataObjectNotFoundException ex) {
            throw new FormProcessException(GlobalizationUtil.globalize("cms.ui.type.invalid"));
        }
        return itemType;
    }

    protected final void addAttribute(DynamicObjectType dot, String label,
            PageState state)
            throws FormProcessException {

        ContentType itemType = getItemType(state);
        dot.addOptionalAssociation(label,
                MetadataRoot.getMetadataRoot().getObjectType(itemType.getAssociatedObjectType()));
    }

    protected final void addFormComponent(PersistentForm pForm, String label,
            PageState state)
            throws FormProcessException {

        ContentType itemType = getItemType(state);
        PersistentHidden pContentTypeName = PersistentHidden.create(label + "Type");
        pContentTypeName.setDefaultValue(itemType.getAssociatedObjectType());
        pContentTypeName.save();
        pForm.addComponent(pContentTypeName);
        PersistentSingleSelect pSelect = PersistentSingleSelect.create(label);
        pSelect.setParameterModel("com.arsdigita.bebop.parameters.BigDecimalParameter");
        pSelect.save();
        pForm.addComponent(pSelect);
    }

    /**
     * Print listener: generates the SingleSelect options for itemType
     */
    private class ItemTypeSelectPrintListener implements PrintListener {

        public void prepare(PrintEvent event) {

            SingleSelect t = (SingleSelect) event.getTarget();
            t.clearOptions();

            // Get the current content section
            ContentSection section = CMS.getContext().getContentSection();

            ContentTypeCollection contentTypes = section.getCreatableContentTypes(true);
            contentTypes.addOrder(ContentType.LABEL);
            while (contentTypes.next()) {
                ContentType type = contentTypes.getContentType();
                t.addOption(new Option(type.getID().toString(), type.getName()));
            }
        }
    }
}
