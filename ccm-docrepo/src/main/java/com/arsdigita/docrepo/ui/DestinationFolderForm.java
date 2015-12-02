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

import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Form;
import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.FormInitListener;
import com.arsdigita.bebop.event.FormProcessListener;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.bebop.form.Hidden;
import com.arsdigita.bebop.form.RadioGroup;
import com.arsdigita.bebop.form.Submit;
import com.arsdigita.bebop.parameters.ArrayParameter;
import com.arsdigita.docrepo.util.GlobalizationUtil;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.docrepo.Folder;
import org.libreccm.docrepo.Repository;
import org.libreccm.docrepo.Resource;
import org.libreccm.docrepo.ResourceManager;
import org.libreccm.docrepo.ResourceRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Intermediate form of the "Move-to" and "Copy-to" process.
 * It shows the folder tree of the repositories expanded
 * with checkboxes next to it.
 *
 * @author <a href="mailto:ddao@arsdigita.com">David Dao</a>
 * @author <a href="mailto:stefan@arsdigita.com">Stefan Deusch</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
public class DestinationFolderForm extends Form implements FormInitListener,
        FormProcessListener, Constants {

    private static final Logger log = Logger.getLogger(DestinationFolderForm.class);

    private Hidden m_resourceList;
    private ExpandedFolderTree m_radioGroup;

    private Submit m_copySubmit;
    private Submit m_moveSubmit;
    private BrowsePane m_parent;

    /**
     * Constructor. Creates a destination folder form.
     *
     * @param parent The parent of the destination folder form
     */
    public DestinationFolderForm(BrowsePane parent) {
        super("Destination-Folder", new ColumnPanel(1));
        m_parent = parent;
        m_resourceList = new Hidden(new ArrayParameter("resourceList"));
        add(m_resourceList);

        m_radioGroup = new ExpandedFolderTree();
        add(m_radioGroup);

        m_copySubmit = new Submit(GlobalizationUtil.globalize(
                "ui.action.submit.copy"));
        add(m_copySubmit);
        m_moveSubmit = new Submit(GlobalizationUtil.globalize(
                "ui.action.submit.copy"));
        add(m_moveSubmit);

        addInitListener(this);
        addProcessListener(this);
    }

    /**
     * <p>Adds a DOM subtree representing this component under the given
     * parent node.  Uses the request values stored in <code>state</code>.</p>
     *
     * @param ps represents the current request
     * @param elt the node under which the DOM subtree should be added
     */
    @Override
    public void generateXML(PageState ps, Element elt) {
        doSubmit(ps);
        super.generateXML(ps, elt);
    }

    /**
     * Does a submit on a given page state.
     *
     * @param ps The page state
     */
    private void doSubmit(PageState ps) {
        Object[] list = (Object[]) m_resourceList.getValue(ps);
        ArrayList l = new ArrayList();
        for (int i = 0; i < list.length; i++) {
            l.add(list[i]);
        }
        m_radioGroup.setSources(ps, l);
    }

    /**
     * Sets a resource list with the given list of objects.
     *
     * @param state The page state
     * @param list The list of objects
     */
    public void setResourceList(PageState state, Object[] list) {
        m_resourceList.setValue(state, list);
    }

    /**
     * Sets the copy submit to the page state.
     *
     * @param state The page state
     */
    public void setCopy(PageState state) {
        state.setVisible(m_moveSubmit, false);
        state.setVisible(m_copySubmit, true);
    }

    /**
     * Sets the move submit to the page state.
     *
     * @param state The page state
     */
    public void setMove(PageState state) {
        state.setVisible(m_moveSubmit, true);
        state.setVisible(m_copySubmit, false);
    }

    /**
     * Initializes the destination folder form, when triggered by an event.
     * Todo: fix usage of class Kernel
     *
     * @param event The section event to be triggered
     */
    public void init(FormSectionEvent event) {
//        if ( Kernel.getContext().getParty() == null ) {
//            Util.redirectToLoginPage(event.getPageState());
//        }
    }

    /**
     * Processes the destination folder form after it has been triggered.
     *
     * @param event The section event to be triggered
     *
     * @throws FormProcessException
     */
    public void process(FormSectionEvent event) throws FormProcessException {
        PageState state = event.getPageState();

        boolean isCopy = true;
        boolean isError = false;

        ArrayList errorList = new ArrayList();
        if (m_moveSubmit.isSelected(state)) {
            isCopy = false;
        }

        Long folderId = Long.valueOf((String) m_radioGroup.getValue(state));
        if (folderId == null) {
            throw new FormProcessException(GlobalizationUtil.globalize(
                    "ui.folder.choose_destination"));
        }

        final CdiUtil cdiUtil = new CdiUtil();
        final ResourceRepository resourceRepository;
        final ResourceManager resourceManager;
        try {
            resourceRepository = cdiUtil.findBean(ResourceRepository.class);
            resourceManager = cdiUtil.findBean(ResourceManager.class);

            Folder folder = (Folder) resourceRepository.findById(folderId);
            if (folder == null) {
                isError = true;
                log.error(String.format("Couldn't find folder %d in the " +
                        "database.", folderId));
            }

            String[] resourceStrings = (String[]) m_resourceList.getValue(state);
            for (String resourceString : resourceStrings) {
                Long resourceId = Long.valueOf(resourceString);

                Resource resource = resourceRepository.findById(resourceId);
                if (resource == null) {
                    errorList.add(resourceString);
                    log.debug(String.format("Couldn't find selected resource " +
                            "%d in the database.", resourceId));
                    continue;
                }

                // Knowledge, weather its a folder or a file is not necessary
                if (isCopy) {
                    resourceManager.copyToFolder(resource, folder);
                } else {
                    resource.setParent(folder);
                    resourceRepository.save(resource);
                }
            }
        } catch (CdiLookupException ex) {
            isError = true;
            log.error("Failed to find bean for either ResourceRepository or " +
                    "ResourceManager.", ex);
        }

        if (isError) {
            String action = isCopy ? "copy" : "move";
            m_parent.displayErrorMsgPanel(state, action, errorList);
        } else {
            m_parent.displayFolderContentPanel(state);
        }
    }

    /**
     * Create an expanded tree of all repositories and folder for given user.
     * Each folder has a checkbox to be selected as destination folder. The
     * parent folder is not selectable. This class should not be use
     * outside of document repository.
     */
    private class ExpandedFolderTree extends RadioGroup {

        // Exclusion list of folders.
        private RequestLocal m_srcResources;

        /**
         * Constructor of inner private class.
         */
        public ExpandedFolderTree() {
            super("resourceID");
            m_srcResources = new RequestLocal();
        }

        /**
         * Sets sources to the exclusion list of folders.
         *
         * @param state The page state
         * @param list  A list of sources
         */
        public void setSources(PageState state, ArrayList list) {
            m_srcResources.set(state, list);
        }

        /**
         * Generates xml for the destination folder form. Therefore retrieving
         * a list of all folders in a certain repository for Copy and Move
         * operations.
         * 
         * Todo: what does this method?
         * Todo: rewrite for ccm_ng
         *
         * @param state  The page state
         * @param parent The element
         */
        @Override
        public void generateXML(PageState state, Element parent) {
            Element treeElement = parent.newChildElement("bebop:tree", BEBOP_XML_NS);

            // Todo: notwendig?
            Folder sourceFolder = null;
            Long sourceFolderId = (m_parent.getFolderID(state)).longValue();

            final CdiUtil cdiUtil = new CdiUtil();
            final ResourceRepository resourceRepository;
            try {
                resourceRepository = cdiUtil.findBean(ResourceRepository.class);
                sourceFolder = (Folder) resourceRepository.findById
                        (sourceFolderId);
            } catch (CdiLookupException ex) {
                log.error("Failed to find bean for the ResourceRepository.", ex);
            }

            if (sourceFolder != null) {
                HashMap map = new HashMap();
                map.put(new Long("-1"), treeElement);

                Repository repository = (Repository) Web.
                        getWebContext().getApplication();

                Folder rootFolder = repository.getRootFolder();
                List<Resource> resources = rootFolder.getImmediateChildren();

                for (Resource resource : resources) {
                    if (resource.isFolder()) {
                        Long parentId = resource.getParent().getObjectId();
                        Long resourceId = resource.getObjectId();

                        Element parentElement = (Element) map.get(parentId);

                        if (parentElement != null) {
                            boolean isSelectable = resource.equals(sourceFolder);
                            map.put(resourceId, createNode(state, parentElement,
                                    isSelectable, (Folder) resource));
                        }
                    }
                }
            } else {
                log.error(String.format("Couldn't find the source folder %d " +
                        "in the database.", sourceFolderId));
            }
        }

        /**
         * Creates a new element node with given folder.
         *
         * @param state The page state
         * @param parent The elements parent
         * @param makeSelectable If new element needs to be selectable
         * @param folder The folder
         *
         * @return The new element node
         */
        private Element createNode(PageState state, Element parent,
                                   boolean makeSelectable, Folder folder) {

            Element element = parent.newChildElement("bebop:t_node", BEBOP_XML_NS);

            element.addAttribute("indentStart", "t");
            element.addAttribute("indentClose", "t");
            if (makeSelectable) {
                element.addAttribute("resourceID", String.valueOf(
                        folder.getObjectId()));
                element.addAttribute("radioGroup", "t");
                element.addAttribute("radioGroupName", getName());
            } else {
                element.addAttribute("radioGroup", "f");
            }

            Label label = new Label(new GlobalizedMessage(folder.getName()));
            label.generateXML(state, element);

            return element;
        }
    }
}
