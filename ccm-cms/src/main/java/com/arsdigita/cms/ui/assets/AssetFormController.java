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
package com.arsdigita.cms.ui.assets;

import org.librecms.contentsection.Asset;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 *
 * Interface for the CDI backend for the forms to manage assets.To avoid
 * problems with transactions etc.
 *
 * the Bebop based forms should access any other CDI beans beside the
 * approbriate implementation of this interface. To minimize the efford to
 * create an implementation the {@link AbstractAssetFormController} class should
 * be used. This class provides basic implementations for most methods.
 * 
 * Implementations of the methods defined by this interface should annotated with
 * {@code @Transactional(Transactional.TxType.REQUIRED)}.
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 *
 * @param <T> The type asset managed by this controller.
 */
public interface AssetFormController<T extends Asset> {

    /**
     * Gets the data for the forms from the asset.
     *
     * @param asset          The asset.
     * @param selectedLocale The selected locale
     *
     * @return The values of the properties of the asset for the the selected
     *         locale.
     */
    Map<String, Object> getData(T asset, Locale selectedLocale);

    /**
     * Updates the asset with the provided data and saves the changes.
     * 
     * @param asset
     * @param selectedLocale
     * @param data 
     */
    void setData(T asset, Locale selectedLocale, Map<String, Object> data);
    
    /**
     * Determines in which locales the provided asset is available.
     * 
     * @param asset The asset.
     * @return A list of the locales for which the asset has data.
     */
    List<Locale> availableLocales(T asset);
    
    /**
     * Determines for which the provided asset has no data. 
     * 
     * @param asset The asset.
     * @return A list of the locales for which the asset has data yet.
     */
    List<Locale> creatableLocales(T asset);

}
