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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.tree.TreeCellRenderer;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.folder.FolderTreeModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.LayoutPanel;
import com.arsdigita.util.Assert;

import org.libreccm.categorization.Category;
import org.libreccm.core.CcmObject;
import org.librecms.CmsConstants;
import org.librecms.contentsection.Folder;

/**
 * A pane that contains a folder tree on the left and a folder manipulator on
 * the right. It is a part of the content section main page and is displayed as
 * the "Browse" tab.
 *
 * @author David LutterKort &lt;dlutter@redhat.com&gt;
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class BrowsePane extends LayoutPanel implements Resettable {

    private final BaseTree tree;
    private final SingleSelectionModel selectionModel;
    private final FolderSelectionModel folderModel; // To support legacy UI code
    private final FolderRequestLocal folderRequestLocal;
    private final FlatItemList flatItemList;

    public BrowsePane() {

        /* The folder tree displayed on the left side / left column           */
        tree = new BaseTree(new FolderTreeModelBuilder());
        tree.setCellRenderer(new TreeCellRenderer() {

            @Override
            public Component getComponent(final Tree tree,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final boolean isExpanded,
                                          final boolean isLeaf,
                                          final Object key) {
                if (value instanceof Folder) {
                    final Folder folder = (Folder) value;
                    if (folder.getParentCategory() == null) {
                        return new Text("/");
                    } else {
                        return new Text(folder.getName());
                    }
                } else {
                    return new Text(value.toString());
                }
            }

        });
        selectionModel = tree.getSelectionModel();
        folderModel = new FolderSelectionModel(selectionModel);
        folderRequestLocal = new FolderRequestLocal(folderModel);

        final SegmentedPanel left = new SegmentedPanel();
        setLeft(left);

        final Label heading = new Label(
            new GlobalizedMessage("cms.ui.folder_browser",
                                  CmsConstants.CMS_BUNDLE));
        left.addSegment(heading, tree);

        flatItemList = new FlatItemList(folderRequestLocal, folderModel);
        setBody(flatItemList);

        flatItemList.getManipulator().getItemView().addProcessListener(
            new ProcessListener());
        flatItemList.getManipulator().getTargetSelector().addProcessListener(
            new ProcessListener());
        flatItemList.getManipulator().getTargetSelector().addSubmissionListener(
            new SubmissionListener());
    }

    @Override
    public final void register(final Page page) {
        super.register(page);

        page.addActionListener(new FolderListener());
        page.addActionListener(new TreeListener());
    }

    @Override
    public final void reset(final PageState state) {
        super.reset(state);

        flatItemList.reset(state);
    }

    // Private classes and methods
    /**
     *
     */
    private final class ProcessListener implements FormProcessListener {

        @Override
        public final void process(final FormSectionEvent event) {
            final PageState state = event.getPageState();

            if (event.getSource() == flatItemList.getManipulator().getItemView()) {
                // Hide everything except for the flat item list
                tree.setVisible(state, false);
            } else if (event.getSource() == flatItemList.getManipulator()
                .getTargetSelector()) {
                tree.setVisible(state, true);
            }
        }

    }

    private final class SubmissionListener implements FormSubmissionListener {

        @Override
        public final void submitted(final FormSectionEvent event) {
            final PageState state = event.getPageState();

            if (event.getSource() == flatItemList.getManipulator()
                .getTargetSelector()) {
                if (!flatItemList.getManipulator().getTargetSelector()
                    .isVisible(state)) {
                    tree.setVisible(state, true);
                }
            }
        }

    }

    private final class FolderListener implements ActionListener {

        @Override
        public final void actionPerformed(final ActionEvent event) {
            final PageState state = event.getPageState();

            if (!selectionModel.isSelected(state)) {
                final String folder = state.getRequest().getParameter(
                    ContentSectionPage.SET_FOLDER);

                if (folder == null) {
                    final Category root = CMS.getContext().getContentSection()
                        .getRootDocumentsFolder();
                    final Long folderID = root.getObjectId();

                    /*
                    ToDo
                    User user = Web.getWebContext().getUser();
                    if (user != null) {
                        Folder homeFolder = Folder.getUserHomeFolder(
                            user, CMS.getContext().getContentSection());
                        if (homeFolder != null) {
                            folderID = homeFolder.getID();
                        }

                    }*/
                    selectionModel.setSelectedKey(state, folderID);
                } else {
                    selectionModel.setSelectedKey(state, folder);
                }
            }
        }

    }

    private final class TreeListener implements ActionListener {

        @Override
        public final void actionPerformed(final ActionEvent event) {
            final PageState state = event.getPageState();

            if (Assert.isEnabled()) {
                Assert.isTrue(selectionModel.isSelected(state));
            }

            final Category root = CMS.getContext().getContentSection()
                .getRootDocumentsFolder();

            if (!root.equals(folderRequestLocal.getFolder(state))) {
                // Expand the ancestor nodes of the currently
                // selected node.

                CcmObject object = folderRequestLocal.getFolder(state);

                while (object != null) {
                    if (object instanceof Category) {
                        final Category category = (Category) object;

                        if (category.getParentCategory() != null) {
                            final Category result = category.getParentCategory();
                            object = result;
                            tree.expand(
                                ((Long) object.getObjectId()).toString(),
                                state);
                        } else {
                            object = null;
                        }

                    } else {
                        object = null;
                    }
                }
            }
        }

    }

}
