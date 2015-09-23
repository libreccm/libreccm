/*
 * Copyright (C) 2003-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop;

import com.arsdigita.bebop.page.PageTransformer;
import com.arsdigita.bebop.util.BebopConstants;
import com.arsdigita.runtime.AbstractConfig;
import com.arsdigita.templating.PresentationManager;
import com.arsdigita.ui.SimplePage;
import com.arsdigita.util.parameter.BooleanParameter;
import com.arsdigita.util.parameter.ClassParameter;
import com.arsdigita.util.parameter.EnumerationParameter;
import com.arsdigita.util.parameter.Parameter;
import com.arsdigita.util.parameter.SingletonParameter;
import com.arsdigita.util.parameter.StringParameter;
import org.apache.log4j.Logger;

/**
 * @author Justin Ross
 * @see com.arsdigita.bebop.Bebop
 * @version $Id: BebopConfig.java 1498 2007-03-19 16:22:15Z apevec $
 */
public final class BebopConfig extends AbstractConfig {

    /** A logger instance to assist debugging.                               */
    private static final Logger s_log = Logger.getLogger(BebopConfig.class);
    
    /** Singleton config object.                                             */
    private static BebopConfig s_config;

    /**
     * Gain a BebopConfig object.
     *
     * Singleton pattern, don't instantiate a config object using the
     * constructor directly!
     * @return
     */
    public static synchronized BebopConfig getInstance() {
        if (s_config == null) {
            s_config = new BebopConfig();
            s_config.load();
        }

        return s_config;
    }

    // set of configuration parameters
    // /////////////////////////////////////////////////////////////////

    /**
     *                                                                       */
    private final Parameter m_presenter = new SingletonParameter
            ("waf.bebop.presentation_manager", Parameter.REQUIRED,
             new PageTransformer());
    /**
     * 
     */
    private final Parameter m_page = new ClassParameter
            ("waf.bebop.base_page", Parameter.REQUIRED, SimplePage.class);
    /** Pointer to JTidy validation listener config file                     */
    private final Parameter m_tidy = new StringParameter
            ("waf.bebop.tidy_config_file", Parameter.REQUIRED,
             "com/arsdigita/bebop/parameters/tidy.properties");
    private final Parameter m_fancyErrors = new BooleanParameter
            ("waf.bebop.fancy_xsl_errors",
             Parameter.REQUIRED,
             Boolean.FALSE);
    /** Double Click Protection, enabled by default for all buttons in a form.*/
    private final Parameter m_dcpOnButtons = new BooleanParameter
            ("waf.bebop.dcp_on_buttons", Parameter.REQUIRED, Boolean.TRUE);
    /** Double Click Protection, disabled by default for all links.           */
    private final Parameter m_dcpOnLinks = new BooleanParameter
            ("waf.bebop.dcp_on_links", Parameter.REQUIRED, Boolean.FALSE);
    /**
     * 
     */
    private final Parameter m_enableTreeSelect = new BooleanParameter
            ("waf.bebop.enable_tree_select_attribute",
             Parameter.REQUIRED,
             Boolean.FALSE);
    /** List of supported DHTML editors, first one is default (Xinha) */
    private final EnumerationParameter m_dhtmlEditor;
    /** Path to DHTML editor source file, relativ to document root */
    private final Parameter m_dhtmlEditorSrcFile;
    /**                                                                       */
    private final Parameter m_showClassName = new BooleanParameter
            ("waf.bebop.show_class_name", Parameter.OPTIONAL, Boolean.FALSE);

    /** 
     * Constructor.
     * Singelton pattern, don't instantiate a config object using the
     * constructor directly! Use getConfig() instead.
     * 
     */
    public BebopConfig() {

        /** List of supported DHTML editors, first one is default (Xinha) */
        m_dhtmlEditor = new EnumerationParameter("waf.bebop.dhtml_editor",
            Parameter.REQUIRED,BebopConstants.BEBOP_XINHAEDITOR);
        m_dhtmlEditor.put("Xinha", BebopConstants.BEBOP_XINHAEDITOR);
        m_dhtmlEditor.put("FCKeditor", BebopConstants.BEBOP_FCKEDITOR);
        // HTMLArea for backwards compatibility with old XSL. to be removed soon!
        m_dhtmlEditor.put("HTMLArea", BebopConstants.BEBOP_DHTMLEDITOR);

        // Xinha is now default!
        m_dhtmlEditorSrcFile = new StringParameter
            ("waf.bebop.dhtml_editor_src", Parameter.REQUIRED,
             "/assets/xinha/XinhaLoader.js");

        register(m_presenter);
        register(m_page);
        register(m_tidy);
        register(m_fancyErrors);
        register(m_dhtmlEditor);
        register(m_dhtmlEditorSrcFile);
        register(m_dcpOnButtons);
        register(m_dcpOnLinks);
        register(m_enableTreeSelect);
	    register(m_showClassName);

        loadInfo();
    }

    /**
     * Gets the configured <code>PresentationManger</code>.
     */
    public final PresentationManager getPresentationManager() {
        return (PresentationManager) get(m_presenter);
    }

    final Class getBasePageClass() {
        return (Class) get(m_page);
    }

    /**
     * I don't *want* to make this public. XXX
     */
    public final String getTidyConfigFile() {
        return (String) get(m_tidy);
    }

    public boolean wantFancyXSLErrors() {
        return ((Boolean)get(m_fancyErrors)).booleanValue();
    }

    public final boolean doubleClickProtectionOnButtons() {
        return ((Boolean) get(m_dcpOnButtons)).booleanValue();
    }

    public final boolean doubleClickProtectionOnLinks() {
        return ((Boolean) get(m_dcpOnLinks)).booleanValue();
    }

    public final boolean treeSelectAttributeEnabled() {
        return ((Boolean) get(m_enableTreeSelect)).booleanValue();
    }

    /**
     * Gets the DHTML editor to use
     */
    public final String getDHTMLEditor() {
        return (String) get(m_dhtmlEditor);
    }

    /**
     * Gets the location of DHTML editor's source file
     */
    public final String getDHTMLEditorSrcFile() {
        return (String) get(m_dhtmlEditorSrcFile);
    }

    public final boolean showClassName() {
        return ((Boolean) get(m_showClassName)).booleanValue();
    }
}
