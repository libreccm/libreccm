<#--filedoc 
    Functions for processing the data provided by the pages application
-->

<#--doc
    Alias for getPathPath
-->
<#function getCategoryPath>
    <#return getPagePath()>
</#function>

<#--
    Gets the path of the current page.

    @return A list of names (URL stubs/slugs) of all categories in the
    path of the current page.
-->
<#function getPagePath>
    <#return pagePath>
</#function>

<#--doc
    Determines if the current page is the root page of a navigation.

    @return `true` if teh current page is a root page, `false` otherwise.
-->
<#function isRootPage>
    <#return getPagePath()?size <= 0>
</#function>

<#--doc
    Gets the currently selected category.

    @return The currently selected category.
-->
<#function getSelectedCategory>
    <#return getPathPath()?last>
</#function>

<#--doc
    Gets the ID of the currently selected category.

    @return The ID of the currently selected category.
-->
<#function getSelectedCategoryId>
    <#return getSelectedCategory().categoryId>
</#function>

<#--doc
    Get the title of the provided category.

    @param The model of a category as returned by several functions in this 
    library.

    @return The title of the category.
-->
<#function getCategoryTitle category>
    <#return category.title>
</#function>

<#--
    Get the ID get the provided category.

    @param The model of a category as returned by several functions in this 
    library.

    @return The ID of the provided category.
-->
<#function getCategoryId category>
    <#return category.categoryId>
</#function>

<#--doc
    Determines of the provided category is selected.

    @param The model of a category as returned by several functions in this 
    library.

    @return `true` if the category is selected, `false` if not.
-->
<#function isCategorySelected category>
    <#return category.isSelected>
</#function>

<#--doc
    Get the URL of the root category of the navigation with the provided id.

    @param navigationId The ID of the navigation system to use.

    @return The URL of the root category of the navigation system with the 
    provided ID.
-->
<#function getNavigationRootUrl navigationId="categoryMenu">
    <#return [navigationId].url>
</#function>

<#--doc
    Get title of the navigation with the provided id.

    @param navigationId The ID of the navigation system to use.

    @return The title of the navigation.
-->
<#function getNavigationTitle navigationId="categoryMenu">
    <#return [navigationId].categoryTitle>
</#function>

<#--doc
    Retrieves the first level of categories from the category hierachy with the 
    provided ID. If no id is provided 'categoryNav' is used.

    @param hierarchyId The ID of the category hierachy to use.

    @return The first level of categories in the hierarchy.
-->
<#function getCategoryHierarchy hierarchyId="categoryNav">
    <#return [hierarchyId].subCategories>
</#function>

<#--doc
    Gets the subcategories of the provided category.

    @param ofCategory The model of the category.

    @return The sub categories of the provided category.
-->
<#function getSubCategories ofCategory>
    <#return ofCategory.subcategories>
</#function>

<#--doc
    Gets the subcategories of the category with the provided id.

    @param categoryId The ID of the category to use.

    @return The sub categories of the category with the provided ID.
-->
<#function getSubCategoriesOfCategoryWithId categoryId>
    <#return model["/bebop:page/nav:categoryMenu//nav:category[@id=${categoryId}]/nav:category"]>
</#function>

<#--doc
    Gets the greeting/index item of the current navigation page. The returned
    model can be processed with usual functions for processing content items.

    @return The model of the index item.
-->
<#function getGreetingItem path="greetingItem">    
    <#return eval(path)>
</#function>