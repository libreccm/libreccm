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

import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.bebop.parameters.GlobalizedParameterListener;
import com.arsdigita.bebop.parameters.ParameterData;
import com.arsdigita.bebop.parameters.ParameterModel;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.globalization.GlobalizedMessage;

import javax.servlet.http.HttpServletRequest;


/**
 * A class representing a file upload widget.
 *
 * @author Karl Goldstein 
 * @author Uday Mathur 
 * @author Stas Freidin 
 * @author Rory Solomon 
 * @author Michael Pih 
 * @version $Id$ */

public class FileUpload extends Widget {

    public FileUpload(String name) {
        this(name, true);
    }

    public FileUpload(String name, boolean validateInputFile) {
        super(name);
        addValidationListener(new FileExistsValidationListener());
    }

    public FileUpload(ParameterModel model) {
        this(model, true);
    }

    public FileUpload(ParameterModel model, boolean validateInputFile) {
        super(model);
        addValidationListener(new FileExistsValidationListener());
    }

    /**
     * Returns a string naming the type of this widget.
     * @return 
     */
    @Override
    public String getType() {
        return "file";
    }

    /**
     * 
     * @return 
     */
    @Override
    public boolean isCompound() {
        return false;
    }


    /**
     * 
     */
    private class FileExistsValidationListener extends GlobalizedParameterListener {
        
        public FileExistsValidationListener() {
            setError(new GlobalizedMessage("file_empty_or_not_found", getBundleBaseName()));
        }
        
        @Override
        public void validate (ParameterEvent e) {
            ParameterData data = e.getParameterData();
            HttpServletRequest request = e.getPageState().getRequest();
            String filename = (String) data.getValue();
            
            if (!(request instanceof MultipartHttpServletRequest) || 
                filename == null || 
                filename.length() == 0) {
                return;
            }
            
            if (((MultipartHttpServletRequest) request).getFile(data.getModel()
                                                       .getName())
                                                       .length()==0) {
                data.addError(filename + " " + getError().localize());
            }
        }
    }
}
