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

    <foundry:doc-file>
        <foundry:doc-file-title>Tags for ccm-cms-types-bookmark</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                Tags for outputting data from the 
                <code>ccm-cms-types-bookmark</code> content type.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the description of a bookmark.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//bookmark-description">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/description">
                <xsl:value-of select="$contentitem-tree/description"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'description']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'description']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Extracts the URL of the bookmark and passes the URL to the child
                tags. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//bookmark-link">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="href" tunnel="yes">
                <xsl:choose>
                    <xsl:when test="$contentitem-tree/url">
                        <xsl:value-of select="$contentitem-tree/url"/>
                    </xsl:when>
                    <xsl:when test="$contentitem-tree/nav:attribute[@name = 'url']">
                        <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'url']"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the URL of the bookmark as text.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//bookmark-url">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/url">
                <xsl:value-of select="$contentitem-tree/url"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'url']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'url']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>