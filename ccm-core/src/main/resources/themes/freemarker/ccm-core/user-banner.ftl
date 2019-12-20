<#--filedoc
    The user banner component provides several informations about the current
    user.
-->

<#--doc
    Retreives the value the `greeting` property from the user banner.

    Not supported.

    @depcreated Without replacement.

    @return Empty string.
-->
<#function getGreeting>
    <#return "">
</#function>

<#--doc
    Determines if the current user is logged in.

    @depcrecated Use `isAuthenticated`

    @return `true` if the current user is logged in, `false` if not.
-->
<#function isLoggedIn>
    <#return isAuthenticated()>
</#function>

<#--doc
    Determines if the current user is *not* logged in.

    @depcrecated Use `isNotAuthenticated`

    @return `true` if the current user is *not* logged in, `false` if the user
    is logged in.
-->
<#function isNotLoggedIn>
    <#return isNotAuthenticated()>
</#function>

<#--doc
    Determines if the current user is authenticated.

    @return `true` if the current user is authenticated, `false` if not.
-->
<#function isAuthenticated>
    <#return currentUser.authenticated>
</#function>

<#--doc
    Determines if the current user is *not* authenticated.

    @return `true` if the current user is *not* authenticated, `false` if the user
    is authenticated.
-->
<#function isNotAuthenticated>
    <#return !currentUser.authenticated>
</#function>

<#--doc
    Retrieves the URL for changing the password. Only available if the current
    user is logged in.

    @return The URL for changing the password.
-->
<#function getChangePasswordUrl>
    <#return currentUser.changePasswordUrl>
</#function>

<#--doc
    Retrieves the link to the login form. Only available if the current user
    is not logged in.

    @depcrecated Use getLoginUrl() instead

    @return The link to the login form.
-->
<#function getLoginLink>
    <#return getLoginUrl()>
</#function>

<#--doc
    Retrieves the URL of the login form. Only available if the current user
    is not logged in.

    @return The link to the login form.
-->
<#function getLoginUrl>
    <#return currentUser.loginUrl>
</#function>

<#--doc
    Retrieves the link for logging out. Only available if the current user
    is logged in.

    @depcreated Use getLogoutUrl instead

    @return The link for logging out.
-->
<#function getLogoutLink>
    <#return getLogoutUrl()>
</#function>

<#--doc
    Retrieves the URL for logging out. Only available if the current user
    is logged in.

    @return The link for logging out.
-->
<#function getLogoutUrl>
    <#return currentUser.logoutUrl>
</#function>


<#--doc
    Retrieves the screen name (user name) of the current user. Only available
    if the current user is logged in.

    @return The screen name of the current user.
-->
<#function getScreenName>
    <#return currentUser.username>
</#function>

<#--doc
    The given name of the current user. Only available if the current user is 
    logged in.

    @return The given name of the current user.
-->
<#function getUserGivenName>
    <#return currentUser.givenName>
</#function>

<#--doc
    The family name of the current user. Only available if the current user is 
    logged in.

    @return The family name of the current user.
-->
<#function getUserFamilyName>
    <#return currentUser.familyName>
</#function>

