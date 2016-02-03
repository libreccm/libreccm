<?xml version="1.0" encoding="utf-8"?>
<!--
    Copyright 2014 Jens Pelzetter for the LibreCCM Foundation
    
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
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">
    
    <foundry:doc-file>
        <foundry:doc-file-title>Tags for ccm-cms-types-mparticle</foundry:doc-file-title>
        <foundry:doc-desc>
            <p>
                This file provides tags for displaying the contents of a 
                Multipart Article. 
            </p>
        </foundry:doc-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the summary of the multi part article.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-summary">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/summary">
                <xsl:value-of select="$contentitem-tree/summary"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'summary']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'summary']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for outputting the list of sections of a MPA.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-sections">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root for rendering a link to a section of a MPA in the list of sections.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-sections//mpa-section">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        <xsl:variable name="section-layout-tree" select="current()"/>
        <xsl:variable name="current-page" 
                      select="$contentitem-tree/cms:articleSectionPanel/@pageNumber"/>
        <xsl:variable name="number-of-pages"
                      select="$contentitem-tree/cms:mpadata/numberOfPages"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/cms:articleSectionPanel/toc">
                <xsl:for-each select="$contentitem-tree/cms:articleSectionPanel/toc/section">
                    <xsl:apply-templates select="$section-layout-tree/*">
                        <xsl:with-param name="mpa-section-title" tunnel="yes" select="."/>
                        <xsl:with-param name="href" tunnel="yes" select="@link"/>
                        <xsl:with-param name="class" 
                                        select="if(./@rank = ../../cms:item/rank)
                                                then 'active'
                                                else ''"/>
                    </xsl:apply-templates>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
        
                <xsl:for-each select="$contentitem-tree/sections">
                    <xsl:sort select="./rank" data-type="number"/>
            
                    <xsl:variable name="current-rank" select="./rank"/>
                    <xsl:variable name="page-number" 
                                  select="count($contentitem-tree/sections[./pageBreak = 'true' 
                                                                   and ./rank &lt; ($current-rank + 1)])"/>
            
                    <xsl:apply-templates select="$section-layout-tree/*">
                
                        <xsl:with-param name="mpa-section-title" tunnel="yes" select="./title"/>
                        <xsl:with-param name="href" tunnel="yes">
                            <xsl:choose>
                                <xsl:when test="./pageBreak = 'true' 
                                        or ($page-number &lt;= $number-of-pages)">
                                    <xsl:choose>
                                        <xsl:when test="$current-page = 'all' 
                                                or $page-number = $current-page">
                                            <xsl:value-of select="concat('#section-', 
                                                                         $current-rank)"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="concat('?page=', 
                                                         $page-number, 
                                                         '#section-', 
                                                         $current-rank)"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                            
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="concat('#section-', $current-rank)"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:with-param>
                    </xsl:apply-templates>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Output the name a section of MPA in the list of sections.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-sections//mpa-section//mpa-section-title">
        <xsl:param name="mpa-section-title" tunnel="yes"/>

        <xsl:value-of select="$mpa-section-title"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for outputting the current sections of a MPA.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-current-sections">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for outputting a current section of a MPA. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-current-sections//mpa-current-section">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        <xsl:variable name="current-section-layout-tree" select="current()"/>
        
        <xsl:for-each select="$contentitem-tree/cms:articleSectionPanel/cms:item">
            <xsl:sort select="./rank"/>
            
            <xsl:apply-templates select="$current-section-layout-tree/*">
                <xsl:with-param name="current-section-title" tunnel="yes" select="./title"/>
                <xsl:with-param name="current-section-content" tunnel="yes" select="./text"/>
                <xsl:with-param name="id" select="concat('section-', ./rank)"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the title of a current section of a MPA. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-current-sections//mpa-current-section//mpa-current-section-title">
        <xsl:param name="current-section-title" tunnel="yes"/>
        
        <xsl:value-of select="$current-section-title"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the content of a current section of a MPA. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-current-sections//mpa-current-section//mpa-current-section-content">
        <xsl:param name="current-section-content" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes" select="$current-section-content"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the parameters (URL via the <code>href</code> parameter) for the link
                to the previous page of a MPA if there is a previous page.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-prev-page-link">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/cms:articleSectionPanel/@pageNumber != 'all' 
                      and $contentitem-tree/cms:articleSectionPanel/@pageNumber &gt; 1">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="concat('?page=', $contentitem-tree/cms:articleSectionPanel/@pageNumber - 1)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the parameters (URL via the <code>href</code> parameter) for the link
                to the next page of a MPA if there is a next page.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-next-page-link">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/cms:articleSectionPanel/@pageNumber != 'all' 
                      and xs:integer($contentitem-tree/cms:articleSectionPanel/@pageNumber) &lt; xs:integer($contentitem-tree/cms:mpadata/numberOfPages)">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="concat('?page=', $contentitem-tree/cms:articleSectionPanel/@pageNumber + 1)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the parameters (URL via the <code>href</code> parameter) for the link
                to show all section of a MPA on one page.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//mpa-all-sections-link">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/cms:articleSectionPanel/@pageNumber != 'all' 
                      and $contentitem-tree/cms:mpadata/numberOfPages &gt; 1">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="'?page=all'"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>