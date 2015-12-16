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

import com.arsdigita.bebop.ActionLink;
import com.arsdigita.bebop.BoxPanel;
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.GridPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.List;
import com.arsdigita.bebop.ModalContainer;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.Tree;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.ChangeEvent;
import com.arsdigita.bebop.event.ChangeListener;
import com.arsdigita.bebop.event.RequestEvent;
import com.arsdigita.bebop.event.RequestListener;
import com.arsdigita.bebop.list.ListCellRenderer;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.xml.Element;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.Resource;
import org.libreccm.docrepo.ResourceRepository;

import java.math.BigDecimal;
import java.util.ArrayList;

/**
 * User Interface component of the Document Repository application which
 * which serves as entry-point and navigation tool of the repositories
 * that a user has subscribed too.
 * The tree of all subscribed repositories is on the left side, the full
 * listing of the currently selected directory is on the right side.
 *
 * @author <a href="mailto:StefanDeusch@computer.org">Stefan Deusch</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
public class BrowsePane extends ModalContainer implements Constants, ChangeListener, RequestListener {

    //private static final Logger log = Logger.getLogger(BrowsePane.class);

    private Component m_folderContent;
    private Component m_destinationFolderPanel;
    private Component m_newFileForm;
    private Component m_newFolderForm;
    private Component m_errorMsgPanel;
    private DestinationFolderForm m_destinationFolderForm;
    private ErrorMessageLabel m_errorMsgLabel;
    private Container m_segmentPanelBrowseContainer;

    private Tree m_tree;

    private BigDecimalParameter m_rootFolderIdParam = new BigDecimalParameter(ROOTFOLDER_ID_PARAM_NAME);
    private BigDecimalParameter m_selFolderIdParam  = new BigDecimalParameter(SEL_FOLDER_ID_PARAM_NAME);
    private BigDecimalParameter m_fileIdParam = new BigDecimalParameter(FILE_ID_PARAM_NAME);
    private StringParameter m_rootAddDocParam= new StringParameter(ROOT_ADD_DOC_PARAM_NAME);

    /**
     * Default constructor.
     */
    public BrowsePane() {
        m_segmentPanelBrowseContainer = new BoxPanel();
        m_segmentPanelBrowseContainer.setClassAttr("sidebarNavPanel");

        BoxPanel leftSide = new BoxPanel();
        leftSide.setClassAttr("navbar");
        m_tree = new SuperTree(new RepositoriesSuperTreeModel());
        m_tree.addChangeListener(this);
        leftSide.add(m_tree);

        m_segmentPanelBrowseContainer.add(leftSide);

        // Create all panels on the right side.
        SegmentedPanel rightSide = new SegmentedPanel();
        rightSide.setClassAttr("segmentPanel");
        m_folderContent = makeContentPanel(rightSide);
        m_segmentPanelBrowseContainer.add(rightSide);

        // Add all forms and panels to the container
        m_newFileForm = makeFileUploadForm();
        m_segmentPanelBrowseContainer.add(m_newFileForm);

        m_newFolderForm = makeFolderCreateForm();
        m_segmentPanelBrowseContainer.add(m_newFolderForm);

        m_destinationFolderPanel = makeExpandFolderPanel();
        m_segmentPanelBrowseContainer.add(m_destinationFolderPanel);

        m_errorMsgPanel = makeErrorMsgPanel();
        m_segmentPanelBrowseContainer.add(m_errorMsgPanel);
    }

    /**
     * Register the page the fist time.
     *
     * @param page The page to be registered.
     */
    @Override
    public void register(Page page) {
        page.addGlobalStateParam(m_rootFolderIdParam);
        page.addGlobalStateParam(m_selFolderIdParam);
        page.addGlobalStateParam(m_rootAddDocParam);
        page.addGlobalStateParam(m_fileIdParam);

        page.addRequestListener(this);

        super.register(page);
    }

    /**
     * Checks if a folder is selected in the page state and consequently
     * hides or shows the Folder Contents or the Folder Action panels.
     * 
     * @param event The request event.
     */
    public void pageRequested(RequestEvent event) {
        PageState state = event.getPageState();

        Long fileId = ((BigDecimal) state.getValue(m_fileIdParam)).longValue();

        boolean display = false;
        String key = (String) m_tree.getSelectedKey(state);

        // start out with root folder selected and open
        if (key == null) {
            key =  Utils.getRootFolder(state).getID().toString();
            m_tree.setSelectedKey(state, key);
            display = true;
        }

        // need this only when coming from 1-file page
        if (fileId != null) {
            final CdiUtil cdiUtil = new CdiUtil();
            final ResourceRepository resourceRepository = cdiUtil.findBean(
                    ResourceRepository.class);
            final File file = (File) resourceRepository.findById(fileId);

            Resource parent = file.getParent();
            key = Long.toString(parent.getObjectId());

            while (!parent.isRoot()) {
                parent = parent.getParent();
                String nextKey = Long.toString(parent.getObjectId());
                m_tree.expand(nextKey, state);
            }

            // to display this file's folder in the table
            m_tree.setSelectedKey(state, key);

            // now wipe out file param to avoid trouble elsewhere
            state.setValue(m_fileIdParam, null);
        }

        // finally expand selected folder
        m_tree.expand(key, state);

        if (display) {
            if( "t".equalsIgnoreCase(((String)state.getValue(m_rootAddDocParam)))) {
                // Entry hook to display FileUpload Form for Root folder
                displayFileUpload(state);
            } else {
                displayFolderContentPanel(state);
            }
        }
    }

    /**
     * Helper method to communicate selected folder ID to sub components.
     * Return only non-null after tree has been displayed at least once.
     * 
     * @param state The page state.
     *              
     * @return The folder id.
     */
    public BigDecimal getFolderID(PageState state) {
        return new BigDecimal((String) m_tree.getSelectedKey(state));
    }

    /**
     * Implementation of the change listener, clicking on the folder
     * loads the directory on the right side.
     * 
     * @param event The change event. e.g. clicking.
     */
    public void stateChanged(ChangeEvent event) {
        PageState state = event.getPageState();

        // Display folder on the right side corresponding to the key
        displayFolderContentPanel(state);
    }

    /**
     * Build a panel to display the Folder content of the selected Folder
     * and add it as a segment to the passed Segmented Panel
     * 
     * @param segmentPanel The segment panel.
     *
     * @return The segment panel with the added segment as a component.
     */
    private Component makeContentPanel(SegmentedPanel segmentPanel) {

        Label folder_info_header = new Label
                (new GlobalizedMessage("ui.folder.content.header", BUNDLE_NAME));
        folder_info_header.addPrintListener(
                new FolderNamePrintListener(m_tree));

        ActionLink newFileLink = new ActionLink(new Label(FOLDER_NEW_FILE_LINK));
        newFileLink.setClassAttr("actionLink");

        ActionLink newFolderLink =
                new ActionLink(new Label(FOLDER_NEW_FOLDER_LINK));
        newFolderLink.setClassAttr("actionLink");

        newFileLink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayFileUpload(e.getPageState());
            }
        });

        newFolderLink.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayFolderCreate(e.getPageState());
            }
        });

        GridPanel folderGrid = new GridPanel(1);
        SimpleContainer pane = new SimpleContainer();
        pane.add(newFileLink);
        pane.add(new Label("        "));
        pane.add(newFolderLink);
        folderGrid.add(pane, GridPanel.RIGHT | GridPanel.BOTTOM);
        folderGrid.add(new FolderContentsTableForm(this, m_tree),
                GridPanel.LEFT | GridPanel.BOTTOM);

        return segmentPanel.addSegment(folder_info_header,
                folderGrid);
    }

    /**
     * Builds the file upload form.
     *
     * @return A component with the build file upload form.
     */
    private Component makeFileUploadForm() {
        GridPanel gridPanel = new GridPanel(1);

        Label fileUploadFormHeaderLabel = new Label(new GlobalizedMessage("ui.file.upload.header", BUNDLE_NAME));
        fileUploadFormHeaderLabel.addPrintListener(new FolderNamePrintListener(m_tree));

        gridPanel.add(fileUploadFormHeaderLabel);
        gridPanel.add(new FileUploadForm(this, m_tree));

        return gridPanel;
    }

    /**
     * Builds the folder create form.
     *
     * @return A component with the build folder create form.
     */
    private Component makeFolderCreateForm() {
        GridPanel gridPanel = new GridPanel(1);
        Label folderCreateFormHeaderLabel = new Label(new GlobalizedMessage("ui.folder.create.header", BUNDLE_NAME));
        folderCreateFormHeaderLabel.addPrintListener(new FolderNamePrintListener(m_tree));

        gridPanel.add(folderCreateFormHeaderLabel);
        gridPanel.add(new FolderCreateForm(this, m_tree));

        return gridPanel;
    }

    /**
     * Builds the destination folders tree.
     *
     * @return A component with the destination folder tree.
     */
    private Component makeExpandFolderPanel() {
        GridPanel gridPanel = new GridPanel(1);
        m_destinationFolderForm = new DestinationFolderForm(this);

        gridPanel.add(DESTINATION_FOLDER_PANEL_HEADER);
        gridPanel.add(m_destinationFolderForm);

        return gridPanel;
    }

    /**
     * Builds the panel to display error messages when copy/move failed.
     *
     * @return A component with the panel to display error messages.
     */
    private Component makeErrorMsgPanel() {
        ColumnPanel columnPanel = new ColumnPanel(1);
        m_errorMsgLabel = new ErrorMessageLabel();
        columnPanel.add(m_errorMsgLabel);

        ActionLink link = new ActionLink(ACTION_ERROR_CONTINUE);
        link.setClassAttr("actionLink");
        link.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayFolderContentPanel(e.getPageState());
            }
        });

        columnPanel.add(link);
        return columnPanel;
    }

    /**
     * Display the file upload form.
     *
     * @param state The page state
     */
    public void displayFileUpload(PageState state) {
        m_newFileForm.setVisible(state, true);
    }

    /**
     * Display the folder create form.
     *
     * @param state The page state
     */
    public void displayFolderCreate(PageState state) {
        m_newFolderForm.setVisible(state, true);
    }

    /**
     * Display the folder content panel.
     *
     * @param state The page state
     */
    public void displayFolderContentPanel(PageState state) {
        m_segmentPanelBrowseContainer.setVisible(state, true);
    }

    /**
     * Display the folder destination panel.
     *
     * @param state The page state
     * @param resourceList A list of resources
     * @param isMove Weather its moved or copied
     */
    public void displayDestinationFolderPanel(PageState state,
                                              Object[] resourceList,
                                              boolean isMove) {

        m_destinationFolderPanel.setVisible(state, true);
        m_destinationFolderForm.setResourceList(state, resourceList);
        if (isMove) {
            m_destinationFolderForm.setMove(state);
        } else {
            m_destinationFolderForm.setCopy(state);
        }
    }

    /**
     * Display the error message panel.
     *
     * @param state The page state
     * @param action The file operation
     * @param list The list of messages
     */
    public void displayErrorMsgPanel(PageState state,
                                     String action,
                                     ArrayList list) {

        m_errorMsgLabel.setMessages(state, action, list);

        m_errorMsgPanel.setVisible(state, true);
    }


    /**
     * Error message panel that allows to set customized error
     * messages without showing a tomcat stacktrace.
     */
    private static class ErrorMessageLabel extends SimpleComponent implements Constants {

        private RequestLocal m_msgs;
        private RequestLocal m_action;

        /**
         * Constructor.
         */
        public ErrorMessageLabel() {
            m_msgs = new RequestLocal();
            m_action = new RequestLocal();

        }

        /**
         * Set list of file/folder that could not be delete/move/copy.
         *
         * @param state The page state
         * @param action The file operation (action, move, copy)
         * @param msgs The list of messages
         */
        public void setMessages(PageState state,
                                String action,
                                ArrayList msgs) {
            m_action.set(state, action);
            m_msgs.set(state, msgs);
        }

        @Override
        public void generateXML(PageState state, Element parent) {
            Element element = parent.newChildElement("docs:error-label",
                    DOCS_XML_NS);
            element.addAttribute("action", (String) m_action.get(state));

            ArrayList list = (ArrayList) m_msgs.get(state);

            if (list != null) {
                for (Object aList : list) {
                    Element item = element.newChildElement("docs:item", DOCS_XML_NS);
                    item.addAttribute("name", ((String) aList));
                }
            }
        }
    }

    /**
     * Table Cell Renderer that provides clickable Links to follow
     * directory links .
     *
     * Todo: inner private class never used..
     */
    private static class DirLinkRenderer implements ListCellRenderer {

        public Component getComponent(List list, PageState state,
                                      Object value,  String key,
                                      int index, boolean isSelected) {

            Link link = new Link((String)value,
                    "?" + SEL_FOLDER_ID_PARAM.getName() +
                            "=" + key);
            return link;
        }
    }
}
