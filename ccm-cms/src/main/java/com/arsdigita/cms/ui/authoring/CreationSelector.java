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
import com.arsdigita.bebop.FormSection;
import com.arsdigita.bebop.MetaForm;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.cms.ItemSelectionModel;
import com.arsdigita.cms.ui.ContentItemPage;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.util.UncheckedWrapperException;
import com.arsdigita.web.RedirectSignal;
import com.arsdigita.web.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.ContentType;
import org.librecms.contentsection.ContentTypeManager;
import org.librecms.contentsection.ContentTypeRepository;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contenttypes.AuthoringKitInfo;
import org.librecms.contenttypes.ContentTypeInfo;
import org.librecms.contenttypes.ContentTypesManager;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Optional;

/**
 * An invisible component which contains all the possible creation components.
 * The components are loaded from the database at construction time. The
 * selector uses a {@link SingleSelectionModel} in order to get the ID of the
 * current content type.
 *
 * <strong>Important:</strong> This component is passed in the constructor to
 * every authoring kit creation component (such as {@link PageCreate}). The
 * creation component is supposed to follow the following pattern:
 *
 * <blockquote><pre>
 *   // The member variable m_parent points to the CreationSelector
 *   SomeContentItem item = somehowCreateTheItem(state);
 *   item.setParent(m_parent.getFolder(state));
 *   m_parent.editItem(state, item);
 * </pre></blockquote>
 *
 * If the creation component wishes to cancel the creation process, it should
 * call
 *
 * <blockquote><pre>m_parent.redirectBack(state);</pre></blockquote>
 *
 * The component may also call
 *
 * <blockquote><pre>m_parent.getContentSection(state);</pre></blockquote>
 *
 * in order to get the current content section.
 *
 * @author unknown
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class CreationSelector extends MetaForm {

    private static final Logger LOGGER = LogManager.getLogger(
        CreationSelector.class);

    private final FolderSelectionModel folderSelectionModel;
    private final SingleSelectionModel<Long> typeSelectionModel;
    private final StringParameter selectedLanguageParam;

    private static final Class[] ARGUMENTS = new Class[]{
        ItemSelectionModel.class,
        CreationSelector.class,
        StringParameter.class
    };

    private Object[] values;

    private final ItemSelectionModel itemSelectionModel;
    private final LongParameter itemIdParameter;

    public static final String ITEM_ID = "iid";

    /**
     * Constructs a new <code>CreationSelector</code>. Load all the possible
     * creation components from the database and stick them in the Map.
     *
     * @param typeSelectionModel   the {@link SingleSelectionModel} which will
     *                             supply a BigDecimal ID of the content type to
     *                             instantiate
     *
     * @param folderSelectionModel the {@link FolderSelectionModel} containing
     *                             the folder in which new items are to be
     *                             created
     * @param selectedLanguageParam
     */
    public CreationSelector(final SingleSelectionModel<Long> typeSelectionModel,
                            final FolderSelectionModel folderSelectionModel,
                            final StringParameter selectedLanguageParam) {

        super("pageCreate");

        this.typeSelectionModel = typeSelectionModel;
        this.folderSelectionModel = folderSelectionModel;
        this.selectedLanguageParam = selectedLanguageParam;

        itemIdParameter = new LongParameter(ITEM_ID);
        itemSelectionModel = new ItemSelectionModel(itemIdParameter);
    }

    /**
     *
     * @param state
     *
     * @return
     */
    @Override
    public Form buildForm(final PageState state) {
        final Long typeId = typeSelectionModel.getSelectedKey(state);
        final Component component;
        final Form returnForm = new Form("pageCreate");
        final FormErrorDisplay formErrorDisplay = new FormErrorDisplay(this);
        formErrorDisplay.setStateParamsAreRegistered(false);
        returnForm.add(formErrorDisplay, ColumnPanel.FULL_WIDTH
                                             | ColumnPanel.LEFT);

        if (typeId != null) {
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentTypeRepository typeRepo = cdiUtil.findBean(
                ContentTypeRepository.class);
            final ContentTypeManager typeManager = cdiUtil.findBean(
                ContentTypeManager.class);
            final ContentTypesManager typesManager = cdiUtil.findBean(
                ContentTypesManager.class);

            final Optional<ContentType> type = typeRepo.findById(typeId);
            if (!type.isPresent()) {
                throw new UncheckedWrapperException(String.format(
                    "Type with id %d not found.", typeId));
            }
            final ContentTypeInfo typeInfo = typesManager
                .getContentTypeInfo(type.get());
            final AuthoringKitInfo kit = typeInfo.getAuthoringKit();
            component = instantiateKitComponent(kit, type.get());
            if (component != null) {
                returnForm.add(component);
                returnForm.setMethod(Form.POST);
                returnForm.setEncType("multipart/form-data");
            }
        }
        return returnForm;
    }

    /**
     * Add the item_id parameter.
     *
     * @param page
     */
    @Override
    public void register(final Page page) {
        super.register(page);
        page.addComponentStateParam(this, itemIdParameter);
    }

    /**
     * Get the creation component.
     *
     * @param kit
     * @param type
     *
     * @return
     */
    protected Component instantiateKitComponent(final AuthoringKitInfo kit,
                                                final ContentType type) {
        final Class<? extends FormSection> createClass = kit
            .getCreateComponent();
        final Object[] vals;

        try {
            final ItemSelectionModel itemSelectionModel
                                         = new ItemSelectionModel(
                    type, itemIdParameter);
            vals = new Object[]{itemSelectionModel, 
                                this, 
                                selectedLanguageParam};

            final Constructor<? extends FormSection> constructor = createClass
                .getConstructor(ARGUMENTS);
            final Component component = (Component) constructor
                .newInstance(vals);
            return component;
        } catch (IllegalAccessException
                 | IllegalArgumentException
                 | InstantiationException
                 | NoSuchMethodException
                 | SecurityException
                 | InvocationTargetException ex) {
            LOGGER.error("Failed to instantiate creation component \"{}\".",
                         kit.getCreateComponent().getName());
            LOGGER.error("Exception: ", ex);
            throw new UncheckedWrapperException(String.format(
                "Failed to instantiate creation component \"%s\".",
                kit.getCreateComponent().getName()),
                                                ex);
        }
    }

    /**
     * Return the currently selected folder. Creation components will place new
     * items in this folder.
     *
     * @param state represents the current request
     *
     * @return the currently selected folder, in which new items should be
     *         placed.
     */
    public final Folder getFolder(final PageState state) {
        return folderSelectionModel.getSelectedObject(state);
    }

    /**
     * Return the currently selected content section. New items created by
     * creation components will belong to this section. This is the content
     * section to which the folder returned by {@link #getFolder getFolder}
     * belongs.
     *
     * @param state represents the current request
     *
     * @return the currently selected content section.
     */
    public final ContentSection getContentSection(final PageState state) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final CreationSelectorController controller = cdiUtil
            .findBean(CreationSelectorController.class);

        return controller.getContentSectionForFolder(getFolder(state));
    }

    /**
     * Forward to the item editing UI. The creation component of an authoring
     * kit may call this method to indicate that the creation process is
     * complete.
     *
     * @param state the page state
     * @param item  the newly created item
     */
    public void editItem(final PageState state, final ContentItem item) {
        final ContentSection section = getContentSection(state);

        final String nodeUrl = String.join("", URL.getDispatcherPath(),
                                           section.getPrimaryUrl(),
                                           "/");
        final String target = ContentItemPage.getItemURL(
            nodeUrl, item.getObjectId(), ContentItemPage.AUTHORING_TAB, true);

        throw new RedirectSignal(target, true);
    }

    /**
     * Cancel item editing and go back to where the user came from
     *
     * @param state the page state
     */
    public void redirectBack(final PageState state) {
        typeSelectionModel.clearSelection(state);
    }

}
