<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<!-- This file was copied Mandalay from and edited to fit into Foundry -->

<!-- EN
  Processing Paddings
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation" 
                exclude-result-prefixes="xsl bebop cms foundry nav" 
                version="2.0">
  
    <!-- DE Übernehme die Parameter der Padding-Tags (Aktivierbar per setting) -->
    <!-- EN Processing some padding tags (if aktivated by setting) -->
    <xsl:template match="bebop:PadFrame | bebop:pad | bebop:border">
    
        <!-- DE Hole alle benötigten Einstellungen-->
        <!-- EN Getting all needed setting-->
        <xsl:variable name="show-padding" 
                      select="'false'"/>
        <xsl:variable name="border-color" 
                      select="'ccc'"/>

        <xsl:choose>
            <xsl:when test="$show-padding = 'true'">
                <div style="concat('padding: ', 
                                   @cellpadding, 
                                   'px; border:', 
                                   @border, 'solid #', 
                                   $border-color, 
                                   '; margin: ', 
                                   @cellspacing, 
                                 ' px;')">
                    <xsl:apply-templates/>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
  
</xsl:stylesheet>
