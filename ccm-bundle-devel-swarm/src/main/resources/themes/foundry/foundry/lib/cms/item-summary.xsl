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
    
    <!-- Show item summary -->
    <xsl:template match="cms:itemSummary">
        
        <div class="cmsSummarySection">
            <h3 class="cmsSummaryHeading">
                <xsl:value-of select="foundry:get-static-text('cms', 
                                                              'summary/itemSummary/header')"/>
            </h3>
            
            <div class="cmsSummaryBody table">
                <div class="tableRow">
                    <span class="key">
                        <xsl:value-of select="foundry:get-static-text('cms',
                                                                      'summary/itemSummary/type')"/>
                    </span>
                    <span class="value">
                        <xsl:value-of select="@objectType"/>
                    </span>
                </div>
            </div>
            <div class="tableRow">
                <span class="key">
                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                  'summary/itemSummary/name')"/>
                </span>
                <span class="value">
                    <xsl:value-of select="@name"/>
                </span>
            </div>
            <div class="tableRow">
                <span class="key">
                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                  'summary/itemSummary/title')"/>
                </span>
                <span class="value">
                    <xsl:value-of select="@title"/>
                </span>
            </div>
            <xsl:apply-templates select="cms:subjectCategories"/>
            <div class="tableRow">
                <span class="key">
                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                  'summary/itemSummary/description')"/>
                </span>
                <span class="value">
                    <xsl:value-of disable-output-escaping="yes" select="@description"/>
                </span>
            </div>
        </div>
        
    </xsl:template>
    
    <!-- show subject categories -->
    <xsl:template match="cms:subjectCategories">
        <div class="tableRow">
            <span class="key">
                <xsl:value-of select="foundry:get-static-text('cms', 
                                                              'summary/itemSummary/subjectCategories')"/>
            </span>
            <span class="value">
                <xsl:apply-templates/>
            </span>
        </div>
    </xsl:template>
    
    <!-- Shows a subject category -->
    <xsl:template match="cms:subjectCategory">
        <ul>
            <li>
                <xsl:value-of disable-output-escaping="yes" select="."/>
            </li>
        </ul>
    </xsl:template>
    
    <!-- Shows the categories -->
    <xsl:template match="cms:categorySummary">
        <div class="cmsSummarySection">
            <h3 class="cmsSummaryHeading">
                <xsl:value-of select="foundry:get-static-text('cms', 
                                                              'summary/categorySummary/header')"/>
            </h3>
            <div class="cmsSummaryBody">
                <xsl:apply-templates mode="summary"/>
            </div>
        </div>
    </xsl:template>
    
    <!-- cms:category is using to different syntax. The other one is located
    in cmsCategoryStep. -->
    <xsl:template match="cms:category" mode="summary">
        <ul class="categoryList">
            <li>
                <xsl:value-of disable-output-escaping="yes" select="."/>
            </li>
        </ul>
    </xsl:template>

    <!-- shows the stable link -->
    <xsl:template match="cms:linkSummary">
        <div class="cmsSummarySection">
            <h3 class="cmsSummaryHeading">
                <xsl:value-of select="foundry:get-static-text('cms', 
                                                              'summary/stableLink/header')"/>
            </h3>
            <div class="cmsSummaryBody">
                <a href="{@url}">
                    <xsl:value-of select="@url"/>
                </a>
            </div>
        </div>
    </xsl:template>
    
    <!-- Shows the lifecycle -->
    <xsl:template match="cms:lifecycleSummary">
        <div class="cmsSummarySection">
            <h3 class="cmsSummaryHeading">
                <xsl:value-of select="foundry:get-static-text('cms', 
                                                              'summary/lifecycle/header')"/>
            </h3>
            <div class="cmsSummaryBody table">
                <xsl:choose>
                    <xsl:when test="@noLifecycle">
                        <span class="noInfo">
                            <xsl:value-of select="foundry:get-static-text('cms', 
                                                                          'summary/lifecycle/noLifecycle')"/>
                        </span>
                    </xsl:when>
                    <xsl:otherwise>
                        <span class="key">
                            <xsl:value-of select="@name"/>
                        </span>
                        <span class="value">
                            <xsl:choose>
                                <xsl:when test="@hasBegun='false'">
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemNotYetPublished/startText')"/>
                                    <xsl:value-of select="@startDate"/>
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemNotYetPublished/middleText')"/>
                                    <xsl:value-of select="@endDateString"/>
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemNotYetPublished/endText')"/>
                                </xsl:when>
                                <xsl:when test="@hasEnded='true'">
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemAlreadyEnded/startText')"/>
                                    <xsl:value-of select="@startDate"/> 
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemAlreadEnded/middleText')"/>
                                    <xsl:value-of select="@endDate"/>
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemAlreadyEnded/endText')"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemPublished/startText')"/>
                                    <xsl:value-of select="@startDate"/> 
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemPublished/middleText')"/>
                                    <xsl:value-of select="@endDateString"/>
                                    <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                  'summary/lifecycle/itemPublished/endText')"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </span>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
        </div>    
    </xsl:template>
    
    <!-- Shows the workflow -->
    <xsl:template match="cms:workflowSummary">
        <div class="cmsSummarySection">
            <h3 class="cmsSummaryHeading">
                <xsl:value-of select="foundry:get-static-text('cms', 
                                                              'summary/workflow/header')"/>
            </h3>
            <div class="cmsSummaryBody">
                <xsl:if test="@restartWorkflowURL">
                    <a href="{@restartWorkflowURL}">
                        <xsl:value-of select="foundry:get-static-text('cms', 
                                                                      'summary/workflow/restartEditing')"/>
                    </a>
                </xsl:if>
                <xsl:apply-templates/>
            </div>
        </div>
    </xsl:template>
    
    <!-- Shows a task -->
    <xsl:template match="cms:task">
        
        <div class="cmsTask tableRow">
            <span class="tableCell">
                <span class="key">
                    <xsl:value-of select="@name"/>
                </span>
                <span class="status tableCell">
                    (
                    <xsl:value-of select="@state"/>
                    )
                </span>
            </span>
            
            <xsl:if test="not(cms:taskComment)">
                <span class="noInfo tableCell">
                    <xsl:value-of select="foundry:get-static-text('cms', 'summary/workflow/noComment')"/>
                </span>
            </xsl:if>
            <xsl:apply-templates/>
            
        </div>
    </xsl:template>
    
    <!-- Shows a non-empty comment -->
    <xsl:template match="cms:taskComment">
        <xsl:if test="./@comment != '' and ./@comment != ' '">
            <span class="cmsTaskComment">
                <xsl:value-of select="./@comment"/>
                <br />
                <span class="cmsTaskCommentCredentials">
                    <xsl:value-of select="concat(./@date, '&#x20;', './@author')"/>
                </span>
            </span>
        </xsl:if>
    </xsl:template>
    
    <!-- Shows information about revisions -->
    <xsl:template match="cms:transactionSummary">
        <div class="cmsSummarySection">
            <h3 class="cmsSummaryHeading">
                <xsl:value-of select="foundry:get-static-text('cms', 'summary/revisionSummary/header')"/>
            </h3>
            <div class="cmsSummaryBody table">
                <div class="tableRow">
                    <span class="cmsCurrentRevision tableCell">
                        <xsl:value-of select="./@lastModifiedDate"/>
                    </span>
                    <span class="cmsCurrentRevision tableCell">
                        <xsl:value-of select="foundry:get-static-text('cms', 'summary/revisionSummary/currentRevision')"/>
                    </span>
                </div>
                <xsl:apply-templates/>
                <div class="tableRow">
                    <span class="cmsInitialRevision tableCell">
                        <xsl:value-of select="@creationDate"/>
                    </span>
                    <span class="cmsInitialRevision tableCell">
                        <xsl:value-of select="foundry:get-static-text('cms', 'summary/revisionSummary/inititalRevision')"/>
                    </span>
                </div>
            </div>
        </div>
    </xsl:template>
  
    <!-- Shows information about transactions -->
    <xsl:template match="cms:transaction">
        <div class="cmsTransaction tableRow">
            <span class="cmsTransactionDate tableCell">
                <a href="/ccm{@url}">
                    <xsl:attribute name="title">
                        <xsl:value-of select="foundry:get-static-text('cms', 'summary/revisionSummary/viewRevision')"/>
                    </xsl:attribute>
                    <xsl:attribute name="alt">
                        <xsl:value-of select="foundry:get-static-text('cms', 'summary/revisionSummary/viewRevision')"/>
                    </xsl:attribute>
                    <xsl:value-of select="./@date"/>
                </a>
            </span>
            <span class="cmsTransactionAuthor tableCell">
                <xsl:value-of select="./@author"/>
            </span>
            <span class="cmsTransactionLink tableCell">
            </span>
        </div>
    </xsl:template>
    
</xsl:stylesheet>