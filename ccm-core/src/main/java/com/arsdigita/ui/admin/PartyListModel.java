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
package com.arsdigita.ui.admin;

import com.arsdigita.bebop.list.ListModel;

import org.libreccm.security.Party;

import java.util.List;


/** 
 * 
 * @version $Id$
 */ 
class PartyListModel implements ListModel {

    private final List<Party> m_parties;
    private Party m_currentParty = null;
    private int index = 0;

    /**
     * Constructor for the list
     * Builds the list of party list model for parties
     *
     * @param partys the partyCollection
     **/
    public PartyListModel(final List<Party> parties) {
        m_parties = parties;
    }

    /**
     * Check whether is an another party
     *
     * @return true if another party exist, false otherwise
     **/
    @Override
    public boolean next() {
        if (index < m_parties.size()) {
            index++;
            m_currentParty = m_parties.get(index);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns the unqiue ID string for current Party
     *
     * @return the unqiue ID string for current Party
     **/
    @Override
    public String getKey() {
        return Long.toString(m_currentParty.getPartyId());
    }

    /**
     * Returns the current Party
     *
     * @return the current Party
     **/
    @Override
    public Object getElement() {
        return m_currentParty.getPartyId();
    }
}
