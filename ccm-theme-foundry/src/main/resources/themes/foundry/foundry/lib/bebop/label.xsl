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

<!-- Author: Sören Bernstein -->

<!-- This file was copied from  Mandalay and edited to fit into Foundry -->
<!-- EN
  Processing bebop label
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">
  
    <!-- DE Ein Text-Label mit Formatierung -->
    <!-- EN A formatted text label -->
    <xsl:template match="bebop:label[@color != '' or @weight != '' or @id != '' or @class != '']">
        <xsl:call-template name="foundry:bebop-label-style">
            <xsl:with-param name="text">
                <xsl:value-of select="."/>
            </xsl:with-param>
            <xsl:with-param name="escape">
                <xsl:value-of select="@escape"/>
            </xsl:with-param>
            <xsl:with-param name="color">
                <xsl:value-of select="@color"/>
            </xsl:with-param>
            <xsl:with-param name="weight">
                <xsl:value-of select="@weight"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>

    <!-- DE Ein Text-Label im Javascript-Mode und mit Formatierung -->
    <!-- EN A formatted, javascript-mode text label -->
    <xsl:template match="bebop:label[@color != '' or @weight != '' or @id != '']" 
                  mode="javascript-mode">
        <xsl:call-template name="foundry:bebop-label-style">
            <xsl:with-param name="text">
                <xsl:value-of select="."/>
            </xsl:with-param>
            <xsl:with-param name="escape">
                <xsl:value-of select="@escape"/>
            </xsl:with-param>
            <xsl:with-param name="color">
                <xsl:value-of select="@color"/>
            </xsl:with-param>
            <xsl:with-param name="weight">
                <xsl:value-of select="@weight"/>
            </xsl:with-param>
            <xsl:with-param name="mode">
                <xsl:text>javascript</xsl:text>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
  
    <!-- DE Ein Text-Label im Javascript-Mode -->
    <!-- EN A javascript-mode text label -->
    <xsl:template match="bebop:label" mode="javascript-mode">
        <xsl:call-template name="foundry:bebop-label-textjs">
            <xsl:with-param name="text">
                <xsl:value-of select="."/>
            </xsl:with-param>
            <xsl:with-param name="escape">
                <xsl:value-of select="@escape"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
  
    <!-- DE Ein vorformatiertes Text-Label -->
    <!-- EN A preformatted text label -->
    <xsl:template match="bebop:label[@class = 'preformatted']">
        <pre>
            <xsl:call-template name="foundry:bebop-label-text">
                <xsl:with-param name="text">
                    <xsl:value-of select="."/>
                </xsl:with-param>
                <xsl:with-param name="escape">
                    <xsl:value-of select="@escape"/>
                </xsl:with-param>
            </xsl:call-template>
        </pre>
    </xsl:template>
  
    <!-- DE Ein Text-Label als Überschrift -->
    <!-- EN A text label for the heading -->
    <xsl:template match="bebop:label[@class = 'heading']">
        <span class="heading">
            <xsl:call-template name="foundry:bebop-label-text">
                <xsl:with-param name="text">
                    <xsl:value-of select="."/>
                </xsl:with-param>
                <xsl:with-param name="escape">
                    <xsl:value-of select="@escape"/>
                </xsl:with-param>
            </xsl:call-template>
        </span>
    </xsl:template>
  
    <!-- DE Ein Text-Label -->
    <!-- EN A text label -->
    <xsl:template match="bebop:label">
        <xsl:call-template name="foundry:bebop-label-text">
            <xsl:with-param name="text">
                <xsl:value-of select="."/>
            </xsl:with-param>
            <xsl:with-param name="escape">
                <xsl:value-of select="@escape"/>
            </xsl:with-param>
        </xsl:call-template>
    </xsl:template>
 
    <!-- DE Ein graphisches Label-->
    <!-- EN A graphical label -->
    <xsl:template match="bebop:label" mode="image">
        <xsl:param name="alt"/>
        <xsl:param name="title"/>
        <xsl:param name="src"/>
        <img alt="{$alt}" title="{$title}" src="{$src}"/>
    </xsl:template>
 
    <!-- DE Setze die Formatierung für das Label als style Attribut für ein span -->
    <!-- EN Set label formatting as style attribute for a span-->
    <xsl:template name="foundry:bebop-label-style">
        <xsl:param name="text"/>
        <xsl:param name="escape">no</xsl:param>
        <xsl:param name="color"/>
        <xsl:param name="weight"/>
        <xsl:param name="mode"/>
    
        <span>
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:if test="$escape = 'yes'">
                <xsl:attribute name="class" select="'escaped wysiwyg'"/>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="$color != ''">
                    <xsl:attribute name="style">
                        <xsl:value-of select="concat('font-color:', $color, ';')"/>
                    </xsl:attribute>
                </xsl:when>
                <xsl:when test="$weight != ''">
                    <xsl:attribute name="style">
                        <xsl:choose>
                            <xsl:when test="$weight = 'b'">
                                <xsl:text>font-weight:bold;</xsl:text>
                            </xsl:when>
                            <xsl:when test="$weight = 'i'">
                                <xsl:text>font-style:italic;</xsl:text>
                            </xsl:when>
                            <xsl:when test="$weight = 'bi' or $weight = 'ib'">
                                <xsl:text>font-style:italic;font-weight:bold;</xsl:text>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="concat('font-weight:', $weight, ';')"/>
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:attribute>
                </xsl:when>
            </xsl:choose>
            <xsl:choose>
                <xsl:when test="$mode = 'javascript'">
                    <xsl:call-template name="foundry:bebop-label-textjs">
                        <xsl:with-param name="text">
                            <xsl:value-of select="$text"/>
                        </xsl:with-param>
                        <xsl:with-param name="escape">
                            <xsl:value-of select="$escape"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:call-template name="foundry:bebop-label-text">
                        <xsl:with-param name="text">
                            <xsl:value-of select="$text"/>
                        </xsl:with-param>
                        <xsl:with-param name="escape">
                            <xsl:value-of select="$escape"/>
                        </xsl:with-param>
                    </xsl:call-template>
                </xsl:otherwise>
            </xsl:choose>
        </span>
    </xsl:template>
  
    <!-- DE Erzuege den Text des Labels javascript konform -->
    <!-- EN Processing the label text in a javascript compatible way -->
    <xsl:template name="foundry:bebop-label-textjs">
        <xsl:param name="text"/>
        <xsl:param name="escape">no</xsl:param>
    
        <xsl:call-template name="foundry:bebop-label-text">
            <xsl:with-param name="text"
                            select="replace(current(), '&#x2019;', '&#x005C;&#x2019;')"/>
            <!--select="replace(current(), '&apos;', '\&apos;')"/>-->
            <xsl:with-param name="escape" select="$escape"/>
        </xsl:call-template>
    </xsl:template>

    <!-- DE Erzeuge den Text des Labels-->
    <!-- EN Processing the label text-->
    <xsl:template name="foundry:bebop-label-text">
        <xsl:param name="text"/>
        <xsl:param name="escape">no</xsl:param>
    
        <!-- DE Keine leeren Labels ausgeben. Das original Theme wandelt diese in &#160; um. 
        Das sollte nicht nötig sein -->
        <!-- EN Don't print empty Labels. Originally they were converted to &#160; 
        but that shouldn't be needed -->
        <xsl:if test="not(normalize-space($text)='')">
            <xsl:choose>
                <xsl:when test="$escape='yes'">
                    <xsl:value-of disable-output-escaping="yes" select="$text"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of disable-output-escaping="no" select="$text"/>
                </xsl:otherwise>
            </xsl:choose> 
        </xsl:if>

        <!-- DE Hinweise anzeigen -->
        <!-- EN Display form hints -->
        <xsl:if test="../following-sibling::bebop:cell/*/@hint != ''">
            <span class="hint">
                <xsl:attribute name="content">
                    <xsl:value-of select="../following-sibling::bebop:cell/*/@hint"/>
                </xsl:attribute>
                <xsl:value-of select="'&#x24d8;'"/>
            </span>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>
