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

<!-- This file was copied from Mandalay and edited to fit into Foundry -->

<!-- EN
    Processing bebop lists
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">
  
    <!-- DE Eine verticale Liste als UL, wenn es keine item-path-Liste ist -->
    <!-- EN A vertical list as ul, if it is not an item-path type list -->
    <xsl:template match="bebop:list[@layout = 'vertical']">
        <xsl:choose>
            <xsl:when test="@type = 'item-path'">
                <xsl:apply-templates mode="item-path"/>
            </xsl:when>
            <xsl:when test="bebop:cell/bebop:link[@class='actionLink']">
                <div class="action-link">
                    <xsl:apply-templates mode="span"/>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <ul class="bebop-list">
                    <xsl:call-template name="foundry:process-datatree-attributes"/>
                    <xsl:apply-templates mode="list"/>
                </ul>
            </xsl:otherwise>
        </xsl:choose>
    
    </xsl:template>

    <!-- DE Eine horizontale Liste als spans -->
    <!-- EN a horizontal list with spans -->
    <xsl:template match="bebop:list[@layout = 'horizontal']">
        <div>
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:apply-templates mode="span"/>
        </div>
    </xsl:template>

    <!-- DE Hier werden lis hinzugefügt -->
    <!-- EN Inserting  li's -->
    <xsl:template match="bebop:cell" mode="list">
        <li>
            <xsl:apply-templates select="."/>
        </li>
    </xsl:template>
  
    <!-- DE Hier werden die spans hinzugefügt --> 
    <!-- EN Inserting span's -->
    <xsl:template match="bebop:cell" mode="span">
        <span>
            <xsl:apply-templates select="."/>
        </span>
    </xsl:template>
  
    <!-- DE Inhalte verarbeiten -->
    <!-- EN Processing content -->
    <xsl:template match="bebop:cell" mode="item-path">
        <xsl:apply-templates select="." mode="span"/>
        <xsl:if test="position() != last() and position() != 1">
            <xsl:text>/</xsl:text>
        </xsl:if>
    </xsl:template>
  
</xsl:stylesheet>
