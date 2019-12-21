<#--filedoc
    Functions for creating the table of contents of a multi part article.

    @depcrecated Use ccm-cms/multiparticle-item-toc.ftl
-->

<#import "/ccm-cms/multiparticle-item-toc.ftl" as Toc>

<#--doc
    Gets the sections of a multi part article.

    @param item The model of the multi part article to use.

    @return The sections of the multi part article.
-->
<#function getSections item>
    <#return Toc.getSections(item)>
</#function>

<#--doc
    Gets the title of a section.

    @param section The model of the section as returned by `getSections`.

    @return The title of the section.
-->
<#function getSectionTitle section>
    <#return Toc.getSectionTitle(section)>
</#function>

<#--doc
    Gets the link for the section.

    @param section The model of the section as returned by `getSections`.

    @return The link for the section.
-->
<#function getSectionLink section>
    <#return Toc.getSectionLink(section)>
</#function>

<#--doc
    Determines of the provided section is the active section.

    @param item The model of the multi part article to use.

    @param section The model of the section as returned by `getSections`.

    @return `true` if the provided section is the active section, `false` otherwise.
-->
<#function isActiveSection item section>
    <#return Toc.getActiveSection(item, section)>
</#function>