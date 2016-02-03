<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>]>
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
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns:xs="http://www.w3.org/2001/XMLSchema"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop foundry ui"
                version="2.0">

    <foundry:doc-file>
        <foundry:doc-file-title>Object lists</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                This tags are used to create the HTML representation of 
                object lists.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for an object list. Checks if there is an object list
                and outputs it using the HTML definied in it if there is any.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list">
        <xsl:variable name="object-list-id" select="./@id"/>
        
        <xsl:if test="$data-tree//nav:simpleObjectList[@id = $object-list-id]
                      | $data-tree//nav:complexObjectList[@id = $object-list-id]
                      | $data-tree//nav:customizableObjectList[@id = $object-list-id]
                      | $data-tree//nav:atozObjectList[@id = $object-list-id]
                      | $data-tree//nav:filterObjectList[@id = $object-list-id]">
            
            <xsl:variable name="object-list-datatree">
                <xsl:choose>
                    <xsl:when test="$data-tree//nav:simpleObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:simpleObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                    <xsl:when test="$data-tree//nav:complexObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:complexObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                    <xsl:when test="$data-tree//nav:customizableObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:customizableObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                    <xsl:when test="$data-tree//nav:atozObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:atozObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                    <xsl:when test="$data-tree//nav:filterObjectList[@id = $object-list-id]">
                        <xsl:copy-of select="$data-tree//nav:filterObjectList[@id = $object-list-id]/*"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:variable>
            
            <xsl:if test="count($object-list-datatree/nav:objectList/nav:item) &gt;= 1">
                <xsl:apply-templates>
                    <xsl:with-param name="object-list-datatree" 
                                    tunnel="yes" 
                                    select="$object-list-datatree"/>
                </xsl:apply-templates>
            </xsl:if>
        </xsl:if>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Defines the HTML for outputting an individual object in an 
                object list.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//object-list-item" name="object-list-items">
        <xsl:param name="object-list-datatree" tunnel="yes"/>
        <xsl:param name="class-first" select="''"/>
        <xsl:param name="class-last" select="''"/>
        
        <!--<pre>Object-list-item</pre>
        <pre>
            <xsl:value-of select="concat('count(object-list-datatree) = ', count($object-list-datatree))"/>
        </pre>
        <pre>
            <xsl:value-of select="concat('count(object-list-datatree/*) = ', count($object-list-datatree/*))"/>
        </pre>
        <pre>
            <xsl:value-of select="concat('name(object-list-datatree/*[1]) = ', name($object-list-datatree/*[1]))"/>
        </pre>
        <pre>
            <xsl:value-of select="concat('count(object-list-datatree/nav:objectList/nav:item = ', count($object-list-datatree/nav:objectList/nav:item))"/>
        </pre>-->
    
        <xsl:variable name="object-list-item-layouttree" select="current()"/>
    
        <xsl:for-each select="$object-list-datatree/nav:objectList/nav:item">
            <xsl:apply-templates select="$object-list-item-layouttree/*">
                <xsl:with-param name="contentitem-tree" 
                                tunnel="yes" 
                                select="current()"/>
                <xsl:with-param name="id"
                                select="concat(./nav:attribute[@name = 'masterVersion.id'], 
                                               '_', 
                                               nav:attribute[@name = 'name'])"/>
                <xsl:with-param name="href" 
                                tunnel="yes" 
                                select="./nav:path"/>
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
                Root element for creating the paginator for an object list. Provides the paginator
                data for the elements enclosed by this element via XSL parameters. The content is
                of this element is only processed if the number of pages is greater than one or
                if the <code>show</code> attribute is set to <code>always</code>.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="show">
                <p>
                    If set to <code>always</code> the paginator is shown even if there is only one
                    page.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="object-list//paginator">
        <xsl:param name="object-list-datatree" tunnel="yes"/>
        
        <xsl:if test="($object-list-datatree/nav:objectList/nav:paginator/@pageCount &gt; 1)
                       or ./@show = 'always'">
            <xsl:apply-templates>
                <xsl:with-param name="paginator-baseurl"
                                tunnel="yes">
                    <xsl:choose>
                        <xsl:when test="contains($object-list-datatree/nav:objectList/nav:paginator/@baseURL, '?')">
                            <xsl:value-of select="concat($object-list-datatree/nav:objectList/nav:paginator/@baseURL, '&amp;')"/>
                        </xsl:when>
                        <xsl:when test="not(contains($object-list-datatree/nav:objectList/nav:paginator/@baseURL, '?'))">
                            <xsl:value-of select="concat($object-list-datatree/nav:objectList/nav:paginator/@baseURL, '?')"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="paginator-object-begin"
                                tunnel="yes"
                                select="$object-list-datatree/nav:objectList/nav:paginator/@objectBegin"/>
                <xsl:with-param name="paginator-object-count"
                                tunnel="yes"
                                select="$object-list-datatree/nav:objectList/nav:paginator/@objectCount"/>
                <xsl:with-param name="paginator-object-end"
                                tunnel="yes"
                                select="$object-list-datatree/nav:objectList/nav:paginator/@objectEnd"/>
                <xsl:with-param name="paginator-page-count" 
                                tunnel="yes"
                                select="$object-list-datatree/nav:objectList/nav:paginator/@pageCount"/>
                <xsl:with-param name="paginator-page-number" 
                                tunnel="yes"
                                select="$object-list-datatree/nav:objectList/nav:paginator/@pageNumber"/>
                <xsl:with-param name="paginator-page-param" 
                                tunnel="yes"
                                select="$object-list-datatree/nav:objectList/nav:paginator/@pageParam"/>
                <xsl:with-param name="paginator-page-size" 
                                tunnel="yes"
                                select="$object-list-datatree/nav:objectList/nav:paginator/@pageSize"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the index of the first object shown on the current page. The value is 
                provided by the surrounding <code>paginator</code> element via a XSL parameter.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//object-begin">
        <xsl:param name="paginator-object-begin" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-begin != ''">
            <xsl:value-of select="$paginator-object-begin"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the index of the last object shown on the current page. The value is 
                provided by the surrounding <code>paginator</code> element via a XSL parameter.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//object-end">
        <xsl:param name="paginator-object-end" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-end != ''">
            <xsl:value-of select="$paginator-object-end"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the number of elements in list.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//object-count">
        <xsl:param name="paginator-object-count" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-count != ''">
            <xsl:value-of select="$paginator-object-count"/>
        </xsl:if>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the number of pages.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//page-count">
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-count != ''">
            <xsl:value-of select="$paginator-page-count"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the number of the current page.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//current-page">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-number != ''">
            <xsl:value-of select="$paginator-page-number"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the size of page (the number of items on each page).
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//page-size">
        <xsl:param name="paginator-page-size" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-size != ''">
            <xsl:value-of select="$paginator-page-size"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the URL to the previous page of the list for an enclosed <code>a</code> 
                element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//prev-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') and ($paginator-page-number &gt; 1)">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-number -1)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the URL to the next page of the list for an enclosed <code>a</code> 
                element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//next-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') 
                      and ($paginator-page-number &lt; $paginator-page-count)">
            <xsl:apply-templates>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-number + 1)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the URL to the first page of the list for an enclosed 
                <code>a</code>  element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//first-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') 
                       and ($paginator-page-number &gt; 1)">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, '=1')"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the URL to the last page of the list for an enclosed 
                <code>a</code> element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="object-list//paginator//last-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') 
                      and ($paginator-page-number &lt; $paginator-page-count)">
            <xsl:apply-templates>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-count)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="object-list//filter-controls">
        
        <xsl:apply-templates select="$data-tree//filterControls" />
        
    </xsl:template>
    
    <xsl:template match="filters">
        <xsl:if test="$data-tree//filters">
            <form>
                <xsl:apply-templates>
                    <xsl:with-param name="filters" 
                                tunnel="yes" 
                                select="$data-tree//filters"/>
                </xsl:apply-templates>
            
                <input type="submit" 
                   label="{foundry:get-static-text('filters', 
                                                   'apply-filters')}"/>
            </form>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="filters//filter">
        <xsl:param name="filters" tunnel="yes"/>
        
        <xsl:variable name="filter-layouttree" select="."/>
        
        <xsl:for-each select="$filters/filter">
            <xsl:apply-templates select="$filter-layouttree/*">
                <xsl:with-param name="filter" tunnel="yes" select="."/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="filters//filter//filter-label">
        <xsl:param name="filter" tunnel="yes"/>
        
        <label for="$filter/@label">
            <xsl:call-template name="foundry:set-id-and-class"/>

            <xsl:value-of select="foundry:get-static-text('filters',
                                                          $filter/@label)"/> 
        </label>
    </xsl:template>
    
    <xsl:template match="filters//filter//filter-control">
        <xsl:param name="filter" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$filter/@type = 'text'">
                
                <input type="text" id="{$filter/@label}">
                    <xsl:call-template name="foundry:set-id-and-class"/>
                </input>
            </xsl:when>
            <xsl:when test="$filter/@type = 'select'">
                <select id="{$filter/@label}">
                    <xsl:call-template name="foundry:set-id-and-class"/>
                    <xsl:for-each select="$filter/option">
                        <option value="{./@label}">
                            <xsl:if test="./@selected = 'selected'">
                                <xsl:attribute name="selected"
                                               select="'selected'"/>
                            </xsl:if>
                            <xsl:value-of select="./@label"/>
                        </option>
                    </xsl:for-each>
                </select>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

</xsl:stylesheet>