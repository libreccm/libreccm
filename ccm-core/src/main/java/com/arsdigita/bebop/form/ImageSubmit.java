/*
 * Copyright (C) 2001-2004 Red Hat Inc. All Rights Reserved.
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

import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.bebop.util.PanelConstraints;

/**
 *    A class representing an image HTML form element.
 *
 *    @author Karl Goldstein 
 *    @author Uday Mathur 
 *    @author Rory Solomon 
 *    @author Michael Pih 
 *    @version $Id$
 */
public class ImageSubmit extends Widget implements PanelConstraints {


    /**
     * Constructor.
     * 
     * @param name 
     */
    public ImageSubmit(String name) {
        super(name);
    }

    public ImageSubmit(ParameterModel model) {
        super(model);
    }

    /**
     * Returns a string naming the type of this widget.
     * 
     * @return 
     */
    @Override
    public String getType() {
        return "image";
    }

    /**
     * Sets the <tt>SRC</tt> attribute for the <tt>INPUT</tt> tag
     * used to render this form element.
     * 
     * @param location
     */
    public void setSrc(String location) {
        setAttribute("src",location);
    }

    /*
     * Sets the <tt>ALRT</tt> attribute for the <tt>INPUT</tt> tag
     * used to render this form element.
     */
    public void setAlt(String alt) {
        setAttribute("alt",alt);
    }

    /**
     * Sets the <tt>ALIGN</tt> attribute for the <tt>INPUT</tt> tag
     * used to render this form element. Uses the positional constants defined
     * in Interface PanelConstraints.
     * Note: These may be refactored in future versions.
     * 
     * @param align Symbolic constant denoting the alignment.
     */
    public void setAlign(int align) {
        String alignString = null;

        switch (align) {
        case LEFT:
            alignString = "left";
            break;
        case RIGHT:
            alignString = "right";
            break;
        case TOP:
            alignString = "top";
            break;
        case ABSMIDDLE:
            alignString = "absmiddle";
            break;
        case ABSBOTTOM:
            alignString = "absbottom";
            break;
        case TEXTTOP:
            alignString = "texttop";
            break;
        case MIDDLE:
            alignString = "middle";
            break;
        case BASELINE:
            alignString = "baseline";
            break;
        case BOTTOM:
            alignString = "botton";
            break;
        }

        if (alignString != null)
            setAttribute("align",alignString);
    }

    @Override
    public boolean isCompound() {
        return false;
    }

    /**
     *      Callback method for rendering this Image widget in a visitor.
     */
    /*  public void accept(FormVisitor visitor) throws IOException {
        visitor.visitImage(this);
        }
    */


}
