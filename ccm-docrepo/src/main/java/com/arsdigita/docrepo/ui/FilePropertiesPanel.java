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

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.docrepo.util.GlobalizationUtil;
import com.arsdigita.web.ParameterMap;
import com.arsdigita.web.URL;
import com.arsdigita.xml.Element;
import org.apache.log4j.Logger;
import org.apache.shiro.authz.AuthorizationException;
import org.hibernate.envers.exception.NotAuditedException;
import org.libreccm.cdi.utils.CdiLookupException;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.docrepo.File;
import org.libreccm.docrepo.Resource;
import org.libreccm.docrepo.ResourceRepository;
import org.libreccm.security.PermissionChecker;
import org.libreccm.security.User;

import javax.activation.MimeType;
import javax.servlet.http.HttpServletRequest;

/**
 * A simple custom bebop component that summarizes the properties of a
 * file in tabular form.
 *
 * @author StefanDeusch@computer.org, ddao@arsdigita.com
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 * @version $Id: FilePropertiesPanel.java  pboy $
 */
public class FilePropertiesPanel extends SimpleComponent implements Constants {

    private static final Logger log = Logger.getLogger(
            FilePropertiesPanel.class);

    /**
     * Generates the XML for this container.
     *
     * @param state represents the current request
     * @param parent the parent XML element
     */
    @Override
    public void generateXML(PageState state, Element parent) {
        // Get file id.
        Long resourceId = (Long) state.getValue(FILE_ID_PARAM);
        Element element = parent.newChildElement("docs:file-info", DOCS_XML_NS);

        File file = null;
        final CdiUtil cdiUtil = new CdiUtil();
        ResourceRepository resourceRepository = null;
        final PermissionChecker permissionChecker;
        try {
            resourceRepository = cdiUtil.findBean(ResourceRepository.class);
            Resource resource = resourceRepository.findById(resourceId);
            file = resource.isFile() ? (File) resource : null;

            permissionChecker = cdiUtil.findBean(PermissionChecker.class);
            // checks if the subject has permissions granting the privilege to
            // 'read' the file
            permissionChecker.checkPermission("read", file);
        } catch (CdiLookupException ex) {
            log.error(GlobalizationUtil.globalize("beanFinder.fail" +
                    ".resourceRepository"), ex);
        } catch (AuthorizationException authEx) {
            log.error(GlobalizationUtil.globalize("ui.file.failure.privilege.read"),
                    authEx);
        }

        Element nameElement = element.newChildElement("docs:name", DOCS_XML_NS);
        nameElement.setText(file.getName());
        Element descriptionElement = element.newChildElement("docs:description",
                DOCS_XML_NS);
        String description = file.getDescription();
        if (description != null) {
            descriptionElement.setText(description);
        }

        Element sizeElement = element.newChildElement("docs:size", DOCS_XML_NS);
        sizeElement.setText(Utils.FileSize.formatFileSize(file.getSize(), state));
        Element typeElement = element.newChildElement("docs:type", DOCS_XML_NS);

        // Retrieve pretty name for a mime type.
        MimeType mimeType = file.getMimeType();
        typeElement.setText(mimeType.getBaseType());

        Element lastModifiedElement = element.newChildElement(
                "docs:last-modified", DOCS_XML_NS);
        lastModifiedElement.setText(Utils.DateFormat.format(
                file.getLastModifiedDate()));

        Element revisionElement = element.newChildElement("docs:revision",
                DOCS_XML_NS);

        long numRevs = 0; // if there aren't any revisions 0 is accurate
        if (resourceRepository != null) {
            try {
                numRevs = resourceRepository.retrieveRevisionNumbersOfEntity(
                        file, file.getObjectId()).size();
            } catch (IllegalArgumentException |
                    NotAuditedException |
                    IllegalStateException ex) {
                log.error(GlobalizationUtil.globalize("ui.file.failure" +
                        ".retrieve.revisionNumber"), ex);
            }
        }

        //deprecated: exchanged through the above
        //TransactionCollection tc = file.getTransactions();
        //long numRevs = tc.size();
        revisionElement.setText(numRevs + "");

        // Must allow for the possibility that not author is available.

        Element authorElement = element.newChildElement("docs:author",
                DOCS_XML_NS);
        User author = file.getCreationUser();
        if (author != null) {
            authorElement.setText(author.getName());
        } else {
            authorElement.setText("Unknown");
        }

        Element uriElement = element.newChildElement("docs:uri", DOCS_XML_NS);
        uriElement.setText(makeFileURL(file, state));
    }

    /**
     * Makes an url for a given file.
     *
     * @param file The file
     * @param state The page state
     *
     * @return The url to the file
     */
    private static String makeFileURL(File file, PageState state) {
        final HttpServletRequest req = state.getRequest();

        final ParameterMap params = new ParameterMap();
        params.setParameter(FILE_ID_PARAM.getName(), file.getObjectId());

        return URL.here(req, "/download/" + file.getName(), params).toString();
    }
}
