<?xml version="1.0" encoding="utf-8"?>
<!--
    Copyright 2015 Jens Pelzetter for the LibreCCM Foundation
    
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
        <foundry:doc-file-title>
            Tags for ccm-cms-types-contact
        </foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                These tags are used to output the information of a contact 
                content item.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for displaying data from the person item which is 
                assigned to a contact item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//contact-person">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="person"
                            tunnel="yes"
                            select="$contentitem-tree/person"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for outputting the contact entries of a contact.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//contact-entries">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/contactentries">
            <xsl:apply-templates>
                <xsl:with-param name="contact-entries" 
                                tunnel="yes"
                                select="$contentitem-tree/contactentries"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                This tag is used to output a specific contact entry.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="key">
                <p>
                    The key of the contact entry to show. The tag itself does
                    not generate any output. It only extracts the informations
                    for the contact entry and passes them to its child tags.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(), 'contact-entries')]//contact-entry">
        <xsl:param name="contact-entries" tunnel="yes"/>

        <xsl:variable name="keyId" select="./@key"/>

        <xsl:if test="$contact-entries[./keyId = $keyId]">
            <xsl:apply-templates>
                <xsl:with-param name="label" 
                                tunnel="yes"  
                                select="$contact-entries[./keyId = $keyId]/key"/>
                <xsl:with-param name="value" 
                                tunnel="yes"  
                                select="$contact-entries[./keyId = $keyId]/value"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the label of the contact entry as provided in data tree.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(), 'contact-entries')]//contact-entry//contact-entry-label">
        <xsl:param name="label" tunnel="yes"/>
        
        <xsl:value-of select="$label"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the data of the contact entry. If the value starts
                with
            </p>
            <ul>
                <li>
                    <code>http://</code>
                </li>
                <li>
                    <code>https://</code>
                </li>
                <li>
                    <code>www</code>
                </li>
            </ul>
            <p>
                and the <code>autolink</code> attribute is not set to 
                <code>false</code> the entry rendered as link, otherwise
                as text.
            </p>
            <p>
                Likewise if the value contains and <code>@</code> character
                and the <code>autolink</code> attribute is not set to 
                <code>false</code> the entry rendered as E-Mail link.
            </p>
        </foundry:doc-desc>
        <foundry:doc-attributes>
            <foundry:doc-attribute name="autolink" type="boolean">
                <p>
                    If set to <code>true</code> or if not present URLs
                    are automatically converted to HTML links. If set to 
                    <code>false</code> URLs are displayed as text.
                </p>
            </foundry:doc-attribute>
        </foundry:doc-attributes>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(), 'contact-entries')]//contact-entry//contact-entry-value">
        <xsl:param name="value" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="(starts-with($value, 'http://')
                             or starts-with($value, 'https://')
                             or starts-with($value, 'www'))
                            and not(foundry:boolean(./@autolink))">
                <a href="{$value}">
                    <xsl:value-of select="$value"/>
                </a>
            </xsl:when> 
            <xsl:when test="contains($value, '@') 
                            and not(foundry:boolean(./@autolink))">
                <a href="{concat('mailto:', $value)}">
                    <xsl:value-of select="$value"/>
                </a>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$value"/>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the value of a contact entry as link. This used if the
                automatic link detection of the <code>contact-entry-value</code>
                tag does not work.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(), 'contact-entries')]//contact-entry//contact-entry-value-as-link">
        <xsl:param name="value" tunnel="yes"/>
       
        <xsl:choose>
            <xsl:when test="contains($value, '@')">
                <xsl:apply-templates>
                    <xsl:with-param name="href" 
                                    tunnel="yes"
                                    select="concat('mailto:', $value)"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates>
                    <xsl:with-param name="href" tunnel="yes" select="$value"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for outputting the address assigned to contact item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//contact-address">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:if test="$contentitem-tree/address">
            <xsl:apply-templates>
                <xsl:with-param name="address"
                                tunnel="yes"
                                select="$contentitem-tree/address"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the address text property of an address assigned to
                a contact item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//address-text">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/address"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the postal code property of an address assigned to
                a contact item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//postal-code">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/postalCode"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the city property of an address assigned to
                a contact item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//city">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/city"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the state property of an address assigned to
                a contact item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//state">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/state"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the country property of an address assigned to
                a contact item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//country">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/country"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the iso country code property of an address assigned to
                a contact item.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//*[ends-with(name(),'-address')]//iso-country-code">
        <xsl:param name="address" tunnel="yes"/>
        
        <xsl:value-of select="$address/isoCountryCode"/>
    </xsl:template>
    
</xsl:stylesheet>