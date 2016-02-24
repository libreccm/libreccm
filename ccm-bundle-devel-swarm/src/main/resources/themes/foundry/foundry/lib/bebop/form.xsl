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

<!-- This file was copied from  Mandalay  and edited to fit into Foundry -->

<!-- EN
  Processing bebop forms
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation" 
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">

    <!-- DE Formulare -->
    <!-- EN Forms -->
    <xsl:template match="bebop:form">
        <xsl:if test="@message">
            <div class="form-message">
                <xsl:value-of select="@message"/>
            </div>
        </xsl:if>
        <form>
            <xsl:if test="not(./@method)">
                <xsl:attribute name="method">post</xsl:attribute>
            </xsl:if>
            <xsl:attribute name="here" select="'foo'"/>
            <xsl:call-template name="foundry:process-datatree-attributes"/>
            <xsl:apply-templates/>
        </form>
    </xsl:template>

    <!-- DE Formularfehler -->
    <!-- EN Form errors -->
    <xsl:template match="bebop:formErrors">
        <span class="bebop-form-errors">
            <xsl:value-of disable-output-escaping="yes" select="@message"/>
        </span>
        <br />
    </xsl:template>
  
    <!-- DE Verschiebe-Knöpfe -->
    <!-- EN Move-Buttons -->
    <xsl:template match="bebop:cell[@prevURL != '' or @nextURL != '']">
        <xsl:apply-templates select="."/>
        <xsl:call-template name="foundry:move-buttons"/>
    </xsl:template>

</xsl:stylesheet>
