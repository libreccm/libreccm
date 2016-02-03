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
        <foundry:doc-title>Tags for ccm-cms-types-externallink</foundry:doc-title>
        <foundry:doc-file-desc>
            <p>
                Tags for displaying the special properties of a ExternalLink
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Checks if the external link has a description and applies the 
                enclosed tags if there is one.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//if-extlink-description">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="string-length($contentitem-tree/pageDescription) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Output the description of an ExternalLink.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//show-extlink-description">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes" 
                      select="$contentitem-tree/pageDescription"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Checks if the <code>showComment</code> property of the 
                ExternalLink is <code>true</code> and if there is a comment 
                text. If both conditions are <code>true</code> the enclosed tags
                are applied.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//if-extlink-comment">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="foundry:boolean($contentitem-tree/showComment) 
                      and string-length($contentitem-tree/comment) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the comment text.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//show-extlink-comment">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes" 
                      select="$contentitem-tree/comment"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Checks if the <code>targetNewWindow</code> property of the 
                ExternalLink is set to <code>true</code> indicating that the 
                link should be opened in a new windows. This can be used to 
                integrate an JavaScript into the HTML for opening the new 
                window.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//if-extlink-new-window">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="foundry:boolean($contentitem-tree/targetNewWindow)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//if-extlink-not-new-window">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="not(foundry:boolean($contentitem-tree/targetNewWindow))">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Puts the URL of the ExternalLink into the environment.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//extlink-target-url">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="href" 
                            tunnel="yes" 
                            select="$contentitem-tree/url"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="/content-item-layout//extlink-data">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <div>
            <xsl:attribute name="id" select="'extlink-data'"/>
            <xsl:attribute name="data-new-window" 
                           select="$contentitem-tree/targetNewWindow"/>
            <xsl:attribute name="data-url" 
                           select="$contentitem-tree/url"/>
        </div>
    </xsl:template>
    
</xsl:stylesheet>