<#--filedoc
    Functions for processing article items.
-->

<#--doc
    Gets the lead text of the provided article.

    @param item The article item to use.

    @return The lead text of the article.
-->
<#function getLead item>
    <#return item.lead>
</#function>

<#--doc
    Checks if the provided item has a lead property.

    @param item The article item to use.

    @return `true` If the provided article has a lead text, `false` otherwise.
-->
<#function hasLead item>
    <#return (getLead(item)?length > 0)>
</#function>

<#--doc
    Gets the main text the the provided article.

    @param item The article item to use.

    @return The main text of the article.
-->
<#function getMainText item>
    <#return item.text>
</#function>