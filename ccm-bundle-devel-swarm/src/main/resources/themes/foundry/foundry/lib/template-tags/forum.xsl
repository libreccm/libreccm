<?xml version="1.0" encoding="UTF-8"?>
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
                xmlns:forum="http://www.arsdigita.com/forum/1.0"
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">
    
    <foundry:doc-file>
        <foundry:doc-file-title>Forum</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                This file defines several tags for displaying a Forum 
                (ccm-forum).
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Show the name of the forum.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="show-forum-name">
        <xsl:value-of select="$data-tree/forum:name"/>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Applies the enclosed tags only if there is a forum introduction.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="if-forum-introducation">
        <xsl:if test="string-length($data-tree/forum:introducation) &gt; 0">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Shows the introducation text of a forum.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="show-forum-introduction">
        <xsl:value-of select="$data-tree/forum:introduction"/>
    </xsl:template>
    
    <xsl:template match="forum-tabs">
        <xsl:if test="$data-tree/forum:forum">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="forum-tabs//forum-tab">
        <xsl:variable name="tab-layout-tree" select="./*"/>
        
        <xsl:for-each select="$data-tree/forum:forum/forum:forumMode">
            <xsl:apply-templates select="$tab-layout-tree">
                <xsl:with-param name="href" 
                                tunnel="yes" 
                                select="./@url"/>
                <xsl:with-param name="label"
                                tunnel="yes"
                                select="./@label"/>
                <xsl:with-param name="class"
                                tunnel="yes"
                                select="if (./@selected = 1)
                                   then 'selected'
                                   else ''"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="forum-tabs//forum-tab//tab-label">
        <xsl:param name="label" tunnel="yes"/>
        
        <xsl:value-of select="$label"/>
    </xsl:template>
    
    <xsl:template match="forum-current-tab">
        <xsl:variable name="forum-data-tree" select="$data-tree/forum:forum/*"/>
        
        <xsl:variable name="selected-tab" 
                      select="$data-tree/forum:forum/forum:forumMode[@selected = 1]/@mode"/>
        
        <xsl:apply-templates select="./forum-tab[@mode = $selected-tab]">
            <xsl:with-param name="forum-data-tree" 
                            tunnel="yes" 
                            select="$forum-data-tree"/>
        </xsl:apply-templates>
    </xsl:template>
    
    <xsl:template match="forum-options">
        <xsl:if test="$data-tree/forum:forum/forum:forumOptions">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="forum-options//forum-option">
        <xsl:variable name="option-layout-tree" select="./*"/>
        
        <xsl:for-each select="$data-tree/forum:forum/forum:forumOptions/bebop:link">
            <xsl:apply-templates select="$option-layout-tree">
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="./@href"/>
                <xsl:with-param name="label"
                                tunnel="yes"
                                select="./bebop:label"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="forum-options//forum-option//option-label">
        <xsl:param name="label" tunnel="yes"/>
        
        <xsl:value-of select="$label"/>
    </xsl:template>
    
    <xsl:template match="forum-threads">
        <xsl:if test="$data-tree/forum:forum/forum:threadList">
            <xsl:apply-templates/>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="forum-threads//paginator">
        
        <xsl:if test="($data-tree/forum:forum/forum:threadList/forum:paginator/@pageCount &gt; 1)
                      or ./@show = 'always'">
            <xsl:apply-templates>
                <xsl:with-param name="paginator-baseurl"
                                tunnel="yes"
                                select="$data-tree/forum:forum/forum:threadList/forum:paginator/@baseURL" />
                <xsl:with-param name="paginator-object-begin"
                                tunnel="yes"
                                select="$data-tree/forum:forum/forum:threadList/forum:paginator/@objectBegin" />
                <xsl:with-param name="paginator-object-count"
                                tunnel="yes"
                                select="$data-tree/forum:forum/forum:threadList/forum:paginator/@objectCount" />
                <xsl:with-param name="paginator-object-end"
                                tunnel="yes"
                                select="$data-tree/forum:forum/forum:threadList/forum:paginator/@objectEnd" />
                <xsl:with-param name="paginator-page-count"
                                tunnel="yes"
                                select="$data-tree/forum:forum/forum:threadList/forum:paginator/@pageCount" />
                <xsl:with-param name="paginator-page-number"
                                tunnel="yes"
                                select="$data-tree/forum:forum/forum:threadList/forum:paginator/@pageNumber" />
                <xsl:with-param name="paginator-page-param"
                                tunnel="yes"
                                select="$data-tree/forum:forum/forum:threadList/forum:paginator/@pageParam" />
                <xsl:with-param name="paginator-page-size"
                                tunnel="yes"
                                select="$data-tree/forum:forum/forum:threadList/forum:paginator/@pageSize" />
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the index of the first object shown on the current page. The value is 
                provided by the surrounding <code>paginator</code> element via a XSL parameter.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//object-begin">
        <xsl:param name="paginator-object-begin" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-begin != ''">
            <xsl:value-of select="$paginator-object-begin"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the index of the last object shown on the current page. The value is 
                provided by the surrounding <code>paginator</code> element via a XSL parameter.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//object-end">
        <xsl:param name="paginator-object-end" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-end != ''">
            <xsl:value-of select="$paginator-object-end"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the number of elements in list.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//object-count">
        <xsl:param name="paginator-object-count" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-object-count != ''">
            <xsl:value-of select="$paginator-object-count"/>
        </xsl:if>
    </xsl:template>

    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the number of pages.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//page-count">
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-count != ''">
            <xsl:value-of select="$paginator-page-count"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the number of the current page.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//current-page">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-number != ''">
            <xsl:value-of select="$paginator-page-number"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the size of page (the number of items on each page).
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//page-size">
        <xsl:param name="paginator-page-size" tunnel="yes" select="''"/>
        
        <xsl:if test="$paginator-page-size != ''">
            <xsl:value-of select="$paginator-page-size"/>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the URL to the previous page of the list for an enclosed <code>a</code> 
                element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//prev-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') and ($paginator-page-number &gt; 1)">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-number -1)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the URL to the next page of the list for an enclosed <code>a</code> 
                element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//next-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') 
                      and ($paginator-page-number &lt; $paginator-page-count)">
            <xsl:apply-templates>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-number + 1)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the URL to the first page of the list for an enclosed 
                <code>a</code>  element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//first-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') 
                       and ($paginator-page-number &gt; 1)">
            <xsl:apply-templates>
                <xsl:with-param name="href" 
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, '=1')"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Provides the URL to the last page of the list for an enclosed 
                <code>a</code> element.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="forum-threads//paginator//last-page-link">
        <xsl:param name="paginator-page-number" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-count" tunnel="yes" select="''"/>
        <xsl:param name="paginator-baseurl" tunnel="yes" select="''"/>
        <xsl:param name="paginator-page-param" tunnel="yes" select="''"/>
        
        <xsl:if test="($paginator-page-number != '') 
                      and ($paginator-page-number &lt; $paginator-page-count)">
            <xsl:apply-templates>
                <xsl:with-param name="href"
                                tunnel="yes"
                                select="concat($paginator-baseurl, 
                                               $paginator-page-param, 
                                               '=', 
                                               $paginator-page-count)"/>
            </xsl:apply-templates>
        </xsl:if>
    </xsl:template>
    
    <xsl:template match="forum-threads//forum-thread">
        <xsl:variable name="thread-layout-tree" select="./*"/>
        
        <xsl:for-each select="$data-tree/forum:forum/forum:threadList/forum:thread">
            <xsl:apply-templates select="$thread-layout-tree">
                <xsl:with-param name="thread" tunnel="yes" select="."/>
                <xsl:with-param name="href" tunnel="yes" select="./@url"/>
            </xsl:apply-templates>
        </xsl:for-each>
    </xsl:template>
    
    <xsl:template match="forum-threads//forum-thread//last-update">
        <xsl:param name="thread" tunnel="yes"/>
        
        
        <xsl:call-template name="foundry:format-date">
            <xsl:with-param name="date-elem" select="$thread/lastUpdate"/>
            <xsl:with-param name="date-format" select="./date-format"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="forum-threads//forum-thread//num-replies">
        <xsl:param name="thread" tunnel="yes"/>
        
        <xsl:value-of select="$thread/numReplies"/>
    </xsl:template>
    
    <xsl:template match="forum-threads//forum-thread//subject">
        <xsl:param name="thread" tunnel="yes"/>
        
        <xsl:value-of select="$thread/root/subject"/>
    </xsl:template>
    
    <xsl:template match="forum-threads//forum-thread//thread-body">
        <xsl:param name="thread" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes" select="$thread/root/body"/>
    </xsl:template>
    
    <xsl:template match="forum-threads//forum-thread//sent-date">
        <xsl:param name="thread" tunnel="yes"/>
        
        
        <xsl:call-template name="foundry:format-date">
            <xsl:with-param name="date-elem" select="$thread/root/sent"/>
            <xsl:with-param name="date-format" select="./date-format"/>
        </xsl:call-template>
    </xsl:template>
    
    <xsl:template match="forum-threads//forum-thread//author-screenname">
        <xsl:param name="thread" tunnel="yes"/>
        
        <xsl:value-of disable-output-escaping="yes" select="$thread/root/author/screenname"/>
    </xsl:template>
    
</xsl:stylesheet>