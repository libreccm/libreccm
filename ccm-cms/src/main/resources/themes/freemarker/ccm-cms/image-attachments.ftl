<#--filedoc
    Provides functions for processing the image attachemnts of a content item.
-->

<#--doc
    Creates a sorted sequence of hashes (see Freemarker docuementation) for the 
    image attachments of a content item.

    @param item The model of the content item to use.

    @return The images attached to the provided item.
-->
<#function getImageAttachments item>
    <#return item.imageAttachments>
</#function>

