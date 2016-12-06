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
package org.librecms.pagemodel;

import com.arsdigita.templating.Templating;
import com.arsdigita.xml.Document;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
@WebServlet(urlPatterns = {"*.bebop"})
public class PageModel extends HttpServlet {

    private static final long serialVersionUID = 1056528247823275004L;

    @Override
    protected void doGet(final HttpServletRequest request,
                         final HttpServletResponse response)
            throws ServletException {

        final BasePage page = new BasePage();
        final ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
        final ScriptEngine scriptEngine = scriptEngineManager.getEngineByName(
                "JavaScript");
        scriptEngine.put("page", page);
        
        try {
            scriptEngine.eval(
                    "load(\'nashorn:mozilla_compat.js\');\n"
                            + "importClass(org.librecms.pagemodel.BasePage);\n"
                            + "var BasePage = Java.type('org.librecms.pagemodel.BasePage');\n"
//                    + "var page = new BasePage;\n"
//                            + "print(page.getClass().getName());\n"
                            + "page.setPageAttribute(\'name\', \'page-model-demo\');\n"
                            + "page.setPageAttribute(\'application\', \'content\');\n"
                    + "label = new com.arsdigita.bebop.Label(\'Test\');\n"
                            + "page.add(label);\n");
        } catch (ScriptException ex) {
            throw new ServletException(ex);
        }

//        final BasePage page = (BasePage) scriptEngine.get("page");
        page.lock();

        final Document document = page.buildDocument(request, response);
        Templating.getPresentationManager().servePage(document,
                                                      request,
                                                      response);

    }

}
