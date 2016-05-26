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
package com.arsdigita.ui.admin.applications;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.PropertySheet;
import com.arsdigita.bebop.PropertySheetModel;
import com.arsdigita.bebop.PropertySheetModelBuilder;
import com.arsdigita.util.LockableImpl;
import org.libreccm.web.ApplicationType;

/**
 * {@link PropertySheetModelBuilder} implementation for the the {@link LegacyApplicationInfoPropertySheetModel}.
 * 
 * @author Jens Pelzetter <jens@jp-digital.de>
 * @version $Id: LegacyApplicationInfoPropertySheetModelBuilder.java 2219 2013-06-19 08:16:11Z jensp $
 */
public class LegacyApplicationInfoPropertySheetModelBuilder 
extends LockableImpl implements PropertySheetModelBuilder {
    
    private final ApplicationType applicationType;
    
    public LegacyApplicationInfoPropertySheetModelBuilder(
            final ApplicationType  applicationType) {
        super();
        this.applicationType = applicationType;
    }
    
    @Override
    public PropertySheetModel makeModel(final PropertySheet sheet, 
                                        final PageState state) {
        return new LegacyApplicationInfoPropertySheetModel(applicationType);
    }
    
}
