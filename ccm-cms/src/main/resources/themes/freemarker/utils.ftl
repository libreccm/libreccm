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

    Not supported in 7.0.0 yet, will always return an empty string.

    Get the title of the current page.

    This will only work of the current page with a category tree

    @return The title of the current page
-->
<#function getPageTitle>
    <#return "">
</#function>

<#--doc
    Get the hostname from the sitebanner data.

    @return The host name of the site.
-->
<#function getSiteHostName>
    <#return siteInfo.host>
</#function>

<#--doc
    Get the name of the site from the sitebanner data.

    @return The name of the site.
-->
<#function getSiteName>
    <#return siteInfo.name>
</#function>

<#--doc
    Get the domain of the site.

    @return The name of the site.
-->
<#function getSiteDomain>
    <#return siteInfo.domain>
</#function>

<#--doc
    A wrapper for the `_formatDateTime` function which adds missing numbers.
    `_formatDateTime` uses Java APIs for formatting which don't work well with
    incomplete dates. This function takes a date from the data model and checks
    if a component (year, month, day, hour, minute, second) is missing. If the
    the component is missing the function adds uses a default value of that
    component.

    @param style The date format style from the theme manifest to use.

    @param date the date to format.

    @return The formatted date.
-->
<#function formatDateTime style date>
    <#assign year   = date.year!0>
    <#assign month  = date.month!0>
    <#assign day    = date.day!0>
    <#assign hour   = date.hour!0>
    <#assign minute = date.minute!0>
    <#assign second = date.second!0>
    <#return _formatDateTime(style, year, month, day, hour, minute, second)>
</#function>

