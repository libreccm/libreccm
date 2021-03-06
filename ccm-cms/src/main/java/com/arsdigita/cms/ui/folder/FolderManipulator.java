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
package com.arsdigita.cms.ui.folder;

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormData;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Paginator;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SaveCancelSection;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.Text;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.FormValidationListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.form.CheckboxGroup;
import com.arsdigita.bebop.form.FormErrorDisplay;
import com.arsdigita.bebop.form.Option;
import com.arsdigita.bebop.form.OptionGroup;
import com.arsdigita.bebop.form.SingleSelect;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.form.TextField;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableColumn;
import com.arsdigita.bebop.tree.TreeCellRenderer;
import com.arsdigita.cms.CMS;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.ActionGroup;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.List;

import org.librecms.CMSConfig;
import org.libreccm.categorization.Category;
import org.libreccm.categorization.CategoryManager;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.core.CcmObject;
import org.libreccm.core.UnexpectedErrorException;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.Shiro;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemManager;
import org.librecms.contentsection.ContentItemRepository;
import org.librecms.contentsection.ContentSection;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.FolderManager;
import org.librecms.contentsection.FolderRepository;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.Objects;

import static org.librecms.CmsConstants.*;

/**
 * Browse folders and manipulate them with various actions (move/copy/delete).
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 */
@SuppressWarnings("PMD.BeanMembersShouldSerialize")
public class FolderManipulator extends SimpleContainer implements
        //FormProcessListener,
        //FormValidationListener,
        //FormSubmissionListener,
        Resettable {

    //public static final String RESOURCE_BUNDLE = "com.arsdigita.cms.ui.folder.CMSFolderResources";
    private static final Logger LOGGER = LogManager.getLogger(
            FolderManipulator.class);

    private static final String ATOZ_FILTER_PARAM = "aToZfilter";
    private static final String ACTION_PARAM = "act";
    private static final String FILTER_PARAM = "filter";

    private static final String SOURCES_PARAM = "srcs";
    private static final String MOVE = "Move";
    private static final String COPY = "Copy";
    //private static final String PUBLISH = "Publish";
    //private static final String UNPUBLISH = "UnPublish";

    private final ArrayParameter sourcesParam = new ArrayParameter(
            new StringParameter(SOURCES_PARAM));
    private final StringParameter actionParam
                                  = new StringParameter(ACTION_PARAM);
    ;
    /**
     * The folder in which the source items live.
     */
    private final FolderSelectionModel sourceFolderModel;
    private final ItemView itemView;
    private final TargetSelector targetSelector = new TargetSelector();
    //private final PublishDialog publishDialog = new PublishDialog();

    private FilterForm filterForm;
    private final StringParameter atozFilterParam = new StringParameter(
            ATOZ_FILTER_PARAM);
    private final StringParameter filterParam
                                  = new StringParameter(FILTER_PARAM);

    public FolderManipulator(final FolderSelectionModel folderModel) {

        super();

        sourceFolderModel = folderModel;
        itemView = new ItemView();
        itemView.addProcessListener(new ItemViewProcessListener());
        itemView.addValidationListener(new ItemViewValidationListener());
        add(itemView);

        targetSelector.addProcessListener(new TargetSelectorProcessListener());
        targetSelector.addValidationListener(
                new TargetSelectorValidationListener());
        targetSelector.addSubmissionListener(
                new TargetSelectorSubmissionListener());
        add(targetSelector);

        //publishDialog.addProcessListener(new PublishDialogProcessListener());
    }

    @Override
    public void register(final Page page) {

        super.register(page);
        page.setVisibleDefault(targetSelector, false);
        page.setVisibleDefault(filterForm, true);
        page.addComponentStateParam(this, sourcesParam);
        page.addComponentStateParam(this, actionParam);
        page.addComponentStateParam(this, atozFilterParam);
        page.addComponentStateParam(this, filterParam);

    }

    public final String[] getSources(final PageState state) {

        final String[] result = (String[]) state.getValue(sourcesParam);

        //Return empty array instead of null.
        if (result == null) {
            return new String[0];
        } else {
            return result;
        }
    }

    public final FolderSelectionModel getSourceFolderModel() {
        return sourceFolderModel;
    }

    public final Category getTarget(final PageState state) {
        return targetSelector.getTarget(state);
    }

    protected final boolean isMove(final PageState state) {
        return MOVE.equals(getAction(state));
    }

    protected final boolean isCopy(final PageState state) {
        return COPY.equals(getAction(state));
    }

//    protected final boolean isPublish(final PageState state) {
//        return PUBLISH.equals(getAction(state));
//    }
//
//    protected final boolean isUnPublish(final PageState state) {
//        return UNPUBLISH.equals(getAction(state));
//    }
    private String getAction(final PageState state) {
        return (String) state.getValue(actionParam);
    }

    protected void moveObjects(final Folder target, final String[] objectIds) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final FolderBrowserController controller = cdiUtil.findBean(
                FolderBrowserController.class);

        controller.moveObjects(target, objectIds);
    }

    protected void copyObjects(final Folder target, final String[] objectIds) {

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final FolderBrowserController controller = cdiUtil.findBean(
                FolderBrowserController.class);

        controller.copyObjects(target, objectIds);
    }

//    protected void publishItems(final BigDecimal[] itemIds) {
//
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("(Re-)Publishing items " + Arrays.asList(itemIds));
//        }
//
//        for (BigDecimal itemId : itemIds) {
//
//            final ContentItem item = retrieveSourceItem(itemId);
//
//            if (item.isLive()) {
//                //Republish
//                //Ensure that we have the draft version
//                //final ContentItem draftItem = item.getDraftVersion(); 
//                republish(itemId);
//            } else {
//                publish(itemId);
//            }
//
//            final LifecycleDefinition lifecycleDef = ContentTypeLifecycleDefinition
//                .getLifecycleDefinition(item.getContentSection(), item.getContentType());
//
//        }
//    }
//    private void publish(final BigDecimal itemId) {
//        
//    }
//    
//    private void republish(final BigDecimal itemId) {
//
//        final User user = Web.getWebContext().getUser();
//
//        final Thread thread = new Thread(new Runnable() {
//
//            @Override
//            public void run() {
//                final ContentItem item = retrieveSourceItem(itemId);
//
//                PublishLock.getInstance().lock(item);
//                item.republish(false);
//                Workflow workflow = Workflow.getObjectWorkflow(item);
//                ItemLifecycleSelectForm.finish(workflow, item, user);
//                PublishLock.getInstance().unlock(item);
//            }
//
//        });
//        thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//
//            @Override
//            public void uncaughtException(final Thread thread,
//                                          final Throwable ex) {
//                final StringWriter strWriter = new StringWriter();
//                final PrintWriter writer = new PrintWriter(strWriter);
//                ex.printStackTrace(writer);
//                final ContentItem item = retrieveSourceItem(itemId);
//
//                PublishLock.getInstance().setError(item, strWriter.toString());
//                LOGGER.error(String.format(
//                    "An error occurred while "
//                        + "publishing the item '%s': ",
//                    item.getOID().toString()),
//                             ex);
//
//                if ((CMSConfig.getInstanceOf().
//                     getPublicationFailureSender()
//                     == null)
//                        && (CMSConfig.getInstanceOf().
//                            getPublicationFailureReceiver() == null)) {
//                    return;
//                }
//
//                final PartyCollection receiverParties = Party.retrieveAllParties();
//                Party receiver = null;
//                receiverParties.addEqualsFilter("primaryEmail",
//                                                CMSConfig.getInstanceOf().
//                                                getPublicationFailureReceiver());
//                if (receiverParties.next()) {
//                    receiver = receiverParties.getParty();
//                }
//                receiverParties.close();
//
//                final PartyCollection senderParties = Party.retrieveAllParties();
//                Party sender = null;
//                senderParties.addEqualsFilter("primaryEmail",
//                                              CMSConfig.getInstanceOf().
//                                              getPublicationFailureReceiver());
//                if (senderParties.next()) {
//                    sender = senderParties.getParty();
//                }
//                senderParties.close();
//
//                if ((sender != null) && (receiver != null)) {
//                    final Writer traceWriter = new StringWriter();
//                    final PrintWriter printWriter = new PrintWriter(
//                        traceWriter);
//                    ex.printStackTrace(printWriter);
//
//                    final Notification notification = new Notification(
//                        sender,
//                        receiver,
//                        String.format(
//                            "Failed to publish item '%s'",
//                            item.getOID().toString()),
//                        String.format("Publishing item '%s' failed "
//                                          + "with error message: %s.\n\n"
//                                          + "Stacktrace:\n%s",
//                                      item.getOID().toString(),
//                                      ex.getMessage(),
//                                      traceWriter.toString()));
//                    notification.save();
//                }
//            }
//
//        });
//
//        thread.start();
//    }
//    private ContentItem retrieveSourceItem(final BigDecimal itemToCopyId) {
//
//        ContentItem source = (ContentItem) DomainObjectFactory.newInstance(
//                new OID(ContentItem.BASE_DATA_OBJECT_TYPE, itemToCopyId));
//        Assert.exists(source, ContentItem.class);
//
//        final ACSObject parent = source.getParent();
//        if (parent instanceof ContentBundle) {
//            source = (ContentBundle) parent;
//        }
//
//        if (LOGGER.isDebugEnabled()) {
//            LOGGER.debug("Copying item " + source);
//        }
//
//        return source;
//    }
    /**
     * Returns the form that contains the folder browser and the move/copy
     * dropdown.
     *
     * @return The form containing the folder browser and dropdown menu
     */
    public final Form getItemView() {
        return itemView;
    }

    /**
     * Returns the form to choose the target folder for move/copy
     *
     * @return
     */
    public final Form getTargetSelector() {
        return targetSelector;
    }

    /**
     * Return the browser contained in the ItemView form
     *
     * @return
     */
    public final FolderBrowser getBrowser() {
        return itemView.getBrowser();
    }

    private class ItemViewProcessListener implements FormProcessListener {

        public ItemViewProcessListener() {
            //Nothing
        }

        @Override
        public void process(final FormSectionEvent event)
                throws FormProcessException {

            final PageState state = event.getPageState();

            itemView.setVisible(state, false);
            targetSelector.setVisible(state, true);
            targetSelector.expose(state);
        }

    }

    private class TargetSelectorProcessListener implements FormProcessListener {

        public TargetSelectorProcessListener() {
            //Nothing
        }

        @Override
        public void process(final FormSectionEvent event)
                throws FormProcessException {

            final PageState state = event.getPageState();

            itemView.setVisible(state, true);
            targetSelector.setVisible(state, false);

            final Folder folder = targetSelector.getTarget(state);
            final String[] itemIds = getSources(state);

            if (isCopy(state)) {
                copyObjects(folder, itemIds);
            } else if (isMove(state)) {
                moveObjects(folder, itemIds);
            }

            reset(state);

        }

    }

//    private class PublishDialogProcessListener implements FormProcessListener {
//
//        public PublishDialogProcessListener() {
//            //Nothing
//        }
//
//        @Override
//        public void process(final FormSectionEvent event) throws FormProcessException {
//
//            final PageState state = event.getPageState();
//
//            itemView.setVisible(state, true);
//            publishDialog.setVisible(state, false);
//
//            final BigDecimal[] itemIds = getSources(state);
//
//            if (isPublish(state)) {
//                publishItems(itemIds);
//            } else if (isUnPublish(state)) {
//                unpublishItems(itemIds);
//            }
//
//            reset(state);
//        }
//
//    }
    private class ItemViewValidationListener implements FormValidationListener {

        public ItemViewValidationListener() {
            //Nothing
        }

        @Override
        public void validate(final FormSectionEvent event) throws
                FormProcessException {

            final PageState state = event.getPageState();
            final FormData data = event.getFormData();

            if (getSources(state).length <= 0) {
                data.addError("cms.ui.folder.must_select_item",
                              CmsConstants.CMS_FOLDER_BUNDLE);
            }
        }

    }

    private class TargetSelectorValidationListener
            implements FormValidationListener {

        public TargetSelectorValidationListener() {
            //Nothing
        }

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

            if (target.equals(sourceFolderModel.getSelectedObject(state))) {
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

                validateItem(source, target, state, data);

            }

        }

        private void validateItem(final String objectId,
                                  final Folder target,
                                  final PageState state,
                                  final FormData data) {

            Objects.requireNonNull(objectId, "objectId can't be null.");

            final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
            final FolderRepository folderRepo = cdiUtil.findBean(
                    FolderRepository.class);
            final ContentItemRepository itemRepo = cdiUtil.findBean(
                    ContentItemRepository.class);
            final ContentItemManager itemManager = cdiUtil.findBean(
                    ContentItemManager.class);
            final FolderBrowserController controller = cdiUtil.findBean(
                    FolderBrowserController.class);
            final FolderManager folderManager = cdiUtil.findBean(
                    FolderManager.class);
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

                //Check if folder or subfolder contains live items
                if (isMove(state)) {
                    final FolderManager.FolderIsMovable movable = folderManager
                            .folderIsMovable(folder, target);
                    switch (movable) {
                        case DIFFERENT_SECTIONS:
                            addErrorMessage(data,
                                            "cms.ui.folder.different_sections",
                                            name);
                            break;
                        case HAS_LIVE_ITEMS:
                            addErrorMessage(data,
                                            "cms.ui.folder.item_is_live",
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
                                    FolderManager.FolderIsMovable.class.
                                            getName()));
                    }
                }

                object = folder;

            } else if (objectId.startsWith(FOLDER_BROWSER_KEY_PREFIX_ITEM)) {
                final long itemId = Long.parseLong(objectId.substring(
                        FOLDER_BROWSER_KEY_PREFIX_ITEM.length()));
                final ContentItem item = itemRepo
                        .findById(itemId)
                        .orElseThrow(() -> new IllegalArgumentException(
                        String.format(
                                "No content item with id %d in database.",
                                itemId)));

                name = item.getDisplayName();

                if (isMove(state) && itemManager.isLive(item)) {
                    addErrorMessage(data, "cms.ui.folder.item_is_live", name);
                }

                object = item;
            } else {
                throw new IllegalArgumentException(String.format(
                        "Provided objectId '%s' does not start with '%s' "
                                + "or '%s'.",
                        objectId,
                        FOLDER_BROWSER_KEY_PREFIX_FOLDER,
                        FOLDER_BROWSER_KEY_PREFIX_ITEM));
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

//    @Override
//    public void submitted(final FormSectionEvent event) throws FormProcessException {
//
//        final PageState state = event.getPageState();
//
//        if (targetSelector.isCancelled(state)) {
//            reset(state);
//            throw new FormProcessException("cms.ui.folder.cancelled");
//
//        }
//    }
    private class TargetSelectorSubmissionListener implements
            FormSubmissionListener {

        public TargetSelectorSubmissionListener() {
            //Nothing
        }

        @Override
        public void submitted(final FormSectionEvent event) throws
                FormProcessException {

            final PageState state = event.getPageState();

            if (targetSelector.isCancelled(state)) {
                reset(state);
                throw new FormProcessException(new GlobalizedMessage(
                        "cms.ui.folder.cancelled",
                        CmsConstants.CMS_FOLDER_BUNDLE));
            }

        }

    }

    @Override
    public void reset(final PageState state) {

        itemView.setVisible(state, true);
        itemView.reset(state);
        targetSelector.setVisible(state, false);
        targetSelector.reset(state);
        //publishDialog.setVisible(state, false);
        state.setValue(actionParam, null);
        state.setValue(sourcesParam, null);
        //s.setValue(m_aToZfilter, null);
        state.setValue(filterParam, null);

    }

    // The form containing the tree to select the target folder from
    private class TargetSelector extends Form implements Resettable {

        private final FolderSelectionModel targetModel;
        private final FolderTree folderTree;
        private final Submit cancelButton;

        public TargetSelector() {
            super("targetSel", new BoxPanel());
            setMethod(GET);
            targetModel = new FolderSelectionModel("target") {

                @Override
                protected Long getRootFolderID(final PageState state) {
                    final ContentSection section = CMS
                            .getContext()
                            .getContentSection();
                    return section.getRootDocumentsFolder().getObjectId();
                }

            };
            folderTree = new FolderTree(targetModel);
            folderTree.setCellRenderer(new FolderTreeCellRenderer());

            final Label label = new Label(new PrintListener() {

                @Override
                public void prepare(final PrintEvent event) {
                    final PageState state = event.getPageState();
                    final Label label = (Label) event.getTarget();
                    final int numberOfItems = getSources(state).length;
                    final Category folder = (Category) sourceFolderModel
                            .getSelectedObject(state);
                    final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
                    final CategoryManager categoryManager = cdiUtil.
                            findBean(CategoryManager.class);

                    final String targetFolderPath;
                    if (targetModel.getSelectedObject(state) == null) {
                        targetFolderPath = "";
                    } else {
                        targetFolderPath = categoryManager.getCategoryPath(
                                targetModel.getSelectedObject(state));
                    }

                    if (isMove(state)) {
                        label.setLabel(new GlobalizedMessage(
                                "cms.ui.folder.move",
                                CmsConstants.CMS_FOLDER_BUNDLE,
                                new Object[]{
                                    numberOfItems,
                                    categoryManager.getCategoryPath(folder),
                                    targetFolderPath
                                }));
                    } else if (isCopy(state)) {
                        label.setLabel(new GlobalizedMessage(
                                "cms.ui.folder.copy",
                                CMS_BUNDLE,
                                new Object[]{
                                    numberOfItems,
                                    categoryManager.getCategoryPath(folder),
                                    targetFolderPath
                                }));
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
            page.addComponentStateParam(this, targetModel.getStateParameter());
        }

        // Set things up the first time the selector gets visible
        public void expose(final PageState state) {
            final Folder folder = sourceFolderModel.getSelectedObject(state);
            targetModel.clearSelection(state);
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
                            .forEach(folderId -> folderTree.expand(folderId,
                                                                   state));
                }
            }
        }

        @Override
        public void reset(final PageState state) {
            folderTree.clearSelection(state);
            // FIXME: add a reset method to Tree and call that instead of this
            // hack
            state.setValue(folderTree.getSelectionModel().getStateParameter(),
                           null);
        }

        public Folder getTarget(final PageState state) {
            return targetModel.getSelectedObject(state);
        }

        public boolean isCancelled(final PageState state) {
            return cancelButton.isSelected(state);
        }

    }

    //The form which is show for the publish and unpublish action
//    private class PublishDialog extends Form {
//
//        public PublishDialog() {
//            super("PublishDialog", new BoxPanel());
//
//            final Label label = new Label(new PrintListener() {
//
//                @Override
//                public void prepare(final PrintEvent event) {
//
//                    final PageState state = event.getPageState();
//                    final Label target = (Label) event.getTarget();
//                    final int numberOfItems = getSources(state).length;
//                    final Folder folder = (Folder) sourceFolderModel.getSelectedObject(state);
//
//                    if (isPublish(state)) {
//                        target.setLabel(globalizationUtil.globalize(
//                            "cms.ui.folder.publish",
//                            new Object[]{numberOfItems,
//                                         folder.getPathNoJsp()}));
//                    } else if (isUnPublish(state)) {
//                        target.setLabel(globalizationUtil.globalize(
//                            "cms.ui.folder.publish",
//                            new Object[]{numberOfItems,
//                                         folder.getPathNoJsp()}));
//                    }
//                }
//
//            });
//
//            label.setOutputEscaping(false);
//            add(label);
//            add(new FormErrorDisplay(this));
//            add(new SaveCancelSection());
//        }
//
//    }
    // The form containing the browser and the drop down for selecting an
    // action
    private class ItemView extends Form implements Resettable {

        private static final String ITEM_VIEW = "itemView";

        private final FolderBrowser folderBrowser;
        private final Paginator paginator;
        private final OptionGroup checkboxGroup;
        private final SingleSelect actionSelect;
        private final Submit submit;

        public ItemView() {

            super(ITEM_VIEW, new SimpleContainer());
            setMethod(GET);

            final ActionGroup group = new ActionGroup();
            add(group);

            final GridPanel panel = new GridPanel(1);
            group.setSubject(panel);

            folderBrowser = new FolderBrowser(sourceFolderModel);
            folderBrowser.setAtoZfilterParameter(atozFilterParam);
            folderBrowser.setFilterParameter(filterParam);
//            folderBrowser.setFilterForm(filterForm);
            folderBrowser.setFolderManipulator(FolderManipulator.this);
            paginator = new Paginator(
                    new FolderBrowserPaginationModelBuilder(folderBrowser),
                    CMSConfig.getConfig().getFolderBrowseListSize());
            folderBrowser.setPaginator(paginator);
            panel.add(paginator);
            panel.add(folderBrowser);

            LOGGER.debug("Adding filter form...");
            filterForm = new FilterForm(new FolderBrowserFilterFormModelBuilder(
                    folderBrowser));
            FolderManipulator.this.add(filterForm);

            checkboxGroup = new CheckboxGroup(sourcesParam);
            panel.add(checkboxGroup);

            panel.add(new FormErrorDisplay(this));

            final Container container = new SimpleContainer();
            group.addAction(container);

            container.add(new Label(new GlobalizedMessage(
                    "cms.ui.folder.edit_selection",
                    CmsConstants.CMS_FOLDER_BUNDLE)));
            actionSelect = new SingleSelect(actionParam);
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
            //Publishing in the folder browser only works if threaded publishing is active
//            if (CMSConfig.getInstanceOf().getThreadedPublishing()) {
//                actionSelect.addOption(new Option(PUBLISH,
//                                                  new Label(globalizationUtil.globalize(
//                                                          "cms.ui.folder.publish.action"))));
//                actionSelect.addOption(new Option(UNPUBLISH,
//                                                  new Label(globalizationUtil.globalize(
//                                                          "cms.ui.folder.unpublish.action"))));
//            }
            container.add(actionSelect);
            submit = new Submit("Go",
                                new GlobalizedMessage(
                                        "cms.ui.folder.go",
                                        CmsConstants.CMS_FOLDER_BUNDLE));
            container.add(submit);

            // Add a new first column to the table
            final TableColumn column = new TableColumn();
            column.setHeaderValue(new GlobalizedMessage("empty_text",
                                                        CmsConstants.CMS_BUNDLE));
            column.setCellRenderer(new CheckboxRenderer());
            folderBrowser.getColumnModel().add(0, column);
        }

        public final FolderBrowser getBrowser() {
            return folderBrowser;
        }

        public Paginator getPaginator() {
            return paginator;
        }

        @Override
        public void reset(final PageState state) {

            checkboxGroup.setValue(state, null);
            actionSelect.setValue(state, null);
            paginator.reset(state);
//            state.setValue(m_aToZfilter, null);
            state.setValue(filterParam, null);
            filterForm.getFilterField().setValue(state, null);
        }

        // The renderer for the first column in the itemView table
        private class CheckboxRenderer implements TableCellRenderer {

            public CheckboxRenderer() {
                //Nothing to do
            }

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

        }

    }

    protected class FilterForm extends Form implements FormProcessListener,
                                                       FormInitListener,
                                                       FormSubmissionListener {

        private final SimpleContainer panel;
        private boolean visible;
        private final FilterFormModelBuilder modelBuilder;
        private final TextField filterField;

        public FilterForm(final FilterFormModelBuilder modelBuilder) {
            super("folderFilterForm");

            LOGGER.debug("Creating filter form...");

            this.modelBuilder = modelBuilder;

            addProcessListener(this);
            addInitListener(this);
            addSubmissionListener(this);

            panel = new BoxPanel(BoxPanel.HORIZONTAL);

            final ActionLink allLink = new ActionLink(
                    new GlobalizedMessage("cms.ui.folder.filter.all",
                                          CmsConstants.CMS_FOLDER_BUNDLE));
            allLink.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(final ActionEvent event) {
                    //event.getPageState().setValue(m_aToZfilter, "");
                    event.getPageState().setValue(filterParam, "");
                }

            });
            panel.add(allLink);

//            for (char c = 'A'; c <= 'Z'; c++) {
//                final char lowerCaseChar = Character.toLowerCase(c);
//                final ActionLink link = new ActionLink(Character.toString(c));
//                link.addActionListener(new ActionListener() {
//
//                    @Override
//                    public void actionPerformed(final ActionEvent event) {
//                        event.getPageState().setValue(m_aToZfilter,
//                                                      Character.toString(lowerCaseChar));
//                    }
//                });
//                panel.add(link);
//            }
            panel.add(new Label(new GlobalizedMessage(
                    "cms.ui.folder.filter",
                    CmsConstants.CMS_FOLDER_BUNDLE)));
            filterField = new TextField(filterParam);
            panel.add(filterField);
            panel.add(new Submit("filterFolderSubmit",
                                 new GlobalizedMessage(
                                         "cms.ui.folder.filter_do",
                                         CmsConstants.CMS_FOLDER_BUNDLE)));

            add(panel);

        }

        public TextField getFilterField() {
            return filterField;
        }

        @Override
        public void process(final FormSectionEvent event) throws
                FormProcessException {
            //Nothing
        }

        @Override
        public void init(final FormSectionEvent event) throws
                FormProcessException {
            //fse.getPageState().setValue(FolderManipulator.this.m_filter, null);
            //filterField.setValue(fse.getPageState(), null);
        }

        @Override
        public void submitted(final FormSectionEvent event) throws
                FormProcessException {
        }

        @Override
        public boolean isVisible(PageState state) {
            if (super.isVisible(state)
                        && (modelBuilder.getFolderSize(state)
                            >= CMSConfig.getConfig().
                            getFolderAtoZShowLimit())) {
                return true;
            } else {
                return false;
            }
        }

    }

    protected interface FilterFormModelBuilder {

        public long getFolderSize(PageState state);

    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key. May not null.
     *
     * @return The globalized message
     */
//    public static GlobalizedMessage globalize(final String key) {
//        return new GlobalizedMessage(key, RESOURCE_BUNDLE);
//    }
//
//    public static GlobalizedMessage globalize(final String key, final Object[] args) {
//        return new GlobalizedMessage(key, RESOURCE_BUNDLE, args);
//    }
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
                final FolderBrowserController controller = cdiUtil.findBean(
                        FolderBrowserController.class);
                invalidFolders = controller.createInvalidTargetsList(
                        Arrays.asList(getSources(state)));
                invalidFoldersRequestLocal.set(state, invalidFolders);
            } else {
                invalidFolders = (List<String>) invalidFoldersRequestLocal
                        .get(state);
            }

            final Label label = new Label(value.toString());

            if (invalidFolders.contains(key.toString())) {
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

}
