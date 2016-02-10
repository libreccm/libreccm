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
  Processing bebop panels
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">

    <!-- DE Layout Panel: Ein 2- 3-spaltiges Panel -->
    <!-- EN Layout panel: A 2- 3-column panel -->
    <xsl:template match="bebop:layoutPanel">
        <div class="bebop-layout-panel">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
  
    <!-- DE Linke Spalte -->
    <!-- EN Left column -->
    <xsl:template match="bebop:left">
        <div class="bebop-left">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
  
    <!-- DE Haupt-Spalte bzw. mittlere Spalte -->
    <!-- EN Main or middle column --> 
    <xsl:template match="bebop:body">
        <div class="bebop-body">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
  
    <!-- DE Segmented Panel: Ein Panel aus Segmenten mit Überschrift und Inhaltsbereich -->
    <!-- EN Segmented Panel: A panel of segments with heading and content part -->
    <xsl:template match="bebop:segmentedPanel">
        <div class="bebop-segmented-panel">
            <!-- DE Verarbeite vorhandene Parameter -->
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:apply-templates/>
        </div>
    </xsl:template>
  
    <!-- DE Ein Segment für das Segmented Panel. Bebop:section macht das gleiche, nur 
    ohne das umschließende Segmented Panel. -->
    <!-- EN A segment for segmented panel. Bebop:section is doing the same, only without
    the surrounding segmented panel. -->
    <xsl:template match="bebop:segment | bebop:section">
        <div class="bebop-segment">
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:apply-templates select="bebop:segmentHeader | bebop:heading" mode="segment"/>
            <xsl:apply-templates select="bebop:segmentBody | bebop:body" mode="segment"/>
        </div>
    </xsl:template>
  
    <!-- DE Überschrift für ein Segment -->
    <!-- EN Heading for a segment -->
    <xsl:template match="bebop:segmentHeader | bebop:heading" mode="segment">
        <h3 class="bebop-segment-header">
            <xsl:apply-templates/>
        </h3>
    </xsl:template>
  
    <!-- DE Inhaltsbereich für ein Segment -->
    <!-- EN Content for a segment -->
    <xsl:template match="bebop:segmentBody | bebop:body" mode="segment">
        <div class="bebop-segment-body">
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <!-- DE Split Panel: Ein Panel mit Überschrift und 2 Spalten -->
    <!-- EN Split Panel: A penel with a heading and 2 columns -->
    <xsl:template match="bebop:splitPanel">
        <div width="{@width}" border="{@border}" cellpadding="{@cellpadding}" cellspacing="{@cellspacing}">
            <div class="bebop-split-panel-header">
                <xsl:apply-templates select="bebop:cell[position()=1]"/>
            </div>
            <div class="bebop-split-panel-left" width="{@divider_left}">
                <xsl:apply-templates select="bebop:cell[position()=2]"/>
            </div>
            <div class="bebop-split-panel-right" width="{@divider_right}">
                <xsl:apply-templates select="bebop:cell[position()>2]"/>
            </div>
        </div>  
    </xsl:template>
  
    <!-- DE List Panel: Eine Panel mit einer Liste -->
    <!-- EN List Panel: A panel with a list-->
    <xsl:template match="bebop:listPanel">
        <ul>
            <xsl:apply-templates select="bebop:cell" mode="list"/>
        </ul>
    </xsl:template>
  
    <!-- DE List Panel: Eine Panel mit einer geordneten Liste -->
    <!-- EN List Panel: A panel with an ordered list-->
    <xsl:template match="bebop:listPanel[@ordered='true']">
        <ol>
            <xsl:apply-templates select="bebop:cell" mode="list"/>
        </ol>
    </xsl:template>
  
    <!-- DE Grid Panel: Ein Panel, daß an einem Gitter ausrichtet (funktioniert nicht) -->
    <!-- EN Grid Panel -->
    <xsl:template match="bebop:gridPanel">
        <div class="bebop-grid-panel">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
  
    <!-- DE PanelRow erzeugt eine neue Zeile im (Grid) Panel -->
    <!-- EN PanelRow creates a new row in a (grid) panel -->
    <xsl:template match="bebop:panelRow">
        <div class="bebop-panel-row">
            <xsl:apply-templates/>
        </div>
    </xsl:template>
  
    <!-- DE Column Panel: Ein Panel, daß alles in Spalten anzeigt -->
    <!-- EN Column Panel: A panel that will create columns -->
    <xsl:template match="bebop:columnPanel">
        <div class="bebop-column-panel">
            <xsl:apply-templates />
        </div>  
    </xsl:template>
  
    <!-- DE Box Panel -->
    <!--  <xsl:template match="bebop:boxPanel">
      <div>
      <xsl:apply-templates/>
      </div>
      </xsl:template>
    -->  

    <!-- DE Ab hier kommen die original templates -->
    <!-- EN Some original templates which aren't rewritten yet-->
  
    <!-- Box Panel -->
    <!-- horizontal -->
    <xsl:template match="bebop:boxPanel[@axis='1']">
        <table>
            <xsl:if test="string-length(@width)>0">
                <xsl:attribute name="width">
                    <xsl:value-of select="@width"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(@border)>0">
                <xsl:attribute name="border">
                    <xsl:value-of select="@border"/>
                </xsl:attribute>
            </xsl:if>
            <tr>
                <xsl:for-each select="bebop:cell">
                    <td>
                        <xsl:for-each select="*/@class|*/@style">
                            <xsl:attribute name="{name()}">
                                <xsl:value-of select="." />
                            </xsl:attribute>
                        </xsl:for-each>
                        <xsl:apply-templates/>
                    </td>
                </xsl:for-each>
            </tr>
        </table>  
    </xsl:template>

    <!-- vertikal -->
    <xsl:template match="bebop:boxPanel[@axis='2']">
        <table>
            <xsl:if test="string-length(@width)>0">
                <xsl:attribute name="width">
                    <xsl:value-of select="@width"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length(@border)>0">
                <xsl:attribute name="border">
                    <xsl:value-of select="@border"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:for-each select="bebop:cell">
                <tr>
                    <td>
                        <xsl:for-each select="*/@class|*/@style">
                            <xsl:attribute name="{name()}">
                                <xsl:value-of select="." />
                            </xsl:attribute>
                        </xsl:for-each>
                        <xsl:apply-templates/>
                    </td>
                </tr>
            </xsl:for-each>
        </table>  
    </xsl:template>

</xsl:stylesheet>
