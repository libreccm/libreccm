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
    
    <xsl:import href="content-items/article.xsl"/>
    <xsl:import href="content-items/bookmark.xsl"/>
    <xsl:import href="content-items/contact.xsl"/>
    <xsl:import href="content-items/decisiontree.xsl"/>
    <xsl:import href="content-items/external-link.xsl"/>
    <xsl:import href="content-items/event.xsl"/>
    <xsl:import href="content-items/generic-orgaunit.xsl"/>
    <xsl:import href="content-items/formitem.xsl"/>
    <xsl:import href="content-items/fsi.xsl"/>
    <xsl:import href="content-items/image.xsl"/>
    <xsl:import href="content-items/mpa.xsl"/>
    <xsl:import href="content-items/news.xsl"/>
    <xsl:import href="content-items/person.xsl"/>
    <xsl:import href="content-items/scidepartment.xsl"/>
    <xsl:import href="content-items/sciinstitute.xsl"/>
    <xsl:import href="content-items/sciorga.xsl"/>
    <xsl:import href="content-items/scipublications.xsl"/>
    <xsl:import href="content-items/sciproject.xsl"/>
    <xsl:import href="content-items/siteproxy.xsl"/>
    <xsl:import href="content-items/assets/file-attachments.xsl"/>
    <xsl:import href="content-items/assets/image-attachments.xsl"/>
    <xsl:import href="content-items/assets/notes.xsl"/>
    <xsl:import href="content-items/assets/related-links.xsl"/>

    <foundry:doc-file>
        <foundry:doc-file-title>
            Tags for displaying Content Items
        </foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                These tags are common tags for displaying Content Items. 
                For most Content Types there are special tags provided by other 
                files.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                The <code>content-item</code> element with the attribute 
                <code>mode</code> set to <code>detail</code> or without the 
                attribute inserts the HTML representation of the  detail view of 
                the current content item. The content item can either be the 
                greeting  item or normal item.
            </p>
            <p>
                The HTML representation of a content item is defined using 
                special templates with the <code>contentitem-layout</code> 
                element as root. Usually these templates are located in the 
                <code>templates/content-items</code> folder. Which template is
                used for a particular content item is defined by the 
                <code>conf/templates.xml</code> file. In this file there is a 
                <code>content-items</code> element below the 
                <code>templates</code> element. The association between 
                templates and  content items is described by the 
                <code>content-item</code> elements in the 
                <code>content-items</code> element. The 
                <code>content-item</code> has four  optional attributes 
                (at least on must be present) which are used to limit the
                content items for which a template is used. The four attributes 
                are:
            </p>
            <dl>
                <dt>
                    <code>oid</code>
                </dt>
                <dd>
                    Limit the use of the template to a specific content item, 
                    identified by its OID (the OID of the master version). Can't 
                    be used in combination with the other attributes.
                </dd>
                <dt>
                    <code>content-section</code>
                </dt>
                <dd>
                    The name of the content section to which the item belongs. 
                    Can be used in combination with the <code>category</code> 
                    and <code>content-type</code> attributes.
                </dd>
                <dt>
                    <code>category</code>
                </dt>
                <dd>
                    The template is only used for the content item if the item 
                    is viewed as  item of the category. The category is set as 
                    a path containing the names the categories.
                </dd>
                <dt>
                    <code>content-type</code>
                </dt>
                <dd>
                    The content-type of the item.
                </dd>
            </dl>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#layout-templates">
                The template system
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="content-item[@mode = 'detail' or not(@mode)]">
        <xsl:if test="$data-tree/cms:contentPanel or $data-tree/nav:greetingItem">
            <xsl:call-template name="process-content-item-detail">
                <xsl:with-param name="contentitem-tree">
                    <xsl:choose>
                        <xsl:when test="$data-tree/nav:greetingItem">
                            <xsl:copy-of select="$data-tree/nav:greetingItem/cms:item/*"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:copy-of select="$data-tree/cms:contentPanel/cms:item/*"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="content-section">
                    <xsl:choose>
                        <xsl:when test="$data-tree/nav:greetingItem">
                            <xsl:value-of select="$data-tree/nav:greetingItem/cms:pathInfo/cms:sectionPath"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$data-tree/cms:contentPanel/cms:pathInfo/cms:sectionPath"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:with-param>
                <xsl:with-param name="style" select="./@style" />
            </xsl:call-template>
            
        </xsl:if>
    </xsl:template>
    
    <xsl:template name="process-content-item-detail">
        <xsl:param name="contentitem-tree"/>
        <xsl:param name="content-section" select="''"/>
        <xsl:param name="style" select="''"/>
        <xsl:param name="mode" select="'detail'"/>
        
        <xsl:variable name="oid" select="$contentitem-tree/masterVersion/@oid"/>
        <xsl:variable name="category" select="foundry:read-current-category()"/>
        <xsl:variable name="content-type" select="$contentitem-tree/objectType"/>
        <xsl:variable name="template-map">
            <xsl:choose>
                <xsl:when test="$mode = 'portlet-item' 
                                and document(foundry:gen-path('conf/templates.xml'))/templates/content-items/portlet-item">
                    <xsl:copy-of select="document(foundry:gen-path('conf/templates.xml'))/templates/content-items/portlet-item/*"/>
                </xsl:when>
                <xsl:when test="$mode = 'greeting-item' 
                                and document(foundry:gen-path('conf/templates.xml'))/templates/content-items/greeting-item">
                    <xsl:copy-of select="document(foundry:gen-path('conf/templates.xml'))/templates/content-items/greeting-item/*"/>
                </xsl:when>
                <xsl:when test="$mode = 'detail'">
                    <xsl:copy-of select="document(foundry:gen-path('conf/templates.xml'))/templates/content-items/detail/*"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:copy-of select="document(foundry:gen-path('conf/templates.xml'))/templates/content-items/detail/*"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable> 
            
        <xsl:choose>
            <xsl:when test="$template-map/content-item[@oid = $oid]">

                <xsl:call-template name="foundry:message-info">
                    <xsl:with-param name="message"
                                    select="'Found template for this special item.'"/>
                </xsl:call-template>
                    
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@oid = $oid]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
                
            <xsl:when test="$template-map/content-item[@content-section = $content-section 
                                                       and @category = $category
                                                       and @style = $style
                                                       and @content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@content-section = $content-section 
                                                                       and @category = $category
                                                                       and @style = $style
                                                                       and @content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$template-map/content-item[@content-section = $content-section 
                                                       and @category = $category
                                                       and not(@style)
                                                       and @content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@content-section = $content-section 
                                                                       and @category = $category
                                                                       and not(@style)
                                                                       and @content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$template-map/content-item[@content-section = $content-section 
                                                       and @category = $category
                                                       and @style = $style
                                                       and not(@content-type)]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@content-section = $content-section 
                                                                       and @category = $category
                                                                       and @style = $style
                                                                       and not(@content-type)]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
             <xsl:when test="$template-map/content-item[@content-section = $content-section 
                                                       and @category = $category
                                                       and not(@style)
                                                       and not(@content-type)]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@content-section = $content-section 
                                                                       and @category = $category
                                                                       and not(@style)
                                                                       and not(@content-type)]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
                
            <xsl:when test="$template-map/content-item[@content-section = $content-section 
                                                       and not(@category)
                                                       and @style = $style
                                                       and @content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@content-section = $content-section 
                                                                       and not(@category)
                                                                       and @style = $style
                                                                       and @content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$template-map/content-item[@content-section = $content-section 
                                                       and not(@category)
                                                       and not(@style)
                                                       and @content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@content-section = $content-section 
                                                                       and not(@category)
                                                                       and not(@style)
                                                                       and @content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
                
            <xsl:when test="$template-map/content-item[@content-section = $content-section 
                                                       and not(@category)
                                                       and @style = $style
                                                       and not(@content-type)]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@content-section = $content-section 
                                                                       and not(@category)
                                                                       and @style = $style
                                                                       and not(@content-type)]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$template-map/content-item[@content-section = $content-section 
                                                       and not(@category)
                                                       and not(@style)
                                                       and not(@content-type)]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[@content-section = $content-section 
                                                                       and not(@category)
                                                                       and not(@style)
                                                                       and not(@content-type)]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
                
            <xsl:when test="$template-map/content-item[not(@content-section)
                                                       and @category = $category
                                                       and @style = $style
                                                       and @content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[not(@content-section)
                                                                       and @category = $category
                                                                       and @style = $style
                                                                       and @content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$template-map/content-item[not(@content-section)
                                                       and @category = $category
                                                       and not(@style)
                                                       and @content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[not(@content-section)
                                                                       and @category = $category
                                                                       and not(@style)
                                                                       and @content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
                
            <xsl:when test="$template-map/content-item[not(@content-section)
                                                       and @category = $category
                                                       and @style = $style
                                                       and not(@content-type)]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[not(@content-section)
                                                                       and @category = $category
                                                                       and @style = $style
                                                                       and not(@content-type)]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$template-map/content-item[not(@content-section)
                                                       and @category = $category
                                                       and not(@style)
                                                       and not(@content-type)]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[not(@content-section)
                                                                       and @category = $category
                                                                       and not(@style)
                                                                       and not(@content-type)]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$template-map/content-item[not(@content-section)
                                                       and not(@category)
                                                       and @style = $style
                                                       and @content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[not(@content-section)
                                                                       and not(@category)
                                                                       and @style = $style
                                                                       and @content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            
            <xsl:when test="$template-map/content-item[not(@content-section)
                                                       and not(@category)
                                                       and not(@style)
                                                       and @content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/content-item[not(@content-section)
                                                                       and not(@category)
                                                                       and not(@style)
                                                                       and @content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
                
            <xsl:when test="$template-map/default">
                <xsl:call-template name="foundry:message-warn">
                    <xsl:with-param name="message"
                                    select="'No layout template for item found. Using default'"/>
                </xsl:call-template>
                    
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/default"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
                
            <xsl:otherwise>
                <xsl:call-template name="foundry:message-warn">
                    <xsl:with-param name="message"
                                    select="'No template for item found and not default configured. Using internal default'"/>
                </xsl:call-template>
                    
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="'contentitem-default-detail.xml'"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                    <xsl:with-param name="internal" 
                                    select="true()"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                The <code>content-item</code> with the <code>mode</code> 
                attribute set to <code>link</code> insert the HTML 
                representation of a content item. In this case the content item 
                to show is provided using by a XSL parameter which has to be 
                provided by a surrounding tag like <code>related-link</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item[@mode = 'link']">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="oid" select="$contentitem-tree/@oid"/>
        
        <xsl:variable name="content-type" select="$contentitem-tree/objectType"/>
        
        <xsl:variable name="template-map">
            <xsl:copy-of select="document(foundry:gen-path('conf/templates.xml'))/templates/content-items/link/*"/>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="$template-map/content-item[@content-type = $content-type]">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file"
                                    select="$template-map/content-item[@content-type = $content-type]"/>
                    <xsl:with-param name="contentitem-tree"
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$template-map/default">
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="$template-map/default"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="foundry:message-warn">
                    <xsl:with-param name="message"
                                    select="'No template for item found and not default configured. Using internal default'"/>
                </xsl:call-template>
                    
                <xsl:call-template name="foundry:process-contentitem-template">
                    <xsl:with-param name="template-file" 
                                    select="'contentitem-default-link.xml'"/>
                    <xsl:with-param name="contentitem-tree" 
                                    select="$contentitem-tree"/>
                    <xsl:with-param name="internal" select="true()"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                The <code>content-item</code> element with the <code>mode</code> 
                attribute set to <code>list</code> inserts the HTML 
                representation of the the list view of a content item. The list 
                view is primarily used in object lists. 
            </p>
            <p>
                As for the detail view, the HTML representation of the list view 
                of a conten item is  defined using special templates with the 
                <code>contentitem-layout</code> element as  root. Usually these 
                templates are located in the 
                <code>templates/content-items</code> folder. Which is used for a 
                particular content  item is defined in the 
                <code>conf/templates.xml</code> file. In this file there is  a 
                <code>content-items</code> element below the 
                <code>templates</code> element. 
            </p>
            <p>
                There three attributes which can be used to define in which 
                cases a specific template is used: 
            </p>
            <dl>
                <dt>style</dt>
                <dd>
                    Used to select a specific style for the list view of the 
                    item. To select a style add a <code>style</code> attribute 
                    to the <code>content-item</code> attribute in the 
                    application layout file.
                </dd>
                <dt>content-type</dt>
                <dd>The content-type of the item.</dd>
                <dt>
                    <code>category</code>
                </dt>
                <dd>
                    The template is only used for the content item if the item 
                    is viewed as item of  the category. The category is set as a 
                    path contains the names the categories.
                </dd>
            </dl>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#layout-templates">The template system</foundry:doc-link>
        </foundry:doc-see-also>           
    </foundry:doc>
    <xsl:template match="content-item[@mode = 'list']">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="category" select="foundry:read-current-category()"/>
        
        <xsl:variable name="content-type" 
                      select="if ($contentitem-tree/nav:attribute[@name = 'objectType'])
                              then $contentitem-tree/nav:attribute[@name = 'objectType']
                              else $contentitem-tree/objectType"/>
        
        <xsl:variable name="style">
            <xsl:choose>
                <xsl:when test="./@style">
                    <xsl:value-of select="./@style"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="''"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
                
        <xsl:variable name="template-map">
            <xsl:copy-of select="document(foundry:gen-path('conf/templates.xml'))/templates/content-items/list/*"/>
        </xsl:variable> 
        
        <xsl:choose>
            <xsl:when test="$style != ''">
                <xsl:choose>
                    <xsl:when test="$template-map/content-item[@style = $style
                                                               and @content-type = $content-type
                                                               and @category = $category]">
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="$template-map/content-item[@style = $style
                                                                               and @content-type = $content-type
                                                                               and @category = $category]"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$template-map/content-item[@style = $style
                                                               and @content-type = $content-type
                                                               and not(@category)]">
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="$template-map/content-item[@style = $style
                                                                               and @content-type = $content-type
                                                                               and not(@category)]"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$template-map/content-item[@style = $style
                                                               and not(@content-type)
                                                               and not(@category)]">
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="$template-map/content-item[@style = $style
                                                                               and not(@content-type)
                                                                               and not(@category)]"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$template-map/default">
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="$template-map/default"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="'contentitem-default-list.xml'"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                            <xsl:with-param name="internal" 
                                            select="true()"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="$template-map/content-item[@content-type = $content-type
                                                               and @category = $category
                                                               and not(@style)]">
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="$template-map/content-item[@content-type = $content-type
                                                                               and @category = $category
                                                                               and not(@style)]"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$template-map/content-item[@content-type = $content-type
                                                               and not(@category)
                                                               and not(@style)]">
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="$template-map/content-item[@content-type = $content-type
                                                                               and not(@category)
                                                                               and not(@style)]"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:when test="$template-map/default">
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="$template-map/default"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                        </xsl:call-template>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:call-template name="foundry:process-contentitem-template">
                            <xsl:with-param name="template-file"
                                            select="'contentitemitem-default-list'"/>
                            <xsl:with-param name="contentitem-tree" 
                                            select="$contentitem-tree"/>
                            <xsl:with-param name="internal" 
                                            select="true()"/>
                        </xsl:call-template>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template name="foundry:process-contentitem-template">
        <xsl:param name="template-file" as="xs:string"/>
        <xsl:param name="internal" as="xs:boolean" select="false()"/>
        <xsl:param name="contentitem-tree"/>
        
        <xsl:choose>
            <xsl:when test="$internal = true()">
                <xsl:apply-templates select="document(foundry:gen-path(
                                                          concat('foundry/templates/',
                                                                 normalize-space($template-file))))">
                    <xsl:with-param name="contentitem-tree" 
                                    tunnel="yes"
                                    select="$contentitem-tree"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="document(foundry:gen-path(
                                                         concat('/templates/',
                                                                normalize-space($template-file))))">
                    <xsl:with-param name="contentitem-tree" 
                                    tunnel="yes"
                                    select="$contentitem-tree"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for the layout templates describing a content item
                layout.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="id"
                            select="$contentitem-tree/name"/>
        </xsl:apply-templates>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Show the title of the current content item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//content-item-title">
        <xsl:param name="contentitem-tree" tunnel="yes"/>

        <xsl:choose>
            <xsl:when test="$contentitem-tree/title">
                <xsl:value-of select="$contentitem-tree/title"/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'title']">
                <xsl:value-of select="$contentitem-tree/nav:attribute[@name = 'title']"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides a link to content item itself. Useful if a content item 
                is shown using a portlet and you want to create a link to the 
                normal detail view of the item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//content-item-link">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="href"
                            tunnel="yes"
                            select="concat($context-prefix, 
                                           '/redirect/?oid=', 
                                           $contentitem-tree/masterVersion/@oid)"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generic tag to test if a content item has a specific property.
                The tags enclosed by <code>if-property</code> are only
                processed if the content item has a property with the provided
                name.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//if-property">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        <xsl:variable name="name" select="./@name"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/*[name() = $name]">
                <xsl:apply-templates/>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = $name]">
                <xsl:apply-templates/>
            </xsl:when>
        </xsl:choose>
        
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generic tag to show the value of the property with the 
                specified name. This tag can be used if there are no special 
                tags for a content type. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#show-date-property">
                Generic tag for showing date properties
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="/content-item-layout//show-property">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        <xsl:variable name="name" select="./@name"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/*[name() = $name]">
                <xsl:choose>
                    <xsl:when test="foundry:boolean(./@disable-output-escaping)">
                        <xsl:value-of disable-output-escaping="yes" 
                                      select="$contentitem-tree/*[name() = $name]"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$contentitem-tree/*[name() = $name]"/>
                    </xsl:otherwise>
                </xsl:choose>
                
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = $name]">
                <xsl:choose>
                    <xsl:when test="foundry:boolean(./@disable-output-escaping)">
                        <xsl:value-of disable-output-escaping="yes"
                                      select="$contentitem-tree/nav:attribute[@name = $name]"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$contentitem-tree/nav:attribute[@name = $name]"/>
                    </xsl:otherwise>
                </xsl:choose>
                
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="foundry:debug-enabled()">
                    <code>
                        <xsl:value-of select="concat('No property ', $name, ' found')"/>
                    </code>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generic tag for showing date properties. The format of the
                date is configured using one or more <code>date-element</code> 
                inside this tag. 
            </p>     
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#show-property">
                Generic tag for showing properties.
            </foundry:doc-link>
        </foundry:doc-see-also>
        <foundry:doc-see-also>
            <foundry:doc-link href="#date-format">
                How to format date values in Foundry.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="/content-item-layout//show-date-property">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        <xsl:variable name="name" select="./@name"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/*[name() = $name]">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$contentitem-tree/*[name() = $name]"/>
                    <xsl:with-param name="date-format"
                                    select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = $name]">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$contentitem-tree/nav:attribute[@name = $name]"/>
                    <xsl:with-param name="date-format"
                                    select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:if test="foundry:debug-enabled()">
                    <code>
                        <xsl:value-of select="concat('No property ', $name, ' found')"/>
                    </code>
                </xsl:if>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the <code>href</code> for creating a link to edit the current item in the 
                content centre if the current user is logged in and has the permission to edit the 
                item.
            </p>
            <p>
                To use this feature put the following snippet like the following into the 
                templates for the content items in your theme:
            </p>
            <pre>
                &#x3c;edit-link&#x3e;
                    &#x3c;div class="edit-link"&#x3e;
                        &#x3c;a&#x3e;
                            &#x3c;a&#x3e;
                                &#x26;#x270e;
                            &#x3c;/a&#x3e;
                        &#x3c;/a&#x3e;
                    &#x3c;/div&#x3e;
                &#x3c;/edit-link&#x3e;
            </pre>
            <p>
                The example uses an UTF-8 character from the <em>Dingbats</em> block (a pencil). 
                You can of course use another character, text or an image. To 
                reduce the amount of code needed you may add a fragment template and include this
                template using the <code>include</code> tag.
            </p>
            <p>
                You also have to include styles for the edit link into your theme. 
                The <em>foundry-base</em> theme, for example, puts the edit link the top right 
                corner of the area which shows the content item. This is done by the following 
                styles:
            </p>
            <pre>
                /* Sets the reference point for position: absolute 
                to the main element block */
                main {
                position: relative;
                }

                /* Don't show the edit link on a first glance */
                main .edit-link {
                display: none;
                }

                /* Display the edit link in the top right corner 
                of the content item area if the cursor is in 
                the content item area */
                main:hover .edit-link {
                display: block; 
                position: absolute;
                right: 0;
                top: 0;
                font-size: 30px;
                color: #0776a0;
                /* Blue border around the link */
                border: 1px solid #0776a0; 
                /* Make the link 32px in width and height */
                width: 32px; 
                height: 32px;
                text-align: center;
                }

                /* Remove default decoration for links */
                main:hover .edit-link a:link,
                main:hover .edit-link a:hover,
                main:hover .edit-link a:active, 
                main:hover .edit-link a:visited {
                color: #0776a0;
                text-decoration: none;
                }

            </pre>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//edit-link | fragment-layout//edit-link">
        
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/editLink">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes" 
                                select="concat($context-prefix, '/ccm', 
                                               $contentitem-tree/editLink)"/>
            </xsl:apply-templates>
        </xsl:if>
        
    </xsl:template>
  
    <xsl:function name="foundry:generate-contentitem-link">
        <xsl:param name="oid"/>
        
        <xsl:sequence select="concat($context-prefix, '/redirect/?oid=', $oid)"/>
    </xsl:function>
  
  
</xsl:stylesheet>
