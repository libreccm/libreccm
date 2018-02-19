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
  Processing search queries
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation" 
                xmlns:search="http://rhea.redhat.com/search/1.0"
                exclude-result-prefixes="xsl bebop cms foundry nav search" 
                version="2.0">
  
    <!-- Show the search queries for the admin pages -->
    <xsl:template match="search:query">
        <xsl:param name="layoutTree" select="."/>
    
        <style type="text/css" media="screen">
            #resultList {display: table; }
            #resultList .resultListHeader {display: table-row; background-color: #eee; width: 100%; color: #999; font-weight: bold; }
            #resultList .resultListHeader span {display: table-cell; padding-top: 0.1em; padding-bottom: 0.1em;}
            #resultList .result {display: table-row; }
            #resultList .result span {display: table-cell; padding-bottom: 0.7em; padding-right: 1em; }
            #resultList .result.even {background-color: #f6f6f6;}
            <!--
                  #search {display: table;}
                  #search .query {display: table-cell; text-align: right; vertical-align: top; font-weight: bold; padding-right: 1em;}
                  #search fieldset {display: table-row;}
                  #search .terms {display: table-cell; padding-bottom: 1.2em;}
                  #search .filter {display: table-row;}
                  #search .filterName {display: table-cell; text-align: right; vertical-align: top; font-weight: bold; padding-right: 1em;}
                  #search .filterParam {display: table-cell; padding-bottom: 1.2em;}
            -->
            <xsl:choose>
                <xsl:when test="'false'">
                </xsl:when>
                <xsl:otherwise>
                </xsl:otherwise>
            </xsl:choose>
        </style>
        <div id="search">
            <!-- Don't show, if there is already a search result -->
            <xsl:if test="foundry:boolean(foundry:get-setting('search', 
                                                              'show-search-form-in-result-list',
                                                              'true')) 
                          or not(../search:results)">
                <xsl:apply-templates select="search:terms"/>
            </xsl:if>
            <xsl:if test="search:*[not(self::search:terms)] 
                          and foundry:boolean(foundry:get-setting('search', 
                                                                  'show-addition-filter', 
                                                                  'true'))">
                <fieldset class="advancedSearch">
                    <legend onclick="javascript:parent.display('none');">
                        <xsl:value-of select="foundry:get-static-text('search', 
                                                                      'additionalFilters')"/>
                    </legend>
                    <xsl:apply-templates select="search:*[not(self::search:terms)]"/>
                </fieldset>
            </xsl:if>
        </div>
    </xsl:template>
  
</xsl:stylesheet>
