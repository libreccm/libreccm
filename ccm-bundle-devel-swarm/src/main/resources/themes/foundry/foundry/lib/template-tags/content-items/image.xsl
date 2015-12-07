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
        <foundry:doc-file-title>
            Tags for displaying the properties of ccm-cms-types-image
        </foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The file provides tags for displaying the special properties
                of ccm-cms-types-image.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>artist</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-artist">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/artist">
                <xsl:value-of select="$contentitem-tree/artist"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'artist']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'artist']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>copyright</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-copyright">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/copyright">
                <xsl:value-of select="$contentitem-tree/copyright"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'copyright']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'copyright']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>description</code> property. 
                This is equivalent to the lead text of an article.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-description">
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
                Outputs the value of the <code>license</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-license">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/license">
                <xsl:value-of select="$contentitem-tree/license"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'license']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'license']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>material</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-material">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/material">
                <xsl:value-of select="$contentitem-tree/material"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'material']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'material']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>publishdate</code> property. 
                The date can be formatted using the date format tags.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-publishdate">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/publishDate">
                <xsl:message>
                    <xsl:value-of select="concat('year = ', ./publishDate/@year)"/>
                </xsl:message>
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$contentitem-tree/publishDate"/>
                    <xsl:with-param name="date-format" 
                                    select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'publishDate']">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$contentitem-tree/nav:attribute[@name = 'publishDate']"/>
                    <xsl:with-param name="date-format" 
                                    select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the height in pixel of the image.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-height">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/description">
                <xsl:value-of select="$contentitem-tree/image/height"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'image-height']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'image-height']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the long description (the main text) of the image.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-maintext">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes"
                      select="$contentitem-tree/textAsset/content"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>origin</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-origin">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/origin">
                <xsl:value-of select="$contentitem-tree/origin"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'origin']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'origin']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>origSize</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-originalsize">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/origsize">
                <xsl:value-of select="$contentitem-tree/origSize"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'origSize']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'origSize']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Puts the URL of the image (full size) into the environment for
                use by a <code>a</code> element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-url">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        <xsl:apply-templates>
            <xsl:with-param name="href"
                            tunnel="yes"
                            select="foundry:gen-src-url(concat('/cms-service/stream/image/?image_id=', $contentitem-tree/image/id))"/>
        </xsl:apply-templates>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Puts the necessary informations for showing the image on into 
                the environment.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//show-image">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="src" 
                            tunnel="yes"
                            select="concat('/cms-service/stream/image/?image_id=', $contentitem-tree/image/id)"/>
            <xsl:with-param name="img-width"
                            tunnel="yes"
                            select="$contentitem-tree/image/width"/>
            <xsl:with-param name="img-height"
                            tunnel="yes"
                            select="$contentitem-tree/image/height"/>
            <xsl:with-param name="alt"
                            tunnel="yes">
                <xsl:choose>
                    <xsl:when test="string-length($contentitem-tree/caption) &gt; 0">
                        <xsl:value-of select="$contentitem-tree/caption"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$contentitem-tree/image/name"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
            
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Puts the necessary informations for showing the thumbnail on into 
                the environment.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//show-thumbnail">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="src" 
                            tunnel="yes"
                            select="concat('/cms-service/stream/image/?image_id=', $contentitem-tree/thumbnail/id)"/>
            <xsl:with-param name="img-width"
                            tunnel="yes"
                            select="$contentitem-tree/thumbnail/width"/>
            <xsl:with-param name="img-height"
                            tunnel="yes"
                            select="$contentitem-tree/thumbnail/height"/>
            <xsl:with-param name="alt"
                            tunnel="yes">
                <xsl:choose>
                    <xsl:when test="string-length($contentitem-tree/caption) &gt; 0">
                        <xsl:value-of select="$contentitem-tree/caption"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$contentitem-tree/thumbnail/name"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param>
            
        </xsl:apply-templates>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>site</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-site">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/site">
                <xsl:value-of select="$contentitem-tree/site"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'site']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'site']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>source</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-source">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/description">
                <xsl:value-of select="$contentitem-tree/source"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'image-source']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'image-source']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of the <code>technique</code> property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-technique">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/technique">
                <xsl:value-of select="$contentitem-tree/technique"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'technique']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'technique']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the width in pixels of the image.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//image-width">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/description">
                <xsl:value-of select="$contentitem-tree/image/width"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'image-width']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'image-width']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>