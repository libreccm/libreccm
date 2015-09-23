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
package com.arsdigita.bebop.event;

/**
 *  Listener interface for
 * the SeachAndSelect Bebop widget.  SearchAndSelect requires
 * knowledge about the data it is searching over (to determine the
 * display method and to actually execute the query).
 *
 * @author Patrick McNeill
 * @version $Id$
 * @since 4.5 */
public interface SearchAndSelectModel {

    /**
     * Specify the user's search and restrict the result set to those queries
     * that match.  An empty string should return all results.
     *
     * @param query the user's search string, space or comma delimited words
     */
    void setQuery ( String query );

    /**
     * Retrieve the query that was last used.
     *
     * @return the query string
     */
    String getQuery ();

    /**
     * Return the number of items that are currently selected by the query
     * string.  If the query string is empty, this should return the number
     * of items in the dataset.
     *
     * @return the number of currently selected items
     */
    int resultsCount ();

    /**
     * Get the "i"th label (0 based indexing)
     *
     * @param i the label number to retrieve
     * @return the ith label
     */
    String getLabel (int i);

    /**
     * Get the "i"th ID (0 based indexing)
     *
     * @param i the ID number to retrieve
     * @return the ith ID
     */
    String getID (int i);
}
