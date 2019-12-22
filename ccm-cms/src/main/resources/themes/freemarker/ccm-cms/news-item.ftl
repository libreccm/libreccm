<#import "/ccm-cms/article-item.ftl" as Article>

<#--filedoc
    Functions for News items
-->

<#--doc
    Gets the lead text of a news item

    @param item The news item to use.

    @return The lead text of the news item.
-->
<#function getLead item>
    <#return Article.getLead(item)>
</#function>

<#--doc
    Gets the main text of a news item

    @param item The news item to use.

    @return The main of the news item.
-->
<#function getMainText item>
    <#return Article.getMainText(item)>
</#function>

<#--doc
    Gets the date of a news item

    @param item The news item to use.

    @return The date of the news item.
-->
<#function getNewsDate item format="yyyy-MM-dd">
    <#return item.newDate?string(format)>
</#function>

<#--doc
    Gets the year of the news date.

    @param item The news item to use.

    @return The year of the news date.
-->
<#function getNewsDateYear item>
    <#return item.newsDate?string("yyyy")>
</#function>

<#--doc
    Gets the month of the news date.

    @param item The news item to use.

    @return The month of the news date.
-->
<#function getNewsDateMonth item>
    <#return item.newsDate?string("mm")>
</#function>

<#--doc
    Gets the day of the news date.

    @param item The news item to use.

    @return The day of the news date.
-->
<#function getNewsDateDay item>
    <#return item.newDate?string("dd")>
</#function>

<#--doc
    Gets the short name of the day of the news date.

    @param item The news item to use.

    @return The short name of the day of the news date.
-->
<#function getNewsDateDayNameShort item>
    <#return item.newDate?string("E")>
</#function>

<#--doc
    Gets the hour of the news date.

    @param item The news item to use.

    @return The hour of the news date.
-->
<#function newsDateHour item>
    <#return item.newDate?string("hh")>
</#function>

<#--doc
    Gets the minute of the news date.

    @param item The news item to use.

    @return The minute of the news date.
-->
<#function newsDateMinute item>
    <#return item.newDate?string("mm")>
</#function>

<#--doc
    Gets the second of the news date.

    @param item The news item to use.

    @return The second of the news date.
-->
<#function newsDateSecond item>
    <#return item.newDate?string("ss")>
</#function>

<#--doc
    Gets the news date in ISO format (`yyyy-mm-dd HH:mm:ss`).

    @param item The news item to use.

    @return The in ISO format.
-->
<#function getNewsDateIso item>
    <#return news.newsDate?string("yyyy-MM-dd hh:mm:ss")>
</#function>

