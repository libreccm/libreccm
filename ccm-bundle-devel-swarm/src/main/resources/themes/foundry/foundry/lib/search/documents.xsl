<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '<xsl:text disable-output-escaping="yes">&amp;</xsl:text>nbsp;'>]>

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
    Processing search results
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation" 
                xmlns:search="http://rhea.redhat.com/search/1.0"
                exclude-result-prefixes="xsl bebop cms foundry nav" 
                version="2.0">
  
    <!-- Search result for webpages are translated to a ul -->
    <xsl:template match="search:documents">
        <xsl:param name="layout-tree" select="."/>
    
        <ul>
            <xsl:apply-templates>
                <xsl:with-param name="layout-tree" select="$layout-tree"/>
            </xsl:apply-templates>
        </ul>
    </xsl:template>
  
    <!-- Show search result for the admin pages -->
    <xsl:template match="search:documents" mode="admin">
        <xsl:param name="layout-tree" select="."/>
    
        <!-- Getting all needed setting-->
        <xsl:variable name="show-summary" 
                      select="foundry:get-setting('search', 
                                                  'show-summary', 
                                                  'true', $layout-tree/show-summary)"/>
        <xsl:variable name="admin-result-mode" 
                  select="foundry:get-setting('search', 
                                              'admin-result-mode', 
                                              'table', 
                                              $layout-tree/admin-result-mode)"/>
    
        <xsl:choose>
            <xsl:when test="$admin-result-mode = 'table'">
                <!-- Create table header for search results -->
                <table id="result-list">
                    <tr class="result-list-header">
                        <th>
                            <xsl:value-of select="foundry:get-static-text('search', 
                                                                          'listheader/score')"/>
                        </th>
                        <th>
                            <xsl:value-of select="foundry:get-static-text('search',
                                                                          'listheader/title')"/>
                        </th>
                        <xsl:if test="$show-summary = 'true'">
                            <th class="summary">
                                <xsl:value-of select="foundry:get-static-text('search',
                                                                          'listheader/summary')"/>
                            </th>
                        </xsl:if>
                        <th style="width: 10em;">
                            <xsl:value-of select="foundry:get-static-text('search',
                                                                          'listheader/addlink')"/>
                        </th>
                    </tr>
                    <xsl:apply-templates mode="admin"/>
                </table>

                <!-- Alternative Version ohne Tabellen. Funktioniert noch nicht
                        <div class="result-list-header">
                          <span style="width: 4em;">
                            <xsl:call-template name="mandalay:getStaticText">
                              <xsl:with-param name="module" select="'search'"/>
                              <xsl:with-param name="id" select="'listheader/score'"/>
                            </xsl:call-template>
                          </span>
                          <span>
                            <xsl:call-template name="mandalay:getStaticText">
                              <xsl:with-param name="module" select="'search'"/>
                              <xsl:with-param name="id" select="'listheader/title'"/>
                            </xsl:call-template>
                          </span>
                          <span>
                            <xsl:call-template name="mandalay:getStaticText">
                              <xsl:with-param name="module" select="'search'"/>
                              <xsl:with-param name="id" select="'listheader/summary'"/>
                            </xsl:call-template>
                          </span>
                          <span style="width: 10em;">
                            <xsl:call-template name="mandalay:getStaticText">
                              <xsl:with-param name="module" select="'search'"/>
                              <xsl:with-param name="id" select="'listheader/addlink'"/>
                            </xsl:call-template>
                          </span>
                        </div>
                -->
      
            </xsl:when>
            <xsl:when test="$admin-result-mode = 'list'">
                <div id="result-list">
                    <div class="result-list-header">
                        <xsl:value-of select="foundry:get-static-text('search', 
                                                                      'resultlist/header')"/>
                    </div>
                    <ul>
                        <xsl:apply-templates mode="admin"/>
                    </ul>
                </div>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
  
</xsl:stylesheet>
