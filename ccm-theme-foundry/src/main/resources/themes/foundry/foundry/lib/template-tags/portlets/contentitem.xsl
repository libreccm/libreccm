<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet>
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
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
                exclude-result-prefixes="xsl cms portlet"
                version="2.0">
    
    <xsl:template match="portlet-layout//portlet-contentitem">
        <xsl:param name="portlet-data-tree" tunnel="yes"/>
        
        <xsl:call-template name="process-content-item-detail">
            <xsl:with-param name="contentitem-tree">
                <xsl:copy-of select="$portlet-data-tree/portlet:contentItem/cms:item/*"/>
            </xsl:with-param>
            <xsl:with-param name="mode" select="'portlet-item'"/>
        </xsl:call-template>
    </xsl:template>

</xsl:stylesheet>