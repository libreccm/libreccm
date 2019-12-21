<#--filedoc
    Functions for processing Event items.

    @depcrecated Use ccm-cms/event-item.ftl
-->

<#import "/ccm-cms/event-item.ftl" as Event>

<#--doc
    Gets the lead text of an event item

    @param item The event item to use.

    @return The lead text of the event item.
-->

<#function getLead item>
    <#return Event.getLead(item)>
</#function>

<#--doc
    Gets the main text of an event item

    @param item The event item to use.

    @return The main of the event item.
-->
<#function getMainText item>
    <#return Event.getMainText(item)>
</#function>

<#--doc
    Gets the end date of an event item

    @param item The news event to use.

    @return The date of the event item.
-->
<#function getEndDate item>
    <#return Event.getEndDate(item)>
</#function>

<#--doc
    Gets the year of the end date of the event.

    @param item The event item to use.

    @return The year of the end date of the event.
-->
<#function getEndDateYear item>
    <#return Event.getEndDateYear(item)>
</#function>

<#--doc
    Gets the month of the end date of the event.

    @param item The event item to use.

    @return The month of the end date of the event.
-->
<#function getEndDateMonth item>
    <#return Event.getEndDateMonth(item)>
</#function>

<#--doc
    Gets the day of the end date of the event.

    @param item The event item to use.

    @return The day of the end date of the event.
-->
<#function getEndDateDay item>
    <#return Event.getEndDateDay(item)>
</#function>

<#--doc
    Gets the short name of the day of the end date of the event.

    @param item The event item to use.

    @return The short name of the day of the end date of the event.
-->
<#function getEndDateDayNameShort item>
    <#return Event.getEndDateDayNameShort(item)>
</#function>

<#--doc
    Gets the end time of the event.

    @param item The event item to use.

    @return The end time of the event.
-->
<#function getEndTime item>
    <#return Event.getEndTime(item)>
</#function>

<#--doc
    Gets the hour of the end time of the event.

    @param item The event item to use.

    @return The hour of the end time of the event.
-->
<#function getEndTimeHour item>
    <#return Event.getEndTimeHour(item)>
</#function>

<#--doc
    Gets the minute of the end time of the event.

    @param item The event item to use.

    @return The minute of the end time of the event.
-->
<#function getEndTimeMinute item>
    <#return Event.getEndTimeMinute(item)>
</#function>

<#--doc
    Gets the second of the end time of the event.

    @param item The event item to use.

    @return The second of the end time of the event.
-->
<#function getEndTimeSecond item>
    <#return Event.getEndTimeSecond(item)>
</#function>

<#--doc
    Gets the start date of an event item

    @param item The news event to use.

    @return The start of the event item.
-->
<#function getStartDate item>
    <#return Event.getStartDate(item)>
</#function>

<#--doc
    Gets the year of the start date of the event.

    @param item The event item to use.

    @return The year of the start date of the event.
-->
<#function getStartDateYear item>
    <#return Event.getStartDateYear(item)>
</#function>

<#--doc
    Gets the month of the start date of the event.

    @param item The event item to use.

    @return The month of the start date of the event.
-->
<#function getStartDateMonth item>
    <#return Event.getStartDateMonth(item)>
</#function>

<#--doc
    Gets the day of the start date of the event.

    @param item The event item to use.

    @return The day of the start date of the event.
-->
<#function getStartDateDay item>
    <#return Event.getStartDateDay(item)>
</#function>

<#--doc
    Gets the short name of the day of the start date of the event.

    @param item The event item to use.

    @return The short name of the day of the start date of the event.
-->
<#function getStartDateDayNameShort item>
    <#return Event.getStartDateDayNameShort(item)>
</#function>

<#--doc
    Gets the start time of the event.

    @param item The event item to use.

    @return The start time of the event.
-->
<#function getStartTime item>
    <#return Event.getStartTime(item)>
</#function>

<#--doc
    Determines if the provided event item has a start time

    @param item The event item to use.

    @return `true` if the provided event item has a start time, `false` otherwise.
-->
<#function hasStartTime item>
    <#return Event.hasStartTime(item)>
</#function>

<#--doc
    Determines if the provided event item has a end time

    @param item The event item to use.

    @return `true` if the provided event item has a end time, `false` otherwise.
-->
<#function hasEndTime item>
    <#return Event.getEndTime(item)>
</#function>

<#--doc
    Determines if the provided event item has a start date.

    @param item The event item to use.

    @return `true` if the provided event item has a start date, `false` otherwise.
-->
<#function hasStartDate item>
    <#return Event.hasStartDate(item)>
</#function>

<#--doc
    Determines if the provided event item has a end date.

    @param item The event item to use.

    @return `true` if the provided event item has a end date, `false` otherwise.
-->
<#function hasEndDate item>
    <#return Event.getEndDate(item)>
</#function>

<#--doc
    Gets the hour of the start time of the event.

    @param item The event item to use.

    @return The hour of the start time of the event.
-->
<#function getstartTimeHour item>
    <#return Event.getstartTimeHour(item)>
</#function>

<#--doc
    Gets the minute of the start time of the event.

    @param item The event item to use.

    @return The minute of the start time of the event.
-->
<#function getstartTimeMinute item>
    <#return Event.getstartTimeMinute(item)>
</#function>

<#--doc
    Gets the second of the start time of the event.

    @param item The event item to use.

    @return The second of the start time of the event.
-->
<#function getstartTimeSecond item>
    <#return Event.getstartTimeSecond(item)>
</#function>

<#--doc
    Gets the location of the event.

    @param item The event item to use.

    @return The value of the `location` property of the event.
-->
<#function getLocation(item)>
    <#return Event.getLocation(item)>
</#function>

<#--doc
    Gets the main contributor of the event.

    @param item The event item to use.

    @return The value of the `mainContributor` property of the event.
-->
<#function getMainContributor item>
    <#return Event.getMainContributor(item)>
</#function>

<#--doc
    Gets the type of the event.

    @param item The event item to use.

    @return The value of the `eventType` property of the event.
-->
<#function getEventType item>
    <#return Event.getEventType(item)>
</#function>

<#--doc
    Gets the cost of the event.

    @param item The event item to use.

    @return The value of the `cost` property of the event.
-->
<#function getCost item>
    <#return Event.getCost(item)>
</#function>

<#--doc
    Gets the map link for the event.

    @param item The event item to use.

    @return The value of the `mapLink` property of the event.
-->
<#function getMapLink item>
    <#return Event.getMapLink(item)>
</#function>

<#--doc
    Gets the addendium  of the event.

    @param item The event item to use.

    @return The value of the `eventDate` property of the event.
-->
<#function getEventDateAddendum item>
    <#return Event.getEventDateAddendum(item)>
</#function>




