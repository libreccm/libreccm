/*
 * Copyright (C) 2017 LibreCCM Foundation.
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

import com.arsdigita.bebop.PageState;
import com.arsdigita.cms.ui.assets.AssetPane;
import org.librecms.assets.BinaryAsset;
import org.librecms.assets.FileAsset;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class FileAssetForm extends AbstractBinaryAssetForm<FileAsset> {

    public FileAssetForm(final AssetPane assetPane) {
        super(assetPane);
    }
    
    @Override
    protected Class<FileAsset> getAssetClass() {
        return FileAsset.class;
    }
    
//    @Override
//    protected BinaryAsset createBinaryAsset(final PageState state) {
//        return new FileAsset();
//    }
    
    
    
}
