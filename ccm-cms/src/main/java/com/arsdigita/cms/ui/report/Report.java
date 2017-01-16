/*
 * Copyright (C) 2009 Permeance Technologies Pty Ltd. All Rights Reserved.
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
 */

package com.arsdigita.cms.ui.report;

import com.arsdigita.bebop.Component;
import com.arsdigita.globalization.GlobalizedMessage;
import com.arsdigita.util.Assert;

import org.librecms.CmsConstants;

/**
 * UI model for a report.
 * A report has a name and a component that displays the report.
 * 
 * @author <a href="https://sourceforge.net/users/thomas-buckel/">thomas-buckel</a>
 * @author <a href="https://sourceforge.net/users/tim-permeance/">tim-permeance</a>
 * @author <a href="jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Report {

   private final String key;
   private final String name;
   private final Component component;
   
   public Report(final String key, final Component component) {
       Assert.exists(key, "Key for report is required");
       Assert.isTrue(key.length() > 0, "Key for report must not be empty");
       Assert.exists(component, "Component for report is required");
       
       this.key = key;
       name = gz(key).localize().toString();
       this.component = component;
   }
   
   public String getKey() {
       return key;
   }
   
   public String getName() {
       return name;
   }
   
   public Component getComponent() {
       return component;
   }
   
    protected final static GlobalizedMessage gz(final String key) {
        return new GlobalizedMessage(key, CmsConstants.CMS_BUNDLE);
    }
    
}
