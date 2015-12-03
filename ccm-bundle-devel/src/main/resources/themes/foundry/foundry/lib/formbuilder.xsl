<?xml version="1.0"  encoding="utf-8"?>
<!DOCTYPE stylesheet>
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
                xmlns:formbuilder="http://www.arsdigita.com/formbuilder/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms formbuilder foundry nav ui"
                version="2.0">
    
    <!-- 
        This file contains XSL templates copied from Mandalay for 
        processing FormBuilder components. They are used for example by the
        ccm-cms-types-formitem content type.
    -->
    
    <xsl:template name="foundry:formbuilder-components">
        
        <!-- Find out which component we are processing -->
        <xsl:choose>
            
            <!-- FormSection -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.cms.formbuilder.FormSectionWrapper'">
                <xsl:call-template name="foundry:formbuilder-form-section"/>
            </xsl:when>
            
            <!--- Label with widget -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.WidgetLabel'">
                <xsl:call-template name="foundry:formbuilder-widget-label"/>
            </xsl:when>
            
            <!-- Checkbox group -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup'">
                <xsl:call-template name="foundry:formbuilder-button-group"/>
            </xsl:when>
            
            <!-- Radiobutton group -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
                <xsl:call-template name="foundry:formbuilder-button-group"/>
            </xsl:when>
            
            <!-- Single Select -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect'">
                <xsl:call-template name="foundry:formbuilder-select"/>
            </xsl:when>
            
            <!-- Mulitple select -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect'">
                <xsl:call-template name="foundry:formbuilder-select"/>
            </xsl:when>
            
            <!-- DataDriven select -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect'">
                <xsl:call-template name="foundry:formbuilder-select"/>
            </xsl:when>
            
            <!-- Form heading -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHeading'">
                <xsl:call-template name="foundry:formbuilder-formheading"/>
            </xsl:when>
            
            <!-- Form text -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentText'">
                <xsl:call-template name="foundry:formbuilder-formtext"/>
            </xsl:when>
            
            <!-- Text field -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentTextField'">
                <xsl:call-template name="foundry:formbuilder-textfield"/>
            </xsl:when>
            
            <!-- Password field -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentPassword'">
                <xsl:call-template name="foundry:formbuilder-passwordfield"/>
            </xsl:when>
            
            <!-- Hidden field -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHidden'">
                <xsl:call-template name="foundry:formbuilder-hiddenfield"/>
            </xsl:when>
            
            <!-- Hidden ID generator -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.HiddenIDGenerator'">
                <xsl:call-template name="foundry:formbuilder-hidden-id-generator"/>
            </xsl:when>
            
            <!-- Text area -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentTextArea'">
                <xsl:call-template name="foundry:formbuilder-textarea"/>
            </xsl:when>
            
            <!-- E-Mail field -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentEmailField'">
                <xsl:call-template name="foundry:formbuilder-emailfield"/>
            </xsl:when>
            
            <!-- Date field -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentEmailField'">
                <xsl:call-template name="foundry:formbuilder-datefield"/>
            </xsl:when>
            
            <!-- Submit button -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSubmit'">
                <xsl:call-template name="foundry:formbuilder-button"/>
            </xsl:when>
            
            <!-- Option -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentOption'">
                <xsl:call-template name="foundry:formbuilder-optionfield"/>
            </xsl:when>

            <!-- Horizontal ruler -->
            <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentHorizontalRuler'">
                <xsl:call-template name="foundry:formbuilder-ruler"/>
            </xsl:when>
            
            <xsl:otherwise>
                <xsl:call-template name="foundry:formbuilder-unknown-component"/>
            </xsl:otherwise>
            
        </xsl:choose>
        
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-form-section">
        <div class="formsection">
            <span class="title">
                <xsl:value-of select="./formSectionItem/title"/>
            </span>
            
            <!-- Process all Widgets. Only the Widget labels will be processed,
                 the Wigets components are duplicates.
            -->
            <xsl:for-each select="./formSectionItem/formSection/component[
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
            
        </div>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-widget-label">
        <div class="component">
            <!-- Process the components inside this label -->
            <xsl:for-each select="./widget">
                <xsl:sort data-type="number" select="./link/orderNumber"/>
                <xsl:call-template name="foundry:formbuilder-components"/>
            </xsl:for-each>
        </div>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-button-group">
        <div class="group">
            <xsl:call-template name="foundry:formbuilder-form-title"/>
            
            <!-- Process components inside this component -->
            <xsl:for-each select="./component">
                <xsl:sort data-type="number" select="./link/orderNumber"/>
                <xsl:call-template name="foundry:formbuilder-components"/>
            </xsl:for-each>
            
            <!-- Process other option -->
            <xsl:call-template name="foundry:formbuilder-optionother"/>
            
        </div>
    </xsl:template>
    
    <!-- Process select fields -->
    <xsl:template name="foundry:formbuilder-select">
        
        <div class="select">
            <xsl:call-template name="foundry:formbuilder-form-title"/>
            
            <select name="{./parameterName}">
                <!-- Set multiple attribute for multiple selects -->
                <xsl:if test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect' 
                               or (./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect' 
                                   and ./multiple = 'true')">
                    <xsl:attribute name="multiple">multiple</xsl:attribute>
                </xsl:if>
                
                <!-- Create empty first entry if necessary -->
                <xsl:if test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect' 
                              or (./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect' 
                                  and ./multiple = 'false')">
                    <option value="">
                        <xsl:value-of select="foundry:get-static-text('formbuilder', 
                                                                      'please-select')"/>
                    </option>
                </xsl:if>
                
                <xsl:choose>
                    <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect'">    
                        <!-- Process options -->
                        <xsl:for-each select="./selectOptions/option">
                            <xsl:sort select="./@label"/>
                            
                            <option value="{./@id}">
                                <xsl:value-of select="./@label"/>
                            </option>
                        </xsl:for-each>
                    </xsl:when>
                    
                    <xsl:otherwise>
                        <!-- Process options -->
                        <xsl:for-each select="./component">
                            <xsl:sort data-type="number" 
                                      select="./link/orderNumber"/>
                            <xsl:call-template name="foundry:formbuilder-components"/>
                        </xsl:for-each>
                    </xsl:otherwise>
                </xsl:choose>
                
                <!-- Add 'other' option if present -->
                <xsl:if test="./optiongroupother = 'true'">
                    <option value="{./optiongroupothervalue}">
                        <xsl:value-of select="./optiongroupotherlabel"/>
                    </option>
                </xsl:if>
                
            </select>
            
            <xsl:call-template name="foundry:formbuilder-optionother"/>
            
        </div>
        
    </xsl:template>
    
    <!-- Template for processing a form heading -->
    <xsl:template name="foundry:formbuilder-formheading">
        <div class="heading">
            <xsl:value-of disable-output-escaping="yes" 
                          select="./description"/>
        </div>
    </xsl:template>
    
    <!-- Form text -->
    <xsl:template name="foundry:formbuilder-formtext">
        <div class="text">
            <xsl:value-of disable-output-escaping="yes" select="./description"/>
        </div>
    </xsl:template>
    
    <!-- Text field -->
    <xsl:template name="foundry:formbuilder-textfield">
        <xsl:call-template name="foundry:formbuilder-label"/>
        
        <span class="textfield">
            <input type="text"
                   name="{./parameterName}"
                   value="{./defaultValue}">
                <xsl:choose>
                    <xsl:when test="xs:integer(./size) &gt; 0">
                        <xsl:attribute name="size" select="./size"/>
                    </xsl:when>
                    <xsl:when test="xs:integer(./size) = 0 
                               and xs:integer(./maxlength) &gt; 0">
                        <xsl:attribute name="size" select="./maxlength"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="size" select="'32'"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="xs:integer(./maxlength) &gt; 0">
                    <xsl:attribute name="maxlength" select="./maxlength"/>
                </xsl:if>
            </input>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-passwordfield">
        <xsl:call-template name="foundry:formbuilder-label"/>
        
        <span class="textfield">
            <input type="password"
                   name="{./parameterName}"
                   value="{./defaultValue}">
                <xsl:choose>
                    <xsl:when test="xs:integer(./size) &gt; 0">
                        <xsl:attribute name="size" select="./size"/>
                    </xsl:when>
                    <xsl:when test="xs:integer(./size) = 0 
                               and xs:integer(./maxlength) &gt; 0">
                        <xsl:attribute name="size" select="./maxlength"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="size" select="'32'"/>
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:if test="xs:integer(./maxlength) &gt; 0">
                    <xsl:attribute name="maxlength" select="./maxlength"/>
                </xsl:if>
            </input>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-hiddenfield">
        <span class="hidden">
            <input type="hidden"
                   name="{./parameterName}"
                   value="{./defaultValue}">
                <xsl:if test="./size &gt; 0">
                    <xsl:attribute name="size" select="./size"/>
                </xsl:if>
                <xsl:if test="./maxlength &gt; 0">
                    <xsl:attribute name="maxlength" select="./maxlength"/>
                </xsl:if>
                <xsl:attribute name="value">
                    <xsl:value-of select="./defaultValue"/>
                </xsl:attribute>
            </input>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-hidden-id-generator">
        <span class="hidden"
              name="{./parameterName}">
            <input type="hidden">
                <xsl:if test="./size &gt; 0">
                    <xsl:attribute name="size">
                        <xsl:value-of select="./size"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="./maxlength &gt; 0">
                    <xsl:attribute name="maxlength">
                        <xsl:value-of select="./maxlength"/>
                    </xsl:attribute>
                </xsl:if>
            </input>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-emailfield">
        <xsl:call-template name="foundry:formbuilder-label"/>
        
        <span class="textfield">
            <input type="email"
                   name="{./parameterName}">
                <xsl:if test="./size &gt; 0">
                    <xsl:attribute name="size">
                        <xsl:value-of select="./size"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="./maxlength &gt; 0">
                    <xsl:attribute name="maxlength">
                        <xsl:value-of select="./maxlength"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:choose>
                    <xsl:when test="string-length(./defaultValue) &gt; 0">
                        <xsl:attribute name="value">
                            <xsl:value-of select="./defaultValue"/>
                        </xsl:attribute>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:attribute name="value">
                            <xsl:value-of select="/bebop:page/ui:userBanner/@primaryEmail"/>
                        </xsl:attribute>
                    </xsl:otherwise>
                </xsl:choose>
            </input>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-datefield">
        <xsl:call-template name="foundry:formbuilder-label"/>
        
        <span class="date">
            
            <!-- Day input -->
            <input type="text"
                   name="{concat(./parameterName, '.day')}"
                   size="2"
                   maxlength="2"
                   value="{./defaultValue/day}"/>
            
            <!-- Month select -->
            <select name="{concat(./parameterName, '.month')}">
                <xsl:for-each select="./monthList/month">
                    <xsl:sort data-type="number" select="./@value"/>
                    
                    <option value="{./@value}">
                        <xsl:if test="./@selected = 'selected'">
                            <xsl:attribute name="selected" 
                                           select="'selected'"/>
                        </xsl:if>
                        <xsl:value-of select="."/>
                    </option>
                </xsl:for-each>
            </select>
            
            <!-- Year select -->
            <select name="{concat(./parameterName, '.year')}">
                <xsl:for-each select="./yearList/year">
                    <xsl:sort data-type="number" 
                              order="descending" 
                              select="./value"/>
                    
                    <option value="{./@value}">
                        <xsl:if test="./@selected = 'selected'">
                            <xsl:attribute name="selected" 
                                           select="'selected'"/>
                        </xsl:if>
                        <xsl:value-of select="."/>
                    </option>
                </xsl:for-each>
            </select>
        </span>
        
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-textarea">
        <div class="textarea">
            <xsl:call-template name="foundry:formbuilder-form-title"/>
            
            <textarea name="{./parameterName}"
                      rows="{./rows}"
                      cols="{./cols}">
                <xsl:value-of select="./defaultValue"/>
            </textarea>
        </div>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-ruler">
        <span class="ruler">
            <hr/>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-optionfield">
        <!-- Decide if we process a button group or a select -->
        <xsl:choose>
            <!-- Button group -->
            <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup' 
                            or ../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
                <div class="option">
                    <input name="{./parameterName}"
                           id="{concat(./parameterName, ':', ./parameterValue)}"
                           value="{./parameterValue}">
                        <!-- Decide if we show a check box or a radio button -->
                        <xsl:choose>
                            <!-- CheckboxGroup-->
                            <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup'">
                                <xsl:attribute name="type">checkbox</xsl:attribute>
                            </xsl:when>

                            <!-- RadioButtonGroup -->
                            <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
                                <xsl:attribute name="type">radio</xsl:attribute>
                            </xsl:when>
                        </xsl:choose>
                    </input>
                    <label for="{concat(./parameterName, ':', ./parameterValue)}">
                        <xsl:value-of disable-output-escaping="yes" 
                                      select="./label"/>
                    </label>
                </div>
            </xsl:when>
            
            <!-- Selects -->
            <xsl:when test="../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect' 
                            or ../defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect'">
                <option value="{./parameterValue}">
                    <xsl:value-of disable-output-escaping="yes" 
                                  select="./label"/>
                </option>
            </xsl:when>
            
        </xsl:choose>
        
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-button">
        <span class="button">
            <input type="{./parameterName}"
                   name="{./parameterName}"
                   value="{./defaultValue}"/>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-unknown-component">
        <span style="color:#ff0000">
            <xsl:value-of select="concat('!!! Unknown component: ', 
                                         ./defaultDomainClass)"/>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-label">
        <span class="label">
            <xsl:if test="./widgetrequired = 'true'">
                <xsl:attribute name="class">label mandatory</xsl:attribute>
            </xsl:if>
            <xsl:value-of disable-output-escaping="yes" select="../label"/>
        </span>
    </xsl:template>
    
    <xsl:template name="foundry:formbuilder-form-title">
        <span class="title">
            <xsl:if test="./widgetrequired = 'true'">
                <xsl:attribute name="class">title mandatory</xsl:attribute>
            </xsl:if>
            <xsl:value-of disable-output-escaping="yes" select="../label"/>
        </span>
    </xsl:template>
    
    <!-- Process other option -->
    <xsl:template name="foundry:formbuilder-optionother">
        
        <xsl:if test="./optiongroupother = 'true'">
            <div>
                <xsl:choose>
                    <!-- Selects -->
                    <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentSingleSelect' 
                                    or ./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentMultipleSelect' 
                                    or ./defaultDomainClass = 'com.arsdigita.formbuilder.DataDrivenSelect'">
                        <xsl:attribute name="id">other</xsl:attribute>
                        <xsl:if test="./optiongroupotherheight = 1">
                            <span class="label">
                                <xsl:value-of select="./optiongroupotherlabel"/>
                            </span>
                        </xsl:if>
                        <xsl:if test="./optiongroupotherheight > 1">
                            <span class="title">
                                <xsl:value-of select="./optiongroupotherlabel"/>
                            </span>
                        </xsl:if>
                    </xsl:when>
                    <xsl:otherwise> 
                        <xsl:attribute name="id">option</xsl:attribute>
                        <input name="{./parameterName}"
                               id="{concat(./parameterName, 
                                           ':',
                                           ./optiongroupotherlabel)}"
                               value="{./optiongroupothervalue}">
                            <!-- 
                                Are we proceesing a checkbox group or 
                                a radio button group? 
                            -->
                            <xsl:choose>
                                <!-- CheckboxGroup-->
                                <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentCheckboxGroup'">
                                    <xsl:attribute name="type">checkbox</xsl:attribute>
                                </xsl:when>

                                <!-- RadioButtonGroup -->
                                <xsl:when test="./defaultDomainClass = 'com.arsdigita.formbuilder.PersistentRadioGroup'">
                                    <xsl:attribute name="type">radio</xsl:attribute>
                                </xsl:when>
                            </xsl:choose>

                        </input>
                    </xsl:otherwise>
                </xsl:choose>
                
                <!-- Input field -->
                <xsl:if test="./optiongroupotherheight = '1'">
                    <label for="{concat(./parameterName, 
                                 ':', 
                                 ./optiongroupotherlabel)}">
                        <input name="{concat(./parameterName, '.other')}"
                               width="{./optiongroupotherwidth}"/>
                    </label>
                </xsl:if>
                <xsl:if test="xs:integer(./optiongroupotherheight) &gt; 1">
                    <textarea name="{./parameterName}"
                              rows="{./optiongroupotherheight}"
                              cols="{./optiongroupotherwidth}">
                    </textarea>
                </xsl:if>
                
            </div>
        </xsl:if>
        
    </xsl:template>
    
</xsl:stylesheet>
