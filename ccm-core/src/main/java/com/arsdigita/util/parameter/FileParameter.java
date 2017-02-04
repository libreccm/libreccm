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
package com.arsdigita.util.parameter;

import java.io.File;


/**
 * A Parameter representing a File
 * 
 * @see Parameter
 * @see java.io.File
 * @author bche
 */
public class FileParameter extends AbstractParameter {
    
    public FileParameter(final String name) {
        super(name, File.class);
    }

    @Override
    public Object unmarshal(final String value, final ErrorList errors) {
        final String sPath = value;
        File file = new File(sPath);
        if (file.exists()) {
            return file;
        } else {
            return null;
        }
    }

    @Override
    public String marshal(final Object value) {
        final File file = (File) value;
        if (file == null) {
            return null;
        } else {
            return file.getAbsolutePath();
        }
    }

    public void doValidate(final Object value, final ErrorList errors) {
        final File file = (File) value;
        if (!file.exists()) {
            errors.add(
                new ParameterError(
                    this,
                    "File " + file.getAbsolutePath() + " does not exist"));
        }
    }
}
