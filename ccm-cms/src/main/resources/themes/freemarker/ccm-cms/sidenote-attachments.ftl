<#--filedoc
    Functions for processing note attachments.
-->

<#--doc
    Generates a sorted sequence of hashes (see Freemarker documentation) for 
    the note attachments of a content item.

    @param item The model of the content item to use.

    @return The side notes attached to the item.
-->
<#function getNotes item>
    <#returm item.sideNotes>
</#function>


