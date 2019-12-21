<#import "./article-item.ftl" as Article>

<#--filedoc
    Functions for processing Event items.
-->

<#--doc
    Gets the lead text of an event item

    @param item The event item to use.

    @return The lead text of the event item.
-->

<#function getLead item>
    <#return Article.getLead(item)>
</#function>

<#--doc
    Gets the main text of an event item

    @param item The event item to use.

    @return The main of the event item.
-->
<#function getMainText item>
    <#return Article.getMainText(item)>
</#function>

<#--doc
    Gets the end date of an event item

    @param item The news event to use.

    @return The date of the event item.
-->
<#function getEndDate item>
    <#return item.endDate>
</#function>

<#--doc
    Gets the year of the end date of the event.

    @param item The event item to use.

    @return The year of the end date of the event.
-->
<#function getEndDateYear item>
    <#return item.endDate.year>
</#function>

<#--doc
    Gets the month of the end date of the event.

    @param item The event item to use.

    @return The month of the end date of the event.
-->
<#function getEndDateMonth item>
    <#return item.endDate.month>
</#function>

<#--doc
    Gets the day of the end date of the event.

    @param item The event item to use.

    @return The day of the end date of the event.
-->
<#function getEndDateDay item>
    <#return item.endDate.day>
</#function>

<#--doc
    Gets the short name of the day of the end date of the event.

    @param item The event item to use.

    @return The short name of the day of the end date of the event.
-->
<#function getEndDateDayNameShort item>
    <#return item.endDate.day>
</#function>

<#--doc
    Gets the end time of the event.

    @param item The event item to use.

    @return The end time of the event.
-->
<#function getEndTime item>
    <#return item.endTime>
</#function>

<#--doc
    Gets the hour of the end time of the event.

    @param item The event item to use.

    @return The hour of the end time of the event.
-->
<#function getEndTimeHour item>
    <#return item.endTime.hour>
</#function>

<#--doc
    Gets the minute of the end time of the event.

    @param item The event item to use.

    @return The minute of the end time of the event.
-->
<#function getEndTimeMinute item>
    <#return item.endTime.minute>
</#function>

<#--doc
    Gets the second of the end time of the event.

    @param item The event item to use.

    @return The second of the end time of the event.
-->
<#function getEndTimeSecond item>
    <#return item.endTime.second>
</#function>

<#--doc
    Gets the start date of an event item

    @param item The news event to use.

    @return The start of the event item.
-->
<#function getStartDate item>
    <#return item.startDate>
</#function>

<#--doc
    Gets the year of the start date of the event.

    @param item The event item to use.

    @return The year of the start date of the event.
-->
<#function getStartDateYear item>
    <#return item.startDate.year>
</#function>

<#--doc
    Gets the month of the start date of the event.

    @param item The event item to use.

    @return The month of the start date of the event.
-->
<#function getStartDateMonth item>
    <#return item.startDate.minute>
</#function>

<#--doc
    Gets the day of the start date of the event.

    @param item The event item to use.

    @return The day of the start date of the event.
-->
<#function getStartDateDay item>
    <#return item.startDate.day>
</#function>

<#--doc
    Gets the short name of the day of the start date of the event.

    @param item The event item to use.

    @return The short name of the day of the start date of the event.
-->
<#function getStartDateDayNameShort item>
    <#return item.startDate.day>
</#function>

<#--doc
    Gets the start time of the event.

    @param item The event item to use.

    @return The start time of the event.
-->
<#function getStartTime item>
    <#return item.startTime>
</#function>

<#--doc
    Determines if the provided event item has a start time

    @param item The event item to use.

    @return `true` if the provided event item has a start time, `false` otherwise.
-->
<#function hasStartTime item>
    <#return item.startTime??>
</#function>

<#--doc
    Determines if the provided event item has a end time

    @param item The event item to use.

    @return `true` if the provided event item has a end time, `false` otherwise.
-->
<#function hasEndTime item>
    <#return item.endTime??>
</#function>

<#--doc
    Determines if the provided event item has a start date.

    @param item The event item to use.

    @return `true` if the provided event item has a start date, `false` otherwise.
-->
<#function hasStartDate item>
    <#return item.startDate??>
</#function>

<#--doc
    Determines if the provided event item has a end date.

    @param item The event item to use.

    @return `true` if the provided event item has a end date, `false` otherwise.
-->
<#function hasEndDate item>
    <#return item.endDate??>
</#function>

<#--doc
    Gets the hour of the start time of the event.

    @param item The event item to use.

    @return The hour of the start time of the event.
-->
<#function getstartTimeHour item>
    <#return item.startTime.hour>
</#function>

<#--doc
    Gets the minute of the start time of the event.

    @param item The event item to use.

    @return The minute of the start time of the event.
-->
<#function getstartTimeMinute item>
    <#return item.startTime.minute>
</#function>

<#--doc
    Gets the second of the start time of the event.

    @param item The event item to use.

    @return The second of the start time of the event.
-->
<#function getstartTimeSecond item>
    <#return item.startTime.second>
</#function>

<#--doc
    Gets the location of the event.

    @param item The event item to use.

    @return The value of the `location` property of the event.
-->
<#function getLocation(item)>
    <#return item.location>
</#function>

<#--doc
    Gets the main contributor of the event.

    @param item The event item to use.

    @return The value of the `mainContributor` property of the event.
-->
<#function getMainContributor item>
    <#return item.mainContributor>
</#function>

<#--doc
    Gets the type of the event.

    @param item The event item to use.

    @return The value of the `eventType` property of the event.
-->
<#function getEventType item>
    <#return item.eventType>
</#function>

<#--doc
    Gets the cost of the event.

    @param item The event item to use.

    @return The value of the `cost` property of the event.
-->
<#function getCost item>
    <#return item.cost]>
</#function>

<#--doc
    Gets the map link for the event.

    @param item The event item to use.

    @return The value of the `mapLink` property of the event.
-->
<#function getMapLink item>
    <#return item.mapLink>
</#function>

<#--doc
    Gets the addendium  of the event.

    @param item The event item to use.

    @return The value of the `eventDate` property of the event.
-->
<#function getEventDateAddendum item>
    <#return item.eventDate>
</#function>




