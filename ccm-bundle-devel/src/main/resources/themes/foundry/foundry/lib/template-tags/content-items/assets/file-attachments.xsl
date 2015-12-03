<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>]>
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
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl bebop cms foundry nav ui"
                version="2.0">
    
    <xsl:template match="file-attachments">
        <xsl:if test="$data-tree/cms:contentPanel/cms:item/fileAttachments
                      or $data-tree/nav:greetingItem/cms:item/fileAttachments">
            <!--<pre>
                file-attachments
            </pre>-->
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="file-attachments//file-attachment">
        <xsl:variable name="caption-layout-tree" select="current()/caption-layout"/>
        <xsl:variable name="file-layout-tree" select="current()/file-layout"/>
        
        <!--<pre>
            file-attachment
        </pre>-->
        
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
        
        <xsl:for-each select="$contentitem-tree/fileAttachments">
            <xsl:sort select="fileOrder"/>
            
            <!--<pre>
                <xsl:value-of select="concat('current elem = ', name(current()))"/>
                <xsl:value-of select="concat('file-id      = ', ./id)"/>
                <xsl:value-of select="concat('file-name    = ', ./name)"/>
                <xsl:value-of select="concat('file-size    = ', ./file-size)"/>
            </pre>-->
            
            <xsl:choose>
                <xsl:when test="./mimeType/mimeType = 'text/plain' 
                                and ./mimeType/label = 'caption'">
                    <xsl:apply-templates select="$caption-layout-tree/*">
                        <xsl:with-param name="file-name" 
                                    tunnel="yes"
                                    select="./name"/>
                        <xsl:with-param name="description"
                                    tunnel="yes"
                                    select="./description"/>
                        <xsl:with-param name="class"
                                    tunnel="yes"
                                    select="'caption'"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$file-layout-tree/*">
                        <xsl:with-param name="file-id"
                                        tunnel="yes" 
                                        select="./id"/>
                        <xsl:with-param name="file-name" 
                                        tunnel="yes"
                                        select="./name"/>
                        <xsl:with-param name="mime-type"
                                        tunnel="yes"
                                        select="./mimeType/mimeType"/>
                        <xsl:with-param name="file-size"
                                        tunnel="yes"
                                        select="./length"/>
                        <xsl:with-param name="description"
                                        tunnel="yes"
                                        select="./description"/>
                        <xsl:with-param name="href"
                                        tunnel="yes"
                                        select="concat($context-prefix, 
                                               '/ccm/cms-service/stream/asset/', 
                                               ./name, '?asset_id=', ./id)"/>
                        <xsl:with-param name="class"
                                        tunnel="yes"
                                        select="concat('mime-type-', 
                                               replace(./mimeType/mimeType, '/', '-'))"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
        
    </xsl:template>
    
    <xsl:template match="file-attachment//file-name">
        <xsl:param name="file-name" tunnel="yes"/>
        
        <xsl:value-of select="$file-name"/>
    </xsl:template>
    
    <xsl:template match="file-attachment//file-size">
        <xsl:param name="file-size" as="xs:integer" tunnel="yes"/>
        
        <xsl:variable name="format" 
                      select="if(./@format)
                              then ./@format
                              else '0.00'"/>
        
        <xsl:choose>
            <xsl:when test="./@unit = 'MB'">
                <xsl:value-of select="concat(format-number($file-size div 1000000, 
                                                           $format), 
                                                           ' MB')"/>
            </xsl:when>
            <xsl:when test="./@unit = 'kB'">
                <xsl:value-of select="concat(format-number($file-size div 1000, 
                                                           $format), 
                                                           ' kB')"/>
            </xsl:when>
            <xsl:when test="./@unit = 'KiB'">
                <xsl:value-of select="concat(format-number($file-size div 1024, 
                                                           $format), 
                                                           ' KiB')"/>
            </xsl:when>
            <xsl:when test="./@unit = 'MiB'">
                <xsl:value-of select="concat(format-number($file-size div  1048576, 
                                                           $format), 
                                                           ' MiB')"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="concat ($file-size, ' bytes')"/>
            </xsl:otherwise>
        </xsl:choose>
        
        
    </xsl:template>
    
    <xsl:template match="file-attachment//file-description">
        <xsl:param name="description" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes" 
                      select="$description"/>
    </xsl:template>
    
    <xsl:template match="file-attachment//file-label">
        <xsl:param name="file-name" tunnel="yes"/>
        <xsl:param name="description" tunnel="yes"/>
        
        <xsl:value-of select="if (string-length($description) &gt; 0)
                              then $description
                              else $file-name"/>
        
    </xsl:template>
    
</xsl:stylesheet>