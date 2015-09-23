/*
 * Copyright (C) 2001-2006 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.bebop.form;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.arsdigita.bebop.Bebop;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.parameters.StringParameter;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;

/**
 * Displays and manages a WYSIWYG HTML editor that takes advantage of DHTML scripting features. This
 * class can use: - <a href="http://www.xinha.org>Xinha</a>
 * - <a href="http://www.fckeditor.net">FCKeditor</a>
 * - HTMLarea for backwards compatibility, development discontinued Editor is choosen based on the
 * config parameter waf.bebop.dhtml_editor, default is "Xinha", which is the successor of HTMLarea
 *
 * @author Jim Parsons
 * @author Richard Li
 * @author Chris Burnett
 * @author Alan Pevec
 *
 * @version $Id$
 */
public class DHTMLEditor extends TextArea {

    /**
     * Constant for specifying <tt>OFF</tt> value for the <tt>WRAP</tt> attribute of this image
     * input.
     *
     * See <a href="http:
     * //developer.netscape.com/docs/manuals/htmlguid/tags10.htm#1340340">here</a>
     * for a description of what this attribute does.
     */
    public static final int OFF = 0;

    /**
     * Constant for specifying <tt>HARD</tt> value for the <tt>WRAP</tt> attribute of this image
     * input.
     *
     * See <a href="http://
     * developer.netscape.com/docs/manuals/htmlguid/tags10.htm#1340340">here</a>
     * for a description of what this attribute does.
     */
    public static final int HARD = 1;

    /**
     * Constant for specifying <tt>SOFT</tt> value for the <tt>WRAP</tt> attribute of this image
     * input. See <a href="http://
     * developer.netscape.com/docs/manuals/htmlguid/tags10.htm#1340340">here</a>
     * for a description of what this attribute does.
     */
    public static final int SOFT = 2;

    /**
     * Config objects for supported DHTMP editors
     */
    public static class Config {

        // WARNING: Processing of these default values by CMSConfig does NOT
        // work correctly because of deviciencies in unmarshal method there.
        public static final Config STANDARD = new Config("Xinha.Config",
                                                         "/assets/xinha/CCMcoreXinhaConfig.js");

        /**
         * Example FCKEditor configuration.
         */
        public static final Config FCK_STANDARD = new Config("FCKEditor.Config.StyleDefault",
                                                             "/assets/fckeditor/config/fckconfigstyledefault.js");

        public static final Config FCK_CMSADMIN = new Config("FCKEditor.Config.StyleCMSAdmin",
                                                             "/assets/fckeditor/config/fckconfigstylecmsadmin.js");

        /**
         * Example old HTMLarea configuration.
         */
        public static final Config HTMLAREA = new Config("HTMLArea.Config", null);

        private String m_name;
        private String m_path;

        public Config(String name) {
            this(name, null);
        }

        public Config(String name,
                      String path) {
            m_name = name;
            m_path = path;
        }

        public String getName() {
            return m_name;
        }

        public String getPath() {
            return m_path;
        }

        public static Config valueOf(String cfg) {
            int offset = cfg.indexOf(",");
            if (offset != -1) {
                return new Config(cfg.substring(0, offset),
                                  cfg.substring(offset + 1));
            } else {
                return new Config(cfg);
            }
        }

        public String toString() {
            if (m_path == null) {
                return m_name;
            } else {
                return m_name + "," + m_path;
            }
        }

    }  //end config object(s)

    private Config m_config;
    private Set m_plugins;
    private Set m_hiddenButtons;

    /**
     * Constructor
     *
     * @param name
     */
    public DHTMLEditor(String name) {
        this(new StringParameter(name));
    }

    /**
     * Constructor
     *
     * @param model
     */
    public DHTMLEditor(ParameterModel model) {
        this(model, Config.STANDARD);
    }

    /**
     * Constructor
     *
     * @param model
     * @param config
     */
    public DHTMLEditor(ParameterModel model,
                       Config config) {
        super(model);
        m_config = config;
        m_plugins = new HashSet();
        m_hiddenButtons = new HashSet();
    }

    /**
     * Returns a string naming the type of this widget.
     */
    public String getType() {
        return "DHTMLEditor";
    }

    public String getEditorURL() {
        return Bebop.getConfig().getDHTMLEditorSrcFile().substring(
            0, Bebop.getConfig().getDHTMLEditorSrcFile().lastIndexOf("/") + 1);
    }

    public String getEditorSrc() {
        return Bebop.getConfig().getDHTMLEditorSrcFile();
    }

    /**
     * deprecated - use {@link setConfig(Config)}
     *
     * @param config
     */
    public void setConfig(String config) {
        setAttribute("config", config);
    }

    public void setConfig(Config config) {
        m_config = config;
    }

    public void addPlugin(String name) {
        m_plugins.add(name);
    }

    /**
     * Prevent the specified button from being displayed in the editor toolbar.
     *
     * @param name name of the button, as specified in the btnList of the htmlarea.js file
     *
     */
    public void hideButton(String name) {
        m_hiddenButtons.add(name);
    }

    /**
     * Sets the <tt>ROWS</tt> attribute for the <tt>TEXTAREA</tt> tag.
     */
    @Override
    public void setRows(int rows) {
        setAttribute("rows", String.valueOf(rows));
    }

    /**
     * Sets the <tt>COLS</tt> attribute for the <tt>TEXTAREA</tt> tag.
     */
    @Override
    public void setCols(int cols) {
        setAttribute("cols", String.valueOf(cols));
    }

    /**
     * Sets the <tt>COLS</tt> attribute for the <tt>TEXTAREA</tt> tag.
     */
    @Override
    public void setWrap(int wrap) {
        String wrapString = null;

        switch (wrap) {
            case OFF:
                wrapString = "off";
                break;
            case HARD:
                wrapString = "hard";
                break;
            case SOFT:
                wrapString = "soft";
                break;
        }

        if (wrapString != null) {
            setAttribute("wrap", wrapString);
        }
    }

    /**
     * The XML tag.
     *
     * @return The tag to be used for the top level DOM element generated for this type of Widget.
     */
    @Override
    protected String getElementTag() {
        return Bebop.getConfig().getDHTMLEditor();
    }

    /**
     * Generates the DOM for the DHTML editor widget
     * <p>
     * Generates DOM fragment:
     * <p>
     * <code>&lt;bebop:dhtmleditor name=... value=... [onXXX=...]/>
     * </code>
     */
    @Override
    public void generateWidget(PageState state, Element parent) {
        String value = getParameterData(state).marshal();
        Element editor = parent.newChildElement(getElementTag(), BEBOP_XML_NS);

        editor.addAttribute("name", getName());
        generateDescriptionXML(state, editor);

        // Set the needed config params so they don't have to be hardcoded in the theme
        editor.addAttribute("editor_url", Web.getWebappContextPath().concat(getEditorURL()));
        editor.addAttribute("editor_src", Web.getWebappContextPath().concat(getEditorSrc()));

        if (value != null) {
            editor.setText(value);
        }

        exportAttributes(editor);

        Element config = editor.newChildElement("bebop:config", BEBOP_XML_NS);
        config.addAttribute("name", m_config.getName());
        if (m_config.getPath() != null) {
            config.addAttribute("path", Web.getWebappContextPath().concat(m_config.getPath()));
        }
        if (m_hiddenButtons.size() > 0) {

            StringBuffer hiddenButtons = new StringBuffer();
            // list must start and end with a space
            hiddenButtons.append(" ");
            Iterator hidden = m_hiddenButtons.iterator();
            while (hidden.hasNext()) {
                hiddenButtons.append(hidden.next());
                hiddenButtons.append(" ");
            }
            config.addAttribute("hidden-buttons", hiddenButtons.toString());
        }
        Iterator plugins = m_plugins.iterator();
        while (plugins.hasNext()) {
            String name = (String) plugins.next();
            Element plugin = editor.newChildElement("bebop:plugin", BEBOP_XML_NS);
            plugin.addAttribute("name", name);
        }
    }

}
