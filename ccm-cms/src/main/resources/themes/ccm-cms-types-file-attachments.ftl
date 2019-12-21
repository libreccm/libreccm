<#--filedoc
    Functions for processing file attachments.

    @depcrecated Use ccm-cms/file-attachments.ftl
-->

<#import "/ccm-cms/file-attachments" as FileAttachments>

<#--doc
    Gets the file attachements of an content item.

    @param item The content item providing the file attachments.

    @return A sequence of the file attachments
-->
<#function getFileAttachments item>
    <#return FileAttachments.getFileAttachments(item)>
</#function>

<#--doc
    *Internal* function for determing the type of a file attachment.

    @param file The file attachment.

    @return The type the file. Either `caption` or `file`.
-->
<#function _getFileType file>
    <#return FileAttachments._getFileType(file)>
</#function>
