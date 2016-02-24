<?xml version="1.0" encoding="utf-8"?>
<!--
    Copyright 2015 Jens Pelzetter for the LibreCCM Foundation
    
    This file is part of the Foundry Theme Engine for LibreCCM
    
    Foundry is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Foundry is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foundry  If not, see <http://www.gnu.org/licenses/>.

-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">
    
    <foundry:doc-file>
        <foundry:doc-file-title>Common tags for content types derived from GenericOrganizationalUnit</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The tags in these file are used to create the HTML representation
                of types derived from GenericOrganzationalUnit, for example
                SciProject, SciDepartment or SciInstitute. 
            </p>
            <p>
                The informations about a organizational unit are provided
                in several sections (tabs).
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for outputting the available tabs of organizational
                unit. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//orgaunit-available-tabs">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="available-tabs"
                            tunnel="yes"
                            select="$contentitem-tree/orgaUnitTabs/availableTabs"/>
            <xsl:with-param name="orgaunit-type-name" 
                            tunnel="yes" 
                            select="$contentitem-tree/type/label"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                This tag encloses the HTML for a individual tab. It also 
                passes the URL for viewing the tab to the enclosed elements.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//orgaunit-available-tabs//available-tab">
        <xsl:param name="available-tabs" tunnel="yes"/>
        
        <xsl:variable name="selected-classes" select="./@selected-classes"/>
        <xsl:variable name="layout-tree" select="./*"/>
                
        <xsl:for-each select="$available-tabs/availableTab">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="label" 
                                tunnel="yes" 
                                select="./@label"/>
                <xsl:with-param name="selected" 
                                tunnel="yes" 
                                select="foundry:boolean(./@selected)"/>
                <xsl:with-param name="class" select="if(foundry:boolean(./@selected))
                                                     then $selected-classes
                                                     else ''"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="foundry:parse-link(concat('?selectedTab=', ./@label))"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the name of the avilable tab.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#tab-label">
                Alternative tag for labeling a tab.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="content-item-layout//orgaunit-available-tabs//available-tab//tab-name">
        <xsl:param name="label" tunnel="yes"/>
        
        <xsl:value-of select="$label"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                This tag is alternative for <code>tab-name</code>. It uses 
                the tab name and the type name of the orga unit to lookup
                the label in the localisable texts of the theme. More
                specifially it looks for a text in in 
                <code>texts/$orgaunit-type-name.xml</code> with the name of
                the tab as id.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//orgaunit-available-tabs//available-tab//tab-label">
        <xsl:param name="label" tunnel="yes"/>
        <xsl:param name="orgaunit-type-name" tunnel="yes"/>
        
        <xsl:value-of select="foundry:get-static-text(lower-case($orgaunit-type-name), $label)"/>
    </xsl:template>
    
    <foundry:doc type="template-tag" section="user">
        <foundry:doc-desc>
            <p>
                Enclosing tag for several other tags for displaying informations
                about a organisational unit. The tag passes several informations
                to the enclosed tags.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//orgaunit">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="orgaunit-data" 
                            tunnel="yes"
                            select="$contentitem-tree"/>
            <xsl:with-param name="orgaunit-type-name"
                            tunnel="yes"
                            select="$contentitem-tree/type/label"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Encloses the tags for displaying the informations from the
                current tag. The immediate sub elements of this tag can only 
                be <code>tab</code>. The <code>name</code> attribute of the 
                <code>tab</code> elements defines for which tab the layout
                enclosed by a <code>tab</code> element is used.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//orgaunit-current-tab">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="selected-tab" 
                      select="$contentitem-tree/orgaUnitTabs/availableTabs/*[@selected='true']/@label"/>
        
        <xsl:apply-templates select="./tab[@name=$selected-tab]/*">
            <xsl:with-param name="orgaunit-data" 
                            tunnel="yes"
                            select="$contentitem-tree/orgaUnitTabs/selectedTab/*"/>
            <xsl:with-param name="orgaunit-type-name"
                            tunnel="yes"
                            select="$contentitem-tree/type/label"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generic tag to output a property in a tab of a orga unit.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//orgaunit-current-tab//tab//show-tab-property">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="foundry:boolean(./@disable-output-escaping)">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data/*[name() = ./@name]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$orgaunit-data/*[name() = ./@name]"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the content of the current tab as text. If the
                <code>disable-output-escaping</code> attribute is set
                to <code>true</code> the content is shown as it is. Otherwise
                some characters are escaped.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="disable-output-escaping">
                <p>
                    Disable output escaping?
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="content-item-layout//orgaunit-current-tab//tab//show-tab-content">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="foundry:boolean(./@disable-output-escaping)">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$orgaunit-data"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>addendum</code> property
                of an orga unit.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//addendum">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:value-of select="$orgaunit-data/addendum"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for showing the list of members of an orga unit.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="members" 
                            tunnel="yes" 
                            select="$orgaunit-data/members"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Encloses the layout for an individual member entry.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//*[ends-with(name(), 'members')]//member">
        <xsl:param name="members" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$members/member">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="person" 
                                tunnel="yes" 
                                select="."/>
                <xsl:with-param name="member-role" 
                                tunnel="yes" 
                                select="./@role"/>
                <xsl:with-param name="member-status" 
                                tunnel="yes" 
                                select="./@status"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the status of the current member. The text itself
                is retrieved from <code>texts/$orgaunit-type-name</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//*[ends-with(name(), 'members')]//member//member-status">
        <xsl:param name="member-status" tunnel="yes"/>
        <xsl:param name="orgaunit-type-name" tunnel="yes"/>
        
        <xsl:if test="string-length($member-status) &gt; 0">
            <xsl:value-of select="foundry:get-static-text($orgaunit-type-name, lower-case($member-status))"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the role of the current member. The text itself
                is retrieved from <code>texts/$orgaunit-type-name</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//*[ends-with(name(), 'members')]//member//member-role">
        <xsl:param name="member-role" tunnel="yes"/>
        <xsl:param name="orgaunit-type-name" tunnel="yes"/>
        
        <xsl:if test="string-length($member-role) &gt; 0">
            <xsl:value-of select="foundry:get-static-text(lower-case($orgaunit-type-name), $member-role)"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Shows the contact entries associated with a member.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//*[ends-with(name(), 'members')]//member//member-contact-entries">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:variable name="contact-type"
                      select="if(./@contact-type)
                              then ./@contact-type
                              else 'commonContact'"/>
        
        <xsl:if test="$person/contacts/contact[@contactType = $contact-type]/contactentries">
            <xsl:apply-templates>
                <xsl:with-param name="contact-entries"
                                tunnel="yes"
                                select="$person/contacts/contact[@contactType = $contact-type]/contactentries"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//*[ends-with(name(), 'members')]//member//if-member-role-is">
        <xsl:param name="member-role" tunnel="yes"/>
        
        <xsl:if test="$member-role = ./@role">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for outputting the contact associated with an orga unit.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//orgaunit-contact">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="$orgaunit-data/contacts/contact[1]">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the person associated with orga unit contact.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//orgaunit-contact//orgaunit-contact-person">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="person" 
                            tunnel="yes"
                            select="$orgaunit-data/contacts/contact[1]/person"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the contact entries of the orga unit contact.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//orgaunit-contact//orgaunit-contact-entries">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="$orgaunit-data/contacts/contact[1]/contactentries">
            <xsl:apply-templates>
                <xsl:with-param name="contact-entries"
                                tunnel="yes"
                                select="$orgaunit-data/contacts/contact[1]/contactentries"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>