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
                xmlns:formbuilder="http://www.arsdigita.com/formbuilder/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms formbuilder foundry nav ui"
                version="2.0">
    
    <foundry:doc-file>
        <foundry:doc-file-title>
            Tags for ccm-cms-types-formitem and ccm-cms-types-formsectionitem
        </foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                The tags in these file are used to create the HTML 
                representation of the FormItem (and the FormSectionItems used
                by the FormItem). 
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
       

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the description of a FormItem.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//form-description">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:value-of select="$contentitem-tree/form/description"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the components (the controls) of a form. Unfortunately
                is not yet possible to customise the HTML of the form 
                components.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//form-components">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <form method="post"
              name="{$contentitem-tree/name}"
              action="{if($contentitem-tree/remote = 'true')
                       then $contentitem-tree/remoteUrl
                       else $data-tree//cms:item/@formAction}"
              accept-charset="utf-8">
            
            <xsl:if test="not($contentitem-tree/remote = 'true')">
                <input type="hidden" 
                       value="visited"
                       name="{concat('form.', $contentitem-tree/name)}"/>
            </xsl:if>
            
            <xsl:for-each select="$contentitem-tree/form/component[
                                  (
                                    objectType != 'com.arsdigita.formbuilder.Widget' and
                                    objectType != 'com.arsdigita.formbuilder.DataDrivenSelect'
                                  ) or 
                                  (
                                    defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit' or
                                    defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden' or
                                    defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator'
                                   )
                                  ]">
                <xsl:sort data-type="number" select="./link/orderNumber"/>
                <xsl:call-template name="foundry:formbuilder-components"/>
            </xsl:for-each>
            
            <!-- Hidden internal information fields -->
            <xsl:for-each select="$contentitem-tree/formbuilder:pageState/bebop:pageState">
                <input type="hidden"
                       name="{./@name}"
                       value="{./@value}"/>
            </xsl:for-each>
        </form>
    </xsl:template>
    
    
    
</xsl:stylesheet>