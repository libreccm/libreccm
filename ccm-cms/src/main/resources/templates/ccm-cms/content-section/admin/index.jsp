<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.cms.ui.ContentSectionPage"/>
  <jsp:directive.page import="org.librecms.contentsection.ContentSection"/>
  <jsp:directive.page import="org.librecms.contentsection.ContentSectionServlet"/>
  <jsp:directive.page import="com.arsdigita.cms.dispatcher.Utilities"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.*"/>
  <jsp:directive.page import="com.arsdigita.web.LoginSignal"/>
  <jsp:directive.page import="com.arsdigita.web.Web"/>
  <jsp:directive.page import="java.util.Date"/>
  <jsp:directive.page import="org.libreccm.cdi.utils.CdiUtil"/>
  <jsp:directive.page import="org.libreccm.security.Shiro"/>

  <jsp:declaration>
    private ContentSectionPage sectionPage = new ContentSectionPage();
  </jsp:declaration>

  <jsp:scriptlet>
    // Restore the wrapped request
    HttpServletRequest myRequest = DispatcherHelper.getRequest();
    DispatcherHelper.cacheDisable(response);

    ContentSection section = ContentSectionServlet.getContentSection(myRequest);

    //if (Web.getWebContext().getUser() == null) {
    if (!CdiUtil.createCdiUtil().findBean(Shiro.class).getSubject().isAuthenticated()) {
        throw new LoginSignal(myRequest);
    } else if (! ContentSectionServlet.checkAdminAccess(myRequest, section)) {
        throw new com.arsdigita.dispatcher.AccessDeniedException();
    }

    RequestContext context = DispatcherHelper.getRequestContext(myRequest);
    sectionPage.init();
    sectionPage.dispatch(myRequest, response, context);
  </jsp:scriptlet>
</jsp:root>


