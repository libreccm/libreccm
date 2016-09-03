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
package com.arsdigita.util;

/**
 * Graph edge.
 *
 * @author Archit Shah (ashah@mit.edut)
 * @author Vadim Nasardinov (vadimn@redhat.com)
 * @since 2003-01-22
 * @version $Revision$ $Date$
 **/
public final class GraphEdge implements Graph.Edge {
    private Object m_tail;
    private Object m_head;
    private Object m_label;

    /**
     * @pre tail != null
     * @pre head != null
     **/
    public GraphEdge(Object tail, Object head, Object label) {
        Assert.exists(tail, Object.class);
        Assert.exists(head, Object.class);
        m_tail = tail;
        m_head = head;
        m_label = label;
    }

    /**
     * @set return != null
     **/
    public Object getTail() {
        return m_tail;
    }

    /**
     * @set return != null
     **/
    public Object getHead() {
        return m_head;
    }

    public Object getLabel() {
        return m_label;
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append(m_tail).append(" -> ").append(m_head);
        buf.append("[").append(m_label).append("]");
        return buf.toString();
    }

    public boolean equals(Object obj) {
        if ( obj == null ) return false;

        if (obj instanceof Graph.Edge) {
            Graph.Edge that = (Graph.Edge) obj;
            Object thatLabel = that.getLabel();

            boolean equalLabels =
                (m_label == null && thatLabel == null ) ||
                (m_label != null && m_label.equals(thatLabel)) ||
                (thatLabel != null && thatLabel.equals(m_label));

            return
                m_tail.equals(that.getTail()) &&
                m_head.equals(that.getHead()) &&
                equalLabels;
        } else {
            return false;
        }
    }

    public int hashCode() {
        return m_tail.hashCode() + m_head.hashCode() +
            (m_label == null ? 0 : m_label.hashCode());
    }
}
