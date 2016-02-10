<?xml version="1.0" encoding="utf-8"?>
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
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">
    
    <foundry:doc-file>
        <foundry:doc-file-title>Tags for ccm-sci-types-institute</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The tags in this file are used to output the special properties
                of the SciInstitute content type.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" typ="template-tag">
        <foundry:doc-desc>
            <p>
                Display the description of a SciInstitute.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciinstitute-desc">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/instituteDesc">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data/instituteDescription"/>
            </xsl:when>
            <xsl:when test="$orgaunit-data/description">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data/description"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for generating a list of members of a SciInstitute.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciinstitute-members">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="members"
                            tunnel="yes"
                            select="$orgaunit-data"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the short description of a institute.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciinstitute-shortdesc">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/instituteShortDescription">
                <xsl:value-of select="$orgaunit-data/instituteShortDescription"/>
            </xsl:when>
            <xsl:when test="$orgaunit-data/shortDesc">
                <xsl:value-of select="$orgaunit-data/shortDescription"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for rendering a list of the departments of an 
                institute.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciinstitute-departments">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:if test="$orgaunit-data/departments">
            <xsl:apply-templates>
                <xsl:with-param name="departments" 
                                tunnel="yes" 
                                select="$orgaunit-data/departments"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Encloses the tags for showing a department.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciinstitute-departments//department">
        <xsl:param name="departments" tunnel="yes"/>
        
        <xsl:variable name="layout-tree" select="./*"/>
        
        <xsl:for-each select="$departments/department">
            <xsl:apply-templates select="$layout-tree">
                <xsl:with-param name="oid" tunnel="yes" select="./@oid"/>
                <xsl:with-param name="title" tunnel="yes" select="./title"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Extracts the link to detail view of a department and passes
                it to the enclosed tags.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciinstitute-departments//department//department-link">
        <xsl:param name="oid" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="href" 
                            tunnel="yes" 
                            select="foundry:generate-contentitem-link($oid)"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the name of the current department.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//sciinstitute-departments//department//department-name">
        <xsl:param name="title" tunnel="yes"/>
        
        <xsl:value-of select="$title"/>
    </xsl:template>
    
</xsl:stylesheet>