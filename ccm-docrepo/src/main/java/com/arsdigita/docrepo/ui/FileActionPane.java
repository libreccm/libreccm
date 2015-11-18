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
import com.arsdigita.bebop.ColumnPanel;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.event.ActionEvent;
import com.arsdigita.bebop.event.ActionListener;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.dispatcher.DispatcherHelper;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.web.Web;
import org.apache.log4j.Logger;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.ResourceRepository;

import java.io.IOException;


/**
 * This component shows the meta data of a file with links to administrative
 * actions on it.
 *
 * @author <a href="mailto:StefanDeusch@computer.org">Stefan Deusch</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
public class FileActionPane extends ColumnPanel implements Constants {

    private static final Logger log = Logger.getLogger(FileActionPane.class);

    private FileInfoPropertiesPane m_parent;
    private RequestLocal m_fileData;
    private Link m_download;
    private ActionLink m_newVersion;
    private ActionLink m_email;
    private ActionLink m_delete;

    /**
     * Constructor. Initializes the action pane for the given info properties.
     *
     * @param parent The info property pane
     */
    public FileActionPane(FileInfoPropertiesPane parent) {
        super(1);

        m_parent = parent;

        m_fileData = new RequestLocal() {
            protected Object initialValue(PageState state) {
                File file = null;
                Long fileId = (Long) state.getValue
                        (FILE_ID_PARAM);

                final CdiUtil cdiUtil = new CdiUtil();
                final ResourceRepository resourceRepository;
                try {
                    resourceRepository = cdiUtil.findBean(ResourceRepository.class);
                    file = (File) resourceRepository.findById(fileId);
                    if (file == null) {
                        log.error(String.format("Couldn't find the file %d in" +
                                " the database", fileId));
                    }
                } catch(CdiLookupException ex) {
                    log.error("Failed to find bean for the " +
                            "ResourceRepository", ex);
                }
                return file;
            }
        };

        m_newVersion = addActionLink(FILE_NEW_VERSION_LINK);

        PrintListener printListener = new PrintListener() {
            public void prepare(PrintEvent event) {
                Link link = (Link) event.getTarget();
                PageState state = event.getPageState();
                File file = getFile(state);
                link.setTarget(String.format("download/%s?%s=%d", file.getName(),
                        FILE_ID_PARAM.getName(), file.getObjectId()));
            }
        };

        m_download = new Link(new Label(FILE_DOWNLOAD_LINK),
                printListener);
        m_download.setClassAttr("actionLink");
        add(m_download);

        m_email  = addActionLink(FILE_SEND_COLLEAGUE_LINK);
        m_delete = addActionLink(FILE_DELETE_LINK);

        m_newVersion.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_parent.displayUploadForm(e.getPageState());
            }
        });

        m_email.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                m_parent.displaySendColleagueForm(e.getPageState());

            }
        });

        m_delete.addActionListener(new DeleteListener());
        m_delete.setConfirmation(FILE_DELETE_CONFIRM);
    }

    /**
     * Adds the action link to this file action pane.
     *
     * @param msg Label of the action link
     *
     * @return The action link
     */
    private ActionLink addActionLink(GlobalizedMessage msg) {
        ActionLink actionlink = new ActionLink(new Label(msg));
        actionlink.setClassAttr("actionLink");
        this.add(actionlink);
        return actionlink;
    }

    /**
     * Return file initialized in RequestLocal
     *
     * @param state The page state
     *
     * @return The initialized file
     */
    private File getFile(PageState state) {
        return (File) m_fileData.get(state);
    }

    /**
     * Private inner class. DeleteListener of a file.
     */
    private final class DeleteListener implements ActionListener {

        /**
         * Method get triggered, if the delete action has been perfomed.
         *
         * @param event The delete event
         */
        public void actionPerformed(ActionEvent event) {
            PageState state = event.getPageState();
            final File file = getFile(state);
            Long parentFolderId = file.getParent().getObjectId();

            final CdiUtil cdiUtil = new CdiUtil();
            final ResourceRepository resourceRepository;
            try {
                resourceRepository = cdiUtil.findBean(ResourceRepository.class);

                // Todo: replace KernelExcursion
//                KernelExcursion ex = new KernelExcursion() {
//                    protected void excurse() {
//                        setEffectiveParty(Kernel.getSystemParty());
//                        resourceRepository.delete(file);
//                    }
//                };
//                ex.run();

            } catch (CdiLookupException ex) {
                log.error("Failed to find bean for the ResourceRepository.", ex);
            }

            try {
                String appURI = getRedirectURI(state);

                DispatcherHelper.sendRedirect(state.getRequest(),
                        state.getResponse(), String.format("%s?%s=%d",
                                appURI, SEL_FOLDER_ID_PARAM.getName(),
                                parentFolderId));
            } catch(IOException iox) {
                log.error("Failed to send the url for the redirect.", iox);
            }
        }

        /**
         * Gets the redirecting uri through a given page state.
         *
         * @param state The page state
         *
         * @return The new uri
         */
        private String getRedirectURI(PageState state) {
            String appURI = state.getRequestURI();
            log.debug(String.format("Original app URI: %s", appURI));
            int idx = appURI.indexOf("/file/");
            appURI = appURI.substring(0, idx);

            final String servletPath = Web.getConfig().getDispatcherServletPath();
            if (appURI.startsWith(servletPath)) {
                appURI = appURI.substring(servletPath.length());
            }

            log.debug(String.format("New URI: %s", appURI));
            return appURI;
        }
    }
}
