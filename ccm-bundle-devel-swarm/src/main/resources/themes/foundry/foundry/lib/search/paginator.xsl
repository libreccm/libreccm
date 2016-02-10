<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
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

<!-- Support for the search paginator -->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation" 
                xmlns:search="http://rhea.redhat.com/search/1.0"
                exclude-result-prefixes="xsl bebop cms foundry nav search" 
                version="2.0">
    
    <xsl:template match="search:paginator" mode="header">
        
        <xsl:if test="./@pageCount &gt; 1">
            
            <span class="paginator header">
                <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                              'header/resultinfo/text/begin')"/>
                <span class="objBegin">
                    <xsl:value-of select="./@objectBegin"/>
                </span>
                <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                              'header/resultinfo/text/inbetween1')"/>
                <span class="objEnd">
                    <xsl:value-of select="./@objectEnd"/>
                </span>
                
                <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                              'header/resultinfo/text/inbetween2')"/>
                <span id="objCount">
                    <xsl:value-of select="./@objectCount"/>
                </span>
                
                <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                              'header/resultinfo/text/end')"/>
                <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                              'header/pageinfo/text/begin')"/>
                <xsl:value-of select="./@pageSize"/>
                <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                              'header/pageinfo/text/end')"/>
            </span>
            
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="search:paginator" mode="navbar">
        
        <xsl:if test="./@pageCount &gt; 1">
            
            <xsl:variable name="page-param" select="if (./@pageParam)
                                                    then ./@pageParam
                                                    else 'pageNumber='"/>
            <xsl:variable name="url">
                <xsl:choose>
                    <xsl:when test="./@baseURL and contains(./@baseURL, '?')">
                        <xsl:value-of select="concat(./@baseURL, '&amp;')"/>
                    </xsl:when>
                    <xsl:when test="./@baseURL and not(contains(./@baseURL, '?'))">
                        <xsl:value-of select="concat(./@baseURL, '?')"/>
                    </xsl:when>
                    <xsl:when test="not(./@baseURL) 
                                    and //nav:letter 
                                    and contains(//nav:letter[./@selected = '1']/@url, '?')">
                        <xsl:value-of select="concat(//nav:letter[./@selected = '1']/@url, '&amp;')"/>
                    </xsl:when>
                    <xsl:when test="not(./@baseURL) 
                                    and //nav:letter 
                                    and not(contains(//nav:letter[./@selected = '1']/@url, '?'))">
                        <xsl:value-of select="concat(//nav:letter[@selected = '1']/@url, '?')"/>
                    </xsl:when>
                    <xsl:when test="not(./@baseURL) 
                                    and not(//nav:letter) 
                                    and not(contains(/bebop:page/@url, '?'))">
                        <xsl:value-of select="concat(/bebop:page/@url, '?')"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:variable name="page-number" as="xs:integer" select="./@pageNumber"/>
            <xsl:variable name="page-count" as="xs:integer" select="./@pageCount"/>
            
            <span class="paginator navbar">
                <xsl:if test="$page-number &gt; 1">
                    <a class="prev" 
                       href="{concat($url, ./@pageParam, '=', $page-number - 1)}"
                       accesskey="{foundry:get-static-text('search-paginator', 
                                                           'navbar/prev/accesskey')}"
                       title="{foundry:get-static-text('search-paginator',
                                                       'navbar/prev/title')}">
                        <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                                      'navbar/prev/link')"/>
                    </a>
                    <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                                  'navbar/separator')"/>
                </xsl:if>
                <span class="pages">
                    <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                                  'navbar/pageNumber/prefix')"/>
                    <xsl:value-of select="$page-number"/>
                    <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                                  'navbar/pageNumber/separator')"/>
                    <xsl:value-of select="$page-count"/>
                </span>
                <xsl:if test="$page-number &lt; $page-count">
                    <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                                  'navbar/separator')"/>
                    <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                                  'navbar/separator')"/>
                    <a href="{concat($url, ./@pageParam, '=', ./@pageNumber + 1)}"
                       accesskey="{foundry:get-static-text('search-paginator', 
                                                           'navbar/next/accesskey')}"
                       title="{foundry:get-static-text('search-paginator', 
                                                           'navbar/next/title')}">
                        <xsl:value-of select="foundry:get-static-text('search-paginator',
                                                                      'navbar/next/link')"/>
                    </a>
                </xsl:if>
            </span>
            
        </xsl:if>
        
    </xsl:template>
    
</xsl:stylesheet>