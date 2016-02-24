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

    <foundry:doc-file>
        <foundry:doc-file-title>Root template tags</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                These tags are the root elements of a layout template.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element of a template. Generates the
                <code>&lt;html&gt;</code> root element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="page-layout[not(./@extends)]">
        <html xmlns="http://www.w3.org/1999/xhtml">
            <xsl:attribute name="lang">
                <xsl:value-of select="$language"/>
            </xsl:attribute>
            <xsl:attribute name="id">
                <xsl:choose>
                    <xsl:when test="@application = 'admin' 
                                    or @application = 'content-center' 
                                    or @application = 'content-section' 
                                    or @application = 'theme' 
                                    or @application = 'shortcuts' 
                                    or @application = 'subsite' 
                                    or @application = 'terms' 
                                    or @application = 'atoz' 
                                    or @application = 'ds'
                                    or @class = 'cms-admin' 
                                    or @class = 'admin'">
                        <xsl:text>cms</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>site</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:apply-templates/>
        </html>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element of an extending template. The <code>extends</code>
                attribute is required and points to the template which is 
                extended by the layout. Only the <code>block</code> elements
                in the layout are processed. The master layout must contain 
                matching <code>insert-block</code> elements.
            </p>
            <p>
                Technically the master template is processed first and the 
                extending layout is passed as parameter. The master layout
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>#block</foundry:doc-see-also>
        <foundry:doc-see-also>#insert-block</foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="page-layout[./@extends]">
        <xsl:apply-templates select="document(./@extends)">
            <xsl:with-param name="extending-layout"
                            tunnel="yes"
                            select=".">
            </xsl:with-param>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc type="template-tag" section="user">
        <foundry:doc-desc>
            <p>
                The element is used in a master layout to insert a block
                from an extending template.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="insert-block">
        <xsl:param name="extending-layout" tunnel="yes"/>
        
        <xsl:variable name="block-name" select="./@name"/>
        
        <xsl:apply-templates select="$extending-layout/block[./@name = $block-name]"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for generating a HTML fragment instead of a complete HTML document.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="fragment-layout">
        
        <xsl:apply-templates/>
        
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                This element allows it to include template fragments into a template. The element
                has two attributes. The <code>file</code> attribute is mandatory and contains 
                the path the fragment to include, relative to the <code>template</code> directory.
                The <code>internal</code> attribute is optional. If set to <code>true</code> the 
                fragment is loaded from the internal template directory.
            </p>
            <p>
                For example <code>&lt;include file="fragments/footer.xml"&lt;</code> will include
                the file <code>templates/fragments/footer.xml</code>. If the <code>internal</code>
                attribute is set to <code>true</code> the file 
                <code>foundry/templates/fragments/footer.xml</code> would be included.
            </p>
            <p>
                An fragment template file included using this element using this element must 
                contain a <code>fragment-layout</code> element as root. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="file">
                <p>
                    Path of the file to include, relative to the <code>templates</code> directory.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="internal">
                <p>
                    If set to <code>true</code> the template fragment file is loaded from the 
                    internal template directory.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="include">
        
        <xsl:variable name="origin" as="xs:string" 
                      select="foundry:get-attribute-value(current(), 'origin', '')"/>
        
        <xsl:apply-templates select="document(foundry:gen-path(concat('templates/', 
                                                                     ./@file),
                                                               $origin))/fragment-layout"/>
        
    </xsl:template>
    
    
    <!-- 
        ========================================================
        Common helper templates/functions for all templates tags
    -->

    <foundry:doc section="devel" type="function-template">
        <foundry:doc-param name="current-layout-node">
            <p>
                The layout node to use. Defaults the the current node.
            </p>
        </foundry:doc-param>
        <foundry:doc-param name="attributes">
            <p>
                The attributes to copy separated by an empty space. For example: 
                <code>autofocus disabled form formaction formenctype formmethod formnovalidate formtarget name type value</code>.
            </p>
        </foundry:doc-param>
        <foundry:doc-desc>
            <p>
                A helper template for copying attributes from the layout tree to the result tree.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:copy-attributes">
        <xsl:param name="current-layout-node" select="current()"/>
        <xsl:param name="attributes" select="''"/>
        
        <xsl:for-each select="tokenize($attributes, ' ')">
            <xsl:copy-of select="$current-layout-node/@*[name() = current()]"/>
        </xsl:for-each>
    </xsl:template>

    <foundry:doc section="devel" type="function-template">
        <foundry:doc-param name="current-layout-node">
            <p>
                The layout node to use. Defaults the the current node.
            </p>
        </foundry:doc-param>
        <foundry:doc-desc>
            <p>
                Helper template for copying <code>data-</code> attributes from the the layout XML
                to the HTML result tree.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>http://www.w3.org/TR/html5/dom.html#embedding-custom-non-visible-data-with-the-data-*-attributes</foundry:doc-see-also>
    </foundry:doc>
    <xsl:template name="foundry:copy-data-attributes">
        <xsl:param name="current-layout-node" select="current()"/>
        
        <xsl:copy-of select="$current-layout-node/@*[starts-with(name(), 'data-')]"/>
    </xsl:template>

    <foundry:doc section="devel" type="function">
        <foundry:doc-desc>
            <p>
                Variant of <code>foundry:gen-src-url</code> without the <code>parameters</code> string
                parameter.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:gen-src-url">
        <xsl:param name="src-raw" as="xs:string"/>
        
        <xsl:sequence select="foundry:gen-src-url($src-raw, '')"/>
    </xsl:function>

    <foundry:doc section="devel" type="function">
        <foundry:doc-params>
            <foundry:doc-param name="src-raw" mandatory="yes" type="string">
                <p>
                    The raw URL to process.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="parameters" mandatory="yes" type="string">
                <p>
                    Parameters to append to the URL.
                </p>
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-result type="string">
            <p>
                The processed URL.
            </p>
        </foundry:doc-result>
        <foundry:doc-desc>
            <p>
                Processes a given URL for use in the <code>src</code> attribute of an 
                <code>audio</code>, <code>img</code> or <code>video</code> element. The function 
                distigushes between this cases:
            </p>
            <dl>
                <dt>The URL starts with <code>http://</code> or <code>https://</code></dt>
                <dd>
                    In this case the URL is threated as an absolute URL pointing to a resource
                    outside of CCM. If any parameters are passed to the function they are appended
                    to the URL.
                </dd>
                <dt>The URL starts with a slash (<code>/</code>)</dt>
                <dd>
                    In this case the URL points to a resource managed by the CCM which also
                    manages the theme. In this case the URL is prefixed with the 
                    <code>dispatcher-prefix</code> and the parameters, if any, are appended.
                </dd>
                <dt>Other cases</dt>
                <dd>
                    If none of the two other cases match the URL points to a URL in the theme. In 
                    this case the URL is processed by the 
                    <a href="#gen-path">
                        <code>gen-path</code>
                    </a> function. The parameters, if any 
                    are appended.
                </dd>
            </dl>
            <p>
                If parameters are passed to this function they are appended to the URL. The 
                parameters are passed as string formatted as URL parameters, for example
                <code>foo=hello&amp;bar=world</code>. A leading <code>?</code> or <code>&amp;</code>
                is removed before adding the string the URL. If the URL already contains parameters
                (if the URL contains a <code>?</code>) the paramters string is added with a leading
                ampersand (<code>&amp;</code>). If not the parameters are appended using a 
                <code>?</code> character.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:gen-src-url">
        <xsl:param name="src-raw" as="xs:string"/>
        <xsl:param name="parameters" as="xs:string"/>
        
        <xsl:variable name="src-url">
            <xsl:choose>
                <xsl:when test="starts-with($src-raw, 'http://') 
                                or starts-with($src-raw, 'https://')">
                    <xsl:value-of select="$src-raw"/>
                </xsl:when>
                <xsl:when test="starts-with($src-raw, '/')">
                    <xsl:value-of select="concat($dispatcher-prefix, $src-raw)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="foundry:gen-path($src-raw)"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="parameters-string">
            <xsl:choose>
                <xsl:when test="starts-with($parameters, '?') or starts-with($parameters, '&amp;')">
                    <xsl:value-of select="substring($parameters, 1)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$parameters"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:choose>
            <xsl:when test="string-length($parameters-string) &gt; 0">
                <xsl:choose>
                    <xsl:when test="contains($src-url, '?')">
                        <xsl:sequence select="concat($src-url, '&amp;', $parameters-string)"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:sequence select="concat($src-url, '?', $parameters-string)"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="$src-url"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper functions for generating the name of the colorset class.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:get-colorset" as="xs:string">
        <xsl:for-each select="$data-tree/nav:categoryMenu/nav:category/nav:category">
            <xsl:if test="./@isSelected = 'true'">
                <xsl:value-of select="concat('colorset-', position())"/>
            </xsl:if>
        </xsl:for-each>
    </xsl:function>
    
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper functions for retrieving the name of the content type of the current content item
            from the result tree XML.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:function name="foundry:get-content-type-name" as="xs:string">
        <xsl:value-of select="$data-tree//cms:item/type/label"/>
    </xsl:function>
    
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper template for processing arrows/links for sorting items.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:move-buttons">
        <span class="move-button">
            <xsl:if test="@prevURL">
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="./@prevURL"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="'moveUp'"/>
                    </xsl:attribute>
                    <img>
                        <xsl:attribute name="src">
                            <xsl:value-of select="foundry:gen-path('images/gray-triangle-up.gif',
                                                                   'internal')"/>
                        </xsl:attribute>
                        <xsl:attribute name="title">
                            <xsl:value-of select="'moveUp'"/>
                        </xsl:attribute>
                    </img>
                </a>
            </xsl:if>
        </span>
        <span class="move-button">
            <xsl:if test="@nextURL">
                <a>
                    <xsl:attribute name="href">
                        <xsl:value-of select="./@nextURL"/>
                    </xsl:attribute>
                    <xsl:attribute name="title">
                        <xsl:value-of select="'moveDown'"/>
                    </xsl:attribute>
                    <img>
                        <xsl:attribute name="src">
                            <xsl:value-of select="foundry:gen-path('images/gray-triangle-down.gif',
                                                                   'internal')"/>
                        </xsl:attribute>
                        <xsl:attribute name="title">
                            <xsl:value-of select="'moveDown'"/>
                        </xsl:attribute>
                    </img>
                </a>
            </xsl:if>
        </span>
    </xsl:template>
    
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            Helper template for processing additional attributes in the data tree XML. They copied
            literally from the XML the HTML.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:process-datatree-attributes">
        <xsl:for-each select="@*">
            <xsl:if test="(name() != 'href_no_javascript')
                       and (name() != 'hint')
                       and (name() != 'label')">
                <xsl:attribute name="{name()}">
                    <xsl:value-of select="."/>
                </xsl:attribute>
            </xsl:if>
        </xsl:for-each>
        <xsl:if test="name() = 'bebop:formWidget' and (not(./@id) and ./@name)">
            <xsl:attribute name="id">
                <xsl:value-of select="./@name"/>
            </xsl:attribute>
        </xsl:if>
         <xsl:if test="name() = 'bebop:form' and (not(./@id) and ./@name)">
            <xsl:attribute name="id">
                <xsl:value-of select="./@name"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="devel" type="function-template">
        <foundry:doc-param name="current-layout-node">
            <p>
                The layout node to process. Default the the current node.
            </p>
        </foundry:doc-param>
        <foundry:doc-param name="attributes">
            <p>
                Additional attributes to copy from the layout tree. <code>data</code> attributes 
                (e.g.<code>data-toggle</code>) are
                copied automatically. Also <code>id</code> and <code>class</code> are already 
                processed by this template.
            </p>
        </foundry:doc-param>
        <foundry:doc-desc>
            <p>
                A convenient helper template which calls three other helper templates:
            </p>
            <ul>
                <li>
                    <a href="#set-id-and-class">
                        <code>foundry:set-id-and-class</code>
                    </a>
                </li>
                <li>
                    <a href="#copy-data-attributes">
                        <code>foundry:copy-data-attributes</code>
                    </a>
                </li>
                <li>
                    <a href="#copy-attributes">
                        <code>foundry:copy-attributes</code>
                    </a>
                </li>
                <li>
                    <a href="#process-title-attribute">
                        <code>foundry:process-title-attribute</code>
                    </a>
                </li>
            </ul>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:process-layouttree-attributes">
        <xsl:param name="current-layout-node" select="current()"/>
        <xsl:param name="copy-attributes" select="''"/>
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="title" select="''"/>
        
        <xsl:call-template name="foundry:set-id-and-class">
            <xsl:with-param name="current-layout-node" select="$current-layout-node"/>
            <xsl:with-param name="id" select="$id"/>
            <xsl:with-param name="class" select="$class"/>
        </xsl:call-template>
        
        <xsl:call-template name="foundry:copy-data-attributes">
            <xsl:with-param name="current-layout-node" select="$current-layout-node"/>
        </xsl:call-template>
        
        <xsl:call-template name="foundry:process-title-attribute">
            <xsl:with-param name="current-layout-node" select="$current-layout-node"/>
            <xsl:with-param name="title" select="$title"/>
        </xsl:call-template>
        
        <xsl:call-template name="foundry:copy-attributes">
            <xsl:with-param name="current-layout-node" select="$current-layout-node"/>
            <xsl:with-param name="attributes" select="$copy-attributes"/>
        </xsl:call-template>
    </xsl:template>

    <foundry:doc section="devel" type="function-template">
        <foundry:doc-param name="template-file"
                           mandantory="yes">
            The name of the template file to process.
        </foundry:doc-param>
        <foundry:doc-desc>
            This template is the entry point for the template parser.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template name="foundry:process-template">
        <xsl:param name="template-file" as="xs:string"/>
        <xsl:param name="origin" as="xs:string" select="''"/>

        <xsl:call-template name="foundry:message-debug">
            <xsl:with-param name="message"
                            select="concat('process-template called with template-file = ', 
                                           $template-file, 
                                           ' and origin = ', 
                                           $origin)"/>
        </xsl:call-template>
        
        <xsl:call-template name="foundry:message-debug">
            <xsl:with-param name="message"
                            select="concat('using template file: ', 
                                           foundry:gen-path(concat('templates/', 
                                                                   normalize-space($template-file)),
                                                                   $origin))" />
        </xsl:call-template>
        
        <xsl:apply-templates select="document(foundry:gen-path(
                                              concat('templates/', 
                                                     normalize-space($template-file)),
                                                     $origin))"/>
    </xsl:template>
    
    <xsl:template name="foundry:process-title-attribute">
        <xsl:param name="current-layout-node" select="current()"/>
        <xsl:param name="title" select="''"/>
         
        <xsl:if test="$title != '' or $current-layout-node/title-static">
            <xsl:attribute name="title">
                <xsl:choose>
                    <xsl:when test="$current-layout-node/title-static">
                        <xsl:value-of select="foundry:get-static-text('', ./@static-title)"/>
                    </xsl:when>
                    <xsl:when test="$title != ''">
                        <xsl:value-of select="$title"/>
                    </xsl:when>
                </xsl:choose>
            </xsl:attribute>
        </xsl:if>
         
    </xsl:template>

    <foundry:doc section="devel" type="function-template">
        <foundry:doc-desc>
            <p>
                Helper template for setting the <code>id</code> and <code>class</code> attributes
                on a HTML element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-params>
            <foundry:doc-param name="id">
                <p>
                    ID to set.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="class">
                <p>
                    (Additional) Classes to set.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="current-layout-node">
                <p>
                    Current node from the layout files.
                </p>
            </foundry:doc-param>
        </foundry:doc-params>
    </foundry:doc>
    <xsl:template name="foundry:set-id-and-class">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="current-layout-node" select="."/>
    
        <xsl:variable name="cond-class">
            <xsl:if test="$current-layout-node/@class-if">
                <!-- DE Funktioniert leider nicht in einer Zeile, daher die Hilfsvariable -->
                <xsl:variable name="key" 
                              select="substring-before($current-layout-node/@class-if, ',')"/>
                <xsl:variable name="condition">
                    <xsl:apply-templates select="//*[@id=$key]"/>
                </xsl:variable>
        
                <xsl:if test="normalize-space($condition)">
                    <xsl:value-of select="substring-after($current-layout-node/@class.if, ', ')"/>
                </xsl:if>
            </xsl:if>
        </xsl:variable>
    
        <xsl:variable name="type-class">
            <xsl:if test="$current-layout-node/@set-type-class='true'">
                <xsl:value-of select="foundry:get-content-type-name()"/>
            </xsl:if>
        </xsl:variable>
    
        <xsl:variable name="color-class">
            <xsl:if test="foundry:boolean($current-layout-node/@with-colorset)">
                <xsl:value-of select="foundry:get-colorset()"/>
            </xsl:if>
        </xsl:variable>
    
        <xsl:if test="$id != '' or $current-layout-node/@id">
            <xsl:attribute name="id">
                <xsl:choose>
                    <xsl:when test="$id != ''">
                        <xsl:value-of select="$id"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="$current-layout-node/@id"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
        </xsl:if>
    
        <xsl:if test="$current-layout-node/@class 
                      or $cond-class != '' 
                      or $type-class != '' 
                      or $color-class != ''
                      or $class != ''">
            <xsl:attribute name="class">
                <xsl:value-of select="normalize-space(concat($current-layout-node/@class, 
                                                             ' ', 
                                                             $class,
                                                             ' ',
                                                             $cond-class, 
                                                             ' ', 
                                                             $type-class, 
                                                             ' ', 
                                                             $color-class))"/>
            </xsl:attribute>
        </xsl:if>
    </xsl:template>

    <xsl:template name="foundry:format-date">
        <xsl:param name="date-elem"/>
        <xsl:param name="date-format"/>
        
        <xsl:choose>
            <xsl:when test="$date-format[@lang = $language]">
                <xsl:apply-templates select="$date-format[@lang = $language]">
                    <xsl:with-param name="date-elem" tunnel="yes" select="$date-elem"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="$date-format[@default = 'true']">
                    <xsl:with-param name="date-elem" tunnel="yes" select="$date-elem"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="date-format//short-date">
        <xsl:param name="date-elem" tunnel="yes"/>
        
        <xsl:value-of select="$date-elem/@date"/>
    </xsl:template>
    
    <xsl:template match="date-format//long-date">
        <xsl:param name="date-elem" tunnel="yes"/>
        
        <xsl:value-of select="$date-elem/@longDate"/>
    </xsl:template>

    <xsl:template match="date-format//iso-date">
        <xsl:param name="date-elem" tunnel="yes"/>
        
        <xsl:variable name="year" select="$date-elem/@year"/>
        <xsl:variable name="month" 
                      select="if (string-length($date-elem/@month) &lt; 2) 
                              then concat('0', $date-elem/@month) 
                              else $date-elem/@month"/>
        <xsl:variable name="day" 
                      select="if (string-length($date-elem/@day) &lt; 2)
                              then concat('0', $date-elem/@day) 
                              else $date-elem/@day"/>
        <!--<xsl:variable name="month-value" select="$date-elem/@month"/>
        <xsl:variable name="month">
            <xsl:choose>
                <xsl:when test="string-length($month-value) &lt; 2">
                    <xsl:value-of select="concat('0', $month-value)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$month-value"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>-->
        <!--<xsl:variable name="day-value" select="$date-elem/@day"/>
        <xsl:variable name="day">
            <xsl:choose>
                <xsl:when test="string-length($day-value) &lt; 2">
                    <xsl:value-of select="concat('0', $day-value)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$day-value"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>-->
        
        <xsl:value-of select="concat($year, '-', $month, '-', $day)"/>
    </xsl:template>
    
    <xsl:template match="date-format//year">
        <xsl:param name="date-elem" tunnel="yes"/>
        
        <xsl:value-of select="if (foundry:boolean(./@short))
                              then substring($date-elem/@year, 3)
                              else $date-elem/@year"/>
        
        <!--<xsl:variable name="year-value" select="$date-elem/@year"/>
        
        <xsl:choose>
            <xsl:when test="foundry:boolean(./@short)">
                <xsl:value-of select="substring($year-value, 3)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$year-value"/>
            </xsl:otherwise>
        </xsl:choose>-->
    </xsl:template>
    
    <xsl:template match="date-format//month">
        <xsl:param name="date-elem" tunnel="yes"/>
        
        <xsl:value-of select="if (string-length($date-elem/@month) &lt; 2 
                                  and foundry:boolean(./@zero))
                              then concat('0', $date-elem/@month)
                              else $date-elem/@month"/>
        
        <!--<xsl:variable name="month-value" select="$date-elem/@month"/>
        
        <xsl:choose>
            <xsl:when test="string-length($month-value) &lt; 2 and foundry:boolean(./@zero)">
                <xsl:value-of select="concat('0', $month-value)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$month-value"/>
            </xsl:otherwise>
        </xsl:choose>-->
    </xsl:template>

    <xsl:template match="date-format//month-name">
        <xsl:param name="date-elem" tunnel="yes"/>
        
        <xsl:value-of select="$date-elem/@monthName"/>
    </xsl:template>

    <xsl:template match="date-format//month-abbr">
        <xsl:param name="date-elem" tunnel="yes"/>
        
        <xsl:value-of select="substring($date-elem/@monthName, 0, 4)"/>
    </xsl:template>
    
    <xsl:template match="date-format//day">
        <xsl:param name="date-elem" tunnel="yes"/>
        
        <xsl:value-of select="if (string-length($date-elem/@day) &lt; 2 
                                  and foundry:boolean(./@zero))
                              then concat('0', $date-elem/@day)
                              else $date-elem/@day"/>
        
        <!--<xsl:variable name="day-value" select="$date-elem/@day"/>
        
        <xsl:choose>
            <xsl:when test="string-length($day-value) &lt; 2 and foundry:boolean(./@zero)">
                <xsl:value-of select="concat('0', $day-value)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$day-value"/>
            </xsl:otherwise>
        </xsl:choose>-->
    </xsl:template>
    
    <xsl:template name="foundry:format-time">
        <xsl:param name="time-elem"/>
        <xsl:param name="style-param"/>
            
        <xsl:variable name="style" 
                      select="if ($style-param[@lang = $language]) 
                              then $style-param[@lang = $language]
                              else $style-param[@default = 'true']"/>
        
        <!--<xsl:variable name="style">
            <xsl:choose>
                <xsl:when test="$style-param[@lang = $language]">
                    <xsl:value-of select="$style-param[@lang = $language]"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$style-param[@default = 'true']"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>-->
        
        <xsl:choose>
            <xsl:when test="$style = '12h'">
                <xsl:variable name="hour24-value" select="$time-elem/@hour"/>
                <xsl:variable name="minute-value" select="$time-elem/@minute"/>
                
                <xsl:variable name="hour-value">
                    <xsl:choose>
                        <xsl:when test="$hour24-value &gt; 11">
                            <xsl:value-of select="$hour24-value - 12"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$hour24-value"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <xsl:variable name="hour">
                    <xsl:choose>
                        <xsl:when test="string-length($hour-value) &lt; 2">
                            <xsl:value-of select="concat('0', $hour-value)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$hour-value"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <xsl:variable name="minute">
                    <xsl:choose>
                        <xsl:when test="string-length($minute-value) &lt; 2">
                            <xsl:value-of select="concat('0', $hour-value)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="$minute-value"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <xsl:variable name="suffix">
                    <xsl:choose>
                        <xsl:when test="hour-value &gt; 11">
                            <xsl:value-of select="foundry:get-static-text('', 'time/pm')"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="foundry:get-static-text('', 'time/am')"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:variable>
                
                <xsl:value-of select="concat($hour, ':', $minute, ' ', $suffix)"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$time-elem/@time"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
</xsl:stylesheet>