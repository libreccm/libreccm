<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008, 2009 Sören Bernstein
  
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

<!-- DE
  Hier werden die  verarbeitet 
-->

<!-- EN
  Processing 
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet 
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
    xmlns:cms="http://www.arsdigita.com/cms/1.0"
    xmlns:foundry="http://foundry.libreccm.org"
    xmlns:nav="http://ccm.redhat.com/navigation" 
    xmlns:mandalay="http://mandalay.quasiweb.de"
    exclude-result-prefixes="xsl bebop cms nav mandalay" 
    version="1.0">
  
    <xsl:template match="cms:notesDisplay">
        <xsl:apply-templates select="object"/>
    </xsl:template>
  
    <xsl:template match="object">
        <div class="cmsNotesDisplay">
            <div class="cmsNotesAction">
                <xsl:variable name="oid" select="@oid"/>
                <xsl:choose>
                    <!-- DE Kein Hoch-Link beim ersten Eintrag -->
                    <!-- EN No up link for the first entry -->
                    <xsl:when test="rank = '0'">
                        <xsl:apply-templates select="../cms:notesAction[@oid=$oid and @action!='up']"/>
                    </xsl:when>
                    <!-- DE Kein Runter-Link beim letzten Eintrag -->
                    <!-- EN No down link for the last entry -->
                    <xsl:when test="position() = count(../object)">
                        <xsl:apply-templates select="../cms:notesAction[@oid=$oid and @action!='down']"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:apply-templates select="../cms:notesAction[@oid=$oid]"/>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
            <div class="cmsNotesContent">
                <xsl:value-of select="content" disable-output-escaping="yes"/>
            </div>
            <div class="endFloat"/>
        </div>
    </xsl:template>
  
    <xsl:template match="cms:notesAction">
        <span>
            <xsl:choose>
                <xsl:when test="@action">
                    <xsl:attribute name="class">
                        <xsl:value-of select="concat('action ',@action)"/>
                    </xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">
                        <xsl:value-of select="'action'"/>
                    </xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>

            <a href="{foundry:parse-link(./@href)}">
                <xsl:value-of select="@action"/>
            </a>
        </span>
    </xsl:template>
    
</xsl:stylesheet>
