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
import java.math.BigDecimal;
import org.libreccm.web.ApplicationType;

/**
 * Tree Node implementation for the Application Tree in the Application admin tab.
 *
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: LegacyApplicationTypeTreeNode.java 2282 2013-08-01 10:45:42Z jensp $
 */
public class LegacyApplicationTypeTreeNode implements TreeNode {

    private final String name;
    private final String objectType;
    private final boolean singleton;
    private final String description;
    // Needed:
    // isSingleton
    // getObjectType

    public LegacyApplicationTypeTreeNode(final ApplicationType applicationType) {
        //this.applicationType = applicationType;
        name = applicationType.name();
        objectType = applicationType.name();
        singleton = applicationType.singleton();
        description = applicationType.descKey();
    }
    
    public String getName() {
        return name;
    }
    
    public String getObjecType() {
        return objectType;
    }
    
    public boolean isSingleton() {
        return singleton;
    }
    
    public String getDescription() {
        return description;
    }
    
    public Object getKey() {
        //return applicationType.getApplicationObjectType();
        return objectType;
    }

    public Object getElement() {
        return name;
    }
}
