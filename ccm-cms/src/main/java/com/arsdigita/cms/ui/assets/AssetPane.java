/*
 * Copyright (C) 2017 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package com.arsdigita.cms.ui.assets;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.cms.CMS;
import com.arsdigita.cms.ui.BaseTree;
import com.arsdigita.cms.ui.folder.FolderCreateForm;
import com.arsdigita.cms.ui.folder.FolderEditorForm;
import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.folder.FolderTreeModelBuilder;
import com.arsdigita.cms.ui.folder.FolderTreeModelController;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.toolbox.ui.LayoutPanel;

import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import java.util.List;
import org.arsdigita.cms.CMSConfig;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetPane extends LayoutPanel implements Resettable {

    public static final String SET_FOLDER = "set_folder";

    private final BaseTree tree;
    private final SingleSelectionModel selectionModel;
    private final FolderSelectionModel folderSelectionModel;
    private final FolderRequestLocal folderRequestLocal;

    private AssetFolderBrowser folderBrowser;

    private SegmentedPanel.Segment browseSegment;
    private SegmentedPanel.Segment currentFolderSegment;
    private SegmentedPanel.Segment actionsSegment;
    private SegmentedPanel.Segment newFolderSegment;
    private SegmentedPanel.Segment editFolderSegment;

    @SuppressWarnings("unchecked")
    public AssetPane() {
        tree = new BaseTree(new FolderTreeModelBuilder() {

            @Override
            protected Folder getRootFolder(final PageState state) {
                final ContentSection section = CMS
                        .getContext()
                        .getContentSection();
                return section.getRootAssetsFolder();
            }

        });
        selectionModel = tree.getSelectionModel();
        folderSelectionModel = new FolderSelectionModel(selectionModel) {

            @Override
            protected Long getRootFolderID(final PageState state) {
                final ContentSection section = CMS
                        .getContext()
                        .getContentSection();
                return section.getRootAssetsFolder().getObjectId();
            }

        };
        folderRequestLocal = new FolderRequestLocal(folderSelectionModel);

        final SegmentedPanel left = new SegmentedPanel();
        setLeft(left);

        final Label heading = new Label(
                new GlobalizedMessage("cms.ui.folder_browser",
                                      CmsConstants.CMS_BUNDLE));
        left.addSegment(heading, tree);

//        final Text placeholder = new Text("Placeholder");
        setBody(createBrowserPane());

    }

    private SimpleContainer createBrowserPane() {

        final SegmentedPanel panel = new SegmentedPanel();

        browseSegment = panel.addSegment();
        folderBrowser = new AssetFolderBrowser(folderSelectionModel);
        final Paginator paginator = new Paginator(
                new AssetFolderBrowserPaginationModelBuilder(folderBrowser),
                CMSConfig.getConfig().getFolderBrowseListSize());
        folderBrowser.setPaginator(paginator);
        browseSegment.add(paginator);
        browseSegment.add(folderBrowser);

        currentFolderSegment = panel.addSegment();
        currentFolderSegment.addHeader(new Text("Current folder"));
        final Label currentFolderLabel = new Label();
        currentFolderLabel.addPrintListener(new PrintListener() {

            @Override
            public void prepare(final PrintEvent event) {
                final PageState state = event.getPageState();
                final Label target = (Label) event.getTarget();

                final long selectedId = Long.parseLong(selectionModel
                        .getSelectedKey(state).toString());
                final long currentFolderId = folderSelectionModel
                        .getSelectedObject(state).getObjectId();
                target.setLabel(String.format(
                        "selectedId = %d; currentFolderId = %d",
                        selectedId,
                        currentFolderId));
            }

        });
        currentFolderSegment.add(currentFolderLabel);

        actionsSegment = panel.addSegment();
        actionsSegment.setIdAttr("folder-browse");

        final ActionGroup actions = new ActionGroup();
        actionsSegment.add(actions);

        final FolderCreateForm folderCreateForm = new FolderCreateForm(
                "fcreat", folderSelectionModel);
        folderCreateForm.addSubmissionListener(new FormSubmissionListener() {

            @Override
            public void submitted(final FormSectionEvent event)
                    throws FormProcessException {

                final PageState state = event.getPageState();
                if (event.getSource() == folderCreateForm
                            && folderCreateForm.isCancelled(state)) {
                    browseMode(state);
                    throw new FormProcessException(new GlobalizedMessage(
                            "cms.ui.cancelled", CmsConstants.CMS_BUNDLE));

                }
            }

        });
        folderCreateForm.addProcessListener(new FormProcessListener() {

            @Override
            public void process(final FormSectionEvent event)
                    throws FormProcessException {

                final PageState state = event.getPageState();
                final Object source = event.getSource();
                if (source == folderCreateForm) {
                    browseMode(state);
                }
            }

        });
        newFolderSegment = panel.addSegment(
                new Label(new GlobalizedMessage("cms.ui.new_folder",
                                                CmsConstants.CMS_BUNDLE)),
                folderCreateForm);

        final FolderEditorForm folderEditorForm = new FolderEditorForm(
                "fedit", folderSelectionModel);
        folderEditorForm.addSubmissionListener(new FormSubmissionListener() {

            @Override
            public void submitted(final FormSectionEvent event)
                    throws FormProcessException {

                final PageState state = event.getPageState();
                if (event.getSource() == folderEditorForm
                            && folderEditorForm.isCancelled(state)) {
                    browseMode(state);
                    throw new FormProcessException(new GlobalizedMessage(
                            "cms.ui.cancelled", CmsConstants.CMS_BUNDLE));
                }
            }

        });
        folderEditorForm.addProcessListener(new FormProcessListener() {

            @Override
            public void process(final FormSectionEvent event)
                    throws FormProcessException {

                final PageState state = event.getPageState();
                final Object source = event.getSource();
                if (source == folderEditorForm) {
                    browseMode(state);
                }
            }

        });
        editFolderSegment = panel.addSegment(
                new Label(new GlobalizedMessage("cms.ui.edit_folder",
                                                CmsConstants.CMS_BUNDLE)),
                folderEditorForm);

        final ActionLink createFolderAction = new ActionLink(
                new Label(new GlobalizedMessage("cms.ui.new_folder",
                                                CmsConstants.CMS_BUNDLE)));
        createFolderAction.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();
                final Object source = event.getSource();
                if (source == createFolderAction) {
                    newFolderMode(state);
                }
            }

        });
        actions.addAction(createFolderAction);

        final ActionLink editFolderAction = new ActionLink(
                new Label(new GlobalizedMessage("cms.ui.edit_folder",
                                                CmsConstants.CMS_BUNDLE)));
        editFolderAction.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();
                final Object source = event.getSource();
                if (source == editFolderAction) {
                    editFolderMode(state);
                }
            }
        });
        actions.addAction(editFolderAction);

        return panel;

    }

    protected void browseMode(final PageState state) {
        browseSegment.setVisible(state, true);
        actionsSegment.setVisible(state, true);
        newFolderSegment.setVisible(state, false);
        editFolderSegment.setVisible(state, false);

    }

    protected void newFolderMode(final PageState state) {
        browseSegment.setVisible(state, false);
        actionsSegment.setVisible(state, false);
        newFolderSegment.setVisible(state, true);
        editFolderSegment.setVisible(state, false);
    }

    protected void editFolderMode(final PageState state) {
        browseSegment.setVisible(state, false);
        actionsSegment.setVisible(state, false);
        newFolderSegment.setVisible(state, false);
        editFolderSegment.setVisible(state, true);
    }

    @Override
    public void register(final Page page) {

        super.register(page);

        page.addActionListener(new TreeListener());
        page.addActionListener(new FolderListener());

        page.setVisibleDefault(browseSegment, true);
        page.setVisibleDefault(actionsSegment, true);
        page.setVisibleDefault(newFolderSegment, false);
        page.setVisibleDefault(editFolderSegment, false);
    }

    @Override
    public void reset(final PageState state) {

        super.reset(state);
        
        folderBrowser.getPaginator().reset(state);

    }

    private final class FolderListener implements ActionListener {

        @Override
        @SuppressWarnings("unchecked")
        public void actionPerformed(final ActionEvent event) {

            final PageState state = event.getPageState();

            if (!selectionModel.isSelected(state)) {
                final String folder = state
                        .getRequest()
                        .getParameter(SET_FOLDER);

                if (folder == null) {
                    final Category root = CMS
                            .getContext()
                            .getContentSection()
                            .getRootAssetsFolder();
                    final Long folderId = root.getObjectId();

                    selectionModel.setSelectedKey(state, folderId);
                } else {
                    selectionModel.setSelectedKey(state, Long.parseLong(folder));
                }
            }
        }

    }

    private final class TreeListener implements ActionListener {

        @Override
        public void actionPerformed(final ActionEvent event) {

            final PageState state = event.getPageState();

            final Category root = CMS
                    .getContext()
                    .getContentSection()
                    .getRootAssetsFolder();

            if (!root.equals(folderRequestLocal.getFolder(state))) {
                // Expand the ancestor nodes of the currently
                // selected node.
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final FolderTreeModelController controller = cdiUtil.findBean(
                        FolderTreeModelController.class);
                final List<Long> ancestorIds = controller.findAncestorIds(
                        folderRequestLocal.getFolder(state));
                ancestorIds.forEach(id -> tree.expand(id.toString(), state));

            }
        }

    }

}
