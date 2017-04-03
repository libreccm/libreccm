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
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.tree.TreeCellRenderer;
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.libreccm.categorization.Category;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;

import java.util.List;

import org.arsdigita.cms.CMSConfig;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.PermissionChecker;
import org.librecms.contentsection.Asset;
import org.librecms.contentsection.AssetManager;
import org.librecms.contentsection.AssetRepository;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.Arrays;
import java.util.Objects;

import static org.librecms.CmsConstants.*;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class AssetPane extends LayoutPanel implements Resettable {

    private static final Logger LOGGER = LogManager.getLogger(AssetPane.class);

    public static final String SET_FOLDER = "set_folder";
    private static final String SOURCES_PARAM = "sources";
    private static final String ACTION_PARAM = "action";
    private static final String MOVE = "Move";
    private static final String COPY = "Copy";

    private final BaseTree tree;
    private final SingleSelectionModel selectionModel;
    private final FolderSelectionModel folderSelectionModel;
    private final FolderRequestLocal folderRequestLocal;
    private final ArrayParameter sourcesParameter = new ArrayParameter(
        new StringParameter(SOURCES_PARAM));
    private final StringParameter actionParameter = new StringParameter(
        ACTION_PARAM);

    private AssetFolderBrowser folderBrowser;
    private Form browserForm;
    private SingleSelect actionSelect;
    private Submit actionSubmit;
    private TargetSelector targetSelector;

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
        browserForm = new Form("assetFolderBrowser",
                               new SimpleContainer());
        browserForm.setMethod(Form.GET);
        folderBrowser = new AssetFolderBrowser(folderSelectionModel);
        final Paginator paginator = new Paginator(
            new AssetFolderBrowserPaginationModelBuilder(folderBrowser),
            CMSConfig.getConfig().getFolderBrowseListSize());
        folderBrowser.setPaginator(paginator);

        final CheckboxGroup checkboxGroup = new CheckboxGroup(sourcesParameter);
        browserForm.add(checkboxGroup);
        final TableColumn checkboxCol = new TableColumn();
        checkboxCol.setHeaderValue(
            new GlobalizedMessage("empty_text", CmsConstants.CMS_BUNDLE));
        checkboxCol.setCellRenderer(new TableCellRenderer() {

            @Override
            public Component getComponent(final Table table,
                                          final PageState state,
                                          final Object value,
                                          final boolean isSelected,
                                          final Object key,
                                          final int row,
                                          final int column) {

                final Option result = new Option(key.toString(),
                                                 new Text(""));
                result.setGroup(checkboxGroup);
                return result;
            }

        });
        folderBrowser.getColumnModel().add(0, checkboxCol);

        browserForm.add(paginator);
        browserForm.add(folderBrowser);
        final SimpleContainer actionFormContainer = new SimpleContainer();
        actionFormContainer.add(new Label(
            new GlobalizedMessage(
                "cms.ui.folder.edit_selection",
                CmsConstants.CMS_FOLDER_BUNDLE)));
        actionSelect = new SingleSelect(actionParameter);
        actionSelect.addOption(
            new Option(COPY,
                       new Label(new GlobalizedMessage(
                           "cms.ui.folder.copy.action",
                           CmsConstants.CMS_FOLDER_BUNDLE))));
        actionSelect.addOption(
            new Option(MOVE,
                       new Label(new GlobalizedMessage(
                           "cms.ui.folder.move.action",
                           CmsConstants.CMS_FOLDER_BUNDLE))));
        actionFormContainer.add(actionSelect);
        actionSubmit = new Submit(
            "Go",
            new GlobalizedMessage("cms.ui.folder.go",
                                  CmsConstants.CMS_FOLDER_BUNDLE));
        actionFormContainer.add(actionSubmit);
        browserForm.addProcessListener(new FormProcessListener() {

            @Override
            public void process(final FormSectionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();

                moveCopyMode(state);

            }

        });
        browserForm.add(actionFormContainer);

        targetSelector = new TargetSelector();
        targetSelector.addProcessListener(new FormProcessListener() {

            @Override
            public void process(final FormSectionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();

                browseMode(state);
                targetSelector.setVisible(state, false);

                final Folder folder = targetSelector.getTarget(state);
                final String[] objectIds = getSources(state);

                if (isCopy(state)) {
                    copyObjects(folder, objectIds);
                } else if (isMove(state)) {
                    moveObjects(folder, objectIds);
                }

                reset(state);
            }

        });
        targetSelector.addValidationListener(
            new TargetSelectorValidationListener());
        targetSelector.addSubmissionListener(new FormSubmissionListener() {

            @Override
            public void submitted(final FormSectionEvent event)
                throws FormProcessException {

                final PageState state = event.getPageState();

                if (targetSelector.isCancelled(state)) {
                    reset(state);
                    browseMode(state);
                    throw new FormProcessException(new GlobalizedMessage(
                        "cms.ui.folder.cancelled",
                        CmsConstants.CMS_FOLDER_BUNDLE));
                }
            }

        });
        browseSegment.add(targetSelector);

//        browseSegment.add(paginator);
//        browseSegment.add(folderBrowser);
        browseSegment.add(browserForm);

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
        tree.setVisible(state, true);
        browseSegment.setVisible(state, true);
        folderBrowser.setVisible(state, true);
        browserForm.setVisible(state, true);
        targetSelector.setVisible(state, false);
        actionsSegment.setVisible(state, true);
        newFolderSegment.setVisible(state, false);
        editFolderSegment.setVisible(state, false);

    }

    protected void moveCopyMode(final PageState state) {
        tree.setVisible(state, false);
        browseSegment.setVisible(state, true);
        folderBrowser.setVisible(state, false);
        browserForm.setVisible(state, false);
        targetSelector.setVisible(state, true);
        actionsSegment.setVisible(state, false);
        newFolderSegment.setVisible(state, false);
        editFolderSegment.setVisible(state, false);
        targetSelector.expose(state);
    }

    protected void newFolderMode(final PageState state) {
        tree.setVisible(state, false);
        browseSegment.setVisible(state, false);
        folderBrowser.setVisible(state, false);
        browserForm.setVisible(state, false);
        targetSelector.setVisible(state, false);
        actionsSegment.setVisible(state, false);
        newFolderSegment.setVisible(state, true);
        editFolderSegment.setVisible(state, false);
    }

    protected void editFolderMode(final PageState state) {
        tree.setVisible(state, false);
        browseSegment.setVisible(state, false);
        targetSelector.setVisible(state, false);
        actionsSegment.setVisible(state, false);
        newFolderSegment.setVisible(state, false);
        editFolderSegment.setVisible(state, true);
    }

    @Override
    public void register(final Page page) {

        super.register(page);

        page.addActionListener(new TreeListener());
        page.addActionListener(new FolderListener());

        page.setVisibleDefault(tree, true);
        page.setVisibleDefault(browseSegment, true);
        page.setVisibleDefault(folderBrowser, true);
        page.setVisibleDefault(browserForm, true);
        page.setVisibleDefault(targetSelector, false);
        page.setVisibleDefault(actionsSegment, true);
        page.setVisibleDefault(newFolderSegment, false);
        page.setVisibleDefault(editFolderSegment, false);

        page.addComponentStateParam(this, actionParameter);
        page.addComponentStateParam(this, sourcesParameter);
    }

    @Override
    public void reset(final PageState state) {

        super.reset(state);

        folderBrowser.getPaginator().reset(state);

        state.setValue(actionParameter, null);
        state.setValue(sourcesParameter, null);

    }

    private String[] getSources(final PageState state) {

        final String[] result = (String[]) state.getValue(sourcesParameter);

        if (result == null) {
            return new String[0];
        } else {
            return result;
        }
    }

    protected final boolean isMove(final PageState state) {
        return MOVE.equals(getAction(state));
    }

    protected final boolean isCopy(final PageState state) {
        return COPY.equals(getAction(state));
    }

    private String getAction(final PageState state) {
        return (String) state.getValue(actionParameter);
    }

    protected void moveObjects(final Folder target, final String[] objectIds) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final AssetFolderBrowserController controller = cdiUtil.findBean(
            AssetFolderBrowserController.class);

        controller.moveObjects(target, objectIds);
    }

    protected void copyObjects(final Folder target, final String[] objectIds) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final AssetFolderBrowserController controller = cdiUtil.findBean(
            AssetFolderBrowserController.class);

        controller.copyObjects(target, objectIds);
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

    private class TargetSelector extends Form implements Resettable {

        private final FolderSelectionModel targetFolderModel;
        private final AssetFolderTree folderTree;
        private final Submit cancelButton;

        public TargetSelector() {
            super("targetSelector", new BoxPanel());
            setMethod(GET);
            targetFolderModel = new FolderSelectionModel("target") {

                @Override
                protected Long getRootFolderID(final PageState state) {
                    final ContentSection section = CMS
                        .getContext()
                        .getContentSection();
                    return section.getRootAssetsFolder().getObjectId();
                }

            };
            folderTree = new AssetFolderTree(targetFolderModel);

            folderTree.setCellRenderer(new FolderTreeCellRenderer());

            final Label label = new Label(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {

                    final PageState state = event.getPageState();
                    final Label label = (Label) event.getTarget();
                    final int numberOfItems = getSources(state).length;
                    final Category folder = (Category) folderSelectionModel
                        .getSelectedObject(state);
                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final CategoryManager categoryManager = cdiUtil
                        .findBean(CategoryManager.class);

                    if (isMove(state)) {
                        label.setLabel(new GlobalizedMessage(
                            "cms.ui.folder.move",
                            CmsConstants.CMS_FOLDER_BUNDLE,
                            new Object[]{numberOfItems,
                                         categoryManager.getCategoryPath(folder)}));
                    } else if (isCopy(state)) {
                        label.setLabel(new GlobalizedMessage(
                            "cms.ui.folder.copy",
                            CMS_BUNDLE,
                            new Object[]{numberOfItems,
                                         categoryManager.getCategoryPath(
                                             folder)}));
                    }
                }

            });

            label.setOutputEscaping(false);
            add(label);
            add(folderTree);
            add(new FormErrorDisplay(this));
            final SaveCancelSection saveCancelSection = new SaveCancelSection();
            cancelButton = saveCancelSection.getCancelButton();
            add(saveCancelSection);
        }

        @Override
        public void register(final Page page) {
            super.register(page);
            page.addComponentStateParam(this, targetFolderModel
                                        .getStateParameter());
        }

        public void expose(final PageState state) {

            final Folder folder = folderSelectionModel.getSelectedObject(state);
            targetFolderModel.clearSelection(state);
            if (folder != null) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final FolderManager folderManager = cdiUtil.findBean(
                    FolderManager.class);
                if (!folderManager.getParentFolder(folder).isPresent()) {
                    folderTree.expand(Long.toString(folder.getObjectId()),
                                      state);
                } else {
                    final List<Folder> parents = folderManager
                        .getParentFolders(folder);
                    parents
                        .stream()
                        .map(parent -> Long.toString(parent.getObjectId()))
                        .forEach(folderId -> folderTree.expand(folderId, state));
                }
            }
        }

        @Override
        public void reset(final PageState state) {
            folderTree.clearSelection(state);
            state.setValue(folderTree.getSelectionModel().getStateParameter(),
                           null);
        }

        public Folder getTarget(final PageState state) {
            return targetFolderModel.getSelectedObject(state);
        }

        public boolean isCancelled(final PageState state) {
            return cancelButton.isSelected(state);
        }

    }

    private class FolderTreeCellRenderer implements TreeCellRenderer {

        private final RequestLocal invalidFoldersRequestLocal
                                       = new RequestLocal();

        /**
         * Render the folders appropriately. The selected folder is a bold
         * label. Invalid folders are plain labels. Unselected, valid folders
         * are control links. Invalid folders are: the parent folder of the
         * sources, any of the sources, and any subfolders of the sources.
         */
        @Override
        @SuppressWarnings("unchecked")
        public Component getComponent(final Tree tree,
                                      final PageState state,
                                      final Object value,
                                      final boolean isSelected,
                                      final boolean isExpanded,
                                      final boolean isLeaf,
                                      final Object key) {

            // Get the list of invalid folders once per request.
            final List<String> invalidFolders;

            if (invalidFoldersRequestLocal.get(state) == null) {
                final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                final AssetFolderBrowserController controller = cdiUtil
                    .findBean(AssetFolderBrowserController.class);
                invalidFolders = controller.createInvalidTargetsList(
                    Arrays.asList(getSources(state)));
                invalidFoldersRequestLocal.set(state, invalidFolders);
            } else {
                invalidFolders = (List<String>) invalidFoldersRequestLocal
                    .get(state);
            }
            final Label label = new Label(value.toString());

            if (invalidFolders.contains(String.format(
                FOLDER_BROWSER_KEY_PREFIX_FOLDER + "%s", key))) {
                return label;
            }

            // Bold if selected
            if (isSelected) {
                label.setFontWeight(Label.BOLD);
                return label;
            }

            return new ControlLink(label);
        }

    }

    private class TargetSelectorValidationListener
        implements FormValidationListener {

        @Override
        public void validate(final FormSectionEvent event)
            throws FormProcessException {

            final PageState state = event.getPageState();

            if (getSources(state).length <= 0) {
                throw new IllegalStateException("No source items specified");
            }

            final Folder target = targetSelector.getTarget(state);
            final FormData data = event.getFormData();
            if (target == null) {
                data.addError(new GlobalizedMessage(
                    "cms.ui.folder.need_select_target_folder",
                    CmsConstants.CMS_FOLDER_BUNDLE));
                //If the target is null, we can skip the rest of the checks
                return;
            }

            if (target.equals(folderSelectionModel.getSelectedObject(state))) {
                data.addError(new GlobalizedMessage(
                    "cms.ui.folder.not_within_same_folder",
                    CmsConstants.CMS_FOLDER_BUNDLE));
            }

            // check create item permission
            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);
            if (!permissionChecker.isPermitted(
                ItemPrivileges.CREATE_NEW, target)) {
                data.addError("cms.ui.folder.no_permission_for_item",
                              CmsConstants.CMS_FOLDER_BUNDLE);
            }

            for (String source : getSources(state)) {

                validateObject(source, target, state, data);

            }
        }

        private void validateObject(final String objectId,
                                    final Folder target,
                                    final PageState state,
                                    final FormData data) {

            Objects.requireNonNull(objectId, "objectId can't be null.");

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final FolderRepository folderRepo = cdiUtil
                .findBean(FolderRepository.class);
            final AssetRepository assetRepo = cdiUtil
                .findBean(AssetRepository.class);
            final AssetManager assetManager = cdiUtil
                .findBean(AssetManager.class);
            final AssetFolderBrowserController controller = cdiUtil
                .findBean(AssetFolderBrowserController.class);
            final FolderManager folderManager = cdiUtil
                .findBean(FolderManager.class);
            final PermissionChecker permissionChecker = cdiUtil.findBean(
                PermissionChecker.class);

            final CcmObject object;
            final String name;
            if (objectId.startsWith(FOLDER_BROWSER_KEY_PREFIX_FOLDER)) {

                final long folderId = Long.parseLong(objectId.substring(
                    FOLDER_BROWSER_KEY_PREFIX_FOLDER.length()));
                final Folder folder = folderRepo.findById(folderId).orElseThrow(
                    () -> new IllegalArgumentException(String.format(
                        "No folder with id %d in database.", folderId)));

                name = folder.getName();

                //Check if folder or subfolder contains in use assets
                if (isMove(state)) {
                    final FolderManager.FolderIsMovable movable = folderManager
                        .folderIsMovable(folder, target);
                    switch (movable) {
                        case DIFFERENT_SECTIONS:
                            addErrorMessage(data,
                                            "cms.ui.folder.different_sections",
                                            name);
                            break;
                        case HAS_IN_USE_ASSETS:
                            addErrorMessage(data,
                                            "cms.ui.folder.has_in_use_assets",
                                            name);
                            break;
                        case DIFFERENT_TYPES:
                            addErrorMessage(data,
                                            "cms.ui.folder.different_folder_types",
                                            name);
                            break;
                        case IS_ROOT_FOLDER:
                            addErrorMessage(data,
                                            "cms.ui.folder.is_root_folder",
                                            name);
                            break;
                        case SAME_FOLDER:
                            addErrorMessage(data,
                                            "cms.ui.folder.same_folder",
                                            name);
                            break;
                        case YES:
                            //Nothing
                            break;
                        default:
                            throw new UnexpectedErrorException(String.format(
                                "Unknown state '%s' for '%s'.",
                                movable,
                                FolderManager.FolderIsMovable.class.getName()));
                    }
                }

                object = folder;
            } else if (objectId.startsWith(FOLDER_BROWSER_KEY_PREFIX_ASSET)) {
                final long assetId = Long.parseLong(objectId.substring(
                    FOLDER_BROWSER_KEY_PREFIX_ASSET.length()));
                final Asset asset = assetRepo
                    .findById(assetId)
                    .orElseThrow(() -> new IllegalArgumentException(
                    String.format(
                        "No asset with id %d in the database.",
                        assetId)));

                name = asset.getDisplayName();

                if (isMove(state) && assetManager.isAssetInUse(asset)) {
                    addErrorMessage(data, "cms.ui.folder.item_is_live", name);
                }

                object = asset;
            } else {
                throw new IllegalArgumentException(String.format(
                    "Provided objectId '%s' does not start with '%s' "
                        + "or '%s'.",
                    objectId,
                    FOLDER_BROWSER_KEY_PREFIX_FOLDER,
                    FOLDER_BROWSER_KEY_PREFIX_ASSET));
            }

            final long count = controller.countObjects(target, name);
            if (count > 0) {
                // there is an item or subfolder in the target folder that already has this name
                addErrorMessage(data, "cms.ui.folder.item_already_exists",
                                name);
            }

            if (!(permissionChecker.isPermitted(
                  ItemPrivileges.DELETE, object))
                    && isMove(state)) {
                addErrorMessage(data,
                                "cms.ui.folder.no_permission_for_item",
                                object.getDisplayName());
            }

        }

    }

    private void addErrorMessage(final FormData data,
                                 final String message,
                                 final String itemName) {
        data.addError(new GlobalizedMessage(message,
                                            CmsConstants.CMS_FOLDER_BUNDLE,
                                            new Object[]{itemName}));
    }

    private class AssetFolderTree extends Tree {

        public AssetFolderTree(final FolderSelectionModel folderSelectionModel) {

            super(new FolderTreeModelBuilder() {

                @Override
                protected Folder getRootFolder(final PageState state) {
                    final ContentSection section = CMS
                        .getContext()
                        .getContentSection();

                    return section.getRootAssetsFolder();
                }

            });
            setSelectionModel(selectionModel);
        }

        @Override
        public void setSelectedKey(final PageState state, final Object key) {
            if (key instanceof String) {
                final Long keyAsLong;
                if (((String) key).startsWith(
                    FOLDER_BROWSER_KEY_PREFIX_FOLDER)) {
                    keyAsLong = Long.parseLong(((String) key).substring(
                        FOLDER_BROWSER_KEY_PREFIX_FOLDER.length()));
                } else {
                    keyAsLong = Long.parseLong((String) key);
                }
                super.setSelectedKey(state, keyAsLong);
            } else if (key instanceof Long) {
                super.setSelectedKey(state, key);
            } else {
                //We know that a FolderSelectionModel only takes keys of type Long.
                //Therefore we try to cast here
                final Long keyAsLong = (Long) key;
                super.setSelectedKey(state, keyAsLong);
            }
        }

    }

}
