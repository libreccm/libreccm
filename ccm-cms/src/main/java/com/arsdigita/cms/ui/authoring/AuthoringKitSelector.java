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

import com.arsdigita.bebop.MapComponentSelectionModel;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;

import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.xml.Element;

import org.apache.logging.log4j.LogManager;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contenttypes.AuthoringKitInfo;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.util.List;
import java.util.Objects;

/**
 * Selects a component based on content type. Helper class for {@link
 * com.arsdigita.cms.ui.authoring.WizardSelector}.
 *
 * @author unknown
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public abstract class AuthoringKitSelector extends SimpleContainer {

    private static final Logger LOGGER = LogManager.getLogger(
        AuthoringKitSelector.class);
    private final Map<String, Component> components;
    private final MapComponentSelectionModel<String> selectionModel;
    private final List<ContentTypeInfo> types;

    /**
     * Construct a new AuthoringKitSelector. Load all the possible authoring
     * kits from the database and construct components for them.
     *
     * @param model the {@link ItemSelectionModel} which will supply the
     *              selector with the id of a content type
     *
     * @pre itemModel != null
     */
    public AuthoringKitSelector(final SingleSelectionModel<String> model) {
        super();

        components = new HashMap<>();
        selectionModel = new MapComponentSelectionModel<>(model, components);

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentTypesManager typesManager = cdiUtil.findBean(
            ContentTypesManager.class);
        types = typesManager.getAvailableContentTypes();

        if (types.isEmpty()) {
            throw new RuntimeException("No Content Types were found.");
        }
    }

    // Overloaded add methods
    @Override
    public void add(final Component component) {
        throw new UnsupportedOperationException();
    }

    // Overloaded add methods
    @Override
    public void add(final Component component, final int constraints) {
        throw new UnsupportedOperationException();
    }

    /**
     * Instantiate all the authoring kit wizards. The child class should call
     * this method after it is done with initialisation.
     */
    protected void processKit() {
        for (final ContentTypeInfo type : types) {
            final AuthoringKitInfo kit = type.getAuthoringKit();
            if (kit != null) {
                final Component component = instantiateKitComponent(kit, type);
                if (component != null) {
                    super.add(component);
                    components.put(type.getContentItemClass().getName(),
                                   component);
                    LOGGER.info("Added component {} for {}",
                                Objects.toString(component),
                                type.getContentItemClass().getName());
                }
            }
        }
    }

    /**
     * Instantiate an authoring kit component. Child classes should override
     * this to do the right thing. It is permissible for this method to return
     * null.
     *
     * @param kit  for this kit
     * @param type for this type
     *
     * @return
     */
    protected abstract Component instantiateKitComponent(
        final AuthoringKitInfo kit, final ContentTypeInfo type);

    /**
     *
     * @param typeClass
     *
     * @return The component the given type id
     */
    public Component getComponent(final String typeClass) {
        return components.get(typeClass);
    }

    /**
     * @return The selection model used by this wizard
     */
    public MapComponentSelectionModel<String> getComponentSelectionModel() {
        return selectionModel;
    }

    // Choose the right component and run it
    @Override
    public void generateXML(final PageState state, final Element parent) {
        if (isVisible(state)) {
            final Component component = selectionModel.getComponent(state);
            if (component == null) {
                throw new IllegalStateException("No component for "
                                                    + selectionModel
                        .getSelectedKey(state));
            }
            component.generateXML(state, parent);
        }
    }

}
