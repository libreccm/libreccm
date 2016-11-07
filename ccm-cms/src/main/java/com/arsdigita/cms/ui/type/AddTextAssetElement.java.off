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
import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.TextAsset;
import com.arsdigita.formbuilder.PersistentDeditor;
import com.arsdigita.formbuilder.PersistentForm;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.metadata.DynamicObjectType;
import com.arsdigita.persistence.metadata.MetadataRoot;


/**
 * This class contains the form component for adding a TextAsset element to
 * a content type
 *
 * @author Scott Seago (scott@arsdigita.com)
 * @version $Revision: #13 $ $Date: 2004/08/17 $
 */
public class AddTextAssetElement extends ElementAddForm {


    /**
     * Constructor
     */
    public AddTextAssetElement(ACSObjectSelectionModel types) {
        super("ContentTypeAddTextAssetElement", "Add a TextAsset Element", types);

        add(m_buttons, ColumnPanel.FULL_WIDTH|ColumnPanel.CENTER);
    }

    protected final void addAttribute(DynamicObjectType dot, String label,
                                      PageState state)
        throws FormProcessException {

        dot.addOptionalAssociation(label,
                                   MetadataRoot.getMetadataRoot().getObjectType
                                   (TextAsset.BASE_DATA_OBJECT_TYPE));
    }


    protected final void addFormComponent(PersistentForm pForm, String label,
                                          PageState state)
        throws FormProcessException {

        PersistentDeditor pTextArea = PersistentDeditor.create(label);
        pTextArea.save();
        pForm.addComponent(pTextArea);
    }
}
