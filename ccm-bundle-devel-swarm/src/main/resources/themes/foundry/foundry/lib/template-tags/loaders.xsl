<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>
                      <!ENTITY quot '&#34;'>]>
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
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop foundry ui"
                version="2.0">
    
    <foundry:doc-file>
        <foundry:doc-file-title>Loaders</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                This tags are used to load resources required by the generated 
                HTML documents, for example CSS files and JavaScript files.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user"
                 type="template-tag">
        <foundry:doc-desc>
            <p>
                Invokes the foundry CSS loader. The CSS loader will parse the file 
                <code>conf/css-files.xml</code> to determine for which CSS an 
                <code>&lt;link&gt;</code> element should be added to the HTML output. For a full
                explanation please refer to the <a href="#css-files">CSS files section</a>.
            </p>
            <p>
                If you are using <a href="http://lesscss.org">LESS</a> for
                your stylesheets you can include the LESS JavaScript compiler to
                compile your LESS stylesheets on the fly to CSS. To do that
                you must first set the <code>less-onthefly</code> setting
                in <code>conf/global.xml</code> to <code>true</code>. 
                In the <code>conf/css-files.xml</code> each CSS file entry
                which is created from a LESS file must have a <code>less</code>
                attribute with its value set to <code>true</code>. Also for 
                these files the file extension (<code>.css</code>) should be 
                omitted.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="load-css-files">
        <xsl:variable name="application"
                      select="if ($data-tree/@application)
                              then $data-tree/@application
                              else 'none'"/>
        <!--<xsl:variable name="application">
            <xsl:choose>
                <xsl:when test="$data-tree/@application">
                    <xsl:value-of select="$data-tree/@application"/>
                </xsl:when>
                <xsl:when test="$data-tree/@class">
                    <xsl:value-of select="$data-tree/@class"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'none'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>-->
        
        <xsl:variable name="class"
                      select="if ($data-tree/@class)
                              then $data-tree/@class
                              else 'none'"/>
        
        <xsl:variable name="css-files-map"
                      select="document(foundry:gen-path('conf/css-files.xml'))"/>
        
        <xsl:variable name="less-onthefly" 
                      as="xs:boolean"
                      select="foundry:boolean(foundry:get-setting('', 
                                                                  'less-onthefly', 
                                                                  'false'))"/>
        
        <xsl:choose>
            <xsl:when test="$css-files-map/css-files/application[@name = $application and @class = $class]">
                <xsl:for-each select="$css-files-map/css-files/application[@name = $application and @class]/css-file">
                    <xsl:call-template name="foundry:load-css-file">
                        <xsl:with-param name="filename" select="."/>
                        <xsl:with-param name="media" select="./@media"/>
                        <xsl:with-param name="origin" 
                                        select="foundry:get-attribute-value(current(), 'origin', '')"/>
                        <xsl:with-param name="less" select="foundry:boolean(./@less)"/>
                        <xsl:with-param name="less-onthefly" select="$less-onthefly"/>
                        <xsl:with-param name="alternate" select="foundry:boolean(./@alternate)"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <xsl:when test="$css-files-map/css-files/application[@name = $application and not(@class)]">
                <xsl:for-each select="$css-files-map/css-files/application[@name = $application and not(@class)]/css-file">
                    <xsl:call-template name="foundry:load-css-file">
                        <xsl:with-param name="filename" select="."/>
                        <xsl:with-param name="media" select="./@media"/>
                        <xsl:with-param name="origin" 
                                        select="foundry:get-attribute-value(current(), 'origin', '')"/>
                        <xsl:with-param name="less" select="foundry:boolean(./@less)"/>
                        <xsl:with-param name="less-onthefly" select="$less-onthefly"/>
                        <xsl:with-param name="alternate" select="foundry:boolean(./@alternate)"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:for-each select="$css-files-map/css-files/default/css-file">
                    <xsl:call-template name="foundry:load-css-file">
                        <xsl:with-param name="filename" select="."/>
                        <xsl:with-param name="media" select="./@media"/>
                        <xsl:with-param name="origin" 
                                        select="foundry:get-attribute-value(current(), 'origin', '')"/>
                        <xsl:with-param name="less" select="foundry:boolean(./@less)"/>
                        <xsl:with-param name="less-onthefly" select="$less-onthefly"/>
                        <xsl:with-param name="alternate" select="foundry:boolean(./@alternate)"/>
                        <xsl:with-param name="title" select="./@title"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>
        
        <!-- Include IE Hacks only for very old IEs (IE 6) -->
        <!-- jensp 2014-09-16 This is copied from Mandalay. Maybe remove and relay and use 
        conditional comments in the other CSS files instead? -->
        
        <xsl:value-of select="'&#x0A;&#x3c;!--[if lt IE 9]&#x3e;&#x0A;'" 
                      disable-output-escaping="yes"/>
        <xsl:choose>
            <xsl:when test="$css-files-map/css-files/application[@name=$application]">
                <xsl:for-each select="$css-files-map/css-files/application[@name=$application]/iehacks">
                    <xsl:call-template name="foundry:load-css-file">
                        <xsl:with-param name="filename" select="."/>
                        <xsl:with-param name="media" select="./@media"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <xsl:for-each select="$css-files-map/css-files/default/iehacks">
                    <xsl:call-template name="foundry:load-css-file">
                        <xsl:with-param name="filename" select="."/>
                        <xsl:with-param name="media" select="./@media"/>
                    </xsl:call-template>
                </xsl:for-each>
            </xsl:otherwise>
        </xsl:choose>
        <xsl:value-of select="'&#x0A;&#x3c;![endif]--&#x3e;&#x0A;'" 
                      disable-output-escaping="yes"/>
        
        <xsl:if test="$less-onthefly and foundry:debug-enabled()">
            <script type="text/javascript">
                less = {
                env: "development",
                dumpLineNumbers: "all"
                    
                };
            </script>
            <script type="text/javascript" 
                    src="{foundry:gen-path('scripts/less.min.js', 'internal')}"/>
        </xsl:if>
        
    </xsl:template>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            A helper template for generating the 
            <code>&lt;link rel="stylesheet" href="..."/&gt; </code> elements for loading the CSS 
            files. 
        </foundry:doc-desc>
        <foundry:doc-param name="filename" mandatory="yes">
            <p>
                The name of the CSS file to load. If the filename contains slashes the filename
                is used as it is provided. If there are not slashes in the filename the filename
                is prefixed with <code>styles/</code>.
            </p>
        </foundry:doc-param>
        <foundry:doc-param name="media" mandatory="no">
            <p>
                The media for which the file should be loaded. If no set, the CSS file is used for all
                media types.
            </p>
        </foundry:doc-param>
        <foundry:doc-param name="origin" mandatory="no">
            <p>
                The origin of the CSS file. If not set or the parameter is empty, the CSS file
                is loaded from current theme. There also some values with 
                a special meaning:
            </p>
            <dl>
                <dt>
                    <code>master</code>
                </dt>
                <dd>
                    File is loaded from the master/parent theme. 
                    Please read the section about parent and child themes for more details.
                </dd>
                <dt>
                    <code>internal</code>
                </dt>
                <dd>
                    The file is loaded from the internal directories
                    If the current theme is a child theme, the file is loaded
                    from the internal directories of the parent theme.
                </dd>
            </dl>
            <p>
                Some examples:
            </p>
            <dl>
                <dt>
                    <code>&lt;css-file&gt;public.css&lt;/css-file&gt;</code>
                </dt>
                <dd>
                    The CSS file <code>public.css</code> is loaded from <code>styles</code> 
                    directory in the current theme.
                </dd>
                <dt>
                    <code>&lt;css-file&gt;bootstrap/css/bootstrap.min.css&lt;/css-file&gt;</code>
                </dt>
                <dd>
                    The CSS file <code>bootstrap.min.css</code> is loaded from the directory 
                    <code>bootstrap/css</code> in the current theme.
                </dd>
                <dt>
                    <code>&lt;css-file origin=&quot;internal&quot;&gt;admin.css&lt;/code&gt;</code>
                </dt>
                <dd>
                    If the current theme is a master theme, the CSS file <code>admin.css</code> is 
                    loaded from the directory <code>foundry/styles</code> in the current theme. If
                    the current theme is child theme the CSS file <code>admin.css</code> is loaded
                    from the directory <code>foundry/styles</code> of the Foundry theme installed
                    at <code>/themes/foundry</code>.
                </dd>
            </dl>
        </foundry:doc-param>
    </foundry:doc>
    <xsl:template name="foundry:load-css-file">
        <xsl:param name="filename"/>
        <xsl:param name="media" select="''"/>
        <xsl:param name="origin" select="''"/>
        <xsl:param name="less" as="xs:boolean" select="false()"/>
        <xsl:param name="less-onthefly" as="xs:boolean" select="false()"/>
        <xsl:param name="alternate" as="xs:boolean" select="false()"/>
        <xsl:param name="title" select="''"/>
        
        <xsl:choose>
            <xsl:when test="contains($filename, '/')">
                <xsl:choose>                   
                    <xsl:when test="string-length($media) &gt; 0">
                        <link rel="{foundry:gen-style-rel-value($less, 
                                                                $less-onthefly,
                                                                $alternate)}"
                              type="text/css"
                              href="{foundry:gen-path(foundry:gen-style-filename($less,
                                                                                 $less-onthefly,
                                                                                 $filename),
                                                      $origin)}"
                              media="{$media}">
                            <xsl:if test="$alternate">
                                <xsl:attribute name="title" 
                                               select="foundry:get-static-text('', $title)"/>
                            </xsl:if>
                        </link>
                    </xsl:when>
                    <xsl:otherwise>
                        <link rel="{foundry:gen-style-rel-value($less, 
                                                                $less-onthefly,
                                                                $alternate)}"
                              type="text/css"
                              href="{foundry:gen-path(foundry:gen-style-filename($less,
                                                                                 $less-onthefly,
                                                                                 $filename), 
                                                      $origin)}">
                            <xsl:if test="$alternate">
                                <xsl:attribute name="title" 
                                               select="foundry:get-static-text('', $title)"/>
                            </xsl:if>
                        </link>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="string-length($media) &gt; 0">
                        <link rel="{foundry:gen-style-rel-value($less, 
                                                                $less-onthefly,
                                                                $alternate)}" 
                              type="text/css" 
                              href="{foundry:gen-path(concat('styles/', 
                                                             $media, 
                                                            '/', 
                                                            foundry:gen-style-filename($less,
                                                                                       $less-onthefly,
                                                                                       $filename)), 
                                                            $origin)}" 
                              media="{$media}" >
                            <xsl:if test="$alternate">
                                <xsl:attribute name="title" 
                                               select="foundry:get-static-text('', $title)"/>
                            </xsl:if>
                        </link>
                    </xsl:when>
                    <xsl:otherwise>
                        <link rel="{foundry:gen-style-rel-value($less, 
                                                                $less-onthefly,
                                                                $alternate)}" 
                              type="text/css" 
                              href="{foundry:gen-path(concat('styles/', 
                                                             foundry:gen-style-filename($less,
                                                                                       $less-onthefly,
                                                                                       $filename)), 
                                                             $origin)}" >
                            <xsl:if test="$alternate">
                                <xsl:attribute name="title" 
                                               select="foundry:get-static-text('', $title)"/>
                            </xsl:if>
                        </link>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:function name="foundry:gen-style-rel-value">
        <xsl:param name="less" as="xs:boolean"/>
        <xsl:param name="less-onthefly" as="xs:boolean"/>
        <xsl:param name="alternate" as="xs:boolean"/>
        
        <xsl:choose>
            <xsl:when test="$less and $less-onthefly and foundry:debug-enabled() and $alternate">
                <xsl:value-of select="'alternate stylesheet/less'"/>
            </xsl:when>
            <xsl:when test="$less and $less-onthefly and foundry:debug-enabled()">
                <xsl:value-of select="'stylesheet/less'"/>
            </xsl:when>
            <xsl:when test="$alternate">
                <xsl:value-of select="'alternate stylesheet'"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="'stylesheet'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="foundry:gen-style-filename">
        <xsl:param name="less" as="xs:boolean"/>
        <xsl:param name="less-onthefly" as="xs:boolean"/>
        <xsl:param name="filename" as="xs:string"/>
        
        <xsl:choose>
            <xsl:when test="$less = true()">
                <xsl:choose>
                    <xsl:when test="($less = true()) 
                             and ($less-onthefly = true()) 
                             and (foundry:debug-enabled() = true())">
                        <xsl:value-of select="concat($filename, '.less')"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="concat($filename, '.css')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$filename"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                This tag can be used to load the 
                <a href="http://fancyapps.com/fancybox/">Fancybox</a> 
                gallery component. The tag includes all JavaScript files
                and CSS files needed by Fancybox.
            </p>
            <p>
                <em>Fancybox</em> is based on jQuery, therefore it is necessary
                to include jQuery also.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="load-fancybox">
        <!-- Add mousewheel plugin (this is optional) -->
        <script type="text/javascript" 
                src="{$context-prefix}/assets/fancybox2/lib/jquery.mousewheel-3.0.6.pack.js"/>
        
        <!-- Add fancyBox main JS and CSS files -->
        <script type="text/javascript" 
                src="{$context-prefix}/assets/fancybox2/source/jquery.fancybox.js"/>
        <link rel="stylesheet" 
              href="{$context-prefix}/assets/fancybox2/source/jquery.fancybox.css" 
              type="text/css" 
              media="screen"/>

        <!-- Add Button helper (this is optional) -->
        <link rel="stylesheet" 
              type="text/css" 
              href="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-buttons.css" />
        <script type="text/javascript"
                src="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-buttons.js"/>

        <!-- Add Thumbnail helper (this is optional) -->
        <link rel="stylesheet" 
              type="text/css" 
              href="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-thumbs.css" />
        <script type="text/javascript" 
                src="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-thumbs.js"/>

        <!-- Add Media helper (this is optional) -->
        <script type="text/javascript"
                src="{$context-prefix}/assets/fancybox2/source/helpers/jquery.fancybox-media.js"/>
        
        <!-- Apply fancybox -->
        <script type="text/javascript"
                src="{$theme-prefix}/scripts/apply-fancybox.js"/>
        
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                This tag is used to include a Favicon. It is stricly recommended
                to use this tag to include a Favicon instead of doing it 
                manually because this tag generates the correct path 
                automatically, based on the path of the theme.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="file">
                <p>
                    The name of the Favicon file relative to the theme 
                    directory.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="load-favicon">
        <link rel="shortcut icon" 
              type="image/x-icon" 
              href="{foundry:gen-path(./@file)}" />
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Loads the <a href="http://jquery.com">jQuery</a> JavaScript library provided by CCM.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="load-jquery">
        <script type="text/javascript" src="{$context-prefix}/assets/jquery.js"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Loads the <a href="http://jqueryui.com">jQuery UI</a> JavaScript library provided 
                by CCM.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="load-jquery-ui">
        <script type="text/javascript" src="{$context-prefix}/assets/jquery-ui.min.js"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Loads the <a href="http://www.mathjax.org/">MathJAX JavaScript</a> library which can
                render mathematical formulas written in MathML or LaTeX syntax.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="load-mathjax">
        <script type="text/javascript" 
                src="{$context-prefix}/assets/mathjax/MathJax.js?config=TeX-MML-AM_HTMLorMML"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Loads the <a href="https://github.com/aFarkas/html5shiv">html5shiv</a> JavaScript 
                library which fixes a bug of old Internet Explorers 
                (up to version 8) with elements unknown by the Internet Explorer. You need this
                library if you want to use HTML 5 elements like <code>article</code> or 
                <code>nav</code> in your templates. All other browser thread unknown elements
                like <code>div</code> or <code>span</code>. The Internet Explorer to version 8
                however adds a closing elements to the DOM tree directly after the unknown opening
                element, effectively removing the element from the DOM. The <em>html5shiv</em>
                library fixes the DOM tree using JavaScript.
            </p>
            <p>
                This tag adds a 
                <a href="http://en.wikipedia.org/wiki/Conditional_comment">conditional comment</a> 
                to load the html5shiv library only for old Internet Explorers
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="load-html5shiv">
        <xsl:value-of disable-output-escaping="yes"
                      select="concat('&#x0a;
                        &lt;!--[if lt IE 9]&gt;&#x0a;
                        &lt;script src=&quot;', $context-prefix, '/assets/html5shiv.js&quot;/&gt;&#x0a;
                        &lt;![endif]--&gt;')"/>
    </xsl:template>
    
</xsl:stylesheet>
