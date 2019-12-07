<#--filedoc
    This library provides functions and macros for processing the data of 
    content items.
-->

<#--doc
    Gets the current content item, or more exactly the model of the current
    content item from the model. This function only works on cms pages and
    not on navigation pages. For navigation pages use the functions
    provided by ccm-navigation for extracting the index item.

    @return The model of the current content item.
-->
<#function getContentItem container="">
    <#if container == "">
        <#return contentItem>
    <#else>
        <#return [container].contentItem>
    </#if>
</#function>

<#--doc
    Includes the template for the type of the provided content item 
    for the provided view and style. The paramters view and style are
    optional. If not provided the default value "detail" is used for 
    the view parameter.

    @param item The data model of the content item itself. There are 
    several functions for extracting this provided by other modules.

    @param view View type to use. Either `detail` or `list`. Default 
    value is `detail`.

    @param style Style to use. Styles allow it to use different 
    templates for `detail` or `list` views depending on the context were the 
    content item is displayed.
-->
<#macro contentItem item view="detail" style="">

    <#--<pre>
        item["/objectType"]: ${item["./objectType"]?size}
        item["/nav:attribute[@name='objectType']"]: ${item["./nav:attribute[@name='objectType']"]?size}
    </pre>-->

    
    <#if (item.contentType.name?length > 0)>
        <#include getContentItemTemplate(item.contentType.name, view, style)>
    <#else>
        <#include getContentItemTemplate("org.librecms.contentsection.ContentItem", view, style)>
    </#if>    
</#macro>

<#--doc
    Retrieve the title of a content item.

    @param item The content item.

    @return The title of the content item.
-->
<#function getItemTitle item>
    <#return item.title>
</#function>

<#--doc
    Get the title of the current page. This function tries several possible 
    sources for the title of the current page and uses to first source found.

    The possible sources are:

    * The title of the primary content item. For items of the types
      Glossary Item and FAQ Item special titles are used.
    * If the title `AtoZ` is provided by Bebop the localized text 
      `layout.page.title.atoz` used.
    * If the title `Search` is provided by Bebop

    @param useCategoryMenu The name of the category menu to use for retrieving 
    the page title.

    @param useRootIndexItemTitle Wether to use the title of the index item of 
    the root category as page title.

    @return The title of the current page.
-->
<#function getPageTitle useCategoryMenu='categoryMenu' useRootIndexItemTitle=false>

    <#if contentItem??>
        <#return contentItem.title>        
    <#elseif (application == 'login')>
        <#return getLocalizedText('layout.page.title.login')>
    <#elseif (application == 'sitemap'))>
        <#return getLocalizedText('layout.page.title.sitemap')>
    <#elseif (application == 'PublicPersonalProfile')>
        <#return model["/bebop:page/nav:greetingItem/cms:item/title"].@@text>
    <#else>
        <#return pageTitle>
    </#if>

</#function>

<#--doc 
    Retrieves the summary/lead text of an content item. The function
    will check several possible sources.

    @param item The item from which the the summary is read.

    @return The value of the `summary` or `lead` property or the provided 
    content item. If the content item does not have a such a property an 
    empty string is returned.
-->
<#function getItemSummary item>
    <#if (item.lead??)>
        <#return item.lead>
    <#elseif (item.summary??)>
        <#return item.summary>
    <#else>
        <#return "">
    </#if>    
</#function>

<#--doc
    Retrieve the value the `pageDescription` property of a conten item. 
    If the provided content item does not have a `pageDescription` property
    an empty string is returned.

    @param item The content item from which the description is read.

    @return The value of the `pageDescription` property of the provided content
    item. If the provided content item does not have such a property an 
    empty string is returned.
-->
<#function getPageDescription item>
    <#if (item.pageDescription??)>
        <#return item.pageDescription>
    <#else>
        <#return "">
    </#if>
</#function>

<#--doc
    Generated a link to a content using the OID of the content item.

    @param oid The OID of the content.

    @return A link to the content item identified by the provided OID.
-->
<#function generateContentItemLink oid>
    <#return "./${oid}">
</#function>

<#--doc
    Generates the the edit link for the provided content item. The link
    is generated using the `editLink` property which is only present in the 
    model if the current user is permitted to edit the item.

    @param item The item for which the edit link is generated.

    @return An edit link for the item. If the provided item does not have an
    `editLink` property an empty string is returned.
-->
<#function getEditLink item>
    <#if (item.editLink??)>
        <#return item.editLink>
    <#else>
        <#return "">
    </#if>
</#function>