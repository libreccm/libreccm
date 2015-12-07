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

<!-- This file was copied from  Mandalay and edited to fit into Foundry -->

<!-- EN
    Processing bebop links
    There are to types of links. href_no_javascript is the links, that will not use
    javascript in the following. The onClick event handler is used to overwrite the
    standard link wih the javascript version, if javascript is running.
    Additionally the link parser of mandalay is called to check the formatting of 
    the link and processAttributes to insert all attributes to the html. 
-->

<!-- Autor: Sören Bernstein -->


<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation" 
                exclude-result-prefixes="xsl bebop cms foundry nav" 
                version="2.0">

    <!-- DE Hier werden die Links verarbeitet -->
    <!-- EN Processing links -->
    <xsl:template name="bebop:link" match="bebop:link">
        <xsl:param name="alt"/>
        <xsl:param name="title"/>
        <xsl:param name="src"/>
    
        <!-- DE  -->
        <!-- EN  -->
        <xsl:variable name="onclick">
            <xsl:choose>
                <xsl:when test="boolean(@onclick)=true() and not(starts-with(@onclick, 'return'))">
                    <xsl:value-of select="@onclick" disable-output-escaping="yes"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:text>this.href='</xsl:text>
                    <xsl:value-of select="@href" disable-output-escaping="yes"/>
                    <xsl:text>'; </xsl:text>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
    
        <!-- DE DoubleClickProtection für Links, wenn es keinen OnClick-Handler gibt -->
        <!-- EN DoubleClickProtection for links without an onclick handler -->
        <xsl:variable name="dcp">
            <xsl:if test="$dcp-on-links and boolean(@onclick)=false()">
                <xsl:text>doubleClickProtect(this); </xsl:text>
            </xsl:if>
        </xsl:variable>
    
        <!-- DE Wenn es ein Link mit Bestätigung ist -->
        <!-- EN A link with confirmation -->
        <xsl:variable name="confirm">
            <xsl:if test="boolean(@confirm)=true() or starts-with(@onclick, 'return')">
                <!-- We have to replace escaped quotes with the correct ones -->
                <xsl:value-of select="replace(./@onclick, '&#x005C;\&apos;, '')"/>
            </xsl:if>
        </xsl:variable>
    
        <a>
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:attribute name="href" select="foundry:parse-link(./@href_no_javascript)"/>
            <xsl:attribute name="onclick">
                <xsl:value-of select="$onclick"/>  
                <xsl:value-of select="$dcp"/>
                <xsl:value-of select="$confirm"/>  
            </xsl:attribute>
      
            <xsl:if test="$src">
                <img alt="{$alt}" title="{$title}" src="{$src}"/>
            </xsl:if>
            <xsl:apply-templates/>
            <xsl:if test="string-length(./@hint) &gt; 0">
                <span class="hint">
                    <xsl:attribute name="content">
                        <xsl:value-of select="@hint"/>
                    </xsl:attribute>
                    <xsl:value-of select="'&#x24d8;'"/>
                </span>
            </xsl:if>
        </a>
    </xsl:template>
    
</xsl:stylesheet>
