<#--filedoc
    Provides functions for processing the image attachemnts of a content item.

    @depcrecated Use ccm-cms/image-attachments.ftl instead
-->

<#import "/ccm-cms/image-attachments.ftl" as ImageAttachments>

<#--doc
    Creates a sorted sequence of hashes (see Freemarker docuementation) for the 
    image attachments of a content item.

    @param item The model of the content item to use.

    @return The images attached to the item.
-->
<#function getImageAttachments item>
    <#return ImageAttachments.getImageAttachments(item)>
</#function>

