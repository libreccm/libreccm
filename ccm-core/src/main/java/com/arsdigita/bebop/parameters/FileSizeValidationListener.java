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
package com.arsdigita.bebop.parameters;

// pboy: unused imports, to be deleted
// import java.awt.Image;
import java.io.File;

import javax.servlet.http.HttpServletRequest;
// import javax.swing.ImageIcon;

// import org.apache.bcel.generic.INSTANCEOF;

// import com.arsdigita.bebop.FormProcessException;
// import com.arsdigita.bebop.event.ParameterListener;
import com.arsdigita.bebop.event.ParameterEvent;
import com.arsdigita.dispatcher.MultipartHttpServletRequest;
import com.arsdigita.globalization.GlobalizedMessage;
// import com.arsdigita.util.Assert;

/**
 *  Verifies that the File is smaller than the specified size in bytes
 *  Note that the file will still be brought into memory in the MultipartHttpRequest 
 * and so it may still be possible to bring your server down with an enormous file upload.
 * but this validation listener can prevent end users being faced with excessively 
 * large downloads
 * @version $Id$
 */
public class FileSizeValidationListener extends GlobalizedParameterListener {

	private final long m_maxLength;

	
	
	public FileSizeValidationListener(final long maxLength) {
		m_maxLength = maxLength;
		setError(new GlobalizedMessage("file_too_large", getBundleBaseName()));

	}

	

	public void validate(ParameterEvent e)  {
		
		ParameterData data = e.getParameterData();
		HttpServletRequest request = e.getPageState().getRequest();
		String filename = (String) data.getValue();
           
		if (!(request instanceof MultipartHttpServletRequest) || 
						filename == null || 
						filename.length() == 0) {
						return;
					}
		
		File file =
			(
				(MultipartHttpServletRequest) e
					.getPageState()
					.getRequest())
					.getFile(
				(String) data.getName());
		if (file.length() > m_maxLength) {
			data.addError(filename + " " + getError().localize());
		}

	}
}
