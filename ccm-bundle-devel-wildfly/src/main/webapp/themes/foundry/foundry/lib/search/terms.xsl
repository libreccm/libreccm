<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
    Copyright: 2015 Jens Pelzetter
  
    This file is part of the Foundry Theme Engine for LibreCCM. This file
    was taken from the Mandalay theme engine at has been modified to work 
    with Foundry.

    Foundry is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Foundry is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Foundry  If not, see <http://www.gnu.org/licenses/>. 
    
-->

<!-- 
  Processing search terms
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation" 
                xmlns:search="http://rhea.redhat.com/search/1.0"
                exclude-result-prefixes="xsl bebop cms foundry nav search" 
                version="2.0">
  
    <!-- Create a widget for the search terms -->
    <xsl:template match="search:terms">
        <xsl:param name="layoutTree" select="."/>

        <xsl:if test="foundry:boolean(foundry:get-setting('search', 'show-query-prefix', 'true'))">
            <span class="query">
                <xsl:value-of select="foundry:get-static-text('search', 'query')"/>
            </span>
        </xsl:if>
        <span class="terms">
            <input size="30" 
                   type="text" 
                   id="{@param}"
                   name="{@param}" 
                   value="{@value}" 
                   title="Enter one or more search terms"/>
                   <br/>
            <xsl:apply-templates select="../bebop:formWidget"/>
        </span>
    </xsl:template>
  
</xsl:stylesheet>
