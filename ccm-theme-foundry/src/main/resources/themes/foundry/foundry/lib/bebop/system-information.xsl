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

<!-- DE
    Hier werden die Bebop-Seiten verarbeitet 
-->

<!-- EN
    Processing bebop pages
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation" 
                exclude-result-prefixes="xsl bebop cms foundry nav" 
                version="2.0">

    <!-- DE Schreibe den PageState in versteckte Input-Felder -->
    <!-- EN Save the page state in hidden input fields -->
    <xsl:template match="bebop:systemInformation">
        <a>
            <xsl:attribute name="href" select="foundry:parse-link(./@apphomepage)"/>
            <xsl:value-of select="./@appname"/>
        </a>
        <xsl:text>&nbsp;</xsl:text>
        <span class="version">
            <xsl:value-of select="./@version"/>
        </span>
    </xsl:template>

</xsl:stylesheet>
