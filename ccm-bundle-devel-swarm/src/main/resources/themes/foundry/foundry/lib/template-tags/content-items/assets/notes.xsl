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
    
    <xsl:template match="notes">
        <xsl:if test="$data-tree/cms:contentPanel/cms:item/ca_notes 
                      or $data-tree/nav:greetingItem/cms:item/ca_notes">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="notes//note">
        <xsl:variable name="notes-layout-tree" select="current()"/>
        
        <xsl:variable name="contentitem-tree">
            <xsl:choose>
                <xsl:when test="$data-tree/nav:greetingItem">
                    <xsl:copy-of select="$data-tree/nav:greetingItem/cms:item/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="$data-tree/cms:contentPanel/cms:item/*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
           
        <xsl:for-each select="$contentitem-tree/*[name() = 'ca_notes']">
            <xsl:sort select="rank"/>
            
            <xsl:apply-templates select="$notes-layout-tree/*">
                <xsl:with-param name="note-content" tunnel="yes" select="./content"/>
            </xsl:apply-templates>
        </xsl:for-each>
        
    </xsl:template>
    
    <xsl:template match="note//note-content">
        <xsl:param name="note-content" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes"
                      select="$note-content"/>
        
    </xsl:template>
    
</xsl:stylesheet>