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
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="xsl bebop cms foundry nav ui"
                version="2.0">
    
    <xsl:template match="if-related-links-or-file-attachments">
        <xsl:if test="$data-tree/cms:contentPanel/cms:item/fileAttachments
                      or $data-tree/nav:greetingItem/cms:item/fileAttachments
                      or $data-tree/nav:greetingItem/cms:item/links
                      or $data-tree/cms:contentPanel/cms:item/links">
            
            <xsl:choose>
                <xsl:when test="($data-tree/cms:contentPanel/cms:item/fileAttachments
                                    or $data-tree/nav:greetingItem/cms:item/fileAttachments)
                                and ($data-tree/nav:greetingItem/cms:item/links
                                    or $data-tree/cms:contentPanel/cms:item/links)">
                    <xsl:apply-templates select="./both/*"/>
                </xsl:when>
                <xsl:when test="($data-tree/cms:contentPanel/cms:item/fileAttachments
                                    or $data-tree/nav:greetingItem/cms:item/fileAttachments)
                                and not ($data-tree/nav:greetingItem/cms:item/links
                                    or $data-tree/cms:contentPanel/cms:item/links)">
                    <xsl:apply-templates select="./files-only/*"/>
                </xsl:when>
                <xsl:when test="not ($data-tree/cms:contentPanel/cms:item/fileAttachments
                                    or $data-tree/nav:greetingItem/cms:item/fileAttachments)
                                and ($data-tree/nav:greetingItem/cms:item/links
                                    or $data-tree/cms:contentPanel/cms:item/links)">
                    <xsl:apply-templates select="./links-only/*"/>
                </xsl:when>
            </xsl:choose>
            
            <!--<xsl:apply-templates/>-->
        </xsl:if>
    </xsl:template>
    
</xsl:stylesheet>