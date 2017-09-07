/*
 * To change this license layout, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.libreccm.admin.ui;

import com.vaadin.server.ClassResource;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;

import javax.servlet.ServletContext;


/**
 *
 * @author <a href="mailto:jens.pelzetter@googlemail.com">Jens Pelzetter</a>
 */
public class Header extends CustomComponent {

    private static final long serialVersionUID = -8503268025688988953L;

    public Header() {
        super();

        final ServletContext servletContext = VaadinServlet
            .getCurrent()
            .getServletContext();

        final GridLayout layout = new GridLayout(5, 1);
        layout.setWidth("100%");
        layout.addStyleName("libreccm-header");

        final Label headerInfoLine = new Label("LibreCCM");
        headerInfoLine.setId("libreccm-headerinfoline");
        layout.addComponent(headerInfoLine, 3, 0, 4, 0);
        layout.setComponentAlignment(headerInfoLine, Alignment.TOP_RIGHT);

        final String logoPath;
        switch (servletContext.getInitParameter("ccm.distribution")
            .toLowerCase()) {
            case "libreccm":
                logoPath = "/themes/libreccm-default/images/libreccm.png";
                break;
            case "librecms":
                logoPath = "/themes/libreccm-default/images/librecms.png";
                break;
            case "aplaws":
                logoPath = "/themes/libreccm-default/images/aplaws.png";
                break;
            case "scientificcms":
                logoPath = "/themes/libreccm-default/images/scientificcms.png";
                break;
            default:
                logoPath = "/themes/libreccm-default/images/libreccm.png";
                break;
        }

        final Image logo = new Image(null, new ClassResource(logoPath));
        logo.setId("libreccm-logo");
        logo.addStyleName("libreccm-logo");
        layout.addComponent(logo, 0, 0);
        layout.setComponentAlignment(logo, Alignment.MIDDLE_LEFT);

        super.setCompositionRoot(layout);
    }

}
