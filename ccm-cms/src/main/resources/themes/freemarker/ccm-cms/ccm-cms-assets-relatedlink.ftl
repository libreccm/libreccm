<#--filedoc
    Functions for processing related links.

    @depcrecated Use ccm-cms/relatedlink-attachments.ftl instead
-->

<#import "/ccm-cms/relatedlink-attachments.ftl" as RelatedLinks>

<#--doc
    Generates a sorted sequence of hashes (see Freemarker documentation) 
    from the related links of a content item.

    @param item The model of the content item to use.

    @param linkListName: The name of the link list to use.

    @return The links attached to the provided item.
-->
<#function getRelatedLinks item linkListName="NONE">

    <#return RelatedLinks.getRelatedLinks(item linkListName)>

    <#--  <#return item["./links[./linkListName='${linkListName}']"]?sort_by("linkOrder")>  -->
</#function>

<#--doc
    *Internal* function for determing the type a related link.

    @param link The link

    @return The type of the link. Either `externalLink`, `internalLink` or `caption`.
-->
<#function _getLinkType link>
    <#return RelatedLinks.:_getLinkType(link)>
</#function>

<#--doc
    *Internal* function for getting parameters for the link.

    @param link The link

    @return Parameters for an internal link.
-->
<#function _getInternalLinkParameters link>
    <#return RelatedLinks._getInternalLinkParameters(link)>
</#function>

<#--doc
    *Internal* function for constructing the target URI of a related link.

    @param link The link model to use.

    @return The URL for the target of the link.
-->
<#function _getTargetUri link>
    <#return RelatedLinks._getTargetUri(link)>
</#function>

