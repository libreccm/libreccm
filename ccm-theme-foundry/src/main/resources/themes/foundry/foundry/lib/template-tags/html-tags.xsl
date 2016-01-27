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
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl xs bebop foundry nav ui"
                version="2.0">

    <foundry:doc-file>
        <foundry:doc-file-title>HTML tags</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                These tags are generating the equivalent HTML tags. In most cases the tags have
                the same name and same attributes as their HTML counterparts, but some work
                in a slightly different way, for example by using values provided by other 
                surrounding tags which are passed to them as XSL parameters.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a HTML <code>a</code> element. There are some differences to the 
                <code>a</code> element in HTML. First, there two attribute for the URL:
            </p>
            <dl>
                <dt>
                    <code>href-property</code>
                </dt>
                <dd>
                    The name of a property of the current object which contains the URL for the 
                    link.
                </dd>
                <dt>
                    <dt>
                        <code>href-static</code>
                    </dt>
                    <dd>
                        A static URL. 
                    </dd>
                </dt>
            </dl>
            <p>
                The third variant for providing an URL is to call the template with a href 
                parameter in the XSL.
            </p>
            <p>
                Values for some other attributes can also be passed to the this template as XSL 
                parameters:
            </p>
            <dl>
                <dt>
                    <code>hreflang</code>
                </dt>
                <dd>Language of the resource the link is pointing to.</dd>
                <dt>
                    <code>title</code>
                </dt>
                <dd>
                    Value for the <code>title</code> attribute of the link. Usally this should
                    a very brief description of the target of the link
                </dd>
                <dt>
                    <code>type</code>
                </dt>
                <dd>The media type of the link target.</dd>
                <dt>
                    <code>target</code>
                </dt>
                <dd>
                    The target window/frame for the link.
                </dd>
            </dl>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="download">
                <p>
                    Value for the HTML5 <code>download</code> attribute.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="href-property">
                <p>
                    The name of a property (aka the name of an XML element in the data-tree) 
                    containing the URL of the link.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="href">
                <p>
                    A static URL for the link.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="rel">
                <p>
                    The relationship of the linking document with the target document.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="title-static">
                <p>
                    A key which identifies the translated title in <code>lang/global.xml</code>.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="title">
                <p>
                    Static, not translated title of the link.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-a-element">
                Description of the <code>a</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="a">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="href" select="''" tunnel="yes"/>
        <xsl:param name="hreflang" select="''" tunnel="yes"/>
        <xsl:param name="title" select="''" tunnel="yes"/>
        <xsl:param name="type" select="''" tunnel="yes"/>
         
        <a>
            <xsl:if test="./@href-property">
                <xsl:attribute name="href">
                    <xsl:value-of select="$data-tree/*[name = ./@href-property]"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@href-static">
                <xsl:attribute name="href">
                    <xsl:choose>
                        <xsl:when test="starts-with(./@href-static, '/')">
                            <xsl:value-of select="concat($context-prefix, ./@href-static)"/>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:value-of select="./@href-static"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$href != ''">
                <xsl:attribute name="href">
                    <xsl:value-of select="$href"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length($hreflang) &gt; 0">
                <xsl:attribute name="hreflang">
                    <xsl:value-of select="$hreflang"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="string-length($type) &gt; 0">
                <xsl:attribute name="type">
                    <xsl:value-of select="$type"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:choose>
                <xsl:when test="string-length($title) &gt; 0">
                    <xsl:attribute name="title" select="$title"/>
                </xsl:when>
                <xsl:when test="./@title">
                    <xsl:attribute name="title" select="./title"/>
                </xsl:when>
                <xsl:when test="./@title-static">
                    <xsl:attribute name="title" 
                                   select="foundry:get-static-text('', ./@title-static)"/>
                </xsl:when>
            </xsl:choose>
            <xsl:if test="./@target">
                <xsl:attribute name="target" select="./@target"/>
            </xsl:if>
           
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" 
                                select="'download rel type'"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </a>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a HTML <code>abbr</code> element used to tag abbreviations.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-abbr-element">
                Description of the <code>abbr</code> element in the HTML 5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="abbr">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <abbr>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </abbr>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>address</code> element in the HTML output. 
                The <code>address</code> elements represents the contact information of the 
                responsible author of <code>article</code> or <code>body</code> it appears in.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-address-element">
                Description of the <code>address</code> element in the HTML5 specification
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="address">
        <xsl:param name="id" select="id"/>
        <xsl:param name="class" select="''"/>
        
        <address>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </address>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates the HTML5 <code>article</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-article-element">
                Description of the <code>article</code> element in the HTML5 specification
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="article">
        <xsl:param name="id" select="''"/>
        
        <article>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </article>
    </xsl:template>
    
    <foundry:doc  section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a HTML5 <code>aside</code> element. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-aside-element">
                Description of the <code>aside</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="aside">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <aside>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </aside>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a HTML5 <code>audio</code> element. The source URL of the audio file can
                be provided by a surrounding element, statically in the theme or by an property in 
                the data tree.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="src-static">
                <p>
                    A static URL for the source of the audio file
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="src-property">
                <p>
                    Name of property in the data tree containing the source URL.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/embedded-content-0.html#the-audio-element">
                Description of the  <code>audio</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="audio">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <!-- Source URL of the audio document provided by a surrounding tag -->
        <xsl:param name="src" tunnel="yes" as="xs:string" select="''"/>
        <xsl:param name="title" tunnel="yes" as="xs:string" select="''"/>
        
        <xsl:variable name="src-raw">
            <xsl:choose>
                <xsl:when test="$src != ''">
                    <xsl:value-of select="$src"/>
                </xsl:when>
                <xsl:when test="./@src-static">
                    <xsl:value-of select="./@src-static"/>
                </xsl:when>
                <xsl:when test="./@src-property">
                    <xsl:value-of select="$data-tree/*[name() = ./@src-property]"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <!--
            Generate src of the audio document. If the path is not an external path (a path starting
            with http:// or https://) add the theme-prefix or the dispatcher prefix. The
            dispatcher-prefix is added ifthe path does start with a slash (/). This indicates 
            that the audio file is served from the CCM database. If the path does *not* start with a
            slash the audio file is part of the theme and the theme-prefix is added.
        -->
        <xsl:variable name="audio-src" select="foundry:gen-src-url($src-raw)"/>
        
        <audio>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" 
                                select="'autoplay buffered controls loop muted played preload volume'"/>
                <xsl:with-param name="title" select="$title"/>
            </xsl:call-template>
            <xsl:attribute name="src" select="$audio-src"/>
            
            <xsl:apply-templates/>
        </audio>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a HTML <code>b</code> element. Use this element only with semantics 
                defined in the HTML5 specification. Originally, in the early incarnations of HTML, 
                this element marked text as bold. In HTML5 the semantics of this element as been 
                redefined. For more information please refer to the HTML5 specification.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-b-element">
                Description of the <code>b</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="b">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <b>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </b>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>blockquote</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-blockquote-element">
                Description of the <code>blockquote</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="blockquote">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <blockquote>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'cite'"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </blockquote>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates the HTML <code>body</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-body-element">
                Description of the <code>body</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="body">
        <body id="site-body">
            <xsl:call-template name="foundry:process-layouttree-attributes"/>

            <xsl:if test="foundry:debug-enabled()">
                <div id="foundry-debug-panel">
                    <div id="foundry-debug-panel-content">
                        <h1>Foundry Debug Panel</h1>
                        <div id="foundry-debug-panel-foundry-sys-info" 
                             class="foundry-debug-panel-box">
                            <h2>Foundry system information</h2>
                            <dl>
                                <dt>Version</dt>
                                <dd>
                                    <xsl:value-of select="$foundry-version"/>
                                </dd>
                                <dt>Theme mode</dt>
                                <dd>
                                    <xsl:value-of select="$theme-mode"/>
                                </dd>
                            </dl>
                        </div>
                        <div id="foundry-debug-panel-layout" class="foundry-debug-panel-box">
                            <xsl:variable name="app-layout-template-file" 
                                          select="foundry:get-app-layout-template(foundry:get-current-application(), 
                                                                                  foundry:get-current-application-class())"/>
                                
                            <h2>Layout Template</h2>
                            <dl>
                                <dt>Basic layout template</dt>
                                <dd>
                                    <xsl:choose>
                                        <xsl:when test="$app-layout-template-file = ''">
                                            <xsl:text>default-layout.xml</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:value-of select="$app-layout-template-file"/>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </dd>
                                <dt>Internal?</dt>
                                <dd>
                                    <xsl:choose>
                                        <xsl:when test="$app-layout-template-file = ''">
                                            <xsl:text>true</xsl:text>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:text>false</xsl:text>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </dd>
                            </dl>
                        </div>
                        <div id="foundry-debug-panel-env-vars"
                             class="foundry-debug-panel-box">
                            <h2>Server related environment variables</h2>
                            <dl>
                                <dt>theme-prefix</dt>
                                <dd>
                                    <xsl:value-of select="$theme-prefix"/>
                                </dd>
                                <dt>context-prefix</dt>
                                <dd>
                                    <xsl:value-of select="$context-prefix"/>
                                </dd>
                                <dt>dispatcher-prefix</dt>
                                <dd>
                                    <xsl:value-of select="$dispatcher-prefix"/>
                                </dd>
                                <dt>language</dt>
                                <dd>
                                    <xsl:value-of select="$language"/>
                                </dd>
                            </dl>
                        </div>
                    </div>
                </div>
            </xsl:if>

            <!--<span id="top"/>-->
            <!--<a href="#startcontent" accesskey="S" class="nav-hide">
                <xsl:attribute name="title"> 
                    <xsl:value-of select="foundry:get-static-text('', 'layout/page/skipnav/title')"/>
                </xsl:attribute>
                <xsl:value-of select="foundry:get-static-text('', 'layout/page/skipnav/link')"/>
            </a>-->

            <xsl:apply-templates/>
        </body>
    </xsl:template>

    <foundry:doc>
        <foundry:doc-desc>
            <p>
                Generates a <code>br</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-br-element">
                Description of the <code>br</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="br">
        <br/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a button element. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/forms.html#the-button-element">
                Description of the <code>button</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="button">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <button>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" 
                                select="'autofocus disabled form formaction formenctype formmethod formnovalidate formtarget name type value'"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </button>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>canvas</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/scripting-1.html#the-canvas-element">
                Description of the <code>canvas</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="canvas">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <canvas>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
            </xsl:call-template>
            <xsl:if test="./@width">
                <xsl:attribute name="width" select="./@width"/>
            </xsl:if>
            
            <xsl:if test="./@height">
                <xsl:attribute name="height" select="./@height"/>
            </xsl:if>
            
            <xsl:apply-templates/>
        </canvas>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>caption</code> element representing the caption of a table.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/tabular-data.html#the-caption-element">
                Description of the <code>caption</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="table//caption">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <caption>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </caption>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>cite</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-cite-element">
                Description of the <code>cite</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="cite">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <cite>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </cite>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>code</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-code-element">
                Description of the <code>code</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="code">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <code>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </code>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                A definition of term in a definition list.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-dd-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="dd">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <dd>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </dd>
    </xsl:template>
    
    <foundry:doc-desc>
        <p>
            Generates a <code>del</code> element.
        </p>
    </foundry:doc-desc>
    <foundry:doc-see-also>
        <foundry:doc-link href="http://www.w3.org/TR/html5/edits.html#the-del-element">
            Description of the <code>del</code> element in the HTML5 specification.
        </foundry:doc-link>
    </foundry:doc-see-also>
    <xsl:template match="del">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <del>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </del>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>dfn</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-dfn-element">
                Description of the <code>dfn</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="dfn">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <dfn>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </dfn>
    </xsl:template>
    
    <foundry:doc section="user" 
                 type="template-tag">
        <foundry:doc-desc>
            Generates a HTML <code>div</code> element. 
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-div-element">
                Description of the <code>div</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="div">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <div>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </div>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML <code>div</code> element, but only if the content is not empty. 
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#div"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="divIfNotEmpty">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <xsl:variable name="div-content">
            <xsl:apply-templates/>
        </xsl:variable>

        <xsl:if test="normalize-space($div-content)">
            <div>
                <xsl:call-template name="foundry:set-id-and-class">
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="class" select="$class"/>
                </xsl:call-template>
                <xsl:apply-templates/>
            </div>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a definition list.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-dl-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="dl">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <dl>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </dl>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                A term in a definition list.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-dt-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="dt">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <dt>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </dt>
    </xsl:template>
    
    <foundry:doc>
        <foundry:doc-desc>
            <p>
                Generates an <code>em</code> element.
            </p> 
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-em-element">
                Description of the <code>em</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="em">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <em>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </em>
    </xsl:template>
    
    <xsl:template match="fieldset">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <fieldset>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'disabled form name'"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </fieldset>
    </xsl:template>
    
    <xsl:template match="figcaption">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <figcaption>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </figcaption>
    </xsl:template>
    
    <xsl:template match="figure">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <figure>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </figure>
    </xsl:template>
        
    <foundry:doc section="user"  type="template-tag">
        <foundry:doc-desc>
            Creates a HTML5 footer element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-footer-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="footer">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <footer>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </footer>
    </xsl:template>
    
       
    <xsl:template match="form">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="action" tunnel="yes" select="''"/>
        
        <form>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'method enctype accept name'"/>
            </xsl:call-template>
            
            <xsl:attribute name="action">
                <xsl:choose>
                    <xsl:when test="$action != ''">
                        <xsl:value-of select="$action"/>
                    </xsl:when>
                    <xsl:when test="./@action">
                        <xsl:value-of select="./@action"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="''"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            
            <xsl:apply-templates/>
        </form>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>h1</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-h1,-h2,-h3,-h4,-h5,-and-h6-elements">
                Description of the <code>h1</code>, <code>h2</code>, <code>h3</code>, 
                <code>h4</code>, <code>h5</code> and <code>h6</code> elements in the HTML5 
                specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="h1">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <h1>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </h1>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>h2</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-h1,-h2,-h3,-h4,-h5,-and-h6-elements">
                Description of the <code>h1</code>, <code>h2</code>, <code>h3</code>, 
                <code>h4</code>, <code>h5</code> and <code>h6</code> elements in the HTML5 
                specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="h2">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <h2>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </h2>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>h3</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-h1,-h2,-h3,-h4,-h5,-and-h6-elements">
                Description of the <code>h1</code>, <code>h2</code>, <code>h3</code>, 
                <code>h4</code>, <code>h5</code> and <code>h6</code> elements in the HTML5 
                specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="h3">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <h3>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </h3>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>h4</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-h1,-h2,-h3,-h4,-h5,-and-h6-elements">
                Description of the <code>h1</code>, <code>h2</code>, <code>h3</code>, 
                <code>h4</code>, <code>h5</code> and <code>h6</code> elements in the HTML5 
                specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="h4">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <h4>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </h4>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>h5</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-h1,-h2,-h3,-h4,-h5,-and-h6-elements">
                Description of the <code>h1</code>, <code>h2</code>, <code>h3</code>, 
                <code>h4</code>, <code>h5</code> and <code>h6</code> elements in the HTML5 
                specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="h5">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <h5>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </h5>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>h6</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-h1,-h2,-h3,-h4,-h5,-and-h6-elements">
                Description of the <code>h1</code>, <code>h2</code>, <code>h3</code>, 
                <code>h4</code>, <code>h5</code> and <code>h6</code> elements in the HTML5 
                specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="h6">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <h6>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </h6>
    </xsl:template>
    
    <foundry:doc section="user"  type="template-tag">
        <foundry:doc-desc>
            Creates the HTML <code>head</code> element which may contain meta data and stylesheets
            etc. It also generates some meta data like the generator meta information or the 
            language meta information.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/document-metadata.html#the-head-element">
                Description of the <code>head</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="head">
        <head>
            <meta name="generator">
                <xsl:attribute name="content">
                    <xsl:value-of select="concat($data-tree/bebop:systemInformation/@appname, 
                                                 ' ', 
                                                 $data-tree/bebop:systemInformation/@version)"/>
                </xsl:attribute>
            </meta>
            
            <meta http-equiv="content-language" content="{$language}"/>
            <!-- These meta informations are needed to get Level 3 WAI -->
            <!-- ToDo
            <meta name="keywords">
                <xsl:attribute name="content">
                    <xsl:call-template name="foundry:keywords"/>
                </xsl:attribute>
            </meta>-->
            <xsl:if test="$data-tree/nav:categoryMenu//nav:category[@isSelected = 'true' and position() = last()]/@description != ''">
                <meta name="description">
                    <xsl:attribute name="content" 
                                   select="$data-tree/nav:categoryMenu//nav:category[@isSelected = 'true' and position() = last()]/@description">
                    
                    </xsl:attribute>
                </meta>
            </xsl:if>
      
            <xsl:apply-templates/>
            
            <!-- Load the CSS files for Foundry's debug mode if debug mode is active -->
            <xsl:if test="foundry:debug-enabled()">
                <link rel="stylesheet" 
                      type="text/css" 
                      href="{foundry:gen-path('styles/debug-mode.css', 'internal')}"/>
            </xsl:if>
            
            <!-- Not implemented yet <xsl:call-template name="bebop:double-click-protection"/> -->
      
            <!-- 
                Set favicon if exists. This three different variants for including the favicon
                are necessary to satisfy all browsers.
            -->
            <link href="{$theme-prefix}/images/favicon.ico" 
                  type="image/x-icon" 
                  rel="shortcut icon"/>
            
            <xsl:apply-templates select="$data-tree//script"/>
        </head>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML5 <code>header</code> element. 
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-header-element">
                Description of the <code>header</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="header">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <header>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </header>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>hr</code> element. This element has no content.  
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-hr-element">
                Description of the <code>hr</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="hr">
        <hr>
            <xsl:call-template name="foundry:process-layouttree-attributes"/>
        </hr>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates an <code>i</code> element. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-i-element">
                Description of the <code>i</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="i">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <i>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </i>
    </xsl:template>

    <foundry:doc>
        <foundry:doc-desc>
            <p>
                The <code>img</code> tag produces an HTML <code>img</code> in the HTML output.
                The source URL of the image can either be provided using the attributes 
                <code>src-static</code> or <code>src-property</code> or by a surrounding tag
                like <code>image-attachment</code>. If the image URL is provided by an surrouding 
                tag these tag usally provides values with the orginal width and height of the image.
            </p>
            <p>
                Depending of the format for URL, the URL is modified. There are three possible 
                cases:
            </p>
            <ol>
                <li>
                    The URL starts with <code>http://</code> or <code>https://</code>. 
                    In this case the URL is used literally.
                </li>
                <li>
                    The URL starts with a slash (<code>/</code>). In this case the URL points to
                    an image provided by CCM (from the database). In this case, the 
                    <code>dispatcher-prefix</code> is appended before the URL. Also width and 
                    height parameters are appended to the URL for CCM server side resizing 
                    function.
                </li>
                <li>
                    Otherwise is is assumed that the image is provided by the theme. In this
                    cases the <code>theme-prefix</code> is added before the image URL. 
                </li>
            </ol>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="src-static">
                <p>
                    An URL to an static image resource. 
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="src-property">
                <p>
                    Name of an XML node in the <code>data-tree</code> providing the URL of the 
                    image.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="width">
                <p>
                    The (maximum) width of the image.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="height">
                <p>
                    The (maximum) height of the image.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/embedded-content-0.html#the-img-element">
                Description of the <code>img</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="img">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <!-- Source URL of the image provided by a surrounding tag. -->
        <xsl:param name="src" tunnel="yes" as="xs:string" select="''"/>
        <!-- Width of the image the URL the src parameter is pointing to (pixel) -->
        <xsl:param name="img-width" tunnel="yes" as="xs:integer" select="(-1)"/>
        <!-- Height of the image the URL the src parameter is pointing to (pixel) -->
        <xsl:param name="img-height" tunnel="yes" as="xs:integer" select="(-1)"/>
        <!-- Content of the alt attribute if provided by surrounding tag -->
        <xsl:param name="alt" tunnel="yes" as="xs:string" select="''"/>
        <xsl:param name="title" tunnel="yes" as="xs:string" select="''"/>
        
        <xsl:variable name="src-raw">
            <xsl:choose>
                <xsl:when test="$src != ''">
                    <xsl:value-of select="$src"/>
                </xsl:when>
                <xsl:when test="./@src-static">
                    <xsl:value-of select="./@src-static"/>
                </xsl:when>
                <xsl:when test="./@src-property">
                    <xsl:value-of select="$data-tree/*[name() = ./@src-property]"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <!-- 
            Value of width attribute of the img element in the layout template if set. 
            If there is no width attribute the value is set to -1.
        -->
        <xsl:variable name="max-width" 
                      as="xs:integer" 
                      select="foundry:get-attribute-value(current(), 'width', -1)"/>
        
        <!--
            Value of the height attribute of the img element in the layout template if set.
            If there is no attribute the value is set to -1.
        -->
        <xsl:variable name="max-height" 
                      as="xs:integer"
                      select="foundry:get-attribute-value(current(), 'height', -1)"/>
        
        <xsl:variable name="width">
            <xsl:choose>
                <xsl:when test="$max-width &gt; 0 
                                and $max-height &gt; 0 
                                and $img-width &gt; $max-width 
                                and $img-height &gt; $max-height ">
                    <xsl:choose>
                        <xsl:when test="$max-width div $img-width &gt; $max-height div $img-height">
                            <xsl:value-of select="round($max-height div $img-height * $img-width)"/>
                        </xsl:when>
                        <xsl:when test="$max-height div $img-height &gt;= $max-width div $img-width">
                            <xsl:value-of select="round($max-width)"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="$max-width &gt; 0 and $img-width &gt; $max-width">
                    <xsl:value-of select="$max-width"/>
                </xsl:when>
                <xsl:when test="$max-height &gt; 0 and $img-height &gt; $max-height" >
                    <xsl:value-of select="round($max-height div $img-height * $img-width)"/>
                </xsl:when>
                <xsl:when test="$img-width &lt;= 0">
                    <xsl:value-of select="$max-width"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$img-width"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="height">
            <xsl:choose>
                <xsl:when test="$max-width &gt; 0 
                                and $max-height &gt; 0 
                                and $img-width &gt; $max-width 
                                and $img-height &gt; $max-height">
                    <xsl:choose>
                        <xsl:when test="$max-height div $img-height &gt; $max-width div $img-width">
                            <xsl:value-of select="round($max-width div width * height)"/>
                        </xsl:when>
                        <xsl:when test="$max-width div $img-width &gt;= $max-height div $img-height">
                            <xsl:value-of select="$max-height"/>
                        </xsl:when>
                    </xsl:choose>
                </xsl:when>
                <xsl:when test="$max-height &gt; 0 and $img-height &gt; $max-height">
                    <xsl:value-of select="$max-height"/>
                </xsl:when>
                <xsl:when test="$max-width &gt; 0 and $img-width &gt; $max-width">
                    <xsl:value-of select="round($max-width div $img-width * $img-height)"/>
                </xsl:when>
                <xsl:when test="$img-height &lt;= 0">
                    <xsl:value-of select="$max-height"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="$img-height"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
      
        <xsl:variable name="img-src" 
                      select="foundry:gen-src-url($src-raw, 
                                                  concat('width=', 
                                                         $width, 
                                                         '&amp;height=', 
                                                         $height))"/>
        
        <img>
            
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:attribute name="src" select="$img-src"/>
            
            <xsl:if test="$alt != ''">
                <xsl:attribute name="alt">
                    <xsl:value-of select="$alt"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@alt">
                <xsl:attribute name="alt">
                    <xsl:value-of select="foundry:get-static-text('', ./@alt, false())"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$title != ''">
                <xsl:attribute name="title">
                    <xsl:value-of select="$title"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="./@title">
                <xsl:attribute name="title">
                    <xsl:value-of select="foundry:get-static-text('', ./@title, false())"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$width &gt; 0">
                <xsl:attribute name="width">
                    <xsl:value-of select="$width"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="$height &gt; 0">
                <xsl:attribute name="height">
                    <xsl:value-of select="$height"/>
                </xsl:attribute>
            </xsl:if>
        </img>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>input</code> element. A preset value can be provided using a 
                XSL parameter by a surrounding element. The HTML5 <code>placeholder</code> attribute
                has been split into two attributes. The <code>placeholder-module</code> attribute
                contains the name of module parameter (the name of the file in the 
                <code>texts</code> directory) in which the text for the placeholder is stored. If 
                omitted the <code>global.xml</code> file is used. The placeholder attribute itself
                contains the ID of the text to show as placeholder. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-parameters>
            <foundry:doc-parameter name="value">
                <p>
                    The value of the input field.
                </p>
            </foundry:doc-parameter>
        </foundry:doc-parameters>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="placeholder-module">
                <p>
                    The name of the file in the <code>texts</code> directory which contains the 
                    text to use as placeholder. If omitted the <code>global.xml</code> file is used.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="placeholder">
                <p>
                    The ID of the text to use as placeholder.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/forms.html#the-input-element">
                Description of the <code>input</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="input">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="value" tunnel="yes" select="''"/>
        
        <input>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" 
                                select="'type accept autocomplete autofocus checked disabled multiple name required size spellcheck tabindex'"/>
            </xsl:call-template>
            <xsl:attribute name="value" select="if($value = '' and ./@value) 
                                                then ./@value
                                                else $value"/>
            
            <xsl:choose>
                <xsl:when test="./@placeholder and ./@placeholder-module">
                    <xsl:attribute name="placeholder" 
                                   select="foundry:get-static-text(./@placeholder-module, 
                                                                   ./@placeholder)"/>
                </xsl:when>
                <xsl:when test="./@placeholder and not(./@placeholder-module)">
                    <xsl:attribute name="placeholder" 
                                   select="foundry:get-static-text('', 
                                                                   ./@placeholder)"/>
                </xsl:when>
            </xsl:choose>
            
        </input>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>ins</code> element
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/edits.html#the-ins-element">
                Description of the <code>ins</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="ins">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <ins>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </ins>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>kbd</code> element
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-kbd-element">
                Description of the <code>kbd</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="kbd">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <kbd>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </kbd>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>label</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/forms.html#the-label-element">
                Description of the <code>label</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="label">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <label>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'for'"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </label>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>legend</code> element inside a form element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/forms.html#the-legend-element">
                Description of the <code>legend</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="legend">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <legend>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </legend>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>li</code> element inside an <code>ul</code> or <code>ol</code>.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-li-element">
                Description of the <code>li</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="li">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
       
        <li>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </li>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML5 <code>main</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-main-element">
                Description of the <code>main</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="main">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <main>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </main>
    </xsl:template>
    

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a meta data field in in the <code>head</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="#head"/>
            <foundry:doc-link href="http://www.w3.org/TR/html5/document-metadata.html#the-meta-element">
                Description of the <code>meta</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="meta">
        <meta>
            <xsl:if test="@name">
                <xsl:attribute name="name">
                    <xsl:value-of select="@name"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@http-equiv">
                <xsl:attribute name="http-equiv">
                    <xsl:value-of select="@http-equiv"/>
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@content">
                <xsl:attribute name="content">
                    <xsl:value-of select="@content"/>
                </xsl:attribute>
            </xsl:if>
        </meta>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>Generates a HTML5 <code>nav</code> element.</p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/sections.html#the-nav-element">
                Description of the <code>nav</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="nav">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <nav>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </nav>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>Generates a <code>noscript</code> element</p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/scripting-1.html#the-noscript-element">
                Description of the <code>noscript</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="noscript">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <noscript>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </noscript>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates an <code>ol</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-ol-element">
                Description of the <code>ol</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="ol">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <ol>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </ol>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates an <code>optgroup</code> element. The label for the option group must be
                provided by a surrounding element as XSL parameter.
            </p>
        </foundry:doc-desc>
        <foundry:doc-parameters>
            <foundry:doc-parameter name="disabled" type="boolean">
                <p>
                    Is the option group enclosed by the element disabled?
                </p>
            </foundry:doc-parameter>
            <foundry:doc-parameter name="label" type="string">
                <p>
                    The label of the option group.
                </p>
            </foundry:doc-parameter>
        </foundry:doc-parameters>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/forms.html#the-optgroup-element">
                Description oft the <code>optgroup</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="optgroup">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="disabled" as="xs:boolean" tunnel="yes" select="false()"/>
        <xsl:param name="label" as="xs:string" tunnel="yes" select="''"/>
        
        <optgroup>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:if test="$disabled = true()">
                <xsl:attribute name="disabled" select="'disabled'"/>
            </xsl:if>
            <xsl:if test="$label != ''">
                <xsl:attribute name="label" select="$label"/>
            </xsl:if>
            <xsl:apply-templates/>
        </optgroup>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>option</code> element for use in <code>select</code> box. Several
                values for attributes have to be provided by a surrounding element using XSL 
                parameters, for example the value of the <code>value</code> attribute.
            </p>
        </foundry:doc-desc>
        <foundry:doc-parameters>
            <foundry:doc-parameter name="disabled" type="boolean">
                <p>
                    Is the option group enclosed by the element disabled?
                </p>
            </foundry:doc-parameter>
            <foundry:doc-parameter name="label" type="string">
                <p>
                    The label of the option.
                </p>
            </foundry:doc-parameter>
            <foundry:doc-parameter name="selected">
                <p>
                    Is the option selected?
                </p>
            </foundry:doc-parameter>
            <foundry:doc-parameter name="value">
                <p>
                    The value of the option. This is value that is send to the server.
                </p>
            </foundry:doc-parameter>
        </foundry:doc-parameters>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/forms.html#the-option-element">
                Description of the <code>option</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="option">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="disabled" as="xs:boolean" tunnel="yes" select="false()"/>
        <xsl:param name="label" as="xs:string" tunnel="yes" select="''"/>
        <xsl:param name="selected" as="xs:boolean" tunnel="yes" select="false()"/>
        <xsl:param name="value" as="xs:string" tunnel="yes" select="''"/>
        
        <option>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:if test="$disabled = true()">
                <xsl:attribute name="disabled" select="'disabled'"/>
            </xsl:if>
            <xsl:if test="$label != ''">
                <xsl:attribute name="label" select="$label"/>
            </xsl:if>
            <xsl:if test="$selected = true()">
                <xsl:attribute name="selected" select="'selected'"/>
            </xsl:if>
            <xsl:if test="$value != ''">
                <xsl:attribute name="value" select="$value"/>
            </xsl:if>
            <xsl:apply-templates/>
        </option>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>p</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-p-element">
                Description of the <code>p</code> element in the HTML5 specification
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="p">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <p>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </p>
    </xsl:template>
    
    <xsl:template match="pIfNotEmpty">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <xsl:variable name="p-content">
            <xsl:apply-templates/>
        </xsl:variable>
        
        <xsl:if test="normalize-space($p-content)">
            <p>
                <xsl:call-template name="foundry:process-layouttree-attributes">
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="class" select="$class"/>
                </xsl:call-template>
                <xsl:apply-templates/>
            </p>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>pre</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-pre-element">
                Description of the <code>pre</code> in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="pre">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <pre>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </pre>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>q</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-q-element">
                Description of the <code>q</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="q">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <q>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </q>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>s</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-s-element">
                Description of the <code>s</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="s">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <s>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </s>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>samp</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-samp-element">
                Description of the <code>samp</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="samp">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <samp>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </samp>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-attributes>
            <foundry:doc-attribute name="origin" mandatory="no">
                <p>
                    As usual <code>origin</code> attribute determines the how the path provided in 
                    the <code>src</code> attribute is interpreted. The following values are 
                    interpreted. In addition to the common values <code>internal</code>, 
                    <code>master</code> and the default value the <code>script</code> element also
                    support the value <code>absolute</code>. If <code>origin</code> is set to 
                    absolute the provided source path is processed by Foundry and is used as it is
                    provided.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="src" mandatory="yes">
                <p>
                    The path of the script to include. If the <code>origin</code> attribute is not
                    set (or not set to <code>absolute</code> the path is interpreted relative to the 
                    theme directory. For example the path of a script included using 
                </p>
                <pre>
                &lt;script type="text/javascript" src="scripts/example.js"/>
                </pre>
                <p>
                    in the a theme named <code>my-theme</code> at the server 
                    <code>http://www.example.org</code> is altered to the absolute path 
                    <code>http://www.example.org/themes/published-themedir/itb/scripts/example.js</code>.
                    If the <code>absolute</code> attribute is set to <code>true</code> the path is not 
                    altered. One usecase for an absolute path is to load an script from a content delivery
                    network.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="type" mandatory="no">
                <p>
                    The type of the script. Usally this is <code>text/javascript</code>. If the 
                    attribute is not set in the layout template, it is automatically set to 
                    <code>text/javascript</code>.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-desc>
            <p>
                Used to include a script (usally a JavaScript). The script is either provided 
                a content of the element or as an external file. Embedded scripts should only be used
                for small parts of code, like the code for activating jQuery plugins for some elements.
                Everything which is longer than five or six lines should be put into a external file 
                in the scripts directory of the theme.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/scripting-1.html#the-script-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="script">
        <script>
            <xsl:attribute name="type">
                <xsl:choose>
                    <xsl:when test="./@type">
                        <xsl:value-of select="./@type"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl-value-of select="'text/javascript'"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:if test="./@src">
                <xsl:variable name="origin" 
                              select="foundry:get-attribute-value(current(), 'origin', '')"/>
                
                <xsl:attribute name="src"
                               select="if ($origin = 'absolute')
                                       then ./@src
                                       else foundry:gen-path(./@src, $origin)"/>
            </xsl:if>
            <xsl:if test="string-length(.)">
                <xsl:value-of select="."/>
            </xsl:if>
        </script>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a HTML5 <code>section</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-section-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="section">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <section>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </section>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a select box in a form. The <code>name</code> of the select box and 
                the status for the <code>disabled</code> attribute can be provided by a surrounding
                element via XSL parameters.
            </p>
        </foundry:doc-desc>
        <foundry:doc-paramters>
            <foundry:doc-parameter name="name">
                <p>
                    The name of the select box control.
                </p>
            </foundry:doc-parameter>
            <foundry:doc-parameter name="disabled">
                <p>
                    If set to <code>true</code> the select box is disabled.
                </p>
            </foundry:doc-parameter>
        </foundry:doc-paramters>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/forms.html#the-select-element">
                Description of the <code>select</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="select">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="name" as="xs:string" tunnel="yes" select="''"/>
        <xsl:param name="disabled" as="xs:boolean" tunnel="yes" select="false()"/>
        
        <select>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" 
                                select="'autofocus disabled multiple required size'"/>
            </xsl:call-template>
            <xsl:if test="$name != ''">
                <xsl:attribute name="name" select="$name"/>
            </xsl:if>
            <xsl:if test="$disabled = true()">
                <xsl:attribute name="disabled" select="'disabled'"/>
            </xsl:if>
            <xsl:apply-templates/>
        </select>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>small</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-small-element">
                Description of the small element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="small">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <small>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </small>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>source</code> element for use in <code>audio</code> and
                <code>video</code> elements. The source URL (value of the <code>src</code> 
                attribute) can either provided by a surrounding element as XSL parameter or
                via the <code>src-static</code> or <code>src-property</code> attribute.
            </p>
        </foundry:doc-desc>
        <foundry:doc-params>
            <foundry:doc-param name="src">
                <p>
                    Source URL provided by a surrounding element.
                </p>
            </foundry:doc-param>
            <foundry:doc-param name="type">
                <p>
                    Value of the <code>type</code> attribute.
                </p>
            </foundry:doc-param>
        </foundry:doc-params>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="src-static">
                <p>
                    An URL to an static resource. 
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="src-property">
                <p>
                    Name of an XML node in the <code>data-tree</code> providing the URL of the 
                    resource.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="source">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="src" as="xs:string" tunnel="yes" select="''"/>
        <xsl:param name="type" as="xs:string" tunnel="yes" select="''"/>
        
        <xsl:variable name="src-raw">
            <xsl:choose>
                <xsl:when test="$src != ''">
                    <xsl:value-of select="$src"/>
                </xsl:when>
                <xsl:when test="./@src-static">
                    <xsl:value-of select="./@src-static"/>
                </xsl:when>
                <xsl:when test="./@src-property">
                    <xsl:value-of select="$data-tree/*[name() = ./@src-property]"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="src-processed" select="foundry:gen-src-url($src-raw)"/>
        
        <source>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'type'"/>
            </xsl:call-template>
            <xsl:attribute name="src" select="$src-processed"/>
              
            <xsl:if test="$type != ''">
                <xsl:attribute name="type" select="$type"/>
            </xsl:if>
        </source>
        
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a <code>span</code> element.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-span-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="span">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <span>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </span>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            Generates a <code>span</code> element but only if the element has any content.
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-span-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="spanIfNotEmpty">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <xsl:variable name="span-content">
            <xsl:apply-templates/>
        </xsl:variable>
        
        <xsl:if test="normalize-space($span-content)">
            <span>
                <xsl:call-template name="foundry:process-layouttree-attributes">
                    <xsl:with-param name="id" select="$id"/>
                    <xsl:with-param name="class" select="$class"/>
                </xsl:call-template>
                <xsl:apply-templates/>
            </span>
        </xsl:if>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>strong</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-strong-element"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="strong">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <strong>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </strong>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates  a <code>sub</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-sub-and-sup-elements">
                Description of the <code>sub</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="sub">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <sub>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </sub>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates  a <code>sup</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-sub-and-sup-elements">
                Description of the <code>sup</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="sup">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <sup>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </sup>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>table</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/tabular-data.html#the-table-element">
                Description of the <code>table</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="table">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <table>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </table>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>tbody</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/tabular-data.html#the-tbody-element">
                Description of the <code>tbody</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="tbody">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <tbody>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </tbody>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>td</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/tabular-data.html#the-td-element">
                Description of the <code>td</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="td">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <td>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'colspan headers rowspan'"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </td>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>textarea</code>. The value may be provided as XSL parameter
                by a surrounding tag.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="placeholder-module">
                <p>
                    The name of the file in the <code>texts</code> directory which contains the 
                    text to use as placeholder. If omitted the <code>global.xml</code> file is used.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="placeholder">
                <p>
                    The ID of the text to use as placeholder.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-parameters>
            <foundry:doc-parameter name="value">
                <p>
                    The value of the textarea.
                </p>
            </foundry:doc-parameter>
        </foundry:doc-parameters>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/forms.html#the-textarea-element">
                Description of the <code>textarea</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="textarea">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="value" tunnel="yes" select="''"/>
        
        <textarea>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" 
                                select="'autocomplete autofocus cols disabled maxlength minlength multiple name readonly required rows wrap'"/>
            </xsl:call-template>
            <xsl:attribute name="value" select="$value"/>
            
            <xsl:if test="./@placeholder">
                <xsl:attribute name="placeholder" 
                               select="foundry:get-static-text(./@placeholder-module, 
                                                               ./@placeholder)"/>
            </xsl:if>
            <xsl:apply-templates/>
        </textarea>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>tfoot</code> element. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/tabular-data.html#the-tfoot-element">
                Description of the <code>tfoot</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="tfoot">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <tfoot>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </tfoot>
    </xsl:template>
        
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>th</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/tabular-data.html#the-th-element">
                Description of the <code>th</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="th">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <th>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'colspan headers rowspan scope'"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </th>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>thead</code> element. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/tabular-data.html#the-thead-element">
                Description of the <code>thead</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="thead">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <thead>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </thead>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>time</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-time-element">
                Description of the <code>time</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="time">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <time>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'datatime'"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </time>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates the title in the HTML head. The other elements are allowed in the 
                <code>&lt;title&gt;</code> tag: 
            </p>
            <ul>
                <li>
                    <code>show-text</code>
                </li>
                <li>
                    <code>show-page-title</code>
                </li>
            </ul>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/document-metadata.html#the-title-element"/>
            <foundry:doc-link href="#show-text"/>
            <foundry:doc-link href="#show-page-title"/>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="title">
        <title>
            <xsl:choose>
                <xsl:when test="./show-text | ./show-page-title">
                    <xsl:for-each select="show-text | show-page-title">
                        <xsl:apply-templates select="."/>
                        <xsl:if test="position() != last()">
                            <xsl:value-of select="foundry:get-setting('global', 
                                                              'title/separator', 
                                                              ' - ',
                                                              ../separator)"/>
                        </xsl:if>
                    </xsl:for-each>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="current()"/>
                </xsl:otherwise>
            </xsl:choose>
        </title>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>tr</code> element. 
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/tabular-data.html#the-tr-element">
                Description of the <code>tr</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="tr">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <tr>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </tr>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>track</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/embedded-content-0.html#the-track-element">
                Description of the <code>track</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="track">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="default" as="xs:boolean" tunnel="yes" select="false()"/>
        <xsl:param name="kind" as="xs:string" tunnel="yes" select="''"/>
        <xsl:param name="label" as="xs:string" tunnel="yes" select="''"/>
        <xsl:param name="src" as="xs:string" tunnel="yes" select="''"/>
        <xsl:param name="srclang" as="xs:string" tunnel="yes" select="''"/>
        
        <xsl:variable name="src-raw">
            <xsl:choose>
                <xsl:when test="$src != ''">
                    <xsl:value-of select="$src"/>
                </xsl:when>
                <xsl:when test="./@src-static">
                    <xsl:value-of select="./@src-static"/>
                </xsl:when>
                <xsl:when test="./@src-property">
                    <xsl:value-of select="$data-tree/*[name() = ./@src-property]"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        
        <xsl:variable name="src-processed" select="foundry:gen-src-url($src-raw)"/>
        
        <track>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" select="'default kind label srclang'"/>
            </xsl:call-template>
            <xsl:if test="$default = true()">
                <xsl:attribute name="default" select="'default'"/>
            </xsl:if>
            <xsl:if test="$kind != ''">
                <xsl:attribute name="kind" select="$kind"/>
            </xsl:if>
            <xsl:if test="$label != ''">
                <xsl:attribute name="label" select="$label"/>
            </xsl:if>
            <xsl:if test="$srclang != ''">
                <xsl:attribute name="label" select="$srclang"/>
            </xsl:if>
            
            <xsl:attribute name="src" select="$src-processed"/>
        </track>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>u</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-u-element">
                Description of the u element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="u">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <u>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </u>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates an <code>ul</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/grouping-content.html#the-ul-element">
                Description of the <code>ul</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="ul">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <ul>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </ul>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates an <code>var</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-var-element">
                Description of the <code>var</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="var">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        
        <var>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
            </xsl:call-template>
            <xsl:apply-templates/>
        </var>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a HTML5 <code>video</code> element. The source URL and the URL of preview
                image (<code>poster</code>) can be provided by a surrounding element, statically in
                the theme or by an property in the data tree.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="src-static">
                <p>
                    A static URL for the source of the video file
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="src-property">
                <p>
                    Name of property in the data tree containing the source URL.
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="poster-static">
                <p>
                    A static URL for the source of the preview image
                </p>
            </foundry:doc-attribute>
            <foundry:doc-attribute name="poster-property">
                <p>
                    Name of property in the data tree containing the poster URL.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/embedded-content-0.html#the-video-element">
                Description of the  <code>video</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="video">
        <xsl:param name="id" select="''"/>
        <xsl:param name="class" select="''"/>
        <xsl:param name="src" as="xs:string" tunnel="yes" select="''"/>
        <xsl:param name="poster" as="xs:string" tunnel="yes" select="''"/>
        
        <xsl:variable name="src-raw">
            <xsl:choose>
                <xsl:when test="$src != ''">
                    <xsl:value-of select="$src"/>
                </xsl:when>
                <xsl:when test="./@src-static">
                    <xsl:value-of select="./@src-static"/>
                </xsl:when>
                <xsl:when test="./@src-property">
                    <xsl:value-of select="$data-tree/*[name() = ./@src-property]"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="video-src" select="foundry:gen-src-url($src-raw)"/>
        
        <xsl:variable name="poster-raw">
            <xsl:choose>
                <xsl:when test="$poster != ''">
                    <xsl:value-of select="$poster"/>
                </xsl:when>
                <xsl:when test="./@poster-static">
                    <xsl:value-of select="./@poster-static"/>
                </xsl:when>
                <xsl:when test="./@poster-property">
                    <xsl:value-of select="$data-tree/*[name() = ./@poster-property]"/>
                </xsl:when>
            </xsl:choose>
        </xsl:variable>
        <xsl:variable name="poster-src" select="foundry:gen-src-url($poster-raw)"/>
        
        <video>
            <xsl:call-template name="foundry:process-layouttree-attributes">
                <xsl:with-param name="id" select="$id"/>
                <xsl:with-param name="class" select="$class"/>
                <xsl:with-param name="copy-attributes" 
                                select="'autoplay buffered controls loop muted played preload'"/>
            </xsl:call-template>
            <xsl:attribute name="poster" select="$poster-src"/>
            <xsl:attribute name="src" select="$video-src"/>
            
            <xsl:apply-templates/>
        </video>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Generates a <code>wbr</code> element.
            </p>
        </foundry:doc-desc>
        <foundry:doc-see-also>
            <foundry:doc-link href="http://www.w3.org/TR/html5/text-level-semantics.html#the-wbr-element">
                Description of the <code>wbr</code> element in the HTML5 specification.
            </foundry:doc-link>
        </foundry:doc-see-also>
    </foundry:doc>
    <xsl:template match="wbr">
        <wbr/>
    </xsl:template>
    
    


</xsl:stylesheet>