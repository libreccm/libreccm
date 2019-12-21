<#--filedoc
    Functions for Bookmark items.
-->

<#--filedoc
    Functions for Bookmark items.

    @depcrecated Use ccm-cms/bookmark-item.ftl 
-->

<#import "ccm-cms/bookmark-item.ftl" as Bookmark>

<#--doc 
    Gets the description of a bookmark.

    @param item The bookmark item to use.

    @return The description of the provided bookmark.
-->
<#function getDescription item>
    <#return Bookmark.getDescription(item)>
</#function>

<#--doc
    Gets the link of a bookmark.

    @param item The bookmark item to use.

    @return The link of the provided bookmark.
-->
<#function getLink item>
    <#return Bookmark.getLink(item)>
</#function>


