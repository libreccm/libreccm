<#--filedoc
    Functions for Bookmark items.
-->

<#--doc 
    Gets the description of a bookmark.

    @param item The bookmark item to use.

    @return The description of the provided bookmark.
-->
<#function getDescription item>
    <#return item.description>
</#function>

<#--doc
    Gets the link of a bookmark.

    @param item The bookmark item to use.

    @return The link of the provided bookmark.
-->
<#function getLink item>
    <#return item.link>
</#function>

