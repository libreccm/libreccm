<?xml version="1.0" encoding="utf-8"?>
<!--
    Copyright 2014 Jens Pelzetter for the LibreCCM Foundation
    
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
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element to create the list of related links assigned to an 
                content item
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="related-links">
        <xsl:param name="contentitem-tree" tunnel="yes">
            <xsl:choose>
                <xsl:when test="$data-tree/nav:greetingItem">
                    <xsl:copy-of select="$data-tree/nav:greetingItem/cms:item/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$data-tree/cms:contentPanel/cms:item/*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:param>
        
        <xsl:if test="$contentitem-tree/links">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                This tag wraps the HTML to display a single related link. The
                tag also adds the parameters required to create the link into 
                the environment, for example the URL (href) of the related link.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="related-links//related-link">
        <xsl:param name="contentitem-tree" tunnel="yes">
            <xsl:choose>
                <xsl:when test="$data-tree/nav:greetingItem">
                    <xsl:copy-of select="$data-tree/nav:greetingItem/cms:item/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$data-tree/cms:contentPanel/cms:item/*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:param>
        
        <xsl:variable name="links-layout-tree" select="current()"/>
        
        <xsl:for-each select="$contentitem-tree/links">
            <xsl:sort select="linkOrder"/>
         
            <xsl:choose>
                <xsl:when test="./targetType = 'externalLink' 
                                and ./targetURI = 'caption'">
                    <xsl:apply-templates select="$links-layout-tree/caption/*">
                        <xsl:with-param name="link-title" 
                                        tunnel="yes" 
                                        select="./linkTitle"/>
                        <xsl:with-param name="link-desc" 
                                        tunnel="yes" 
                                        select="./linkDescription"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="./targetType = 'internalLink'">
                    <xsl:variable name="params">
                        <xsl:choose>
                            <xsl:when test="starts-with(./targetURI, '&amp;?')">
                                <xsl:value-of select="concat('&amp;', 
                                                      substring(./targetURI, 3))"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="./targetURI"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:variable>
                    
                    <xsl:apply-templates select="$links-layout-tree/internal/*">
                        <xsl:with-param name="link-title" 
                                        tunnel="yes" 
                                        select="./linkTitle"/>
                        <xsl:with-param name="link-desc" 
                                        tunnel="yes" 
                                        select="./linkDescription"/>
                        <xsl:with-param name="href" 
                                        tunnel="yes" 
                                        select="concat($context-prefix,
                                                       '/redirect/?oid=', 
                                                       ./targetItem/@oid, 
                                                       $params)"/>
                        <xsl:with-param name="target-item-title" 
                                        tunnel="yes" 
                                        select="./targetItem/title"/>
                        <!--<xsl:with-param name="contentitem-tree" 
                                        tunnel="yes">
                            <xsl:copy-of select="./targetItem/*"/>
                        </xsl:with-param>-->
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$links-layout-tree/external/*">
                        <xsl:with-param name="link-title" tunnel="yes" select="./linkTitle"/>
                        <xsl:with-param name="link-desc" tunnel="yes" select="./linkDescription"/>
                        <xsl:with-param name="href" tunnel="yes" select="./targetURI"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the title of a related link.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="related-link//related-link-title">
        <xsl:param name="link-title" tunnel="yes"/>
        
        <xsl:value-of select="$link-title"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the description of a related link.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="related-link//related-link-desc">
        <xsl:param name="link-desc" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes" select="$link-desc"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the title of the target item of an internal related 
                link.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="related-link//internal//target-item-title">
        <xsl:param name="target-item-title" tunnel="yes"/>
        
        <xsl:value-of select="$target-item-title"/>
    </xsl:template>

</xsl:stylesheet>