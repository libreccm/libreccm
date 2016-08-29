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
package com.arsdigita.cms.ui.util;

import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.ControlLink;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.Table;
import com.arsdigita.bebop.table.TableCellRenderer;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;
import com.arsdigita.util.LockableImpl;
import org.librecms.CmsConstants;

/**
 * The default renderer for table cells. This renderer is used by the
 * {@link com.arsdigita.bebop.Table} component for rendering the table headers
 * and cells if no other renderer is specified.
 *
 * <p>
 * This renderer can operate in two different modes: <em>active</em>
 * and <em>inactive</em> mode. In inactive mode, all objects are rendered by
 * converting them to a string and enclosing that string in a {@link
 * com.arsdigita.bebop.Label}. If the renderer is in active mode, this label is
 * further enclosed in a control link. When the user clicks on this link, the
 * table will fire an <code>TableActionEvent</code> whose <code>getKey()</code>
 * and <code>getColumn()</code> method return the values of the <code>key</code>
 * and <code>column</code> parameters that were passed into
 * {@link #getComponent getComponent}.
 *
 * <p>
 * In a nutshell, an active renderer will let the user click a link that causes
 * a <code>TableActionEvent</code> for the corresponding cell, while an inactive
 * renderer will display the values just as strings, thus making it impossible
 * for the user to cause such an event.
 *
 * @author <a href="mailto:lutter@arsdigita.com">David Lutterkort</a>
 * @author Michael Pih (pihman@arsdigita.com)
 * @see com.arsdigita.bebop.Table
 * @see com.arsdigita.bebop.event.TableActionEvent
 *
 * @version $Id: DefaultTableCellRenderer.java 287 2005-02-22 00:29:02Z sskracic
 * $
 */
public class DefaultTableCellRenderer extends LockableImpl
        implements TableCellRenderer {

    private boolean m_active;
    private ThreadLocal m_label;
    private ThreadLocal m_controlLink;

    /**
     * Creates a new table cell renderer. The table cell renderer is in inactive
     * mode.
     */
    public DefaultTableCellRenderer() {
        this(false);
    }

    /**
     * Creates a new table cell renderer. The <code>active</code> argument
     * specifies whether the renderer should be active or not.
     *
     * @param active <code>true</code> if the renderer should generate links
     * instead of just static labels.
     */
    public DefaultTableCellRenderer(boolean active) {
        m_active = active;
        m_label = new ThreadLocal() {
            protected Object initialValue() {
                return new Label("");
            }
        };
        m_controlLink = new ThreadLocal() {
            protected Object initialValue() {
                return new ControlLink((Label) m_label.get());
            }
        };
    }

    /**
     * Return <code>true</code> if the renderer is in active mode. A rendererin
     * active mode will enclose the objects it renders in links that, when
     * clicked, will cause the containing table to fire a
     * <code>TableActionEvent</code>.
     *
     * @return <code>true</code> if the renderer is in active mode.
     */
    public final boolean isActive() {
        return m_active;
    }

    /**
     * Set the renderer to active or inactive mode.
     *
     * @param v <code>true</code> if the renderer should operate in active mode.
     * @pre ! isLocked()
     */
    public void setActive(boolean v) {
        Assert.isUnlocked(this);
        m_active = v;
    }

    /**
     * Return the component that should be used to render the given
     * <code>value</code>. Returns a {@link com.arsdigita.bebop.Label} if the
     * renderer is active, and a {@link com.arsdigita.bebop.ControlLink} if the
     * renderer is inactive.
     *
     * @pre table == null || table != null
     */
    public Component getComponent(Table table, PageState state, Object value,
                                  boolean isSelected, Object key,
                                  int row, int column) {
        if (!isLocked() && table != null && table.isLocked()) {
            lock();
        }

        Label l = (Label) m_label.get();
        if (value == null) {
            l.setLabel(new GlobalizedMessage("cms.ui.util.",
                                             CmsConstants.CMS_BUNDLE));
            l.setOutputEscaping(false);
        } else {
            l.setLabel((GlobalizedMessage) value);
            l.setOutputEscaping(true);
        }
        l.setFontWeight((isSelected && m_active) ? Label.BOLD : null);
        if (m_active && !isSelected) {
            return (ControlLink) m_controlLink.get();
        } else {
            return l;
        }
    }
}
