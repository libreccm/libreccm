/*
 * Copyright (C) 2016 LibreCCM Foundation.
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
package org.libreccm.themes;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebServlet(urlPatterns = {"/themes/static/*"})
public class StaticThemesServlet extends HttpServlet {

    private static final long serialVersionUID = -2732540547314051013L;

    private static final Logger LOGGER = LogManager.getLogger(
        StaticThemesServlet.class);

    @Override
    public void doGet(final HttpServletRequest request,
                      final HttpServletResponse response)
        throws ServletException, IOException {

        final String pathInfo = request.getPathInfo();
        if (pathInfo == null) {
            LOGGER.warn("Received request without path info which this Servlet "
                            + "can't handle. Responding with SC_FORBIDDEN.");
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        final String resourcePath = String.join("", "/themes", pathInfo);
        LOGGER.debug("Resource path is '{}'.", resourcePath);

//        final InputStream inStream = getClass().getClassLoader()
//            .getResourceAsStream(resourcePath);
//        final URL resourceUrl = getClass().getClassLoader().getResource(
//            String.format("file://%s", resourcePath));
//        final MimetypesFileTypeMap mimetypesMap = new MimetypesFileTypeMap();
//        final String type;
//        try {
//            type = mimetypesMap.getContentType(new File(resourceUrl.toURI()));
//        } catch (URISyntaxException ex) {
//            throw new ServletException(ex);
//        }
//        
//        response.setContentType(type);
//        if (inStream == null) {
//            LOGGER.warn("Failed to get requested resource '{}'", resourcePath);
//            response.sendError(HttpServletResponse.SC_NOT_FOUND);
//            return;
//        }
//        
//        IOUtils.copy(inStream, response.getOutputStream());
        final ServletContext servletContext = getServletContext();

        final Set<String> paths = servletContext.getResourcePaths(resourcePath);
        if (paths == null) {
            final InputStream inputStream = servletContext.getResourceAsStream(
                resourcePath);
            if (inputStream == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } else {
                response.setContentType(servletContext.getMimeType(resourcePath));
                IOUtils.copy(inputStream, response.getOutputStream());
            }
        } else {
            response.setContentType("text/plain");
            final PrintWriter printWriter = response.getWriter();
            printWriter.append(String.format("%s%n", resourcePath));
            for(int i = 0; i < resourcePath.length(); i++) {
                printWriter.append('=');
            }
            printWriter.append(System.lineSeparator());
            paths.forEach(p -> printWriter.append(String.format("%s%n", p)));
        }

        //IOUtils.copy(new FileInputStream(file), response.getOutputStream());
//        try {
//            final File file = new File(resourceURL.toURI());
//            response.setContentType(getServletContext().getMimeType(resourcePath));
//            IOUtils.copy(getServletContext().getResourceAsStream(resourcePath), 
//                         response.getOutputStream());
//            
//            final Path path = Paths.get(resourceURL.toURI());
//            
//            Files.copy(path, response.getOutputStream());
//        } catch (URISyntaxException ex) {
//            throw new ServletException(ex);
//        }
    }

}
