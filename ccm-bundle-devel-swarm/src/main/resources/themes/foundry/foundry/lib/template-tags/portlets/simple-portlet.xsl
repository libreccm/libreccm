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
                xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
                exclude-result-prefixes="xsl portlet"
                version="2.0">
    
    <xsl:template match="portlet-layout//portlet-simple-content">
        <xsl:param name="portlet-data-tree" tunnel="yes"/>
        
        <xsl:apply-templates select="$portlet-data-tree/portlet:simple/*"/>
    </xsl:template>

</xsl:stylesheet>