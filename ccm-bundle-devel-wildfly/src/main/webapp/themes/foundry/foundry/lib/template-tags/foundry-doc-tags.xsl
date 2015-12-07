<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>
                      <!ENTITY ndash '&#8211;'>]>
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
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">

    <!--
        These tags are part of the documentation system of Foundry. Therefore there is no file
        description here. The documentation system, including the tags defined in this file
        are described in the Developer Manual part in the documentation of Foundry.
    -->
    
    <xsl:template match="doc-chapter-list">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="doc-chapter-entry">
        <xsl:variable name="doc-chapter-entry-tree" select="./*"/>
        
        <xsl:for-each select="$foundry-doc-tree/foundry:doc-chapter">
            <xsl:apply-templates select="$doc-chapter-entry-tree">
                <xsl:with-param name="href" tunnel="yes" select="concat('#', ./@chapter-id)"/>
                <xsl:with-param name="chapter-title" tunnel="yes" select="./@chapter-title"/>
                <xsl:with-param name="doc-sections" 
                                tunnel="yes" 
                                select="./foundry:doc-section"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-chapter-title">
        <xsl:param name="chapter-title" tunnel="yes" select="''"/>
        
        <xsl:value-of select="$chapter-title"/>
    </xsl:template>

    <xsl:template match="doc-css-files">
        <xsl:for-each select="./doc-css-file">
            <link rel="stylesheet" type="text/css">
                <xsl:attribute name="href" select="current()"/>
            </link>
        </xsl:for-each>
    </xsl:template>

    <xsl:template match="doc-section-list">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="doc-section-entry">
        <xsl:param name="doc-sections" tunnel="yes"/>
        
        <xsl:variable name="doc-section-entry-tree" select="./*"/>
        
        <xsl:for-each select="$doc-sections">
            <xsl:apply-templates select="$doc-section-entry-tree">
                <xsl:with-param name="href" tunnel="yes" select="concat('#', ./@section-id)"/>
                <xsl:with-param name="section-title" tunnel="yes" select="./@section-title"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-section-title">
        <xsl:param name="section-title" tunnel="yes" select="''"/>
        
        <xsl:value-of select="$section-title"/>
    </xsl:template>
    
    <xsl:template match="doc-chapters">
        <xsl:for-each select="$foundry-doc-tree/foundry:doc-chapter">
            
            <xsl:apply-templates select="document('../../templates/doc/foundry-doc-chapter.xml')/*">
                <xsl:with-param name="chapter-id" tunnel="yes" select="./@chapter-id"/>
                <xsl:with-param name="chapter-title" tunnel="yes" select="./@chapter-title"/>
                <xsl:with-param name="doc-chapter-tree" tunnel="yes" select="./*"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-chapter-layout">
        <xsl:param name="chapter-id" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="id" select="$chapter-id"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-chapter-id">
        <xsl:param name="chapter-id" tunnel="yes"/>
        
        <xsl:attribute name="id">
            <xsl:value-of select="$chapter-id"/>
        </xsl:attribute>
    </xsl:template>
    
    <xsl:template match="doc-sections">
        <xsl:param name="doc-chapter-tree" tunnel="yes"/>
        
        <xsl:for-each select="$doc-chapter-tree[name()='foundry:doc-section']">
            <xsl:apply-templates select="document('../../templates/doc/foundry-doc-section.xml')/*">
                <xsl:with-param name="section-id" tunnel="yes" select="./@section-id"/>
                <xsl:with-param name="section-title" tunnel="yes" select="./@section-title"/>
                <xsl:with-param name="section-static" tunnel="yes" select="./@static"/>
                <xsl:with-param name="section-generate" tunnel="yes" select="./@generate"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-section-layout">
        <xsl:param name="section-id" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="id" select="$section-id"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-section-id">
        <xsl:param name="section-id" tunnel="yes"/>
        
        <xsl:attribute name="id">
            <xsl:value-of select="$section-id"/>
        </xsl:attribute>
    </xsl:template>
  
    <xsl:template match="doc-section-content">
        <xsl:param name="section-static" tunnel="yes" select="''"/>
        <xsl:param name="section-generate" tunnel="yes" select="''"/>
        
        <xsl:choose>
            <xsl:when test="$section-static != ''">
                <xsl:choose>
                    <xsl:when test="document(concat('../../../doc/static-texts/', $section-static))">
                        <xsl:copy-of select="document(concat('../../../doc/static-texts/', $section-static))//main"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <div class="doc-error">
                            <xsl:value-of select="concat('Missing static text doc/static-texts/', $section-static)"/>
                        </div>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:when test="$section-generate != ''">
                <xsl:apply-templates select="document(foundry:gen-path('foundry/main.xsl'))" 
                                     mode="doc">
                    <xsl:with-param name="section-generate" 
                                    tunnel="yes" 
                                    select="$section-generate"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <div class="doc-error">
                    None of the attributes <code>static</code> or <code>generate</code> has been
                    set at the definition of this section.
                </div>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="xsl:import" mode="doc">
        <xsl:param name="section-generate" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="count(document(./@href)//foundry:doc[@section=$section-generate]) &gt; 0">
                <!-- Only generate a section for the file if there is document. -->
                <section>
                    <xsl:attribute name="id" 
                                   select="concat($section-generate, '-', replace(./@href, '/', '-'))"/>
            
                    <h1>
                        <xsl:value-of select="document(./@href)/xsl:stylesheet/foundry:doc-file/foundry:doc-file-title"/>
                    </h1>
                    <xsl:copy-of select="document(./@href)/xsl:stylesheet/foundry:doc-file/foundry:doc-file-desc/*"/>
            
                    <xsl:apply-templates select="document(./@href)//xsl:import" mode="doc">
                        <xsl:sort select="./@href"/>
                    </xsl:apply-templates>
                    <xsl:apply-templates select="document(./@href)//foundry:doc[@section=$section-generate]" 
                                         mode="doc">
                        <xsl:sort select="./following::xsl:template[1]/@match"/>
                    </xsl:apply-templates>
                </section>
            </xsl:when>
            <xsl:otherwise>
                <!-- Process imports in the file  -->
                <xsl:apply-templates select="document(./@href)//xsl:import" mode="doc">
                    <xsl:sort select="./@href"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="foundry:doc-file">
        <!-- Empty template to avoid that the foundry:doc-file tag is copied to the HTML output -->
    </xsl:template>
    
    <xsl:template match="foundry:doc-title">
        <!-- Empty template to avoid that the foundry:doc-file-title tag is copied to the HTML 
        output -->
    </xsl:template>
    
    <xsl:template match="foundry:doc-file-desc">
        <!-- Empty template to avoid that the foundry:doc-file-desc tag is copied to the HTML 
        output -->
    </xsl:template>
    
    <xsl:template match="foundry:doc[@type='function']" mode="doc">
        <xsl:apply-templates select="document(foundry:gen-path('foundry/templates/doc/function-layout.xml'))">
            <xsl:with-param name="function-name" 
                            tunnel="yes"
                            select="./following::xsl:function[1]/@name"/>
            <xsl:with-param name="result" 
                            tunnel="yes"
                            select="./foundry:doc-result"/>
            <xsl:with-param name="doc-desc" tunnel="yes" select="./foundry:doc-desc"/>
            <xsl:with-param name="doc-params" tunnel="yes" select="./foundry:doc-params"/>
            <xsl:with-param name="doc-see-also" tunnel="yes" select="./foundry-doc-see-also"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="foundry:doc-desc">
        <!-- Empty template to avoid that the foundry:doc-file-desc tag is copied to the HTML 
        output -->
    </xsl:template>
    
    <xsl:template match="doc-function-layout">
        <xsl:param name="function-name" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="id" select="$function-name"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-function-name">
        <xsl:param name="function-name" tunnel="yes"/>
        
        <xsl:value-of select="$function-name"/>
    </xsl:template>
    
    <xsl:template match="doc-function-desc">
        <xsl:param name="doc-desc" tunnel="yes"/>
        
        <xsl:copy-of select="$doc-desc/*"/>
    </xsl:template>
    
    <xsl:template match="doc-function-params">
        <xsl:param name="doc-params" tunnel="yes"/>
        
        <xsl:if test="$doc-params">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="doc-function-param">
        <xsl:param name="doc-params" tunnel="yes"/>
        
        <xsl:variable name="doc-function-param-layout" select="current()"/>
        
        <xsl:for-each select="$doc-params/foundry:doc-param">
            <xsl:apply-templates select="$doc-function-param-layout/*">
                <xsl:with-param name="param-name" tunnel="yes" select="./@name"/>
                <xsl:with-param name="param-type" tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="./@type">
                            <xsl:value-of select="./@type"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="'any'"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="param-mandatory" tunnel="yes" select="./@mandatory"/>
                <xsl:with-param name="param-desc" tunnel="yes" select="./*"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-function-param-name">
        <xsl:param name="param-name" tunnel="yes"/>
        
        <xsl:value-of select="$param-name"/>
    </xsl:template>
    
    <xsl:template match="doc-function-param-type">
        <xsl:param name="param-type" tunnel="yes"/>
        
        <xsl:value-of select="$param-type"/>
    </xsl:template>
    
    <xsl:template match="doc-function-param-mandatory">
        <xsl:param name="param-mandatory" tunnel="yes"/>
        
        <xsl:value-of select="$param-mandatory"/>
    </xsl:template>
    
    <xsl:template match="doc-function-param-desc">
        <xsl:param name="param-desc" tunnel="yes"/>
        
        <xsl:copy-of select="$param-desc"/>
    </xsl:template>
    
    <xsl:template match="doc-function-result-type">
        <xsl:param name="result" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$result/@type">
                <xsl:value-of select="$result/@type"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'any'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="doc-function-result-desc">
        <xsl:param name="result" tunnel="yes"/>
        
        <xsl:copy-of select="$result/*"/>
    </xsl:template>
    
    <xsl:template match="foundry:doc[@type='function-template']" mode="doc">
        
    </xsl:template>
    
    <xsl:template match="foundry:doc[@type='global-var']" mode="doc">
        
    </xsl:template>
    
    <xsl:template match="foundry:doc[@type='template-tag']" mode="doc">
        <xsl:apply-templates select="document(foundry:gen-path('foundry/templates/doc/template-tag-layout.xml'))">
            <xsl:with-param name="matches" 
                            tunnel="yes" 
                            select="./following::xsl:template[1]/@match"/>
            <xsl:with-param name="doc-desc" tunnel="yes" select="./foundry:doc-desc"/>
            <xsl:with-param name="doc-attributes" tunnel="yes" select="./foundry:doc-attributes"/>
            <xsl:with-param name="doc-see-also" tunnel="yes" select="./foundry:doc-see-also"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-layout">
        <xsl:param name="matches" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="id" select="$matches"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-name">
        <xsl:param name="matches" tunnel="yes"/>
        
        <xsl:value-of select="$matches"/>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-desc">
        <xsl:param name="doc-desc" tunnel="yes"/>
        
        <xsl:copy-of select="$doc-desc/*"/>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-attributes">
        <xsl:param name="doc-attributes" tunnel="yes"/>
        
        <xsl:if test="$doc-attributes">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-attribute">
        <xsl:param name="doc-attributes" tunnel="yes"/>
        
        <xsl:variable name="doc-template-tag-attribute-layout" select="current()"/>
        
        <xsl:for-each select="$doc-attributes/foundry:doc-attribute">
            <xsl:apply-templates select="$doc-template-tag-attribute-layout/*">
                <xsl:with-param name="attribute-name" tunnel="yes" select="./@name"/>
                <xsl:with-param name="attribute-desc" tunnel="yes" select="./*"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-attribute-name">
        <xsl:param name="attribute-name" tunnel="yes"/>
        
        <xsl:value-of select="$attribute-name"/>
    </xsl:template>
    
    <xsl:template match="doc-template-tag-attribute-desc">
        <xsl:param name="attribute-desc" tunnel="yes"/>
        
        <xsl:copy-of select="$attribute-desc"/>
    </xsl:template>
    
    <xsl:template match="foundry:doc[@type='env-var']" mode="doc">
        <xsl:apply-templates select="document(foundry:gen-path('foundry/templates/doc/env-var-layout.xml'))">
            <xsl:with-param name="name" 
                            tunnel="yes">
                <xsl:choose>
                    <xsl:when test="name(./following::*[1]) = 'xsl:variable'">
                        <xsl:value-of select="./following::xsl:variable[1]/@name"/>
                    </xsl:when>
                    <xsl:when test="name(./following::*[1]) = 'xsl:param'">
                        <xsl:value-of select="./following::xsl:param[1]/@name"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:with-param> 
            <!--                select="./following::xsl:variable[1]/@name"/>-->
            <xsl:with-param name="doc-desc" tunnel="yes" select="./foundry:doc-desc"/>
            <xsl:with-param name="doc-see-also" tunnel="yes" select="./foundry:doc-see-also"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-envvar-layout">
        <xsl:param name="name" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="id" select="$name"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="doc-env-var-name">
        <xsl:param name="name" tunnel="yes"/>
        
        <xsl:value-of select="$name"/>
    </xsl:template>
    
    <xsl:template match="doc-env-var-desc">
        <xsl:param name="doc-desc" tunnel="yes"/>
        
        <xsl:copy-of select="$doc-desc/*"/>
    </xsl:template>
    
    <xsl:template match="doc-see-also-link-list">
        <xsl:param name="doc-see-also" tunnel="yes"/>
   
        <xsl:if test="$doc-see-also">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="doc-see-also-link">
        <xsl:param name="doc-see-also" tunnel="yes"/>
        
        <xsl:variable name="doc-see-also-layout" select="current()"/>
        
        <xsl:for-each select="$doc-see-also/foundry:doc-link">
            <xsl:apply-templates select="$doc-see-also-layout/*">
                <xsl:with-param name="href" tunnel="yes" select="./@href"/>
                <xsl:with-param name="title" tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="string-length(current()) &gt; 0">
                            <xsl:value-of select="current()"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./@href"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="doc-see-also-link-title">
        <xsl:param name="title" tunnel="yes"/>
        
        <xsl:value-of select="$title"/>
    </xsl:template>
    
</xsl:stylesheet>