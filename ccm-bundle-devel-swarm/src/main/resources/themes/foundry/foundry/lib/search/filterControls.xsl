<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'> <!ENTITY Cross '&#10799;'>]>

<!-- 
    Copyright: 2014 Jens Pelzetter
  
    This file is part of Foundry.

    Mandalay is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 2 of the License, or
    (at your option) any later version.

    Mandalay is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with Mandalay.  If not, see <http://www.gnu.org/licenses/>.
-->

<!-- This file was copied from Mandalay and edited to work with Foundry. -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">

    <xsl:template match="filterControls"
                  name="foundry:filter-controls">
        <form action=".">
            <xsl:attribute name="accept-charset">UTF-8</xsl:attribute>
            <xsl:choose>
                <xsl:when test="string-length(./@customName) &gt; 0">
                    <xsl:attribute name="class" 
                                   select="concat('filter-controls ', 
                                                  ./@customName, 
                                                  'FilterControls')"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="class" select="'filter-controls'"/>
                </xsl:otherwise>
            </xsl:choose>
            <xsl:if test="string-length(./@show) &gt; 0">
                <input type="hidden" name="show">	    
                    <xsl:attribute name="value">
                        <xsl:value-of select="./@show"/>
                    </xsl:attribute>
                </input>
            </xsl:if>
            
            <xsl:variable name="sort-and-filter-list-text" 
                          select="foundry:get-static-text('filter-controls', 
                                                          concat(./@customName, 
                                                                 'sortAndFilterList'))"/>
            <xsl:choose>
                <xsl:when test="string-length($sort-and-filter-list-text) > 0">
                    <fieldset>
                        <legend>
                            <xsl:value-of select="$sort-and-filter-list-text"/>
                        </legend>
                        <xsl:call-template name="filterControlsFiltersBody"/>
                        <xsl:call-template name="filterControlsSortFieldsBody"/>
                    </fieldset>
                </xsl:when>
                <xsl:otherwise>
                    <div>
                        <xsl:call-template name="filterControlsFiltersBody"/>
                        <xsl:call-template name="filterControlsSortFieldsBody"/>
                    </div>
                </xsl:otherwise>
            </xsl:choose>
        </form>
    </xsl:template>

    <xsl:template match="filterControls/filters"
                  name="filterControlsFiltersBody">
        <fieldset>
            <legend>
                <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                              concat(./@customName, 'filterList'))"/>
            </legend>
            <xsl:call-template name="filterControlsFilters"/>
            <input type="submit" class="submit">
                <xsl:attribute name="value" 
                               select="foundry:get-static-text('filter-controls', 
                                                               concat(./@customName, 'SubmitFilters'))"/>
            </input>
        </fieldset>
    </xsl:template>


    <xsl:template match="filterControls/sortFields"
                  name="filterControlsSortFieldsBody">
        <xsl:if test="count(./sortFields/sortField) &gt; 1">
            <fieldset>
                <legend>
                    <xsl:value-of select="foundry:get-static-text('filter-controls', 
                                                                  concat(./@customName, 'sortList'))"/>
                </legend>
                <xsl:call-template name="filterControlsSortFields"/>
                <input type="submit" class="submit">
                    <xsl:attribute name="value" 
                                   select="foundry:get-static-text('filter-controls',
                                                                   concat(./@customName, 'SortList'))"/>
                </input>
            </fieldset>
        </xsl:if>
    </xsl:template>

    <xsl:template name="filterControlsFilters">
        <!--<code>Current node: <xsl:value-of select="name()"/></code>-->
        <xsl:for-each select="./filters/filter">
            <!--<code><xsl:value-of select="name()"/></code>
            <p><code><xsl:value-of select="./@type"/></code></p>-->
            <xsl:choose>
                <xsl:when test="./@type='text'">
                    <span>
                        <xsl:attribute name="class">
                            <xsl:value-of select="'textFilter'"/>
                        </xsl:attribute>
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat(../../@customName, ./@label, 'Filter')"/>
                        </xsl:attribute>
                        <label>
                            <xsl:attribute name="for">
                                <xsl:value-of select="concat(./@label, 'Filter')"/>
                            </xsl:attribute>
                            <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                                          concat(../../@customName, ./@label))"/>
                        </label>
                        <xsl:element name="input">
                            <xsl:attribute name="type">text</xsl:attribute>
                            <xsl:attribute name="size">20</xsl:attribute>
                            <xsl:attribute name="maxlength">512</xsl:attribute>
                            <xsl:attribute name="id">
                                <xsl:value-of select="concat(./@label, 'Filter')"/>
                            </xsl:attribute>
                            <xsl:attribute name="name">
                                <xsl:value-of select="./@label"/>
                            </xsl:attribute>
                            <xsl:attribute name="value">
                                <xsl:value-of select="./@value"/>
                            </xsl:attribute>
                        </xsl:element>
                    </span>
                </xsl:when>
                <xsl:when test="(./@type='select') or (./@type='compare')">
                    <span>
                        <xsl:attribute name="class">
                            <xsl:value-of select="'selectFilter'"/>
                        </xsl:attribute>
                        <xsl:attribute name="id">
                            <xsl:value-of select="concat(../../@customName, ./@label, 'Filter')"/>
                        </xsl:attribute>
                        <label>
                            <xsl:attribute name="for">
                                <xsl:value-of select="concat(./@label, 'Filter')"/>
                            </xsl:attribute>
                            <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                                          concat(../../@customName, ./@label))"/>
                        </label>
                        <xsl:element name="select">
                            <xsl:attribute name="size">1</xsl:attribute>
                            <xsl:attribute name="id">
                                <xsl:value-of select="concat(./@label, 'Filter')"/>
                            </xsl:attribute>
                            <xsl:attribute name="name">
                                <xsl:value-of select="./@label"/>
                            </xsl:attribute>
                            <xsl:for-each select="./option">
                                <xsl:element name="option">
                                    <xsl:attribute name="value">
                                        <xsl:value-of select="./@label"/>
                                    </xsl:attribute>
                                    <xsl:if test="./@label = ../@selected">
                                        <xsl:attribute name="selected">selected</xsl:attribute>
                                    </xsl:if>
                                    <xsl:choose>
                                        <xsl:when test="./@label='--ALL--'">
                                            <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                                                          concat(../../../@customName, ../@label, 'All'))"/>
                                        </xsl:when>
                                        <xsl:otherwise>
                                            <xsl:choose>
                                                <xsl:when test="(../@type='compare') or (./@valueType = 'text')">
                                                    <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                                                          concat(../../../@customName, ../@label, ./@label))"/>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <xsl:value-of select="./@label"/>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:otherwise>
                                    </xsl:choose>
                                </xsl:element>
                            </xsl:for-each>
                        </xsl:element>
                    </span>
                </xsl:when>
                <xsl:when test="(./@type='categoryFilter')">
                    <xsl:variable name="filterId">
                        <xsl:value-of select="concat(../../@customName, ./@label, 'Filter')"/>
                    </xsl:variable>
                    <span>
                        <xsl:attribute name="class">
                            <xsl:value-of select="'categoryFilter'"/>
                        </xsl:attribute>
                        <xsl:attribute name="id">
                            <xsl:value-of select="$filterId"/>
                        </xsl:attribute>
                        <script type="text/javascript">
                            function addSelectedCategory(catFilterId, selectedCategory, selectedCategoryId) {
                            //Add span to selected catagories area, including close/remove function
                            var elem = $(document.createElement('span'));
                            elem.attr("id", catFilterId + "Selected" + selectedCategoryId);
                            elem.append(selectedCategory);
                            var closeElem = $(document.createElement('a'));
                            closeElem.attr("href", "#");
                            closeElem.append("&Cross;");
                            closeElem.click(function() {
                            removeSelectedCategory(elem, catFilterId, selectedCategoryId);
                            });
                            elem.append(closeElem);
                                
                            $("#" + catFilterId + " span.selectedCategories").append(elem);
                                
                            var newVal = $("#" + catFilterId + " input.selectedCategories").val() + selectedCategoryId + ";";
                            $("#" + catFilterId + " input.selectedCategories").val(newVal);
                            }
                            
                            function removeSelectedCategory(elem, catFilterId, category) {
                            //alert("close\ncatFilterId = " + catFilterId);
                                
                            //var category = $(elem.text();
                                
                            //alert("category =" + category);
                            
                            //alert("catFilterId = " + catFilterId); 
                            //alert("oldVal = '" + $("#" + catFilterId + " input.selectedCategories").val() + "'\n"
                            //    + "newVal = '" + $("#" + catFilterId + " input.selectedCategories").val().replace(category + ";", "") + "'");
                               
                            var newVal = $("#" + catFilterId + " input.selectedCategories").val().replace(category + ";", "");
                            $("#" + catFilterId + " input.selectedCategories").val(newVal);
                                
                            elem.remove();
                            return false;
                            }
                        </script>
                        <label>
                            <xsl:attribute name="for">
                                <xsl:value-of select="concat(./@label, 'Filter')"/>
                            </xsl:attribute>
                            <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                                          concat(../../@customName, ./@label))"/>
                        </label>
                        <xsl:choose>
                            <xsl:when test="./multiple = 'true'">
                                <span class="category-input">
                                    <xsl:element name="input">
                                        <xsl:attribute name="type">text</xsl:attribute>
                                        <xsl:attribute name="size">42</xsl:attribute>
                                        <xsl:attribute name="class">selectedCategories</xsl:attribute>
                                        <xsl:attribute name="maxlength">1024</xsl:attribute>
                                        <xsl:attribute name="id">
                                            <xsl:value-of select="concat(./@label, 'Filter')"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="name">
                                            <xsl:value-of select="./@label"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="./searchString"/>
                                        </xsl:attribute>
                                        <xsl:attribute name="style">
                                            <xsl:value-of select="'display:none'"/>
                                        </xsl:attribute>
                                    </xsl:element>
                                    <span class="selectedCategories">
                                        <xsl:attribute name="id">
                                            <xsl:value-of select="concat(./@label, 'SelectedKeywords')"/>
                                        </xsl:attribute>
                                        <xsl:for-each select="./categories//category[@selected='selected']">
                                            <span>
                                                <xsl:attribute name="id">
                                                    <!--<xsl:value-of select="concat(../../../../@customName, ../../@label, 'Filter', 'Selected', translate(current(), ' ', '_'))"/>-->
                                                    <xsl:value-of select="concat($filterId, 'Selected', @id)"/>
                                                </xsl:attribute>
                                                <xsl:value-of select="."/>
                                                <a>
                                                    <xsl:attribute name="href">
                                                        <xsl:value-of select="'#'"/>
                                                    </xsl:attribute>
                                                    <xsl:text>&Cross;</xsl:text>
                                                </a>
                                                <script type="text/javascript">
                                                    $("#<xsl:value-of select="concat($filterId, 'Selected', @id)"/>").click(function() {
                                                    removeSelectedCategory($("#<xsl:value-of select="concat($filterId, 'Selected', @id)"/>"), 
                                                    "<xsl:value-of select="$filterId"/>", 
                                                    "<xsl:value-of select="concat(@id, ../separator)"/>");
                                                    return false;
                                                    });
                                                </script>
                                            </span>
                                        </xsl:for-each>
                                    </span>
                                    <select size="1" class="availableCategories">
                                        <xsl:attribute name="id">
                                            <xsl:value-of select="concat(./@label, 'AvailableCategories')"/>
                                        </xsl:attribute>
                                        <option value=""></option>
                                        <xsl:for-each select="./categories/*">
                                            <xsl:choose>
                                                <xsl:when test="name() = 'categoryGroup'">
                                                    <optgroup>
                                                        <xsl:attribute name="label">
                                                            <xsl:value-of select="./@label"/>
                                                        </xsl:attribute>
                                                        <xsl:for-each select="./category">
                                                            <option>
                                                                <xsl:if test="./@selected = 'selected'">
                                                                    <xsl:attribute name="selected">selected</xsl:attribute>
                                                                </xsl:if>
                                                                <xsl:attribute name="value">
                                                                    <xsl:value-of select="./@id"/>
                                                                </xsl:attribute>
                                                                <xsl:value-of select="."/>
                                                            </option>
                                                        </xsl:for-each>
                                                    </optgroup>
                                                </xsl:when>
                                                <xsl:otherwise>
                                                    <option>
                                                        <xsl:attribute name="value">
                                                            <xsl:value-of select="./@id"/>
                                                        </xsl:attribute>
                                                        <xsl:value-of select="."/>
                                                    </option>
                                                </xsl:otherwise>
                                            </xsl:choose>
                                        </xsl:for-each>
                                    </select>
                                </span>
                                <script type="text/javascript">
                                    $("#<xsl:value-of select="concat(./@label, 'AvailableCategories')"/>").change(function() {
                                    var catFilterId = "<xsl:value-of select="$filterId"/>";
                                    var selectedCategory = $("#<xsl:value-of select="concat(./@label, 'AvailableCategories')"/> option:selected").text();
                                    var selectedCategoryId = $("#<xsl:value-of select="concat(./@label, 'AvailableCategories')"/> option:selected").attr("value");
                                    
                                    addSelectedCategory(catFilterId, selectedCategory, selectedCategoryId);
                                        
                                    $("#<xsl:value-of select="concat(./@label, 'AvailableCategories')"/>").val("");
                                    });
                                    
                                </script>
                            </xsl:when>
                            <xsl:otherwise>
                                <select size="1">
                                    <xsl:attribute name="id">
                                        <xsl:value-of select="concat(./@label, 'Filter')"/>
                                    </xsl:attribute>
                                    <xsl:attribute name="name">
                                        <xsl:value-of select="./@label"/>
                                    </xsl:attribute>
                                    <option value="">
                                        <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                                                       concat(../../@customName,./@label,'All'))"/>
                                    </option>
                                    <xsl:for-each select="./categories/category">
                                        <option>
                                            <xsl:if test="./@selected = 'selected'">
                                                <xsl:attribute name="selected">selected</xsl:attribute>
                                            </xsl:if>
                                            <xsl:attribute name="value">
                                                <xsl:value-of select="./@id"/>
                                            </xsl:attribute>
                                            <xsl:value-of select="."/>
                                        </option>
                                    </xsl:for-each>
                                </select>
                            </xsl:otherwise>
                        </xsl:choose>
                        
                    </span>
                </xsl:when>
            </xsl:choose>
        </xsl:for-each>
    </xsl:template>

    

    <xsl:template name="filterControlsSortFields">
        <!-- <xsl:if test="count(./controls/sortFields/sortField) &gt; 1">-->
        <!--   <code>sortFields</code>-->
        <span class="sortBy">
            <label>
                <xsl:attribute name="for">
                    <xsl:value-of select="concat(../../@customName, 'SortBy')"/>
                </xsl:attribute>
                <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                               concat(./@customName, 'SortBy'))"/>
            </label>
            <select>
                <xsl:attribute name="size">1</xsl:attribute>
                <xsl:attribute name="id">
                    <xsl:value-of select="concat(../../@customName, 'SortBy')"/>
                </xsl:attribute>
                <xsl:attribute name="name">sort</xsl:attribute>	     
                <xsl:for-each select="./sortFields">
                    <xsl:for-each select="./sortField">
                        <xsl:element name="option">
                            <xsl:attribute name="value">
                                <xsl:value-of select="./@label"/>
                            </xsl:attribute>
                            <xsl:if test="./@label = ../@sortBy">
                                <xsl:attribute name="selected">selected</xsl:attribute>
                            </xsl:if>
                            <xsl:value-of select="foundry:get-static-text('filter-controls',
                                                                          concat(../../@customName, 'SortBy', ./@label))"/>
                        </xsl:element>
                    </xsl:for-each>
                </xsl:for-each>
            </select>
        </span>
        <!-- </xsl:if> -->
    </xsl:template>

</xsl:stylesheet>
