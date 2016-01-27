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

<!-- This file was copied from Mandalay and edited to fit into Foundry  -->

<!-- EN
    Processing bebop action groups
    Images for the Actionlinks will be set through css so they won't corrupt the layout in lynx
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation" 
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">
    
    <!-- DE Erzeugt eine ActionGroup -->
    <!-- EN Create an action group -->
    <xsl:template match="bebop:actionGroup">
        <div class="action-group">
            <xsl:apply-templates/>
        </div> 
    </xsl:template>

    <!-- DE Titel der Gruppe (?)-->
    <!-- EN Heading (?)-->
    <xsl:template match="bebop:subject">
        <div class="action-group-subject">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
    
    <!-- DE Action link-->
    <!-- EN Action link-->
    <xsl:template match="bebop:action">
        <div class="action-group-action">
            <xsl:variable name="actionType">
                <xsl:choose>
                    <xsl:when test="@class">
                        <xsl:value-of select="concat('action ',@class)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="'action'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>

            <span class="{$actionType}">
                <xsl:apply-templates/>
            </span>
        </div>
    </xsl:template>
    
</xsl:stylesheet>
