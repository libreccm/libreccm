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

import java.util.List;

import com.arsdigita.bebop.list.ListModel;
import com.arsdigita.util.Assert;

/** 
 * ListModel for Reports.
 * 
 * @author <a href="https://sourceforge.net/users/thomas-buckel/">thomas-buckel</a>
 * @author <a href="https://sourceforge.net/users/tim-permeance/">tim-permeance</a>
 */
public class ReportListModel implements ListModel {
   
   private int m_index = -1;
   private final List<Report> m_reports;
   
   public ReportListModel(List<Report> reports) {
       Assert.exists(reports);
       m_reports = reports;
   }
   
   @Override
   public Object getElement() {
       return m_reports.get(m_index).getName();
   }

   @Override
   public String getKey() {
       return m_reports.get(m_index).getKey();
   }

   @Override
   public boolean next() {
       m_index++;
        return (m_reports.size() > m_index);
   }

}
