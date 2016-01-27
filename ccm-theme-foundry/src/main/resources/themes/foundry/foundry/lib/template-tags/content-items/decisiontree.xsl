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

    <foundry:doc-file>
        <foundry:doc-file-title>Tags for displaying a Decisiontree item</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The tags are used to configure the output of the decisiontree item. For technical
                reasons it is not yet possible to customise the HTML for the decisiontree 
                completely. 
            </p>
            <p>
                For title and description of the decisiontree the standard tags can be used. 
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root element for outputting the current section of a decisiontree.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//decisiontree-current-section">
        <xsl:param name="contentitem-tree" tunnel="yes"/>

        <xsl:variable name="current-section">
            <xsl:copy-of select="$contentitem-tree/sections[title]/*"/>
        </xsl:variable>
        <xsl:variable name="parameters">
            <xsl:copy-of select="$contentitem-tree/parameters"/>
        </xsl:variable>
        
        <xsl:apply-templates>
            <xsl:with-param name="current-section" tunnel="yes" select="$current-section"/>
            <xsl:with-param name="current-section-oid" 
                            tunnel="yes" 
                            select="$contentitem-tree/sections[title]/@oid"/>
            <xsl:with-param name="parameters" tunnel="yes" select="$parameters"/>
            <xsl:with-param name="current-url" 
                            tunnel="yes" 
                            select="$contentitem-tree/customInfo/@currentURL"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the title of the current section of a decisiontree.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="decisiontree-current-section//section-title">
        <xsl:param name="current-section" tunnel="yes"/>
        
        <xsl:value-of select="$current-section/title"/>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the instructions for the current section of a decisiontree.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="decisiontree-current-section//section-instructions">
        <xsl:param name="current-section" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes" select="$current-section/instructions"/>
    </xsl:template>
    
    <foundry:doc section="user" type="decisiontree">
        <foundry:doc-desc>
            <p>
                Outputs the options for the current section of a decisiontree. This tag outputs
                a complete HTML form because the current implementation of the decisiontree does not
                provide enough information in the XML to support a fully customisable HTML.
            </p>
            <p>
                Nevertheless this it is possible to customise the classes set on various of the HTML
                in the created form using subelements:
            </p>
            <dl>
                <dt>
                    <code>class-form</code>
                </dt>
                <dd>
                    Classes for the form itself.
                </dd>
                <dt>
                    <code>class-formgroup</code>
                </dt>
                <dd>
                    Classes to set on the <code>div</code> surrouding each pair of a label and an 
                    input element.
                </dd>
                <dt>
                    <code>class-label</code>
                </dt>
                <dd>
                    Classes to set on each label.
                </dd>
                <dt>
                    <code>class-input</code>
                </dt>
                <dd>
                    Classes to set on each input element.
                </dd>
                <dt>
                    <code>class-buttons</code>
                </dt>
                <dd>
                    Classes to set on the <code>div</code> surrounding the submit and cancel button.
                </dd>
                <dt>
                    <code>class-cancel</code>
                </dt>
                <dd>
                    Classes to set on the cancel button.
                </dd>
                <dt>
                    <code>class-submit</code>
                </dt>
                <dd>
                    Classes to set on the submit button.
                </dd>
            </dl>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="decisiontree-current-section//options">
        <xsl:param name="current-section" tunnel="yes"/>
        <xsl:param name="current-section-oid" tunnel="yes"/>
        <xsl:param name="parameters" tunnel="yes"/>
        <xsl:param name="current-url" tunnel="yes"/>
        
        <xsl:variable name="class-form" select="./class-form"/>
        <xsl:variable name="class-formgroup" select="./class-formgroup"/>
        <xsl:variable name="class-label" select="./class-label"/>
        <xsl:variable name="class-input" select="./class-input"/>
        <xsl:variable name="class-buttons" select="./class-buttons"/>
        <xsl:variable name="class-submit" select="./class-submit"/>
        <xsl:variable name="class-cancel" select="./class-cancel"/>
        <xsl:variable name="next-button-left" select="foundry:boolean(./next-button-left)"/>
        
        <script type="text/javascript"
                src="{$context-prefix}/templates/ccm-cms-types-decisiontree/forms.js"/>
        <form method="get"
              action="{$context-prefix}/templates/ccm-cms-types-decisiontree/form-handler.jsp"
              class="{$class-form}">
            
            <input name="section_oid"
                   type="hidden"
                   value="{$current-section-oid}"/>
            
            <input name="return_url"
                   type="hidden"
                   value="{$current-url}"/>
               
            <xsl:for-each select="$parameters">
                <xsl:if test="./@name != 'output'">
                    <input type="hidden"
                           name="{./@name}"
                           value="{./@value}"/>
                </xsl:if>
            </xsl:for-each>
            
            <xsl:for-each select="$current-section/sectionOptions">
                <xsl:sort select="./rank"/>
                
                <div class="{$class-formgroup}">
                    <label for="{./@oid}"
                           class="{$class-label}">
                        <xsl:value-of select="./label"/>
                    </label>
                    <input type="radio"
                           class="{$class-input}"
                           name="{$current-section/parameterName}"
                           value="{./value}"
                           id="./@oid"/>
                </div>
            </xsl:for-each>
            
            <xsl:variable name="cancel-text" 
                          select="foundry:get-static-text('decisiontree', 'cancel')"/>
            <xsl:variable name="submit-text" 
                          select="foundry:get-static-text('decisiontree', 'submit')"/>
            
            <div class="{$class-buttons}">
                <xsl:choose>
                    <xsl:when test="$next-button-left">
                        <input type="submit"
                               class="{$class-submit}"
                               onclick="return-validate(this.form)"
                               value="{$submit-text}"
                               name="next"/>
                        <input type="submit"
                               class="{$class-cancel}"
                               value="{$cancel-text}"
                               name="cancel">
                        </input>
                    </xsl:when>
                    <xsl:otherwise>
                        <input type="submit"
                               class="{$class-cancel}"
                               value="{$cancel-text}"
                               name="cancel">
                        </input>
                        <input type="submit"
                               class="{$class-submit}"
                               onclick="return-validate(this.form)"
                               value="{$submit-text}"
                               name="next"/>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
        </form>
    </xsl:template>

</xsl:stylesheet>