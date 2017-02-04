/*
 * Copyright (C) 2002-2004 Red Hat Inc. All Rights Reserved.
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
package com.arsdigita.ui;

import com.arsdigita.bebop.BasePage;
import com.arsdigita.bebop.SimpleComponent;
import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Label;
import com.arsdigita.util.Classes;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Iterator;

/**
 * SimplePage is a subclass of ApplicationPage providing a
 * simple page which can have custom, application independant
 * widgets added to it. The default styling for this 
 * component provides a border layout with panels for the
 * top, bottom, left & right margins of the page.
 * 
 * <h3>Configuration</h3>
 * The components to be added to a page are configured in the
 * enterprise.init file using two parameters. The first, 
 * <code>defaultLayout</code>, specifies the site wide components,
 * the second, <code>applicationLayouts</code> allows individual
 * applications to be given a custom set of components.
 * <p>
 * The <code>defaultLayout</code> parameter is a list of components
 * & their associated layout tags. The values specified for the
 * layout tags are handled opaquely by the Java code, passing them
 * straight through to the output XML. Thus the exact values you
 * can specify are determined by the XSLT used for styling. For 
 * the default styling rules, the allowable tags are 'top', 'bottom',
 * 'left' & 'right'.
 * <h3>Generated XML</h3>
 * To allow XSLT to easily distinguish the generic components for the 
 * page borders from the application specific content, all components
 * added to the page are placed within one of two trivial containers.
 * <p>
 * All the application specific components (as added by the <code>add</code>
 * method) are placed  within a single <code>ui:simplePageContent</code>
 * tag. The components for each position tag are placed within a 
 * <code>ui:simplePagePanel</code> tag, with the <code>position</code>
 * attribute set accordingly.
 * 
 */
public class SimplePage extends BasePage {
    
    private static SimplePageLayout s_default = new SimplePageLayout();
    private static HashMap s_layouts = new HashMap();

    private static Logger LOGGER = LogManager.getLogger(SimplePage.class);

    /**
     * Set the default layout for all applications, which haven't
     * got a specific layout configuration.
     *
     * @param layout the default layout policy
     */
    static void setDefaultLayout(SimplePageLayout layout) {
        s_default = layout;
    }
    
    /**
     * Set the application specific layout, overriding the default
     * layout.
     *
     * @param application the application name
     * @param layout the layout policy
     */
    static void setLayout(String application,
                          SimplePageLayout layout) {
        s_layouts.put(application, layout);
    }
    
    /**
     * Retrieve the layout policy for a particular application.
     * Looks for an application specific layout first, and if
     * that fails, opts for the default layout.
     * 
     * @param application the application name
     * @return the applications layout
     */
    static SimplePageLayout getLayout(String application) {
        SimplePageLayout layout = (SimplePageLayout)s_layouts.get(application);

        if (layout == null) {
            layout = s_default;
        }

        return layout;
    }
    
    /**
     * Creates a new SimplePage object. This constructor
     * is only intended for subclasses & PageFactory. Applications should
     * call <code>PageFactory.buildPage</code> to obtain a suitable
     * instance of the com.arsdigita.bebop.Page class.
     *
     * @param application the application name
     * @param title label for the page title
     * @param id (optionally null) unique id for the page
     */
    public SimplePage(String application,
                      Label title,
                      String id) {
        super(application, title, id);

        setClassAttr("simplePage");

        addLayoutComponents(application);
    }

    /**
     * Adds a component to the body of the page. To add a component
     * to header / footer / etc, set its classname in one of the
     * page layouts.
     * @param child the component to add to the body
     */
    public void add(Component child) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding component to body " + child.getClass());
        }
        
        super.add(child);
    }
    
    /**
     * Adds a component to the body of the page. To add a component
     * to header / footer / etc, set its classname in one of the
     * page layouts.
     * @param child the component to add to the body
     */
    public void add(Component child,
                    int constraints) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Adding component to body " + child.getClass());
        }
        
        super.add(child, constraints);
    }
    

    /**
     * Configure this page object, adding the pre-configured
     * components to its body
     */
    private void addLayoutComponents(String application) {
        SimplePageLayout layout = getLayout(application);
        
        Iterator tags = layout.getPositionTags();
        while (tags.hasNext()) {
            String tag = (String)tags.next();

            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Adding component with tag " + tag);
            }
        
            
            Iterator i = layout.getComponents(tag);
            while (i.hasNext()) {
                Class klass = (Class)i.next();
                SimpleComponent child = (SimpleComponent)Classes.newInstance(klass);
                child.setMetaDataAttribute("tag", tag);

                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Adding component " + child.getClass());
                }
        
                super.add(child);
            }
        }
    }    
}
