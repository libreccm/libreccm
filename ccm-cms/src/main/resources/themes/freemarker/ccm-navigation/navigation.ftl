<#--
    @depcrecated Use ccm-cms/pages.ftl 
    The functions in this libary call the functions with the same
    names from ccm-cms/pages.ftl. This library will removed in a future
    release.
-->

<#import "/ccm-cms/pages.ftl" as Pages>

<#--doc 
    Get all categories from the category path.

    @return All categories in the current category path.
-->
<#function getCategoryPath>
    <#return Pages.getCategoryPath()>
</#function>

<#--doc
    Determines if the current page is the root page of a navigation.

    @return `true` if teh current page is a root page, `false` otherwise.
-->
<#function isRootPage>
    <#return Pages.isRootPage()>
</#function>

<#--doc
    Gets the currently selected category.

    @return The currently selected category.
-->
<#function getSelectedCategory>
    <#return Pages.getSelectedCategory()>
</#function>

<#--doc
    Gets the ID of the currently selected category.

    @return The ID of the currently selected category.
-->
<#function getSelectedCategoryId>
    <#return Pages.getSelectedCategoryId()>
</#function>

<#--doc
    Get the title of the provided category.

    @param The model of a category as returned by several functions in this 
    library.

    @return The title of the category.
-->
<#function getCategoryTitle category>
    <#return Pages.getCategoryTitle(category)>
</#function>

<#--
    Get the URL of the provided category.

    @param The model of a category as returned by several functions in this 
    library.

    @return The URL of the category.
-->
<#function getCategoryUrl category>
    <#return Pages.getCategoryUrl(category)>
</#function>

<#--
    Get the ID get the provided category.

    @param The model of a category as returned by several functions in this 
    library.

    @return The ID of the provided category.
-->
<#function getCategoryId category>
    <#return Pages.getCategoryId(category)>
</#function>

<#--doc
    Determines of the provided category is selected.

    @param The model of a category as returned by several functions in this 
    library.

    @return `true` if the category is selected, `false` if not.
-->
<#function isCategorySelected category>
    <#return Pages.isCategorySelected(category)>
</#function>

<#--doc
    Get the URL of the root category of the navigation with the provided id.

    @param navigationId The ID of the navigation system to use.

    @return The URL of the root category of the navigation system with the 
    provided ID.
-->
<#function getNavigationRootUrl navigationId="categoryMenu">
    <#return Pages.getNavigationRootUrl(navigationId)>
</#function>

<#--doc
    Get title of the navigation with the provided id.

    @param navigationId The ID of the navigation system to use.

    @return The title of the navigation.
-->
<#function getNavigationTitle navigationId="categoryMenu">
    <#return Pages.getNavigationTitle(navigationId)>
</#function>

<#--doc
    Retrieves the first level of categories from the category menu with the provided ID. 
    If no id is provided "categoryMenu" is used.

    @param menuId The ID of the category menu to use.

    @return The first level of categories in the menu.
-->
<#function getCategoryMenu menuId="categoryMenu">
    <#return Pages.getCategoryMenu(menuId)>
</#function>

<#--doc
    Retrieves the first level of categories from the category hierachy with the 
    provided ID. If no id is provided 'categoryNav' is used.

    @param hierarchyId The ID of the category hierachy to use.

    @return The first level of categories in the hierarchy.
-->
<#function getCategoryHierarchy hierarchyId="categoryNav">
    <#return Pages.getCategoryHierarchy(hierarchyId)>
</#function>

<#--doc
    Gets the subcategories of the provided category.

    @param ofCategory The model of the category.

    @return The sub categories of the provided category.
-->
<#function getSubCategories ofCategory>
    <#return Pages.getSubCategories(ofCategory)>
</#function>

<#--doc
    Gets the subcategories of the category with the provided id.

    @param categoryId The ID of the category to use.

    @return The sub categories of the category with the provided ID.
-->
<#function getSubCategoriesOfCategoryWithId categoryId>
    <#return Pages.getSubCategoriesOfCategoryWithId(categoryId)>
</#function>

<#--doc
    Gets the greeting/index item of the current navigation page. The returned
    model can be processed with usual functions for processing content items.

    @return The model of the index item.
-->
<#function getGreetingItem>    
    <#return Pages.getGreetingItem()>
</#function>