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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.MetaForm;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.AuthoringKit;
import com.arsdigita.cms.ContentBundle;
import com.arsdigita.cms.ContentItem;
import com.arsdigita.cms.ContentSection;
import com.arsdigita.cms.ContentType;
import com.arsdigita.cms.Folder;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.domain.DataObjectNotFoundException;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;
import org.apache.log4j.Logger;

import java.lang.reflect.Constructor;
import java.math.BigDecimal;

/**
 * An invisible component which contains all the possible creation
 * components. The components are loaded from the database at
 * construction time. The selector uses a {@link SingleSelectionModel}
 * in order to get the ID of the current content type.
 *
 * <strong>Important:</strong> This component is passed in the
 * constructor to every authoring kit creation component (such as
 * {@link PageCreate}). The creation component is supposed to follow
 * the following pattern:
 *
 * <blockquote><pre>
 *   // The member variable m_parent points to the CreationSelector
 *   SomeContentItem item = somehowCreateTheItem(state);
 *   item.setParent(m_parent.getFolder(state));
 *   m_parent.editItem(state, item);
 * </pre></blockquote>
 *
 * If the creation component wishes to cancel the creation process,
 * it should call
 *
 * <blockquote><pre>m_parent.redirectBack(state);</pre></blockquote>
 *
 * The component may also call
 *
 * <blockquote><pre>m_parent.getContentSection(state);</pre></blockquote>
 *
 * in order to get the current content section.
 *
 * @version $Id: CreationSelector.java 2185 2011-06-20 21:16:02Z pboy $
 */
public class CreationSelector extends MetaForm {

    private static Logger s_log = Logger.getLogger(CreationSelector.class);

    private final FolderSelectionModel m_folderSel;
    private final SingleSelectionModel m_typeSel;

    private static Class[] s_args = new Class[] {
        ItemSelectionModel.class,
        CreationSelector.class
    };

    private Object[] m_vals;

    private ItemSelectionModel m_itemSel;
    private BigDecimalParameter m_itemId;

    private ItemSelectionModel m_bundleSel;
    private BigDecimalParameter m_bundleId;

    public static final String ITEM_ID = "iid";
    public static final String BUNDLE_ID = "bid";

    /**
     * Constructs a new <code>CreationSelector</code>. Load all the
     * possible creation components from the database and stick them
     * in the Map.
     *
     * @param typeModel the {@link SingleSelectionModel} which will
     * supply a BigDecimal ID of the content type to instantiate
     *
     * @param folderModel the {@link FolderSelectionModel} containing
     * the folder in which new items are to be created
     */
    public CreationSelector(final SingleSelectionModel typeModel,
                            final FolderSelectionModel folderModel) {
        super("pageCreate");

        m_typeSel = typeModel;

        m_folderSel = folderModel;
        m_itemId = new BigDecimalParameter(ITEM_ID);
        m_bundleId = new BigDecimalParameter(BUNDLE_ID);
        m_bundleSel = new ItemSelectionModel(ContentBundle.class.getName(),
                                             ContentBundle.BASE_DATA_OBJECT_TYPE, m_bundleId);
    }


    /**
     * 
     * @param state
     * @return 
     */
    @Override
    public Form buildForm(PageState state) {
        BigDecimal typeID = (BigDecimal)m_typeSel.getSelectedKey(state);
        Component c = null;
        Form returnForm = new Form("pageCreate");
        FormErrorDisplay fed = new FormErrorDisplay(this);
        fed.setStateParamsAreRegistered(false);
        returnForm.add(fed , ColumnPanel.FULL_WIDTH | ColumnPanel.LEFT);
        if (typeID != null) {
            try {
                ContentType type = new ContentType(typeID);
                AuthoringKit kit = type.getAuthoringKit();
                if(kit != null) {
                    c = instantiateKitComponent(kit, type);
                    if(c != null) {
                        returnForm.add(c);
                        returnForm.setMethod(Form.POST);
                        returnForm.setEncType("multipart/form-data");
                    }
                }
            } catch (DataObjectNotFoundException e) {
                // content type not found
            }
        }
        return returnForm;
    }

    /**
     * Add the item_id parameter.
     * 
     * @param p 
     */
    @Override
    public void register(Page p) {
        super.register(p);
        p.addComponentStateParam(this, m_itemId);
        p.addComponentStateParam(this, m_bundleId);
    }

    /**
     * Get the creation component.
     * 
     * @param kit
     * @param type
     * @return 
     */
    protected Component instantiateKitComponent
             (final AuthoringKit kit, final ContentType type) {
        String creatorName = kit.getCreateComponent();
        Object[] vals;

        if(creatorName == null) {
            return null;
        }

        try {
            Class createClass = Class.forName(creatorName);
            ItemSelectionModel itemModel = new ItemSelectionModel(type, m_itemId);
            vals = new Object[]{itemModel, this};
            Constructor constr = createClass.getConstructor(s_args);
            Component c = (Component)constr.newInstance(vals);
            return c;
        } catch (Exception e) {
            s_log.error("Instantiation failure", e);
            throw new UncheckedWrapperException (
                          "Failed to instantiate creation component " +
                          kit.getCreateComponent() + ": " + e.getMessage(),
                          e);
        }
    }

    /**
     * Return the currently selected folder. Creation components will place
     * new items in this folder.
     *
     * @param s represents the current request
     * @return the currently selected folder, in which new items should be
     * placed.
     */
    public final Folder getFolder(PageState s) {
        return (Folder) m_folderSel.getSelectedObject(s);
    }

    /**
     * Return the currently selected content section. New items created by
     * creation components will belong to this section. This is the content
     * section to which the folder returned by {@link #getFolder getFolder}
     * belongs.
     *
     * @param s represents the current request
     * @return the currently selected content section.
     */
    public final ContentSection getContentSection(PageState s) {
        final ContentSection section = (ContentSection)
            getFolder(s).getContentSection();

        return section;
    }

    /**
     * Forward to the item editing UI. The creation component of an authoring
     * kit may call this method to indicate that the creation process is
     * complete.
     *
     * @param s the page state
     * @param item the newly created item
     */
    public void editItem(PageState s, ContentItem item) {
        ContentSection sec = getContentSection(s);

        String nodeURL = URL.getDispatcherPath() + sec.getPath() + "/";
        String target = ContentItemPage.getItemURL
            (nodeURL, item.getID(), ContentItemPage.AUTHORING_TAB,true);

        throw new RedirectSignal(target, true);
    }

    /**
     * Cancel item editing and go back to where the user came from
     *
     * @param s the page state
     */
    public void redirectBack(PageState state) {
        m_typeSel.clearSelection(state);
    }

}
