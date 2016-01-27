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
        <foundry:doc-file-title>Tags for person types</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The tags provided by this file can be used to display the 
                properties types derived from <code>GenericPerson</code>, for 
                example ccm-cms-types-member or the SciAuthor type from 
                ccm-sci-publications.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag enclosing all other tags for persons. Extracts the
                person data from the data tree and passed it to the enclosed 
                tags.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//person">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:apply-templates>
            <xsl:with-param name="person"
                            tunnel="yes"
                            select="$contentitem-tree"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the surname of a person.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//surname">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:value-of select="$person/surname"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the given name of person.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//givenname">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:value-of select="$person/givenname"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the titlepre property of person (used for example for
                titles like <em>Prof.</em> or <em>Dr.</em>. 
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//titlepre">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:value-of select="$person/titlepre"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Displays the titlepost property of person (used for example for
                titles like <em>PhD</em>.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//titlepost">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:value-of select="$person/titlepost"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Apply the enclosed tags only if the person has a surname.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//if-surname">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:if test="string-length($person/surname) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Apply the enclosed tags only if the person has a given name.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//if-givenname">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:if test="string-length($person/givenname) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Apply the enclosed tags only if the person has a titlepre 
                property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//if-titlepre">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:if test="string-length($person/titlepre) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Apply the enclosed tags only if the person has a title post 
                property.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//if-titlepost">
        <xsl:param name="person" tunnel="yes"/>
        
        <xsl:if test="string-length($person/titlepost) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Extracts the URL of the personal homepage of the person (if 
                provided) and passed the URL to the enclosed tags.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//*[ends-with(name(), 'person') or ends-with(name(), 'member')]//person-homepage-link">
        <xsl:param name="person" tunnel="yes"/>
       
        <xsl:variable name="homepage-contact-type"
                      select="if (./@contact-type)
                              then ./@contact-type
                              else 'commonContact'"/>
        
        <xsl:variable name="homepage-contact-entry-key"
                      select="if (./@contact-entry-key)
                              then ./@contact-entry-key
                              else 'homepage'"/>

        <xsl:choose>
            <xsl:when test="$person/contacts/contact[./@contactType = $homepage-contact-type]/contactentries[./keyId = $homepage-contact-entry-key]">
                <xsl:apply-templates>
                    <xsl:with-param name="href"
                                    tunnel="yes"
                                    select="$person/contacts/contact[@contactType = $homepage-contact-type]/contactentries[./keyId = $homepage-contact-entry-key]/value"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates/>
            </xsl:otherwise>
        </xsl:choose>
        
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Root tag for showing the contact entries of a person.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//person-contact-entries">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="contact-type" 
                      select="if(./@contact-type)
                              then ./@contact-type
                              else 'commonContact'"/>

        <xsl:if test="$contentitem-tree/contacts/contact[@contactType=$contact-type]/contactentries">
            <xsl:apply-templates>
                <xsl:with-param name="contact-entries" 
                                tunnel="yes"
                                select="$contentitem-tree/contacts/contact[@contactType=$contact-type]/contactentries"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Show the address associated with contact dataset of a person.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="content-item-layout//person-address">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:variable name="contact-type" 
                      select="if(./@contact-type)
                              then ./@contact-type
                              else 'commonContact'"/>
        
        <xsl:if test="$contentitem-tree/contacts/contact[@contactType=$contact-type]/address">
            <xsl:apply-templates>
                <xsl:with-param name="address"
                                tunnel="yes"
                                select="$contentitem-tree/contacts/contact[@contactType=$contact-type]/address"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>