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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.RequestLocal;
import com.arsdigita.bebop.SegmentedPanel;
import com.arsdigita.bebop.SimpleContainer;
import org.libreccm.docrepo.File;
import org.apache.log4j.Logger;

/**
 * This component shows the version history of a document. It allows
 * to download historical versions.
 *
 * @author <a href="mailto:StefanDeusch@computer.org">Stefan Deusch</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
public class FileInfoHistoryPane extends SimpleContainer implements Constants {

    private static final Logger log = Logger.getLogger(
            FileInfoHistoryPane.class);

    private Component m_history;
    // share file instance for all sub components
    private RequestLocal requestLocal;

    /**
     * Constructor. Constructs the info-history pane for a file.
     */
    public FileInfoHistoryPane() {

        requestLocal = new DocRepoRequestLocal();

        SegmentedPanel main = new SegmentedPanel();
        main.setClassAttr("main");

        m_history = makeHistoryPane(main);

        add(main);
    }

    /**
     * Creates a new history pane.
     *
     * @param panel The segment panel.
     * @return The new segment panel with a created history pane.
     */
    private Component makeHistoryPane(SegmentedPanel panel) {
        return panel.addSegment(FILE_REVISION_HISTORY_HEADER,
                new FileRevisionsTable(this));
    }

    /**
     * Registers the given page.
     *
     * @param page The page.
     */
    @Override
    public void register(Page page) {
        page.addGlobalStateParam(FILE_ID_PARAM);
        super.register(page);
    }

    /**
     * Returns a file.
     *
     * @param state The page state
     * @return A file
     */
    public File getFile(PageState state) {
        return (File) requestLocal.get(state);
    }
}
