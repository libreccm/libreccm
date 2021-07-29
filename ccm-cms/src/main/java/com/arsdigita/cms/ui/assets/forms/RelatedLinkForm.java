/*
 * Copyright (C) 2021 LibreCCM Foundation.
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
package com.arsdigita.cms.ui.assets.forms;

import com.arsdigita.bebop.FormProcessException;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.event.FormSectionEvent;
import com.arsdigita.cms.ui.assets.AbstractAssetForm;
import com.arsdigita.cms.ui.assets.AssetPane;

import org.librecms.assets.RelatedLink;
import org.librecms.assets.SideNote;

import java.util.Map;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class RelatedLinkForm extends AbstractAssetForm<RelatedLink>{

    public RelatedLinkForm(final AssetPane assetPane) {
        super(assetPane);
    }

    @Override
    protected Class<RelatedLink> getAssetClass() {
        return RelatedLink.class;
    }

    @Override
    protected void showLocale(final PageState state) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Map<String, Object> collectData(final FormSectionEvent event) throws
                                                                             FormProcessException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
