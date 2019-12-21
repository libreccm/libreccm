<#--filedoc
    Functions for processing note attachments.

    @depcrecated Use ccm-cms/sidenote-attachments instead
-->

<#import "/ccm-cms/sidenote-attachments" as SideNotes>

<#--doc
    Generates a sorted sequence of hashes (see Freemarker documentation) for 
    the note attachments of a content item.

    @param item The model of the content item to use.

    @return The side notes attached to the item.
-->
<#function getNotes item>
    <#return item.sideNotes>
</#function>


