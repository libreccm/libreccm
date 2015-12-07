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
        <foundry:doc-file-title>Tags ccm-sci-types-project</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The tags is this file are used to display the special properties 
                of a SciProject item.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the begin date of the project. The date can be 
                formatted in the usual way.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciproject-begin">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/projectBegin">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/projectBegin"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$orgaunit-data/lifeSpan/begin">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/lifeSpan/begin"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$orgaunit-data/nav:attribute[@name = 'projectBegin']">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/nav:attribute[@name = 'projectBegin']"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the end date of the project. The date can be 
                formatted in the usual way.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciproject-end">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/projectEnd">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/projectEnd"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$orgaunit-data/lifeSpan/end">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/lifeSpan/end"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$orgaunit-data/nav:attribute[@name = 'projectEnd']">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$orgaunit-data/nav:attribute[@name = 'projectEnd']"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the description of the project.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciproject-desc">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/projectDesc">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data/projectDesc"/>
            </xsl:when>
            <xsl:when test="$orgaunit-data/description">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data/description"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the short description of the project.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciproject-shortdesc">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/projectShortDesc">
                <xsl:value-of select="$orgaunit-data/projectShortDesc"/>
            </xsl:when>
            <xsl:when test="$orgaunit-data/shortDesc">
                <xsl:value-of select="$orgaunit-data/shortDesc"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for the list of sponsors of the project.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="$orgaunit-data/sponsors">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                This tag encloses a entry in the list of sponsor. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors//sponsor">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$orgaunit-data/sponsors/sponsor">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="sponsor-name" 
                                tunnel="yes" 
                                select="."/>
                <xsl:with-param name="funding-code" 
                                tunnel="yes" 
                                select="./@fundingCode"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the name of a sponsor.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors//sponsor//sponsor-name">
        <xsl:param name="sponsor-name" tunnel="yes"/>
        
        <xsl:value-of select="$sponsor-name"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the funding code associated with a sponsor.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors//sponsor//funding-code">
        <xsl:param name="funding-code" tunnel="yes"/>
        
        <xsl:value-of select="$funding-code"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                The tags enclosed by this tag are only processed if there
                is a funding code.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sponsors//sponsor/if-funding-code">
        <xsl:param name="funding-code" tunnel="yes"/>
        
        <xsl:if test="string-length($funding-code) &gt; 0"/>
    </xsl:template>
        
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the funding text of the project.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//funding">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:value-of select="$orgaunit-data/funding"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the funding volume of the project.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//funding-volume">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:value-of select="$orgaunit-data/fundingVolume"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                The tags enclosed by this tag are only processed if there is
                a funding text.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//if-funding">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="string-length($orgaunit-data/funding) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                The tags enclosed by this tag are only processed if a 
                funding volume is provided.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//if-funding-volume">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="string-length($orgaunit-data/fundingVolume) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for the list of members of project.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:variable name="members-datatree" select="$orgaunit-data/members"/>
        
        <xsl:if test="count($members-datatree) &gt; 0">
            <xsl:apply-templates>
                <xsl:with-param name="members-datatree"
                                tunnel="yes"
                                select="$members-datatree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Encloses an entry in the member list. The show the member item 
                you either use the special tags defined here or you insert
                a list view of the member item by using the 
                <code>content-item</code> tag.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member">
        <xsl:param name="members-datatree" tunnel="yes"/>
        <xsl:param name="class-first" select="''"/>
        <xsl:param name="class-last" select="''"/>
        
        <xsl:variable name="member-layouttree" select="./*"/>
        
        <xsl:variable name="separator" select="./@separator"/>
        
        <xsl:for-each select="$members-datatree/member">
            <xsl:apply-templates select="$member-layouttree">
                <xsl:with-param name="contentitem-tree"
                                tunnel="yes"
                                select="current()"/>
                <xsl:with-param name="id"
                                select="concat(./masterVersion/id, 
                                               '_',
                                               ./@name)"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="foundry:generate-contentitem-link(./@oid)"/>
                <xsl:with-param name="class">
                    <xsl:choose>
                        <xsl:when test="position() = 1">
                            <xsl:value-of select="$class-first"/>
                        </xsl:when>
                        <xsl:when test="position() = last()">
                            <xsl:value-of select="$class-last"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
            <xsl:if test="position() != last()">
                <xsl:value-of select="$separator"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Output the surname of the current member.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member//surname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/surname) &gt; 0">
            <xsl:if test="./@before">
                <xsl:value-of select="./@before"/>
            </xsl:if>
            <xsl:value-of select="$contentitem-tree/surname"/>
            <xsl:if test="./@after">
                <xsl:value-of select="./@after"/>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Output the given name of the current member.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member//givenname">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/givenName) &gt; 0">
            <xsl:if test="./@before">
                <xsl:value-of select="./@before"/>
            </xsl:if>
            <xsl:value-of select="$contentitem-tree//givenName"/>
            <xsl:if test="./@after">
                <xsl:value-of select="./@after"/>
            </xsl:if>
            <xsl:if test="./@after">
                <xsl:value-of select="./@after"/>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Output the value of the titlepre property of the current member 
                item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member//titlePre">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/titlePre) &gt; 0">
            <xsl:if test="./@before">
                <xsl:value-of select="./@before"/>
            </xsl:if>
            <xsl:value-of select="$contentitem-tree//titlePre"/>
            <xsl:if test="./@after">
                <xsl:value-of select="./@after"/>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Output the value of the titlepost property of the current
                member item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//members//member//titlePost">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/titlePost) &gt; 0">
            <xsl:if test="./@before">
                <xsl:value-of select="./@before"/>
            </xsl:if>
            <xsl:value-of select="$contentitem-tree/titlePost"/>
            <xsl:if test="./@after">
                <xsl:value-of select="./@after"/>
            </xsl:if>
        </xsl:if>
    </xsl:template>
</xsl:stylesheet>