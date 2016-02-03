<?xml version="1.0"  encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

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
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                exclude-result-prefixes="xsl foundry bebop"
                version="2.0">

    <xsl:import href="lib.xsl"/>
    
    <xsl:output method="html"
                doctype-system="about:legacy-compat"
                indent="yes"
                encoding="utf-8"/>
    
    <xsl:strip-space elements="*"/>
    
    <xsl:template match="bebop:page">
        
        <xsl:variable name="class" select="@class" />
        
        <xsl:variable name="app-layout-template-file" 
                      select="foundry:get-app-layout-template(foundry:get-current-application(), 
                                                              foundry:get-current-application-class())"/>
        
        <xsl:call-template name="foundry:message-debug">
            <xsl:with-param name="message" 
                            select="concat('app-layout-template-file = ', $app-layout-template-file)"/>
        </xsl:call-template>
        
        <xsl:choose>
            <xsl:when test="$app-layout-template-file = ''">
                <xsl:call-template name="foundry:process-template">
                    <xsl:with-param name="template-file" 
                                    select="'default-layout.xml'"/>
                    <xsl:with-param name="origin" select="'internal'"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:call-template name="foundry:process-template">
                    <xsl:with-param name="template-file"
                                    select="$app-layout-template-file"/>
                    <xsl:with-param name="origin" 
                                    select="foundry:app-layout-template-get-origin(foundry:get-current-application(), 
                                                                                   foundry:get-current-application-class())"/>
                </xsl:call-template>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:function name="foundry:get-current-application">
        <xsl:choose>
            <xsl:when test="$data-tree/@application">
                <xsl:sequence select="$data-tree/@application"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="'none'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="foundry:get-current-application-class">
        <xsl:choose>
            <xsl:when test="$data-tree/@class">
                <xsl:sequence select="$data-tree/@class"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="'none'"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <xsl:function name="foundry:get-app-layout-template">
        <xsl:param name="application" as="xs:string"/>
        <xsl:param name="class" as="xs:string"/>
        
        <xsl:call-template name="foundry:message-debug">
            <xsl:with-param name="message"
                            select="concat('templates.xml path: ', 
                                           foundry:gen-path('conf/templates.xml'))"/>
        </xsl:call-template>
        
        <xsl:choose>
            <xsl:when test="document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and @class=$class]">
                <xsl:sequence select="document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and @class=$class]"/>
            </xsl:when>
            <xsl:when test="document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and not(@class)]">
                <xsl:sequence select="document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and not(@class)]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:choose>
                    <xsl:when test="document(foundry:gen-path('conf/templates.xml'))/templates/applications/default">
                        <xsl:sequence select="document(foundry:gen-path('conf/templates.xml'))/templates/applications/default"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:sequence select="''"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>
    
    <xsl:function name="foundry:app-layout-template-get-origin" as="xs:string">
        <xsl:param name="application" as="xs:string"/>
        <xsl:param name="class" as="xs:string"/>
        
        <xsl:call-template name="foundry:message-debug">
            <xsl:with-param name="message"
                            select="concat('trying to determine origin for template file for application = ', 
                                           $application, 
                                           ' and class = ', 
                                           $class)"/>
        </xsl:call-template>
        <xsl:call-template name="foundry:message-debug">
            <xsl:with-param name="message"
                            select="concat('using template configuration file: ', 
                                           foundry:gen-path('conf/templates.xml'))"/>
        </xsl:call-template>
        
        <xsl:choose>
            <xsl:when test="document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and @class=$class]
                            and document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and @class=$class]/@origin">
                <xsl:sequence select="document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and @class=$class]/@origin"/>
            </xsl:when>
            <xsl:when test="document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and not(@class)]
                            and document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and not(@class)]/@origin">
                <xsl:sequence select="document(foundry:gen-path('conf/templates.xml'))/templates/applications/application[@name=$application and not(@class)]/@origin"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:sequence select="''"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:function>

    <!--<foundry:doc section="devel">
        <foundry:doc-desc>
            <p>
                The entry point for creating Foundry documentation.
            </p>
        </foundry:doc-desc>
    </foundry:doc>-->
    <xsl:template match="/foundry:documentation">
        <xsl:apply-templates select="document(foundry:gen-path('foundry/templates/doc/foundry-documentation.xml'))"/>
    </xsl:template>

</xsl:stylesheet>