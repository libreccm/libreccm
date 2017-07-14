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
package com.arsdigita.cms.ui.authoring;

import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.kernel.KernelConfig;

import java.util.Locale;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class SelectedLanguageUtil {
    
    private SelectedLanguageUtil() {
        //Nothing
    }
    
    public static final Locale selectedLocale(
        final PageState state,
        final StringParameter selectedLanguageParam) {
        
        final String selectedLanguage = (String) state
            .getValue(selectedLanguageParam);
        if (selectedLanguage == null) {
            return KernelConfig.getConfig().getDefaultLocale();
        } else {
            return new Locale(selectedLanguage);
        }
    }
}
