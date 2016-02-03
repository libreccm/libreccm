<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:portal="http://www.uk.arsdigita.com/portal/1.0"
                xmlns:portlet="http://www.uk.arsdigita.com/portlet/1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop foundry nav portal portlet"
                version="2.0">
    
    <foundry:doc-file>
        <foundry:doc-file-title>Tags for portal-workspace</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                ToDo
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <xsl:template match="portal-workspace//portal-list">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-list//portal-page-link">
        <xsl:variable name="portal-page-link-notselected-tree">
            <xsl:choose>
                <xsl:when test="./not-selected">
                    <xsl:copy-of select="./not-selected/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="portal-page-link-selected-tree">
            <xsl:choose>
                <xsl:when test="./selected">
                    <xsl:copy-of select="./selected/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="./*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:for-each select="$data-tree/portal:workspace/portal:portalList/portal:portalDetails">
            <xsl:sort select="./sortKey"/>
            
            <xsl:choose>
                <xsl:when test="./@isSelected = 'true'">
                    <xsl:apply-templates select="$portal-page-link-selected-tree/*">
                        <xsl:with-param name="href" tunnel="yes" select="./@selectAction"/>
                        <xsl:with-param name="portal-page-title" tunnel="yes" select="./title"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$portal-page-link-notselected-tree/*">
                        <xsl:with-param name="href" tunnel="yes" select="./@selectAction"/>
                        <xsl:with-param name="portal-page-title" tunnel="yes" select="./title"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="portal-page-title">
        <xsl:param name="portal-page-title" tunnel="yes"/>
        
        <xsl:value-of select="$portal-page-title"/>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-edit-form">
        <xsl:apply-templates select="$data-tree/portal:workspace/portal:portalList/bebop:form[@name='editPortal']"/>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-layout-form">
        <xsl:apply-templates select="$data-tree/portal:workspace/portal:portalList/bebop:form[@name='editLayout']"/>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-add-page-link">
        <xsl:apply-templates select="$data-tree/portal:workspace/bebop:link[1]"/>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-edit-basic-properties-link">
        <xsl:apply-templates select="$data-tree/portal:workspace/bebop:link[2]"/>
    </xsl:template>
    
    <xsl:template match="portal-workspace">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-workspace-edit-links">
        <xsl:if test="$data-tree/portal:workspace/portal:workspaceDetails/@canEdit = 'true'
                      or $data-tree/portal:workspace/portal:workspaceDetails/@canAdmin = 'true'">
            <xsl:choose>
                <xsl:when test="$data-tree/portal:workspace/@id = 'view'">
                    <xsl:value-of select="foundry:get-static-text('portal-workspace', 'view/link')"/>
                </xsl:when>
                <xsl:otherwise>
                    <a href="{foundry:parse-link($data-tree/portal:workspace/portal:workspaceDetails//primaryURL)}"
                       title="{foundry:get-static-text('portal-workspace', 'view/title')}">
                        <xsl:value-of select="foundry:get-static-text('portal-workspace', 'view/link')"/>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="$data-tree/portal:workspace/portal:workspaceDetails/@canEdit = 'true'">
                &nbsp;
                <xsl:choose>
                    <xsl:when test="$data-tree/portal:workspace/@id = 'edit'">
                        <xsl:value-of select="foundry:get-static-text('portal-workspace', 'edit/link')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <a href="{foundry:parse-link(concat($data-tree/portal:workspace/portal:workspaceDetails//primaryURL, 'edit.jsp'))}"
                           title="{foundry:get-static-text('portal-workspace', 'edit/title')}">
                            <xsl:value-of select="foundry:get-static-text('portal-workspace', 'edit/link')"/>
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
            <xsl:if test="$data-tree/portal:workspace/portal:workspaceDetails/@canAdmin = 'true'">
                &nbsp;
                <xsl:choose>
                    <xsl:when test="$data-tree/portal:workspace/@id = 'admin'">
                        <xsl:value-of select="foundry:get-static-text('portal-workspace', 'admin/link')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <a href="{foundry:parse-link(concat($data-tree/portal:workspace/portal:workspaceDetails//primaryURL, 'admin/index.jsp'))}"
                           title="{foundry:get-static-text('portal-workspace', 'admin/title')}">
                            <xsl:value-of select="foundry:get-static-text('portal-workspace', 'admin/link')"/>
                        </a>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:if>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-workspace-columns">
        <!--<xsl:apply-templates>
            <xsl:with-param name="use-default-styles"
                            tunnel="yes"
                            select="foundry:boolean(./@use-default-styles)"/>
        </xsl:apply-templates>-->
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-workspace-columns//portal-workspace-column">
        <xsl:variable name="class">
            <xsl:choose>
                <xsl:when test="./@class">
                    <xsl:value-of select="./@class"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="''"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="column-layout-tree" select="./*"/>
        
        <xsl:variable name="layout-format" 
                      select="$data-tree/portal:workspace/portal:portal/@layout"/>
        
        <xsl:for-each select="tokenize($layout-format, ',')">
            
            <xsl:variable name="col-number" select="position()"/>
            <xsl:variable name="col-id" select="concat('[', $col-number, ']')"/>
            <xsl:variable name="is-last" select="position() = last()"/>
            
            <xsl:choose>
                <xsl:when test="$column-layout-tree[@format=$layout-format]/column-layout[contains(@for, $col-id)]">
                    <xsl:apply-templates select="$column-layout-tree[@format=$layout-format]/column-layout[contains(@for, $col-id)]/*">
                        <xsl:with-param name="workspace-portlets"
                                        tunnel="yes"
                                        select="$data-tree/portal:workspace/portal:portal/bebop:portlet[@cellNumber = $col-number]"/>
                        <xsl:with-param name="is-last"
                                        tunnel="yes"
                                        select="$is-last"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$column-layout-tree[@format=$layout-format]/column-layout[not(@for)]/*">
                        <xsl:with-param name="workspace-portlets"
                                        tunnel="yes"
                                        select="$data-tree/portal:workspace/portal:portal/bebop:portlet[@cellNumber = $col-number]"/>
                        <xsl:with-param name="is-last"
                                        tunnel="yes"
                                        select="$is-last"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="portal-workspace//portal-workspace-columns//portal-workspace-column//portal-workspace-portlets">
        <xsl:param name="workspace-portlets" tunnel="yes"/>
        <xsl:param name="is-last" tunnel="yes" as="xs:boolean"/>
        
        <xsl:variable name="workspace"
                      select="$data-tree/portal:workspace/portal:workspaceDetails/primaryURL"/>
        
        <xsl:variable name="template-map">
            <xsl:copy-of select="document(foundry:gen-path('conf/templates.xml'))/templates/portlets/*"/>
        </xsl:variable>
        
        <xsl:for-each select="$workspace-portlets">
            <xsl:variable name="classname">
                <xsl:value-of select="./@bebop:classname"/>
            </xsl:variable>
            
            <xsl:if test="./portlet:action">
                <ul class="portlet-edit-links">
                    <xsl:choose>
                        <xsl:when test="./portlet:action[@name = 'moveLeft'] and ./@cellNumber != 1">
                            <li>
                                <a href="{./portlet:action[@name = 'moveLeft']/@url}">&#9664;</a>
                            </li>
                        </xsl:when>
                        <xsl:otherwise>
                            <li>
                                <span>&#9664;</span>
                            </li>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        <!-- 
                            Last portlet is the form for adding a new portlet to column, therefore
                            subtract 1 from last position.
                        -->
                        <xsl:when test="./portlet:action[@name = 'moveDown'] and position() &lt; last() -1">
                            <li>
                                <a href="{./portlet:action[@name = 'moveDown']/@url}">&#9660;</a>
                            </li>
                        </xsl:when>
                        <xsl:otherwise>
                            <li>
                                <span>&#9660;</span>
                            </li>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:if test="./portlet:action[@name = 'customize']">
                        <li>
                            <a href="{./portlet:action[@name = 'customize']/@url}">&#9998;</a>
                        </li>
                    </xsl:if>
                    <xsl:if test="./portlet:action[@name = 'delete']">
                        <li>
                            <a href="{./portlet:action[@name = 'delete']/@url}">&#10006;</a>
                        </li>
                    </xsl:if>
                    <xsl:choose>
                        <xsl:when test="./portlet:action[@name = 'moveUp'] and position() != 1">
                            <li>
                                <a href="{./portlet:action[@name = 'moveUp']/@url}">&#9650;</a>
                            </li>
                        </xsl:when>
                        <xsl:otherwise>
                            <li>
                                <span>&#9650;</span>
                            </li>
                        </xsl:otherwise>
                    </xsl:choose>
                    <xsl:choose>
                        <xsl:when test="./portlet:action[@name = 'moveRight'] and not($is-last)">
                            <li>
                                <a href="{./portlet:action[@name = 'moveRight']/@url}">&#9658;</a>
                            </li>
                        </xsl:when>
                        <xsl:otherwise>
                            <li>
                                <span>&#9658;</span>
                            </li>
                        </xsl:otherwise>
                    </xsl:choose>
                    
                </ul>
            </xsl:if>
            
            <xsl:choose>
                <xsl:when test="$template-map/portlet[@class=$classname and @workspace = $workspace]">
                    <xsl:apply-templates select="document(foundry:gen-path(concat('templates/', normalize-space($template-map/portlet[@class=$classname and @workspace=$workspace]))))/*">
                        <xsl:with-param name="portlet-data-tree" tunnel="yes" select="current()"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="$template-map/portlet[@class=$classname]">
                    <xsl:apply-templates select="document(foundry:gen-path(concat('templates/', normalize-space($template-map/portlet[@class=$classname]))))/*">
                        <xsl:with-param name="portlet-data-tree" tunnel="yes" select="current()"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:when test="$template-map/default">
                    <xsl:apply-templates select="document(foundry:gen-path(concat('templates/', normalize-space($template-map/default))))/*">
                        <xsl:with-param name="portlet-data-tree" tunnel="yes" select="current()"/>
                    </xsl:apply-templates>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
        
        <!--<xsl:apply-templates select="$workspace-portlets"/>-->
    </xsl:template>
    
    <!-- Styles for Portal Admin page. Some of the containers have special names so we need templates
         for them. The content of the containers are bebop elements which are already supported.
         so we can just delegate to this templates by calling apply-templates    
    -->
    
    <xsl:template match="portal-workspace//portal-admin">
        <xsl:apply-templates select="$data-tree/portal:admin"/>
    </xsl:template>
    
    <xsl:template match="portal:admin/portal:categoryPanel">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal:admin/portal:workspaceDelete">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal:admin//portal:memberPicker">
        <xsl:apply-templates/>
    </xsl:template>
        
    <xsl:template match="portal:admin//portal:adminPicker">
        <xsl:apply-templates/>
    </xsl:template>
    
</xsl:stylesheet>