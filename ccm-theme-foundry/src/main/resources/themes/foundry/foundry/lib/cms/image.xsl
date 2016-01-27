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
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">
    
    <xsl:template match="cms:imageDisplay">
        
        <div class="cmsImageDisplay">
            <img class="cmsImageDisplay"
                 src="{./@src}" 
                 alt="{./@name}"
                 width="{./@width}"
                 height="{./@height}"/>
            
            <xsl:if test="not(foundry:boolean(./@plain))">
                <div class="cmsImageOverlay">
                    <xsl:if test="foundry:boolean(foundry:get-setting('cms', 
                                                                      'image-display/show-image-name', 
                                                                      'true'))">
                        <span class="key">
                            <xsl:value-of select="./@name_label"/>
                        </span>
                        <span class="value">
                            <xsl:value-of select="./@name"/>
                        </span>
                        <br />
                    </xsl:if>
                    <xsl:if test="foundry:boolean(foundry:get-setting('cms', 
                                                                      'image-display/show-mime-type', 
                                                                      'true'))">
                        <span class="key">
                            <xsl:value-of select="./@mime_type_label"/>
                        </span>
                        <span class="value">
                            <xsl:choose>
                                <xsl:when test="./@mime_type">
                                    <xsl:value-of select="./@mime_type"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <em>
                                        <xsl:value-of select="foundry:get-static-text('cms', 
                                                                                      'image-display/info-overlay/type-unknown')"/>
                                    </em>
                                </xsl:otherwise>
                            </xsl:choose>
                        </span>
                        <br />
                    </xsl:if>
                    <xsl:if test="foundry:boolean(foundry:get-setting('cms', 
                                                                      'image-display/show-image-dimensions', 
                                                                      'true'))">
                        <span class="key">
                            <xsl:value-of select="./@dimension_label"/>
                        </span>
                        <span class="value">
                            <xsl:value-of select="concat(./@width, ' x ', ./@height)"/>
                        </span>
                        <br />
                    </xsl:if>
                    <xsl:if test="foundry:boolean(foundry:get-setting('cms', 
                                                                      'image-display/show-context', 
                                                                      'true'))">
                        <span class="key">
                            <xsl:value-of select="./@context_label"/>
                        </span>
                        <span class="value">
                            <xsl:value-of select="./@context"/>
                        </span>
                        <br/>
                    </xsl:if>
                    <xsl:if test="foundry:boolean(foundry:get-setting('cms', 
                                                                      'image-display/show-caption', 
                                                                      'true'))">
                        <span class="key">
                            <xsl:value-of select="./@caption_label"/>
                        </span>
                        <span class="value">
                            <xsl:value-of select="./@caption"/>
                        </span>
                    </xsl:if>
                </div>
            </xsl:if>
        </div>
        
    </xsl:template>
    
</xsl:stylesheet>