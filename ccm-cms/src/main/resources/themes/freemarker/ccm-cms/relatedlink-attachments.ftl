<#--filedoc
    Functions for processing related links.
-->

<#--doc
    Generates a sorted sequence of hashes (see Freemarker documentation) 
    from the related links of a content item.

    @param item The model of the content item to use.

    @param linkListName: The name of the link list to use.

    @return The links attached to the provided item.
-->
<#function getRelatedLinks item linkListName="NONE">

    <#return item.relatedLinks[linkListName]>
</#function>

<#--doc
    *Internal* function for determing the type a related link.

    @param link The link

    @return The type of the link..
-->
<#function _getLinkType link>
    <#return link.type>
</#function>

<#--doc
    *Internal* function for getting parameters for the link.

    @param link The link

    @return Parameters for an internal link.
-->
<#function _getInternalLinkParameters link>
    <#return link.parameters>
</#function>

<#--doc
    *Internal* function for constructing the target URI of a related link.

    @param link The link model to use.

    @return The URL for the target of the link.
-->
<#function _getTargetUri link>
    <#return link.targetUri>
</#function>