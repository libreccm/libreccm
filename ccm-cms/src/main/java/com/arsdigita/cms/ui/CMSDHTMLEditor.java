/*
 * Copyright (C) 2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.cms.ui;

import com.arsdigita.bebop.form.DHTMLEditor;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;

import org.arsdigita.cms.CMSConfig;

/**
 *
 *
 */
public class CMSDHTMLEditor extends DHTMLEditor {
    
    public CMSDHTMLEditor(final String name) {
        super(new StringParameter(name),
              CMSConfig.getConfig().getDHTMLEditorConfig());
        addPlugins();
        hideButtons();
        
    }
    
    public CMSDHTMLEditor(final ParameterModel model) {
        super(model,
              CMSConfig.getConfig().getDHTMLEditorConfig());
        
        addPlugins();
        hideButtons();
    }
    
    private void addPlugins() {
        
        CMSConfig
            .getConfig()
            .getDhtmlEditorPlugins()
            .forEach(plugin -> addPlugin(plugin));
    }
    
    private void hideButtons() {
        
        CMSConfig
            .getConfig()
            .getDhtmlEditorHiddenButtons()
            .forEach(hiddenButton -> hideButton(hiddenButton));
    }
    
}
