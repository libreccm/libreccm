<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>
                      <!ENTITY shy '&#173;'>]>
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

    <xsl:template match="show-cms-global-navigation">
        <!--<div class="cms-global-navigation">-->
            <!--<xsl:choose>
                <xsl:when test="*">
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:apply-templates select="$data-tree/cms:globalNavigation"/>
                </xsl:otherwise>
            </xsl:choose>-->
            <!--<pre>
                show-cms-global-navigation
                <xsl:value-of select="count(./show-change-password-link)"/>
            </pre>-->
            <xsl:if test="$data-tree/cms:globalNavigation or $data-tree/ui:userBanner">
               <xsl:apply-templates />
           </xsl:if>
       <!--</div>-->
    </xsl:template>

    <xsl:template match="show-cms-global-navigation//show-contentcenter-link">
        <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:contentCenter"/>    
    </xsl:template>
  
    <xsl:template match="show-cms-global-navigation//show-admincenter-link">
        <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:adminCenter"/>
    </xsl:template>
  
    <xsl:template match="show-cms-global-navigation//show-workspace-link">
        <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:workspace"/>
    </xsl:template>
                            
    <xsl:template match="show-cms-global-navigation//show-change-password-link">                            
       <xsl:choose>
            <xsl:when test="$data-tree/cms:globalNavigation">
               <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:changePassword"/>
            </xsl:when>
            <xsl:when test="$data-tree/ui:userBanner">
               <span class="cms-global-navigation-change-password">
                    <a href="{$data-tree/ui:userBanner/@changePasswordURL}">
                        <xsl:apply-templates select="$data-tree/ui:userBanner/@changePasswordLabel"/>
                    </a>
                </span>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
  
    <xsl:template match="show-cms-global-navigation//show-logout-link">
       <xsl:choose>
            <xsl:when test="$data-tree/cms:globalNavigation">
                <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:signOut"/>
            </xsl:when>
            <xsl:when test="$data-tree/ui:userBanner">
                <span class="cms-global-navigation-signout">
                    <a href="{$data-tree/ui:userBanner/@logoutURL}">
                        <xsl:apply-templates select="$data-tree/ui:userBanner/@signoutLabel"/>
                    </a>
                </span>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
  
    <xsl:template match="show-cms-global-navigation/show-help-link">
        <xsl:apply-templates select="$data-tree/cms:globalNavigation/cms:help"/>
    </xsl:template>

    <xsl:template match="show-cms-global-navigation/show-preview-link">
        <span class="cms-preview">
            <xsl:apply-templates select="$data-tree/bebop:link[@id='preview_link']"/>
        </span>
    </xsl:template>
    
    <xsl:template match="cms:contentCenter">
        <span class="cms-global-navigation-contentcenter">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>
  
    <xsl:template match="cms:adminCenter">
        <span class="cms-global-navigation-admin-center">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>
  
    <xsl:template match="cms:workspace">
        <span class="cms-global-navigation-workspace">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template match="cms:changePassword">
        <span class="cms-global-navigation-change-password">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template match="cms:signOut">
        <span class="cms-global-navigation-signout">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template match="cms:help">
        <span class="cms-global-navigation-help">
            <xsl:call-template name="cms:globalNavigationEntry"/>
        </span>
    </xsl:template>

    <xsl:template name="cms:globalNavigationEntry">
       <a href="{./@href}">
            <xsl:value-of select="./@title"/>
        </a>
    </xsl:template>
    
    <!-- _______________________________________________________________________________________ -->
    
    <xsl:template match="show-cms-content-view-menu">
        <xsl:param name="layoutTree" select="."/>
    
        <xsl:variable name="layout" 
                      select="'horizontal'"/>
            
        <xsl:choose>
            <xsl:when test="$layout = 'horizontal'">
                <xsl:apply-templates select="$data-tree//*[contains(@class, 'cmsContentViewMenu')]"/>
            </xsl:when>
            <xsl:otherwise>
                <ul>
                    <xsl:for-each select="$data-tree//*[contains(@class, 'cmsContentViewMenu')]">
                        <li>
                            <xsl:apply-templates/>
                        </li>
                    </xsl:for-each>
                </ul>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <!-- _______________________________________________________________________________________ -->
    
    <xsl:template match="show-content-type">
        <xsl:if test="$data-tree/bebop:contentType">
            <span id="contenttype">
                <xsl:value-of select="$data-tree/bebop:contentType"/>
            </span>
        </xsl:if>
    </xsl:template>
    
    <!-- _______________________________________________________________________________________ -->
    
    <xsl:template match="cms-greeting">
        <xsl:apply-templates/>
    </xsl:template>
    
    <xsl:template match="cms-greeting//cms-greeting-static-text">
        <xsl:value-of select="foundry:get-internal-static-text('cms', 'greeting')"/>
    </xsl:template>
    
    <xsl:template match="cms-greeting//user-name">
        <xsl:choose>
            <xsl:when test="$data-tree/@name">
                <xsl:value-of select="$data-tree/@name"/>
            </xsl:when>
            <xsl:when test="$data-tree//ui:userBanner/@screenName">
                <xsl:value-of select="concat($data-tree//ui:userBanner/@givenName, ' ', 
                                             $data-tree//ui:userBanner/@familyName)"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
    
    <!-- _______________________________________________________________________________________ -->
    <!-- Begin of category step -->
    <xsl:template match="cms:categoryStep">
        <h2>
            <xsl:value-of select="foundry:get-internal-static-text('cms', 'category-step/header')"/>
        </h2>
        <xsl:apply-templates/>
    </xsl:template>
  
    <!--  Show all category roots -->
    <xsl:template match="cms:categoryRoots">
        <dl class="cmsCategoryRoots">            
            <xsl:apply-templates>
                <xsl:sort select="./@name" data-type="text"/>
            </xsl:apply-templates>
        </dl>
    </xsl:template>
    
    <!--  Show selected categories and a link zu select more -->
    <xsl:template match="cms:categoryRoot">
    
        <xsl:variable name="cat-name" select="@name"/>

        <dt class="cmsCategoryRoot">
            <xsl:value-of select="@name"/>
        </dt>
        <dd>
            <!--  Using two kinds of links. First, non-javascript link form addAction. Second, a 
            javascript link using ajax to manipulate categories. For the second method, the 
            onClick-event handler is overwriting the html link if javscript is running. -->
            <a href="{@addAction}" onclick="this.href='{@addJSAction}';">
                <xsl:value-of select="foundry:get-internal-static-text('cms', 'category-step/add-categories')"/>
            </a>
            <xsl:choose>
                <xsl:when test="count(../../cms:itemCategories/cms:itemCategory[starts-with(@path, $cat-name)]) = 0">
                    <div>
                        <xsl:value-of select="foundry:get-internal-static-text('cms', 
                                                                               'category-step/no-categories')"/>
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    <ul>
                        <xsl:apply-templates select="../../cms:itemCategories/cms:itemCategory[starts-with(@path, $cat-name)]" 
                                             mode="list">
                            <xsl:sort select="@path"/>
                        </xsl:apply-templates>
                    </ul>
                </xsl:otherwise>
            </xsl:choose>
        </dd>
        <br />
    </xsl:template>

    <!--  Show an assigned category and a link to remove this assignment. Because this template is meant
    to be called only by cms:categoryRoot, there is a mode="list" added. Otherwise there will be
    a duplicated list below the list of category roots. -->
    <xsl:template match="cms:itemCategory" mode="list">
        <xsl:variable name="show-delete-link" 
                      select="'false'"/>
        
        <li>
            <xsl:choose>
                <xsl:when test="$show-delete-link = 'true' and @deleteAction">
                    <a href="{@deleteAction}">
                        <xsl:attribute name="title" 
                                       select="foundry:get-internal-static-text('cms', 
                                                                                'category-step/remove-category')"/>
                        <img alt="[X]">
                            <xsl:attribute name="src"
                                           select="foundry:gen-path('images/cms/categoryDelete.png', 
                                                                    'internal')"/>
                        </img>
                        &nbsp;
                        <xsl:value-of select="@path"/>
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@path"/>
                </xsl:otherwise>
            </xsl:choose>
        </li>
    </xsl:template>
    
    <!--  Show categories to select -->
    <xsl:template match="cms:categoryWidget">
        <!--    <script type="text/javascript" src="/assets/prototype.js"/> -->
        <!--<script type="text/javascript" src="{$context-prefix}/assets/jquery.js"/> -->
        <!--<script type="text/javascript" src="{$theme-prefix}/includes/cms/category-step.js"/> -->
        <xsl:choose>
            <xsl:when test="./@mode = 'javascript'">
                <ul>
                    <xsl:apply-templates select="cms:category" mode="javascript"/>
                </ul>
                <xsl:apply-templates select="cms:selectedCategories"/>
                <xsl:apply-templates select="cms:selectedAncestorCategories"/>
                <script type="text/javascript">
                    colorAncestors();
                </script>
            </xsl:when>
            <xsl:otherwise>
                <select name="./@name" size="30" multiple="multiple">
                    <xsl:apply-templates mode="plain">
                        <xsl:sort data-type="number" select="./@sortKey"/>
                    </xsl:apply-templates>
                </select>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    
    <xsl:template match="cms:selectedCategories">
        <select id="catWdHd" 
                name="{../@name}" 
                size="5" 
                multiple="multiple" 
                style="display: none">
            <xsl:apply-templates select="cms:category" mode="hidden"/>
        </select>
    </xsl:template>
    
    <xsl:template match="cms:selectedAncestorCategories">
        <input id = "selectedAncestorCategories" 
               name = "selectedAncestorCategories"
               value = "{string-join(./cms:category/@id, ',')}"
               type = "hidden" />
               
    </xsl:template>
    
    <xsl:template match="cms:category" mode="hidden">
        <option value="{./@id}">
            <xsl:value-of select="./@id"/>
        </option>
        <xsl:apply-templates select="cms:category" mode="hidden"/>
    </xsl:template>
  
    <!--  Toggle parts of the category tree with AJAX -->
    <!--  cms:category is using to different syntax. The other one is located in cmsSummary. -->
    <xsl:template match="cms:category" mode="javascript">
    
        <xsl:variable name="only-one-level">
            <xsl:choose>
                <xsl:when test="not(@root = '1' or cms:category/@expand = 'all') 
                                and not(//cms:categoryWidget)">
                    <xsl:value-of select="'yes'"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="'no'"/>
                </xsl:otherwise>
            </xsl:choose>
        </xsl:variable>
        <li id="catSelf{@id}" class="notSelectedAncestorCategory">
            <!--  Treefunctions (expand and collapse) -->
            <xsl:variable name="tree-toogle-mode">
                <xsl:choose>
                    <xsl:when test="@root = '1' or cms:category/@expand = 'all'">
                        <xsl:text>catBranchToggle</xsl:text>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:text>catToggle</xsl:text>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:variable>
            <xsl:variable name="selCats">
                <xsl:for-each select="//cms:categoryWidget/cms:selectedCategories/cms:category">
                    <xsl:choose>
                        <xsl:when test="position() != last()">
                            <xsl:value-of select="concat(@id, ', ')"/>
                        </xsl:when>
                        <xsl:otherwise >
                            <xsl:value-of select="@id"/>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:variable>
            <xsl:choose>
                <xsl:when test="cms:category and $only-one-level = 'no'">
                    <a id="catTreeToggleLink{@node-id}" href="#" 
                       onclick="{$tree-toogle-mode}('{@node-id}', '{$selCats}');">
                        <img id="catTreeToggleImage{@node-id}" alt="-">
                            <xsl:attribute name="src" 
                                           select="foundry:gen-path('images/cms/categoryCollapse.png', 
                                                                    'internal')"/>
                        </img>
                    </a>
                </xsl:when>
                <xsl:when test="(@root = '1' and not(cms:category)) 
                                 or ($only-one-level = 'yes' and cms:category)">
                    <a id="catTreeToggleLink{@node-id}" 
                       href="#" 
                       onclick="{$tree-toogle-mode}('{@node-id}', '{$selCats}');">
                        <img id="catTreeToggleImage{@node-id}" alt="+">
                            <xsl:attribute name="src"
                                           select="foundry:gen-path('images/cms/categoryExpand.png', 
                                                                    'internal')"/>
                        </img>  
                    </a>
                </xsl:when>
                <xsl:otherwise>
                    <img id="catTreeToggleImage{@node-id}" alt=" ">
                        <xsl:attribute name="src"
                                       select="foundry:gen-path('images/cms/categoryNode.png', 
                                                                'internal')"/>
                    </img>
                </xsl:otherwise>
            </xsl:choose>
            &nbsp;
      
            <!--  Choose categories -->
            <xsl:choose>
                <xsl:when test="@isSelected = '1'">
                    <a id="catToggleLink{@id}" href="#" onclick="catDeselect({@id});">
                        <img id="catToggleImage{@id}" alt="[X]">
                            <xsl:attribute name="src" 
                                           select="foundry:gen-path('images/cms/categorySelected.gif',
                                                                    'internal')"/>
                        </img>
                    </a>
                </xsl:when>
                <xsl:when test="@isAbstract = '1'">
                    <img id="catToggleImage{@id}" alt="   ">
                        <xsl:attribute name="src" 
                                       select="foundry:gen-path('images/cms/categoryAbstract.gif',
                                                                'internal')"/>
                    </img>
                </xsl:when>
                <xsl:otherwise>
                    <a id="catToggleLink{@id}" href="#" onclick="catSelect({@id});">
                        <img id="catToggleImage{@id}" alt="[ ]" title="Select">
                            <xsl:attribute name="src" 
                                           select="foundry:gen-path('images/cms/categoryUnselected.gif',
                                                                    'internal')"/>
                        </img>
                    </a>
                </xsl:otherwise>
            </xsl:choose>
      &nbsp;
      
            <!-- DE Name der Kategorie -->
            <!--  category name -->
            <xsl:value-of select="@name"/>
            <ul id="catBranch{@node-id}">
                <xsl:if test="$only-one-level = 'yes'">
                    <xsl:attribute name="style">
                        <xsl:value-of select="'display: none;'"/>
                    </xsl:attribute>
                </xsl:if>
                <xsl:if test="./cms:category">
                    <xsl:choose>
                        <xsl:when test="@order='sortKey'">
                            <xsl:apply-templates mode="javascript">
                                <xsl:sort data-type="number" select="@sortKey"/>
                            </xsl:apply-templates>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates mode="javascript">
                                <xsl:sort data-type="text" select="@name"/>
                            </xsl:apply-templates>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:if>
            </ul>
        </li>
    </xsl:template>
  
    <!-- DE Kategorieeintrag ohne Javascript -->
    <!--  Category entry without javascript -->
    <!-- DE cms:category wird in zwei verschiedenen Syntax verwendet. Die andere ist in
    cmsSummary zu finden. -->
    <!--  cms:category is using to different syntax. The other one is located in cmsSummary. -->
    <xsl:template match="cms:category" mode="plain">
        <option value="{@id}">
            <xsl:if test="@isSelected = '1'">
                <xsl:attribute name="selected">
                    selected
                </xsl:attribute>
            </xsl:if>
            <xsl:if test="@isAbstract = '1' or @isSelected = '1'">
                <xsl:attribute name="disabled">
                    disabled
                </xsl:attribute>
            </xsl:if>
            <xsl:value-of select="@fullname"/>
        </option>
        <xsl:apply-templates mode="plain">
            <xsl:sort data-type="number" select="@sortKey"/>
        </xsl:apply-templates>
    </xsl:template>

    <xsl:template match="cms:emptyPage[@title='childCategories']">
        <xsl:choose>
            <xsl:when test="cms:category/@order='sortKey'">
                <xsl:apply-templates select="cms:category/cms:category" mode="javascript">
                    <xsl:sort data-type="number" select="@sortKey"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="cms:category/cms:category" mode="javascript">
                    <xsl:sort data-type="text" select="@name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
  
    <xsl:template match="cms:emptyPage[@title='autoCategories']">
        <xsl:choose>
            <xsl:when test="cms:category/@order='sortKey'">
                <xsl:apply-templates select="cms:category" mode="javascript" >
                    <xsl:sort data-type="number" select="@sortKey"/>
                </xsl:apply-templates>
            </xsl:when>
            <xsl:otherwise>
                <xsl:apply-templates select="cms:category" mode="javascript" >
                    <xsl:sort data-type="text" select="@name"/>
                </xsl:apply-templates>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template match="cms:sortableList">
        <div class="cmsSortableList" id="{./@id}">
            <ul>
                <xsl:apply-templates mode="sortableList"/>
            </ul>
        </div>
    </xsl:template>  
  
    <!-- DE Spezielles bebop:cell für die sortierbaren Listen, daß die Pfeile mit erzeugt -->
    <!-- EN A special bebop:cell for sortable list, which will create sorting buttons -->
    <xsl:template match="bebop:cell" mode="sortableList">
        <li>
            <xsl:if test="@configure">
                <span class="sortButtons">
                    <span class="sortButtonUp">
                        <xsl:choose>
                            <xsl:when test="@prevURL">
                                <a href="{@prevURL}">
                                    <img alt="&#x2B06;" 
                                         src="{foundry:gen-path('images/cms/arrowUp.gif', 
                                                               'internal')}"/>
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                &nbsp;
                            </xsl:otherwise>
                        </xsl:choose>
                    </span>
                    <span class="sortButtonDown">
                        <xsl:choose>
                            <xsl:when test="@nextURL">
                                <a href="{@nextURL}">
                                    <img alt="&#x2B07;" 
                                         src="{foundry:gen-path('images/cms/arrowDown.gif',
                                                                'internal')}"/>
                                </a>
                            </xsl:when>
                            <xsl:otherwise>
                &nbsp;
                            </xsl:otherwise>
                        </xsl:choose>
                    </span>
                </span>
            </xsl:if>
            <xsl:apply-templates/>
        </li>
    </xsl:template>
</xsl:stylesheet>
