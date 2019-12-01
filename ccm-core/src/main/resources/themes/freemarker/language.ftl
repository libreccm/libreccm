<#--filedoc
    Language related utility functions
-->

<#--doc
    Retrieves the supported languages.

    @deprecated: Use getSupportedLanguages 

    @return A sequence of the available languages (as ISO language codes)
#-->
<#function getSupportedLanguages>
    <#return siteInfo.supportedLanguages>
</#function>

<#--doc
    Retrieves the supported languages.

    @deprecated: Use getSupportedLanguages and the functions provided by the
    module providing content.

    @return A sequence of the available languages (as ISO language codes)
#-->
<#function getAvailableLanguages>
    <#return getSupportedLanguages()>
</#function>