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

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation" 
                xmlns:search="http://rhea.redhat.com/search/1.0"
                exclude-result-prefixes="xsl bebop cms foundry nav search" 
                version="2.0">
  
    <!-- Show the search results -->
    <xsl:template match="search:object">
        <xsl:param name="layoutTree" select="."/>
    
        <li>
            <span class="re">
                <xsl:if test="foundry:boolean(foundry:get-setting('search', 'show-score', 'true'))">
                    <div class="score">
                        <xsl:choose>
                            <xsl:when test="foundry:boolean(foundry:get-setting('search', 
                                                                  'show-graphic-score', 
                                                                  'true'))">
                                <xsl:attribute name="style" 
                                               select="concat('background-image: url(', foundry:gen-path('images/search/score-empty.gif', 'internal'), '); ',
                                                              'background-repeat: no-repeat; ',
                                                              'width: ', foundry:get-setting('search', 'graphic-score-width', '50'), 'px; ')"/>
                                <div class="imgFull">
                                    <xsl:attribute name="style"
                                                   select="concat('font-size: 0px; overflow: hidden; width: ', ./@score, '%; height: ', foundry:get-setting('search', 'graphic-score-height', '10'), 'px; ')"/>
                                    <img>
                                        <xsl:attribute name="src" 
                                                       select="foundry:gen-path('images/search/score-full.gif', 'internal')"/>
                                        <xsl:attribute name="alt">
                                            <xsl:value-of select="./@score"/>%</xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="./@score"/>%</xsl:attribute>
                                    </img>
                                </div>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="./@score"/>%&#x20;
                            </xsl:otherwise>
                        </xsl:choose>
                    </div>
                </xsl:if>
                
                <xsl:value-of disable-output-escaping="yes" select="./search:jsAction"/>
                <span>
                    <a>
                        <xsl:attribute name="href">
                            <xsl:choose>
                                <xsl:when test="/bebop:page/@class='cms-admin' 
                                                and not(./@class = 'jsButton')">
                                    <xsl:value-of select="concat($context-prefix, '/ccm/', ./@contentSectionName, '/admin/item.jsp?item_id=', ./@id, '&amp;set_tab=1')"/>
                                </xsl:when>
                                <xsl:when test="./@class = 'jsButton'">
                                    <xsl:value-of select="'#'"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="./@url"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:attribute>
                        <xsl:if test="./search:jsAction">
                            <xsl:attribute name="onClick">
                                <xsl:value-of select="./search:jsAction/@action"/>
                            </xsl:attribute>
                        </xsl:if>
                        <xsl:value-of select="./@title"/>
                    </a>
                </span>
            </span>
            <xsl:if test="foundry:boolean(foundry:get-setting('search', 'show-summary', 'true'))">
                <br />
                <span class="summary">
                    <xsl:value-of select="./@summary" disable-output-escaping="yes"/>
                </span>
            </xsl:if>
        </li>
    </xsl:template>
  
    <!-- 
        Show search results for admin pages. These a still using tables, so there is a
        special processing for the results. 
      
    -->
    <xsl:template match="search:object" mode="admin">
        <xsl:param name="layoutTree" select="."/>
      
        <div class="searchResults">
            <xsl:choose>
                <xsl:when test="position() mod 2 = 0">
                    <xsl:attribute name="class">result even</xsl:attribute>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class">result odd</xsl:attribute>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="foundry:boolean(foundry:get-setting('search', 'show-score', 'true'))">
                <span class="score">
                    <xsl:choose>
                        <xsl:when test="foundry:boolean(foundry:get-setting('search', 
                                                                            'show-graphic-score', 
                                                                              'true'))">
                            <div class="score">
                                <xsl:attribute name="style" 
                                               select="concat('background-image: ', foundry:gen-path('images/search/score-empty.gif', 'internal'), '; ',
                                                              'background-repeat: no-repeat; ',
                                                              'width: ', foundry:get-setting('search', 'graphic-score-width', '50'), 'px; ')"/>
                                <div class="imgFull">
                                    <xsl:attribute name="style"
                                                   select="concat('font-size: 0px; overflow: hidden; width: ', ./@score, '; height: ', foundry:get-setting('search', 'graphic-score-height', '10'), 'px; ')"/>
                                    <img>
                                        <xsl:attribute name="src" 
                                                       select="foundry:gen-path('images/search/score-full.gif', 'internal')"/>
                                        <xsl:attribute name="alt">
                                            <xsl:value-of select="./@score"/>%</xsl:attribute>
                                        <xsl:attribute name="title">
                                            <xsl:value-of select="./@score"/>%</xsl:attribute>
                                    </img>
                                </div>
                            </div>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./@score"/>%
                        </xsl:otherwise>
                    </xsl:choose>
                </span>
            </xsl:if>
            <span>
                <a href="{./@url}&amp;context=draft">
                    <xsl:value-of select="concat(@title, ' (', ./@locale, ')')"/>
                </a>
            </span>
            <xsl:if test="foundry:boolean(foundry:get-setting('search', 'show-summary', 'true'))">
                <span>
                    <em>
                        <xsl:value-of select="@summary"/>
                    </em>
                </span>
            </xsl:if>
            <xsl:if test="./@class='jsButton' or ./@class='radioButton'">
                <span>
                    <xsl:value-of disable-output-escaping="yes" select="search:jsAction"/>
                    <a onClick="{search:jsAction/@name}" href="javascript:{search:jsAction/@name}">
                        <img>
                            <xsl:attribute name="src" 
                                           select="foundry:gen-path('images/search/action.png', 
                                                                     'internal')"/>
                            <xsl:attribute name="alt"
                                           select="foundry:get-static-text('search', 
                                                                           'resultList/select')"/>
                            <xsl:attribute name="title" 
                                           select="foundry:get-static-text('search', 
                                                                            'resultlist/select')"/>
                        </img>
                        <xsl:value-of select="foundry:get-static-text('search', 
                                                                      'resultlist/select')"/>
                    </a>
                </span>
            </xsl:if>
        </div>
    </xsl:template>
  
</xsl:stylesheet>
