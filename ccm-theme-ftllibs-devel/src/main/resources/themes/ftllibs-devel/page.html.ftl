<#import "/ccm-core/user-banner.ftl" as UserBanner>
<#import "/language.ftl" as Language>
<#import "/utils.ftl" as Utils>
<#import "/ccm-cms/pages.ftl" as Pages>
<#import "/ccm-navigation/navigation.ftl" as Navigation>

<!DOCTYPE html>
<html>
    <head>
        <title>FTL Libs Devel</title>
    </head>
    <body>
        <h1>FTL Libs Test</h1>
        <h2>ccm-core/user-banner.ftl as UserBanner</h2>
        <dl>
            <div>
                <dt>UserBanner.getGreeting</dt>
                <dd>${UserBanner.getGreeting()}</dd>
            </div>
            <div>
                <dt>UserBanner.isLoggedIn</dt>
                <dd>${UserBanner.isLoggedIn()?c}</dd>
            </div>
            <div>
                <dt>UserBanner.isAuthenticated</dt>
                <dd>${UserBanner.isAuthenticated()?c}</dd>
            </div>
            <div>
                <dt>UserBanner.isNotLoggedIn</dt>
                <dd>${UserBanner.isNotLoggedIn()?c}</dd>
            </div>
            <div>
                <dt>UserBanner.isNotAuthenticated</dt>
                <dd>${UserBanner.isNotAuthenticated()?c}</dd>
            </div>
            <div>
                <dt>UserBanner.getChangePasswordUrl</dt>
                <dd>${UserBanner.getChangePasswordUrl()}</dd>
            </div>
            <div>
                <dt>UserBanner.getLoginLink</dt>
                <dd>${UserBanner.getLoginLink()}</dd>
            </div>
            <div>
                <dt>UserBanner.getLogoutLink</dt>
                <dd>${UserBanner.getLogoutLink()}</dd>
            </div>
            <div>
                <dt>UserBanner.getScreenName</dt>
                <dd>${UserBanner.getScreenName()}</dd>
            </div>
            <div>
                <dt>UserBanner.getUserGivenName</dt>
                <dd>${UserBanner.getUserGivenName()}</dd>
            </div>
            <div>
                <dt>UserBanner.getUserFamilyName</dt>
                <dd>${UserBanner.getUserFamilyName()}</dd>
            </div>
        </dl>
        <h2>language.ftl as Language</h2>
        <dl>
            <div>
                <dt>Language.getSupportedLanguages</dt>
                <dd>
                    <#list Language.getSupportedLanguages()>
                        <ul>
                            <#items as lang>
                                <li>${lang}</li>
                            </#items>
                        </ul>
                    <#else>
                        No supported languages
                    </#list>
                </dd>
            </div>
            <div>
                <dt>Language.getSupportedLanguages</dt>
                <dd>
                    <#list Language.getAvailableLanguages()>
                        <ul>
                            <#items as lang>
                                <li>${lang}</li>
                            </#items>
                        </ul>
                    <#else>
                        No languages available
                    </#list>
                </dd>
            </div>
        </dt>
        <h2>utils.ftl as Utils</h2>
        <dl>
            <div>
                <dt>Utils.getPageApplication()</dt>
                <dd>${Utils.getPageApplication()}</dd>
            </div>
            <div>
                <dt>Utils.getPageTitle()</dt>
                <dd>${Utils.getPageTitle()}</dd>
            </div>
            <div>
                <dt>Utils.getSiteHostName()</dt>
                <dd>${Utils.getSiteHostName()}</dd>
            </div>
            <div>
                <dt>Utils.getSiteHDomain()</dt>
                <dd>${Utils.getSiteDomain()}</dd>
            </div>
            <div>
                <dt>Utils.getSiteName()</dt>
                <dd>${Utils.getSiteName()!""}</dd>
            </div>
        </dl>
        <h2>ccm-cms/pages.ftl as Pages</h2>
        <dl>
            <div>
                <dt>Pages.getCategoryPath()</dt>
                <dd>
                    Path length: ${Pages.getCategoryPath()?size}
                    <#list Pages.getCategoryPath()>
                    <ul>
                        <#items as cat>
                        <li>
                            <dl>
                                <dt>Name</dt>
                                <dd>${cat.categoryName}</dd>
                                <dt>Title</dt>
                                <dd>${cat.categoryTitle!""}</dd>
                            </dl>
                        </li>
                        </#items>
                    </ul>
                    </#list>
                </dd>
            </div>
            <div>
                <dt>Pages.getPagePath()</dt>
                <dd>
                    Path length: ${Pages.getPagePath()?size}
                    <#list Pages.getPagePath()>
                    <ul>
                        <#items as cat>
                        <li>
                            <dl>
                                <dt>Name</dt>
                                <dd>${cat.categoryName}</dd>
                                <dt>Title</dt>
                                <dd>${cat.categoryTitle!""}</dd>
                            </dl>
                        </li>
                        </#items>
                    </ul>
                    </#list>
                </dd>
            </div>
            <div>
                <dt>Pages.isRootPage()</dt>
                <dd>${Pages.isRootPage()?c}</dd>
            </div>
            <div>
                <dt>Pages.getSelectedCategory()</dt>
                <dd>
                    <#if Pages.getSelectedCategory()??>
                        ${Pages.getSelectedCategory().categoryName}
                    <#else>
                        No category selected.
                    </#if>
                </dd>
            </div>
            <div>
                <dt>Pages.getSelectedCategoryId()</dt>
                <dd>
                    ${Pages.getSelectedCategoryId()}
                </dd>
            </div>
            <div>
                <dt>Pages.getNavigationRootUrl()</dt>
                </dd>${Pages.getNavigationRootUrl()}</dd>
            </div>
            <div>
                <dt>Pages.getCategoryHierarchy()</dt>
                <dd>
                    <#list Pages.getCategoryHierarchy()>
                        <ul>
                            <#items as cat>
                                <li>
                                    ${cat.categoryId}:${cat.categoryName}
                                </li>
                            </#items>
                        </ul>
                    <#else>
                        No categories in hierarchy.
                    </#list>
                </dd>
            </div>
            <div>
                <dt>Pages.getIndexItem()</dt>
                <dd>
                    <#assign indexItem = Pages.getIndexItem()>
                    <#if (indexItem?keys?size > 0)>
                        ${indexItem.uuid}:${indexItem.name}:${indexItem.title}
                    <#else>
                        No index item
                    </#if>
                </dd>
            </div>            

        </dl>
        <h2>ccm-navigation/navigation.ftl as Navigation</h2>
        <p>
            Note: Deprecated, replaced by ccm-cms/pages.ftl
        </p>
        <dl>
            <div>
                <dt>Navigation.getCategoryPath</dt>
                <dd>
                    Path length: ${Navigation.getCategoryPath()?size}
                    <#list Navigation.getCategoryPath()>
                        <ul>
                            <#items as cat>
                            <li>
                                <dl>
                                    <dt>Name</dt>
                                    <dd>${cat.categoryName}</dd>
                                    <dt>Title</dt>
                                    <dd>${cat.categoryTitle!""}</dd>
                                </dl>
                            </li>
                            </#items>
                        </ul>
                    </#list>
                </dd>
            </div>
        </dl>
    </body>
</html>