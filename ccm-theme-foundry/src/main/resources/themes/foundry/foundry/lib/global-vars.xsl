<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>
                      <!ENTITY hellip '&#8230;'>
                    ]>
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

<!-- 
This file defines several global variables (constants). Some are provided by CCM thorough the XSL
processor, some are read from the configuration files of Foundry and some are defined here.
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl bebop foundry ui"
                version="2.0">
    
     <foundry:doc-file>
        <foundry:doc-file-title>Global/environment variables</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                Global variables either provided by the calling CCM instance or by Foundry itself.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <!-- Foundry internal variables -->
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
            The version of Foundry. Kept in sync with the version of CCM, so the first version
            was be 2.2.3.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="foundry-version" select="'2.2.3-SNAPSHOT'"/>
    
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
                The mode of the theme. If the theme is standalone theme, the value is 
                <code>master</code>. If the theme is a child theme the value is <code>child</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <!-- 
        We have to duplicate the logic of foundry:get-setting here because otherwise 
        theme-mode would depend on its own value.
    -->
    <xsl:variable name="theme-mode"
                  select="document(concat($theme-prefix, '/conf/global.xml'))/foundry:configuration/setting[@id='theme-mode']"/>
        
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
                The master theme of the current if the theme is a child theme. if theme is direct
                child of the Foundry base theme the value is <code>foundry</code>. Otherwise it is
                the name of master theme.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="master-theme" select="foundry:get-setting('global', 
                                                                  'master-theme',
                                                                  'foundry')"/>
    
    <!-- **************************************************************************** -->
    
    <!-- CCM Environment variables -->
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
                The path the to theme file. This path is used at several points to load files which are
                part of the theme, like CSS files, images and fonts.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="theme-prefix" 
               select="concat($context-prefix, 'themes/libreccm-default/')"/>
    
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
            The context prefix in which CCM is installed. If CCM is installed into the ROOT context
            of the servlet container, this variable will be empty.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="context-prefix"/>
    
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
                The path on which the CCM dispatcher Servlet is mounted. Usually this is 
                <code>CCM</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="dispatcher-prefix"/>
    
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
                The name of user currently login in CCM.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="username"> 
        <xsl:choose>
            <xsl:when test="/bebop:page/ui:userBanner/@screenName">
                <xsl:value-of select="concat(/bebop:page/ui:userBanner/@givenName, ' ', /bebop:page/ui:userBanner/@familyName)"/>
            </xsl:when>
        </xsl:choose>
    </xsl:variable>
    
    <!-- System variables -->
    
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
                This variable stores the XML created by CCM for later access.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="data-tree" select="/bebop:page"/>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            <p>
                This variables stores the XML definition of the Foundry documentation.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="foundry-doc-tree" select="foundry:documentation"/>
    
    <!-- **************************************************************************** -->
    
    <!-- Double click protection -->
    <foundry:doc section="devel">
        <foundry:doc-desc>
            <p>
                Activate double click protection on buttons?
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="dcp-on-buttons"/>
    
    <foundry:doc section="devel">
        <foundry:doc-desc>
            <p>
                Activate double click protection on links?
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="dcp-on-links"/>
    
    <!-- **************************************************************************** -->
    
    
    <!-- Language related variables -->
    
    <foundry:doc section="devel" type="template-tag">
        <foundry:doc-desc>
            <p>
                The language negotiated between CCM and the user agent.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <foundry:doc section="devel">
        <foundry:doc-desc>
            The language to use as negotiated by CCM.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:param name="negotiated-language" select="'en'"/>
    
    <xsl:variable name="lang">
        <xsl:choose>
            <xsl:when test="document(foundry:gen-path('conf/global.xml'))/foundry:configuration/supported-languages/language[@locale=$negotiated-language]">
                <xsl:value-of select="$negotiated-language"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="document(foundry:gen-path('conf/global.xml'))/foundry:configuration/supported-languages/language[@default='true']/@locale"/>
            </xsl:otherwise>
        </xsl:choose>
        
        
    </xsl:variable>
    
    <!--<foundry:doc section="devel">
        <foundry:doc-desc>
            The languages supported by this theme. Set in the <code>global.xml</code> configuration 
            file.
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="languages">
        
    </xsl:variable>-->
    
    <foundry:doc section="devel" type="env-var">
        <foundry:doc-desc>
            <p>
                The languages supported by this theme. They are configured in 
                <code>conf/global.xml</code> using the <code>&lt;supported-languages&gt;</code>
                element. Example for german and english:
                <pre>
                &lt;?xml version="1.0"?&gt;
                &lt;foundry:configuration&gt;
                    &hellip;
                    &lt;supported-languages default="de"&gt;
                        &lt;language locale=de"&gt;
                        &lt;language locale=en"&gt;
                    &lt;/supported-languages&gt;
                    &hellip;
                &lt;/foundry:configuration&gt;
                </pre>
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="supported-languages"
              select="document(foundry:gen-path('conf/global.xml'))/foundry:configuration/supported-languages"/>
    
    <foundry:doc section="devel" type="template-tag">
        <foundry:doc-desc>
            <p>
                The language to use by theme engine for static texts etc. The language is determined
                as follows:
            </p>
            <ul>
                <li>If the negotiated language is also in the <code>supported-languages</code></li>
                <li>If not the language which set by the default attribute of the 
                    <code>&lt;supported-languages&gt;</code> is used, but only if this language
                    is in the supported languages.</li>
                <li>Otherwise the first of the supported languages is used.</li>
            </ul>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:variable name="language">
        <xsl:choose>
            <xsl:when test="$supported-languages/language[@locale=$negotiated-language]">
                <xsl:value-of select="$negotiated-language"/>
            </xsl:when>
            <xsl:when test="not($supported-languages/language[@locale=$negotiated-language]) and $supported-languages/language[$supported-languages/@default]">
                <xsl:value-of select="$supported-languages/language[$supported-languages/@default]"/>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$supported-languages/language[1]/@locale"/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:variable>
    
    <!-- **************************************************************************** -->
    
    <!-- 
        Variables describing the user agent.
        ToDo: Check if we still need them.
    -->
    <!--<foundry:doc section="devel" type="evn-var">
        <foundry:doc-desc>
            <p>
                The name of the user agent (browser) which is used to access CCM.
            </p>
        </foundry:doc-desc>
    </foundry:doc>-->
    <xsl:param name="user-agent"/>
    
    <xsl:variable name="mozilla-version">
        <xsl:value-of select="substring(substring-after($user-agent, 'Mozilla/'), 1, 1)"/>
    </xsl:variable>
    
    <!-- Firefox -->
    <xsl:variable name="firefox-version">
        <xsl:value-of select="substring(substring-after($user-agent, 'Firefox/'), 1, 1)"/>
    </xsl:variable>
  
    <!-- Konqueror -->
    <xsl:variable name="konqueror-version">
        <xsl:value-of select="substring(substring-after($user-agent, 'Konqueror/'), 1, 1)"/>
    </xsl:variable>
  
    <!-- Opera -->
    <xsl:variable name="opera-version1">
        <xsl:value-of select="substring(substring-after($user-agent, 'Opera/'), 1, 1)"/>
    </xsl:variable>
  
    <xsl:variable name="opera-version2">
        <xsl:value-of select="substring(substring-after($user-agent, 'Opera '), 1, 1)"/>
    </xsl:variable>
  
    <!-- MSIE -->
    <xsl:variable name="msie_version">
        <xsl:value-of select="substring(substring-after($user-agent, 'MSIE '), 1, 1)"/>
    </xsl:variable>
  
    <!-- AppleWebKit -->
    <xsl:variable name="webkit_version">
        <xsl:value-of select="substring(substring-after($user-agent, 'AppleWebKit/'), 1, 3)"/>
    </xsl:variable>
    
</xsl:stylesheet>