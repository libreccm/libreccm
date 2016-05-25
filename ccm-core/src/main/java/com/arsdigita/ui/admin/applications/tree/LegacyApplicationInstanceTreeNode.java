/* 
 * Copyright (c) 2013 Jens Pelzetter
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
package com.arsdigita.ui.admin.applications.tree;

import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.ui.admin.applications.ApplicationsAdministrationTab;

import org.libreccm.web.CcmApplication;

/**
 * Tree Node for the application tree representing an instance of a application.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: LegacyApplicationInstanceTreeNode.java 2282 2013-08-01 10:45:42Z jensp $
 * 
 * @see ApplicationTreeModel
 * @see ApplicationTreeModelBuilder
 * @see ApplicationTypeTreeNode
 * @see ApplicationsAdministrationTab
 * @see TreeNode
 */
public class LegacyApplicationInstanceTreeNode implements TreeNode {
    
    /**
     * The application instance represented by this {@code TreeNode}
     */
    //private final Application application;
    private final String path;
    private final String title;
    private final String appType;
    
    /**
     * Constructor
     * 
     * @param application The application instance to represent by this {@code TreeNode}
     */
    public LegacyApplicationInstanceTreeNode(final CcmApplication application) {
        //this.application = application;
        path = application.getPrimaryUrl();
        title = application.getTitle().getValue();
        appType = application.getClass().getName();
    }
    
    public String getPath() {
        return path;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAppType() {
        return appType;
    }
    
    /**
     * Returns the key for this {@link TreeNode}. 
     * 
     * @return The path of the application instance.
     * @see TreeNode#getKey() 
     */
    @Override
    public Object getKey() {
        //return application.getPath();
        return path;
    }
    
    /**
     * Data to show in the tree for this node.
     * 
     * @return The title of the application instance
     * @see TreeNode#getElement() 
     */
    @Override
    public Object getElement() {
        //return application.getTitle();
        return title;
    }
    
}
