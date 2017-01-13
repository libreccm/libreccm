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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.parameters.LongParameter;

import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentType;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.kernel.ui.ACSObjectSelectionModel;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.xml.Element;

import org.librecms.contenttypes.AuthoringKitInfo;
import org.librecms.contenttypes.ContentTypeInfo;


/**
 * An invisible component which contains all the possible authoring kits. The
 * kits are loaded from the database at construction time. The selector chooses
 * which kit to display at page rendering time based on the value of the
 * content_type state parameter.
 *
 * Essentially, this component is a hack which is used to get around the fact
 * that we cannot instantiate stateful components dynamically.
 * 
 * @author unknown
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class WizardSelector extends AuthoringKitSelector
    implements Resettable {

    private final ItemSelectionModel itemSelectionModel;

    /**
     * Construct a new WizardSelector. Load all the possible authoring kits from
     * the database and construct wizards for them.
     *
     * @param model     the {@link ItemSelectionModel} which will supply the
     *                  wizard with its item
     *
     * @param typeModel the {@link ACSObjectSelectionModel} which will supply
     *                  the default content type
     *
     * @pre itemModel != null
     */
    public WizardSelector(final ItemSelectionModel model,
                          final SingleSelectionModel<String> typeModel) {
        super(typeModel);
        itemSelectionModel = model;
        super.processKit();
    }

    /**
     * Get the wizard for the given kit.
     *
     * @param kit
     * @param type
     *
     * @return
     */
    @Override
    public Component instantiateKitComponent(final AuthoringKitInfo kit,
                                             final ContentTypeInfo type) {

        final ItemSelectionModel itemModel = new ItemSelectionModel(
            type, (LongParameter) itemSelectionModel.getStateParameter());

        final AuthoringKitWizard wizard
                                 = new AuthoringKitWizard(type, itemModel);
        return wizard;
    }

    /**
     * @return The item selection model used by this wizard
     */
    public ItemSelectionModel getSelectionModel() {
        return itemSelectionModel;
    }

    // Determine the current wizard
    private Component getCurrentWizard(PageState state) {

        // Get the current item and extract its content type
        if (!itemSelectionModel.isSelected(state)) {
            throw new UncheckedWrapperException("No item selected.");
        }

        final ContentItem item = itemSelectionModel.getSelectedObject(state);

        final ContentType type = item.getContentType();
        final String typeClass;

        if (type == null) {
            // Try to get the default content type
            typeClass = getComponentSelectionModel().getSelectedKey(state);
            if (typeClass == null || typeClass.isEmpty()) {
                throw new UncheckedWrapperException("Content type is missing");
            }
        } else {
            typeClass = type.getContentItemClass();
        }

        // Return the selected wizard
        return getComponent(typeClass);
    }

    // Choose the right wizard and run it
    @Override
    public void generateXML(final PageState state, final Element parent) {

        final Component component = getCurrentWizard(state);

        if (component == null) {
            throw new UncheckedWrapperException("No Wizard.");
        }

        component.generateXML(state, parent);
    }

    /**
     * Reset the state of the current wizard
     */
    public void reset(final PageState state) {
        final Resettable resettable = (Resettable) getCurrentWizard(state);
        if (resettable != null) {
            resettable.reset(state);
        }
    }

}
