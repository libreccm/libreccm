<#--filedoc
    Functions for processing article items.

    @depcrecated Use ccm-cms/article-item.ftl instea
-->

<#import "/ccm-cms/article-item.ftl" as Article>

<#--doc
    Gets the lead text of the provided article.

    @param item The article item to use.

    @return The lead text of the article.
-->
<#function getLead item>
    <#return Article.getLead(item)>
</#function>

<#--doc
    Checks if the provided item has a lead property.

    @param item The article item to use.

    @return `true` If the provided article has a lead text, `false` otherwise.
-->
<#function hasLead item>
    <#return Article.hasLead(item)>
</#function>

<#--doc
    Gets the main text the the provided article.

    @param item The article item to use.

    @return The main text of the article.
-->
<#function getMainText item>
    <#return Article.getMainText(item)>
</#function>