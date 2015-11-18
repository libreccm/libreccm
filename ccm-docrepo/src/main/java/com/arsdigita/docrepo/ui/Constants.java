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
package com.arsdigita.docrepo.ui;

import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;

/**
 * Variously used constant objects used in Document Repository UI
 *
 * Todo: refactor this whole fucking class. Totally idiotic.
 *
 * @author <a href="mailto:StefanDeusch@computer.org">Stefan Deusch</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */

public interface Constants {

    // PDL vars
    String FOLDER_ID = "folderID";
    String IS_LOCKED   = "isLocked";
    String IS_MOUNTED   = "isMounted";
    String LAST_MODIFIED = "lastModified";
    String MODIFYING_USER = "modifyingUser";
    String MIME_TYPE_LABEL = "mimeTypeDescription";
    String NAME        = "name";
    String ABS_PATH    = "absPath";
    String NUM_FILES   = "numFiles";
    String REPOSITORY_ID = "repositoryID";
    String RESOURCE_ID  = "resourceID";
    String SIZE        = "size";
    String TYPE        = "mimeType";
    String IS_FOLDER   = "isFolder";

    // PDL queries
    String GET_ALL_TREES = "com.arsdigita.docrepo.getAllTreesView";
    String GET_REPOSITORIES = "com.arsdigita.docrepo.getRepositoriesView";
    String GET_REPOSITORIES_ROOTS = "com.arsdigita.docrepo.getRepositoryRoots";
    String GET_CHILDREN = "com.arsdigita.docrepo.getChildren";

    // PDL associations
    String FILES   = "files";
    String FOLDERS = "folders";

    /**
     * The XML namespace.
     */
    String DOCS_XML_NS = "http://www.arsdigita.com/docs-ui/1.0";

    /**
     * Globalization resource
     */
    String BUNDLE_NAME = "com.arsdigita.docrepo.DRResources";

    /**
     * Global state parameters.
     */
    String ROOTFOLDER_ID_PARAM_NAME = "r_id";
    BigDecimalParameter ROOTFOLDER_ID_PARAM = new BigDecimalParameter(ROOTFOLDER_ID_PARAM_NAME);

    String SEL_FOLDER_ID_PARAM_NAME = "f_id";
    BigDecimalParameter SEL_FOLDER_ID_PARAM  = new BigDecimalParameter(SEL_FOLDER_ID_PARAM_NAME);

    String FILE_ID_PARAM_NAME = "d_id";
    BigDecimalParameter FILE_ID_PARAM = new BigDecimalParameter(FILE_ID_PARAM_NAME);

    /**
     * DM Index page title
     */
    Label PAGE_TITLE_LABEL  = new Label
            (new GlobalizedMessage("ui.page.title", BUNDLE_NAME));

    /**
     * DM File Info Page
     */
    Label FILE_INFO_LABEL = new Label
            (new GlobalizedMessage("ui.fileinfo.title", BUNDLE_NAME));

    // File Info Navigational Tabs
    Label FILE_INFO_PROPERTIES_TITLE = new Label
            (new GlobalizedMessage("ui.fileinfo.properties.title", BUNDLE_NAME));

    Label FILE_INFO_HISTORY_TITLE = new Label
            (new GlobalizedMessage("ui.fileinfo.history.title", BUNDLE_NAME));

    Label FILE_INFO_COMMENTS_TITLE = new Label
            (new GlobalizedMessage("ui.fileinfo.comments.title", BUNDLE_NAME));

    Label FILE_INFO_LINKS_TITLE = new Label
            (new GlobalizedMessage("ui.fileinfo.links.title", BUNDLE_NAME));

    Label GO_BACK_LABEL = new Label
            (new GlobalizedMessage("ui.fileinfo.goback.label", BUNDLE_NAME));

    /**
     * Navigational dimensional bar
     */
    Label MY_WORKSPACE_LABEL = new Label
            (new GlobalizedMessage("ui.workspace.title", BUNDLE_NAME));

    Label SIGN_OUT_LABEL = new Label
            (new GlobalizedMessage("ui.nav.signout", BUNDLE_NAME));

    Label HELP_LABEL = new Label
            (new GlobalizedMessage("ui.nav.help", BUNDLE_NAME));


    /**
     * Page navigational tabs
     */
    Label WS_BROWSE_TITLE = new Label
            (new GlobalizedMessage("ui.workspace.browse.title", BUNDLE_NAME));

    Label WS_SEARCH_TITLE = new Label
            (new GlobalizedMessage("ui.workspace.search.title", BUNDLE_NAME));

    Label WS_REPOSITORIES_TITLE = new Label
            (new GlobalizedMessage("ui.workspace.repositories.title", BUNDLE_NAME));

    /**
     * One Folder content
     */
    Label FOLDER_INFORMATION_HEADER = new Label
            (new GlobalizedMessage("ui.folder.content.header", BUNDLE_NAME));

    /**
     * Repositories
     */
    Label REPOSITORIES_INFORMATION_HEADER = new Label
            (new GlobalizedMessage("ui.repositories.content.header", BUNDLE_NAME));

    GlobalizedMessage REPOSITORY_RECENTDOCS_EMPTY
            = new GlobalizedMessage("ui.repositories.recentDocs.empty", BUNDLE_NAME);

    /**
     * File Uplaod Form
     */
    Label FILE_UPLOAD_FORM_HEADER = new Label
            (new GlobalizedMessage("ui.file.upload.header", BUNDLE_NAME));

    /**
     *  Folder Create Form
     */
    Label FOLDER_CREATE_FORM_HEADER = new Label
            (new GlobalizedMessage("ui.folder.create.header", BUNDLE_NAME));

    /**
     *  File Properties
     */
    Label FILE_PROPERTIES_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.properties.header", BUNDLE_NAME));

    /**
     * File Edit Panel
     */
    Label FILE_EDIT_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.edit.header", BUNDLE_NAME));

    GlobalizedMessage FILE_EDIT_ACTION_DESCRIPTION =
            new GlobalizedMessage("ui.fileinfo.edit.action.description", BUNDLE_NAME);

    /**
     * File Upload Panel
     */
    Label FILE_UPLOAD_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.upload.header", BUNDLE_NAME));

    GlobalizedMessage FILE_UPLOAD_INITIAL_TRANSACTION_DESCRIPTION =
            new GlobalizedMessage("ui.fileinfo.upload.initialversion.description", BUNDLE_NAME);

    /**
     * File Download Panel
     */
    Label FILE_DOWNLOAD_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.download.header", BUNDLE_NAME));

    /**
     * File-Send-to-Colleague Form
     */
    Label FILE_SEND_COLLEAGUE_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.sendcolleague.header", BUNDLE_NAME));

    /**
     * File-Delete Form
     */
    Label FILE_DELETE_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.delete.header", BUNDLE_NAME));

    /**
     * File Action Panel
     */
    Label FILE_ACTION_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.actions.header", BUNDLE_NAME));

    /**
     * File Revision History Panel
     */

    Label FILE_REVISION_HISTORY_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.history.header", BUNDLE_NAME));


    /**
     * File Feedback Panel
     */

    Label FILE_FEEDBACK_HEADER = new Label
            (new GlobalizedMessage("ui.fileinfo.feedback.header", BUNDLE_NAME));


    /**
     * Action Panel Constants
     */
    Label DESTINATION_FOLDER_PANEL_HEADER = new Label(
            new GlobalizedMessage("ui.folder.destination.list.header", BUNDLE_NAME));

    Label FOLDER_EMPTY_LABEL = new Label(
            new GlobalizedMessage("ui.folder.empty", BUNDLE_NAME));

    GlobalizedMessage  FOLDER_NEW_FOLDER_LINK =
            new GlobalizedMessage("ui.action.newfolder", BUNDLE_NAME);

    GlobalizedMessage  FOLDER_NEW_FILE_LINK =
            new GlobalizedMessage("ui.action.newfile", BUNDLE_NAME);

    Label ACTION_CUT_LABEL = new Label(
            new GlobalizedMessage("ui.action.edit.cut",  BUNDLE_NAME));

    Label ACTION_COPY_LABEL = new Label(
            new GlobalizedMessage("ui.action.edit.copy",  BUNDLE_NAME));

    Label ACTION_DELETE_LABEL = new Label(
            new GlobalizedMessage("ui.action.edit.delete", BUNDLE_NAME));

    GlobalizedMessage ACTION_DELETE_CONFIRM =
            new GlobalizedMessage("ui.action.confirm.delete", BUNDLE_NAME);

    Label ACTION_ERROR_LABEL = new Label(
            new GlobalizedMessage("ui.action.error", BUNDLE_NAME));

    Label ACTION_ERROR_CONTINUE = new Label(
            new GlobalizedMessage("ui.action.error.continue", BUNDLE_NAME));

    String ACTION_CUT_VALUE = "resource-cut";
    String ACTION_COPY_VALUE = "resource-copy";
    String ACTION_DELETE_VALUE = "resource-delete";

    GlobalizedMessage ACTION_DELETE_SUBMIT =
            new GlobalizedMessage("ui.action.submit.delete", BUNDLE_NAME);

    GlobalizedMessage ACTION_COPY_SUBMIT =
            new GlobalizedMessage("ui.action.submit.copy", BUNDLE_NAME);

    GlobalizedMessage ACTION_MOVE_SUBMIT =
            new GlobalizedMessage("ui.action.submit.move", BUNDLE_NAME);


    /**
     * Portlet Panel Constants
     */
    GlobalizedMessage  ROOT_ADD_RESOURCE_LINK =
            new GlobalizedMessage("ui.action.portlet.newresource", BUNDLE_NAME);

    String ROOT_ADD_DOC_PARAM_NAME = "root_add_doc";
    StringParameter ROOT_ADD_DOC_PARAM =
            new StringParameter(ROOT_ADD_DOC_PARAM_NAME);


    /**
     * File Action Panel Constants
     */
    GlobalizedMessage  FILE_EDIT_LINK  =
            new GlobalizedMessage("ui.fileinfo.edit.link", BUNDLE_NAME);

    GlobalizedMessage  FILE_NEW_VERSION_LINK  =
            new GlobalizedMessage("ui.fileinfo.newversion.link", BUNDLE_NAME);

    GlobalizedMessage  FILE_DOWNLOAD_LINK  =
            new GlobalizedMessage("ui.fileinfo.download.link", BUNDLE_NAME);

    GlobalizedMessage  FILE_SEND_COLLEAGUE_LINK  =
            new GlobalizedMessage("ui.fileinfo.sendcolleague.link", BUNDLE_NAME);

    GlobalizedMessage  FILE_DELETE_LINK  =
            new GlobalizedMessage("ui.fileinfo.delete.link", BUNDLE_NAME);


    /**
     * Error messages
     */
    GlobalizedMessage FOLDER_PARENTNOTFOUND_ERROR =
            new GlobalizedMessage("ui.error.parentnotfound", BUNDLE_NAME);

    GlobalizedMessage RESOURCE_EXISTS_ERROR =
            new GlobalizedMessage("ui.error.resourceexists", BUNDLE_NAME);

    GlobalizedMessage EMAIL_INVALID_ERROR =
            new GlobalizedMessage("ui.email.formatinvalid", BUNDLE_NAME);

    GlobalizedMessage DIFFERENT_MIMETYPE_ERROR =
            new GlobalizedMessage("ui.error.mimetype", BUNDLE_NAME);


    /**
     * FILE DELETE link
     */
    GlobalizedMessage  FILE_DELETE_CONFIRM =
            new GlobalizedMessage("ui.file.confirm.delete", BUNDLE_NAME);

    // Labels for Files
    GlobalizedMessage FILE_NAME =
            new GlobalizedMessage("ui.file.name", BUNDLE_NAME);

    GlobalizedMessage FILE_NAME_REQUIRED =
            new GlobalizedMessage("ui.file.name.required", BUNDLE_NAME);

    GlobalizedMessage FILE_UPLOAD_ADD_FILE =
            new GlobalizedMessage("ui.file.upload", BUNDLE_NAME);

    GlobalizedMessage FILE_SOURCE =
            new GlobalizedMessage("ui.file.source", BUNDLE_NAME);

    GlobalizedMessage FILE_DESCRIPTION =
            new GlobalizedMessage("ui.file.description", BUNDLE_NAME);

    GlobalizedMessage FILE_VERSION_DESCRIPTION =
            new GlobalizedMessage("ui.file.version.description", BUNDLE_NAME);

    GlobalizedMessage FILE_KEYWORDS =
            new GlobalizedMessage("ui.file.keywords", BUNDLE_NAME);

    GlobalizedMessage FILE_SAVE =
            new GlobalizedMessage("ui.file.save", BUNDLE_NAME);

    GlobalizedMessage FILE_SUBMIT =
            new GlobalizedMessage("ui.file.submit", BUNDLE_NAME);

    GlobalizedMessage CANCEL =
            new GlobalizedMessage("ui.action.cancel", BUNDLE_NAME);

    /**
     * Folder parameters
     */
    String FOLDER_NAME = "folder-name";
    String FOLDER_DESCRIPTION = "folder-description";

    Label FOLDER_NAME_LABEL = new  Label(
            new GlobalizedMessage("ui.folder.name", BUNDLE_NAME));

    Label FOLDER_DESCRIPTION_LABEL = new  Label(
            new GlobalizedMessage("ui.folder.description", BUNDLE_NAME));

    GlobalizedMessage FOLDER_SAVE =
            new GlobalizedMessage("ui.folder.save", BUNDLE_NAME);

    /**
     * Repsitories Selection Form
     */
    GlobalizedMessage REPOSITORIES_MOUNTED_SAVE =
            new GlobalizedMessage("ui.repositories.mounted.save", BUNDLE_NAME);

    /**
     * Send to colleague form variables.
     */
    Label SEND_FRIEND_FORM_EMAIL_SUBJECT = new Label(
            new GlobalizedMessage("ui.send.friend.email.subject", BUNDLE_NAME));

    Label SEND_FRIEND_FORM_EMAIL_LIST = new Label(
            new GlobalizedMessage("ui.send.friend.email.list", BUNDLE_NAME));

    Label SEND_FRIEND_FORM_DESCRIPTION = new Label(
            new GlobalizedMessage("ui.send.friend.description", BUNDLE_NAME));

    GlobalizedMessage SEND_FRIEND_FORM_SUBMIT =
            new GlobalizedMessage("ui.send.friend.submit", BUNDLE_NAME);
}
