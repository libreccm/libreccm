/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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

import java.util.TreeMap;
import java.util.HashMap;
import java.util.Comparator;
import org.apache.log4j.Logger;

/**
 * An implementation of Map which preserves the order in which you put
 * entries into it.
 *
 * @deprecated use {@link com.arsdigita.util.SequentialMap} instead
 * @author Justin Ross &lt;jross@redhat.com&gt;
 * @version $Id$
 */
public class OrderedMap extends TreeMap {

    private static final Logger s_log = Logger.getLogger(OrderedMap.class);

    private OrderingComparator m_comparator;

    public OrderedMap() {
        super(new OrderingComparator());

        m_comparator = (OrderingComparator) comparator();
    }

    /**
     * Calls to put define the order in which the OrderedMap returns
     * its contents in calls to entrySet().iterator();
     */
    public Object put(final Object key, final Object value) {
        if (s_log.isDebugEnabled()) {
            s_log.debug("Adding a new map entry: " + key + " => " + value);
        }

        m_comparator.keep(key);

        return super.put(key, value);
    }

    public Object clone() {
        final OrderedMap result = (OrderedMap) super.clone();

        result.m_comparator = (OrderingComparator) m_comparator.clone();

        return result;
    }

    public void clear() {
        super.clear();

        m_comparator.clear();
    }
}

final class OrderingComparator implements Comparator, Cloneable {
    private static final Logger s_log = Logger.getLogger
        (OrderingComparator.class);

    private HashMap m_sortKeyMap = new HashMap();
    private long m_currSortKey = 0;

    public final int compare(final Object o1, final Object o2) {
        Long sk1 = (Long) m_sortKeyMap.get(o1);
        Long sk2 = (Long) m_sortKeyMap.get(o2);

        if (sk1 == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("The sort key of " + o1 + " is null; " +
                            "returning 1");
            }

            return 1;
        } else if (sk2 == null) {
            if (s_log.isDebugEnabled()) {
                s_log.debug("The sort key of " + o2 + " is null; " +
                            "returning -1");
            }

            return -1;
        } else {
            final int result = (int) (sk1.longValue() - sk2.longValue());

            if (s_log.isDebugEnabled()) {
                s_log.debug("The sort key of " + o1 + " is " +
                            sk1.longValue());
                s_log.debug("The sort key of " + o2 + " is " +
                            sk2.longValue());
                s_log.debug("The result is " + result);
            }

            if (Assert.isEnabled() && result == 0) {
                Assert.isTrue(o1.equals(o2));
            }

            return result;
        }
    }

    final void keep(final Object key) {
        m_sortKeyMap.put(key, new Long(m_currSortKey++));
    }

    protected Object clone() {
        try {
            final OrderingComparator result =
                (OrderingComparator) super.clone();

            result.m_sortKeyMap = (HashMap) m_sortKeyMap.clone();

            return result;
        } catch (CloneNotSupportedException cnse) {
            // I don't think we can get here.

            return null;
        }
    }

    final void clear() {
        m_sortKeyMap.clear();
        m_currSortKey = 0;
    }
}
