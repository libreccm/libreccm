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
package com.arsdigita.cms.ui.revision;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.AbstractTableModelBuilder;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.cms.ui.item.ContentItemRequestLocal;
import com.arsdigita.globalization.GlobalizedMessage;

import org.libreccm.security.User;

import com.arsdigita.toolbox.ui.FormatStandards;

import org.libreccm.auditing.CcmRevision;
import org.libreccm.cdi.utils.CdiUtil;
import org.librecms.CmsConstants;
import org.librecms.contentsection.ContentItem;
import org.librecms.contentsection.ContentItemRepository;

import java.util.List;

/**
 * @author Stanislav Freidin &lt;sfreidin@redhat.com&gt;
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id: RevisionTableModelBuilder.java 1942 2009-05-29 07:53:23Z terry
 * $
 */
class RevisionTableModelBuilder extends AbstractTableModelBuilder {

    static final int FROM = 0;
    static final int TO = 1;
    static final int TIMESTAMP = 2;
    static final int USER = 3;
    static final int DESCRIPTION = 4;
    static final int PREVIEW = 5;
    static final int ROLLBACK = 6;

    static final int COLUMNS = 7;

    private final ContentItemRequestLocal itemRequestLocal;

    RevisionTableModelBuilder(final ContentItemRequestLocal itemRequestLocal) {
        this.itemRequestLocal = itemRequestLocal;
    }

    @Override
    public final TableModel makeModel(final Table table,
                                      final PageState state) {
        return new Model(itemRequestLocal.getContentItem(state));
    }

    private static class Model implements TableModel {

        private final List<CcmRevision> revisions;
        private CcmRevision currentRevision;
        private long count = 0;
        private long last = 2;
        private int index = -1;

        public Model(final ContentItem item) {
            final ContentItemRepository itemRepository = CdiUtil
                .createCdiUtil()
                .findBean(ContentItemRepository.class);
            revisions = itemRepository.retrieveRevisions(item,
                                                         item.getObjectId());

            last = revisions.size() + 2;
        }

        @Override
        public final int getColumnCount() {
            return COLUMNS;
        }

        @Override
        public final boolean nextRow() {
            count++;
            index++;

            if (count == 1) {
                return true;
            } else if (count == last) {
                return true;
            } else if (revisions.size() < index) {
                currentRevision = revisions.get(index);

                return true;
            } else {
                return false;
            }
        }

        @Override
        public final Object getElementAt(final int column) {
            if (count == 1) {
                switch (column) {
                    case TIMESTAMP:
                        return lz("cms.ui.item.revision.current");
                    default:
                        return "";
                }
            } else if (count == last) {
                switch (column) {
                    case TIMESTAMP:
                        return lz("cms.ui.item.revision.first");
                    default:
                        return "";
                }
            } else {
                switch (column) {
                    case TIMESTAMP:
                        return FormatStandards.formatDateTime(
                            currentRevision.getRevisionDate());
                    case USER:
                        
                        return currentRevision.getUserName();
                    case DESCRIPTION:
                        return "";
                    default:
                        return "";
                }
            }
        }

        @Override
        public final Object getKeyAt(final int column) {
            if (count == 1) {
                return "first";
            } else if (count == last) {
                return "last";
            } else {
                return currentRevision.getId();
            }
        }

    }

    private static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }

    private static String lz(final String key) {
        return (String) gz(key).localize();
    }

}
