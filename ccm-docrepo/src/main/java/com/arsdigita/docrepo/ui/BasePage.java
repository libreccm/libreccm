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
package com.arsdigita.docrepo.ui;


import com.arsdigita.bebop.Component;
import com.arsdigita.bebop.Container;
import com.arsdigita.bebop.DimensionalNavbar;
import com.arsdigita.bebop.Label;
import com.arsdigita.bebop.Link;
import com.arsdigita.bebop.Page;
import com.arsdigita.bebop.PageState;
import com.arsdigita.bebop.SimpleContainer;
import com.arsdigita.bebop.event.PrintEvent;
import com.arsdigita.bebop.event.PrintListener;
import com.arsdigita.bebop.parameters.IntegerParameter;
import com.arsdigita.docrepo.util.GlobalizationUtil;
import com.arsdigita.util.Assert;
import com.arsdigita.web.Web;
import com.arsdigita.xml.Element;
import org.libreccm.web.CcmApplication;

/**
 * <p>BasePage class</p>
 *
 * @author <a href="mailto:jparsons@arsdigita.com">Jim Parsons</a>
 * @author <a href="mailto:tosmers@uni-bremen.de">Tobias Osmers</a>
 */
public class BasePage extends Page {

    private final Container globalContainer;
    private final Container headerContainer;
    private final Container bodyContainer;
    private final Container footerContainer;

    private IntegerParameter selected = new IntegerParameter("m");

    // Todo: add strings to package properties
    public static final String DOC_GLOBAL_ELEMENT = "docs:global";
    public static final String DOC_HEADER_ELEMENT = "docs:header";
    public static final String DOC_BODY_ELEMENT = "docs:body";
    public static final String DOC_FOOTER_ELEMENT = "docs:footer";
    public static final String DOC_XML_NS = "http://www.redhat.com/docs/1.0";

    // There are 2 views: user and admin. view determines which context
    // bar and view link to show.
    private String view;
    private Link viewLink;


    /**
     * Default constructor.
     */
    public BasePage() {
        this(null);
    }

    /**
     * Overloaded constructor.
     *
     * @param view A String that specifies which application view to
     * show.  Valid values: "user", "admin" and "null".  If view is
     * "null", there will be no right-hand navigation link.  Note -
     * We've decided not to have the right-hand navigation link at
     * all. Instead, you should create tabs.  So, once the
     * applications are migrated, view will always be null and we can
     * remove view altogether.
     */
    public BasePage(String view) {
        super(new Label(), new SimpleContainer());

        setClassAttr("DOCS");

        m_panel = new Panel();

        addGlobalStateParam(selected);

        globalContainer = new SimpleContainer(DOC_GLOBAL_ELEMENT, DOC_XML_NS);
        headerContainer = new SimpleContainer(DOC_HEADER_ELEMENT, DOC_XML_NS);
        bodyContainer   = new SimpleContainer(DOC_BODY_ELEMENT, DOC_XML_NS);
        footerContainer = new SimpleContainer(DOC_FOOTER_ELEMENT, DOC_XML_NS);

        super.add(globalContainer);
        super.add(headerContainer);
        super.add(bodyContainer);
        super.add(footerContainer);

        this.view = view;
    }

    /**
     * Getter for the global container.
     *
     * @return The global container.
     */
    public Container getGlobal() {
        return globalContainer;
    }

    /**
     * Getter for the header container.
     *
     * @return The header container.
     */
    public Container getHeader() {
        return headerContainer;
    }

    /**
     * Getter for the body container.
     *
     * @return The body container.
     */
    public Container getBody() {
        return bodyContainer;
    }

    /**
     * Getter for the footer container.
     *
     * @return The footer container.
     */
    public Container getFooter() {
        return footerContainer;
    }

    /**
     * Locks the page and all its components against further modifications.
     *
     * <p>Locking a page helps in finding mistakes that result from modifying a
     * page's structure.</P>
     */
    @Override
    public void lock() {
        buildPage();

        super.lock();
    }

    /**
     * Builds the page.
     *
     * Only the PortalPage.lock() should invoke this method, though users
     * of this class may sometimes want to <em>override</em> this method.
     *
     * Todo: resolvable or deletable?
     * Context Bar temporarily deactivated until the functionality to create
     * multiple repositories (table docs_mounted) is restored. Because
     * currently there is only one repository mounted there is no relevant
     * context
     */
    protected final void buildPage() {
        buildTitle();
        //buildContextBar();
        buildGlobal(getGlobal());
        buildHeader(getHeader());
        buildBody(getBody());
        buildFooter(getFooter());
    }

    /**
     * Builds the title.
     */
    protected void buildTitle() {
        /**
         * Internal class.
         */
        class ApplicationAdminLabelPrinter implements PrintListener {
            public void prepare(PrintEvent e) {
                Label targetLabel = (Label)e.getTarget();

                CcmApplication application = Web.getWebContext().getApplication();

                Assert.exists(application, CcmApplication.class);

                targetLabel.setLabel(application.getTitle() + " Administration");
            }
        }

        if (view != null && view.equals("admin")) {
            setTitle(new Label(new ApplicationAdminLabelPrinter()));
        } else {
            setTitle(new Label(new CurrentApplicationLabelPrinter()));
        }
    }

    /**
     * Builds the context bar.
     *
     * Todo: unused method.. delete?
     */
    protected void buildContextBar() {
        DimensionalNavbar navbar = new DimensionalNavbar();

        navbar.setClassAttr("portalNavbar");
        navbar.add(new Link(new ParentApplicationLinkPrinter()));
        navbar.add(new Link(new CurrentApplicationLinkPrinter()));

        getHeader().add(navbar);
    }

    /**
     * Builds by default the global container in the base page.
     *
     * @param global The global container.
     */
    protected void buildGlobal(Container global) {
        Link link = new Link(new Label(GlobalizationUtil.globalize("ui.workspace.sign_out")),  "/register/logout");

        link.setClassAttr("signoutLink");
        getGlobal().add(link);
    }

    /**
     * Builds by default the header container in the base page.
     *
     * @param header The header container.
     */
    protected void buildHeader(Container header) {
        if (view != null) {
            if (view.equals("user")) {
                viewLink = new Link(new Label(GlobalizationUtil.globalize("ui.view.admin")), "./admin/index.jsp") {
                    public boolean isVisible(PageState ps) {
                        return userIsAdmin(ps);
                    }};
            } else if (view.equals("admin")) {
                viewLink = new Link( new Label(GlobalizationUtil.globalize("ui.view.user")),  "../index.jsp");
            }
        }

        if (viewLink != null) {
            viewLink.setClassAttr("portalControl");

            header.add(viewLink);
        }
    }

    /**
     * Builds the body container in the base page. By default
     * nothing.
     *
     * @param body The body container.
     */
    protected void buildBody(Container body) {}

    /**
     * Builds the footer container in the base page. By default
     * nothing.
     *
     * @param footer The body container.
     */
    protected void buildFooter(Container footer) {}

    
    /**
     * Represents a panel as a simple container.
     */
    private class Panel extends SimpleContainer {
        @Override
        public void generateXML(PageState ps, Element p) {
            Component selected = getSelected(ps);
            if (selected == null) {
                super.generateXML(ps, p);
            } else {
                SimpleContainer fakeBody = new SimpleContainer(DOC_BODY_ELEMENT, DOC_XML_NS);
                fakeBody.add(selected);

                Element parent = generateParent(p);

                headerContainer.generateXML(ps, parent);
                footerContainer.generateXML(ps, parent);
                fakeBody.generateXML(ps, parent);
            }
        }
    }

    /**
     * Gets the selected component by the given page state.
     *
     * @param ps The given page state.
     * @return The slected component.
     */
    private Component getSelected(PageState ps) {
        Integer stateIndex = (Integer) ps.getValue(selected);
        Component c = null;
        if (stateIndex != null) {
            c = getComponent(stateIndex);
        }

        return c;
    }

    /**
     * Sets the selected component by a given page state and component.
     *
     * @param ps The page state.
     * @param c The component being selected.
     */
    private void setSelected(PageState ps, Component c) {
        if (c == null) {
            ps.setValue(selected, null);
        } else {
            ps.setValue(selected, stateIndex(c));
        }
    }

    /**
     * Makes the given component the only visible component between
     * the header and footer of this page.
     *
     * @param ps The page state.
     * @param c The given component.
     */
    public void goModal(PageState ps, Component c) {
        Component oldc = getSelected(ps);
        if (oldc != null) {
            oldc.setVisible(ps, false);
        }
        c.setVisible(ps, true);
        setSelected(ps, c);
    }

    /**
     * Clears the currently selected modal component if it has been set.
     *
     * @param ps The page state to be cleared.
     */
    public void goUnmodal(PageState ps) {
        Component oldc = getSelected(ps);
        if (oldc != null) {
            oldc.setVisible(ps, false);
        }
        setSelected(ps, null);
    }

    /**
     * Checks on the permission level, weather the user is admin by the
     * given page state.
     *
     * @param ps The given page state
     * @return true if the user is admin, false otherwise.
     *
     * Todo: does not need param, can be changed
     * Todo: Usage of Permission classes don't work

     */
    private boolean userIsAdmin(PageState ps) {
//        PermissionDescriptor permDescriptor =
//                new PermissionDescriptor(PrivilegeDescriptor.ADMIN,
//                        Web.getWebContext().getApplication(),
//                        Kernel.getContext().getParty());
//        return PermissionService.checkPermission(permDescriptor);
        return false;
    }

    /**
     * Adds a component to the body.
     *
     * @param pc the component to be added
     */
    @Override
    public void add(Component pc) {
        Assert.isUnlocked(this);
        bodyContainer.add(pc);
    }

    /**
     * Class.
     */
    protected class CurrentApplicationLinkPrinter implements PrintListener {
        public CurrentApplicationLinkPrinter() {
            // Empty
        }

        public void prepare(PrintEvent e) {
            Link link = (Link) e.getTarget();
            //PageState pageState = e.getPageState();

            CcmApplication app = Web.getWebContext().getApplication();

            Assert.exists(app, CcmApplication.class);

            link.setChild(new Label(app.getTitle().toString()));
            link.setTarget(app.getPrimaryUrl());
        }
    }

    /**
     * Class.
     */
    protected class ParentApplicationLinkPrinter implements PrintListener {
        public ParentApplicationLinkPrinter() {
            // Empty
        }

        public void prepare(PrintEvent e) {
            Link link = (Link) e.getTarget();
            //PageState pageState = e.getPageState();

            CcmApplication app = Web.getWebContext().getApplication();

            Assert.exists(app, CcmApplication.class);

            CcmApplication parent = (CcmApplication) app.getParent();
            if (parent != null ) {
                link.setChild(new Label(parent.getTitle().toString()));
                link.setTarget(parent.getPrimaryUrl());
            } else {
                link.setChild(new Label("/"));
                link.setTarget(com.arsdigita.web.URL.root().toString());
            }
        }
    }

    /**
     * Class.
     */
    protected class CurrentApplicationLabelPrinter implements PrintListener {

        public CurrentApplicationLabelPrinter() {
            // Empty
        }

        public void prepare(PrintEvent e) {
            Label label = (Label) e.getTarget();
            //PageState pageState = e.getPageState();

            CcmApplication app = Web.getWebContext().getApplication();

            Assert.exists(app, CcmApplication.class);

            label.setLabel(app.getTitle().toString());
        }
    }
}
