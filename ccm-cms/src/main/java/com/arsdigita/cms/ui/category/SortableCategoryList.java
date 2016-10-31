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
package com.arsdigita.cms.ui.category;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.ParameterSingleSelectionModel;
import com.arsdigita.bebop.parameters.BigDecimalParameter;
import com.arsdigita.cms.CMS;

import com.arsdigita.cms.ui.SortableList;
import org.apache.log4j.Logger;
import org.libreccm.categorization.Category;
import org.librecms.contentsection.ContentSection;

import javax.servlet.ServletException;
import java.math.BigDecimal;

/**
 * This list offers the option for the code to provide the developer
 * with links to sort the given categories.
 *
 * NOTE: This UI currently does not scale well with large numbers of
 * items since it just lists all of them.  It would probably be nice
 * to integrate a paginator as well to as to allow the user to move an
 * item in large distances and to insert an item in the middle.  Right
 * now, when you add an item it is just placed at the end.  However,
 * if you want the item to appear in the middle then you must hit the
 * "up" arrow n/2 times where n is the number of items in the list.
 * This clearly is not a good setup.
 *
 * @author <a href="mailto:yannick.buelter@yabue.de">Yannick Bülter</a>
 * @author Randy Graebner (randyg@alum.mit.edu)
 * @version $Id: SortableCategoryList.java 1942 2009-05-29 07:53:23Z terry $
 */
abstract class SortableCategoryList extends SortableList {

    private static final Logger s_log = Logger.getLogger
        (SortableCategoryList.class);

    public final static String CHILDREN = "ch";

    private final CategoryRequestLocal m_parent;

    /**
     * This just makes a standard
     * {@link SortableList}
     */
    public SortableCategoryList(final CategoryRequestLocal parent) {
        super(new ParameterSingleSelectionModel
              (new BigDecimalParameter(CHILDREN)), false);

        m_parent = parent;

        setIdAttr("categorized_objects_list");
    }

    protected final Category getCategory(final PageState state) {
        return m_parent.getCategory(state);
    }

    /**
     *  This actually performs the sorting
     */
    public void respond(PageState ps) throws ServletException {
        String event = ps.getControlEventName();
        /* TODO Do actual sorting
        if (NEXT_EVENT.equals(event) || PREV_EVENT.equals(event)) {
            try {
                ACSObject child =
                    (ACSObject)DomainObjectFactory.newInstance
                    (new OID(ACSObject.BASE_DATA_OBJECT_TYPE,
                             new BigDecimal(ps.getControlEventValue())));
                Category parent = m_parent.getCategory(ps);

                if (CMS.getContext().getSecurityManager().canAccess
                        (SecurityManager.CATEGORY_ADMIN)) {
                    if (NEXT_EVENT.equals(event)) {
                        parent.swapWithNext(child);
                    } else {
                        parent.swapWithPrevious(child);
                    }

                    parent.save();
                }               
            } catch (DataObjectNotFoundException e) {
                s_log.error("Trying to create categories with state = " + ps, e);
                throw new ServletException(e);
            }
        } else {
            super.respond(ps);
        }*/
    }
}
