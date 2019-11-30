<?xml version="1.0" encoding="utf-8"?>
<!--
/*
 * Copyright (C) 2019 LibreCCM Foundation.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
-->
<!--
    Processes the series search widget.
    
    Author: Jens Pelzetter, jens.pelzetter@googlemail.com
-->
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">
    
    <xsl:template match="cms:series-search-widget">
        
        <div class="series-search-widget">
            <xsl:choose>
                <xsl:when test="./cms:selected-series">
                    <p>
                        <xsl:value-of select="./cms:selected-series/@name" />
                    </p>
                    <input type="hidden" 
                           id="{./@name}" 
                           value="{./selected-series/@seriesId}" />
                </xsl:when>
                <xsl:otherwise>
                    <p id="{concat(./@name, '-selected')}">
                        <strong>
                            <xsl:value-of select="foundry:get-internal-static-text('cms', 'series-search-widget/no-series-selected')" />
                        </strong>
                    </p>
                </xsl:otherwise>
            </xsl:choose>
           <button id="{concat(./@name, 'select-series-button')}"
                    type="button"
                    class="select-series-button"
                    data-dialogId="{concat(./@name, '-dialog')}"
                    data-target="{./@name}">
                <xsl:value-of select="foundry:get-internal-static-text('cms', 'series-search-widget/select-series-button')" />
            </button>
            <dialog id="{concat(./@name, '-dialog')}"
                    class="series-search-widget-dialog"
                    data-dispatcherPrefix="{$dispatcher-prefix}"
                    data-targetId="{./@name}">
                <h3 class="titlebar">
                    <xsl:value-of select="foundry:get-internal-static-text('cms', 'series-search-widget/titlebar')" />
                    <button type="button" 
                            class="close-button" 
                            data-dialogId="{concat(./@name, '-dialog')}">
                        <span>
                            <xsl:value-of select="foundry:get-internal-static-text('cms', 'series-search-widget/titlebar/close')" />
                        </span>
                    </button>
                </h3>
                <div class="controls">
                    <input type="hidden" id="{concat(./@name, '-last-focus')}" />
                    <label for="{concat(./@name, 'series-filter')}">
                        <xsl:value-of select="foundry:get-internal-static-text('cms', 'series-search-widget/filter-list')" />
                    </label>
                    <input type="text" id="{concat(./@name, '-dialog-series-filter')}" />
                    <button type="button" 
                            class="apply-filter"
                            data-dialogId="{concat(./@name, '-dialog')}">
                        <xsl:value-of select="foundry:get-internal-static-text('cms', 'series-search-widget/filter-list/apply')" />
                    </button>
                </div>
                <div class="selectable-seriess">
                    <table>
                        <thead>
                            <tr>
                                <th>
                                    <xsl:value-of select="foundry:get-internal-static-text('cms', 'series-search-widget/table/header/name')" />
                                </th>
                                <th>
                                    <xsl:value-of select="foundry:get-internal-static-text('cms', 'series-search-widget/table/header/place')" />
                                </th>
                            </tr>
                        </thead>
                    </table>
                    <tbody>
                        <tr>
                            <td>$name</td>
                        </tr>
                    </tbody>
                </div>
            </dialog>
        </div>
        
    </xsl:template>
    
</xsl:stylesheet>
