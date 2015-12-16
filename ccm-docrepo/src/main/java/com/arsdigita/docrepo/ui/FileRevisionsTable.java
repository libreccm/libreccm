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
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.event.TableActionEvent;
import com.arsdigita.bebop.event.TableActionListener;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.bebop.table.TableModel;
import com.arsdigita.bebop.table.TableModelBuilder;
import org.libreccm.docrepo.File;
import com.arsdigita.util.LockableImpl;
import org.apache.log4j.Logger;

/**
 * This component lists all file revisions in tabular form.
 * The right-most column has a button to download that particular
 * version.
 *
 * @author <a href="mailto:StefanDeusch@computer.org">Stefan Deusch</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */

public class FileRevisionsTable extends Table implements TableActionListener,
        Constants {

    private static Logger log = Logger.getLogger(FileRevisionsTable.class);

    private FileInfoHistoryPane m_parent;
    private static String[] s_tableHeaders =
            {"", "Author", "Date", "Comments", ""};

    /**
     * Constructor. Creates a file revision table for a given file info
     * history pane.
     *
     * @param parent The fileInfoHisoryPane
     */
    public FileRevisionsTable(FileInfoHistoryPane parent) {

        super(new FileRevisionsTableModelBuilder(parent), s_tableHeaders);
        m_parent = parent;

        setClassAttr("AlternateTable");
        setWidth("100%");
        setCellRenderers();
        addTableActionListener(this);
    }

    public void cellSelected(TableActionEvent e) {
    }


    public void headSelected(TableActionEvent e) {
        throw new UnsupportedOperationException();
    }

    private void setCellRenderers() {
        getColumn(4).setCellRenderer(new LinkRenderer());
    }

    private final class LinkRenderer implements TableCellRenderer {
        public Component getComponent(Table table, PageState state,
                                      Object value, boolean isSelected,
                                      Object key, int row, int column) {
            if (value != null) {

                File file = m_parent.getFile(state);

                Link link = new Link("Download", "download/?" + FILE_ID_PARAM
                        .getName() + "=" + file.getObjectId() + "&trans_id=" + key);
                link.setClassAttr("downloadLink");
                return link;

            } else {
                return new Label();
            }
        }
    }

    private final class FileRevisionsTableModelBuilder extends LockableImpl
            implements TableModelBuilder {

        private FileInfoHistoryPane m_parent;

        FileRevisionsTableModelBuilder(FileInfoHistoryPane parent) {
            m_parent = parent;
        }

        public TableModel makeModel(Table t, PageState state) {
            return new FileRevisionsTableModel(m_parent.getFile(state), state);
        }
    }

    private final class FileRevisionsTableModel implements TableModel,
            Constants {

        private FileInfoHistoryPane m_parent;
        private File m_file;
        private PageState m_state;
        private TransactionCollection m_tc;
        private Transaction m_transaction;
        private Transaction m_lastContentChange;
        private int m_row;
        private int m_last = 2;

        FileRevisionsTableModel(File file, PageState state) {
            m_file = file;
            m_state = state;

            m_tc = m_file.getRevision();
            m_row = (int) m_tc.size() + 1;
            m_last = m_row;

            // separate collection from last content changes
        }

        public int getColumnCount() {
            return 5;
        }

        public Object getElementAt(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return new BigDecimal(m_row);
                case 1: {
                    com.arsdigita.kernel.User user = m_file.getLastModifiedUser();
                    if (null == user) {
                        return "Unknown";
                    } else {
                        return user.getPersonName().toString();
                    }
                }
                case 2:
                    if (m_row == 0)
                        return Utils.DateFormat.format(m_file.getCreationDate());
                    else
                        return Utils.DateFormat.format(m_file.getLastModifiedDate());
                case 3: {
                    StringBuffer sb = new StringBuffer();
                    TagCollection tc = m_transaction.getTags();
                    int counter = 0;
                    while (tc.next()) {
                        counter++;
                        Tag t = tc.getTag();
                        sb.append(counter + ") " + t.getDescription() + "  ");
                    }
                    return sb.toString();
                }
                case 4:
                    return "download";
                default:
                    break;
            }
            return null;
        }

        public Object getKeyAt(int columnIndex) {
            if (columnIndex == 4) {
                if (m_row == m_last - 1) {
                    return "current";
                } else {
                    return m_transaction.getID();
                }
            } else {
                return m_file.getID() + "." + (m_row);
            }
        }

        public boolean nextRow() {
            m_row--;
            if (m_tc.next()) {
                m_transaction = m_tc.getTransaction();
                return true;
            } else {
                m_tc.close();
                return false;
            }
        }
    }
}
