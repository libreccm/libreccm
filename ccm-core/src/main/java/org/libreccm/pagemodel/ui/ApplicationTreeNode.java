/*
 * Copyright (C) 2016 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package org.libreccm.pagemodel.ui;

import com.arsdigita.bebop.tree.TreeNode;
import com.arsdigita.kernel.KernelConfig;
import java.util.Locale;
import org.libreccm.cdi.utils.CdiUtil;
import org.libreccm.l10n.GlobalizationHelper;
import org.libreccm.web.CcmApplication;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class ApplicationTreeNode implements TreeNode {
    
    private final CcmApplication application;

    public ApplicationTreeNode(final CcmApplication application) {
        this.application = application;
    }
    
    public CcmApplication getApplication() {
        return application;
    }
   
    @Override
    public Object getKey() {
        return application.getPrimaryUrl();
    }

    @Override
    public Object getElement() {
        final GlobalizationHelper globalizationHelper = CdiUtil.createCdiUtil().findBean(GlobalizationHelper.class);
        final Locale locale = globalizationHelper.getNegotiatedLocale();
        
        if (application.getTitle().hasValue(locale)) {
            return application.getTitle().hasValue(locale);
        } else {
            final Locale defaultLocale = KernelConfig.getConfig().getDefaultLocale();
            return application.getTitle().getValue(defaultLocale);
        }
    }
    
    
    
}
