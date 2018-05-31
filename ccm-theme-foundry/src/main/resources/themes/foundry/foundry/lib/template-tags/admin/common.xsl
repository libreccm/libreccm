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
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop cms foundry ui"
                version="2.0">

    <xsl:template match="show-bebop-contextbar">
        <xsl:apply-templates select="$data-tree/bebop:contextBar">
            <xsl:with-param name="layout-tree" select="."/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="show-bebop-page-title">
        <xsl:apply-templates select="$data-tree/bebop:title"/>
    </xsl:template>
            
    <xsl:template match="show-body-column">
        <xsl:choose>
            <xsl:when test="$data-tree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:body[//bebop:formWidget] 
                            | $data-tree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:right[//bebop:formWidget]">
                <form>
                    <xsl:attribute name="method" 
                                   select="if ($data-tree//bebop:form/@method)
                                           then $data-tree//bebop:form/@method
                                           else 'post'"/>
                    <!--<xsl:attribute name="method" select="'post'"/>-->
                    <xsl:call-template name="foundry:process-datatree-attributes"/>
                    <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:body 
                                                 | $data-tree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:right"/>
                    <!--<xsl:message>
                        <xsl:value-of select="concat('Applying template for bebop:pageState to ', 
                                                     count($data-tree//bebop:currentPane/bebop:form//bebop:pageState), 
                                                     ' objects...')"/>
                    </xsl:message>-->
                    <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:form//bebop:pageState"/>
                </form>
            </xsl:when>
      
            <xsl:when test="$data-tree//bebop:currentPane/bebop:form[not(//bebop:layoutPanel)]">
                <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:form"/>
            </xsl:when>
      
            <xsl:when test="$data-tree//bebop:currentPane/cms:container/*[name() != 'cms:container']">
                <xsl:apply-templates select="$data-tree//bebop:currentPane/cms:container/*[name() != 'cms:container']"/>
            </xsl:when>
      
            <xsl:when test="$data-tree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:body 
                            | $data-tree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:right">
                <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:body 
                                             | $data-tree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:right"/>
            </xsl:when>
            
            <xsl:when test="$data-tree/bebop:form">
                <xsl:apply-templates select="$data-tree/bebop:form"/>
            </xsl:when>
      
            <xsl:otherwise>
                <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:layoutPanel/bebop:body 
                                             | $data-tree//bebop:currentPane/bebop:layoutPanel/bebop:right 
                                             | $data-tree//bebop:currentPane/cms:itemSummary 
                                             | $data-tree//bebop:currentPane/cms:categorySummary 
                                             | $data-tree//bebop:currentPane/cms:linkSummary 
                                             | $data-tree//bebop:currentPane/cms:lifecycleSummary 
                                             | $data-tree//bebop:currentPane/cms:workflowSummary 
                                             | $data-tree//bebop:currentPane/cms:transactionSummary"/>
                <!--| $data-tree//bebop:currentPane/bebop:reactApp-->
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
            
    <xsl:template match="show-left-column">
        <xsl:choose>
            <xsl:when test="$data-tree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:left[//bebop:formWidget]">
                <form>
                    <xsl:attribute name="method" 
                                   select="if ($data-tree//bebop:form/@method)
                                           then $data-tree//bebop:form/@method
                                           else 'post'"/>
                    <!--<xsl:attribute name="method" select="'post'"/>-->
                    <xsl:call-template name="foundry:process-datatree-attributes"/>
                    <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:form//bebop:layoutPanel/bebop:left"/>
                    <!--<xsl:message>
                        <xsl:value-of select="concat('Applying template for bebop:pageState to ', 
                                                     count($data-tree//bebop:currentPane/bebop:form//bebop:pageState), 
                                                     ' objects...')"/>
                    </xsl:message>-->
                    <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:form//bebop:pageState"/>
                </form>
            </xsl:when>
      
            <xsl:when test="$data-tree//bebop:currentPane/cms:container/cms:container">
                <xsl:apply-templates select="$data-tree//bebop:currentPane/cms:container/cms:container"/>
            </xsl:when>
      
            <xsl:when test="$data-tree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:left">
                <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:boxPanel//bebop:layoutPanel/bebop:left"/>
            </xsl:when>
      
            <xsl:otherwise>
                <xsl:apply-templates select="$data-tree//bebop:currentPane/bebop:layoutPanel/bebop:left"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="show-system-information">
        <div class="system-information">
            <xsl:apply-templates select="$data-tree/bebop:systemInformation"/>
        </div>
    </xsl:template>
    
    <xsl:template match="show-tabbed-pane">
        <xsl:apply-templates select="$data-tree/bebop:tabbedPane">
            <xsl:with-param name="layout-tree" select="."/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="site-logo">
        <xsl:variable name="src"
                      select="foundry:get-setting('', 'site-logo', '')"/>
        
        <xsl:choose>
            <xsl:when test="string-length($src) &gt; 0">
                <img src="{foundry:gen-path($src)}"/> 
            </xsl:when>
            <xsl:otherwise>
                <img src="{foundry:gen-path('images/scientificcms_logo.png', 'internal')}"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="if-contains-react-app">
        <xsl:choose>
            <xsl:when test="$data-tree//bebop:reactApp">
                <xsl:apply-templates select="./when/*" />
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="./otherwise/*" />
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="load-react-app">
        <xsl:if test="$data-tree//bebop:reactApp">
            <div class="react-data" 
                 id="{$data-tree//bebop:reactApp/@appId}"            
                 data-ccm-application="{$data-tree//bebop:reactApp/@ccmApplication}"
                 data-dispatcher-prefix="{$data-tree//bebop:reactApp/@dispatcherPrefix}">
            </div>
            <script src="{$data-tree//bebop:reactApp/@scriptPath}"></script>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
