<?xml version="1.0" encoding="UTF-8"?>
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
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry ui"
                version="2.0">

    <foundry:doc-file>
        <foundry:doc-file-title>User banner tags</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                These tags can be used to extract the data provided by the 
                <code>&lt;userBanner&gt;</code> element in the data tree XML.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Shows the greeting text from the user banner.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="show-greeting">
        <xsl:value-of select="$data-tree/ui:userBanner/@greeting"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Render the enclosed tags only if the current user is logged in.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="if-loggedin">
        <xsl:if test="$data-tree/ui:userBanner/@screenName">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Render the enclosed tags only if the current user is 
                <strong>not</strong>logged in.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="if-not-loggedin">
        <xsl:if test="not($data-tree/ui:userBanner/@screenName)">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the link for changing the password. Uses the label 
                provided by the <code>&lt;userBanner&gt;</code> element in the 
                data tree XML as label for the link if there is no 
                <code>label</code> attribute present. If there is 
                <code>label</code> attribute than the value of the attribute
                is used to lookup the label for the link in the 
                <code>texts/user-banner.xml</code> file.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="user-banner-show-change-password-link">
        <a href="{$data-tree/ui:userBanner/@changePasswordURL}">
            <xsl:value-of select="if (./@label) 
                                  then foundry:get-static-text('user-banner', ./@label)
                                  else $data-tree/ui:userBanner/@changePasswordLabel"/>
        </a>
    </xsl:template>
    
    <foundry:doc section="user" typ="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the login link. Unfortuntly the data tree XML does not 
                provide a label for this link. Therefore if there is no 
                label attribute present at this tag we will use the key 
                <code>login</code> to lookup the label in the 
                <code>texts/user-banner.xml</code> file. If there is label 
                attribute present the value of the label attribute will be used
                to lookup the label in the <code>texts/user-banner.xml</code>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="user-banner-show-login-link">
        <a href="{$data-tree/ui:userBanner/@loginExcursionURL}">
            <xsl:value-of select="if (./@label) 
                                  then foundry:get-static-text('user-banner', ./@label)
                                  else foundry:get-static-text('user-banner', 'login')"/>
        </a>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the logout link. Uses the label 
                provided by the <code>&lt;userBanner&gt;</code> element in the 
                data tree XML as label for the link if there is no 
                <code>label</code> attribute present. If there is 
                <code>label</code> attribute than the value of the attribute
                is used to lookup the label for the link in the 
                <code>texts/user-banner.xml</code> file.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="user-banner-show-logout-link">
        <a href="{$data-tree/ui:userBanner/@logoutURL}">
            <xsl:value-of select="if (./@label) 
                                  then foundry:get-static-text('user-banner', ./@label)
                                  else $data-tree/ui:userBanner/@signOutLabel"/>
        </a>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the screenname of user currently logged in.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="show-screenname">
        <xsl:value-of select="$data-tree/ui:userBanner/@screenName"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the given name of the user currently logged in.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="show-user-givenname">
        <xsl:value-of select="$data-tree/ui:userBanner/@givenName"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the family name of the user currently logged in.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="show-user-familyname">
        <xsl:value-of select="$data-tree/ui:userBanner/@familyName"/>
    </xsl:template>
    
    
    
</xsl:stylesheet>
