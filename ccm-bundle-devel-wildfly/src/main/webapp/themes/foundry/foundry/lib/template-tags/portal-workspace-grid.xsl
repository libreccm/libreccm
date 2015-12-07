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
        <foundry:doc-file-title>Tags for portal-workspace-grid</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>ToDo</p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <xsl:template match="portal-grid-workspace">
        <!--<pre>grid-workspace</pre>-->
        
        <xsl:apply-templates>
            <xsl:with-param name="use-default-styles" 
                            tunnel="yes"
                            select="foundry:boolean(./@use-default-styles)"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace//portal-grid-workspace-rows">
        <xsl:choose>
            <xsl:when test="./@rows">
                <xsl:variable name="rows-attr" 
                              select="tokenize(./@rows, ',')"/>
                <xsl:apply-templates> 
                    <xsl:with-param name="rows"
                                    tunnel="yes"
                                    select="$data-tree/portal:gridWorkspace/portal:rows/portal:row[not(empty(index-of($rows-attr, @title)))]"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates>
                    <xsl:with-param name="rows" 
                                    tunnel="yes"
                                    select="$data-tree/portal:gridWorkspace/portal:rows/portal:row"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>

    </xsl:template>
    
    <xsl:template match="portal-grid-workspace//portal-grid-workspace-rows//portal-grid-workspace-row">
        <xsl:param name="rows" tunnel="yes"/>

        <xsl:variable name="row-layout-tree">
            <xsl:copy-of select="./*"/>
        </xsl:variable>
                     
        <xsl:for-each select="$rows">
            <xsl:variable name="row-title" select="./@title"/>
            
            <xsl:choose>                
                <xsl:when test="$row-layout-tree/row-layout[not(empty(index-of(tokenize(@rows, ','), $row-title)))]">
                    <xsl:apply-templates select="$row-layout-tree/row-layout[not(empty(index-of(tokenize(@rows, ','), $row-title)))]/*">                            
                        <xsl:with-param name="row-data-tree" tunnel="yes" select="current()"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$row-layout-tree/row-layout[not(@rows)]/*">
                        <xsl:with-param name="row-data-tree" tunnel="yes" select="current()"/>
                    </xsl:apply-templates>                            
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace-columns">
        <!--<pre>grid-workspace-row-columns</pre>-->
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace-columns//portal-grid-workspace-column">
        <xsl:param name="row-data-tree" tunnel="yes"/>
        
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
                      select="$row-data-tree/@layout"/>
        
        <xsl:for-each select="tokenize($layout-format, ',')">
            
            <xsl:variable name="col-number" select="position()"/>
            <xsl:variable name="col-id" select="concat('[', $col-number, ']')"/>
            
            <xsl:choose>
                <xsl:when test="$column-layout-tree[@format=$layout-format]/column-layout[contains(@for, $col-id)]">
                    <xsl:apply-templates select="$column-layout-tree[@format=$layout-format]/column-layout[contains(@for, $col-id)]/*">
                        <xsl:with-param name="column-portlets"
                                        tunnel="yes"
                                        select="$row-data-tree/portal:portlets/bebop:portlet[@cellNumber = $col-number]"/>
                    </xsl:apply-templates>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$column-layout-tree[@format=$layout-format]/column-layout[not(@for)]/*">
                        <xsl:with-param name="column-portlets"
                                        tunnel="yes"
                                        select="$row-data-tree/portal:portlets/bebop:portlet[@cellNumber = $col-number]"/>
                    </xsl:apply-templates>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="portal-grid-workspace-columns//portal-grid-workspace-column//portal-grid-workspace-column-portlets">
        <xsl:param name="column-portlets" tunnel="yes"/>
       
        <xsl:variable name="workspace" 
                      select="$data-tree/portal:gridWorkspace/portal:workspaceDetails/primaryURL"/>
        
        <xsl:variable name="template-map">
            <xsl:copy-of select="document(foundry:gen-path('conf/templates.xml'))/templates/portlets/*"/>
        </xsl:variable>
            
        <!--<xsl:apply-templates select="$column-portlets"/>-->
        <xsl:for-each select="$column-portlets">
            <xsl:variable name="classname">
                <xsl:value-of select="./@bebop:classname"/>
            </xsl:variable>
            <xsl:variable name="title">
                <xsl:value-of select="./@title"/>
            </xsl:variable>
            
            <!--<pre>
                <xsl:value-of select="concat('portlet-class = ', ./@bebop:classname)"/>
            </pre>
            <pre>
                <xsl:value-of select="concat('in workspace = ', $workspace)"/>
            </pre>
            <pre>
                <xsl:value-of select="count($template-map/portlet[@class=$classname])"/>
            </pre>-->
            
            <xsl:choose>
                <xsl:when test="$template-map/portlet[@class=$classname and @workspace = $workspace and @title=$title]">
                    <xsl:apply-templates select="document(foundry:gen-path(concat('templates/', normalize-space($template-map/portlet[@class=$classname and @workspace=$workspace and @title=$title]))))/*">
                        <xsl:with-param name="portlet-data-tree" tunnel="yes" select="current()"/>
                    </xsl:apply-templates>
                </xsl:when>
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
                <xsl:otherwise>
                    <pre>
                        <xsl:value-of select="concat('failed to find a template for ', $classname)"/>
                    </pre>
                </xsl:otherwise>
            </xsl:choose>
            
            
        </xsl:for-each>
    </xsl:template>

</xsl:stylesheet>
