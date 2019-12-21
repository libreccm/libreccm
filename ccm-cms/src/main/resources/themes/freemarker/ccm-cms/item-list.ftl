<#--filedoc
    Functions for accessing item lists and their properties.
-->

<#--doc
    Retrieve the item (models) of an object list. This function can deal with 
    several different types of object lists, including `SimpleObjectList`, 
    `ComplexObjectList` and `CustomziableObjectList`.

    @param listId The ID of the object list from the the items are retrieved.
    @param containerId The ID of the container of the list

    @return The models for the entries in the object list. If no list with 
    the provided `listId` is found and empty sequence is returned.
-->
<#function getItems listId containerId="container">
    <#return containerId?eval[listId].items>
</#function>

<#--doc
    Gets the number of objects/items in an object list.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The number of objects in the list. If no list with the provided
    `listId` is found `0` is returned.
-->
<#function getObjectCount listId containerId="container">
    <#return getItems(listId, containerId)?size>
</#function>

<#--doc
    Gets the base URL for the paginator of an object list.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The base URL for the the paginator.
-->
<#function getPaginatorBaseUrl listId containerId="container">
    <#return containerId?eval[listId].paginator.baseUrl
</#function>

<#--doc
    Gets the index of the first item shown on the current page.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The index of the first item shown on the current page.
-->
<#function getPaginatorBegin listId containerId="container">
    <#return containerId?eval[listId].paginator.start>
</#function>

<#--doc
    Gets the index of the last item shown on the curent page.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The index of the last item shown on the current page.
-->
<#function getPaginatorEnd listId containerId="container">
    <#return containerId?eval[listId].paginator.end>
</#function>

<#--doc
    Get the number of pages of an object list.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The number of pages of the object list. If no list with the 
    provided `listId` is found the function will return `0`.
-->
<#function getPageCount listId containerId="container">
    <#return containerId?eval[listId].paginator.pageCount
</#function>

<#--doc
    Gets the number of the current page.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The number of the page currently shown.
-->
<#function getPageNumber listId containerId="container"> 
    <#return containerId?eval[listId].paginator.pageNumber>
</#function>

<#--doc
    Gets the name of the page URL parameter.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The name of the page URL parameter.
-->
<#function getPageParam listId containerId="container">
    <#return containerId?eval[listId].paginator.pageParam>
</#function>

<#--doc
    Gets the maximum number of items on a page.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The maxium number of items on a page.
-->
<#function getPageSize listId containerId="container">
    <#return containerId?eval[listId].paginator.pageSize>
</#function>

<#--doc
    Gets the link to the previous page.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The link to the previous page.
-->
<#function getPrevPageLink listId containerId="container">
    <#return containerId?eval[listId].paginator.prevPageLink>
</#function>

<#--doc
    Gets the link to the next page.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The link to the next page.
-->
<#function getNextPageLink listId containerId="container">
    <#return containerId?eval[listId].paginator.nextPageLink>
</#function>

<#--doc
    Gets the link to the first page.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The link to the first page.
-->
<#function getFirstPageLink listId containerId="container">
    <#return containerId?eval[listId].paginator.firstPageLink>
</#function>

<#--doc
    Gets the link to the last page.

    @param listId The ID of the object list to use.
    @param containerId The ID of the container of the list

    @return The link to the last page.
-->
<#function getLastPageLink listId containerId="container">
    <#return containerId?eval[listId].paginator.lastPageLink>
</#function>

<#--doc
    Gets the title of an item from an object list.

    @param item The model of the list entry for the item.
    @param containerId The ID of the container of the list

    @depcrecated Use the title property of the item directly

    @return The title of the item.
-->
<#function getItemTitle item>
    <#return item.title>
</#function>

<#--doc
    Gets the link to the detail view of an item in a list.

    @param item The model of the list entry for the item.

    @return The link to the detail view of the item.
-->
<#function getItemLink item>
    <#return "./" + item.name>
</#function>

<#--doc
    Checks if the model of the list entry of an item contains a value for the
    `lead` property.

    @param item The model of the list entry for the item.

    @return `true` if the model provides a value of the `lead` property, 
    `false` if not.
-->
<#function hasItemLead item>
    <#return item.lead??>
</#function>

<#--doc
    Gets the value of the `lead` property of an item.

    @param item The model of the list entry for the item.

    @return The value of the `lead` property.
-->
<#function getItemLead item>
    <#return item.lead>
</#function>

<#--doc
    A generic function for checking if the model of a list entry for an item
    has a specific property.

    @param item The model of the list entry for the item.

    @param property The name of the property to check for.

    @return `true` If the provided model provides an value for the property, 
    `false` if not.
-->
<#function hasItemProperty item property>
    <#return false>
</#function>

<#--doc
    A generic function for retrieving the value of an property from the model
    for a list entry for an item.

    @param item The model of the list entry for the item.

    @param property The name of the property to retrieve.

    @return The value of the property.
-->
<#function getItemProperty item property>
    <#return "">
</#function>

<#--doc
    Checks if the provided item has an associated image. 

    @param item The model of the list entry for the item.

    @return `true` if the item has an associated image, `false` if not.
-->
<#function hasImage item>
    <#return false>
</#function>

<#--doc
    Get the ID of an associated image.

    @param item The model of the list entry for the item.

    @return The ID of the associated image.
-->
<#function getImageId item>
    <#return "">
</#function>

<#--doc
    Generates the URL for an associated image.

    @param item The model of the list entry for the item.

    @return The URL of the associated image. 
-->
<#function getImageUrl item>
    <#return "">
</#function>

<#--doc
    Gets the caption for the associated image.

    @param item The model of the list entry for the item.

    @return The caption for the associated image.
-->
<#function getImageCaption item>
    <#return "">
</#function>

<#--doc
    Retrieves the filters for an object list.

    @param listId The ID of the list to use.

    @return The filters for the list. An empty sequence is returned if no list 
    with the provided `listId`  could be found or if the list does not have any filters.
-->
<#function getFilters listId containerId="container">
    <#return []>
</#function>

<#--doc
    Get the label of a filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The label of the filter.
-->
<#function getFilterLabel filter>
    <#return "">
</#function>

<#--doc
    Get the type of a filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The type of the filter.
-->
<#function getFilterType filter>
    <#return "">
</#function>

<#--doc
    Get the options of a select filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The options of the select filter.
-->
<#function getSelectFilterOptions filter>
    <#return []>
</#function>

<#--doc
    Get the currently selected option of a select filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The currently selected option.
-->
<#function getSelectFilterSelection filter>
    <#return "">
</#function>

<#--doc
    The label for an option of a select filter.

    @param filter model of an option as returned by `getSelectFilterOptions`.

    @return The label of the option.
-->
<#function getSelectFilterOptionLabel option>
    <#return "">
</#function>

<#--doc
    Gets the search string of an category filter.

    @param filter model of a filter as returned by `getFilters`.

    @return The search string.
-->
<#function getCategoryFilterSearchString filter>
    <#return "">
</#function>

<#--doc
    Gets the separator for a category filter.

    @param filter The model of a filter as returned by `getFilters`.

    @return The separator for separating the categories in the search string.
-->
<#function getCategoryFilterSeparator filter>
        <#return "">
</#function>

<#--doc
    Determines if a category allows the selection of multiple categories.

    @param filter The model of a filter as returned by `getFilters`.

    @return `true` if the filter allows multiple selections, `false` otherwise.
-->
<#function getCategoryFilterMultiple filter>
        <#return false>
</#function>

<#--doc
    Gets the selectable categories for a category filter.

    @param filter The model of a filter as returned by `getFilters`.

    @return The selectable categories.
-->
<#function getCategoryFilterCategories filter>
        <#return []>
</#function>

<#--doc
    Gets the groups of a category filter.

    @param filter The model of a filter as returned by `getFilters`.

    @return The groups of a category filter.
-->
<#function getCategoryFilterCategoryGroups filter>    
        <#return []>
</#function>

<#--doc
    Gets the label of a category group.

    @param group The model of a category group as returned by 
    `getCategoryFilterCategoryGroups`.

    @return The label for the group.
-->
<#function getCategoryGroupLabel group>
    <#return "">
</#function>

<#--doc
    Gets the categories in a group.

    @param group The model of a category group as returned by 
    `getCategoryFilterCategoryGroups`.

    @return The categories in the group.
-->
<#function getCategoryFilterCategoryGroupsCategories groups>
   
    <#return "">
   
</#function>

<#--doc
    Gets the ID of a category of a category filter.

    @param category The model of a category as returned by 
    `getCategoryFilterCategoryGroupsCategories`.

    @return The ID of the category.
-->
<#function getCategoryFilterCategoryId category>
    <#return "">
</#function>

<#--doc
    Gets the label of a category of a category filter.

    @param category The model of a category as returned by 
    `getCategoryFilterCategoryGroupsCategories`.

    @return The label of the category.
-->
<#function getCategoryFilterCategoryLabel category>
    <#return "">
</#function>


