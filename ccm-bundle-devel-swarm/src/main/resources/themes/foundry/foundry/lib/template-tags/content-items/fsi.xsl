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
        <foundry:doc-file-title>Tags for ccm-cms-types-filestorageitem</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The tags in these file can be used to output the special 
                properties of the file storage item.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the description of a file storage item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//fsi-description">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/description">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/description"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'description']">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$contentitem-tree/nav:attribute[@name = 'description']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the link to download the file associated with the
                file storage item. The tag has an optional attribute to decide
                if the link should force a download or not.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="mode">
                <p>
                    If set to <code>stream</code> the file will be opened 
                    in the approbriate program or browser plugin if available.
                    If not set or set to <code>download</code> the link
                    should cause a downlaod.
                </p>
                <p>
                    The real behaviour depends on the configuration of the 
                    browser used to to view the site.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="use-filename">
                <p>
                    If set to <code>true</code> the name of the file associated
                    with the file storage item is included into the link. 
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="/content-item-layout//fsi-link">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="mode" 
                      select="if (./@mode = 'stream')
                              then 'stream'
                              else 'download'"/>

        <xsl:apply-templates>
            <xsl:with-param name="href" tunnel="yes">
                <xsl:choose>
                    <xsl:when test="foundry:boolean(./@use-filename)">
                        <xsl:value-of select="concat($dispatcher-prefix, 
                                             '/cms-service/',
                                             $mode, 
                                             '/asset/',
                                             $contentitem-tree/file/name,
                                             '?asset_id=', 
                                             $contentitem-tree/file/id)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($dispatcher-prefix, 
                                             '/cms-service/',
                                             $mode,
                                             '/asset/?asset_id=', 
                                             $contentitem-tree/file/id)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:with-param> 
        </xsl:apply-templates>
    </xsl:template>

</xsl:stylesheet>