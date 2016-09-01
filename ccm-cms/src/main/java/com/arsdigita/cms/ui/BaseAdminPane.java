/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.SingleSelectionModel;
import com.arsdigita.bebop.list.ListModelBuilder;
import com.arsdigita.bebop.tree.TreeModelBuilder;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.toolbox.ui.SelectionPanel;
import org.apache.log4j.Logger;
import org.librecms.CmsConstants;

/**
 * A base component for use in CMS admin panes.
 *
 * @author Justin Ross &lt;jross@redhat.com&gt;
 */
public abstract class BaseAdminPane extends SelectionPanel {

    /** Internal logger instance to faciliate debugging. Enable logging output
     *  by editing /WEB-INF/conf/log4j.properties int the runtime environment
     *  and set com.arsdigita.cms.ui.BaseAdminPane=DEBUG 
     *  by uncommenting or adding the line.                                   */
    private static final Logger s_log = Logger.getLogger(BaseAdminPane.class);

    protected BaseAdminPane() {
        super();
    }

    protected BaseAdminPane(final Component title,
                            final Component selector) {
        super(title, selector);
    }

    protected BaseAdminPane(final GlobalizedMessage title,
                            final Component selector) {
        super(title, selector);
    }

    protected BaseAdminPane(final Component title,
                            final Component selector,
                            final SingleSelectionModel model) {
        super(title, selector, model);
    }

    protected BaseAdminPane(final GlobalizedMessage title,
                            final Component selector,
                            final SingleSelectionModel model) {
        super(title, selector, model);
    }

    protected BaseAdminPane(final Component title,
                            final ListModelBuilder builder) {
        super(title, builder);
    }

    protected BaseAdminPane(final GlobalizedMessage title,
                            final ListModelBuilder builder) {
        super(title, builder);
    }

    protected BaseAdminPane(final Component title,
                            final TreeModelBuilder builder) {
        super(title, builder);
    }

    protected BaseAdminPane(final GlobalizedMessage title,
                            final TreeModelBuilder builder) {
        super(title, builder);
    }

    protected static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    protected static String lz(final String key) {
        return (String) gz(key).localize();
    }
}
