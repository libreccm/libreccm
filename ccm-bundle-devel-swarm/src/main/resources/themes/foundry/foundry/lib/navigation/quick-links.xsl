<?xml version="1.0"  encoding="utf-8"?>
<!DOCTYPE stylesheet>
<!--
    Copyright 2015 Jens Pelzetter for the LibreCCM Foundation
    
    This file is part of the Foundry Theme Engine for LibreCCM
    
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
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">

    <xsl:template match="nav:quickLinkListing">
        <h4>
            <xsl:value-of select="foundry:get-static-text('navigation', 'quicklinks/listing/head')"/>
        </h4>
        <table class="quickLinkListing">
            <tr>
                <th>
                    <xsl:value-of select="foundry:get-static-text('navigation', 
                                                                  'quicklinks/listing/title')"/>
                </th>
                <th>
                    <xsl:value-of select="foundry:get-static-text('navigation', 
                                                                  'quicklinks/listing/url')"/>
                </th>
                <th>
                    <xsl:value-of select="foundry:get-static-text('navigation', 
                                                                  'quicklinks/listing/cascade')"/>
                </th>
                <th>
                    <xsl:value-of select="foundry:get-static-text('navigation', 
                                                                  'quicklinks/listing/description')"/>
                </th>
                <th colspan="2">
                    <xsl:value-of select="foundry:get-static-text('navigation', 
                                                                  'quicklinks/listing/actions')"/>
                </th>
            </tr>
            <xsl:for-each select="./nav:object">
                <tr class="{if (position() mod 2 = 0) then 'even' else 'odd'}">
                    <td>
                        <xsl:value-of select="./nav:title"/>
                    </td>
                    <td>
                        <xsl:value-of select="./nav:url"/>
                    </td>
                    <td>
                        <xsl:value-of select="if (./nav:cascade = 'true')
                                              then foundry:get-static-text('navigation', 
                                                                           'quicklinks/listing/cascade/true')
                                              else foundry:get-static-text('navigation', 
                                                                           'quicklinks/listing/cascade/false')" />
                    </td>
                    <td>
                        <xsl:value-of select="./nav:description"/>
                    </td>
                    <td>
                        <a href="{./nav:action[@name='edit']/@url}">
                            <xsl:value-of select="foundry:get-static-text('navigation',
                                                                          'quicklinks/listing/edit')"/>
                        </a>
                    </td>
                    <td>
                        <a href="{./nav:action[@name='delete']/@url}">
                            <xsl:value-of select="foundry:get-static-text('navigation',
                                                                          'quicklinks/listing/delete')"/>
                        </a>
                    </td>
                </tr>
            </xsl:for-each>
        </table>
    </xsl:template>
    
</xsl:stylesheet>