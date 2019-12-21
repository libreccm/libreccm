<#--filedoc
    Functions for processing MultiPartArticles.
-->

<#--doc
    Get the summary of a multi part article.

    @param item The model of the multi part article to use.

    @return The summary of the multi part article.
-->
<#function getSummary item>
    <#return item.summary>
</#function>

<#--doc
    Get the visible sections of a multi part article.

    @param item The model of the multi part article to use.

    @return The visible sections of the multi part article.
-->
<#function getSections item>
    <#return item.sections>
</#function>

<#--doc
    Gets the title of a section.

    @param section The model of the section to use, as returned by `getSections`.

    @return The title of the section.
-->
<#function getSectionTitle section>
    <#return section.title>
</#function>

<#--doc
    Gets the content of a section.

    @param section The model of the section to use, as returned by `getSections`.

    @return The content of the section.
-->
<#function getSectionContent section>
    <#return section.text>
</#function>

<#--doc
    Gets the rank (sort key) of a section.

    @param section The model of the section to use, as returned by `getSections`.

    @return The rank of the section.
-->
<#function getSectionRank section>
    <#return section.rank>
</#function>

<#--doc
    Gets the number of the current page.

    @param item The model of the multi part article to use.

    @return The number of the current page.
-->
<#function getPageNumber item>
    <#return item.pageNumber>
</#function>

<#--doc
    Gets the number of the pages.

    @param item The model of the multi part article to use.

    @return The number of the pages.
-->
<#function getNumberOfPages item>
    <#return item.numberOfPages>
</#function>

<#--doc
    Determines if a link to the previous page is available.

    @param item The model of the multi part article to use.

    @return `true` if the link is available, `false` otherwise.
-->
<#function hasPreviousPage item>
    <#return (item.pageNumber != "all" && item.numberOfPages > 1)>
</#function>

<#--doc
    Determines if a link to the next page is available.

    @param item The model of the multi part article to use.

    @return `true` if the link is available, `false` otherwise.
-->
<#function hasNextPage item>
    <#return (item.pageNumber != "all" && item.pageNumber < itemnumberOfPages)>
</#function>

<#--doc
    Determines of the multi part article has multiple pages.

    @param item The model of the multi part article to use.

    @return `true` if the article has multiple pages, `false` otherwise.
-->
<#function hasMultiplePages item>
    <#return item.pageNumber != "all" && item.pageNumber != "1">
</#function>

<#--doc
    Gets the link to the previous page.

    @param item The model of the multi part article to use.

    @return The link to the previous page.
-->
<#function getLinkToPreviousPage item>
    <#return "?page${item.pageNumber - 1}">
</#function>

<#--doc
    Gets the link to the next page.

    @param item The model of the multi part article to use.

    @return The link to the next page.
-->
<#function getLinkToNextPage item>
    <#return "?page${item.pageNumber + 1}">
</#function>

<#--doc
    Gets the link for showing all sections on one page..

    @param item The model of the multi part article to use.

    @return The link for showing all sections on one page.
-->
<#function getAllSectionsLink item>
    <#return "?page=all">
</#function>

