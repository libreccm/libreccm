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
  Processing search filter
-->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation" 
                xmlns:search="http://rhea.redhat.com/search/1.0"
                exclude-result-prefixes="xsl bebop cms foundry nav search"
                version="2.0">
  
    <!-- Search filter for content type -->
    <xsl:template match="search:filter[@type='contentType']">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:if test="foundry:boolean(foundry:get-setting('search', 
                                                          'filter/show-content-type-filter', 
                                                          'true'))">
            <div class="filter">
                <span class="filterName">
                    <xsl:value-of select="foundry:get-static-text('search', 'searchfilter/types')"/>
                </span>
                <span class="filterParam">
                    <select size="10" name="{@param}" multiple="multiple">
                        <xsl:for-each select="search:contentType">
                            <xsl:sort select="@title"/>
                            <option value="{@name}">
                                <xsl:if test="@isSelected">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </span>
            </div>
        </xsl:if>
    </xsl:template>
  
    <!-- Search filter for object type -->
    <xsl:template match="search:filter[@type='objectType']">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:if test="foundry:boolean(foundry:get-setting('search', 
                                                          'filter/show-object-type-filter', 
                                                          'true'))">
            <div class="filter">
                <span class="filterName">
                    <xsl:value-of select="foundry:get-static-text('search', 'searchfilter/types')"/>
                </span>
                <span class="filterParam">
                    <select size="10" name="{@param}" multiple="multiple">
                        <xsl:for-each select="search:objectType">
                            <xsl:sort select="@name"/>
                            <option value="{@name}">
                                <xsl:if test="@isSelected">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="@name"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </span>
            </div>
        </xsl:if>
    </xsl:template>
  
    <!-- Search filter for host -->
    <xsl:template match="search:filter[@type='host']">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:if test="foundry:boolean(foundry:get-setting('search', 
                                                          'filter/show-host-filter', 
                                                          'true'))">
            <div class="filter">
                <span class="filterName">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/sites')"/>
                </span>
                <span class="filterParam">
                    <select size="10" name="{@param}" multiple="multiple">
                        <xsl:for-each select="search:remoteHost">
                            <option value="{@oid}">
                                <xsl:if test="@isSelected">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </span>
            </div>
        </xsl:if>
    </xsl:template>
  
    <!-- Search filter for category -->
    <xsl:template match="search:filter[@type='category']">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:if test="foundry:boolean(foundry:get-setting('search', 
                                                          'filter/show-category-filter', 
                                                          'true'))">
            <div class="filter">
                <span class="filterName">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/categories')"/>
                </span>
                <span class="filterParam">
                    <select size="10" name="{@param}" multiple="multiple">
                        <xsl:for-each select="search:category">
                            <xsl:sort select="@title"/>
                            <option value="{@oid}">
                                <xsl:if test="@isSelected">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                    <br/>
                    <input type="checkbox" value="true">
                        <xsl:attribute name="name">
                            <xsl:value-of select="search:includeSubCats/@name"/>
                        </xsl:attribute>
                        <xsl:if test="search:includeSubCats/@value = 'true'">
                            <xsl:attribute name="checked">checked</xsl:attribute>
                        </xsl:if>
                    </input>
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/searchRecursiv')"/>
                </span>
            </div>
        </xsl:if>
    </xsl:template>
  
    <!-- Search filter for author -->
    <xsl:template match="search:filter[@type='creationUser']">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:if test="foundry:boolean(foundry:get-setting('search', 
                                                          'show-creation-user-filter',
                                                          'true'))">
            <xsl:apply-templates select="search:partyText">
                <xsl:with-param name="filterName">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                              'searchfilter/creationUser')"/>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
  
    <!-- Search filter for last editor -->
    <xsl:template match="search:filter[@type='lastModifiedUser']">
        <xsl:param name="layoutTree" select="."/>

        <xsl:if test="foundry:boolean(foundry:get-setting('search',
                                                          'show-last-editor-filter',
                                                          'true'))">
            <xsl:apply-templates select="search:partyText">
                <xsl:with-param name="filterName">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/lastModUser')"/>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
  
    <!-- Search filter for lauch date -->
    <xsl:template match="search:filter[@type='launchDate']">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:if test="foundry:boolean(foundry:get-setting('search' , 
                                                          'filter/show-launch-date-filter', 
                                                          'true'))">
            <xsl:call-template name="search:dateRangeFilter">
                <xsl:with-param name="filterName">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/lauchDate')"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
  
    <!-- Search filter for last modified date -->
    <xsl:template match="search:filter[@type='lastModifiedDate']">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:if test="foundry:boolean(foundry:get-setting('search',
                                                          'show-last-modified-date-filter',
                                                          'true'))">
            <xsl:call-template name="search:dateRangeFilter">
                <xsl:with-param name="filterName">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/lastModDate')"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
  
    <!-- Search filter for creation date -->
    <xsl:template match="search:filter[@type='creationDate']">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:if test="foundry:boolean(foundry:get-setting('search', 
                                                          'filter/show-creation-date-filter',
                                                          'true'))">
            <xsl:call-template name="search:dateRangeFilter">
                <xsl:with-param name="filterName">
                    <xsl:value-of select="foundry:get-static-text('search',
                                                                  'searchFilter/creationDate')"/>
                </xsl:with-param>
            </xsl:call-template>
        </xsl:if>
    </xsl:template>
  
    <!-- Create widgets for a date range filter -->
    <xsl:template name="search:dateRangeFilter">
        <xsl:param name="filterName"/>
        <div class="filter">
            <span class="filterName">
                <xsl:value-of select="$filterName"/>
            </span>
            <span class="filterParam">
                <xsl:choose>
                    <xsl:when test="@format">
                        <xsl:call-template name="search:newStyleDateRangeFilter"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="search:oldStyleDateRangeFilter"/>
                    </xsl:otherwise>
                </xsl:choose>
            </span>
        </div>
    </xsl:template>

    <xsl:template name="search:newStyleDateRangeFilter">  
        <xsl:variable name="fragment1">
            <xsl:value-of select="substring-before(@format, ' ')"/>
        </xsl:variable>
        <xsl:variable name="fragment2">
            <xsl:value-of select="substring-before(substring-after(@format, concat($fragment1, ' ')), ' ')"/>
        </xsl:variable>
        <xsl:variable name="fragment3">
            <xsl:value-of select="substring-after(@format, concat($fragment1, ' ', $fragment2, ' '))"/>
        </xsl:variable>
        <table>
            <tr>
                <td/>
                <th>
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  concat('searchfilter/dateRange/', $fragment1))"/>
                </th>
                <th>
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  concat('searchfilter/dateRange/', $fragment2))"/>
                </th>
                <th>
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  concat('searchfilter/dateRange/', $fragment3))"/>
                </th>
            </tr>
            <tr>
                <th align="right">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/dateRange/from')"/>
                </th>
                <xsl:call-template name="search:dateRangeFragment">
                    <xsl:with-param name="fragment" select="$fragment1"/>
                    <xsl:with-param name="mode" select="'start'"/>
                </xsl:call-template>
                <xsl:call-template name="search:dateRangeFragment">
                    <xsl:with-param name="fragment" select="$fragment2"/>
                    <xsl:with-param name="mode" select="'start'"/>
                </xsl:call-template>
                <xsl:call-template name="search:dateRangeFragment">
                    <xsl:with-param name="fragment" select="$fragment3"/>
                    <xsl:with-param name="mode" select="'start'"/>
                </xsl:call-template>
            </tr>
            <tr>
                <th align="right">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/dateRange/to')"/>
                </th>
                <xsl:call-template name="search:dateRangeFragment">
                    <xsl:with-param name="fragment" select="$fragment1"/>
                    <xsl:with-param name="mode" select="'end'"/>
                </xsl:call-template>
                <xsl:call-template name="search:dateRangeFragment">
                    <xsl:with-param name="fragment" select="$fragment2"/>
                    <xsl:with-param name="mode" select="'end'"/>
                </xsl:call-template>
                <xsl:call-template name="search:dateRangeFragment">
                    <xsl:with-param name="fragment" select="$fragment3"/>
                    <xsl:with-param name="mode" select="'end'"/>
                </xsl:call-template>
            </tr>
        </table>
    </xsl:template>
  
    <xsl:template name="search:dateRangeFragment">
        <xsl:param name="fragment" select="''"/>
        <xsl:param name="mode" select="''"/>
    
        <xsl:choose>
      
            <xsl:when test="$fragment = 'day'">
                <td>
                    <input size="2" type="text" name="{@param}.start.day">
                        <xsl:choose>
                            <xsl:when test="$mode = 'start' and search:day/@startDay">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="./search:day/@startDay"/>
                                </xsl:attribute>
                            </xsl:when>
                            <xsl:when test="$mode = 'end' and search:day/@endDay">
                                <xsl:attribute name="value">
                                    <xsl:value-of select="./search:day/@endDay"/>
                                </xsl:attribute>
                            </xsl:when>
                            <xsl:otherwise/>
                        </xsl:choose>
                    </input>
                </td>
            </xsl:when>
      
            <xsl:when test="$fragment = 'month'">
                <td>
                    <select name="{@param}.start.month">
                        <xsl:for-each select="search:month">
                            <option value="{@value}">
                                <xsl:choose>
                                    <xsl:when test="$mode = 'start' and @startMonth">
                                        <xsl:attribute name="selected">selected</xsl:attribute>
                                    </xsl:when>
                                    <xsl:when test="$mode = 'end' and @endMonth">
                                        <xsl:attribute name="selected">selected</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise/>
                                </xsl:choose>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </td>
            </xsl:when>

            <xsl:when test="$fragment = 'year'">
                <td>
                    <select name="{@param}.start.year">
                        <xsl:for-each select="search:year">
                            <option value="{@value}">
                                <xsl:choose>
                                    <xsl:when test="$mode = 'start' and @startYear">
                                        <xsl:attribute name="selected">selected</xsl:attribute>
                                    </xsl:when>
                                    <xsl:when test="$mode = 'end' and @endYear">
                                        <xsl:attribute name="selected">selected</xsl:attribute>
                                    </xsl:when>
                                    <xsl:otherwise/>
                                </xsl:choose>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </td>
            </xsl:when>
            <xsl:otherwise/>
        </xsl:choose>

    </xsl:template>
  
    <xsl:template name="search:oldStyleDateRangeFilter">
        <table>
            <tr>
                <td/>
                <th>
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/dateRange/day')"/>
                </th>
                <th>
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/dateRange/month')"/>
                </th>
                <th>
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/dateRange/year')"/>
                </th>
            </tr>
            <tr>
                <th align="right">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/dateRange/from')"/>
                </th>
                <td>
                    <input size="2" type="text" name="{@param}.start.day">
                        <xsl:if test="search:day/@startDay">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./search:day/@startDay"/>
                            </xsl:attribute>
                        </xsl:if>
                    </input>
                </td>
                <td>
                    <select name="{@param}.start.month">
                        <xsl:for-each select="search:month">
                            <option value="{@value}">
                                <xsl:if test="@startMonth">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </td>
                <td>
                    <select name="{@param}.start.year">
                        <xsl:for-each select="search:year">
                            <option value="{@value}">
                                <xsl:if test="@startYear">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </td>
            </tr>
            <tr>
                <th align="right">
                    <xsl:value-of select="foundry:get-static-text('search', 
                                                                  'searchfilter/dateRange/to')"/>
                </th>
                <td>
                    <input size="2" type="text" name="{@param}.end.day">
                        <xsl:if test="search:day/@endDay">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./search:day/@endDay"/>
                            </xsl:attribute>
                        </xsl:if>
                    </input>
                </td>
                <td>
                    <select name="{@param}.end.month">
                        <xsl:for-each select="search:month">
                            <option value="{@value}">
                                <xsl:if test="@endMonth">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </td>
                <td>
                    <select name="{@param}.end.year">
                        <xsl:for-each select="search:year">
                            <option value="{@value}">
                                <xsl:if test="@endYear">
                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                </xsl:if>
                                <xsl:value-of select="@title"/>
                            </option>
                        </xsl:for-each>
                    </select>
                </td>
            </tr>
        </table>
    </xsl:template>

</xsl:stylesheet>
