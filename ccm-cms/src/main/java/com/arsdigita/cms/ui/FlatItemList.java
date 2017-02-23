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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.Resettable;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.event.FormSubmissionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.LongParameter;
import com.arsdigita.cms.CMS;

import org.librecms.contentsection.ContentSection;

import com.arsdigita.cms.ui.authoring.CreationSelector;
import com.arsdigita.cms.ui.authoring.NewItemForm;
import com.arsdigita.cms.ui.folder.FolderCreateForm;
import com.arsdigita.cms.ui.folder.FolderEditorForm;
import com.arsdigita.cms.ui.folder.FolderManipulator;
import com.arsdigita.cms.ui.folder.FolderPath;
import com.arsdigita.cms.ui.folder.FolderRequestLocal;
import com.arsdigita.cms.ui.folder.FolderSelectionModel;
import com.arsdigita.cms.ui.permissions.CMSPermissionsPane;
import com.arsdigita.globalization.GlobalizedMessage;

import com.arsdigita.toolbox.ui.ActionGroup;
import com.arsdigita.ui.CcmObjectSelectionModel;

import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.PermissionManager;
import org.librecms.CmsConstants;
import org.librecms.contentsection.Folder;
import org.librecms.contentsection.privileges.ItemPrivileges;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Encapsulates a {@link FolderManipulator} in order to create a flat item
 * listing. Also contains a new item form.
 *
 * @author <a href="mailto:sfreidin@arsdigita.com">Stanislav Freidin</a>
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FlatItemList extends SegmentedPanel implements FormProcessListener,
                                                            ChangeListener,
                                                            FormSubmissionListener,
                                                            Resettable,
                                                            ActionListener {

    private static final String CONTENT_TYPE_ID = "ct";
    // The folder selectors
    private final FolderSelectionModel folderSelectionModel;
    private final FolderRequestLocal folderRequestLocal;
    private final NewItemForm newItemForm;
    private final SingleSelectionModel<Long> typeSelectionModel;
    private final CreationSelector creationSelector;
    private final FolderManipulator folderManipulator;
    private final FolderCreateForm folderCreator;
//    private final ActionLink m_setHomeFolderAction;
//    private final ActionLink m_removeHomeFolderAction;
    private final ActionLink createFolderAction;
//    private final ActionLink togglePrivateAction;
//    private final Label m_homeFolderLabel;
    private final Segment browseSegment;
    private final Segment newItemSegment;
    private final Segment newFolderSegment;
    private final Segment editFolderSegment;
    private final Segment permissionsSegment;
    private final CMSPermissionsPane permissionsPane;
    // Folder edit/rename functionality.
    private final ActionLink editFolderAction;
    private final FolderEditorForm folderEditor;
    private final Label contentLabel;
    private final FolderPath folderPath;
    private final Label chooseLabel;

    /**
     * Construct a new item listing pane. The provided folder selection model is
     * used to keep track of the currently displayed folder.
     *
     * @param folderRequestLocal
     * @param folderSelectionModel maintains the currently displayed folder.
     */
    @SuppressWarnings("unchecked")
    public FlatItemList(final FolderRequestLocal folderRequestLocal,
                        final FolderSelectionModel folderSelectionModel) {
        this.folderRequestLocal = folderRequestLocal;
        this.folderSelectionModel = folderSelectionModel;
        folderSelectionModel.addChangeListener(event -> reset(event
            .getPageState()));

        setIdAttr("flat-item-list");

        newItemSegment = addSegment();
        newItemSegment.setIdAttr("folder-new-item");

        newFolderSegment = addSegment();
        newFolderSegment.setIdAttr("folder-new-folder");

        editFolderSegment = addSegment();
        editFolderSegment.setIdAttr("folder-edit-folder");

        browseSegment = addSegment();
        browseSegment.setIdAttr("folder-browse");

        final ActionGroup browseActions = new ActionGroup();
        browseSegment.add(browseActions);

        // The top 'browse' segment
        contentLabel = new Label(globalize("cms.ui.contents_of"), false);
        browseSegment.addHeader(contentLabel);
        chooseLabel = new Label(globalize("cms.ui.choose_target_folder"),
                                false);
        browseSegment.addHeader(chooseLabel);
        folderPath = new FolderPath(folderSelectionModel);

        browseSegment.addHeader(folderPath);
        folderManipulator = new FolderManipulator(folderSelectionModel);
        folderManipulator.getItemView().addProcessListener(this);
        folderManipulator.getTargetSelector().addProcessListener(this);
        folderManipulator.getTargetSelector().addSubmissionListener(this);

        browseActions.setSubject(folderManipulator);

        createFolderAction = new ActionLink(new Label(globalize(
            "cms.ui.new_folder")));
        createFolderAction.addActionListener(this);
        browseActions.addAction(createFolderAction);

        editFolderAction = new ActionLink(new Label(globalize(
            "cms.ui.edit_folder")));
        editFolderAction.addActionListener(this);
        browseActions.addAction(editFolderAction);

        newItemForm = new SectionNewItemForm("newItem");
        newItemForm.addProcessListener(this);
        browseActions.addAction(newItemForm);

        permissionsSegment = addSegment();
        permissionsSegment.setIdAttr("folder-permissions");

        final ActionGroup permissionActions = new ActionGroup();
        permissionsSegment.add(permissionActions);

        permissionsSegment.addHeader(new Label(new GlobalizedMessage(
            "cms.ui.permissions", CmsConstants.CMS_BUNDLE)));

        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionManager permissionManager = cdiUtil.findBean(
            PermissionManager.class);
        final List<String> privileges = permissionManager
            .listDefiniedPrivileges(ItemPrivileges.class);
        final Map<String, String> privNameMap = new HashMap<>();
        privileges.forEach(privilege -> privNameMap.put(privilege, privilege));

        permissionsPane = new CMSPermissionsPane(
            privileges.toArray(new String[]{}),
            privNameMap,
            (CcmObjectSelectionModel) folderSelectionModel);
        permissionActions.setSubject(permissionsPane);

        newItemSegment.addHeader(new Label(globalize("cms.ui.new_item")));
        typeSelectionModel = new ParameterSingleSelectionModel<>(
            new LongParameter(CONTENT_TYPE_ID));
        typeSelectionModel.addChangeListener(this);

        creationSelector = new CreationSelector(typeSelectionModel,
                                                folderSelectionModel);
        newItemSegment.add(creationSelector);
        //m_newItemSeg.add(new Label("<br/>", false));

        // The 'new folder' segment
        newFolderSegment.addHeader(new Label(globalize("cms.ui.new_folder")));
//        final Form folderCreate = new Form("fcreat");
        folderCreator = new FolderCreateForm("fcreat", folderSelectionModel);
        folderCreator.addSubmissionListener(this);
        folderCreator.addProcessListener(this);
        //folderCreator.add(folderCreator);
        newFolderSegment.add(folderCreator);
        newFolderSegment.add(new Label("<br/>", false));

        editFolderSegment.addHeader(new Label(globalize("cms.ui.edit_folder")));
        folderEditor = new FolderEditorForm("fedit", folderSelectionModel);
        folderEditor.addSubmissionListener(this);
        folderEditor.addProcessListener(this);
        //Form folderEditorForm = new Form("fedit_form");
        //folderEditorForm.add(folderEditor);
        editFolderSegment.add(folderEditor);
        editFolderSegment.add(new Label("<br/>", false));
    }

    @Override
    public void register(final Page page) {
        super.register(page);

        page.setVisibleDefault(chooseLabel, false);
//        page.setVisibleDefault(browseSegment, true);
        page.setVisibleDefault(newItemSegment, false);
        page.setVisibleDefault(newFolderSegment, false);
        page.setVisibleDefault(editFolderSegment, false);

        page.addComponentStateParam(this,
                                    typeSelectionModel.getStateParameter());

        page.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(final ActionEvent event) {
                final PageState state = event.getPageState();

                if (state.isVisibleOnPage(FlatItemList.this)) {
                    showHideSegments(state);
                }
            }

        });
    }

    /**
     * Show/hide segments based on access checks.
     *
     * @param state The page state
     *
     * @pre ( state != null )
     */
    private void showHideSegments(final PageState state) {
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);
        final Folder folder = folderRequestLocal.getFolder(state);

        // MP: This should be checked on the current folder instead of just
        //     the content section.
        final boolean newItem = permissionChecker.isPermitted(
            ItemPrivileges.CREATE_NEW, folder);

        if (!newItem) {
            browseMode(state);
        }

        createFolderAction.setVisible(state, newItem);
        newItemForm.setVisible(state, newItem);

        final boolean editItem = permissionChecker.isPermitted(
            ItemPrivileges.EDIT, folder);

        editFolderAction.setVisible(state, editItem);
//        chooseLabel.setVisible(state, editItem);
//        editFolderSegment.setVisible(state, editItem);

        if (permissionChecker.isPermitted(ItemPrivileges.ADMINISTER, folder)) {
            permissionsSegment.setVisible(state, true);
        } else {
            permissionsSegment.setVisible(state, false);
        }
    }

    private void browseMode(final PageState state) {
        browseSegment.setVisible(state, true);
        permissionsSegment.setVisible(state, true);
        chooseLabel.setVisible(state, false);
        contentLabel.setVisible(state, true);
        folderPath.setVisible(state, true);
        newItemSegment.setVisible(state, false);
        newFolderSegment.setVisible(state, false);
        editFolderSegment.setVisible(state, false);

        typeSelectionModel.clearSelection(state);
    }

    private void newItemMode(final PageState state) {
        permissionsSegment.setVisible(state, false);
        newItemSegment.setVisible(state, true);
    }

    private void newFolderMode(final PageState state) {
        permissionsSegment.setVisible(state, false);
        newFolderSegment.setVisible(state, true);
    }

    @Override
    public void submitted(final FormSectionEvent event)
        throws FormProcessException {

        final PageState state = event.getPageState();
        if (event.getSource() == folderCreator
                && folderCreator.isCancelled(state)) {
            browseMode(state);
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.cancelled", CmsConstants.CMS_BUNDLE));
        } else if (event.getSource() == folderEditor
                       && folderEditor.isCancelled(state)) {
            browseMode(state);
            throw new FormProcessException(new GlobalizedMessage(
                "cms.ui.cancelled", CmsConstants.CMS_BUNDLE));
        } else if (event.getSource() == folderManipulator.getTargetSelector()) {
            // This only works if this submission listener is run
            // after the target selector's one
            if (!folderManipulator.getTargetSelector().isVisible(state)) {
                browseMode(state);
            }
        }
    }

    @Override
    public void process(final FormSectionEvent event) {
        final PageState state = event.getPageState();
        final Object source = event.getSource();
        if (source == newItemForm) {
            final Long typeID = newItemForm.getTypeID(state);
            typeSelectionModel.setSelectedKey(state, typeID);
            newItemMode(state);
        } else if (source == folderCreator || source == folderEditor) {
            browseMode(state);
        } else if (source == folderManipulator.getItemView()) {
            // Hide everything except for the browseSeg
            permissionsSegment.setVisible(state, false);
            chooseLabel.setVisible(state, true);
            contentLabel.setVisible(state, false);
            folderPath.setVisible(state, false);
        } else if (source == folderManipulator.getTargetSelector()) {
            browseMode(state);
        }
    }

    @Override
    public void stateChanged(final ChangeEvent event) {
        final PageState state = event.getPageState();
        if (event.getSource().equals(typeSelectionModel)) {
            if (!typeSelectionModel.isSelected(state)) {
                browseMode(state);
            }
        }
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final PageState state = event.getPageState();
        final Object source = event.getSource();
        if (source == createFolderAction) {
            newFolderMode(state);
        } else if (source == editFolderAction) {
            permissionsSegment.setVisible(state, false);
            editFolderSegment.setVisible(state, true);
        }
//          else if (source == togglePrivateAction) {
//            togglePermissions(state);
//        }
//        } else if (source == m_setHomeFolderAction) {
//            User user = Web.getWebContext().getUser();
//            Folder folder = m_folder.getFolder(state);
//            user = (User) DomainObjectFactory.newInstance(user.getOID());
//            Folder.setUserHomeFolder(user, folder);
//        } else if (source == m_removeHomeFolderAction) {
//            User user = Web.getWebContext().getUser();
//            ContentSection section = CMS.getContext().getContentSection();
//            UserHomeFolderMap map = UserHomeFolderMap
//                .findUserHomeFolderMap(user, section);
//            if (map != null) {
//                map.delete();
//            }
//        }
    }

    private void togglePermissions(final PageState state) {
        final Folder currentFolder = folderRequestLocal.getFolder(state);
        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
        final PermissionChecker permissionChecker = cdiUtil.findBean(
            PermissionChecker.class);
        permissionChecker.checkPermission(ItemPrivileges.ADMINISTER,
                                          currentFolder);

        permissionsPane.reset(state);
    }

    @Override
    public void reset(final PageState state) {
        browseMode(state);
        folderManipulator.reset(state);
        // switching between folders used to keep showing the permission pane
        // in the same perm mode (direct or inherited) regardless
        // of the folder status
        permissionsPane.reset(state);
    }

    public final FolderManipulator getManipulator() {
        return folderManipulator;
    }

    public final CMSPermissionsPane getPermissionsPane() {
        return permissionsPane;
    }

    public void setPermissionLinkVis(final PageState state) {
//        final Folder currentFolder = folderRequestLocal.getFolder(state);
//        final CdiUtil cdiUtil = CdiUtil.createCdiUtil();
//        final PermissionChecker permissionChecker = cdiUtil.findBean(
//            PermissionChecker.class);
//        if (!permissionChecker.isPermitted(ItemPrivileges.ADMINISTER,
//                                           currentFolder)) {
//            togglePrivateAction.setVisible(state, false);
//        }
    }

    private static class SectionNewItemForm extends NewItemForm {

        public SectionNewItemForm(final String name) {
            super(name);
        }

        @Override
        public ContentSection getContentSection(final PageState state) {
            return CMS.getContext().getContentSection();
        }

    }

    /**
     * Getting the GlobalizedMessage using a CMS Class targetBundle.
     *
     * @param key The resource key
     *
     * @pre ( key != null )
     */
    private static GlobalizedMessage globalize(final String key) {
        return ContentSectionPage.globalize(key);
    }

}
