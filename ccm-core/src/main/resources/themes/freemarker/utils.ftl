<#--filedoc
    Utility functions for Freemarker based themes.
-->

<#--doc
    Gets the application of the page served from the model.

    @return The current application.
-->
<#function getPageApplication>
    <#return application>
</#function>

<#--doc
    Get the title of the current page.

    This will only work of the current page is a navigation page with a category 
    menu.

    @return The title of the current page
-->
<#function getPageTitle>
    <#return title>
</#function>