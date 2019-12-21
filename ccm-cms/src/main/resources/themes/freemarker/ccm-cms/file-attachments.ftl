<#--filedoc
    Functions for processing file attachments.
-->

<#--doc
    Gets the file attachements of an content item.

    @param item The content item providing the file attachments.

    @return A sorted sequence of hashes (see Freemarker documentation) containing 
    the data the file attachments. The following keys are available: 
    
    * `fileType`: Type of the attachments. Either `file` or `caption`
    * `mimeType`: The mime type of the file.
    * `mimeTypeLabel`: A human readable label for the mime type.
    * `fileSize`: The size of the file.
    * `fileExtension`: The extension part of the file name.
    * `fileId`: The ID of the file.
    * `fileName`: The name of the file.
    * `description`: The description of the file attachment.
    * `fileUrl`: The URL for downloading the file.
    * `fileOrder`: The value of the order property of the file attachment.
-->

<#function getFileAttachments item>
    <#return item.fileAttachments>
</#function>

<#--doc
    *Internal* function for determing the type of a file attachment.

    @param file The file attachment.

    @depcrecated without replacement.

    @return The type the file. Starting with version 7.0.0 always `file`.
-->
<#function _getFileType file>
    <#return "file">
</#function>

