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
        <foundry:doc-file-title>Tags for ccm-cms-types-scidepartment</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                Tags for displaying the special properties for SciDepartment.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the description of a SciDepartment.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//scidepartment-desc">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/departmentDescription">
                <xsl:value-of disable-output-escaping="yes" 
                              select="$orgaunit-data/departmentDescription"/>
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
                Displays the short description of a SciDepartment.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//scidepartment-shortdesc">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$orgaunit-data/departmentShortDescription">
                <xsl:value-of select="$orgaunit-data/departmentShortDescription"/>
            </xsl:when>
            <xsl:when test="$orgaunit-data/shortDesc">
                <xsl:value-of select="$orgaunit-data/shortDescription"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for rendering a list of the heads of the department
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-heads">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:variable name="heads-datatree" select="$orgaunit-data/heads"/>
        
        <xsl:if test="count($heads-datatree/head) &gt; 0">
            <xsl:apply-templates>
                <xsl:with-param name="heads-datatree" 
                                tunnel="yes" 
                                select="$heads-datatree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Renders a department head entry. To display the data from the
                person item use a <code>content-item</code> tag with the mode
                attribute set to the view mode you want to use (usually 
                <code>list</code>). If you want a different look than for
                normal object lists you can use the <code>style</code> 
                attribute.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-heads//department-head">
        <xsl:param name="heads-datatree" tunnel="yes"/>
        <xsl:param name="class-first" select="''"/>
        <xsl:param name="class-last" select="''"/>

        <xsl:variable name="head-layouttree" select="./*"/>
        
        <xsl:for-each select="$heads-datatree/head">
            <xsl:apply-templates select="$head-layouttree">
                <xsl:with-param name="contentitem-tree" 
                                tunnel="yes"
                                select="current()"/>
                <xsl:with-param name="id"
                                select="concat(./masterVersion/id, 
                                               '_',
                                               ./@name)"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="foundry:generate-contentitem-link(./@oid)"/>
                <xsl:with-param name="class">
                    <xsl:choose>
                        <xsl:when test="position() = 1">
                            <xsl:value-of select="$class-first"/>
                        </xsl:when>
                        <xsl:when test="position() = last()">
                            <xsl:value-of select="$class-last"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for rendering a list of the vice heads of the department
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-viceheads">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:variable name="viceheads-datatree" select="$orgaunit-data/viceheads"/>
        
        <xsl:if test="count($viceheads-datatree/vicehead) &gt; 0">
            <xsl:apply-templates>
                <xsl:with-param name="viceheads-datatree" 
                                tunnel="yes" 
                                select="$viceheads-datatree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Renders a department vice head entry. To display the data from the
                person item use a <code>content-item</code> tag with the mode
                attribute set to the view mode you want to use (usually 
                <code>list</code>). If you want a different look than for
                normal object lists you can use the <code>style</code> 
                attribute.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-viceheads//department-vicehead">
        <xsl:param name="viceheads-datatree" tunnel="yes"/>
        <xsl:param name="class-first" select="''"/>
        <xsl:param name="class-last" select="''"/>

        <xsl:variable name="vicehead-layouttree" select="./*"/>
        
        <xsl:for-each select="$viceheads-datatree/vicehead">
            <xsl:apply-templates select="$vicehead-layouttree">
                <xsl:with-param name="contentitem-tree" 
                                tunnel="yes"
                                select="current()"/>
                <xsl:with-param name="id"
                                select="concat(./masterVersion/id, 
                                               '_',
                                               ./@name)"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="foundry:generate-contentitem-link(./@oid)"/>
                <xsl:with-param name="class">
                    <xsl:choose>
                        <xsl:when test="position() = 1">
                            <xsl:value-of select="$class-first"/>
                        </xsl:when>
                        <xsl:when test="position() = last()">
                            <xsl:value-of select="$class-last"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for rendering a list of the secretaries of the department
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-secretariats">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:variable name="secretariats-datatree" select="$orgaunit-data/secretariats"/>
        
        <xsl:if test="count($secretariats-datatree/secretariat) &gt; 0">
            <xsl:apply-templates>
                <xsl:with-param name="secretariats-datatree" 
                                tunnel="yes" 
                                select="$secretariats-datatree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Renders a department secretariat entry. To display the data from the
                person item use a <code>content-item</code> tag with the mode
                attribute set to the view mode you want to use (usually 
                <code>list</code>). If you want a different look than for
                normal object lists you can use the <code>style</code> 
                attribute.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-secretariats//department-secretariat">
        <xsl:param name="secretariats-datatree" tunnel="yes"/>
        <xsl:param name="class-first" select="''"/>
        <xsl:param name="class-last" select="''"/>

        <xsl:variable name="secretariat-layouttree" select="./*"/>
        
        <xsl:for-each select="$secretariats-datatree/secretariat">
            <xsl:apply-templates select="$secretariat-layouttree">
                <xsl:with-param name="contentitem-tree" 
                                tunnel="yes"
                                select="current()"/>
                <xsl:with-param name="id"
                                select="concat(./masterVersion/id, 
                                               '_',
                                               ./@name)"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="foundry:generate-contentitem-link(./@oid)"/>
                <xsl:with-param name="class">
                    <xsl:choose>
                        <xsl:when test="position() = 1">
                            <xsl:value-of select="$class-first"/>
                        </xsl:when>
                        <xsl:when test="position() = last()">
                            <xsl:value-of select="$class-last"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for rendering a list of the members of the department.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-members">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:variable name="members-datatree" 
                      select="$orgaunit-data"/>
        
        <xsl:if test="count($members-datatree) &gt; 0">
            <xsl:apply-templates>
                <xsl:with-param name="members-datatree"
                                tunnel="yes"
                                select="$members-datatree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Renders a member entry. The output the member use the
                <code>content-item</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-members//member">
        <xsl:param name="members-datatree" tunnel="yes"/>
        <xsl:param name="class-first" select="''"/>
        <xsl:param name="class-last" select="''"/>
        
        <xsl:variable name="member-layouttree" select="./*"/>
        
        <xsl:variable name="separator" select="./@separator"/>
        
        <xsl:for-each select="$members-datatree/member">
            <xsl:apply-templates select="$member-layouttree">
                <xsl:with-param name="contentitem-tree"
                                tunnel="yes"
                                select="current()"/>
                <xsl:with-param name="id"
                                select="concat(./masterVersion/id, 
                                               '_',
                                               ./@name)"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="foundry:generate-contentitem-link(./@oid)"/>
                <xsl:with-param name="class">
                    <xsl:choose>
                        <xsl:when test="position() = 1">
                            <xsl:value-of select="$class-first"/>
                        </xsl:when>
                        <xsl:when test="position() = last()">
                            <xsl:value-of select="$class-last"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
            <xsl:if test="position() != last()">
                <xsl:value-of select="$separator"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for rendering a list of the projects of the department.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-projects">
        <xsl:param name="orgaunit-data" tunnel="yes"/>
        
        <xsl:variable name="projects-datatree" 
                      select="$orgaunit-data"/>
        
        <xsl:if test="count($projects-datatree) &gt; 0">
            <xsl:apply-templates>
                <xsl:with-param name="projects-datatree"
                                tunnel="yes"
                                select="$projects-datatree"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Renders a project entry. The output the member use the
                <code>content-item</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[starts-with(name(), 'orgaunit')]//department-projects//project">
        <xsl:param name="projects-datatree" tunnel="yes"/>
        <xsl:param name="class-first" select="''"/>
        <xsl:param name="class-last" select="''"/>
        
        <xsl:variable name="project-layouttree" select="./*"/>
        
        <xsl:variable name="separator" select="./@separator"/>
        
        <xsl:for-each select="$projects-datatree/project">
            <xsl:apply-templates select="$project-layouttree">
                <xsl:with-param name="contentitem-tree"
                                tunnel="yes"
                                select="current()"/>
                <xsl:with-param name="id"
                                select="concat(./masterVersion/id, 
                                               '_',
                                               ./@name)"/>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="foundry:generate-contentitem-link(./@oid)"/>
                <xsl:with-param name="class">
                    <xsl:choose>
                        <xsl:when test="position() = 1">
                            <xsl:value-of select="$class-first"/>
                        </xsl:when>
                        <xsl:when test="position() = last()">
                            <xsl:value-of select="$class-last"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
            </xsl:apply-templates>
            <xsl:if test="position() != last()">
                <xsl:value-of select="$separator"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
</xsl:stylesheet>