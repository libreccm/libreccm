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
package com.arsdigita.cms.workflow;

/**
 * Class for generating a URL to the Authoring kit given the ID of the
 * ContentItem and the Task. Eventually we may have a separate kit for editors,
 * hence this is a separate class and has its own TaskType
 *
 * @author Uday Mathur (umathur@arsdigita.com)
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 */
public class EditingTaskURLGenerator implements TaskURLGenerator {

    public EditingTaskURLGenerator() {
    }

    /**
     * Generates a Link to the Workflow Tab in the Item Management part of the
     * CMS UI.
     *
     * @param itemId id of the item in question
     * @param taskId this param is ignored.
     *
     * @return
     *
     */
    @Override
    public String generateURL(final long itemId, final long taskId) {
//        final StringBuffer url = new StringBuffer
//            (ContentItemPage.getItemURL(itemId, ContentItemPage.WORKFLOW_TAB));
//
//        // XXX task, approve, and action were constants; restore them
//        url.append("&action=approve&task=").append(taskId.toString());
//
//        return url.toString();
        throw new UnsupportedOperationException("ToDo");
    }

}
