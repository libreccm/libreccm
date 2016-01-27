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

<!-- This file was copied from Mandalay and edited to fit into Foundry -->

<!-- EN
  Processing tree structures. This is used to display the folder structur in content center.
-->

<!-- Autor: Sören Bernstein -->

<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" 
                xmlns:bebop="http://www.arsdigita.com/bebop/1.0"
                xmlns:cms="http://www.arsdigita.com/cms/1.0" 
                xmlns:foundry="http://foundry.libreccm.org" 
                xmlns:nav="http://ccm.redhat.com/navigation"
                exclude-result-prefixes="xsl bebop cms foundry nav"
                version="2.0">
  
    <!-- DE Eine Baumstruktur wird als UL angebildet. -->
    <!-- EN A tree structur will create a ul. -->
    <xsl:template match="bebop:tree">
        <ul class="bebop-tree" style="list-style-type:none">
            <xsl:apply-templates/>
        </ul>
    </xsl:template>
  
    <!-- DE Ein Eintrag in der Baumstruktur. -->
    <!-- EN A tree node. -->
    <xsl:template match="bebop:t_node">
        <li>
            <xsl:choose>
                <xsl:when test="@expanded='t'">
                    <a class="bebop-tree-expanded" title="[-]" href="{@href}">
                        <img style="border:none" alt="[-]">
                            <xsl:attribute name="src"
                                            select="foundry:gen-path('images/bebop/collapse.png', 
                                                                     'internal')"/>
                                           <!--select="foundry:parse-link('/images/bebop/collapse.png', 
                                                                      $theme-prefix)"/>-->
                        </img>
                    </a>
                    &nbsp;
                    <xsl:apply-templates select="*[position() = 1]"/>
                    <ul style="list-style-type:none">
                        <xsl:apply-templates select="*[position() > 1]"/>
                    </ul>
                </xsl:when>
                <xsl:when test="@collapsed='t'">
                    <a class="bebop:treeCollapsed" title="[+]" href="{@href}">
                        <img style="border:none" alt="[+]">
                            <xsl:attribute name="src" 
                                           select="foundry:gen-path('images/bebop/expand.png', 
                                                                    'internal')"/>
                                           <!--select="foundry:parse-link('/images/bebop/expand.png', 
                                                                      $theme-prefix)"/>-->
                        </img>
                    </a>
                    &nbsp;
                    <xsl:apply-templates/>
                </xsl:when>
                <xsl:otherwise>
                    <span class="nav-hide" style="width:12px;">&nbsp;&nbsp;&nbsp;&nbsp;</span>
                    <xsl:apply-templates/>
                </xsl:otherwise>
            </xsl:choose>
        </li>
    </xsl:template>

</xsl:stylesheet>



