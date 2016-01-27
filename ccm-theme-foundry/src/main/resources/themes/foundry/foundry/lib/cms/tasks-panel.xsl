<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet [<!ENTITY nbsp '&#160;'>]>

<!-- 
    Copyright: 2006, 2007, 2008 Sören Bernstein
  
    This file is part of Mandalay.

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

<!-- DE
  Hier werden die cmsTaskPanel verarbeitet 
-->

<!-- EN
  Processing cmsTaskPanel
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet   xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                  xmlns:bebop="http://www.arsdigita.com/bebop/1.0" 
                  xmlns:cms="http://www.arsdigita.com/cms/1.0"
                  xmlns:foundry="http://foundry.libreccm.org"
                  xmlns:nav="http://ccm.redhat.com/navigation" 
                  exclude-result-prefixes="xsl bebop cms foundry nav" 
                  version="2.0">
  
    <!-- EN Create a list of tasks -->
    <xsl:template match="cms:tasksPanel">
        <xsl:choose>
            <xsl:when test="count(cms:tasksPanelTask) = 0">
                <xsl:value-of select="foundry:get-static-text('cms', 'taskPanel/noTasks')"/>
            </xsl:when>
            <xsl:otherwise>
                <table>
                    <thead>
                        <tr>
                            <xsl:apply-templates select="bebop:link | bebop:label" mode="tableHeadCell"/>
                        </tr>
                    </thead>
                    <tbody>
                        <xsl:apply-templates select="cms:tasksPanelTask"/>
                    </tbody>
                </table>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
  
    <!-- EN Creates an entry to the list -->
    <xsl:template match="cms:tasksPanelTask">
        <tr>
            <td>
                <a>
                    <xsl:attribute name="href" 
                                   select="foundry:parse-link(concat(./@sectionPath, 
                                                                     '/admin/item.jsp?item_id=', 
                                                                      ./@itemID))"/>
                    <xsl:value-of select="./@pageTitle"/>
                </a>
            </td>
            <td>
                <a title="{./@taskDescription}">
                    <xsl:attribute name="href"
                                   select="foundry:parse-link(./@actionURL)"/>
                    <xsl:value-of select="./@taskLabel"/>
                </a>
            </td>
            <td>
                <xsl:value-of select="./@dueDate"/>
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="./@status = '1'">
                        <xsl:value-of select="foundry:get-static-text('cms', 
                                                                      'taskPanel/lockedByYou')"/>
                    </xsl:when>
                    <xsl:when test="./@status = '2'">
                        <xsl:value-of select="foundry:get-static-text('cms', 
                                                                      'taskPanel/notLocked')"/>
                    </xsl:when>
                    <xsl:when test="./@status = '3'">
                        <xsl:value-of select="foundry:get-static-text('cms', 
                                                                      'taskPanel/lockedBySomeoneElse')"/>
                    </xsl:when>
                </xsl:choose>
            </td>
            <td>
                <xsl:choose>
                    <xsl:when test="./@assignee">
                        <xsl:value-of select="./@assignee"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="foundry:get-static-text('cms', 
                                                                      'taskPanel/notAssigned')"/>
                    </xsl:otherwise>
                </xsl:choose>
            </td>
            <td>
                <xsl:value-of select="./@processLabel"/>
            </td>
        </tr>
    </xsl:template>
  
    <!-- EN Special mode for a link or label in a table head -->
    <xsl:template match="bebop:link | bebop:label" mode="tableHeadCell">
        <th>
            <xsl:apply-templates select="."/>
        </th>
    </xsl:template>
</xsl:stylesheet>
