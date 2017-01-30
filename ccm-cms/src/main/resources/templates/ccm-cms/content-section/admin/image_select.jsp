<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.cms.ui.ImageSelectPage"/>
  <jsp:directive.page import="org.librecms.ContentSectionServlet"/>
  <jsp:directive.page import="org.librecms.ContentSection"/>
  <jsp:directive.page import="com.arsdigita.cms.dispatcher.Utilities"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.*"/>
  <jsp:directive.page import="java.util.Date"/>

  <jsp:declaration>
    private ImageSelectPage imageSelectPage = new ImageSelectPage();
  </jsp:declaration>

  <jsp:scriptlet>
    // Restore the wrapped request
    HttpServletRequest myRequest = DispatcherHelper.getRequest();
    DispatcherHelper.cacheDisable(response);

    ContentSection section = ContentSectionServlet.getContentSection(myRequest);

    if (! ContentSectionServlet.checkAdminAccess(myRequest, section)) {
      throw new com.arsdigita.cms.dispatcher.AccessDeniedException();
    }

    RequestContext context = DispatcherHelper.getRequestContext(myRequest);
    imageSelectPage.init();
    imageSelectPage.dispatch(myRequest, response, context);
  </jsp:scriptlet>
</jsp:root>
