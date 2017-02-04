<jsp:root xmlns:jsp="http://java.sun.com/JSP/Page" version="1.2">

  <jsp:directive.page import="com.arsdigita.bebop.Page"/>
  <jsp:directive.page import="com.arsdigita.cms.ui.ContentItemPage"/>
  <jsp:directive.page import="org.librecms.ContentSectionServlet"/>
  <jsp:directive.page import="org.librecms.ContentSection"/>
  <jsp:directive.page import="com.arsdigita.cms.dispatcher.Utilities"/>
  <jsp:directive.page import="com.arsdigita.dispatcher.*"/>
  <jsp:directive.page import="com.arsdigita.web.LoginSignal"/>
  <jsp:directive.page import="com.arsdigita.web.Web"/>
  <jsp:directive.page import="org.apache.logging.log4j.Logger"/>
  <jsp:directive.page import="org.apache.logging.log4j.LogManager"/>
  <jsp:directive.page import="java.util.Date"/>


  <jsp:declaration>
    private static final Logger s_log =
        LogManager.getLogger("content-section.www.admin.item.jsp");
    private ContentItemPage itemPage = null;
    private Date timestamp = new Date(0);
  </jsp:declaration>

  <jsp:scriptlet>
    s_log.debug("entered item.jsp's service method");
    // Restore the wrapped request
    HttpServletRequest myRequest = DispatcherHelper.getRequest();
    DispatcherHelper.cacheDisable(response);

    // ?? just doubles the initial line
    // request = DispatcherHelper.getRequest();

    ContentSection section = ContentSectionServlet.getContentSection(myRequest);


    if (Web.getWebContext().getUser() == null) {
        throw new LoginSignal(myRequest);
    } else if (! ContentSectionServlet.checkAdminAccess(myRequest, section)) {
        throw new com.arsdigita.cms.dispatcher.AccessDeniedException();
    }

    // page needs to be refreshed when content types or authoring kits
    // in the section change
    synchronized(this) {
        if (Utilities.getLastSectionRefresh(section).after(timestamp)) {
            s_log.debug("refreshing itemPage");
	    s_log.debug("Creating new ContentItemPage instance...");
            itemPage = new ContentItemPage();
	    s_log.debug("Calling init on new instance...");
            itemPage.init();
	    s_log.debug("Creating time stamp...");
            timestamp = new Date();
        }
    }

    s_log.debug("Starting dispatch process...");
    RequestContext context = DispatcherHelper.getRequestContext(myRequest);
    if(itemPage == null) {
      s_log.warn("WARNING: itemPage is NULL");
    }
    else {
      s_log.info("ALL OK: itemPage is not null");
    }      
    itemPage.dispatch(myRequest, response, context);
    s_log.debug("exited item.jsp's service method");
  </jsp:scriptlet>
</jsp:root>



