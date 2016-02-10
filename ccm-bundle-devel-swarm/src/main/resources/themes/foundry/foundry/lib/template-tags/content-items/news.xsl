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
                xmlns:foundry="http://foundry.libreccm.org"
                xmlns:nav="http://ccm.redhat.com/navigation"
                xmlns:ui="http://www.arsdigita.com/ui/1.0"
                exclude-result-prefixes="xsl xs bebop cms foundry nav ui"
                version="2.0">

    <foundry:doc-file>
        <foundry:doc-file-title>Tags for ccm-cms-types-newsitem</foundry:doc-file-title>
        <foundry:doc-file-desc>
            <p>
                Tags for displaying the special properties of a NewsItem.
            </p>
        </foundry:doc-file-desc>
    </foundry:doc-file>
    
    <foundry:doc section="user" type="template-tag">
        <foundry:doc-desc>
            <p>
                Outputs the the date of a news. The <code>news-date</code> must contain at least 
                one <code>format</code> element. The <code>format</code> element encloses the
                format definition for the specific language or the default format. The language
                for which a format is used is provided using the <code>lang</code> attribute at the 
                <code>format</code> element. The default format has a <code>default</code> attribute
                with the value <code>true</code>. An example:
            </p>
            <pre>
                &lt;news-date&gt;
                    &lt;format default="true"&gt;
                        &lt;iso-date/&gt;
                    &lt;/format&gt;
                    &lt;format lang="de"&gt;
                        &lt;day zero="true"/&gt;.&lt;month zero="true"/&gt;.&lt;year/&gt;
                    &lt;/format&gt;
                &lt;/news-date&gt;
            </pre>
            <p>
                In this example a visitor with a browser using <em>German</em> as default locale 
                will see the news date in the date format that common in Germany 
                (<code>dd.mm.yyyy</code>). For all other languages, the default format is used.
                In this case the <code>iso-format</code> is used.
            </p>
        </foundry:doc-desc>
    </foundry:doc>
    <xsl:template match="/content-item-layout//news-date">
        <xsl:param name="contentitem-tree" tunnel="yes"/>
        
        <xsl:choose>
            <xsl:when test="$contentitem-tree/newsDate">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" select="$contentitem-tree/newsDate"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="$contentitem-tree/nav:attribute[@name = 'newsDate']">
                <xsl:call-template name="foundry:format-date">
                    <xsl:with-param name="date-elem" 
                                    select="$contentitem-tree/nav:attribute[@name = 'newsDate']"/>
                    <xsl:with-param name="date-format" select="./date-format"/>
                </xsl:call-template>
            </xsl:when>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>
