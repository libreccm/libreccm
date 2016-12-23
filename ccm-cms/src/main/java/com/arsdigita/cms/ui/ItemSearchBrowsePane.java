/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.TreeExpansionEvent;
import com.arsdigita.bebop.event.TreeExpansionListener;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.LongParameter;

import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.folder.FolderTreeModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;

import org.apache.logging.log4j.LogManager;

import org.apache.logging.log4j.Logger;
import org.arsdigita.cms.CMSConfig;
import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSectionRepository;

import java.util.List;

/**
 * A pane that contains a folder tree on the left and a folder manipulator on
 * the right.
 *
 * @author David LutterKort &lt;dlutter@redhat.com&gt;
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ItemSearchBrowsePane extends SimpleContainer implements Resettable,
                                                                     TreeExpansionListener,
                                                                     ChangeListener,
                                                                     FormProcessListener,
                                                                     FormSubmissionListener {

    private static final String CONTENT_TYPE_ID = "ct";
    private static final Logger LOGGER = LogManager.getLogger(
        ItemSearchBrowsePane.class);
    private final FolderSelectionModel folderSelectionModel;
    private final FolderRequestLocal folderRequestLocal;
    private final Tree tree;
    private ItemSearchFolderBrowser folderBrowser;
    private SingleSelect sectionSelect;
    private SingleSelectionModel typeSelectionModel;

    public ItemSearchBrowsePane() {

        final LayoutPanel mainPanel = new LayoutPanel();

        setClassAttr("sidebarNavPanel");
        setAttribute("navbar-title",
                     new GlobalizedMessage("cms.ui.folder_browser",
                                           CmsConstants.CMS_BUNDLE)
                         .localize().toString());

        final BoxPanel left = new BoxPanel(BoxPanel.VERTICAL);

        final Label label = new Label(new GlobalizedMessage(
            "cms.ui.folder_browser", CmsConstants.CMS_BUNDLE));
        label.setClassAttr("heading");
        left.add(label);

        // As described in ticket 20540, some clients do not want the option to pick items from other 
        // subsites through the ItemSearchBrowsePane.  A new parameter has been added to allow the 
        // administrator to pick between the old and new versions.
        boolean linksOnlyInSameSubsite = CMSConfig.getConfig()
            .isLinksOnlyInSameSubsite();
        LOGGER.debug("linksOnlyInSameSubsite value is {}",
                     linksOnlyInSameSubsite);

        tree = new Tree(new FolderTreeModelBuilder() {

            @Override
            protected Category getRootFolder(final PageState state) {
                final Category root = ItemSearchBrowsePane.this.getRootFolder(
                    state);

                if (null == root) {
                    return super.getRootFolder(state);
                }
                return root;
            }

        });
        folderSelectionModel = createFolderSelectionModel();
        folderSelectionModel.addChangeListener(this);
        folderRequestLocal = new FolderRequestLocal(folderSelectionModel);

        if (!linksOnlyInSameSubsite) {
            // The client should be able to pick between the subsites
            Form sectionForm = getSectionForm();
            add(sectionForm);
        }

        tree.setSelectionModel(folderSelectionModel);

        tree.setClassAttr("navbar");
        tree.addTreeExpansionListener(this);
        left.add(tree);

        left.setClassAttr("main");

        final BoxPanel body = new BoxPanel(BoxPanel.VERTICAL);
        folderBrowser = new ItemSearchFolderBrowser(folderSelectionModel);
        body.add(folderBrowser);
        body.add(folderBrowser.getPaginator());

        mainPanel.setLeft(left);
        mainPanel.setBody(body);
        add(mainPanel);
    }

    @Override
    public boolean isVisible(final PageState state) {
        // Always expand root node
        if (tree.isCollapsed(Long.toString(getRootFolder(state).getObjectId()),
                             state)) {
            tree.expand(Long.toString(getRootFolder(state).getObjectId()),
                        state);
        }

        return super.isVisible(state);
    }

    private Form getSectionForm() {
        final Form sectionForm = new Form("isfbSectionForm",
                                          new BoxPanel(BoxPanel.HORIZONTAL));
        sectionForm.setClassAttr("navbar");

        sectionSelect = new SingleSelect(new LongParameter("isfbSection"));
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final ContentSectionRepository sectionRepo = cdiUtil.findBean(
            ContentSectionRepository.class);
        final List<ContentSection> sections = sectionRepo.findAll();

        sections.forEach(section -> sectionSelect.addOption(
            new Option(Long.toString(section.getObjectId()),
                       section.getDisplayName())));

        sectionForm.addInitListener(new FormInitListener() {

            @Override
            public void init(final FormSectionEvent event) {
                final PageState state = event.getPageState();

                if (null == sectionSelect.getValue(state)) {
                    ContentSection section = CMS.getContext().
                        getContentSection();
                    sectionSelect.setValue(state, section.getObjectId());
                }
            }

        });

        sectionForm.add(sectionSelect);
        sectionForm.add(new Submit("Change Section"));

        return sectionForm;
    }

    private Folder getRootFolder(final PageState state) {
        LOGGER.debug("Getting the root folder.");
        if (sectionSelect != null) {
            // We have more than one subsite to choose between
            final Long sectionId = (Long) sectionSelect.getValue(state);
            if (LOGGER.isDebugEnabled()) {
                if (null == sectionId) {
                    LOGGER.debug("Using default section");
                } else {
                    LOGGER.debug("Using section " + sectionId.toString());
                }
            }

            if (null == sectionId) {
                return null;
            }

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final ContentSectionRepository sectionRepo = cdiUtil.findBean(
                ContentSectionRepository.class);
            final ContentSection section = sectionRepo.findById(sectionId);

            return section.getRootDocumentsFolder();
        } else {
            return null;
        }
    }

    @Override
    public void register(final Page page) {
        super.register(page);
        page.addComponentStateParam(this, folderSelectionModel
                                    .getStateParameter());

        // Only add the SingleSelect item if it exists
        if (sectionSelect != null) {
            page.addComponentStateParam(this, sectionSelect.getParameterModel());
        }

        // Save the state of the new item component
//        p.addComponentStateParam(this, m_typeSel.getStateParameter());
        page.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                if (state.isVisibleOnPage(ItemSearchBrowsePane.this)) {
                    showHideSegments(state);
                }
            }

        });
    }

    /**
     * Show/hide segments based on access checks.
     *
     * @param state The page state
     */
    private void showHideSegments(final PageState state) {
        //Empty
    }

    @Override
    public void reset(final PageState state) {
        //Empty
    }

    public ItemSearchFolderBrowser getFolderBrowser() {
        return folderBrowser;
    }

    public final FolderSelectionModel getFolderSelectionModel() {
        return folderSelectionModel;
    }

    /**
     * sets the current level of expansion of the folder tree and in the folder
     * browser table
     *
     * @param state
     * @param key
     */
    protected void setSelectedFolder(final PageState state,
                                     final String key) {

        //set the selected folder of the folder browser
        folderBrowser.getFolderSelectionModel().setSelectedKey(
            state, Long.parseLong(key));

        //set the selected folder of the folder tree
        folderSelectionModel.setSelectedKey(state, Long.parseLong(key));
        final Folder current = (Folder) folderSelectionModel.getSelectedObject(
            state);
        final Folder parent = current.getParentFolder();
        if (parent != null) {
            final long parentId = parent.getObjectId();
            tree.expand(Long.toString(parentId), state);
        }
    }

    // Implement TreeExpansionListener
    @Override
    public void treeCollapsed(final TreeExpansionEvent event) {
        final PageState state = event.getPageState();
        folderSelectionModel.setSelectedKey(
            state, Long.parseLong((String) event.getNodeKey()));
    }

    @Override
    public void treeExpanded(final TreeExpansionEvent event) {
        // Empty
    }

    @Override
    public void stateChanged(final ChangeEvent event) {
        final PageState state = event.getPageState();
        final Folder current = (Folder) folderSelectionModel.getSelectedObject(state);
        final Folder parent =  current.getParentFolder();
        folderBrowser.getPaginator().reset(state);
        if (parent != null) {
            final Long parentId = parent.getObjectId();
            tree.expand(parentId.toString(), state);
        }
    }

    @Override
    public void process(final FormSectionEvent event) {
        final PageState state = event.getPageState();
        browseMode(state);
    }

    @Override
    public void submitted(final FormSectionEvent event) {
        //Nothing
    }

    private void browseMode(final PageState state) {
        typeSelectionModel.clearSelection(state);
    }

    private void newItemMode(final PageState state) {
        //Nothing
    }

    private FolderSelectionModel createFolderSelectionModel() {
        return new FolderSelectionModel("folder") {

            @Override
            protected Long getRootFolderID(final PageState state) {
                final Folder root = getRootFolder(state);

                if (null == root) {
                    return super.getRootFolderID(state);
                }
                return root.getObjectId();
            }

        };
    }

}
