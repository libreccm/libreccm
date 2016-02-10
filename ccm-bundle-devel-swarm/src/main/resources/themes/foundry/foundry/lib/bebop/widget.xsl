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
  Processing bebop widgets. Because bebop is recreating the html attributes,
  most of the attributes can be simply copied by foundry:process-datatree-attributes.
  This is especially true for <input>.
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">
  
    <!-- DE Verabeitung von Checkbox- und Radiobutton-Groups. 
    Kann diese horizontal oder vertikal anzeigen -->
    <!-- EN Processing checkbox group and radiobutton group.
    Distinguish between horizontal and vertical display. --> 
    <xsl:template match="bebop:checkboxGroup | bebop:radioGroup">
        <xsl:choose>
            <xsl:when test="@class = 'vertical' or @axis = '2'">
                <div class="option-group">
                    <xsl:apply-templates mode="vertical"/>
                </div>
            </xsl:when>
            <xsl:otherwise>
                <span id="{./@id}" class="option-group">
                    <xsl:apply-templates/>
                </span>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
  
    <!-- DE Vertikale Anzeige von Radiobuttons -->
    <!-- EN Display radio buttons vertically -->
    <xsl:template match="bebop:radio" mode="vertical">
        <xsl:apply-templates select="."/>
        <br />
    </xsl:template>
  
    <!-- DE Erzeuge einen Radiobutton mit allen gefundenen Angaben -->
    <!-- EN Create a radio button with all given attributes -->
    <xsl:template match="bebop:radio">
        <input type="radio" id="{@name}:{@value}">
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:for-each select="../@readonly | ../@disabled | ../@title | ../@onclick">
                <xsl:attribute name="{name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
        </input>
    &nbsp;
        <label for="{@name}:{@value}">
            <xsl:apply-templates/>
        </label>
    </xsl:template>
  
    <!-- DE Vertikale Anzeige von Checkboxen -->
    <!-- EN Display checkboxes vertically -->
    <xsl:template match="bebop:checkbox" mode="vertical">
        <xsl:apply-templates select="."/>
        <br />
    </xsl:template>
  
    <!-- DE Erzeuge eine Checkbox mit allen gefundenen Angaben -->
    <!-- EN Create a checkbox with all given attributes -->
    <xsl:template match="bebop:checkbox">
        <input type="checkbox" id="{@name}:{@value}">
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:for-each select="../@readonly | ../@disabled | ../@title | ../@onclick">
                <xsl:attribute name="{name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:for-each>
        </input>
        <label for="{@name}:{@value}">
            <xsl:apply-templates/>
        </label>
    &nbsp;
    </xsl:template>
  
    <!-- DE Erzeuge eine Auswahlliste (Select), optional mit Mehrfachauswahl (MultiSelect).
    Wenn angefordert, erzeugt es für eine Auswahl der Länge 1 stattdessen ein Label. -->
    <!-- EN Create a select or multiselect widget. If wanted, creates a label instead for
    a selection length of 1. -->
    <xsl:template match="bebop:select | bebop:multiSelect">
        <xsl:call-template name="process-label">
            <xsl:with-param name="widget" select="."/>
        </xsl:call-template>
        <xsl:choose>
            <xsl:when test="@class = 'displayOneOptionAsLabel' and count(bebop:option) = 1">
                <xsl:choose>
                    <xsl:when test="bebop:option/@label">
                        <label>
                            <xsl:attribute name="for">
                                <xsl:value-of select="@name"/>
                            </xsl:attribute>
                            <xsl:value-of select="bebop:option/@label"/>
                        </label>
                    </xsl:when>
                    <xsl:otherwise>
                        <label>
                            <xsl:attribute name="for">
                                <xsl:value-of select="@name"/>
                            </xsl:attribute>
                            <xsl:apply-templates select="bebop:option/bebop:label"/>
                        </label>
                    </xsl:otherwise>
                </xsl:choose>
                <input type="hidden">
                    <xsl:attribute name="name">
                        <xsl:value-of select="@name"/>
                    </xsl:attribute>
                    <xsl:attribute name="value">
                        <xsl:value-of select="bebop:option/@value"/>
                    </xsl:attribute>
                </input>
            </xsl:when>
            <xsl:otherwise>
                <select id="@name">
                    <xsl:call-template name="foundry:process-datatree-attributes"/>
                    <xsl:apply-templates/>
                </select>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- DE Erzeugt einen Eintrag für eine Selectbox -->
    <!-- EN Create an entry for a select widget -->
    <xsl:template match="bebop:option">
        <option>
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:apply-templates/>
        </option>
    </xsl:template>
  
    <!-- DE Erzeugt alle Formular-Eingabefelder, die per <input> in HTML angelegt werden. -->
    <!-- EN Creates all form input field, which are created by a <input> in html. -->
    <xsl:template match="bebop:formWidget">
        <!--<xsl:if test="@label">
            <label for="{@name}">
                <xsl:value-of select="@label"/>
            </label>
        </xsl:if>-->
        <xsl:call-template name="process-label">
            <xsl:with-param name="widget" select="."/>
        </xsl:call-template>
        <input>
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <!-- DE Besondere Behandlung von Submit-Button Double-Click-Protection -->
            <!-- EN Special processing for submit button: doble click protection -->
            <xsl:if test="$dcp-on-buttons and @type='submit' and boolean(@onclick)=false()">
                <xsl:attribute name="onclick">
                    <xsl:text>doubleClickProtect(this);</xsl:text>
                </xsl:attribute>
            </xsl:if>
            <xsl:apply-templates/>
        </input>
    </xsl:template>
  
    <!-- DE Erzeugt ein Textfeld -->
    <!-- EN Create a textarea -->
    <xsl:template match="bebop:textarea">
        <xsl:call-template name="process-label">
            <xsl:with-param name="widget" select="."/>
        </xsl:call-template>
        <textarea>
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:value-of select="@value"/>
        </textarea>
    </xsl:template>
  
    <xsl:template match="bebop:date">
        <!--<span class="date">
            <xsl:apply-templates/>
        </span>-->
        <fieldset class="date">
            <legend>
                <xsl:value-of select="@label"/>
                <xsl:if test="string-length(./@hint) &gt; 0">
                    <span class="hint">
                        <xsl:attribute name="content">
                            <xsl:value-of select="@hint"/>
                        </xsl:attribute>
                        <xsl:value-of select="'&#x24d8;'"/>
                    </span>
                </xsl:if>
            </legend>
            <xsl:apply-templates/>
        </fieldset>
    </xsl:template>
  
    <xsl:template match="bebop:time">
        <!--<span class="time">
            <xsl:apply-templates/>
        </span>-->
        <fieldset>
            <legend>
                <xsl:value-of select="@label"/>
                <xsl:if test="string-length(./@hint) &gt; 0">
                    <span class="hint">
                        <xsl:attribute name="content">
                            <xsl:value-of select="@hint"/>
                        </xsl:attribute>                        
                        <xsl:value-of select="'&#x24d8;'"/>
                    </span>
                </xsl:if>
            </legend>
            <xsl:apply-templates/>
        </fieldset>
    </xsl:template>
  
    <xsl:template match="bebop:fieldset">
        <fieldset>
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <legend>
                <xsl:value-of select="@legend"/>
            </legend>
            <xsl:apply-templates/>
        </fieldset>
    </xsl:template>
  
    <xsl:template name="process-label">
        <xsl:param name="widget"/>
        
        <xsl:if test="$widget/@label">
            <label for="{$widget/@name}" class="widget-label">
                <xsl:value-of select="$widget/@label"/>
                <xsl:if test="string-length($widget/@hint) &gt; 0">
                    <span class="hint">
                        <xsl:attribute name="content">
                            <xsl:value-of select="@hint"/>
                        </xsl:attribute>
                        <xsl:value-of select="'&#x24d8;'"/>
                    </span>
                </xsl:if>
            </label>
        </xsl:if>
    </xsl:template>
  
</xsl:stylesheet>
