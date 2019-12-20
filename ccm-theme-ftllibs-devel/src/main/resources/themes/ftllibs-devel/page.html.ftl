<#import "/ccm-core/user-banner.ftl" as UserBanner>

<!DOCTYPE html>
<html>
    <head>
        <title>FTL Libs Devel</title>
    </head>
    <body>
        <h1>FTL Libs Test</h1>
        <h2>user-banner.ftl</h2>
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
    </body>
</html>